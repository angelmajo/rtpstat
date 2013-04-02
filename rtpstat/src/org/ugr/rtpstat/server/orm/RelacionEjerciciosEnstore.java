package org.ugr.rtpstat.server.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.ResultadoGeneracionRelacionEjercicios;
import org.ugr.rtpstat.client.orm.TipoRelacion;
import org.ugr.rtpstat.server.PMF;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class RelacionEjerciciosEnstore {
	public RelacionEjerciciosEnstore(User owner, String titulo, String[] areas, TipoRelacion tipo) {
		this.owner = owner;
		this.titulo = titulo;
		this.tipo = tipo;
		this.areasObjetivo = areas;
	}

	/*
	 * Getters/Setters
	 */
	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String[] getAreasObjetivo() {
		return areasObjetivo;
	}

	public void setAreasObjetivo(String[] areasObjetivo) {
		this.areasObjetivo = areasObjetivo;
	}

	public ArrayList<Key> getProblemas() {
		return problemas;
	}

	public void setProblemas(ArrayList<Key> problemas) {
		this.problemas = problemas;
	}

	public void setTipo(TipoRelacion tipo) {
		this.tipo = tipo;
	}

	public TipoRelacion getTipo() {
		return tipo;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	/*
	 * Atributos
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private User owner;

	@Persistent
	private TipoRelacion tipo;

	@Persistent
	private String titulo;

	@Persistent
	private String[] areasObjetivo;

	@Persistent
	private ArrayList<Key> problemas = new ArrayList<Key>();

	@Persistent
	private String token;

	@Persistent
	private Boolean generada;

	/*
	 * Generación de listas automática
	 */
	public static ResultadoGeneracionRelacionEjercicios generarRelacion(RelacionEjerciciosEnstore relacion, HashMap<GradoDificultad, Integer> problemas,
			String[] areas) {
		ResultadoGeneracionRelacionEjercicios res = new ResultadoGeneracionRelacionEjercicios();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Set<String> areasSolicitadas = new HashSet<String>(Arrays.asList(areas));

		try {
			Query query = pm.newQuery(ProblemaEnStore.class);
			query.setFilter("owner == usuarioActivo");
			query.declareParameters("com.google.appengine.api.users.User usuarioActivo");
			try {
				List<ProblemaEnStore> listado = executeQuery(query, UserServiceFactory.getUserService().getCurrentUser());
				Collections.shuffle(listado);
				for (ProblemaEnStore p : listado) {
					Set<String> areasProblema = new HashSet<String>(Arrays.asList(p.getAreasObjetivo()));
					//TODO Manejar que cuando pedimos indeterminado incluya de todas las áreas
					areasProblema.retainAll(areasSolicitadas); //Realiza la intersección de los dos sets en areasProblemas
					if (areasProblema.size() > 0) {
						if (problemas.get(p.getDificultad()) > 0) {
							relacion.problemas.add(p.getKey());
							problemas.put(p.getDificultad(), problemas.get(p.getDificultad()) - 1);
							res.incrementar(p.getDificultad());
						}
					}
				}
			} finally {
				query.closeAll();
			}
			pm.makePersistent(relacion);
			res.setIdRelacion(relacion.getKey().getId());
		} finally {
			pm.close();
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private static List<ProblemaEnStore> executeQuery(Query q, User u) {
		return new ArrayList<ProblemaEnStore>((List<ProblemaEnStore>) q.execute(u));
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public boolean isGenerada() {
		if(generada == null) {
			generada = false;
		}
		return generada;
	}
	
	public void setGenerada(boolean generada) {
		this.generada = generada;
	}
}
