package org.ugr.rtpstat.client.uibinder.problemas;

import java.util.ArrayList;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.problemas.datos.EditorDatos;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class EditorProblema extends LayoutPanel {
  public static final String CAMPO_OBLIGATORIO = "Este campo es obligatorio";

  private static final String MSG_FALLO_COMUNICACION = "Fallo de comunicación con el servidor: ";

  private static final String MSG_PROBLEMA_SALVADO = "Problema guardado";

  private static final String MSG_ERRORES = "Los campos enmarcados en rojo son obligatorios";

  interface Estilos extends CssResource {
    String notificacionInfo();

    String notificacionError();

    String stackConErrores();

    String disabledMenuItem();
  }

  @UiField
  Estilos style;

  @UiField
  MenuItem guardar;
  /*
   * @UiField Button guardar_top;
   * 
   * @UiField HTML notificacionTop;
   */

  @UiField
  StackLayoutPanel stack_panel;

  @UiField
  TextArea descripcion;

  @UiField
  ListBox dificultad;

  @UiField
  TextArea cabecera;

  @UiField
  TextArea cabecera_segunda;

  @UiField
  EditorDatos editor_datos;

  @UiField
  ListBox areas_objetivo;

  @UiField(provided = true)
  ListadoApartados listado_apartados;

  private NotificadorGeneral notificadorGeneral;

  public static enum Direccion {
    ARRIBA, ABAJO
  };

  public EditorProblema(final NotificadorGeneral notificadorGeneral) {
    this.notificadorGeneral = notificadorGeneral;
    this.listado_apartados = new ListadoApartados(false, notificadorGeneral);
    add(uiBinder.createAndBindUi(this));

    for (GradoDificultad grado : GradoDificultad.values()) {
      dificultad.addItem(grado.comoString(), grado.toString());
    }

    dificultad.setVisibleItemCount(dificultad.getItemCount());

    /*
     * final HTML topref = notificacionTop; ocultarNotificacion = new Timer() {
     * 
     * @Override public void run() { guardar_top.setVisible(true);
     * topref.setVisible(false); } };
     */
    areas_objetivo.setVisibleItemCount(dificultad.getItemCount());// Para
    // mantener
    // la
    // consistencia
    // visual
    // y el
    // layout
    areas_objetivo.addItem("Cargando áreas disponibles...");
    areas_objetivo.setEnabled(false);
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
        validar();
      }
    });
    validar();
  }

  // Manejando los cambios para habilitar/deshabilitar el boton guardar

  @UiHandler("editor_datos")
  public void datosCambiados(ValueChangeEvent<EditorDatos> event) {
    validar();
  }

  @UiHandler({ "descripcion", "cabecera", "cabecera_segunda" })
  public void descripcionCambiada(ValueChangeEvent<String> evento) {
    validar();
  }

  @UiHandler({ "descripcion", "cabecera", "cabecera_segunda", "dificultad", "areas_objetivo" })
  public void descripcionCambiada(KeyUpEvent event) {
    validar();
  }

  @UiHandler({ "areas_objetivo", "dificultad" })
  public void areasObjetivoCambiado(ChangeEvent ev) {
    validar();
  }

  @UiHandler({ "areas_objetivo", "dificultad" })
  public void areasObjetivoCambiado(ClickEvent ev) {
    validar();
  }

  @UiHandler("listado_apartados")
  public void apartadosChanged(ValueChangeEvent<ListadoApartados> event) {
    validar();
  }

  public void setProblema(Problema problema) {
    editandoNuevoProblema = problema == null ? true : false;
    if (!editandoNuevoProblema) {
      idProblema = problema.getId();
    }
    if (problema == null) {
      limpiarEditor();
    } else {
      limpiarEditor();
      __setProblema(problema);
    }
    validar();
  }

  private void validar() {
    boolean errorEnDetalles = false;
    boolean errorEnDatos = false;
    boolean errorEnApartados = false;

    if (descripcion.getText().length() == 0) {
      addError(FallosValidacion.DESCRIPCION);
      descripcion.addStyleName("campoinvalido");
      descripcion.setTitle(CAMPO_OBLIGATORIO);
      errorEnDetalles = true;
    } else {
      removeError(FallosValidacion.DESCRIPCION);
      descripcion.removeStyleName("campoinvalido");
      descripcion.setTitle("");
    }
    if (cabecera.getText().length() == 0) {
      addError(FallosValidacion.CABECERA);
      cabecera.addStyleName("campoinvalido");
      cabecera.setTitle(CAMPO_OBLIGATORIO);
      errorEnDetalles = true;
    } else {
      removeError(FallosValidacion.CABECERA);
      cabecera.removeStyleName("campoinvalido");
      cabecera.setTitle("");
    }
    if (dificultad.getSelectedIndex() < 0) {
      addError(FallosValidacion.DIFICULTAD);
      dificultad.addStyleName("campoinvalido");
      dificultad.setTitle(CAMPO_OBLIGATORIO);
      errorEnDetalles = true;
    } else {
      removeError(FallosValidacion.DIFICULTAD);
      dificultad.removeStyleName("campoinvalido");
      dificultad.setTitle("");
    }
    if (areas_objetivo.getSelectedIndex() < 0) {
      addError(FallosValidacion.AREAS_OBJETIVO);
      areas_objetivo.addStyleName("campoinvalido");
      areas_objetivo.setTitle(CAMPO_OBLIGATORIO);
      errorEnDetalles = true;
    } else {
      removeError(FallosValidacion.AREAS_OBJETIVO);
      areas_objetivo.removeStyleName("campoinvalido");
      areas_objetivo.setTitle("");
    }

    // if (cabecera_segunda.getText().length() == 0) {
    // cabecera_segunda.addStyleName("campoinvalido");
    // cabecera_segunda.setTitle(CAMPO_OBLIGATORIO);
    // addError(FallosValidacion.CABECERA2);
    // errorEnDetalles = true;
    // } else {
    // removeError(FallosValidacion.CABECERA2);
    // cabecera_segunda.removeStyleName("campoinvalido");
    // cabecera_segunda.setTitle("");
    // }
    if (editor_datos.validar() == false) {
      addError(FallosValidacion.DATOS);
      errorEnDatos = true;
    } else {
      removeError(FallosValidacion.DATOS);
    }
    if (listado_apartados.isValid() == false) {
      addError(FallosValidacion.APARTADOS);
      errorEnApartados = true;
    } else {
      removeError(FallosValidacion.APARTADOS);
    }
    if (errorEnDetalles) {
      stack_panel.getHeaderWidget(0).addStyleName(style.stackConErrores());
    } else {
      stack_panel.getHeaderWidget(0).removeStyleName(style.stackConErrores());
    }
    if (errorEnDatos) {
      stack_panel.getHeaderWidget(1).addStyleName(style.stackConErrores());
    } else {
      stack_panel.getHeaderWidget(1).removeStyleName(style.stackConErrores());
    }
    if (errorEnApartados) {
      stack_panel.getHeaderWidget(2).addStyleName(style.stackConErrores());
    } else {
      stack_panel.getHeaderWidget(2).removeStyleName(style.stackConErrores());
    }
  }

  private static enum FallosValidacion {
    DESCRIPCION, CABECERA, CABECERA2, DATOS, APARTADOS, AREAS_OBJETIVO, DIFICULTAD
  }

  private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

  private ArrayList<FallosValidacion> fallos = new ArrayList<FallosValidacion>();
  private boolean editandoNuevoProblema = true;
  private long idProblema;

  private Command guardarCommand = new Command() {
    public void execute() {
      guardarProblema();
    }
  };

  private static EditorProblemaUiBinder uiBinder = GWT.create(EditorProblemaUiBinder.class);

  interface EditorProblemaUiBinder extends UiBinder<Widget, EditorProblema> {
  }

  private void addError(FallosValidacion fallo) {
    if (!fallos.contains(fallo)) {
      fallos.add(fallo);
    }
    revisarErrores();
  }

  private void removeError(FallosValidacion fallo) {
    fallos.remove(fallo);
    revisarErrores();
  }

  private void revisarErrores() {
    if (fallos.size() > 0) {
      guardar.setScheduledCommand(null);
      guardar.setText(MSG_ERRORES);
      guardar.addStyleName(style.disabledMenuItem());
    } else {
      guardar.setScheduledCommand(guardarCommand);
      guardar.setText("Guardar");
      guardar.removeStyleName(style.disabledMenuItem());
    }
    // guardar_top.setEnabled(fallos.size() == 0);
    // guardar_top.setTitle(msg);
    // guardar_bottom.setEnabled(fallos.size() == 0);
    // guardar_bottom.setTitle(msg);
  }

  private void __setProblema(Problema problema) {
    if (problema.getDificultad() == null) {
      problema.setDificultad(GradoDificultad.Intermedio);
    }

    descripcion.setText(problema.getDescripcion());
    dificultad.setSelectedIndex(problema.getDificultad().ordinal());

    cabecera.setText(problema.getCabecera());
    cabecera_segunda.setText(problema.getCabeceraSegunda());
    for (int i = 0; i < areas_objetivo.getItemCount(); i++) {
      areas_objetivo.setItemSelected(i, false);
      for (String area : problema.getAreasObjetivo()) {
        if (areas_objetivo.getItemText(i).equals(area)) {
          areas_objetivo.setItemSelected(i, true);
        }
      }
    }
    editor_datos.setDatos(problema.getDatos());
    listado_apartados.setApartados(problema.getApartados());
    // out.setDatos(editor_datos.getDatos());
    // out.setApartados(listado_apartados.getApartados());
  }

  private void limpiarEditor() {
    descripcion.setText("");
    areas_objetivo.setSelectedIndex(-1);
    cabecera.setText("");
    cabecera_segunda.setText("");

    editor_datos.limpiar();
    listado_apartados.limpiar();
    listado_apartados.nuevoApartado();
  }

  private void guardarProblema() {
    Problema p = getProblema();
    notificadorGeneral.info("Guardando problema");
    if (editandoNuevoProblema) {
      rtpstatService.addProblema(p, new AsyncCallback<Long>() {
        public void onSuccess(Long result) {
          if (result == Long.MIN_VALUE) {
            notificadorGeneral
                .warning("Ha ocurrido un error al intentar guardar el problema. Si el error persiste, <a tabindex=\"0\" class=\"gwt-Anchor\" target=\"_blank\" href=\"http://code.google.com/p/rtpstat/issues/entry\">¡Cuéntanoslo!</a> ");
          } else {
            notificadorGeneral.info(MSG_PROBLEMA_SALVADO);
            History.newItem("mis_problemas");
          }
        }

        public void onFailure(Throwable caught) {
          notificadorGeneral.warning(MSG_FALLO_COMUNICACION + caught.getMessage());
        }
      });
    } else {
      rtpstatService.updateProblema(p, new AsyncCallback<Void>() {
        public void onSuccess(Void result) {
          notificadorGeneral.info(MSG_PROBLEMA_SALVADO);
          History.newItem("mis_problemas");
        }

        public void onFailure(Throwable caught) {
          notificadorGeneral.warning(MSG_FALLO_COMUNICACION + caught.getMessage());
        }
      });

    }
  }

  private Problema getProblema() {
    Problema out = new Problema();
    if (!editandoNuevoProblema) {
      out.setId(idProblema);
    }
    out.setDescripcion(descripcion.getText());
    out.setDificultad(GradoDificultad.valueOf(dificultad.getValue(dificultad.getSelectedIndex())));
    out.setAreasObjetivo(getAreasObjetivo());
    out.setCabecera(cabecera.getText());
    out.setCabeceraSegunda(cabecera_segunda.getText());
    out.setDatos(editor_datos.getDatos());
    out.setApartados(listado_apartados.getApartados());

    return out;
  }

  private String[] getAreasObjetivo() {
    ArrayList<String> out = new ArrayList<String>();
    for (int i = 0; i < areas_objetivo.getItemCount(); i++) {
      if (areas_objetivo.isItemSelected(i)) {
        out.add(areas_objetivo.getItemText(i));
      }
    }
    return out.toArray(new String[out.size()]);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.user.client.ui.Composite#onAttach()
   */
  @Override
  public void onAttach() {
    super.onAttach();
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        descripcion.setFocus(true);
      }
    });
  }
}
