package org.ugr.rtpstat.client.uibinder.principal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BotonNavegacion extends Composite implements ValueChangeHandler<String> {

	interface MiCss extends CssResource {
		String activo();
	}
	
	private static BotonNavegacionUiBinder uiBinder = GWT.create(BotonNavegacionUiBinder.class);

	interface BotonNavegacionUiBinder extends UiBinder<Widget, BotonNavegacion> {
	}

	private String token;
	private String textoEnlace;
	
	private void setTextoEnlace(String textoEnlace) {
		if (textoEnlace == null || textoEnlace.length() == 0) {
			throw new IllegalArgumentException("El texto del boton no puede ser null");
		}
		this.textoEnlace = textoEnlace;
		
		enlace.setText(this.textoEnlace);
		etiqueta.setText(this.textoEnlace);
		
		setEnabled(!this.token.equals(History.getToken()));
	}
	
	protected void setEnabled(boolean enabled) {
		enlace.setVisible(enabled);
		etiqueta.setVisible(!enabled);
		if(!enabled) {
			panel.addStyleName(style.activo());
		} else {
			panel.removeStyleName(style.activo());
		}
	}
	@UiField
	MiCss style;
	
	@UiField
	HTMLPanel panel;
	
	@UiField
	Hyperlink enlace;
	
	@UiField
	Label etiqueta;

	@UiConstructor
	public BotonNavegacion(String token,String textoEnlace) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setToken(token);
		setTextoEnlace(textoEnlace);
		
		History.addValueChangeHandler(this);
	}

	

	private void setToken(String token) {
		if (token == null || token.length() == 0) {
			throw new IllegalArgumentException("No se puede crear un boton de navegación sin un token asociado");
		}
		
		this.token = token;
		enlace.setTargetHistoryToken(token);
	}

	/**
	 * Nos permite activar o desactivar el boton dependiendo de el token del
	 * historial activo
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		setEnabled(!this.token.equals(event.getValue()));
	}

	

}
