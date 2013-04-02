package org.ugr.rtpstat.client.uibinder;

public interface FloatTextBoxHandler {

	void insertarUno(FloatTextBox despuesDe);
	void insertarVarios(FloatTextBox floatTextBox);
	
	void eliminar(FloatTextBox box);
	void eliminarTodos(boolean pedirConfirmacion);
	
	void validar();
}
