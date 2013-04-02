package org.ugr.rtpstat.client.uibinder.relaciones;

import java.util.ArrayList;
import java.util.HashMap;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.uibinder.NumberTextBox;
import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NuevaRelacionAutomatica extends Composite implements HasValueChangeHandlers<NuevaRelacionAutomatica>, ValueChangeHandler<String>, KeyUpHandler {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private HashMap<GradoDificultad, NumberTextBox> problemasPorGrado = new HashMap<GradoDificultad, NumberTextBox>();

	private static NuevaRelacionAutomaticaUiBinder uiBinder = GWT.create(NuevaRelacionAutomaticaUiBinder.class);

	interface NuevaRelacionAutomaticaUiBinder extends UiBinder<Widget, NuevaRelacionAutomatica> {
	}

	public NuevaRelacionAutomatica(final NotificadorGeneral notificadorGeneral) {
		initWidget(uiBinder.createAndBindUi(this));
		rtpstatService.listarAreasObjetivo(new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				areas_objetivo.clear();
				areas_objetivo.addItem("¡Descarga fallida!");
				notificadorGeneral.warning("Ha fallado la comunicación con el servidor, recarge la página");
			}

			public void onSuccess(String[] result) {
				areas_objetivo.clear();
				for (String area : result) {
					areas_objetivo.addItem(area);
				}
				areas_objetivo.setSelectedIndex(0);
				areas_objetivo.setEnabled(true);
			}
		});

		for (GradoDificultad grado : GradoDificultad.values()) {
			HorizontalPanel panel = new HorizontalPanel();
			panel.setSpacing(5);
			panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			NumberTextBox cantidad = new NumberTextBox(TipoNumero.NATURAL_CON_0);

			cantidad.addValueChangeHandler(this);
			cantidad.addKeyUpHandler(this);

			panel.add(cantidad);
			panel.add(new Label(grado.comoString()));

			descripcionProblemas.add(panel);

			problemasPorGrado.put(grado, cantidad);
		}
	}

	@UiField
	ListBox areas_objetivo;

	@UiField
	VerticalPanel descripcionProblemas;

	@UiHandler( {
		"areas_objetivo"
	})
	public void descripcionCambiada(KeyUpEvent event) {
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler( {
		"areas_objetivo"
	})
	public void areasObjetivoCambiado(ChangeEvent ev) {
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler( {
		"areas_objetivo"
	})
	public void areasObjetivoCambiado(ClickEvent ev) {
		ValueChangeEvent.fire(this, this);
	}

	public boolean isValid() {
		int cuentaPositivos = 0;
		int cuentaInvalidos = 0;
		for (NumberTextBox numero : problemasPorGrado.values()) {
			if (!numero.isValid(true)) {
				cuentaInvalidos++;
			} else if (Integer.parseInt(numero.getValue()) != 0) {
				cuentaPositivos++;
			}
		}
		return cuentaPositivos > 0 && cuentaInvalidos == 0 && areas_objetivo.getSelectedIndex() != -1;
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		ValueChangeEvent.fire(this, this);
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<NuevaRelacionAutomatica> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void onKeyUp(KeyUpEvent event) {
		ValueChangeEvent.fire(this, this);
	}

	public HashMap<GradoDificultad, Integer> getProblemas() {
		HashMap<GradoDificultad, Integer> out = new HashMap<GradoDificultad, Integer>();
		for (GradoDificultad grado : problemasPorGrado.keySet()) {
			out.put(grado, problemasPorGrado.get(grado).getIntValue());
		}
		return out;
	}

	public String[] getAreasSeleccionadas() {
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0; i < areas_objetivo.getItemCount(); i++) {
			if (areas_objetivo.isItemSelected(i)) {
				out.add(areas_objetivo.getItemText(i));
			}
		}
		return out.toArray(new String[out.size()]);
	}

	@Override
	public void setVisible(boolean visible) {
		boolean eraVisible = isVisible();
		super.setVisible(visible);
		if (!eraVisible && visible) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					NumberTextBox box = problemasPorGrado.get(GradoDificultad.MuyFacil);
					box.setSelectionRange(0, box.getText().length());
					box.setFocus(true);
				}
			});
		}
	}
}
