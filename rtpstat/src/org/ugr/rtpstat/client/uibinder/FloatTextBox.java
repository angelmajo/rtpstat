package org.ugr.rtpstat.client.uibinder;

import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public class FloatTextBox extends Composite implements HasValueChangeHandlers<FloatTextBox> {
	interface Styles extends CssResource {
		String campoerroneo();
	}

	private static FloatTextBoxUiBinder uiBinder = GWT.create(FloatTextBoxUiBinder.class);

	private FloatTextBoxHandler handler;

	interface FloatTextBoxUiBinder extends UiBinder<Widget, FloatTextBox> {
	}

	// @UiField
	// Styles style;

	@UiField
	HorizontalPanel panel;

	@UiField
	NumberTextBox text_box;

	@UiField
	MenuBar menu;

	@UiField
	MenuItem insertar_uno;

	@UiField
	MenuItem insertar_varios;

	@UiField
	MenuItem eliminar;

	@UiField
	MenuItem menu_eliminar;

	@UiField
	MenuItem eliminar_todas;

	@UiField
	Label posicion;

	@UiHandler("text_box")
	public void textBoxChanged(ValueChangeEvent<String> event) {
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("text_box")
	public void textBoxChanged(KeyUpEvent event) {
		ValueChangeEvent.fire(this, this);
	}

	public FloatTextBox(int precision, final FloatTextBoxHandler handler) {
		initWidget(uiBinder.createAndBindUi(this));
		this.text_box.setPrecision(precision);
		this.handler = handler;

		insertar_uno.setScheduledCommand(new Command() {
			public void execute() {
				insertarUno();
			}
		});

		insertar_varios.setScheduledCommand(new Command() {
			public void execute() {
				insertarVarios();
			}
		});

		eliminar.setScheduledCommand(new Command() {
			public void execute() {
				eliminar();
			}
		});

		eliminar_todas.setScheduledCommand(new Command() {
			public void execute() {
				eliminar_todas();
			}
		});
	}

	public FloatTextBox() {
		this(0, null, false, false);
	}

	public FloatTextBox(boolean hasPosicion, boolean hasMenu) {
		this(0, null, hasPosicion, hasMenu);
	}

	public FloatTextBox(int precision, FloatTextBoxHandler handler, boolean hasPosicion, boolean hasMenu) {
		this(precision, handler);
		this.setHasPosicion(hasPosicion);
		this.setHasMenu(hasMenu);
	}

	protected void eliminar_todas() {
		handler.eliminarTodos(true);
	}

	public void setPosicion(int posicion) {
		this.posicion.setText(posicion + "");
	}

	public int getPosicion() {
		return Integer.parseInt(this.posicion.getText());
	}

	protected void eliminar() {
		handler.eliminar(this);
		handler.validar();
	}

	protected void insertarVarios() {
		handler.insertarVarios(this);
	}

	protected void insertarUno() {
		handler.insertarUno(this);
		handler.validar();
	}

	public void setEliminarEnabled(boolean b) {
		menu_eliminar.setVisible(b);
	}

	public void setPrecision(int precision) {
		text_box.setPrecision(precision);
	}

	public void setValue(Float value) {
		if (getTipoNumero() == TipoNumero.FLOTANTE) {
			text_box.setText("" + value);
		} else {
			text_box.setText("" + value.intValue());
		}
		text_box.validar();
		text_box.aplicarPrecision();
	}

	/**
	 * Establece este textbox como invalido manualmente (override)
	 * 
	 * @param razon
	 *            La razon por la que este textbox es invalido, o null para
	 *            eliminar el override
	 */

	public Float getValue() {
		return Float.parseFloat(text_box.getValue());
	}

	public boolean isValid() {
		return this.isValid(false);
	}

	public boolean isValid(boolean ignorarOverride) {
		return text_box.isValid(ignorarOverride);
	}

	public void setHasMenu(boolean hasMenu) {
		menu.setVisible(hasMenu);
	}

	public boolean hasMenu() {
		return menu.isVisible();
	}

	public void setHasPosicion(boolean hasPosicion) {
		posicion.setVisible(hasPosicion);
	}

	public boolean hasPosicion() {
		return posicion.isVisible();
	}

	public void setFocus(boolean focused) {
		text_box.setFocus(focused);
	}

	public void setTipoNumero(TipoNumero tipo) {
		text_box.setTipoNumero(tipo);
	}

	public TipoNumero getTipoNumero() {
		return text_box.getTipoNumero();
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FloatTextBox> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void setInvalido(String mensaje) {
		text_box.setInvalido(mensaje);
		// text_box.validar();
	}

	public void setEditable(boolean editable) {
		text_box.setEnabled(editable);
	}

}
