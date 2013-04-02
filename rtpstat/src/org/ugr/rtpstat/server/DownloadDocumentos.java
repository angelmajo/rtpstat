package org.ugr.rtpstat.server;

import static org.ugr.rtpstat.client.Constantes.FORMATO_PROBLEMA_FOLIOS;
import static org.ugr.rtpstat.client.Constantes.FORMATO_PROBLEMA_PRESENTACION;
import static org.ugr.rtpstat.client.Constantes.NOMBRE_PARAMETRO_FORMATO;
import static org.ugr.rtpstat.client.Constantes.NOMBRE_PARAMETRO_ID;
import static org.ugr.rtpstat.client.Constantes.NOMBRE_PARAMETRO_TIPO;
import static org.ugr.rtpstat.client.Constantes.TIPO_DESCARGA_LIBRO;
import static org.ugr.rtpstat.client.Constantes.TIPO_DESCARGA_PROBLEMA;
import static org.ugr.rtpstat.client.Constantes.TIPO_DESCARGA_RELACION;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ugr.rtpstat.client.orm.Permiso;
import org.ugr.rtpstat.server.orm.LibroEnStore;
import org.ugr.rtpstat.server.orm.ProblemaEnStore;
import org.ugr.rtpstat.server.orm.RelacionEjerciciosEnstore;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;

public class DownloadDocumentos extends HttpServlet {
	private static final long serialVersionUID = -1250323310718113444L;
	private static final Logger logger = Logger.getLogger(DownloadDocumentos.class.getSimpleName());

