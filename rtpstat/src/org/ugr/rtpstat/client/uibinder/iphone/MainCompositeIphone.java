package org.ugr.rtpstat.client.uibinder.iphone;

import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.Permiso;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.Rol;
import org.ugr.rtpstat.client.uibinder.MainCompositeBase;
import org.ugr.rtpstat.client.uibinder.UserInfo;
import org.ugr.rtpstat.client.uibinder.admin.AutorizarUsuarios;
import org.ugr.rtpstat.client.uibinder.libros.EditorLibro;
import org.ugr.rtpstat.client.uibinder.libros.MisLibros;
import org.ugr.rtpstat.client.uibinder.principal.BotonNavegacion;
import org.ugr.rtpstat.client.uibinder.principal.Instrucciones;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.principal.Instrucciones.Mensaje;
import org.ugr.rtpstat.client.uibinder.problemas.EditorProblema;
import org.ugr.rtpstat.client.uibinder.problemas.MisProblemas;
import org.ugr.rtpstat.client.uibinder.relaciones.EditorRelacionEjercicios;
import org.ugr.rtpstat.client.uibinder.relaciones.GeneradorRelacionEjercicios;
import org.ugr.rtpstat.client.uibinder.relaciones.ListaRelacionesEjercicios;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainCompositeIphone extends MainCompositeBase {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	@UiField
	Panel panel_principal;

	@UiField
	Panel panel_centro;

	@UiField(provided = true)
	Image logotipo;

	@UiField
	BotonNavegacion boton_crear_problema;

	@UiField
	BotonNavegacion boton_mis_problemas;

	@UiField
	BotonNavegacion boton_autorizar_usuarios;

	@UiField
	BotonNavegacion boton_relaciones_ejercicios;

	@UiField
	BotonNavegacion boton_mis_libros;

	@UiField(provided = true)
	UserInfo userInfo;

	@UiField
	VerticalPanel sidebar_izq;

	@UiField
	HorizontalPanel cabecera;

	private NotificadorGeneral notificadorGeneral;
	private static MainCompositeIphoneUiBinder uiBinder = GWT.create(MainCompositeIphoneUiBinder.class);

	interface MainCompositeIphoneUiBinder extends UiBinder<Widget, MainCompositeIphone> {
	}
	public MainCompositeIphone() {
		notificadorGeneral = new NotificadorGeneral();
		RootPanel.get().add(notificadorGeneral);
		userInfo = new UserInfo(notificadorGeneral);
		logotipo = new Image(Imagenes.INSTANCE.logoSmall());

		initWidget(uiBinder.createAndBindUi(this));
		cabecera.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		OrientationChangeService.registerOrientationChangedHandler(new OrientationChangeEventHandler() {
			@Override
			public void onOrientationChange(String orientation) {
				if("portrait".equals(orientation)) {
					panel_principal.setWidth("320px");	
				} else if("landscape".equals(orientation)) {
					panel_principal.setWidth("48px");	
				}
			}
		});	
	}

	@Override
	public void limpiarPorRol() {
		rtpstatService.currentUserRol(new AsyncCallback<Rol>() {
			public void onSuccess(Rol result) {
				List<Permiso> permisos = result.permisos();
				if (!permisos.contains(Permiso.MisProblemas)) {
					boton_crear_problema.removeFromParent();
					boton_mis_problemas.removeFromParent();
				}
				if (!permisos.contains(Permiso.AutorizarUsuarios)) {
					boton_autorizar_usuarios.removeFromParent();
				}
				if (!permisos.contains(Permiso.MisRelaciones)) {
					boton_relaciones_ejercicios.removeFromParent();
				}
				if (!permisos.contains(Permiso.MisLibros)) {
					boton_mis_libros.removeFromParent();
				}
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Problema de comunicación con el servidor. Recarge la página.");
			}
		});
	}

	@Override
	public void cambiarEstado(final String token, final ESTADO nuevoEstado) {
		rtpstatService.currentUserRol(new AsyncCallback<Rol>() {
			public void onSuccess(Rol rol) {
				if (Rol.UsuarioNuevo.equals(rol)) {
					Instrucciones i = new Instrucciones(Instrucciones.Mensaje.USUARIO_NO_AUTORIZADO);
					panel_centro.add(i);
				}
				boolean cambioValido = false;
				switch (nuevoEstado) {
				case AUTORIZAR_USUARIOS:
					if (rol.permisos().contains(Permiso.AutorizarUsuarios)) {
						cambioValido = true;
					}
					break;
				case EDITAR_PROBLEMA:
				case CREAR_PROBLEMA:
				case MIS_PROBLEMAS:
					if (rol.permisos().contains(Permiso.MisProblemas)) {
						cambioValido = true;
					}
					break;
				case INICIO:
					cambioValido = true;
					break;
				case EDITAR_USUARIO:
					if (rol.permisos().contains(Permiso.EditarUsuario)) {
						cambioValido = true;
					}
					break;
				case GESTIONAR_RELACIONES_EJERCICIOS:
				case NUEVA_RELACION_EJERCICIOS:
				case EDITAR_RELACION_EJERCICIOS:
					if (rol.permisos().contains(Permiso.MisRelaciones)) {
						cambioValido = true;
					}
					break;
				case MIS_LIBROS:
				case NUEVO_LIBRO:
				case EDITAR_LIBRO:
					if (rol.permisos().contains(Permiso.MisLibros)) {
						cambioValido = true;
					}
					break;
				default:;
				}
				if (cambioValido) {
					_cambiarEstado(token, nuevoEstado);
				} else {
					notificadorGeneral.warning("No tiene permiso para acceder a esa acción");
				}
			}

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void _cambiarEstado(final String token, ESTADO nuevoEstado) {
		// notificadorGeneral.info(nuevoEstado.toString());
		panel_centro.clear();
		userInfo.refresh();
		switch (nuevoEstado) {
		case AUTORIZAR_USUARIOS:
			GWT.runAsync(new RunAsyncCallback() {

				public void onSuccess() {
					AutorizarUsuarios a = new AutorizarUsuarios(notificadorGeneral);
					panel_centro.add(a);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case CREAR_PROBLEMA:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					EditorProblema ed = new EditorProblema(notificadorGeneral);
					panel_centro.add(ed);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case MIS_PROBLEMAS:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					MisProblemas mp = new MisProblemas(notificadorGeneral);
					panel_centro.add(mp);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case FEED_OFICIAL:
			break;
		case INICIO:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					Instrucciones i = new Instrucciones(Mensaje.INSTRUCCIONES_INICIALES);
					panel_centro.add(i);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});

			break;
		case EDITAR_USUARIO:
			notificadorGeneral.warning("¡¡No implementado aun!!");
			break;
		case EDITAR_PROBLEMA:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					if (token != null) {
						String[] partes = token.split("-");
						if (partes.length == 2) {
							String idString = partes[1];
							try {
								long id = Long.parseLong(idString);
								rtpstatService.getProblema(id, new AsyncCallback<Problema>() {
									public void onSuccess(Problema result) {
										if (result == null) {
											notificadorGeneral.warning("Lo siento, no puedo descargar ese problema");
										} else {
											EditorProblema ed = new EditorProblema(notificadorGeneral);
											ed.setProblema(result);
											panel_centro.add(ed);
										}
									}

									public void onFailure(Throwable caught) {
										notificadorGeneral.warning("Ha fallado: " + caught.getMessage());
									}
								});
							} catch (NumberFormatException nfe) {
								notificadorGeneral.warning("Ha fallado: " + nfe.getMessage());
							}
						} else {
							notificadorGeneral.warning("Ha fallado, id invalido");
						}
					} else {
						notificadorGeneral.warning("Token not set!!");
					}
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});

			break;
		case GESTIONAR_RELACIONES_EJERCICIOS:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					ListaRelacionesEjercicios rel = new ListaRelacionesEjercicios(notificadorGeneral);
					panel_centro.add(rel);

				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case NUEVA_RELACION_EJERCICIOS:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					GeneradorRelacionEjercicios edr = new GeneradorRelacionEjercicios(notificadorGeneral);
					panel_centro.add(edr);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case EDITAR_RELACION_EJERCICIOS:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					// token.matches("^editar_relacion_(nueva_){0,1}[0-9]+$")
					// TODO boolean relacionNuevecita = token.indexOf("nueva_")
					// != -1;
					String[] partes = token.split("_");
					int id = Integer.parseInt(partes[partes.length - 1]);
					EditorRelacionEjercicios editor = new EditorRelacionEjercicios(id, notificadorGeneral);
					panel_centro.add(editor);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case MIS_LIBROS:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					MisLibros misLibros = new MisLibros(notificadorGeneral);
					panel_centro.add(misLibros);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case NUEVO_LIBRO:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					EditorLibro editorLibro = new EditorLibro(notificadorGeneral);
					panel_centro.add(editorLibro);
				}

				public void onFailure(Throwable reason) {

				}
			});
			break;
		case EDITAR_LIBRO:
			GWT.runAsync(new RunAsyncCallback() {
				public void onSuccess() {
					// token.matches("^editar_libro[0-9]+$")
					String[] partes = token.split("_");
					Long id = Long.parseLong(partes[partes.length - 1]);
					EditorLibro editor = new EditorLibro(id, notificadorGeneral);
					panel_centro.add(editor);
				}

				public void onFailure(Throwable reason) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

	@Override
	public void limpiarPrincipal() {
		panel_centro.clear();
	}
}
