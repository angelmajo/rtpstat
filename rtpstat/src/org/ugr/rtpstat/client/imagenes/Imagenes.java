package org.ugr.rtpstat.client.imagenes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Imagenes extends ClientBundle {
	public final static Imagenes INSTANCE = GWT.create(Imagenes.class);
	
	@Source("arrow_black_gloss.png")
	public ImageResource flechaGlossGrande();
	
	@Source("flecha_arriba.png")
	public ImageResource flechaArriba();
	
	@Source("flecha_abajo.png")
	public ImageResource flechaAbajo();
	
	@Source("Download.png")
	public ImageResource downloadIcon();
	
	@Source("Write2.png")
	public ImageResource editarIcon();
	
	@Source("Trash.png")
	public ImageResource eliminarIcon();
	
	@Source("Warning.png")
	public ImageResource warningIcon();
	
	@Source("Warning-Red.png")
	public ImageResource warningRedIcon();
	
	@Source("Rss.png")
	public ImageResource feedIcon();
	
	@Source("Ok48.png")
	public ImageResource okIcon48x48();
	
	@Source("Ok24.png")
	public ImageResource okIcon24x24();
	
	@Source("Info16.png")
	public ImageResource infoIcon16x16();
	
	@Source("Arrow3 Left - 48.png")
	public ImageResource simpleArrowLeft48();
	
	@Source("Arrow3 Right - 48.png")
	public ImageResource simpleArrowRight48();
	
	@Source("ajax-loader.gif")
	public ImageResource ajaxLoader();
	
	@Source("loader_square24.gif")//http://www.preloaders.net/en/rectangular
	public ImageResource ajaxLoaderCuadrado24();
	
	@Source("logo-rtpstat-small.png")
	public ImageResource logoSmall();
	
	@Source("Question24.png")
	public ImageResource questionMark24();
	
	@Source("minimizar.png")
	public ImageResource minimizar();
	
	@Source("maximizar.png")
	public ImageResource maximizar();
	
	@Source("restaurar.png")
	public ImageResource restaurar();
	
	@Source("Clipboard Copy.png")
	public ImageResource duplicarProblema();
}
