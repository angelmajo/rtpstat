package org.ugr.rtpstat.client.uibinder;

import java.util.ArrayList;

import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class TablaDinamica extends Composite implements FloatTextBoxHandler, HasValueChangeHandlers<FloatTextBox> {
	interface Estilo extends CssResource {
		String correccionPosicion();

		String cabeceraTabla();
	}

	private ArrayList<TablaDinamicaValidator> validators = new ArrayList<TablaDinamicaValidator>();

	private static TablaDinamicaUiBinder uiBinder = GWT.create(TablaDinamicaUiBinder.class);

	interface TablaDinamicaUiBinder extends UiBinder<Widget, TablaDinamica> {
	}

	public final class Columna {
		private String nombre;
		private TipoNumero tipo;

		public Columna(String nombre, TipoNumero tipo) {
			super();
			if (nombre == null) {
				throw new NullPointerException("el nombre de la columna no puede ser null");
			}
			if (nombre.length() == 0) {
				throw new IllegalArgumentException("Las columnas tienen que tener un nombre");
			}
			this.nombre = nombre;
			this.tipo = tipo;
		}

		public String getNombre() {
			return nombre;
		}

		public TipoNumero getTipo() {
			return tipo;
		}
	}

	private int minRows;

	public int getMinRows() {
		return minRows;
	}

	public void setMinRows(int minRows) {
		this.minRows = minRows;
	}

	public Columna getNuevaColumna(String nombre, TipoNumero tipo) {
		return new Columna(nombre, tipo);
	}

	public TablaDinamica() {
		this(1);
	}

	public TablaDinamica(int minRows) {
		this.minRows = minRows;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setColumnas(Columna[] columnas) {
		if (this.columnas != null) {
			throw new IllegalStateException("¡Las columnas solo se pueden inicializar una vez!");
		}
		if (columnas == null) {
			throw new NullPointerException("las columnas no pueden ser null");
		}
		if (columnas.length == 0) {
			throw new IllegalArgumentException("Debe pasar alguna columna");
		}
		this.columnasEditables = new boolean[columnas.length];
		this.ultimoEditable = new boolean[columnas.length];
		for (int i = 0; i < columnas.length; i++) {
			columnasEditables[i] = true;
			ultimoEditable[i] = true;
		}
		this.columnas = columnas;
		eliminarTodos(false);
	}

	public Columna[] getColumnas() {
		return columnas;
	}

	// FloatTextBoxHandler
	public void eliminarTodos(boolean pedirConfirmacion) {
		boolean limpiar = true;
		if (pedirConfirmacion) {
			limpiar = Window.confirm("¿Esta seguro de que quiere eliminar todos los datos de la tabla?");
		}
		if (limpiar) {
			grid.resize(1, columnas.length);
			for (int i = 0; i < columnas.length; i++) {
				grid.setWidget(0, i, new Label(columnas[i].getNombre()));
				grid.getCellFormatter().setAlignment(0, i, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);

				if (i == 0) {
					grid.getWidget(0, 0).addStyleName(style.correccionPosicion());
				} else {
					grid.getWidget(0, i).addStyleName(style.cabeceraTabla());
				}
			}
			for (int i = 0; i < minRows; i++) {
				insertarUno(null);
			}
			validar();
		}
	}

	public void eliminar(FloatTextBox box) {
		int pos = box.getPosicion();
		grid.removeRow(pos);
		for (; pos < grid.getRowCount(); pos++) {
			for (int i = 0; i < columnas.length; i++) {
				((FloatTextBox) grid.getWidget(pos, i)).setPosicion(pos);
			}
		}
		boolean eliminarEnabled = grid.getRowCount() > getMinRows() + 1;// El
		// uno
		// es
		// por
		// la
		// fila
		// del
		// titulo
		for (int i = 1; i < grid.getRowCount(); i++) {
			((FloatTextBox) grid.getWidget(i, grid.getColumnCount() - 1)).setEliminarEnabled(eliminarEnabled);
		}
	}

	public void insertarUno(FloatTextBox despuesDe) {
		int pos = 0;
		if (despuesDe != null) {
			pos = despuesDe.getPosicion();
		}
		pos = grid.insertRow(pos + 1);
		for (int i = 0; i < columnas.length; i++) {
			FloatTextBox box = new FloatTextBox(precision, this, i == 0, i == columnas.length - 1);

			box.setEditable(columnasEditables[i]);

			box.addValueChangeHandler(new ValueChangeHandler<FloatTextBox>() {
				public void onValueChange(ValueChangeEvent<FloatTextBox> event) {
					fireValueChange(event.getValue());
				}
			});
			box.setPosicion(pos);
			box.setTipoNumero(columnas[i].getTipo());
			grid.setWidget(pos, i, box);
		}
		actualizarControles();
	}

	protected void actualizarControles() {
		for (int fila = 1; fila < grid.getRowCount(); fila++) {
			for (int columna = 0; columna < columnas.length; columna++) {
				FloatTextBox box = (FloatTextBox) grid.getWidget(fila, columna);
				box.setPosicion(fila);
				box.setEditable(columnasEditables[columna]);
				if (fila == grid.getRowCount() - 1 && ultimoEditable[columna]) {
					box.setEditable(true);
				}
			}
		}
		boolean eliminarEnabled = grid.getRowCount() > getMinRows() + 1;
		for (int i = 1; i < grid.getRowCount(); i++) {
			((FloatTextBox) grid.getWidget(i, grid.getColumnCount() - 1)).setEliminarEnabled(eliminarEnabled);
		}
	}

	protected void fireValueChange(FloatTextBox floatTextBox) {
		validar();
		ValueChangeEvent.fire(this, floatTextBox);
	}

	public void insertarVarios(final FloatTextBox floatTextBox) {
		DialogoNumeroEntero dialogo = new DialogoNumeroEntero("¿Cuantos desea insertar?", 1, 100);
		dialogo.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			public void onValueChange(ValueChangeEvent<Integer> event) {
				for (int i = 0; i < event.getValue(); i++) {
					insertarUno(floatTextBox);
				}
				validar();
			}
		});
		dialogo.center();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible  && grid.getRowCount() > 0) {
			((FloatTextBox) grid.getWidget(1, 0)).setFocus(true);
		}
	}

	// Fields
	@UiField
	Grid grid;

	@UiField
	Estilo style;

	// Partes privadas
	private Columna[] columnas = null;

	private int precision;

	private boolean valid;

	private boolean[] columnasEditables;

	private boolean[] ultimoEditable;

	public void setPrecision(int precision) {
		this.precision = precision;
		for (int fila = 1; fila < grid.getRowCount(); fila++) {
			for (int col = 0; col < grid.getColumnCount(); col++) {
				FloatTextBox box = ((FloatTextBox) grid.getWidget(fila, col));
				if (box.getTipoNumero() == TipoNumero.FLOTANTE) {
					box.setPrecision(precision);
				}
			}
		}
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FloatTextBox> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void validar() {
		valid = true;
		for (int fila = 1; fila < grid.getRowCount(); fila++) {
			for (int col = 0; col < grid.getColumnCount(); col++) {
				FloatTextBox box = ((FloatTextBox) grid.getWidget(fila, col));
				valid = box.isValid(true);
				if (!valid) {
					break;
				}
			}
			if (!valid) {
				break;
			}
		}
		if (valid) {
			for (TablaDinamicaValidator validator : validators) {
				valid = validator.isValid(this);
				if (!valid) {
					break;
				}
			}
		}

	}

	public boolean isValid() {
		validar();
		return valid;
	}

	public int getPrecision() {
		return precision;
	}

	public int getNumeroRegistros() {
		return grid.getRowCount() - 1;
	}

	public float getValue(int fila, int columna) {
		FloatTextBox box = ((FloatTextBox) grid.getWidget(fila + 1, columna));
		return box.getValue();
	}

	public void setValue(int fila, int columna, Float valor) {
		FloatTextBox box = ((FloatTextBox) grid.getWidget(fila + 1, columna));
		box.setValue(valor);
	}

	public void addValidator(TablaDinamicaValidator validator) {
		if (!validators.contains(validator)) {
			validators.add(validator);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Number> ArrayList<T> getValores(Class<T> class1, int columna) {
		ArrayList<T> out = new ArrayList<T>();
		for (int fila = 1; fila <= getNumeroRegistros(); fila++) {
			FloatTextBox box = ((FloatTextBox) grid.getWidget(fila, columna));
			out.add((T) box.getValue());
		}
		return out;
	}

	public void setValores(float[] valores, int columna) {
		for (int i = getNumeroRegistros(); i < valores.length; i++) {
			insertarUno(null);
		}
		for (int i = 0; i < valores.length; i++) {
			setValue(i, columna, valores[i]);
		}
		validar();
	}

	public void setInvalido(int fila, int columna, String mensaje) {
		((FloatTextBox) grid.getWidget(fila + 1, columna)).setInvalido(mensaje);
	}

	public void setValido(int fila, int columna) {
		((FloatTextBox) grid.getWidget(fila + 1, columna)).setInvalido(null);
	}

	public void setColumnaEditable(int columna, boolean editable, boolean ultimoEditable) {
		this.columnasEditables[columna] = editable;
		this.ultimoEditable[columna] = ultimoEditable;
	}
}
