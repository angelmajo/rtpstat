package org.ugr.rtpstat.client.orm;

import java.io.Serializable;
import java.util.Date;

public class UsuarioRegistrado implements Serializable{
	private static final long serialVersionUID = -3788660209785066114L;
	
	private String userId;
	private Date fechaRegistro;
	private String nombreReal;
	private String email;
	private String organizacion;
	private Rol rol;
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserId() {
		return userId;
	}
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}
	public Date getFechaRegistro() {
		return fechaRegistro;
	}
	public void setNombreReal(String nombreReal) {
		this.nombreReal = nombreReal;
	}
	public String getNombreReal() {
		return nombreReal;
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
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
}
