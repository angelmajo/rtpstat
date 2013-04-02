package org.ugr.rtpstat.client.uibinder;

import com.google.gwt.user.client.ui.Composite;

public abstract class MainCompositeBase extends Composite {
	public enum ESTADO {
		INICIO, MIS_PROBLEMAS, CREAR_PROBLEMA, EDITAR_USUARIO, EDITAR_PROBLEMA, FEED_OFICIAL, AUTORIZAR_USUARIOS, GESTIONAR_RELACIONES_EJERCICIOS, NUEVA_RELACION_EJERCICIOS, EDITAR_RELACION_EJERCICIOS, MIS_LIBROS, NUEVO_LIBRO, EDITAR_LIBRO
	}
	
	public MainCompositeBase() {
		super();
	}

	public abstract void cambiarEstado(final String token, final ESTADO nuevoEstado);

	public abstract void limpiarPorRol();

	public abstract void limpiarPrincipal();

}