package org.ugr.rtpstat.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResultadoGeneracionRelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.orm.ResumenRelacionEjercicios;
import org.ugr.rtpstat.client.orm.Rol;
import org.ugr.rtpstat.client.orm.TipoRelacion;
import org.ugr.rtpstat.client.uibinder.problemas.DialogoEliminarProblema.TipoRelacionado;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service")
public interface RtpstatService extends RemoteService {
	/* MISCELANEA */
	String[] listarEntidades();

	String[] listarAreasObjetivo();

	/* INICIO DE SESIÓN Y REGISTRO DE USUARIOS */
	String getUserName();

	boolean isRegisteredUser();

	boolean registrarUsuario(String nombreReal, String institucion);

	Rol currentUserRol();

	String getLogingUrl(String targetURL);

	String getLogoutUrl();

	/* GESTIÓN DE PROBLEMAS */
	List<ResumenProblema> listadoProblemas();

	long addProblema(Problema p);

	long duplicarProblema(long id, String nuevaDescripcion);

	void rmProblema(long id);

	void updateProblema(Problema p);

	Problema getProblema(long id);

	/* DESCARGA DE PROBLEMAS */
	EstadoGeneracion comprobarEstadoGeneracionProblema(long id);

	String getCausaErrorProblema(long id);

	String getURLDocumento(long id);

	/* AUTORIZACION DE USUARIOS */
	List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> usuariosSinAutorizar();

	List<org.ugr.rtpstat.client.orm.UsuarioRegistrado> usuariosAutorizados();

	org.ugr.rtpstat.client.orm.UsuarioRegistrado cambiarRolUsuario(
			String userId, Rol nuevoRol);

	/* DESCARGA DE RELACIONES */
	EstadoGeneracion comprobarEstadoGeneracionRelacion(long id);

	String getCausarErrorRelacion(long id);
	
	String getURLRelacion(long id);

	/* RELACIONES DE EJERCICIOS */
	ResultadoGeneracionRelacionEjercicios addRelacion(String titulo,
			HashMap<GradoDificultad, Integer> problemas, String[] areas,
			TipoRelacion tipo) throws IOException;

	ResultadoGeneracionRelacionEjercicios addRelacion(String titulo,
			TipoRelacion tipo) throws IOException;

	ArrayList<ResumenRelacionEjercicios> listarRelacionesEjercicios();

	RelacionEjercicios getRelacionEjercicios(long id);

	boolean updateRelacion(long idRelacion, String tituloNuevo,
			TipoRelacion tipoNuevo, String[] array,
			ArrayList<Long> problemasIncluidos) throws IOException;

	/* Libros */
	long addLibro(Libro libro);
	ArrayList<Libro> listarLibros();

	Libro getLibro(Long id);

	boolean updateLibro(Libro libro);

	EstadoGeneracion comprobarEstadoGeneracionLibro(Long id);

	String getCausarErrorLibro(Long id);

	Map<TipoRelacionado, ArrayList<String>> getRelacionesLibrosRelacionados(long idProblema);

	void rmRelacion(long id);

	void rmLibro(Long id);
}
