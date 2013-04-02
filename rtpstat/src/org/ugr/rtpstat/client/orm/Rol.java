package org.ugr.rtpstat.client.orm;

import java.util.Arrays;
import java.util.List;

public enum Rol {
	Administrador, UsuarioAvanzado, UsuarioNuevo, UsuarioNormal;

	public final List<Permiso> permisos() {
		List<Permiso> out = null;
		switch (this) {
		case Administrador:
			out = Arrays.asList(Permiso.values());
			break;
		case UsuarioNuevo:
			out = Arrays.asList(new Permiso[] { Permiso.MisProblemas });
			break;
		case UsuarioNormal:
			out = Arrays.asList(new Permiso[] { Permiso.MisProblemas, Permiso.MisRelaciones, Permiso.MisLibros });
			break;
		case UsuarioAvanzado:
			out = Arrays.asList(new Permiso[] { Permiso.MisProblemas, Permiso.MisRelaciones, Permiso.MisLibros, Permiso.AutorizarUsuarios });
			break;
		}
		return out;
	}

	public String toLongString() {
		String out = null;
		switch (this) {
		case UsuarioAvanzado:
			out = "Usuario avanzado";
			break;
		case UsuarioNuevo:
			out = "Usuario nuevo (no autorizado)";
			break;
		case UsuarioNormal:
			out = "Usuario normal";
			break;
		case Administrador:
			out = "Administrador";
			break;
		default:
			throw new IllegalStateException();
		}
		return out;
	}
}
