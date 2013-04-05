package org.ugr.rtpstat.server.orm;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.Apartado;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.server.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.client.rpc.IsSerializable;

//Duplica totalmente la clase Problema, pero resulta comodo y es necesario en parte
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ProblemaEnStore implements IsSerializable {
	private static Logger logger;
	{
		logger = Logger.getLogger(ProblemaEnStore.class.getSimpleName());
	}
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	@Persistent
	private String descripcion;
	@Persistent
	private GradoDificultad dificultad;
	@Persistent
	private String cabecera;
	@Persistent
	private String cabeceraSegunda;
	@Persistent
	private String[] areasObjetivo;
	@Persistent
	private DatosEnStore datos;

	@Persistent(serialized="true")
	private LinkedList<ApartadoEnStore> apartadosEnStore;

	@Persistent
	private Date ultimaActualizacion;

	@Persistent
	private Date fechaCreacion;

	@Persistent
	private User owner;
	@Persistent
	private String token;

	@Persistent
	private boolean documentosGenerados = false;

	public ProblemaEnStore() {

	}

	public ProblemaEnStore(PersistenceManager pm, Problema problema) {
		fromProblema(pm,problema);
	}

	public Problema comoProblema() {
		Problema out = new Problema();
		out.setId(getKey().getId());
		out.setDescripcion(this.getDescripcion());
		out.setDificultad(this.getDificultad());
		out.setCabecera(this.getCabecera());
		out.setCabeceraSegunda(this.getCabeceraSegunda());
		out.setAreasObjetivo(this.getAreasObjetivo());
		out.setDocumentosGenerados(this.isDocumentosGenerados());
		if (this.getDatos() != null) {
			out.setDatos(this.getDatos().comoDatos());
		} else {
			logger.warning("Este problema no tiene datos para exportar");
		}
		ArrayList<Apartado> apartados = new ArrayList<Apartado>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			this.getApartadosEnStore();
			for (ApartadoEnStore apartadoEnStore: this.getApartadosEnStore()) {
				//ApartadoEnStore apartadoEnStore = pm.getObjectById(ApartadoEnStore.class,key.getId());
				apartados.add(apartadoEnStore.comoApartado());
			}
			out.setApartados(apartados.toArray(new Apartado[apartados.size()]));
		} finally {
			pm.close();
		}
		return out;
	}

	// @Override
	public String getDescripcion() {
		return descripcion;
	}

	// @Override
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	// @Override
	public String[] getAreasObjetivo() {
		return areasObjetivo;
	}

	// @Override
	public void setAreasObjetivo(String[] areasObjetivo) {
		this.areasObjetivo = areasObjetivo;
	}

	// @Override
	public String getCabecera() {
		return cabecera;
	}

	// @Override
	public void setCabecera(String cabecera) {
		this.cabecera = cabecera;
	}

	// @Override
	public void setCabeceraSegunda(String cabeceraSegunda) {
		this.cabeceraSegunda = cabeceraSegunda;
	}

	// @Override
	public String getCabeceraSegunda() {
		return cabeceraSegunda;
	}

	public void setDatos(DatosEnStore datos) {
		if (this.datos != null) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			try {
				tx.begin();
				pm.detachCopy(this.datos);
				pm.deletePersistent(this.datos);
				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}

				pm.close();
			}
		}
		this.datos = datos;
	}

	public DatosEnStore getDatos() {
		return datos;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public void fromProblema(PersistenceManager pm, Problema problema) {
		this.setDescripcion(problema.getDescripcion());
		this.setDificultad(problema.getDificultad());
		this.setCabecera(problema.getCabecera());
		this.setCabeceraSegunda(problema.getCabeceraSegunda());
		this.setAreasObjetivo(problema.getAreasObjetivo());

		//List<ApartadoEnStore> nuevosApartados = new ArrayList<ApartadoEnStore>();
		for (Apartado a : problema.getApartados()) {
			ApartadoEnStore apartadoEnStore = new ApartadoEnStore(this,a);
			//apartadoEnStore = pm.makePersistent(apartadoEnStore);
			if(this.getApartadosEnStore() == null) {
				this.setApartadosEnStore(new LinkedList<ApartadoEnStore>());
			}
			this.getApartadosEnStore().add(apartadoEnStore);
		}

		if (problema.getDatos() != null) {
			this.setDatos(new DatosEnStore(problema.getDatos()));
		} else {
			logger.warning("El problema recibido no tiene datos");
		}
		this.setOwner(UserServiceFactory.getUserService().getCurrentUser());
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setDificultad(GradoDificultad dificultad) {
		this.dificultad = dificultad;
	}

	public GradoDificultad getDificultad() {
		return dificultad;
	}

	public void setUltimaActualizacion(Date ultimaActualizacion) {
		this.ultimaActualizacion = ultimaActualizacion;
	}

	public Date getUltimaActualizacion() {
		return ultimaActualizacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setDocumentosGenerados(boolean documentosGenerados) {
		this.documentosGenerados = documentosGenerados;
	}

	public boolean isDocumentosGenerados() {
		return documentosGenerados;
	}

	public Key getKey() {
		return key;
	}

	public void setApartadosEnStore(LinkedList<ApartadoEnStore> apartadosEnStore) {
		this.apartadosEnStore = apartadosEnStore;
	}

	public LinkedList<ApartadoEnStore> getApartadosEnStore() {
		return apartadosEnStore;
	}
}
