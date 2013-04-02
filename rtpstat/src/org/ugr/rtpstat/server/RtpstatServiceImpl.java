package org.ugr.rtpstat.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.Permiso;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResultadoGeneracionRelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.orm.ResumenRelacionEjercicios;
import org.ugr.rtpstat.client.orm.Rol;
import org.ugr.rtpstat.client.orm.TipoRelacion;
import org.ugr.rtpstat.client.uibinder.problemas.DialogoEliminarProblema.TipoRelacionado;
import org.ugr.rtpstat.server.backendproxy.Backend;
import org.ugr.rtpstat.server.backendproxy.BackendProxy;
import org.ugr.rtpstat.server.orm.ApartadoEnStore;
import org.ugr.rtpstat.server.orm.CapituloLibroEnStore;
import org.ugr.rtpstat.server.orm.LibroEnStore;
import org.ugr.rtpstat.server.orm.ProblemaEnStore;
import org.ugr.rtpstat.server.orm.RelacionEjerciciosEnstore;
import org.ugr.rtpstat.server.orm.UsuarioRegistrado;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RtpstatServiceImpl extends RemoteServiceServlet implements RtpstatService {

	private static Logger logger;
	{
		logger = Logger.getLogger(RtpstatServiceImpl.class.getSimpleName());
	}
	
	private UserService service = UserServiceFactory.getUserService();

	protected ProblemaEnStore getProblemaEnStore(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ProblemaEnStore problema = null;
		try {
			problema = pm.getObjectById(ProblemaEnStore.class, id);
			problema.getDatos();// Para forzar la carga de los datos
			problema.getApartadosEnStore();// Para forzar la carga de los
			// apartados
		} catch (Exception ex) {
			logger.info("Se ha intentado descargar un problema con id = " + id + ", que no existe");
		} finally {
			pm.close();
		}
		return problema;
	}

	private UsuarioRegistrado getRegisteredUser() throws JDOObjectNotFoundException {
		return getRegisteredUser(service.getCurrentUser().getUserId());
	}

	protected UsuarioRegistrado getRegisteredUser(String userId) throws JDOObjectNotFoundException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		UsuarioRegistrado out = null;
		try {
			out = pm.getObjectById(UsuarioRegistrado.class, userId);
		} catch (JDOObjectNotFoundException ex) {
			// No hacemos nada
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Al intentar cargar un usuario registrado", ex);
			throw new RuntimeException(ex);
		} finally {
			pm.close();
		}
		return out;
	}

	private void notificarCambioRol(UsuarioRegistrado usuario) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String htmlBody = "<p>Hola " + usuario.getNombreReal() + "</p><p>Su Rol en la aplicación RTPSTAT ha sido cambiado por un administrador.</p>" + "<p>Su nuevo Rol de usuario es: " + usuario.getRol().toLongString() + "</p>";
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("rtpstat.ugr@gmail.com", "RTPSTAT Admin"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(usuario.getRealUser().getEmail()));
			msg.setSubject(MimeUtility.encodeText("RTPSTAT: Su Rol ha cambiado"));
			msg.setText(htmlBody, "UTF-8", "html");

			Transport.send(msg);

		} catch (AddressException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar un cambio de rol", e);
		} catch (MessagingException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar un cambio de rol", e);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar un cambio de rol", e);
		}
	}

	protected boolean comprobarPermisoDisponible(Permiso permiso) {
		boolean out = false;
		if (isRegisteredUser()) {
			Rol rol = currentUserRol();
			if (rol != null && rol.permisos().contains(permiso)) {
				out = true;
			}
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	private List<UsuarioRegistrado> executeQuery(Query q, Rol rol) {
		return (List<UsuarioRegistrado>) q.execute(rol);
	}

	@SuppressWarnings("unchecked")
	private List<ProblemaEnStore> executeQuery(Query q, User u) {
		return (List<ProblemaEnStore>) q.execute(u);
	}

	@SuppressWarnings("unchecked")
	private List<RelacionEjerciciosEnstore> obtenerListadoRelacionesEnStore(Query query, User user) {
		return (List<RelacionEjerciciosEnstore>) query.execute(user);

	}

	private void notificarRegistro() {
		// TODO Notificar registro nuevo a todos los administradores
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		String msgBody = "Hola , existe un nuevo usuario pendiente de autorización. Visita https://rtpstat.appspot.com/#autorizar_usuarios para revisar el nuevo usuario.";

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("rtpstat.ugr@gmail.com", "RTPSTAT Admin"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("opsidao@gmail.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress("angelmajo@gmail.com"));
			msg.setSubject(MimeUtility.encodeText("RTPSTAT: Usuario nuevo"));
			msg.setText(msgBody);
			Transport.send(msg);

		} catch (AddressException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar una nueva autorizacion pendiente", e);
		} catch (MessagingException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar una nueva autorizacion pendiente", e);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.WARNING, "Fallo al intentar notificar una nueva autorizacion pendiente", e);
		}
	}

	public String[] listarEntidades() {
		return new String[] { "Universidad de Granada", "Universidad de Almería", "Universidad de Zaragoza", "Universidad de Jaén", "Universidad de Córdoba", "Universidad de Sevilla" };
	}

	public String[] listarAreasObjetivo() {
		// TODO Las areas objetivo validas estan hardCoded
		return new String[] { "Indeterminada", "Humanidades", "Ciencias Jurídicas y Sociales", "Ciencias Experimentales", "Ciencias de la Salud", "Técnicas" };
	}

	public String getUserName() {
		String out = null;
		UserService service = UserServiceFactory.getUserService();
		if (service.isUserLoggedIn()) {
			try {
				UsuarioRegistrado user = getRegisteredUser();
				if (user != null) {
					out = user.getNombreReal();
				}
			} catch (JDOObjectNotFoundException ex) {
				// No hacemos nada, out ya es null
			}
		}
		return out;
	}
	
	public boolean isRegisteredUser() {
		boolean out = false;
		if (getRegisteredUser() != null) {
			out = true;
		}
		return out;
	}

	public boolean registrarUsuario(String nombreReal, String institucion) {
		boolean out = false;
		if (!isRegisteredUser()) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				UsuarioRegistrado usuario = new UsuarioRegistrado(service.getCurrentUser(), nombreReal, institucion);
				pm.makePersistent(usuario);
				out = true;
				notificarRegistro();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Ha fallado el registro de un usario: ", ex);
			} finally {
				pm.close();
			}
		} else {
			logger.warning("Un usuario registrado está intentando volver a registrarse");
		}
		return out;
	}

	public Rol currentUserRol() {
		Rol out = null;
		try {
			if (isRegisteredUser()) {
				UsuarioRegistrado usuario = getRegisteredUser();
				if (usuario != null) {
					if (usuario.getRealUser().getEmail().equals("opsidao@gmail.com") || usuario.getRealUser().getEmail().equals("angelmajo@gmail.com")) {
						out = Rol.Administrador;
					} else {
						out = usuario.getRol();
					}
				}
			}
		} catch (JDOObjectNotFoundException ex) {

		}
		return out;
	}

	public String getLogingUrl(String targetURL) {
		return service.createLoginURL(targetURL);
	}

	public String getLogoutUrl() {
		return service.createLogoutURL("/");
	}

	public ArrayList<ResumenProblema> listadoProblemas() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(ProblemaEnStore.class);
			query.setFilter("owner == usuarioActivo");
			query.declareParameters("com.google.appengine.api.users.User usuarioActivo");
			try {
				List<ProblemaEnStore> listado = executeQuery(query, UserServiceFactory.getUserService().getCurrentUser());
				ArrayList<ResumenProblema> out = new ArrayList<ResumenProblema>();
				for (ProblemaEnStore p : listado) {
					out.add(new ResumenProblema(p.comoProblema()));
				}
				return out;
			} finally {
				query.closeAll();
			}
		} finally {
			pm.close();
		}
	}

	public long addProblema(Problema p) {
		long out = Long.MIN_VALUE;
		
		if (p.isValid()) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			try {
				tx.begin();
				ProblemaEnStore pes = new ProblemaEnStore(pm,p);
				
				Date fechaActual = new Date();
				pes.setFechaCreacion(fechaActual);
				pes.setUltimaActualizacion(fechaActual);

				String token = BackendProxy.getBackendProxy().solicitarGeneracionProblema(p);
				if (token.startsWith("ERROR:")) {
					logger.warning("No se ha podido enviar un problema al backend: " + token);
				}
				logger.info("Token recibido: " + token);
				pes.setToken(token);
				pm.makePersistent(pes);

				// TODO Marranada, pasarlo a problema es excesivo, para solo
				// sacar el ID
				out = pes.getKey().getId();
				tx.commit();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Ha fallado la comunicación con el backend", e);
			} finally {
				if (tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}
		} else {
			logger.info("Ignorando problema inválido");
		}
		return out;
	}

	public long duplicarProblema(long id, String nuevaDescripcion) {
		long out = 0;
		if (comprobarPermisoDisponible(Permiso.MisProblemas)) {
			ProblemaEnStore problema = getProblemaEnStore(id);
			if (problema != null) {
				Problema duplicado = problema.comoProblema();
				duplicado.setDescripcion(nuevaDescripcion);
				out = addProblema(duplicado);
			}
		}
		return out;
	}

	public void rmProblema(long id) {
		try {
			ProblemaEnStore problema = getProblemaEnStore(id);
			if (problema != null && problema.getOwner().equals(service.getCurrentUser())) {
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					List<RelacionEjerciciosEnstore> relacionesEjercicios = getRelacionesEjercicios(pm);
					for (RelacionEjerciciosEnstore relacion : relacionesEjercicios) {
						relacion.getProblemas().remove(problema.getKey());
						pm.makePersistent(relacion);
					}
				} finally {
					pm.close();
				}

				pm = PMF.get().getPersistenceManager();
				try {
					List<LibroEnStore> libros = getLibros(pm);
					for (LibroEnStore libro : libros) {
						for (CapituloLibroEnStore capitulo : libro.getCapitulos()) {
							capitulo.getPropuestos().remove(problema.getKey());
							capitulo.getResueltos().remove(problema.getKey());
							pm.makePersistent(capitulo);
						}
					}
				} finally {
					pm.close();
				}

				pm = PMF.get().getPersistenceManager();
				Transaction tx = pm.currentTransaction();
				try {
					tx.begin();
					pm.detachCopy(problema.getDatos());
					pm.deletePersistent(problema.getDatos());

					pm.detachCopy(problema);
					pm.deletePersistent(problema);
					tx.commit();
				} finally {
					if (tx.isActive()) {
						tx.rollback();
					}
					pm.close();
				}
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Excepción eliminando un problema", ex);
			throw new RuntimeException("No se ha podido eliminar el problema. ");
		}
	}

	public void updateProblema(Problema p) {
		try {
			if (p.isValid()) {
				long id = p.getId();
				ProblemaEnStore problema = getProblemaEnStore(id);
				if (problema == null) {
					throw new IllegalArgumentException("El identificador de problema no es válido");
				}
				if (problema.getOwner().equals(service.getCurrentUser())) {
					PersistenceManager pm = PMF.get().getPersistenceManager();
					Transaction tx = pm.currentTransaction();
					try {
						tx.begin();
						if (problema.getApartadosEnStore() != null) {
							for (ApartadoEnStore apartadoEnStore : problema.getApartadosEnStore()) {
								//ApartadoEnStore apartadoEnStore = pm.getObjectById(ApartadoEnStore.class, key.getId());
								pm.detachCopy(apartadoEnStore);
								pm.deletePersistent(apartadoEnStore);
							}
						}
						problema.fromProblema(pm,p);

						problema.setUltimaActualizacion(new Date());
						problema.setDocumentosGenerados(false);
						pm.makePersistent(problema);

						String token = BackendProxy.getBackendProxy().solicitarGeneracionProblema(p);
						if (token.startsWith("ERROR:")) {
							logger.warning("Error al cargar problema no manejado");
						}
						problema.setToken(token);
						pm.makePersistent(problema);
						tx.commit();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (tx.isActive()) {
							tx.rollback();
						}
						pm.close();
					}
				} else {
					throw new IllegalAccessError("Está intentando actualizar un problema que no es suyo");
				}
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Excepción actualizando un problema", ex);
			throw new RuntimeException("No se ha podido guardar el problema.  Si el error persiste, <a tabindex=\"0\" class=\"gwt-Anchor\" target=\"_blank\" href=\"http://code.google.com/p/rtpstat/issues/entry\">¡Cuéntanoslo!</a> ");
		}
	}

	public Problema getProblema(long id) {
		Problema out = null;
		ProblemaEnStore problema = getProblemaEnStore(id);
		if (problema != null && problema.getOwner().equals(service.getCurrentUser())) {
			out = problema.comoProblema();
		}
		return out;
	}

	public EstadoGeneracion comprobarEstadoGeneracionProblema(long id) {
		// TODO Cachear el estado de un problema cuando este sea OK
		EstadoGeneracion out = EstadoGeneracion.ERROR;
		ProblemaEnStore problema = getProblemaEnStore(id);
		if (problema != null && problema.getOwner().equals(service.getCurrentUser())) {
			if (problema.getToken().startsWith("ERROR:")) {
				out = EstadoGeneracion.ERROR;
			} else {
				out = BackendProxy.getBackendProxy().getEstado(problema.getToken(), ProblemaEnStore.class);
			}
		}
		return out;
	}

	public String getCausaErrorProblema(long id) {
		String out = null;
		ProblemaEnStore problema = getProblemaEnStore(id);
		if (problema != null) {
			if (problema.getOwner().equals(service.getCurrentUser())) {
				if (problema.getToken().startsWith("ERROR:")) {
					out = problema.getToken();
				} else if (EstadoGeneracion.ERROR.equals(comprobarEstadoGeneracionProblema(id))) {
					Backend backend = BackendProxy.getBackendProxy();
					out = backend.getMensajeError(problema.getToken());
				} else {
					out = "No existen errores para ese problema";
				}
			}
		}
		return out;
	}

	public String getURLDocumento(long id) {
		String out = null;
		ProblemaEnStore problema = getProblemaEnStore(id);
		if (problema != null && problema.getOwner().equals(service.getCurrentUser())) {
			out = BackendProxy.getBackendProxy().getURLDocumento(problema.getToken());
		}
		return out;
	}

	public List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> usuariosSinAutorizar() {
		List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> out = null;
		if (isRegisteredUser()) {
			if (currentUserRol().permisos().contains(Permiso.AutorizarUsuarios)) {
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					Query q = pm.newQuery(UsuarioRegistrado.class);
					q.setFilter("rol == rolParam");
					q.declareImports("import org.ugr.rtpstat.client.orm.Rol;");
					q.declareParameters("Rol rolParam");

					List<UsuarioRegistrado> usuarios = executeQuery(q, Rol.UsuarioNuevo);
					out = new ArrayList<org.ugr.rtpstat.client.orm.UsuarioRegistrado>();
					for (UsuarioRegistrado usuario : usuarios) {
						out.add(usuario.paraCliente());
					}
				} finally {
					pm.close();
				}
			} else {
				logger.warning("Un usuario no autorizado ha intentado acceder al listado de usuarios sin registrar: " + service.getCurrentUser().getUserId());
			}
		}
		return out;
	}

	public List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> usuariosAutorizados() {
		List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> out = null;
		if (isRegisteredUser()) {
			if (currentUserRol().permisos().contains(Permiso.AutorizarUsuarios)) {
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					Query q = pm.newQuery(UsuarioRegistrado.class);
					q.setFilter("rol != rolParam");
					q.declareImports("import org.ugr.rtpstat.client.orm.Rol;");
					q.declareParameters("Rol rolParam");

					List<UsuarioRegistrado> usuarios = executeQuery(q, Rol.UsuarioNuevo);
					out = new ArrayList<org.ugr.rtpstat.client.orm.UsuarioRegistrado>();
					for (UsuarioRegistrado usuario : usuarios) {
						out.add(usuario.paraCliente());
					}
				} finally {
					pm.close();
				}
			} else {
				logger.warning("Un usuario no autorizado ha intentado acceder al listado de usuarios sin registrar: " + service.getCurrentUser().getUserId());
			}
		}
		return out;
	}

	public org.ugr.rtpstat.client.orm.UsuarioRegistrado cambiarRolUsuario(String userId, Rol nuevoRol) {
		org.ugr.rtpstat.client.orm.UsuarioRegistrado out = null;
		if (comprobarPermisoDisponible(Permiso.AutorizarUsuarios)) {
			UsuarioRegistrado usuario = getRegisteredUser(userId);

			usuario.setRol(nuevoRol);
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(usuario);
				out = usuario.paraCliente();
				notificarCambioRol(usuario);
			} finally {
				pm.close();
			}
		}
		return out;
	}

	@Override
	public EstadoGeneracion comprobarEstadoGeneracionRelacion(long id) {
		EstadoGeneracion out = EstadoGeneracion.ERROR;
		RelacionEjerciciosEnstore relacion = getRelacionEjerciciosEnStore(id);
		if (relacion != null && relacion.getOwner().equals(service.getCurrentUser())) {
			if (relacion.getToken() == null || relacion.getToken().startsWith("ERROR:")) {
				out = EstadoGeneracion.ERROR;
			} else {
				out = BackendProxy.getBackendProxy().getEstado(relacion.getToken(), RelacionEjerciciosEnstore.class);
			}
		}
		return out;
	}

	@Override
	public String getCausarErrorRelacion(long id) {
		String out = null;
		RelacionEjerciciosEnstore relacion = getRelacionEjerciciosEnStore(id);
		if (relacion != null) {
			if (relacion.getOwner().equals(service.getCurrentUser())) {
				if (relacion.getToken() == null) {
					out = "Aún no se ha solicitado la generación de este PDF";
				} else if (relacion.getToken().startsWith("ERROR:")) {
					out = relacion.getToken();
				} else if (EstadoGeneracion.ERROR.equals(comprobarEstadoGeneracionProblema(id))) {
					Backend backend = BackendProxy.getBackendProxy();
					out = backend.getMensajeError(relacion.getToken());
				} else {
					out = "No existen errores para ese problema";
				}
			}
		}
		return out;
	}

	@Override
	public String getURLRelacion(long id) {
		String out = null;
		RelacionEjerciciosEnstore relacion = getRelacionEjerciciosEnStore(id);
		if (relacion != null && relacion.getOwner().equals(service.getCurrentUser())) {
			out = BackendProxy.getBackendProxy().getURLDocumento(relacion.getToken());
		}
		return out;
	}

	RelacionEjerciciosEnstore getRelacionEjerciciosEnStore(long id) {
		RelacionEjerciciosEnstore out = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			out = pm.getObjectById(RelacionEjerciciosEnstore.class, id);
		} catch (Exception ex) {
			logger.info("Se ha intentado descargar una relación con id = " + id + ", que no existe");
		} finally {
			pm.close();
		}
		return out;
	}

	public ResultadoGeneracionRelacionEjercicios addRelacion(String titulo, HashMap<GradoDificultad, Integer> problemasDef, String[] areas, TipoRelacion tipo) throws IOException {
		ResultadoGeneracionRelacionEjercicios res = null;
		if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
			RelacionEjerciciosEnstore relacion = new RelacionEjerciciosEnstore(service.getCurrentUser(), titulo, areas, tipo);
			res = RelacionEjerciciosEnstore.generarRelacion(relacion, problemasDef, areas);
			ArrayList<ResumenProblema> problemas = new ArrayList<ResumenProblema>();
			for (Key k : relacion.getProblemas()) {
				problemas.add(new ResumenProblema(getProblema(k.getId())));
			}
			relacion.setToken(BackendProxy.getBackendProxy().solicitarGeneracionRelacion(new RelacionEjercicios(relacion.getKey().getId(), relacion.getTitulo(), relacion.getTipo(), relacion.getAreasObjetivo(), problemas)));
		}
		return res;
	}

	public ResultadoGeneracionRelacionEjercicios addRelacion(String titulo, TipoRelacion tipo) throws IOException {
		ResultadoGeneracionRelacionEjercicios res = null;
		if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
			RelacionEjerciciosEnstore relacion = new RelacionEjerciciosEnstore(service.getCurrentUser(), titulo, new String[] { "Indeterminada" }, tipo);
			relacion.setProblemas(new ArrayList<Key>());
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(relacion);
				res = new ResultadoGeneracionRelacionEjercicios();
				res.setIdRelacion(relacion.getKey().getId());
				ArrayList<ResumenProblema> problemas = new ArrayList<ResumenProblema>();
				for (Key k : relacion.getProblemas()) {
					problemas.add(new ResumenProblema(getProblema(k.getId())));
				}
				// relacion.setToken(BackendProxy.getBackendProxy().solicitarGeneracionRelacion(new
				// RelacionEjercicios(relacion.getKey().getId(),
				// relacion.getTitulo(), relacion.getTipo(),
				// relacion.getAreasObjetivo(), problemas)));
			} finally {
				pm.close();
			}
		}
		return res;
	}

	public ArrayList<ResumenRelacionEjercicios> listarRelacionesEjercicios() {
		ArrayList<ResumenRelacionEjercicios> out = null;
		if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				Query query = pm.newQuery(RelacionEjerciciosEnstore.class);
				query.setFilter("owner == usuarioActivo");
				query.declareParameters("com.google.appengine.api.users.User usuarioActivo");
				try {
					List<RelacionEjerciciosEnstore> relacionesEnStore = obtenerListadoRelacionesEnStore(query, service.getCurrentUser());
					out = new ArrayList<ResumenRelacionEjercicios>();
					for (RelacionEjerciciosEnstore r : relacionesEnStore) {
						out.add(new ResumenRelacionEjercicios(r.getKey().getId(), r.getTitulo(), r.getAreasObjetivo(), r.getTipo(), r.getProblemas().size(), r.isGenerada()));
					}
				} finally {
					query.closeAll();
				}
			} finally {
				pm.close();
			}
		}
		return out;
	}

	public RelacionEjercicios getRelacionEjercicios(long id) {
		RelacionEjercicios out = null;
		if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				RelacionEjerciciosEnstore relacion = pm.getObjectById(RelacionEjerciciosEnstore.class, id);
				if (service.getCurrentUser().equals(relacion.getOwner())) {
					ArrayList<ResumenProblema> problemas = new ArrayList<ResumenProblema>();
					for (Key problemaKey : relacion.getProblemas()) {
						ProblemaEnStore problema = pm.getObjectById(ProblemaEnStore.class, problemaKey);
						ResumenProblema resumen = new ResumenProblema(problema.comoProblema());
						problemas.add(resumen);
					}
					out = new RelacionEjercicios(relacion.getKey().getId(), relacion.getTitulo(), relacion.getTipo(), relacion.getAreasObjetivo(), problemas);
				} else {
					logger.info("Un usuario ha intentado descargar el problema con id " + id + ", que no es suyo");
				}
			} catch (Exception ex) {
				logger.info("Se ha intentado descargar una relación con id = " + id + ", que no existe");
			} finally {
				pm.close();
			}
		}
		return out;
	}

	public boolean updateRelacion(long id, String tituloNuevo, TipoRelacion tipoNuevo, String[] areas_objetivo, ArrayList<Long> problemasIncluidos) {
		boolean out = false;
		try {
			if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
				PersistenceManager pm = PMF.get().getPersistenceManager();
				try {
					RelacionEjerciciosEnstore relacion = pm.getObjectById(RelacionEjerciciosEnstore.class, id);
					if (service.getCurrentUser().equals(relacion.getOwner())) {
						relacion.setTitulo(tituloNuevo);
						relacion.setTipo(tipoNuevo);
						relacion.setAreasObjetivo(areas_objetivo);
						ArrayList<Key> nuevosProblemas = new ArrayList<Key>();
						for (Long idProblema : problemasIncluidos) {
							nuevosProblemas.add(KeyFactory.createKey(ProblemaEnStore.class.getSimpleName(), idProblema));
						}
						relacion.setProblemas(nuevosProblemas);
						pm.makePersistent(relacion);
						ArrayList<ResumenProblema> problemas = new ArrayList<ResumenProblema>();
						for (Key k : relacion.getProblemas()) {
							problemas.add(new ResumenProblema(getProblema(k.getId())));
						}
						relacion.setToken(BackendProxy.getBackendProxy().solicitarGeneracionRelacion(new RelacionEjercicios(relacion.getKey().getId(), relacion.getTitulo(), relacion.getTipo(), relacion.getAreasObjetivo(), problemas)));
						out = true;
					} else {
						logger.info("Un usuario ha intentado actualizar el problema con id " + id + ", que no es suyo");
					}
				} catch (Exception ex) {
					logger.info("Se ha intentado actualizar una relación con id = " + id + ", que no existe");
				} finally {
					pm.close();
				}
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Excepción actualizando una relación", ex);
			throw new RuntimeException(ex);
		}
		return out;
	}

	@Override
	public long addLibro(Libro libro) {
		long out = Long.MIN_VALUE;
		if (comprobarPermisoDisponible(Permiso.MisLibros)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				ArrayList<CapituloLibroEnStore> capitulos = new ArrayList<CapituloLibroEnStore>();
				for (CapituloLibro capitulo : libro.getCapitulos()) {
					ArrayList<Key> resueltos = new ArrayList<Key>();
					for (ResumenProblema resumen : capitulo.getResueltos()) {
						ProblemaEnStore p = getProblemaEnStore(resumen.getId());
						resueltos.add(p.getKey());
					}

					ArrayList<Key> propuestos = new ArrayList<Key>();
					for (ResumenProblema resumen : capitulo.getPropuestos()) {
						ProblemaEnStore p = getProblemaEnStore(resumen.getId());
						propuestos.add(p.getKey());
					}
					CapituloLibroEnStore capituloEnStore = new CapituloLibroEnStore(capitulo.getTitulo(), capitulo.getResueltosTitulo(), resueltos, capitulo.getPropuestosTitulo(), propuestos);
					capitulos.add(capituloEnStore);
				}
				LibroEnStore libroEnStore = new LibroEnStore(service.getCurrentUser(), libro.getTitulo(), capitulos, libro.getAreasObjetivo());
				libroEnStore.setToken(BackendProxy.getBackendProxy().solicitarGeneracionLibro(libro));
				pm.makePersistent(libroEnStore);
				out = libroEnStore.getKey().getId();
			} catch (IOException ex) {
				logger.log(Level.WARNING, "Error de comunicación con el backend", ex);
				throw new RuntimeException();
			} finally {
				pm.close();
			}
		}

		return out;
	}

	@Override
	public ArrayList<Libro> listarLibros() {
		ArrayList<Libro> out = null;
		if (comprobarPermisoDisponible(Permiso.MisLibros)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				Query query = pm.newQuery(LibroEnStore.class);
				query.setFilter("owner == usuarioActivo");
				query.declareParameters("com.google.appengine.api.users.User usuarioActivo");
				try {
					List<LibroEnStore> lista = executeQueryListarLibros(query, UserServiceFactory.getUserService().getCurrentUser());
					out = new ArrayList<Libro>();
					for (LibroEnStore libro : lista) {
						ArrayList<CapituloLibro> capitulos = new ArrayList<CapituloLibro>();
						for (CapituloLibroEnStore capitulo : libro.getCapitulos()) {
							capitulos.add(capitulo.toCapituloLibro());
						}
						Libro nuevo = new Libro(libro.getKey().getId(), libro.getTitulo(), capitulos, libro.getAreasObjetivo());
						out.add(nuevo);
					}
				} finally {
					query.closeAll();
				}
			} finally {
				pm.close();
			}
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	private List<LibroEnStore> executeQueryListarLibros(Query query, User currentUser) {
		return (List<LibroEnStore>) query.execute(currentUser);
	}

	@Override
	public Libro getLibro(Long id) {
		Libro out = null;
		if (comprobarPermisoDisponible(Permiso.MisLibros)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				LibroEnStore libro = pm.getObjectById(LibroEnStore.class, id);
				ArrayList<CapituloLibro> capitulos = new ArrayList<CapituloLibro>();
				for (CapituloLibroEnStore capitulo : libro.getCapitulos()) {
					capitulos.add(capitulo.toCapituloLibro());
				}
				out = new Libro(libro.getKey().getId(), libro.getTitulo(), capitulos, libro.getAreasObjetivo());
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Al intentar descargar un libro", ex);
			} finally {
				pm.close();
			}
		}
		return out;
	}

	@Override
	public boolean updateLibro(Libro libro) {
		boolean out = false;
		if (comprobarPermisoDisponible(Permiso.MisLibros)) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				LibroEnStore libroEnStore = pm.getObjectById(LibroEnStore.class, libro.getId());
				libroEnStore.setTitulo(libro.getTitulo());
				libroEnStore.setAreasObjetivo(libro.getAreasObjetivo());
				ArrayList<CapituloLibroEnStore> capitulos = new ArrayList<CapituloLibroEnStore>();
				for (CapituloLibro capitulo : libro.getCapitulos()) {
					ArrayList<Key> resueltos = new ArrayList<Key>();
					for (ResumenProblema resumen : capitulo.getResueltos()) {
						resueltos.add(KeyFactory.createKey(ProblemaEnStore.class.getSimpleName(), resumen.getId()));
					}

					ArrayList<Key> propuestos = new ArrayList<Key>();
					for (ResumenProblema resumen : capitulo.getPropuestos()) {
						propuestos.add(KeyFactory.createKey(ProblemaEnStore.class.getSimpleName(), resumen.getId()));
					}
					CapituloLibroEnStore capituloEnStore = new CapituloLibroEnStore(capitulo.getTitulo(), capitulo.getResueltosTitulo(), resueltos, capitulo.getPropuestosTitulo(), propuestos);
					capitulos.add(capituloEnStore);
				}
				libroEnStore.setCapitulos(capitulos);
				libroEnStore.setToken(BackendProxy.getBackendProxy().solicitarGeneracionLibro(libro));
				pm.makePersistent(libroEnStore);
				out = true;
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Al intentar actualizar un libro", ex);
			} finally {
				pm.close();
			}
		}
		return out;
	}

	@Override
	public EstadoGeneracion comprobarEstadoGeneracionLibro(Long id) {
		EstadoGeneracion out = EstadoGeneracion.ERROR;

		LibroEnStore libro = getLibroEnStore(id);
		if (libro != null && libro.getOwner().equals(service.getCurrentUser())) {
			if (libro.getToken().startsWith("ERROR:")) {
				out = EstadoGeneracion.ERROR;
			} else {
				out = BackendProxy.getBackendProxy().getEstado(libro.getToken(), LibroEnStore.class);
			}
		}
		return out;
	}

	LibroEnStore getLibroEnStore(Long id) {
		LibroEnStore out = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			out = pm.getObjectById(LibroEnStore.class, id);
		} catch (Exception ex) {
			logger.log(Level.INFO, "Al intentar extraer un libro del DataStore", ex);
		} finally {
			pm.close();
		}
		return out;
	}

	@Override
	public String getCausarErrorLibro(Long id) {
		String out = null;
		LibroEnStore libro = getLibroEnStore(id);
		if (libro != null) {
			if (libro.getOwner().equals(service.getCurrentUser())) {
				if (libro.getToken().startsWith("ERROR:")) {
					out = libro.getToken();
				} else if (EstadoGeneracion.ERROR.equals(comprobarEstadoGeneracionProblema(id))) {
					Backend backend = BackendProxy.getBackendProxy();
					out = backend.getMensajeError(libro.getToken());
				} else {
					out = "No existen errores para ese problema";
				}
			}
		}
		return out;
	}

	public String getURLLibro(long id) {
		String out = null;
		LibroEnStore libro = getLibroEnStore(id);
		if (libro != null && libro.getOwner().equals(service.getCurrentUser())) {
			out = BackendProxy.getBackendProxy().getURLDocumento(libro.getToken());
		}
		return out;
	}

	@Override
	public Map<TipoRelacionado, ArrayList<String>> getRelacionesLibrosRelacionados(long idProblema) {
		Map<TipoRelacionado, ArrayList<String>> out = null;
		try {
			if (comprobarPermisoDisponible(Permiso.MisProblemas)) {
				ProblemaEnStore problemaEnStore = getProblemaEnStore(idProblema);
				if (problemaEnStore != null) {
					out = new HashMap<TipoRelacionado, ArrayList<String>>();
					if (comprobarPermisoDisponible(Permiso.MisRelaciones)) {
						out.put(TipoRelacionado.RELACION, getRelacionesRelacionadas(problemaEnStore));
					}
					if (comprobarPermisoDisponible(Permiso.MisLibros)) {
						out.put(TipoRelacionado.LIBRO, getLibrosRelacionados(problemaEnStore));
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Obteniendo libros y relaciones relacionados al problema " + idProblema, ex);
			throw new RuntimeException(ex);
		}
		return out;
	}

	private ArrayList<String> getLibrosRelacionados(ProblemaEnStore problemaEnStore) {
		ArrayList<String> out = new ArrayList<String>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			List<LibroEnStore> libros = getLibros(pm);
			for (LibroEnStore libro : libros) {
				CAPITULOS: for (CapituloLibroEnStore capitulo : libro.getCapitulos()) {
					for (Key claveProblema : capitulo.getResueltos()) {
						if (problemaEnStore.getKey().equals(claveProblema)) {
							out.add(libro.getTitulo());
							break CAPITULOS;
						}
					}
				}
			}
		} finally {
			pm.close();
		}
		return out;
	}

	private ArrayList<String> getRelacionesRelacionadas(ProblemaEnStore problemaEnStore) {
		ArrayList<String> out = new ArrayList<String>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			List<RelacionEjerciciosEnstore> relacionesEjercicios = getRelacionesEjercicios(pm);
			for (RelacionEjerciciosEnstore relacion : relacionesEjercicios) {
				for (Key claveProblema : relacion.getProblemas()) {
					if (problemaEnStore.getKey().equals(claveProblema)) {
						out.add(relacion.getTitulo());
					}
				}
			}
		} finally {
			pm.close();
		}

		return out;
	}

	@SuppressWarnings("unchecked")
	private List<RelacionEjerciciosEnstore> getRelacionesEjercicios(PersistenceManager pm) {
		Query q = pm.newQuery(RelacionEjerciciosEnstore.class);
		return (List<RelacionEjerciciosEnstore>) q.execute();
	}

	@SuppressWarnings("unchecked")
	private List<LibroEnStore> getLibros(PersistenceManager pm) {
		Query q = pm.newQuery(LibroEnStore.class);
		return (List<LibroEnStore>) q.execute();

	}

	@Override
	public void rmRelacion(long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			RelacionEjerciciosEnstore relacionEjerciciosEnstore = pm.getObjectById(RelacionEjerciciosEnstore.class, id);
			pm.detachCopy(relacionEjerciciosEnstore);
			pm.deletePersistent(relacionEjerciciosEnstore);
		} finally {
			pm.close();
		}
	}

	@Override
	public void rmLibro(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try {
			tx.begin();
			LibroEnStore libroEnStore = pm.getObjectById(LibroEnStore.class, id);
			for (CapituloLibroEnStore capitulo : libroEnStore.getCapitulos()) {
				pm.detachCopy(capitulo);
				pm.deletePersistent(capitulo);
			}
			pm.detachCopy(libroEnStore);
			pm.deletePersistent(libroEnStore);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
	}
}
