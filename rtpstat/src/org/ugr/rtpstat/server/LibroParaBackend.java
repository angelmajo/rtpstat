package org.ugr.rtpstat.server;

import java.util.ArrayList;

import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.Libro;

public class LibroParaBackend extends Libro {
  private static final String lineSeparator = "\r\n";
  private ArrayList<CapituloLibroParaBackend> capitulosParaBackend;
  private String carpetaRepositorio;

  public LibroParaBackend(String titulo, ArrayList<CapituloLibro> capitulos) {
    super(titulo, capitulos, null);
    capitulosParaBackend = new ArrayList<CapituloLibroParaBackend>();
    for (CapituloLibro capitulo : capitulos) {
      capitulosParaBackend.add(new CapituloLibroParaBackend(capitulo));
    }
  }

  @Override
  public String toString() {
    if (carpetaRepositorio == null) {
      throw new NullPointerException(
          "Establezca la carpeta donde esta el repositorio usando setCarpetaRepositorio(String) antes de usar el método toString()");
    }
    String out = "";
    out += "<formato>" + lineSeparator + "Libro" + lineSeparator + "</formato>" + lineSeparator;
    for (CapituloLibroParaBackend capitulo : capitulosParaBackend) {
      capitulo.setCarpetaRepositorio(carpetaRepositorio);
      out += capitulo.toString();
    }
    return out;
  }

  public ArrayList<CapituloLibroParaBackend> getCapitulosParaBackend() {
    return capitulosParaBackend;
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
}
