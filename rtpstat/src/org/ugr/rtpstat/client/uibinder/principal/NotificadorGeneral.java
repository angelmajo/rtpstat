package org.ugr.rtpstat.client.uibinder.principal;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.ugr.rtpstat.client.animations.FadeAnimation;
import org.ugr.rtpstat.client.imagenes.Imagenes;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotificadorGeneral extends Composite implements MouseOverHandler, HasMouseOverHandlers {
	public static enum Level {
		WARNING, INFO
	};

	private static NotificadorGeneralUiBinder uiBinder = GWT.create(NotificadorGeneralUiBinder.class);

	private static enum EstadoVentana {
		MINIMIZADA, CONTRAIDA, MAXIMIZADA
	}

	private FadeAnimation animation = new FadeAnimation(this);
	private EstadoVentana estadoVentana;
	@UiField
	Estilo style;

	@UiField
	VerticalPanel panelExterno;

	@UiField
	VerticalPanel panel;

	@UiField
	HTML ultimoMensaje;

	@UiField(provided = true)
	Image minimizar;

	@UiField(provided = true)
	Image restaurar;

	@UiField(provided = true)
	Image expandir;

	//private FadeAnimation animation;

	interface NotificadorGeneralUiBinder extends UiBinder<Widget, NotificadorGeneral> {
	}

	interface Estilo extends CssResource {
		String warning();

		String info();

		String blinking();
	}

	public NotificadorGeneral() {
		this.addMouseOverHandler(this);
		minimizar = new Image(Imagenes.INSTANCE.minimizar());
		restaurar = new Image(Imagenes.INSTANCE.restaurar());
		expandir = new Image(Imagenes.INSTANCE.maximizar());

		initWidget(uiBinder.createAndBindUi(this));
		//animation = new FadeAnimation(this);
		minimizar(null);
	}

	@UiHandler("minimizar")
	public void minimizar(ClickEvent event) {
		estadoVentana = EstadoVentana.MINIMIZADA;
		minimizar.setVisible(false);
		restaurar.setVisible(true);
		expandir.setVisible(true);

		ultimoMensaje.setVisible(false);
		panel.setVisible(false);
	}

	@UiHandler("expandir")
	public void iniciarTimer(ClickEvent event) {
		actualizarTiempoMensajesTimer.scheduleRepeating(1000);
	}

	@UiHandler( {
		"restaurar"
	})
	public void restaurar(ClickEvent event) {
		estadoVentana = EstadoVentana.CONTRAIDA;
		minimizar.setVisible(true);
		restaurar.setVisible(false);
		expandir.setVisible(true);

		ultimoMensaje.setVisible(true);
		panel.setVisible(false);
		actualizarTiempoMensajesTimer.cancel();
	}

	@UiHandler("expandir")
	public void expandir(ClickEvent event) {
		estadoVentana = EstadoVentana.MAXIMIZADA;
		minimizar.setVisible(true);
		restaurar.setVisible(true);
		expandir.setVisible(false);

		ultimoMensaje.setVisible(false);
		panel.setVisible(true);
	}

	public void warning(String mensaje) {
		this.log(Level.WARNING, mensaje);
	}

	public void info(String mensaje) {
		this.log(Level.INFO, mensaje);
	}

	private Queue<MensajeNotificador> mensajes = new LinkedList<MensajeNotificador>();

	private Timer actualizarTiempoMensajesTimer = new Timer() {
		@Override
		public void run() {
			for (MensajeNotificador m : mensajes) {
				m.refresh();
			}
		}
	};

	public void log(Level level, String mensaje) {
		minimizarTimer.cancel();
		ultimoMensaje.removeStyleName(style.info());
		ultimoMensaje.removeStyleName(style.warning());

		//HTML html = new HTML(calendar.getDisplayName(Date.DAY_OF_WEEK, Date.SHORT, Locale.getDefault())+ ":" + mensaje);
		Date fecha = new Date();
		MensajeNotificador m = new MensajeNotificador(fecha, mensaje);
		switch (level) {
			case WARNING:
				m.addStyleName(style.warning());
				ultimoMensaje.addStyleName(style.warning());
				break;
			case INFO:
				m.addStyleName(style.info());
				ultimoMensaje.addStyleName(style.info());
				break;
		}
		mensajes.add(m);
		panel.insert(m, 0);
		ultimoMensaje.setHTML(mensaje);
		ultimoMensaje.setTitle(fechaToLocale(fecha));

		if (estadoVentana == EstadoVentana.MINIMIZADA) {
			restaurar(null);
		}
		this.setVisible(true);
		if (mensajes.size() == 1) {
			animation.fadeIn(2000);
		}
		minimizarTimer.schedule(5000);
		blinkAnimation.run(3000);
	}

	private Animation blinkAnimation = new Animation() {
		@Override
		protected void onUpdate(double progress) {
			int comoEntero = (int) (progress * 6);
			if (comoEntero % 2 == 0) {
				addStyleName(style.blinking());
			} else {
				removeStyleName(style.blinking());
			}
			if (progress == 1) {
				removeStyleName(style.blinking());
			}
		}
	};

	private Timer minimizarTimer = new Timer() {
		@Override
		public void run() {
			minimizar(null);
		}
	};

	@SuppressWarnings("deprecation")
	private String fechaToLocale(Date fecha) {
		return fecha.toLocaleString();
	}

	public void onMouseOver(MouseOverEvent event) {
		minimizarTimer.cancel();
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}
}
