package org.ugr.rtpstat.client.uibinder;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.TextBox;

public class AdvancedTextBox extends TextBox {
	private boolean vacio;
	private String mensaje;
	private String mensajeStyle;

	@UiConstructor
	public AdvancedTextBox(String mensaje, String mensajeStyle) {
		this.mensaje = mensaje;
		this.mensajeStyle = mensajeStyle;
		this.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				focoObtenido();
			}
		});
		this.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				cambioTexto();
			}

		});
		this.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				focoPerdido();
			}
		});
		focoPerdido();
	}

	@Override
	public void setText(String text) {
		if (text != null && text.length() > 0) {
			super.setText(text);
			vacio = false;
			removeStyleName(mensajeStyle);
		} else {
			super.setText(mensaje);
			vacio = true;
			addStyleName(mensajeStyle);
		}
	}

	@Override
	public String getText() {
		return vacio ? "" : super.getText();
	}

	private void cambioTexto() {
		vacio = super.getText().length() == 0 ? true : false;
	}

	protected void focoPerdido() {
		if ("".equals(super.getText()) || vacio) {
			vacio = true;
			super.setText(mensaje);
			addStyleName(mensajeStyle);
		} else {
			vacio = false;
		}
	}

	protected void focoObtenido() {
		if (vacio) {
			removeStyleName(mensajeStyle);
			super.setText("");
		}
	}
}
