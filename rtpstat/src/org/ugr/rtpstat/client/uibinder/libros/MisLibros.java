package org.ugr.rtpstat.client.uibinder.libros;

import java.util.ArrayList;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class MisLibros extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static MisLibrosUiBinder uiBinder = GWT.create(MisLibrosUiBinder.class);

	interface MisLibrosUiBinder extends UiBinder<Widget, MisLibros> {
	}
	
	@UiField
	FlowPanel panel_lista;

	@UiField
	HTMLPanel cargando_libros;

	@UiField(provided = true)
	Image imagen_loader;

	private NotificadorGeneral notificadorGeneral;

	public MisLibros(NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.imagen_loader = new Image(Imagenes.INSTANCE.ajaxLoader());
		initWidget(uiBinder.createAndBindUi(this));
		cargarListaLibros();
	}

	private void cargarListaLibros() {
		panel_lista.clear();
		cargando_libros.setVisible(true);
		rtpstatService.listarLibros(new AsyncCallback<ArrayList<Libro>>() {
			@Override
			public void onSuccess(ArrayList<Libro> result) {
				cargando_libros.setVisible(false);
				for(Libro libro: result) {
					panel_lista.add(new LibroEnLista(libro, notificadorGeneral));
				}				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				cargando_libros.setVisible(false);
				notificadorGeneral.warning("Ha ocurrido un error al intentar descargar la lista de libros");
			}
		});
	}

}
