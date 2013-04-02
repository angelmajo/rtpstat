package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.Arrays;
import java.util.Date;

import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.Calculo;
import org.ugr.rtpstat.client.orm.Calculo.TipoCalculo;
import org.ugr.rtpstat.client.uibinder.DialogoNumeroEntero;
import org.ugr.rtpstat.client.uibinder.NumberTextBox;
import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorCalculos extends Composite implements HasValueChangeHandlers<EditorCalculos> {
	private static EditorCalculosUiBinder uiBinder = GWT.create(EditorCalculosUiBinder.class);
	private Date tiempoUltimoClickDisponibles = null;
	private int ultimoSeleccionadoDisponibles = -1;
	private Date tiempoUltimoClickAsignados = null;
	private int ultimoSeleccionadoAsignados = -1;

	private void procesarEliminacion(String itemText, String itemValue) {
		Calculo c = Calculo.valueOf(itemValue);
		if (!c.necesitaParametro()) {
			int i = 0;
			for (; i < calculos_disponibles.getItemCount(); i++) {
				if (calculos_disponibles.getValue(i).compareTo(itemValue) > 0) {
					break;
				}
			}
			calculos_disponibles.insertItem(c.toLongString() + " (X)", c.getTipo().toString(), i);
		}
		botonEliminarCalculo.setEnabled(false);
		calculos_asignados.removeItem(calculos_asignados.getSelectedIndex());
		ValueChangeEvent.fire(this, this);
	}

	private void procesarInsercion(final String itemText) {
		final Calculo c = Calculo.valueOf(itemText);
		final EditorCalculos thisRef = this;
		if (c.necesitaParametro()) {
			if (TipoCalculo.FunciónDeDistribucion.equals(c.getTipo())) {
				final DialogBox dialogo = new DialogBox(true);
				dialogo.setText("Indica el valor a aplicar");

				VerticalPanel panel = new VerticalPanel();
				final NumberTextBox box = new NumberTextBox(TipoNumero.FLOTANTE);
				box.setIgnorarPrecision(true);
				panel.add(box);
				panel.add(new Button("Aceptar", new ClickHandler() {
					public void onClick(ClickEvent event) {
						botonInsertarCalculo.setEnabled(false);
						calculos_asignados.addItem(c.toLongString() + " (" + box.getText() + ")", c.toString() + box.getText());
						ValueChangeEvent.fire(thisRef, thisRef);
						dialogo.hide();
					}
				}));

				dialogo.add(panel);
				dialogo.center();
			} else {
				DialogoNumeroEntero dialogo = new DialogoNumeroEntero("Seleccione el elemento especifico que desea: ", c.getMinParam().intValue(), c
						.getMaxParam().intValue());
				dialogo.addValueChangeHandler(new ValueChangeHandler<Integer>() {
					public void onValueChange(ValueChangeEvent<Integer> event) {
						botonInsertarCalculo.setEnabled(false);
						calculos_asignados.addItem(c.toLongString() + " (" + event.getValue() + ")", c.toString() + event.getValue());
						ValueChangeEvent.fire(thisRef, thisRef);
					}
				});
				dialogo.center();
			}
		} else {
			botonInsertarCalculo.setEnabled(false);
			calculos_disponibles.removeItem(calculos_disponibles.getSelectedIndex());
			calculos_asignados.addItem(c.toLongString() + " (X)", c.toString());
			ValueChangeEvent.fire(this, this);
		}

	}

	private void ordenarLista(ListBox lista) {
		String[] elementos = new String[lista.getItemCount()];
		for (int i = 0; i < elementos.length; i++) {
			elementos[i] = lista.getItemText(0) + ":" + lista.getValue(0);
			lista.removeItem(0);
		}
		Arrays.sort(elementos);
		for (int i = 0; i < elementos.length; i++) {
			String[] partes = elementos[i].split(":");
			lista.addItem(partes[0], partes[1]);
		}
	}

	private void setupCalculosDisponibles() {
		calculos_disponibles.clear();
		for (TipoCalculo calc : TipoCalculo.values()) {
			calculos_disponibles.addItem(Calculo.toLongString(calc) + " (X)", calc.toString());
		}
		ordenarLista(calculos_disponibles);
	}

	private void eliminarDeDisponibles(Calculo c) {
		for (int j = 0; j < calculos_disponibles.getItemCount(); j++) {
			if (c.toString().equals(calculos_disponibles.getValue(j))) {
				calculos_disponibles.removeItem(j);
				break;
			}
		}
	}

	interface EditorCalculosUiBinder extends UiBinder<Widget, EditorCalculos> {
	}

	@UiField
	ListBox calculos_disponibles;

	@UiField
	ListBox calculos_asignados;

	@UiField(provided = true)
	PushButton botonInsertarCalculo;

	@UiField(provided = true)
	PushButton botonEliminarCalculo;

	public EditorCalculos() {
		botonInsertarCalculo = new PushButton(new Image(Imagenes.INSTANCE.simpleArrowRight48()));
		botonEliminarCalculo = new PushButton(new Image(Imagenes.INSTANCE.simpleArrowLeft48()));

		initWidget(uiBinder.createAndBindUi(this));
		setupCalculosDisponibles();
	}

	@UiHandler("botonInsertarCalculo")
	public void clickEnBotonInsertar(ClickEvent event) {
		int seleccionado = calculos_disponibles.getSelectedIndex();
		procesarInsercion(calculos_disponibles.getValue(seleccionado));
	}

	@UiHandler("botonEliminarCalculo")
	public void clickEnBotonEliminar(ClickEvent event) {
		int seleccionado = calculos_asignados.getSelectedIndex();
		procesarEliminacion(calculos_asignados.getItemText(seleccionado), calculos_asignados.getValue(seleccionado));
	}

	@UiHandler("calculos_disponibles")
	public void disponiblesSelectionChange(ChangeEvent event) {
		botonInsertarCalculo.setEnabled(calculos_disponibles.getSelectedIndex() >= 0);

	}

	@UiHandler("calculos_disponibles")
	public void clickEnDisponibles(ClickEvent event) {
		Date tiempo = new Date();
		int seleccionado = calculos_disponibles.getSelectedIndex();
		if (tiempoUltimoClickDisponibles != null && (tiempo.getTime() - tiempoUltimoClickDisponibles.getTime()) < 350
				&& ultimoSeleccionadoDisponibles == seleccionado) {
			procesarInsercion(calculos_disponibles.getValue(seleccionado));
		}
		tiempoUltimoClickDisponibles = tiempo;

		ultimoSeleccionadoDisponibles = seleccionado;
	}

	@UiHandler("calculos_asignados")
	public void asignadosSelectionChange(ChangeEvent event) {
		botonEliminarCalculo.setEnabled(calculos_asignados.getSelectedIndex() >= 0);

	}

	@UiHandler("calculos_asignados")
	public void clickEnAsignados(ClickEvent event) {
		Date tiempo = new Date();
		int seleccionado = calculos_asignados.getSelectedIndex();
		if (tiempoUltimoClickAsignados != null && (tiempo.getTime() - tiempoUltimoClickAsignados.getTime()) < 350
				&& ultimoSeleccionadoAsignados == seleccionado) {
			procesarEliminacion(calculos_asignados.getItemText(seleccionado), calculos_asignados.getValue(seleccionado));
		}
		tiempoUltimoClickAsignados = tiempo;

		ultimoSeleccionadoAsignados = seleccionado;
	}

	public Calculo[] getCalculosAsignados() {
		Calculo[] out = new Calculo[calculos_asignados.getItemCount()];
		for (int i = 0; i < out.length; i++) {
			String item = calculos_asignados.getValue(i);
			out[i] = Calculo.valueOf(item);
		}
		return out;
	}

	public void setCalculos(Calculo[] calculos) {
		setupCalculosDisponibles();
		calculos_asignados.clear();
		for (int i = 0; i < calculos.length; i++) {
			Calculo c = calculos[i];

			if (!c.necesitaParametro()) {
				calculos_asignados.addItem(c.toLongString() + " (X)", c.toString());
				eliminarDeDisponibles(c);
			} else {
				if (c.getTipo() == TipoCalculo.FunciónDeDistribucion) {
					calculos_asignados.addItem(c.toLongString() + " (" + c.getParametro() + ")", c.toString());
				} else {
					calculos_asignados.addItem(c.toLongString() + " (" + c.getParametro().intValue() + ")", c.toString());
				}
			}
		}
	}

	public boolean isValid() {
		return calculos_asignados.getItemCount() > 0;
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorCalculos> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
