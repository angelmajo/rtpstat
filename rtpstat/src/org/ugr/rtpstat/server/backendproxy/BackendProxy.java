package org.ugr.rtpstat.server.backendproxy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;

public class BackendProxy implements Backend {
  /**
   * Para obtener la instancia única del BackendProxy
   * 
   * @return la instancia del singleton
   */
  public static Backend getBackendProxy() {
    if (instancia == null) {
      instancia = new BackendProxy();
    }
    return instancia;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ugr.rtpstat.server.backendproxy.Backend#solicitarGeneracion(Problema)
   */
  public String solicitarGeneracionProblema(Problema p) throws IOException {
    String out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.solicitarGeneracionProblema(p);
    }
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ugr.rtpstat.server.backendproxy.Backend#solicitarGeneracion(org.ugr
   * .rtpstat.client.orm.RelacionEjercicios)
   */
  public String solicitarGeneracionRelacion(RelacionEjercicios r) throws IOException {
    String out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.solicitarGeneracionRelacion(r);
    }
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ugr.rtpstat.server.backendproxy.Backend#solicitarGeneracionLibro(org
   * .ugr.rtpstat.client.orm.Libro)
   */
  public String solicitarGeneracionLibro(Libro libro) throws IOException {
    String out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.solicitarGeneracionLibro(libro);
    }
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ugr.rtpstat.server.backendproxy.Backend#getEstado(java.lang.String)
   */
  public EstadoGeneracion getEstado(String token, Class<?> clase) throws NoSuchElementException {
    EstadoGeneracion out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.getEstado(token, clase);
    }
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.ugr.rtpstat.server.backendproxy.Backend#getURLDocumento(org.ugr.rtpstat
   * .client.orm.Problema)
   */
  public String getURLDocumento(String token) {
    String out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.getURLDocumento(token);
    }
    return out;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.ugr.rtpstat.server.backendproxy.Backend#getMensajeError(java.lang
   * .String)
   */
  public String getMensajeError(String token) {
    String out = null;
    if (backends.size() > 1) {
      throw new Error("Demasiados backends disponibles");
    }
    for (Backend b : backends) {
      out = b.getMensajeError(token);
    }
    return out;
  }

  /***************************************/
  /* Inicio de la implementación privada */
  /***************************************/

  /* Singleton */
  protected static Backend instancia = null;

  protected BackendProxy() {
    try {
      // backends.add(new DefaultBackend(new
      // URL("http://localhost:8080/rtpstat-backend/DocumentService")));

      backends.add(new DefaultBackend(new URL("http://jamaldo2.ugr.es:8080/rtpstat-backend/")));
    } catch (MalformedURLException e) {
      // Esto no debería de pasar nunca, la URL está aquí mismo...
      e.printStackTrace();
    }
  }

  protected ArrayList<Backend> backends = new ArrayList<Backend>();
}
