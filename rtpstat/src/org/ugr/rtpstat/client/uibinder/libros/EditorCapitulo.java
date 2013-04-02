package org.ugr.rtpstat.client.uibinder.libros;

import java.util.ArrayList;
import java.util.List;

import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.relaciones.ProblemaEnRelacion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditorCapitulo extends Composite implements HasValueChangeHandlers<Void> {
	private static EditorCapituloUiBinder uiBinder = GWT.create(EditorCapituloUiBinder.class);
	private ArrayList<ProblemaEnRelacion> arrayProblemasResueltos = new ArrayList<ProblemaEnRelacion>();
	private ArrayList<ProblemaEnRelacion> arrayProblemasPropuestos = new ArrayList<ProblemaEnRelacion>();
	//private NotificadorGeneral notificadorGeneral;

	interface EditorCapituloUiBinder extends UiBinder<Widget, EditorCapitulo> {
	}

	@UiField
	TextBox titulo;

	@UiField
	TextBox titulo_resueltos;

	@UiField
	TextBox titulo_propuestos;

	@UiField
	FlowPanel lista_problemas_propuestos;

	@UiField
	FlowPanel lista_problemas_resueltos;

	@UiHandler("titulo")
	public void tituloCambiado(KeyUpEvent ev) {
		validar();
		ValueChangeEvent.fire(this, null);
	}

	@UiHandler("titulo")
	public void tituloCambiado(ChangeEvent ev) {
		validar();
		ValueChangeEvent.fire(this, null);
	}

	private void validar() {
		if (getTitulo().length() == 0) {
			titulo.addStyleName("campoinvalido");
		} else {
			titulo.removeStyleName("campoinvalido");
		}
	}

	public EditorCapitulo(NotificadorGeneral notificadorGeneral, List<ResumenProblema> listadoProblemas) {
		initWidget(uiBinder.createAndBindUi(this));
		//this.notificadorGeneral = notificadorGeneral;
		for (ResumenProblema resumen : listadoProblemas) {
			ProblemaEnRelacion problema = new ProblemaEnRelacion(false, resumen, notificadorGeneral);
			arrayProblemasResueltos.add(problema);
			lista_problemas_resueltos.add(problema);

			problema = new ProblemaEnRelacion(false, resumen, notificadorGeneral);
			arrayProblemasPropuestos.add(problema);
			lista_problemas_propuestos.add(problema);
		}
		validar();
	}

	public String getTitulo() {
		return titulo.getText();
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Void> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public boolean isValid() {
		return titulo.getText().length() > 0;
	}

	public CapituloLibro getCapitulo() {
		ArrayList<ResumenProblema> resueltos = new ArrayList<ResumenProblema>();
		for (ProblemaEnRelacion p : arrayProblemasResueltos) {
			if (p.isIncluido()) {
				resueltos.add(p.getProblema());
			}
		}
		ArrayList<ResumenProblema> propuestos = new ArrayList<ResumenProblema>();
		for (ProblemaEnRelacion p : arrayProblemasPropuestos) {
			if (p.isIncluido()) {
				propuestos.add(p.getProblema());
			}
		}
		CapituloLibro capitulo = new CapituloLibro(getTitulo(), titulo_resueltos.getText(), resueltos, titulo_propuestos.getText(), propuestos);
		return capitulo;
	}

	public void setCapituloLibro(CapituloLibro capitulo) {
		titulo.setText(capitulo.getTitulo());
		titulo_propuestos.setText(capitulo.getPropuestosTitulo());
		titulo_resueltos.setText(capitulo.getResueltosTitulo());
		for (ProblemaEnRelacion problemaEnRelacion : arrayProblemasResueltos) {
			for (ResumenProblema resumen : capitulo.getResueltos()) {
				boolean incluir = problemaEnRelacion.getProblema().getId() == resumen.getId();
				if (incluir) {
					problemaEnRelacion.setIncluido(incluir);
					break;
				}
			}
		}
		for (ProblemaEnRelacion problemaEnRelacion : arrayProblemasPropuestos) {
			for (ResumenProblema resumen : capitulo.getPropuestos()) {
				boolean incluir = problemaEnRelacion.getProblema().getId() == resumen.getId();
				if (incluir) {
					problemaEnRelacion.setIncluido(problemaEnRelacion.getProblema().getId() == resumen.getId());
					break;
				}
			}
		}
		validar();
	}
}
