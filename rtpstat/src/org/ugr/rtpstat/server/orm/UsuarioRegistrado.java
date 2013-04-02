package org.ugr.rtpstat.server.orm;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.Rol;

import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UsuarioRegistrado {
	@PrimaryKey
	@Persistent
	private String userId;
	@Persistent
	private User realUser;
	@Persistent
	private Date fechaRegistro;
	@Persistent
	private String nombreReal;
	@Persistent
	private String organizacion;
	@Persistent
	private Rol rol;

	public UsuarioRegistrado() {

	}

	public UsuarioRegistrado(User currentUser, String nombreReal, String institucion) {
		if (currentUser == null) {
			throw new NullPointerException("Se debe pasar el usuario actual.");
		}
		if (nombreReal == null || "".equals(nombreReal)) {
			throw new NullPointerException("Se debe pasar el nombre real del nuevo usuario.");
		}
		if (institucion == null || "".equals(institucion)) {
			throw new NullPointerException("Se debe pasar la institución.");
		}
		this.setRealUser(currentUser);
		this.setNombreReal(nombreReal);
		this.setOrganizacion(institucion);
		
		this.setFechaRegistro(new Date());
		this.setRol(Rol.UsuarioNuevo);
		this.userId = currentUser.getUserId();
	}

	public void setRealUser(User realUser) {
		this.realUser = realUser;
	}

	public User getRealUser() {
		return realUser;
	}

	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	public void setOrganizacion(String organizacion) {
		this.organizacion = organizacion;
	}

	public String getOrganizacion() {
		return organizacion;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Rol getRol() {
		return rol;
	}

	public String getUserId() {
		return userId;
	}

	public void setNombreReal(String nombreReal) {
		this.nombreReal = nombreReal;
	}

	public String getNombreReal() {
		return nombreReal;
	}

	public org.ugr.rtpstat.client.orm.UsuarioRegistrado paraCliente() {
		org.ugr.rtpstat.client.orm.UsuarioRegistrado usuario = new org.ugr.rtpstat.client.orm.UsuarioRegistrado();
		usuario.setFechaRegistro(this.getFechaRegistro());
		usuario.setNombreReal(this.getNombreReal());
		usuario.setOrganizacion(this.getOrganizacion());
		usuario.setRol(this.getRol());
		usuario.setUserId(this.getUserId());
		usuario.setEmail(realUser.getEmail());
		return usuario;
	}
}
