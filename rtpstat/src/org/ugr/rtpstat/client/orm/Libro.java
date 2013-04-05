package org.ugr.rtpstat.client.orm;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Libro implements IsSerializable {
  private Long id;
  private String titulo;
  private ArrayList<CapituloLibro> capitulos;
  private String[] areasObjetivo;

  public Libro() {
    this(Long.MIN_VALUE, null, null, null);
  }

  public Libro(String titulo, ArrayList<CapituloLibro> capitulos, String[] areasObjetivo) {
    this(null, titulo, capitulos, areasObjetivo);
  }

  public Libro(Long id, String titulo, ArrayList<CapituloLibro> capitulos, String[] areasObjetivo) {
    this.id = id;
    this.titulo = titulo;
    this.capitulos = capitulos;
    this.areasObjetivo = areasObjetivo;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public ArrayList<CapituloLibro> getCapitulos() {
    return capitulos;
  }

  public void setCapitulos(ArrayList<CapituloLibro> capitulos) {
    this.capitulos = capitulos;
  }

  public void setAreasObjetivo(String[] areasObjetivo) {
    this.areasObjetivo = areasObjetivo;
  }

  public String[] getAreasObjetivo() {
    return areasObjetivo;
  }
}
