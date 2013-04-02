package org.ugr.rtpstat.client.uibinder;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.TextBox;

public class NumberTextBox extends TextBox {
	protected static final String MSG_SOLO_ENTEROS = "Solo se aceptan números enteros";
	protected static final String MSG_SOLO_NATURALES = "Solo se aceptan números naturales positivos";
	protected static final String MSG_SOLO_NUMEROS = "Solo se aceptan números (el separador de decimales es el punto (.))";
	private static final String MSG_SOLO_NATURALES_CON_0 = "Solo se aceptan números naturales";

	public static enum TipoNumero {
		FLOTANTE, NATURAL, ENTERO, NATURAL_CON_0
	}

	public NumberTextBox(TipoNumero tipo) {
		super();
		this.addStyleName("align_right");

		this.setTipoNumero(tipo);
		addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				validar();
			}
		});
		addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				validar();
				aplicarPrecision();
			}
		});
	}

	public NumberTextBox() {
		this(TipoNumero.FLOTANTE);
	}

	public boolean isValid(boolean ignorarOverride) {
		boolean out = this.valid;
		if (!ignorarOverride && mensajeInvalido != null) {
			out = false;
		}
		return out;

	}

	public void setTipoNumero(TipoNumero tipoNumero) {
		this.tipoNumero = tipoNumero;
		if (tipoNumero.equals(TipoNumero.NATURAL)) {
			this.setValue("1");
		} else {
			this.setValue("0");
		}
		validar();
		aplicarPrecision();
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		if (tipoNumero != TipoNumero.FLOTANTE) {
			throw new IllegalStateException("Solo los NumberTextBox cuyo tipo == TipoNumero.FLOTANTE pueden cambiar su precisión.");
		}
		if (precision < 0) {
			throw new IllegalArgumentException("La precision tiene que ser un número positivo");
		}
		this.precision = precision;
		if (tipoNumero == TipoNumero.FLOTANTE && isValid(true)) {
			aplicarPrecision();
		}
	}

	protected void aplicarPrecision() {
		if (isValid(true) && tipoNumero == TipoNumero.FLOTANTE && !ignorarPrecision) {
			String texto = getText();
			String[] partes = texto.split("\\.");
			if (partes.length == 1) { // No tiene parte decimal
				if (precision > 0) {
					texto += ".";
					for (int i = 0; i < precision; i++) {
						texto += "0";
					}
				}
			} else if (partes.length == 2) { // Tiene parte decimal
				int numDecActual = partes[1].length();
				if (numDecActual < precision) { // Tenemos que añadir decimales
					for (int i = numDecActual; i < precision; i++) {
						partes[1] += "0";
					}
				} else if (numDecActual > precision) { // Tenemos que quitar
					// decimales
					partes[1] = partes[1].substring(0, precision);
				}

				texto = partes[0];

				// Eliminamos ceros a la izquierda
				while (texto.charAt(0) == '0' && texto.length() > 1) {
					texto = texto.substring(1);
				}

				if (partes[1].length() > 0) {
					texto += "." + partes[1];
				}

			}
			setText(texto);
		}
	}

	public TipoNumero getTipoNumero() {
		return tipoNumero;
	}

	@Override
	public void setValue(String value, boolean fireEvents) {
		super.setValue(value, fireEvents);
		validar();
	}

	// Implementación privada
	private TipoNumero tipoNumero;
	private int precision;// 0
	private boolean valid;// false
	private String mensajeInvalido;
	private boolean ignorarPrecision;

	private void setValid(boolean valid) {
		this.valid = valid;
		if (valid) {
			this.removeStyleName("campoinvalido");
		} else {
			this.addStyleName("campoinvalido");
		}
	}

	protected void validar() {
		boolean esNumero = true;
		setTitle("");
		setValid(true);
		float valor = 0;
		try {
			valor = Float.parseFloat(this.getText());
		} catch (NumberFormatException ex) {
			esNumero = false;
		}
		if (esNumero) {
			boolean tieneDecimanles = this.getText().indexOf(".") != -1;
			if (getTipoNumero() == TipoNumero.NATURAL_CON_0 && (tieneDecimanles || valor < 0)) {
				setTitle(MSG_SOLO_NATURALES_CON_0);
				setValid(false);
			}
			if (getTipoNumero() == TipoNumero.NATURAL && (tieneDecimanles || valor <= 0)) {
				setTitle(MSG_SOLO_NATURALES);
				setValid(false);
			}
			if (getTipoNumero() == TipoNumero.ENTERO && tieneDecimanles) {
				setTitle(MSG_SOLO_ENTEROS);
				setValid(false);
			}
		} else {
			setTitle(MSG_SOLO_NUMEROS);
			setValid(false);
		}
	}

	public void setInvalido(String mensaje) {
		mensajeInvalido = mensaje;
		if (mensaje != null) {
			setTitle(mensajeInvalido);
			this.addStyleName("campoinvalido");
		} else {
			validar();
		}

	}

	public Integer getIntValue() {
		return Integer.parseInt(getValue());
	}

	public void setIgnorarPrecision(boolean ignorarPrecision) {
		 this.ignorarPrecision = ignorarPrecision;
	}
}
