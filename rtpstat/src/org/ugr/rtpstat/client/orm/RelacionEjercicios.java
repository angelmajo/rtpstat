package org.ugr.rtpstat.client.orm;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RelacionEjercicios implements IsSerializable {
  public RelacionEjercicios() {
    this(Long.MIN_VALUE, null, null, null, null);
  }

  public RelacionEjercicios(long id, String titulo, TipoRelacion tipo, String[] areasObjetivo,
      ArrayList<ResumenProblema> problemas) {
    this.id = id;
    this.titulo = titulo;
    this.tipo = tipo;
    this.areasObjetivo = areasObjetivo;
    this.problemas = problemas;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public TipoRelacion getTipo() {
    return tipo;
  }

  public void setTipo(TipoRelacion tipo) {
    this.tipo = tipo;
  }

  public String[] getAreasObjetivo() {
    return areasObjetivo;
  }

  public void setAreasObjetivo(String[] areasObjetivo) {
    this.areasObjetivo = areasObjetivo;
  }

  public ArrayList<ResumenProblema> getProblemas() {
    return problemas;
  }

  public void setProblemas(ArrayList<ResumenProblema> problemas) {
    this.problemas = problemas;
  }

  private long id;
  private String titulo;
  private TipoRelacion tipo;
  private String[] areasObjetivo;
  private ArrayList<ResumenProblema> problemas;
}
