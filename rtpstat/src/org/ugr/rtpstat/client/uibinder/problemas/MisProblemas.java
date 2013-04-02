package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class MisProblemas extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static MisProblemasUiBinder uiBinder = GWT.create(MisProblemasUiBinder.class);

	interface MisProblemasUiBinder extends UiBinder<Widget, MisProblemas> {
	}

	@UiField
	Panel cargando;

	@UiField(provided = true)
	Image imagen_loader;

	@UiField(provided = true)
	ListadoProblemas listadoProblemas;

	public MisProblemas(final NotificadorGeneral notificadorGeneral) {
		this.listadoProblemas = new ListadoProblemas(notificadorGeneral);
		this.imagen_loader = new Image(Imagenes.INSTANCE.ajaxLoader());
		initWidget(uiBinder.createAndBindUi(this));
		listadoProblemas.setProblemas(null);
		rtpstatService.listadoProblemas(new AsyncCallback<List<ResumenProblema>>() {
			public void onSuccess(List<ResumenProblema> result) {
				listadoProblemas.setProblemas(result);
				cargando.setVisible(false);
			}

			public void onFailure(Throwable caught) {
				cargando.setVisible(false);
				notificadorGeneral.warning("Ha fallado la comunicación con el servidor");
			}
		});
	}
}