	private final RtpstatServiceImpl rtpstatService = new RtpstatServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			if (rtpstatService.comprobarPermisoDisponible(Permiso.MisProblemas) || rtpstatService.comprobarPermisoDisponible(Permiso.MisRelaciones)) {
				Map<Object, Object> parametros = getParameterMap(req);
				if (parametros.containsKey(NOMBRE_PARAMETRO_TIPO)) {
					String tipo = req.getParameter(NOMBRE_PARAMETRO_TIPO);
					if (TIPO_DESCARGA_PROBLEMA.equals(tipo) || TIPO_DESCARGA_RELACION.equals(tipo) || TIPO_DESCARGA_LIBRO.equals(tipo)) {
						if (parametros.containsKey(NOMBRE_PARAMETRO_ID)) {
							try {
								long id = Long.parseLong(req.getParameter(NOMBRE_PARAMETRO_ID));
								if (TIPO_DESCARGA_PROBLEMA.equals(tipo)) {
									if (parametros.containsKey(NOMBRE_PARAMETRO_FORMATO)) {
										String formato = req.getParameter(NOMBRE_PARAMETRO_FORMATO);
										if (FORMATO_PROBLEMA_PRESENTACION.equals(formato) || FORMATO_PROBLEMA_FOLIOS.equals(formato)) {
											descargarProblema(resp, id, formato);
										} else {
											throw new Exception("Formato desconocido para la descarga:" + formato);
										}
									} else {
										throw new Exception("Falta el formato para descargar el problema");
									}
								} else if (TIPO_DESCARGA_RELACION.equals(tipo)) {
									descargarRelacionEjercicios(resp, id);
								} else {
									descargarLibro(resp, id);
								}
							} catch (NumberFormatException ex) {
								throw new Exception("El valor de id no es un Long: " + req.getParameter(NOMBRE_PARAMETRO_ID), ex);
							}
						} else {
							throw new Exception("No se ha indicado ningún id, tipo = " + tipo);
						}

					} else {
						throw new Exception("Tipo de descarga desconocido: " + tipo);
					}
				} else {
					throw new Exception("Falta el tipo de descarga en la solicitud");
				}
			} else {
				throw new Exception("Permisos insuficientes para el usuario");
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "ERROR EN DESCARGAS", ex);
			resp.getOutputStream().write("ERROR".getBytes("UTF-8"));
		}
	}

	private void descargarLibro(HttpServletResponse resp, long id) throws Exception {
		LibroEnStore libro = rtpstatService.getLibroEnStore(id);
		if (libro != null) {
			User user = UserServiceFactory.getUserService().getCurrentUser();
			if (libro.getOwner().equals(user)) {
				URL url = new URL(rtpstatService.getURLLibro(id) + "&tipo=relacion&cache-destroyer=" + UUID.randomUUID());
				enviarURLACliente(resp, url);
				// URLConnection connection = url.openConnection();
				// resp.setContentLength(connection.getContentLength());
				// resp.setContentType(connection.getContentType());
				// resp.setHeader("Content-Disposition",
				// "attachment; filename=libro_" + id + ".pdf");
				// InputStream is = connection.getInputStream();
				// OutputStream os = resp.getOutputStream();
				// int leido;
				// byte[] buffer = new byte[256];
				// while ((leido = is.read(buffer)) != -1) {
				// os.write(buffer, 0, leido);
				// }
			} else {
				throw new Exception("Un usuario ha intentado descargar un problema que no es suyo");
			}
		} else {
			throw new Exception("Problema desconocido");
		}
	}

	@SuppressWarnings("unchecked")
	private Map<Object, Object> getParameterMap(HttpServletRequest req) {
		return req.getParameterMap();
	}

	private void descargarProblema(HttpServletResponse resp, long id, String formato) throws Exception {
		ProblemaEnStore problema = rtpstatService.getProblemaEnStore(id);
		if (problema != null) {
			User user = UserServiceFactory.getUserService().getCurrentUser();
			if (problema.getOwner().equals(user)) {
				URL url = new URL(rtpstatService.getURLDocumento(id) + "&tipo=" + formato + "&cache-destroyer=" + UUID.randomUUID());
				enviarURLACliente(resp, url);
				// URLConnection connection = url.openConnection();
				// resp.setContentLength(connection.getContentLength());
				// resp.setContentType(connection.getContentType());
				// resp.setHeader("Content-Disposition", "attachment; filename="
				// + formato + "_" + id + ".pdf");
				// InputStream is = connection.getInputStream();
				// OutputStream os = resp.getOutputStream();
				// int leido;
				// byte[] buffer = new byte[256];
				// while ((leido = is.read(buffer)) != -1) {
				// os.write(buffer, 0, leido);
				// }
			} else {
				throw new Exception("Un usuario ha intentado descargar un problema que no es suyo");
			}
		} else {
			throw new Exception("Problema desconocido");
		}
	}

	private void descargarRelacionEjercicios(HttpServletResponse resp, long id) throws Exception {
		RelacionEjerciciosEnstore problema = rtpstatService.getRelacionEjerciciosEnStore(id);
		if (problema != null) {
			User user = UserServiceFactory.getUserService().getCurrentUser();
			if (problema.getOwner().equals(user)) {
				URL url = new URL(rtpstatService.getURLRelacion(id) + "&tipo=relacion&cache-destroyer=" + UUID.randomUUID());
				enviarURLACliente(resp, url);
				// URLConnection connection = url.openConnection();
				// resp.setContentLength(connection.getContentLength());
				// resp.setContentType(connection.getContentType());
				// resp.setHeader("Content-Disposition",
				// "attachment; filename=relacion_" + id + ".pdf");
				// InputStream is = connection.getInputStream();
				// OutputStream os = resp.getOutputStream();
				// int leido;
				// byte[] buffer = new byte[256];
				// while ((leido = is.read(buffer)) != -1) {
				// os.write(buffer, 0, leido);
				// }
			} else {
				throw new Exception("Un usuario ha intentado descargar un problema que no es suyo");
			}
		} else {
			throw new Exception("Problema desconocido");
		}
	}

	private void enviarURLACliente(HttpServletResponse resp, URL url) {
		resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		resp.setHeader("Location", url.toString());
	}
}
