package org.ugr.rtpstat.client.orm;

public class ApartadoConSubApartados extends Apartado {
	public ApartadoConSubApartados(String enunciado,Apartado[] apartados) {
		super(enunciado);
		this.setSubApartados(apartados);
	}
	private static final long serialVersionUID = 3557706366662985499L;

	private Apartado [] subApartados;
	
	@Override
	public boolean isValid() {
		boolean valid = super.isValid();
		if(valid) {
			valid = subApartados != null && subApartados.length > 0;
			if(valid) {
				for(Apartado a: subApartados) {
					valid = a.isValid();
					if(!valid) {
						break;
					}
				}
			}
		}
		return valid;
	}
	public void setSubApartados(Apartado[] apartados) {
		this.subApartados = apartados;
	}
	public Apartado [] getSubApartados() {
		return subApartados;
	}

}
