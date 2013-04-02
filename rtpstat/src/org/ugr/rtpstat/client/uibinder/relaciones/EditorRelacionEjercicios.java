package org.ugr.rtpstat.client.uibinder.relaciones;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.orm.TipoRelacion;
import org.ugr.rtpstat.client.uibinder.libros.CheckableMenuItem;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorRelacionEjercicios extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static EditorRelacionEjerciciosUiBinder uiBinder = GWT.create(EditorRelacionEjerciciosUiBinder.class);
	private ArrayList<ProblemaEnRelacion> problemasEnRelacion = new ArrayList<ProblemaEnRelacion>();
	private ArrayList<CheckableMenuItem> areas_objetivo = new ArrayList<CheckableMenuItem>();

	interface EditorRelacionEjerciciosUiBinder extends UiBinder<Widget, EditorRelacionEjercicios> {
	}

	interface Estilo extends CssResource {
		String disabled();
		String mensajeUnEjercicioMinimo();
	}

	@UiField
	MenuBar menu_areas_objetivo;

	@UiField
	Estilo style;

	@UiField
	MenuItem guardar;

	/*
	 * @UiField MenuItem generar_pdf;
	 * 
	 * @UiField MenuItem descargar;
	 */

	@UiField
	TextBox titulo;

	@UiField
	ListBox tipo;

	@UiField
	VerticalPanel lista_problemas;

	@UiHandler("titulo")
	public void tituloCambiado(KeyUpEvent event) {
		validar();
	}

	@UiHandler("titulo")
	public void tituloCambiado(ChangeEvent event) {
		validar();
	}

	private void validar() {
		boolean valido = true;

		if (titulo.getText().length() == 0) {
			titulo.addStyleName("campoinvalido");
			titulo.setTitle("Este campo es obligatorio");
			valido = false;
		} else {
			titulo.removeStyleName("campoinvalido");
			titulo.setTitle("");
		}
		
		int cuentaIncluidos = 0;
		for (ProblemaEnRelacion p : problemasEnRelacion) {
			if (p.isIncluido()) {
				cuentaIncluidos++;
			}
		}
		
		if (cuentaIncluidos == 0) {
			lista_problemas.setTitle("Tienes que seleccionar como mínimo un problema para la relación");
			
			Label mensaje = new Label("Tienes que seleccionar como mínimo un problema para la relación");
			mensaje.addStyleName("campoinvalido");
			mensaje.addStyleName(style.mensajeUnEjercicioMinimo());
			lista_problemas.insert(mensaje, 0);
			valido = false;
		} else {
			lista_problemas.setTitle("");
			if(lista_problemas.getWidget(0) instanceof Label) {
				lista_problemas.remove(0);
			}
		}

		setGuardarEnabled(valido);
	}

	private long idRelacion;
	private NotificadorGeneral notificadorGeneral;

	public EditorRelacionEjercicios(long id, final NotificadorGeneral notificadorGeneral) {
		this.idRelacion = Long.MIN_VALUE;
		this.notificadorGeneral = notificadorGeneral;
		initWidget(uiBinder.createAndBindUi(this));

		setGuardarEnabled(false);

		// setDescargarEnabled(false);
		// setGenerarEnabled(false);

		for (TipoRelacion t : TipoRelacion.values()) {
			tipo.addItem(t.toLongString(), t.toString());
		}
		this.idRelacion = id;
		rtpstatService.getRelacionEjercicios(id, new AsyncCallback<RelacionEjercicios>() {
			public void onSuccess(final RelacionEjercicios relacionEjercicios) {
				titulo.setText(relacionEjercicios.getTitulo());
				tipo.setSelectedIndex(relacionEjercicios.getTipo().ordinal());
				rtpstatService.listadoProblemas(new AsyncCallback<List<ResumenProblema>>() {
					public void onFailure(Throwable caught) {

					}

					public void onSuccess(List<ResumenProblema> listadoProblemasCompleto) {
						for (ResumenProblema problema : listadoProblemasCompleto) {
							// FIXME OMG!!!!
							boolean incluido = false;
							for (ResumenProblema p : relacionEjercicios.getProblemas()) {
								if (p.getId() == problema.getId()) {
									incluido = true;
									break;
								}
							}
							ProblemaEnRelacion widget = new ProblemaEnRelacion(incluido, problema, notificadorGeneral);
							widget.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {
									validar();
								}
							});
							problemasEnRelacion.add(widget);
							lista_problemas.add(widget);
						}
						validar();
					}
				});

				actualizarListaAreasObjetivo(notificadorGeneral, relacionEjercicios);
			}

			private void actualizarListaAreasObjetivo(final NotificadorGeneral notificadorGeneral, final RelacionEjercicios relacionEjercicios) {
				rtpstatService.listarAreasObjetivo(new AsyncCallback<String[]>() {
					public void onFailure(Throwable caught) {
						areas_objetivo.clear();
						menu_areas_objetivo.addItem("<strong>¡Ha fallado la descarga!</strong><br/> Pulsa aquí para reintentarlo", true, new Command() {
							@Override
							public void execute() {
								actualizarListaAreasObjetivo(notificadorGeneral, relacionEjercicios);
							}
						});
					}

					public void onSuccess(String[] resultAreas) {
						menu_areas_objetivo.clearItems();
						List<String> areasEnRelacion = Arrays.asList(relacionEjercicios.getAreasObjetivo());

						for (String area : resultAreas) {
							CheckableMenuItem item = new CheckableMenuItem(area);

							item.setChecked(areasEnRelacion.contains(area));

							areas_objetivo.add(item);
							menu_areas_objetivo.addItem(item);
						}
					}
				});
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Ha fallado la descarga de la relación, recarge la página para volver a intentarlo");
			}
		});
	}

	private Command guardarCommand = new Command() {
		public void execute() {
			String tituloNuevo = titulo.getText();
			TipoRelacion tipoNuevo = TipoRelacion.valueOf(tipo.getValue(tipo.getSelectedIndex()));
			ArrayList<String> areasNuevas = new ArrayList<String>();
			for (int i = 0; i < areas_objetivo.size(); i++) {
				if (areas_objetivo.get(i).isChecked()) {
					areasNuevas.add(areas_objetivo.get(i).getText());
				}
			}
			ArrayList<Long> problemasIncluidos = new ArrayList<Long>();
			for (ProblemaEnRelacion p : problemasEnRelacion) {
				if (p.isIncluido()) {
					problemasIncluidos.add(p.getProblema().getId());
				}
			}
			try {
				rtpstatService.updateRelacion(idRelacion, tituloNuevo, tipoNuevo, areasNuevas.toArray(new String[areasNuevas.size()]), problemasIncluidos, new AsyncCallback<Boolean>() {
					public void onSuccess(Boolean result) {
						notificadorGeneral.info("Relación de problemas " + idRelacion + " guardada");
						History.newItem("mis_relaciones");
					}

					public void onFailure(Throwable caught) {
						notificadorGeneral.warning("No se ha podido guardar la relación.  Si el error persiste, <a tabindex=\"0\" class=\"gwt-Anchor\" target=\"_blank\" href=\"http://code.google.com/p/rtpstat/issues/entry\">¡Cuéntanoslo!</a> ");
					}
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void setGuardarEnabled(boolean enabled) {
		enabled = enabled && idRelacion != Long.MIN_VALUE;
		if (!enabled) {
			guardar.addStyleName(style.disabled());
		} else {
			guardar.removeStyleName(style.disabled());
		}
		guardar.setScheduledCommand(enabled ? guardarCommand : null);
	}
}
