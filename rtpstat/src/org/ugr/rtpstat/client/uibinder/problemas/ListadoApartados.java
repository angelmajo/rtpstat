package org.ugr.rtpstat.client.uibinder.problemas;

import org.ugr.rtpstat.client.orm.Apartado;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.problemas.EditorApartado.PosicionApartado;
import org.ugr.rtpstat.client.uibinder.problemas.EditorProblema.Direccion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ListadoApartados extends Composite implements HasValueChangeHandlers<ListadoApartados> {
	private static final String TEXTO_NUEVO_APARTADO = "Nuevo %1";
	private static final String TEXTO_ELIMINAR_APARTADOS = "Eliminar todos los %1";

	private boolean sonSubapartados;
	private static ListadoApartadosUiBinder uiBinder = GWT.create(ListadoApartadosUiBinder.class);

	interface ListadoApartadosUiBinder extends UiBinder<Widget, ListadoApartados> {
	}

	@UiField
	Button nuevo_apartado;

	@UiField
	VerticalPanel panel_apartados;

	@UiField
	Button limpiar_apartados;
	private NotificadorGeneral notificadorGeneral;

	// public ListadoApartados() {
	// this(false);
	// }

	public ListadoApartados(final boolean sonSubapartados,NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		initWidget(uiBinder.createAndBindUi(this));
		this.sonSubapartados = sonSubapartados;
		if (sonSubapartados) {
			nuevo_apartado.setText(TEXTO_NUEVO_APARTADO.replace("%1", "subapartado"));
			limpiar_apartados.setText(TEXTO_ELIMINAR_APARTADOS.replace("%1", "subapartados"));
		} else {
			nuevo_apartado.setText(TEXTO_NUEVO_APARTADO.replace("%1", "apartado"));
			limpiar_apartados.setText(TEXTO_ELIMINAR_APARTADOS.replace("%1", "apartados"));
		}
		// Añade un nuevo apartado al problema
		nuevo_apartado.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				nuevoApartado();
			}
		});

		// Elimina todos los apartados
		limpiar_apartados.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boolean eliminar = Window.confirm("¿Esta seguro de que quiere eliminar todos los apartados?");
				if (eliminar) {
					eliminarApartados();
				}

			}
		});
		validarBotonosMoverApartado();
		validarBotonEliminarTodos();
		validarBotonNuevoApartado();

		ValueChangeEvent.fire(this, this);
	}

	public EditorApartado nuevoApartado() {
		EditorApartado nuevo = new EditorApartado(this, sonSubapartados,notificadorGeneral);
		nuevo.setEditando(true);
		final ListadoApartados thisRef = this;
		nuevo.addValueChangeHandler(new ValueChangeHandler<EditorApartado>() {
			public void onValueChange(ValueChangeEvent<EditorApartado> event) {
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		});
		panel_apartados.add(nuevo);
		for (int i = 0; i < panel_apartados.getWidgetCount() - 1; i++) {
			Widget w = panel_apartados.getWidget(i);
			if (w instanceof EditorApartado) {
				EditorApartado instance = ((EditorApartado) w);
				instance.setEditando(false);

			}
		}
		Window.scrollTo(nuevo.getAbsoluteLeft(), nuevo.getAbsoluteTop());
		validarBotonosMoverApartado();
		validarBotonEliminarTodos();
		validarBotonNuevoApartado();
		ValueChangeEvent.fire(this, this);
		return nuevo;
	}

	private void validarBotonosMoverApartado() {
		int wcount = panel_apartados.getWidgetCount();
		for (int i = 0; i < wcount; i++) {
			Widget w = panel_apartados.getWidget(i);
			if (w instanceof EditorApartado) {
				EditorApartado instance = ((EditorApartado) w);
				if (wcount == 1) {
					instance.setPosicion(PosicionApartado.UNICO);
				} else if (i == 0) {
					instance.setPosicion(PosicionApartado.PRIMERO);
				} else if (i < wcount - 1) {
					instance.setPosicion(PosicionApartado.INTERMEDIO);
				} else {
					instance.setPosicion(PosicionApartado.ULTIMO);
				}
			}
		}
	}

	public void moverApartado(EditorApartado editorApartado, Direccion direccion) {
		int index = panel_apartados.getWidgetIndex(editorApartado);
		switch (direccion) {
			case ARRIBA:
				panel_apartados.remove(index);
				panel_apartados.insert(editorApartado, index - 1);
				break;
			case ABAJO:
				panel_apartados.remove(index);
				panel_apartados.insert(editorApartado, index + 1);
				break;
		}

		validarBotonosMoverApartado();
	}

	protected void validarBotonEliminarTodos() {
		if (panel_apartados.getWidgetCount() > 0) {
			limpiar_apartados.setEnabled(true);
		} else {
			limpiar_apartados.setEnabled(false);
		}
	}

	protected void validarBotonNuevoApartado() {
		if (panel_apartados.getWidgetCount() >= 4) {
			nuevo_apartado.setEnabled(false);
		} else {
			nuevo_apartado.setEnabled(true);
		}
	}

	public void eliminarApartado(EditorApartado editorApartado) {

		panel_apartados.remove(editorApartado);
		validarBotonEliminarTodos();
		validarBotonosMoverApartado();
		validarBotonNuevoApartado();
		ValueChangeEvent.fire(this, this);
	}

	protected void eliminarApartados() {
		while (panel_apartados.getWidgetCount() > 0) {
			panel_apartados.remove(0);
		}
		validarBotonEliminarTodos();
		validarBotonNuevoApartado();
		ValueChangeEvent.fire(this, this);
	}

	public void limpiar() {
		eliminarApartados();
	}

	public boolean isValid() {
		boolean out = true;
		if (panel_apartados.getWidgetCount() == 0) {
			out = false;
			this.addStyleName("campo");
			this.addStyleName("campoinvalido");
			this.setTitle("Un problema tiene que tener un apartado como minimo");
		} else {
			this.removeStyleName("campo");
			this.removeStyleName("campoinvalido");
			this.setTitle("");
			for (int i = 0; i < panel_apartados.getWidgetCount(); i++) {
				EditorApartado editor = (EditorApartado) panel_apartados.getWidget(i);
				if (!editor.isValid()) {
					out = false;
					if (!sonSubapartados) {
						editor.addStyleName("campoinvalido");
					}
				} else if (!sonSubapartados) {
					editor.removeStyleName("campoinvalido");
				}
			}
		}
		return out;
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<ListadoApartados> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public int getCuentaApartados() {
		return panel_apartados.getWidgetCount();
	}

	public Apartado[] getApartados() {
		Apartado[] out = new Apartado[panel_apartados.getWidgetCount()];
		for (int i = 0; i < out.length; i++) {
			out[i] = ((EditorApartado) panel_apartados.getWidget(i)).getApartado();
		}
		return out;
	}

	public void setApartados(Apartado[] apartados) {
		limpiar();
		for(int i = 0; i < apartados.length; i++) {
			EditorApartado editor = nuevoApartado();
			editor.setApartado(apartados[i]);
		}
	}
}
