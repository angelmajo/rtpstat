package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.ArrayList;

import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.Apartado;
import org.ugr.rtpstat.client.orm.ApartadoConCalculos;
import org.ugr.rtpstat.client.orm.ApartadoConSubApartados;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class EditorApartado extends Composite implements HasValueChangeHandlers<EditorApartado> {
	public static enum PosicionApartado {
		PRIMERO, INTERMEDIO, ULTIMO, UNICO
	};

	public static enum TipoContenido {
		CALCULOS, SUBAPARTADOS
	};

	private static EditorApartadoUiBinder uiBinder = GWT.create(EditorApartadoUiBinder.class);

	interface EditorApartadoUiBinder extends UiBinder<Widget, EditorApartado> {
	}

	@UiField
	DisclosurePanel panel;

	@UiField
	TextArea enunciado;

	@UiField
	PushButton mover_arriba;

	@UiField
	PushButton mover_abajo;

	@UiField
	PushButton eliminar;

	@UiField
	RadioButton tiene_calculos;

	@UiField
	RadioButton tiene_subapartados;

	@UiField
	HorizontalPanel seleccionar_contenido;

	@UiField
	EditorCalculos calculos;

	@UiField(provided=true)
	ListadoApartados listado_subapartados;

	@UiHandler("calculos")
	public void calculosModificado(ValueChangeEvent<EditorCalculos> event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}
	
	@UiHandler("enunciado")
	public void enunciadoModificado(ValueChangeEvent<String> event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("enunciado")
	public void enunciadoModificado(KeyUpEvent event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("listado_subapartados")
	public void subapartadosChanged(ValueChangeEvent<ListadoApartados> event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	private void validar() {
		if (enunciado.getText().length() == 0) {
			addError(FallosValidacion.ENUNCIADO);
			enunciado.addStyleName("campoinvalido");
			enunciado.setTitle(EditorProblema.CAMPO_OBLIGATORIO);
		} else {
			removeError(FallosValidacion.ENUNCIADO);
			enunciado.removeStyleName("campoinvalido");
			enunciado.setTitle("");
		}
		if (!this.esSubapartado) {
			if (!tiene_calculos.getValue() && !tiene_subapartados.getValue()) {
				addError(FallosValidacion.CONTENIDO_ELEGIDO);
				tiene_calculos.getParent().addStyleName("campoinvalido");
				tiene_calculos.getParent().setTitle(EditorProblema.CAMPO_OBLIGATORIO);
			} else {
				removeError(FallosValidacion.CONTENIDO_ELEGIDO);
				tiene_calculos.getParent().removeStyleName("campoinvalido");
			}
		}
		if (esSubapartado || tiene_calculos.getValue()) {
			removeError(FallosValidacion.NINGUN_SUBAPARTADO);
			removeError(FallosValidacion.SUBAPARTADOS);
		}
		if (tiene_subapartados.getValue()) {
			if (listado_subapartados.isValid() == false) {
				if (listado_subapartados.getCuentaApartados() == 0) {
					addError(FallosValidacion.NINGUN_SUBAPARTADO);
					listado_subapartados.addStyleName("campoinvalido");
					listado_subapartados.setTitle(EditorProblema.CAMPO_OBLIGATORIO);
				} else {
					removeError(FallosValidacion.NINGUN_SUBAPARTADO);
					listado_subapartados.removeStyleName("campoinvalido");
					addError(FallosValidacion.SUBAPARTADOS);
				}
			} else {
				removeError(FallosValidacion.NINGUN_SUBAPARTADO);
				removeError(FallosValidacion.SUBAPARTADOS);
				listado_subapartados.removeStyleName("campoinvalido");
			}
		} else if (tiene_calculos.getValue()) {
			if(calculos.isValid()) {
				removeError(FallosValidacion.NINGUN_CALCULO);
				calculos.removeStyleName("campoinvalido");
			} else {
				addError(FallosValidacion.NINGUN_CALCULO);
				calculos.addStyleName("campoinvalido");
			}
		}
	}

	private static enum FallosValidacion {
		ENUNCIADO, CONTENIDO_ELEGIDO, NINGUN_SUBAPARTADO, SUBAPARTADOS, NINGUN_CALCULO
	}

	private ArrayList<FallosValidacion> fallos = new ArrayList<FallosValidacion>();

	private void addError(FallosValidacion fallo) {
		if (!fallos.contains(fallo)) {
			fallos.add(fallo);
		}
	}

	private void removeError(FallosValidacion fallo) {
		fallos.remove(fallo);
	}

	private final ListadoApartados listadoApartadosPadre;
	private boolean esSubapartado;

	private NotificadorGeneral notificadorGeneral;

	public EditorApartado(ListadoApartados listadoApartados, boolean esSubapartado,NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.listado_subapartados = new ListadoApartados(true, notificadorGeneral);
		initWidget(uiBinder.createAndBindUi(this));
		this.listadoApartadosPadre = listadoApartados;
		tiene_calculos.setName(tiene_calculos.getName() + hashCode());
		tiene_subapartados.setName(tiene_subapartados.getName() + hashCode());
		setupHandlers();
		this.esSubapartado = esSubapartado;
		if (esSubapartado) {
			seleccionar_contenido.removeFromParent();
			actualizar_tipo_contenido(TipoContenido.CALCULOS);
		}
		validar();
	}

	private void setupHandlers() {
		mover_arriba.getUpFace().setImage(new Image(Imagenes.INSTANCE.flechaArriba()));
		mover_arriba.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mover_arriba();
			}
		});
		mover_abajo.getUpFace().setImage(new Image(Imagenes.INSTANCE.flechaAbajo()));
		mover_abajo.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mover_abajo();
			}
		});

		panel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				setEditando(true);
			}
		});

		panel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			public void onClose(CloseEvent<DisclosurePanel> event) {
				setEditando(false);
			}
		});

		eliminar.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				eliminarApartado();
			}
		});

		final EditorApartado thisRef = this;
		tiene_calculos.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				actualizar_tipo_contenido(TipoContenido.CALCULOS);
				validar();
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		});
		tiene_subapartados.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				actualizar_tipo_contenido(TipoContenido.SUBAPARTADOS);
				validar();
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		});
	}

	protected void actualizar_tipo_contenido(TipoContenido tipoContenido) {
		switch (tipoContenido) {
			case CALCULOS:
				calculos.setVisible(true);
				// calculos.panel.setSplitPosition("50%");
				listado_subapartados.setVisible(false);
				break;
			case SUBAPARTADOS:
				listado_subapartados.setVisible(true);
				calculos.setVisible(false);
				break;
		}
	}

	protected void eliminarApartado() {
		listadoApartadosPadre.eliminarApartado(this);
	}

	protected void mover_abajo() {
		listadoApartadosPadre.moverApartado(this, EditorProblema.Direccion.ABAJO);

	}

	protected void mover_arriba() {
		listadoApartadosPadre.moverApartado(this, EditorProblema.Direccion.ARRIBA);

	}

	public void setEditando(boolean open) {
		panel.setOpen(open);
		if (open) {
			panel.getHeaderTextAccessor().setText("Click para ocultar editor");
		} else {
			actualizarTextoCabecera();
		}
	}

	public void setPosicion(PosicionApartado posicion) {
		mover_arriba.setEnabled(true);
		mover_abajo.setEnabled(true);
		switch (posicion) {
			case PRIMERO:
				mover_arriba.setEnabled(false);
				break;
			case ULTIMO:
				mover_abajo.setEnabled(false);
				break;
			case UNICO:
				mover_arriba.setEnabled(false);
				mover_abajo.setEnabled(false);
				break;
			case INTERMEDIO:
				// Este es el caso con los estados por defecto
				break;
		}
	}

	private void actualizarTextoCabecera() {
		int length = enunciado.getText().length();
		if (length == 0) {
			panel.getHeaderTextAccessor().setText("Apartado sin descripción");
		} else {
			String texto = enunciado.getText();
			texto = texto.replaceAll("\n", "<br>");
			panel.getHeaderTextAccessor().setText(texto);
		}
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorApartado> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public boolean isValid() {
		return fallos.size() == 0;
	}

	public Apartado getApartado() {
		Apartado out = null;
		if (esSubapartado || tiene_calculos.getValue()) {
			out = new ApartadoConCalculos(enunciado.getText(),calculos.getCalculosAsignados());
		} else if (tiene_subapartados.getValue()) {
			out = new ApartadoConSubApartados(enunciado.getText(),listado_subapartados.getApartados());
		}
		return out;
	}

	public void setApartado(Apartado apartado) {
		enunciado.setText(apartado.getEnunciado());
		if (apartado instanceof ApartadoConCalculos) {
			tiene_calculos.setValue(true);
			actualizar_tipo_contenido(TipoContenido.CALCULOS);
			ApartadoConCalculos ap = (ApartadoConCalculos) apartado;
			calculos.setCalculos(ap.getCalculos());
		} else if (apartado instanceof ApartadoConSubApartados) {
			tiene_subapartados.setValue(true);
			actualizar_tipo_contenido(TipoContenido.SUBAPARTADOS);
			ApartadoConSubApartados ap = (ApartadoConSubApartados) apartado;
			listado_subapartados.limpiar();
			listado_subapartados.setApartados(ap.getSubApartados());
		} else {
			notificadorGeneral.warning(apartado.getClass().getName());
		}
		validar();
	}
}
