package org.ugr.rtpstat.publico.client;

import org.ugr.rtpstat.publico.client.imagenes.Imagenes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PublicoComposite extends Composite {
	private static PublicoCompositeUiBinder uiBinder = GWT.create(PublicoCompositeUiBinder.class);

	interface PublicoCompositeUiBinder extends UiBinder<Widget, PublicoComposite> {
	}

	public PublicoComposite(String loginUrl) {
		img_cabecera = new Image(Imagenes.INSTANCE.cabecera());
		img_logo_ugr = new Image(Imagenes.INSTANCE.logo_ugr());

		/*competencias = new Image(Imagenes.INSTANCE.competencias());
		competencias_clave = new Image(Imagenes.INSTANCE.competencias_clave());
		encuesta_fisica = new Image(Imagenes.INSTANCE.encuesta_fisica());
		encuesta_web = new Image(Imagenes.INSTANCE.encuesta_web());
		valoracion_media_bloque_2 = new Image(Imagenes.INSTANCE.valoracion_media_bloque_2());
		valoraciones_bloque_1 = new Image(Imagenes.INSTANCE.valoraciones_bloque_1());
		valoraciones_bloque_2 = new Image(Imagenes.INSTANCE.valoraciones_bloque_2());*/
		
		initWidget(uiBinder.createAndBindUi(this));

		acceder.setHref(loginUrl);

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				actualizarSeccion(event.getValue());
			}
		});
		if (History.getToken().isEmpty()) {
			History.newItem("inicio");
		}
		History.fireCurrentHistoryState();
	}

	protected void actualizarSeccion(String value) {
		inicio.setVisible("inicio".equals(value));
		acerca_de.setVisible("acerca_de".equals(value));
		proyectos.setVisible("proyectos".equals(value));
		evaluacion.setVisible("evaluacion".equals(value));

	}

	@UiField
	Anchor acceder;

	@UiField
	Panel inicio;

	@UiField
	Panel acerca_de;

	@UiField
	Panel proyectos;

	@UiField
	Panel evaluacion;

	@UiField(provided = true)
	Image img_cabecera;

	@UiField(provided = true)
	Image img_logo_ugr;
/*
	@UiField(provided = true)
	Image competencias_clave;

	@UiField(provided = true)
	Image competencias;

	@UiField(provided = true)
	Image encuesta_fisica;

	@UiField(provided = true)
	Image encuesta_web;

	@UiField(provided = true)
	Image valoracion_media_bloque_2;

	@UiField(provided = true)
	Image valoraciones_bloque_1;

	@UiField(provided = true)
	Image valoraciones_bloque_2;*/

}
