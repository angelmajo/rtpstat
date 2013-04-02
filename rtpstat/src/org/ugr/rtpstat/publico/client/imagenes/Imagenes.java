package org.ugr.rtpstat.publico.client.imagenes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Imagenes extends ClientBundle {
	public final static Imagenes INSTANCE = GWT.create(Imagenes.class);
	
	@Source("cabecera.png")
	public ImageResource cabecera();
	
	@Source("logo_ugr.png")
	public ImageResource logo_ugr();

	@Source("competencias_clave.png")
	public ImageResource competencias_clave();
	
	@Source("competencias.png")
	public ImageResource competencias();
	
	@Source("encuesta_fisica.png")
	public ImageResource encuesta_fisica();
	
	@Source("encuesta_web.png")
	public ImageResource encuesta_web();
	
	@Source("valoracion_media_bloque_2.png")
	public ImageResource valoracion_media_bloque_2();
	
	@Source("valoraciones_bloque_1.png")
	public ImageResource valoraciones_bloque_1();
	
	@Source("valoraciones_bloque_2.png")
	public ImageResource valoraciones_bloque_2();
}
