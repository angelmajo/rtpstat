package org.ugr.rtpstat.client.uibinder.relaciones;

import java.util.ArrayList;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.ResumenRelacionEjercicios;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListaRelacionesEjercicios extends Composite {
	@UiField
	VerticalPanel panel_listado;

	@UiField
	HTMLPanel cargando_libros;

	@UiField(provided = true)
	Image imagen_loader;
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private NotificadorGeneral notificador;

	private void actualizarListado() {
		cargando_libros.setVisible(true);
		panel_listado.clear();
		rtpstatService.listarRelacionesEjercicios(new AsyncCallback<ArrayList<ResumenRelacionEjercicios>>() {

			public void onSuccess(ArrayList<ResumenRelacionEjercicios> result) {
				cargando_libros.setVisible(false);
				for (ResumenRelacionEjercicios rel : result) {
					panel_listado.add(new RelacionEnListado(rel, notificador));
				}
			}

			public void onFailure(Throwable caught) {
				cargando_libros.setVisible(false);
				notificador.warning("Ha ocurrido un error al cargar el listado: " + caught.getLocalizedMessage());
			}
		});
	}

	private static ListaRelacionesEjerciciosUiBinder uiBinder = GWT.create(ListaRelacionesEjerciciosUiBinder.class);

	interface ListaRelacionesEjerciciosUiBinder extends UiBinder<Widget, ListaRelacionesEjercicios> {
	}

	public ListaRelacionesEjercicios(NotificadorGeneral notificador) {
		this.notificador = notificador;
		this.imagen_loader = new Image(Imagenes.INSTANCE.ajaxLoader());
		initWidget(uiBinder.createAndBindUi(this));
		actualizarListado();
	}
}
