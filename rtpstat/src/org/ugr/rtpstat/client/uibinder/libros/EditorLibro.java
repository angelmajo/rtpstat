package org.ugr.rtpstat.client.uibinder.libros;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.orm.CapituloLibro;
import org.ugr.rtpstat.client.orm.Libro;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.AdvancedTextBox;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorLibro extends Composite implements ValueChangeHandler<Void> {

	private static final int MAX_CAPITULOS = 30;

	private List<ResumenProblema> listadoProblemas;
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static EditorLibroUiBinder uiBinder = GWT.create(EditorLibroUiBinder.class);
	private List<EditorCapitulo> editoresCapitulo = new ArrayList<EditorCapitulo>();
	private List<HandlerRegistration> editoresCapituloHandlers = new ArrayList<HandlerRegistration>();
	private ArrayList<CheckableMenuItem> areas_objetivo = new ArrayList<CheckableMenuItem>();
	private NotificadorGeneral notificadorGeneral;
	private Long idLibro = null;

	private Command guardarCommand = new Command() {
		public void execute() {
			notificadorGeneral.info("Guardando nuevo libro");
			ArrayList<CapituloLibro> capitulosArray = new ArrayList<CapituloLibro>();
			for (EditorCapitulo editor : editoresCapitulo) {
				capitulosArray.add(editor.getCapitulo());
			}
			ArrayList<String> areasSeleccionadas = new ArrayList<String>();
			for (CheckableMenuItem area : areas_objetivo) {
				if (area.isChecked()) {
					areasSeleccionadas.add(area.getText());
				}
			}
			Libro libro = new Libro(titulo.getText(), capitulosArray, areasSeleccionadas.toArray(new String[areasSeleccionadas.size()]));
			if (idLibro == null) {
				rtpstatService.addLibro(libro, new AsyncCallback<Long>() {
					@Override
					public void onSuccess(Long result) {
						notificadorGeneral.info("Libro " + result + " guardado");
						History.newItem("mis_libros");
					}

					@Override
					public void onFailure(Throwable caught) {
						notificadorGeneral.warning("Ha sido imposible guardar el Libro por un problema de comunicación con el servidor");
					}
				});
			} else {
				libro.setId(idLibro);
				rtpstatService.updateLibro(libro, new AsyncCallback<Boolean>() {
					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							notificadorGeneral.info("Libro " + idLibro + " guardado");
							History.newItem("mis_libros");
						} else {
							notificadorGeneral.warning("Libro " + idLibro + " no guardado");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						notificadorGeneral.warning("Ha sido imposible guardar el Libro por un problema de comunicación con el servidor");
					}
				});
			}
		}
	};

	private class DialogoEliminarCapitulos extends DialogBox {
		public DialogoEliminarCapitulos(final int numCapitulosAEliminar) {
			setGlassEnabled(true);
			setAnimationEnabled(true);
			setText("Has solicitado eliminar " + numCapitulosAEliminar + " capítulo" + (numCapitulosAEliminar == 1 ? "" : "s"));

			VerticalPanel panel = new VerticalPanel();
			panel.setWidth("100%");
			if (numCapitulosAEliminar == 1) {
				panel.add(new Label("Selecciona el capítulo a eliminar:"));
			} else {
				panel.add(new Label("Selecciona los " + numCapitulosAEliminar + " capítulos a eliminar:"));
			}
			final ListBox seleccionados = new ListBox(true);
			seleccionados.setWidth("100%");
			seleccionados.setVisibleItemCount(10);
			for (int i = 0; i < editoresCapitulo.size(); i++) {
				seleccionados.addItem("" + (i + 1) + " - " + editoresCapitulo.get(i).getTitulo(), "" + i);
			}

			panel.add(seleccionados);

			final Button ok = new Button("Borrar");
			ok.setEnabled(false);
			Anchor cancelar = new Anchor("Cancelar");

			HorizontalPanel controles = new HorizontalPanel();
			controles.setWidth("100%");
			controles.add(cancelar);
			controles.add(ok);
			controles.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);

			panel.add(controles);

			this.add(panel);

			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
					for (int i = seleccionados.getItemCount() - 1; i >= 0; i--) {
						if (seleccionados.isItemSelected(i)) {
							HandlerRegistration handlerReg = editoresCapituloHandlers.get(i);
							handlerReg.removeHandler();
							editoresCapituloHandlers.remove(handlerReg);

							EditorCapitulo editor = editoresCapitulo.get(i);
							editoresCapitulo.remove(editor);
							capitulos.remove(editor);
						}
					}
					for (int i = 0; i < capitulos.getWidgetCount(); i++) {
						capitulos.setTabText(i, "" + (i + 1));
					}
					validar();
				}
			});

			cancelar.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
					numero_capitulos.setSelectedIndex(capitulos.getWidgetCount() - 1);
				}
			});

			seleccionados.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					int selCount = 0;
					for (int i = 0; i < seleccionados.getItemCount(); i++) {
						if (seleccionados.isItemSelected(i)) {
							selCount++;
						}
					}
					ok.setEnabled(selCount == numCapitulosAEliminar);
				}
			});
		}
	}

	interface EditorLibroUiBinder extends UiBinder<Widget, EditorLibro> {
	}

	interface Estilo extends CssResource {
		String disabled();

		String invalido();
	}

	@UiField
	Estilo style;

	@UiField
	MenuItem guardar;

	@UiField
	MenuBar menu_areas_objetivo;

	@UiField
	AdvancedTextBox titulo;

	@UiField
	ListBox numero_capitulos;

	@UiField
	TabLayoutPanel capitulos;

	@UiHandler("numero_capitulos")
	public void numeroCapitulosCambiado(ChangeEvent event) {
		int numCapitulosDeseados = numero_capitulos.getSelectedIndex() + 1;
		cambiarNumeroCapitulos(numCapitulosDeseados);
		validar();
	}

	private void cambiarNumeroCapitulos(int numCapitulosDeseados) {
		int numCapitulosExistentes = capitulos.getWidgetCount();
		if (numCapitulosExistentes < numCapitulosDeseados) {
			for (int i = numCapitulosExistentes; i < numCapitulosDeseados; i++) {
				EditorCapitulo editor = new EditorCapitulo(notificadorGeneral, listadoProblemas);
				HandlerRegistration handlerReg = editor.addValueChangeHandler(this);
				editoresCapituloHandlers.add(handlerReg);
				editoresCapitulo.add(editor);
				capitulos.add(editor, "" + (i + 1));
			}
		} else {
			int numCapitulosAEliminar = numCapitulosExistentes - numCapitulosDeseados;
			if (numCapitulosAEliminar > 0) {
				DialogoEliminarCapitulos dialogo = new DialogoEliminarCapitulos(numCapitulosAEliminar);
				dialogo.showRelativeTo(numero_capitulos);
			}
		}
	}

	@UiHandler("titulo")
	public void tituloCambiado(KeyUpEvent ev) {
		validar();
	}

	@UiHandler("titulo")
	public void tituloCambiado(ChangeEvent ev) {
		validar();
	}

	private void validar() {
		titulo.removeStyleName("campoinvalido");
		boolean valid = true;
		if (titulo.getText().length() == 0) {
			titulo.addStyleName("campoinvalido");
			valid = false;
		}
		for (EditorCapitulo editor : editoresCapitulo) {
			Widget tab = capitulos.getTabWidget(capitulos.getWidgetIndex(editor));
			if (!editor.isValid()) {
				valid = false;
				tab.addStyleName(style.invalido());
			} else {
				tab.removeStyleName(style.invalido());
			}
		}
		setGuardarEnabled(valid);
	}

	private void setGuardarEnabled(boolean enabled) {
		if (enabled) {
			guardar.removeStyleName(style.disabled());
			guardar.setScheduledCommand(guardarCommand);
		} else {
			guardar.addStyleName(style.disabled());
			guardar.setScheduledCommand(null);
		}
	}

	public EditorLibro(final Long id, final NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		initWidget(uiBinder.createAndBindUi(this));
		for (int i = 1; i < MAX_CAPITULOS; i++) {
			numero_capitulos.addItem("" + i);
		}
		numero_capitulos.setSelectedIndex(0);

		rtpstatService.listarAreasObjetivo(new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {

			}

			public void onSuccess(String[] result) {
				menu_areas_objetivo.clearItems();
				for (String area : result) {
					CheckableMenuItem item = new CheckableMenuItem(area);
					areas_objetivo.add(item);
					menu_areas_objetivo.addItem(item);
				}
				rtpstatService.listadoProblemas(new AsyncCallback<List<ResumenProblema>>() {
					public void onFailure(Throwable caught) {

					}

					public void onSuccess(List<ResumenProblema> listadoProblemasCompleto) {
						listadoProblemas = listadoProblemasCompleto;
						numero_capitulos.setEnabled(true);
						numeroCapitulosCambiado(null);
						if (id != Long.MIN_VALUE) {
							rtpstatService.getLibro(id, new AsyncCallback<Libro>() {
								@Override
								public void onSuccess(Libro result) {
									if (result != null) {
										idLibro = id;
										titulo.setText(result.getTitulo());
										numero_capitulos.setSelectedIndex(result.getCapitulos().size() - 1);
										cambiarNumeroCapitulos(result.getCapitulos().size());
										for (int i = 0; i < result.getCapitulos().size(); i++) {
											EditorCapitulo editor = editoresCapitulo.get(i);
											CapituloLibro capitulo = result.getCapitulos().get(i);
											editor.setCapituloLibro(capitulo);
										}
										List<String> areasEnLibro = Arrays.asList(result.getAreasObjetivo());
										for (CheckableMenuItem area : areas_objetivo) {
											area.setChecked(areasEnLibro.contains(area.getText()));
										}
										validar();
									} else {
										notificadorGeneral.warning("Ese libro no parece existir");
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									notificadorGeneral.warning("Ha ocurrido un error de comunicación con el servidor");
								}

							});
						}
					}
				});
			}
		});
	}

	public EditorLibro(final NotificadorGeneral notificadorGeneral) {
		this(Long.MIN_VALUE, notificadorGeneral);
	}

	public void onValueChange(ValueChangeEvent<Void> event) {
		validar();
	}
}
