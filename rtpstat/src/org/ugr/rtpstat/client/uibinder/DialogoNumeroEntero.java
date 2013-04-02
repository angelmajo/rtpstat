package org.ugr.rtpstat.client.uibinder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class DialogoNumeroEntero extends DialogBox implements HasValue<Integer> {

	private static DialogoNumeroEnteroUiBinder uiBinder = GWT
			.create(DialogoNumeroEnteroUiBinder.class);

	interface DialogoNumeroEnteroUiBinder extends
			UiBinder<Widget, DialogoNumeroEntero> {
	}

	@UiField
	ListBox valores;

	@UiHandler("aceptar")
	public void doAceptarClickHandler(ClickEvent event) {
		this.hide();
		ValueChangeEvent.fire(this, getValue()); 
	}
	@UiHandler("cancelar")
	public void doCancelarClickHandler(ClickEvent event) {
		this.hide(); 
	}

	private int max;

	private int min;

	public DialogoNumeroEntero(String titulo, int min, int max
			) {
		this.setWidget(uiBinder.createAndBindUi(this));
		//this.addValueChangeHandler(handler);
		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.setModal(true);
		this.setText(titulo);
		
		// CheckBox
		this.min = min;
		this.max = max;
		if (min > max) {
			throw new IllegalArgumentException(
					"Min tiene que ser menor o igual que max");
		}

		while (min <= max) {
			valores.addItem("" + min);
			min++;
		}
		valores.setSelectedIndex(0);
	}

	public Integer getValue() {
		return Integer
				.parseInt(valores.getItemText(valores.getSelectedIndex()));
	}

	public void setValue(Integer value) {
		if (value < min || value > max) {
			throw new IllegalArgumentException("Valor fuera de rango");
		}
		valores.setSelectedIndex(value - min);
	}

	public void setValue(Integer value, boolean fireEvents) {
		setValue(value);
		ValueChangeEvent.fire(this, getValue()); 
	}

	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Integer> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
