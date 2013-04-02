package org.ugr.rtpstat.client.uibinder.registro;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DialogoCampoObligatorio extends DialogBox {

	private static DialogoCampoObligatorioUiBinder uiBinder = GWT.create(DialogoCampoObligatorioUiBinder.class);

	interface DialogoCampoObligatorioUiBinder extends UiBinder<Widget, DialogoCampoObligatorio> {
	}

	@UiConstructor
	public DialogoCampoObligatorio(String mensaje) {
		this.add(uiBinder.createAndBindUi(this));
		
		this.setAutoHideEnabled(true);
		this.setAnimationEnabled(true);
		this.setGlassEnabled(true);
		this.setText("Campo Obligatorio");
		
		this.mensaje.setText(mensaje);
	}
	
	@UiField
	Label mensaje;
}
