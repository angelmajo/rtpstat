package org.ugr.rtpstat.client.uibinder.principal;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MensajeNotificador extends Composite {
	private Date fecha;
	private String mensaje;

	private static MensajeNotificadorUiBinder uiBinder = GWT.create(MensajeNotificadorUiBinder.class);

	interface MensajeNotificadorUiBinder extends UiBinder<Widget, MensajeNotificador> {
	}

	@UiField
	HTML cuando;

	@UiField
	HTML panelMensaje;

	public MensajeNotificador(Date fecha, String mensaje) {
		initWidget(uiBinder.createAndBindUi(this));
		this.fecha = fecha;
		this.mensaje = mensaje;
		setTitleToDate();
		refresh();
	}

	public void refresh() {
		Date currentCal = new Date();
		long timeDiff = currentCal.getTime() - fecha.getTime();
		timeDiff /= 1000;//Segundos
		long horas = timeDiff / 3600;
		long minutos = timeDiff / 60;
		long segundos = timeDiff % 60;

		String horaString = "hora" + (horas == 1 ? "" : "s");
		String minutoString = "minuto" + (minutos == 1 ? "" : "s");
		String segundoString = "segundo" + (segundos == 1 ? "" : "s");

		String stringCuando = "Hace ";
		if (horas > 0) {
			stringCuando += horas + " " + horaString + ", ";
		}
		if (minutos > 0 || horas > 0) {
			stringCuando += minutos + " " + minutoString + " y ";
		}
		stringCuando += segundos + " " + segundoString;

		cuando.setHTML(stringCuando);
		
		panelMensaje.setHTML(mensaje);
	}

	@SuppressWarnings("deprecation")
	private void setTitleToDate() {
		setTitle(fecha.toLocaleString());
	}
}
