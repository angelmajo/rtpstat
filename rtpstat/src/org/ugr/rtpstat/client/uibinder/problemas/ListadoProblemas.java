package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.ArrayList;
import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.events.EventBus;
import org.ugr.rtpstat.client.events.ListaProblemaCambiadaEvent;
import org.ugr.rtpstat.client.events.ListaProblemaCambiadaHandler;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListadoProblemas extends Composite implements ListaProblemaCambiadaHandler {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static ListadoProblemasUiBinder uiBinder = GWT.create(ListadoProblemasUiBinder.class);

	interface ListadoProblemasUiBinder extends UiBinder<Widget, ListadoProblemas> {
	}

	private List<ResumenProblema> problemas = new ArrayList<ResumenProblema>();

	@UiField
	VerticalPanel panelProblemas;

	@UiField
	HTMLPanel mensajeListaVacia;

	private NotificadorGeneral notificadorGeneral;

	public ListadoProblemas(NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		initWidget(uiBinder.createAndBindUi(this));
		EventBus.getInstance().addHandler(ListaProblemaCambiadaEvent.TYPE, this);
	}

	public void setProblemas(List<ResumenProblema> problemas) {
		this.problemas = problemas;
		actualizarListado();
	}

	private void actualizarListado() {
		panelProblemas.clear();
		if (problemas != null) {
			if (problemas.size() == 0) {
				// mensajeListaVacia.setVisible(true);
			} else {
				// mensajeListaVacia.setVisible(false);
				for (ResumenProblema p : problemas) {
					panelProblemas.add(new ProblemaEnListado(p, notificadorGeneral));
				}
			}
		}
	}

	public List<ResumenProblema> getProblemas() {
		return problemas;
	}

	public void actualizarListaProblemas() {
		setProblemas(null);
		rtpstatService.listadoProblemas(new AsyncCallback<List<ResumenProblema>>() {
			public void onSuccess(List<ResumenProblema> result) {
				setProblemas(result);
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Falló la descarga, vuelva a intentarlo : " + caught.getMessage());
			}
		});
	}

}
