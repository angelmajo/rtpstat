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
import org.ugr.rtpstat.client.orm.UsuarioRegistrado;
import org.ugr.rtpstat.client.uibinder.problemas.DialogoEliminarProblema.TipoRelacionado;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface RtpstatServiceAsync {
	void listadoProblemas(AsyncCallback<List<ResumenProblema>> callback);

	void listarAreasObjetivo(AsyncCallback<String[]> callback);

	void getUserName(AsyncCallback<String> callback);

	void getLogoutUrl(AsyncCallback<String> callback);

	void getLogingUrl(String targetURL, AsyncCallback<String> callback);

	void addProblema(Problema p, AsyncCallback<Long> callback);

	void duplicarProblema(long id, String nuevaDescripcion, AsyncCallback<Long> callback);

	void rmProblema(long id, AsyncCallback<Void> callback);

	void getProblema(long id, AsyncCallback<Problema> callback);

	void updateProblema(Problema p, AsyncCallback<Void> callback);

	void comprobarEstadoGeneracionProblema(long id, AsyncCallback<EstadoGeneracion> callback);

	void getURLDocumento(long id, AsyncCallback<String> callback);

	void isRegisteredUser(AsyncCallback<Boolean> callback);

	void listarEntidades(AsyncCallback<String[]> callback);

	void registrarUsuario(String nombreReal, String institucion, AsyncCallback<Boolean> callback);

	void currentUserRol(AsyncCallback<Rol> callback);

	void usuariosSinAutorizar(AsyncCallback<List<org.ugr.rtpstat.client.orm.UsuarioRegistrado>> callback);

	void cambiarRolUsuario(String userId, Rol nuevoRol, AsyncCallback<org.ugr.rtpstat.client.orm.UsuarioRegistrado> callback);

	void usuariosAutorizados(AsyncCallback<List<UsuarioRegistrado>> callback);

	void getCausaErrorProblema(long id, AsyncCallback<String> callback);

	void getURLRelacion(long id, AsyncCallback<String> callback);

	void addRelacion(String titulo, HashMap<GradoDificultad, Integer> problemas, String[] areas, TipoRelacion tipo,
			AsyncCallback<ResultadoGeneracionRelacionEjercicios> asyncCallback) throws IOException;

	void addRelacion(String titulo, TipoRelacion tipo, AsyncCallback<ResultadoGeneracionRelacionEjercicios> callback) throws IOException;

	void listarRelacionesEjercicios(AsyncCallback<ArrayList<ResumenRelacionEjercicios>> callback);

	void getRelacionEjercicios(long id, AsyncCallback<RelacionEjercicios> callback);

	void updateRelacion(long idRelacion, String tituloNuevo, TipoRelacion tipoNuevo, String[] array, ArrayList<Long> problemasIncluidos,
			AsyncCallback<Boolean> asyncCallback) throws IOException;

	void comprobarEstadoGeneracionRelacion(long id,
			AsyncCallback<EstadoGeneracion> callback);

	void getCausarErrorRelacion(long id, AsyncCallback<String> callback);

	void addLibro(Libro libro, AsyncCallback<Long> asyncCallback);

	void listarLibros(AsyncCallback<ArrayList<Libro>> callback);

	void getLibro(Long id, AsyncCallback<Libro> asyncCallback);

	void updateLibro(Libro libro, AsyncCallback<Boolean> asyncCallback);

	void comprobarEstadoGeneracionLibro(Long id, AsyncCallback<EstadoGeneracion> asyncCallback);

	void getCausarErrorLibro(Long id, AsyncCallback<String> asyncCallback);

	void getRelacionesLibrosRelacionados(long idProblema, AsyncCallback<Map<TipoRelacionado, ArrayList<String>>> asyncCallback);

	void rmRelacion(long id, AsyncCallback<Void> asyncCallback);

	void rmLibro(Long id, AsyncCallback<Void> asyncCallback);
}
