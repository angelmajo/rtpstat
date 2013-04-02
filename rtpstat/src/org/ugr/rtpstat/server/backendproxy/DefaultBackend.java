package org.ugr.rtpstat.server.backendproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.server.LibroParaBackend;
import org.ugr.rtpstat.server.PMF;
import org.ugr.rtpstat.server.RelacionEjerciciosParaBackend;
import org.ugr.rtpstat.server.orm.LibroEnStore;
import org.ugr.rtpstat.server.orm.ProblemaEnStore;
import org.ugr.rtpstat.server.orm.RelacionEjerciciosEnstore;

public class DefaultBackend implements Backend {
	public DefaultBackend(URL baseRemoteURL) {
		super();
		try {
			this.documentServiceURL = new URL(baseRemoteURL.toString() + "DocumentService");
			this.documentStatusServiceURL = new URL(baseRemoteURL.toString() + "DocumentStatusService");
			this.documentDownloadURL = new URL(baseRemoteURL.toString() + "DownloadPDF");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private String solicitarGeneracionInterna(Object o) throws IOException {
		URLConnection connection = documentServiceURL.openConnection();
		connection.setDoOutput(true);
		OutputStream os = connection.getOutputStream();

		ObjectOutputStream e = new ObjectOutputStream(os);
		e.writeObject(o);
		e.close();

		BufferedReader isr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String out = "";
		String leido;
		while ((leido = isr.readLine()) != null) {
			out += leido;
		}
		if ((connection instanceof HttpURLConnection) && ((HttpURLConnection) connection).getResponseCode() != 200) {
			out = "ERROR:BACKEND:" + ((HttpURLConnection) connection).getResponseCode();
		}
		return out;
	}

	public String solicitarGeneracionProblema(Problema p) throws IOException {
		if (p == null) {
			throw new NullPointerException("La relación de problemas no puede ser null");
		}
		if (!p.isValid()) {
			throw new IllegalArgumentException();
		}
		return solicitarGeneracionInterna(p);
	}

	public String solicitarGeneracionRelacion(RelacionEjercicios r) throws IOException {
		if (r == null) {
			throw new NullPointerException("La relación de problemas no puede ser null");
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ArrayList<String> tokens = new ArrayList<String>();
		try {

			for (ResumenProblema resumen : r.getProblemas()) {
				ProblemaEnStore problemaEnStore = pm.getObjectById(ProblemaEnStore.class, resumen.getId());
				tokens.add(problemaEnStore.getToken());
			}
		} finally {
			pm.close();
		}
		// TODO Validar relaciones
		RelacionEjerciciosParaBackend paraStore = new RelacionEjerciciosParaBackend(r.getId(), r.getTitulo(), r.getTipo(), r.getAreasObjetivo(), tokens.toArray(new String[tokens.size()]));
		String respuesta = solicitarGeneracionInterna(paraStore);
		logger.info("El backend ha contestado: " + respuesta);
		return respuesta;
	}

	@Override
	public String solicitarGeneracionLibro(Libro libro) throws IOException {
		if (libro == null) {
			throw new NullPointerException("La relación de problemas no puede ser null");
		}
		// TODO Validar libros
		LibroParaBackend libroParaBackend = new LibroParaBackend(libro.getTitulo(), libro.getCapitulos());
		String respuesta = solicitarGeneracionInterna(libroParaBackend);
		logger.info("El backend ha contestado: " + respuesta);
		return respuesta;
	}

	public EstadoGeneracion getEstado(String token, Class<?> clase) throws NoSuchElementException {
		EstadoGeneracion salida = EstadoGeneracion.DESCONOCIDO;
		if (getEstadoCacheado(token)) {
			salida = EstadoGeneracion.OK;
		} else {
			String urlCompleta = documentStatusServiceURL.toString();
			urlCompleta += "?token=" + token + "&cache-destroyer=" + UUID.randomUUID();// El
			URLConnection connection;
			try {
				connection = new URL(urlCompleta).openConnection();
				String out = "";
				if (connection.getDoInput()) {
					BufferedReader isr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String leido;
					while ((leido = isr.readLine()) != null) {
						out += leido;
					}
				}
				if (EstadoGeneracion.ERROR.toString().equals(out)) {
					salida = EstadoGeneracion.ERROR;
				} else if (EstadoGeneracion.OK.toString().equals(out)) {
					salida = EstadoGeneracion.OK;
					if (ProblemaEnStore.class.equals(clase)) {
						cachearDocumentoGeneradoParaProblema(token);
					} else if (RelacionEjerciciosEnstore.class.equals(clase)) {
						cachearDocumentoGeneradoParaRelacion(token);
					} else if (LibroEnStore.class.equals(clase)) {
						cachearDocumentoGeneradoParaLibro(token);
					} else {
						throw new IllegalArgumentException("Clase no manejada: " + clase.getSimpleName());
					}
				} else if (EstadoGeneracion.GENERANDO.toString().equals(out)) {
					salida = EstadoGeneracion.GENERANDO;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.fine("Para el token : " + token + " se devuelve " + salida);
		return salida;
	}

	public String getURLDocumento(String token) {
		return documentDownloadURL + "?token=" + token;
	}

	public String getMensajeError(String token) {
		String salida = "";
		String urlCompleta = documentStatusServiceURL.toString();
		urlCompleta += "?verbose=true&token=" + token;
		URLConnection connection;
		try {
			connection = new URL(urlCompleta).openConnection();
			if (connection.getDoInput()) {
				BufferedReader isr = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String leido;
				while ((leido = isr.readLine()) != null) {
					salida += leido;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (salida.startsWith("ERROR:")) {
			salida = salida.substring(6);
		} else {
			salida = "";
		}
		return salida;
	}

	private URL documentServiceURL;
	private URL documentStatusServiceURL;
	private URL documentDownloadURL;

	private static Logger logger;
	{
		logger = Logger.getLogger(DefaultBackend.class.getSimpleName());
	}

	private void cachearDocumentoGeneradoParaLibro(String token) {
		// TODO Cacher la generación de los libros

	}

	private void cachearDocumentoGeneradoParaProblema(String token) {
		ProblemaEnStore p = getProblemaEnStore(token);
		if (p != null) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				p.setDocumentosGenerados(true);
				pm.makePersistent(p);
			} finally {
				pm.close();
			}
		}
	}

	private void cachearDocumentoGeneradoParaRelacion(String token) {
		RelacionEjerciciosEnstore r = getRelacionEnStore(token);
		if (r != null) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				r.setGenerada(true);
				pm.makePersistent(r);
			} finally {
				pm.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<ProblemaEnStore> executeQueryProblemas(Query q, String token) {
		return (List<ProblemaEnStore>) q.execute(token);
	}

	private ProblemaEnStore getProblemaEnStore(String token) {
		ProblemaEnStore out = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(ProblemaEnStore.class);
			q.setFilter("token == tokenParam");
			q.declareParameters("String tokenParam");

			List<ProblemaEnStore> problemas = executeQueryProblemas(q, token);
			if (problemas.size() == 1) {
				out = problemas.get(0);
				out.getApartadosEnStore();
				out.getDatos();
			}
		} finally {
			pm.close();
		}

		return out;
	}

	private RelacionEjerciciosEnstore getRelacionEnStore(String token) {
		RelacionEjerciciosEnstore out = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(RelacionEjerciciosEnstore.class);
			q.setFilter("token == tokenParam");
			q.declareParameters("String tokenParam");

			List<RelacionEjerciciosEnstore> relaciones = executeQueryRelaciones(q, token);
			if (relaciones.size() == 1) {
				out = relaciones.get(0);
			}
		} finally {
			pm.close();
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	private List<RelacionEjerciciosEnstore> executeQueryRelaciones(Query q, String token) {
		return (List<RelacionEjerciciosEnstore>) q.execute(token);
	}

	private boolean getEstadoCacheado(String token) {
		ProblemaEnStore p = getProblemaEnStore(token);
		return p != null && p.isDocumentosGenerados();
	}
}
