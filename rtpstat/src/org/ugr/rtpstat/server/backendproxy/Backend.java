package org.ugr.rtpstat.server.backendproxy;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;

public interface Backend {
	/**
	 * Solicita a alguno de los backends que genere los PDFs asociado al problema
	 * que se le envia.
	 * 
	 * @param p
	 *            El problema del que se quiere generar un PDF.
	 * @return el UUID asociado al problema que permite obtener la URL del PDF
	 *         del problema
	 * @throws IOException
	 *             Si no es posible comunicarse con ninguno de los backends
	 */
	public String solicitarGeneracionProblema(Problema p) throws IOException;

	/**
	 * Solicita a alguno de los backends que genere el PDF asociado a la relación
	 * de ejercicios que se le envia
	 * 
	 * @param r
	 *            La relación de ejercicios de la que se quiere generar un PDF
	 * @return el UUID asociado a la relación de ejercicios que permite obtener
	 *         la URL del PDF del problema
	 * @throws IOException
	 *             Si no es posible comunicarse con ninguno de los backends
	 */
	public String solicitarGeneracionRelacion(RelacionEjercicios r) throws IOException;

	/**
	 * Solicita a alguno de los backends que genere el PDF asociado a la relación
	 * de ejercicios que se le envia
	 * 
	 * @param r
	 *            La relación de ejercicios de la que se quiere generar un PDF
	 * @return el UUID asociado a la relación de ejercicios que permite obtener
	 *         la URL del PDF del problema
	 * @throws IOException
	 *             Si no es posible comunicarse con ninguno de los backends
	 */
	public String solicitarGeneracionLibro(Libro libro) throws IOException;
	
	/**
	 * Permite consulta el estado de la generación del PDF asociado al token
	 * pasado como parametro.
	 * 
	 * @param token
	 *            Un token unico obtenido usando
	 *            {@link #solicitarGeneracionProblema(Problema)}
	 *        clase
	 *        	Puedes ser ProblemaEnStore o RelacionEjerciciosEnStore según sobre qué se quiera consultar
	 * @return El ultimo estado conocido para el token
	 * @throws NoSuchElementException
	 *             Si no existe ningún problema asociado al token indicado
	 */
	public abstract EstadoGeneracion getEstado(String token,Class<?> clase) throws NoSuchElementException;

	/**
	 * Obtiene la URL para descargar el documento (A4s) del problema. Esa URL
	 * puede ser de cualquier backend (servidor).
	 * 
	 * @param token
	 *            del que se quiere obtener la URL
	 * @return La URL para descargar el documento
	 */
	public String getURLDocumento(String token);

	/**
	 * Obtiene el mensaje de error, si existe, para el problema asociado al
	 * token
	 * 
	 * @param token
	 */
	public String getMensajeError(String token);
}