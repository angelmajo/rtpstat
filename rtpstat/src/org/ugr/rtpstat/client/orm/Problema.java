package org.ugr.rtpstat.client.orm;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Problema implements IsSerializable, IsValidable {
  private static final String DEFAULT_STRING = "DEFAULT";

  protected String descripcion;
  private GradoDificultad dificultad;
  protected String cabecera;
  protected String cabeceraSegunda;
  protected String[] areasObjetivo;
  protected Datos datos;
  protected Apartado[] apartados;

  private long id;

  private boolean documentosGenerados;

  public Problema(String descripcion, GradoDificultad dificultad, String cabecera,
      String cabeceraSegunda, String[] areasObjetivo, Datos datos, Apartado[] apartados) {
    super();
    this.descripcion = descripcion;
    this.setDificultad(dificultad);
    this.cabecera = cabecera;
    this.cabeceraSegunda = cabeceraSegunda;
    this.areasObjetivo = areasObjetivo;
    this.datos = datos;
    this.apartados = apartados;
    this.documentosGenerados = false;
  }

  public Problema() {
    this(DEFAULT_STRING, null, DEFAULT_STRING, DEFAULT_STRING, null, null, null);
    this.id = Long.MIN_VALUE;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String[] getAreasObjetivo() {
    return areasObjetivo;
  }

  public void setAreasObjetivo(String[] areasObjetivo) {
    this.areasObjetivo = areasObjetivo;
  }

  public String getCabecera() {
    return cabecera;
  }

  public void setCabecera(String cabecera) {
    this.cabecera = cabecera;
  }

  public void setCabeceraSegunda(String cabeceraSegunda) {
    this.cabeceraSegunda = cabeceraSegunda;
  }

  public String getCabeceraSegunda() {
    return cabeceraSegunda;
  }

  @Override
  public String toString() {
    if (!this.isValid()) {
      return "PROBLEMA INVALIDO";
    } else {
      StringBuilder out = new StringBuilder();
      out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
      out.append("<problema>\r\n");

      out.append("\t<descripcion>\r\n");
      out.append("\t\t" + this.getDescripcion() + "\r\n");
      out.append("\t</descripcion>\r\n");

      out.append("\t<cabecera>\r\n");
      out.append("\t\t" + this.getCabecera() + "\r\n");
      out.append("\t</cabecera>\r\n");

      out.append("\t<cabecera2>\r\n");
      if (this.getCabeceraSegunda() != null && this.getCabeceraSegunda().length() > 0) {
        out.append("\t\t" + this.getCabeceraSegunda() + "\r\n");
      }
      out.append("\t</cabecera2>\r\n");

      out.append("\t<descripcion_variable>\r\n");
      out.append("\t\t" + this.getDatos().getDescripcionVariable() + "\r\n");
      out.append("\t</descripcion_variable>\r\n");

      String tipoVariable = "FALLIDO!!!";
      String organizacionVariable = "FALLIDO!!!";
      switch (this.getDatos().getTipoVariable()) {
      case CONTINUA_SERIE:
        tipoVariable = "Continua";
        organizacionVariable = "Serie";
        break;
      case CONTINUA_TABLA:
        tipoVariable = "Continua";
        organizacionVariable = "Tabla";
        break;
      case DISCRETA_SERIE:
        tipoVariable = "Discreta";
        organizacionVariable = "Serie";
        break;
      case DISCRETA_TABLA:
        tipoVariable = "Discreta";
        organizacionVariable = "Tabla";
        break;
      }
      out.append("\t<tipo_variable>\r\n");
      out.append("\t\t" + tipoVariable + "\r\n");
      out.append("\t</tipo_variable>\r\n");

      out.append("\t<organizacion_datos>\r\n");
      out.append("\t\t" + organizacionVariable + "\r\n");
      out.append("\t</organizacion_datos>\r\n");

      out.append("\t<apartados>\r\n");
      for (Apartado apartado : getApartados()) {
        out.append("\t\t<apartado>\r\n");
        out.append("\t\t\t<enunciado>\r\n");
        out.append("\t\t\t\t" + apartado.getEnunciado() + "\r\n");
        out.append("\t\t\t</enunciado>\r\n");
        if (apartado instanceof ApartadoConSubApartados) {
          out.append("\t\t\t<subapartados>\r\n");
          ApartadoConSubApartados acs = (ApartadoConSubApartados) apartado;
          for (Apartado a : acs.getSubApartados()) {
            out.append("\t\t\t\t<subapartado>\r\n");
            out.append("\t\t\t\t\t<enunciado>\r\n");
            out.append("\t\t\t\t\t\t" + a.getEnunciado() + "\r\n");
            out.append("\t\t\t\t\t</enunciado>\r\n");
            out.append("\t\t\t\t\t<calculos>\r\n");
            out.append("\t\t\t\t\t\t");
            Calculo[] calculos = ((ApartadoConCalculos) a).getCalculos();
            for (int i = 0; i < calculos.length; i++) {
              Calculo calculo = calculos[i];
              out.append(calculo.toShortString());
              if (i != calculos.length - 1) {
                out.append(", ");
              }
            }
            out.append("\r\n");
            out.append("\t\t\t\t\t</calculos>\r\n");
            out.append("\t\t\t\t</subapartado>\r\n");
          }
          out.append("\t\t\t</subapartados>\r\n");
        } else if (apartado instanceof ApartadoConCalculos) {
          out.append("\t\t\t<calculos>\r\n");
          out.append("\t\t\t\t");
          Calculo[] calculos = ((ApartadoConCalculos) apartado).getCalculos();
          for (int i = 0; i < calculos.length; i++) {
            Calculo calculo = calculos[i];
            out.append(calculo.toShortString());
            if (i != calculos.length - 1) {
              out.append(", ");
            }
          }
          out.append("\r\n");
          out.append("\t\t\t</calculos>\r\n");
        } else {
          // TODO Wowowowowow, horrible
          out.append("\t\t\t<TipoApartadoDesconocido/>\r\n");
        }
        out.append("\t\t</apartado>\r\n");
      }
      out.append("\t</apartados>\r\n");

      out.append("</problema>\r\n");
      return out.toString();
    }
  }

  public void setDatos(Datos datos) {
    this.datos = datos;
  }

  public Datos getDatos() {
    return datos;
  }

  public void setApartados(Apartado[] apartados) {
    this.apartados = apartados;
  }

  public Apartado[] getApartados() {
    return apartados;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public boolean isValid() {
    boolean out = false;
    if (descripcion != null && descripcion.length() > 0) {
      if (cabecera != null && cabecera.length() > 0) {
        if (datos != null && datos.isValid()) {
          if (apartados != null) {
            int validos = 0;
            for (Apartado a : apartados) {
              if (a.isValid()) {
                validos++;
              }
            }
            if (validos > 0 && validos == apartados.length) {
              out = true;
            }
          }
        }
      }
    }
    return out;
  }

  public void setDificultad(GradoDificultad dificultad) {
    this.dificultad = dificultad;
  }

  public GradoDificultad getDificultad() {
    return dificultad;
  }

  public boolean isDocumentosGenerados() {
    return documentosGenerados;
  }

  public void setDocumentosGenerados(boolean documentosGenerados) {
    this.documentosGenerados = documentosGenerados;
  }
}
