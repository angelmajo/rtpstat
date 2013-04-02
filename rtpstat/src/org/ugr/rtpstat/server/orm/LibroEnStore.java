package org.ugr.rtpstat.server.orm;

import java.util.ArrayList;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LibroEnStore {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private User owner;
	
	@Persistent
	private String titulo;

	@Persistent(mappedBy = "libro")
	private ArrayList<CapituloLibroEnStore> capitulos;

	@Persistent
	private String[] areasObjetivo;

	@Persistent
	private String token;
	
	public LibroEnStore() {
		this(null,null, null,null);
	}

	public LibroEnStore(User owner, String titulo, ArrayList<CapituloLibroEnStore> capitulos,String [] areasObjetivo) {
		super();
		this.setOwner(owner);
		this.titulo = titulo;
		this.capitulos = capitulos;
		this.areasObjetivo = areasObjetivo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public ArrayList<CapituloLibroEnStore> getCapitulos() {
		return capitulos;
	}

	public void setCapitulos(ArrayList<CapituloLibroEnStore> capitulos) {
		this.capitulos = capitulos;
	}

	public Key getKey() {
		return key;
	}

	public String[] getAreasObjetivo() {
		return areasObjetivo;
	}
	
	public void setAreasObjetivo(String [] areasObjetivo) {
		this.areasObjetivo = areasObjetivo;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

}
