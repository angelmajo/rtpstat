package org.ugr.rtpstat.server.orm;

import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.server.PMF;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CapituloLibroEnStore {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String titulo;

	@Persistent
	private LibroEnStore libro;

	@Persistent
	private String resueltosTitulo;

	@Persistent
	private ArrayList<Key> resueltos;

	@Persistent
	private String propuestosTitulo;

	@Persistent
	private ArrayList<Key> propuestos;

	public CapituloLibroEnStore() {
		this(null, null, null, null, null);
	}

	public CapituloLibroEnStore(String titulo, String resueltosTitulo, ArrayList<Key> resueltos, String propuestosTitulo, ArrayList<Key> propuestos) {
		super();
		this.titulo = titulo;
		this.resueltosTitulo = resueltosTitulo;
		this.resueltos = resueltos;
		this.propuestosTitulo = propuestosTitulo;
		this.propuestos = propuestos;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public LibroEnStore getLibro() {
		return libro;
	}

	public void setLibro(LibroEnStore libro) {
		this.libro = libro;
	}

	public String getResueltosTitulo() {
		return resueltosTitulo;
	}

	public void setResueltosTitulo(String resueltosTitulo) {
		this.resueltosTitulo = resueltosTitulo;
	}

	public ArrayList<Key> getResueltos() {
		return resueltos;
	}

	public void setResueltos(ArrayList<Key> resueltos) {
		this.resueltos = resueltos;
	}

	public String getPropuestosTitulo() {
		return propuestosTitulo;
	}

	public void setPropuestosTitulo(String propuestosTitulo) {
		this.propuestosTitulo = propuestosTitulo;
	}

	public ArrayList<Key> getPropuestos() {
		return propuestos;
	}

	public void setPropuestos(ArrayList<Key> propuestos) {
		this.propuestos = propuestos;
	}

	public Key getKey() {
		return key;
	}

	public CapituloLibro toCapituloLibro() {
		CapituloLibro out = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			ArrayList<ResumenProblema> propuestosArray = new ArrayList<ResumenProblema>();
			for (Key k : getPropuestos()) {
				ProblemaEnStore p = pm.getObjectById(ProblemaEnStore.class, k);
				ResumenProblema resumen = new ResumenProblema(p.comoProblema());
				propuestosArray.add(resumen);
			}
			ArrayList<ResumenProblema> resueltosArray = new ArrayList<ResumenProblema>();
			for (Key k : getResueltos()) {
				ProblemaEnStore p = pm.getObjectById(ProblemaEnStore.class, k);
				ResumenProblema resumen = new ResumenProblema(p.comoProblema());
				resueltosArray.add(resumen);
			}
			out = new CapituloLibro(getTitulo(), getResueltosTitulo(), resueltosArray, getPropuestosTitulo(), propuestosArray);
		} finally {
			pm.close();
		}
		return out;
	}
}
