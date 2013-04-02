package org.ugr.rtpstat.client.uibinder.principal;

import org.ugr.rtpstat.client.imagenes.Imagenes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class Instrucciones extends Composite {
	public static enum Mensaje {
		USUARIO_NO_AUTORIZADO, INSTRUCCIONES_INICIALES
	};

	private static InstruccionesUiBinder uiBinder = GWT.create(InstruccionesUiBinder.class);

	interface InstruccionesUiBinder extends UiBinder<Widget, Instrucciones> {
	}
	@UiField(provided=true)
	Image flecha_izquierda;
	
	@UiField
	HTMLPanel mensajeUsuarioNoAutorizado;

	@UiField
	HTMLPanel instrucciones_iniciales;

	public Instrucciones() {
		this.flecha_izquierda = new Image(Imagenes.INSTANCE.flechaGlossGrande());
		initWidget(uiBinder.createAndBindUi(this));
	}

	public Instrucciones(Mensaje m) {
		this();
		setMensaje(m);
	}

	public void setMensaje(Mensaje m) {
		mensajeUsuarioNoAutorizado.setVisible(false);
		instrucciones_iniciales.setVisible(false);
		switch (m) {
		case USUARIO_NO_AUTORIZADO:
			mensajeUsuarioNoAutorizado.setVisible(true);
			break;
		case INSTRUCCIONES_INICIALES:
			instrucciones_iniciales.setVisible(true);
			break;
		}
	}
}
