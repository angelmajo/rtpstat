package org.ugr.rtpstat.server;

import java.io.File;
import java.util.ArrayList;

import javax.jdo.PersistenceManager;

import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.server.orm.ProblemaEnStore;

public class CapituloLibroParaBackend extends CapituloLibro {
  private static final String lineSeparator = "\r\n";
  private ArrayList<String> resueltosTokens;
  private ArrayList<String> propuestosTokens;
  private String carpetaRepositorio;

  public CapituloLibroParaBackend() {
    super();
  }

  public CapituloLibroParaBackend(CapituloLibro capitulo) {
    super(capitulo.getTitulo(), capitulo.getResueltosTitulo(), null,
        capitulo.getPropuestosTitulo(), null);
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      resueltosTokens = new ArrayList<String>();
      propuestosTokens = new ArrayList<String>();
      for (ResumenProblema problema : capitulo.getResueltos()) {
        ProblemaEnStore problemaEnStore = pm.getObjectById(ProblemaEnStore.class, problema.getId());
        resueltosTokens.add(problemaEnStore.getToken());
      }
      for (ResumenProblema problema : capitulo.getPropuestos()) {
        ProblemaEnStore problemaEnStore = pm.getObjectById(ProblemaEnStore.class, problema.getId());
        propuestosTokens.add(problemaEnStore.getToken());
      }
    } finally {
      pm.close();
    }
  }

  @Override
  public String toString() {
    if (carpetaRepositorio == null) {
      throw new NullPointerException(
          "Establezca la carpeta donde esta el repositorio usando setCarpetaRepositorio(String) antes de usar el método toString()");
    }

    StringBuilder sb = new StringBuilder();
    sb.append("<chapter>" + lineSeparator);
    sb.append("  <titleCh>" + lineSeparator);
    sb.append(getTitulo() + lineSeparator);
    sb.append("  </titleCh>" + lineSeparator);
    sb.append("  <resueltos>" + lineSeparator);
    sb.append("    <title>" + lineSeparator);
    sb.append(getResueltosTitulo() + lineSeparator);
    sb.append("    </title>" + lineSeparator);
    sb.append("    <archivos>" + lineSeparator);
    for (String archivo : resueltosTokens) {
      sb.append(new File(carpetaRepositorio, archivo).getAbsolutePath() + File.separator
          + "Problema" + lineSeparator);
    }
    sb.append("    </archivos>" + lineSeparator);
    sb.append("  </resueltos>" + lineSeparator);
    sb.append("  <propuestos>" + lineSeparator);
    sb.append("    <title>" + lineSeparator);
    sb.append(getPropuestosTitulo() + lineSeparator);
    sb.append("    </title>" + lineSeparator);
    sb.append("    <archivos>" + lineSeparator);
    for (String archivo : propuestosTokens) {
      sb.append(new File(carpetaRepositorio, archivo).getAbsolutePath() + File.separator
          + "Problema" + lineSeparator);
    }
    sb.append("    </archivos>" + lineSeparator);
    sb.append("  </propuestos>" + lineSeparator);
    sb.append("</chapter>" + lineSeparator);
    return sb.toString();
  }

  /**
   * Establece el path completo a la carpeta del repositorio. Debe ser un path
   * absoluto, terminado en File.pathSeparator
   * 
   * @param carpetaRepositorio
   */
  public void setCarpetaRepositorio(String carpetaRepositorio) {
    this.carpetaRepositorio = carpetaRepositorio;
  }

  @Override
  @Deprecated
  public ArrayList<ResumenProblema> getPropuestos() {
    throw new UnsupportedOperationException(
        "En el backend no pensamos en ResumenProblema, pensamos en tokens...");
  }

  @Override
  @Deprecated
  public ArrayList<ResumenProblema> getResueltos() {
    throw new UnsupportedOperationException(
        "En el backend no pensamos en ResumenProblema, pensamos en tokens...");
  }

  @Override
  @Deprecated
  public void setPropuestos(ArrayList<ResumenProblema> propuestos) {
    throw new UnsupportedOperationException(
        "En el backend no pensamos en ResumenProblema, pensamos en tokens...");
  }

  @Override
  @Deprecated
  public void setResueltos(ArrayList<ResumenProblema> resueltos) {
    throw new UnsupportedOperationException(
        "En el backend no pensamos en ResumenProblema, pensamos en tokens...");
  }

}
