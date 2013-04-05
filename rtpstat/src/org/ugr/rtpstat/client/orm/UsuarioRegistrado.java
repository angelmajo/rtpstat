package org.ugr.rtpstat.client.orm;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UsuarioRegistrado implements IsSerializable {
  private String userId;
  private Date fechaRegistro;
  private String nombreReal;
  private String email;
  private String organizacion;
  private Rol rol;

  public UsuarioRegistrado() {}
  
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
