package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.ArrayList;
import java.util.Map;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DialogoEliminarProblema extends DialogBox {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private NotificadorGeneral notificadorGeneral;
	private ProblemaEnListado padre;
	private static DialogoEliminarProblemaUiBinder uiBinder = GWT.create(DialogoEliminarProblemaUiBinder.class);

	public static enum TipoRelacionado {
		RELACION, LIBRO
	};

	interface DialogoEliminarProblemaUiBinder extends UiBinder<Widget, DialogoEliminarProblema> {
	}

	public DialogoEliminarProblema(ProblemaEnListado padre, NotificadorGeneral notificadorGeneral2) {
		super(true);
		this.padre = padre;
		this.notificadorGeneral = notificadorGeneral2;
		this.ajax_loader = new Image(Imagenes.INSTANCE.ajaxLoader());
		setWidget(uiBinder.createAndBindUi(this));

		setGlassEnabled(true);
		setAnimationEnabled(true);
		setText("Eliminar problema");

		rtpstatService.getRelacionesLibrosRelacionados(padre.getProblema().getId(), new AsyncCallback<Map<TipoRelacionado, ArrayList<String>>>() {
			@Override
			public void onSuccess(Map<TipoRelacionado, ArrayList<String>> result) {
				panel_cargando.setVisible(false);
				panel_resto.setVisible(true);
				ArrayList<String> libros = result.get(TipoRelacionado.LIBRO);
				int librosSize = libros == null ? 0 : libros.size();
				if (librosSize > 0) {
					for (String libro : libros) {
						nombres_libros.addItem(libro);
					}
					panel_libros.setVisible(true);
				}

				ArrayList<String> relaciones = result.get(TipoRelacionado.RELACION);
				int relacionesSize = relaciones == null ? 0 : relaciones.size();
				if (relacionesSize > 0) {
					for (String relacion : relaciones) {
						nombres_relaciones.addItem(relacion);
					}
					panel_relaciones.setVisible(true);
				}
				if (librosSize > 0 || relacionesSize > 0) {
					String html = "El problema está referenciado en ";
					if(librosSize == 0) {
						html+=" las relaciones indicadas.";
					} else if(relacionesSize == 0) {
						html+=" los libros indicados.";
					} else {
						html+=" las relaciones y los libros indicados.";
					}
					html+=" Al borrar el problema se eliminaran todas estas referencias.";
					mensaje.setHTML(html);
				} else {
					mensaje.setHTML("Una vez eliminado, el problema no se podra recuperar.");
				}
				center();
			}

			@Override
			public void onFailure(Throwable caught) {
				hide();
				notificadorGeneral.warning("Ha ocurrido un error de comunicación con el servidor");
			}
		});
	}
	
	@UiHandler("cancelar")
	public void cancelar(ClickEvent c) {
		hide();
	}
	
	@UiHandler("confirmar")
	public void confirmar(ClickEvent c) {
		hide();
		notificadorGeneral.info("Eliminando el problema");
		rtpstatService.rmProblema(padre.getProblema().getId(), new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				padre.borrarDelListado();
				notificadorGeneral.info("Problema eliminado");
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Ha ocurrido un error: " + caught.getMessage());
			}
		});
	}

	@UiField(provided = true)
	Image ajax_loader;

	@UiField
	Panel panel_cargando;

	@UiField
	Panel panel_resto;

	@UiField
	Panel panel_relaciones;

	@UiField
	Panel panel_libros;

	@UiField
	ListBox nombres_libros;

	@UiField
	ListBox nombres_relaciones;

	@UiField
	HTML mensaje;
}
