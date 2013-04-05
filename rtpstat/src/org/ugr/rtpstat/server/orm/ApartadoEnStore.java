package org.ugr.rtpstat.server.orm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.Apartado;
import org.ugr.rtpstat.client.orm.ApartadoConCalculos;
import org.ugr.rtpstat.client.orm.ApartadoConSubApartados;
import org.ugr.rtpstat.client.orm.Calculo;

import com.google.appengine.api.datastore.Key;
import com.google.gwt.user.client.rpc.IsSerializable;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class ApartadoEnStore implements IsSerializable{

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String enunciado;

	@Persistent
	private ProblemaEnStore problema;
	
	@Persistent
	private String[] calculos;

	@Persistent(mappedBy ="apartadoPadre")
	private List<ApartadoEnStore> subApartados;

	private ApartadoEnStore apartadoPadre;
	
	public ApartadoEnStore() {

	}

	public ApartadoEnStore(ProblemaEnStore problema, Apartado apartado) {
		if (apartado == null) {
			throw new NullPointerException("El apartado no puede ser null");
		}
		
		if (!apartado.isValid()) {
			throw new IllegalArgumentException("El apartado no es valido");
		}
		
		setProblema(problema);
		if (apartado instanceof ApartadoConCalculos) {
			setCalculos(((ApartadoConCalculos) apartado).getCalculos());
		} else if (apartado instanceof ApartadoConSubApartados) {
			//ArrayList<ApartadoEnStore> nuevos = new ArrayList<ApartadoEnStore>();
			Apartado[] subApartadosRecibidos = ((ApartadoConSubApartados) apartado).getSubApartados();
			for (Apartado a : subApartadosRecibidos) {
				//nuevos.add(new ApartadoEnStore(getProblema(),a));
				if(this.getSubApartados() == null) {
					this.setSubApartados(new LinkedList<ApartadoEnStore>());
				}
				this.getSubApartados().add(new ApartadoEnStore(getProblema(),a));
			}
			//setSubApartados(nuevos);
		} else {
			throw new IllegalArgumentException("Tipo de Apartado desconocido: " + apartado.getClass());
		}

		setEnunciado(apartado.getEnunciado());
	}

	public String getEnunciado() {
		return enunciado;
	}

	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}

	public Calculo[] getCalculos() {
		Calculo[] out = null;
		if (calculos != null) {
			out = new Calculo[calculos.length];
			for (int i = 0; i < calculos.length; i++) {
				out[i] = Calculo.valueOf(calculos[i]);
			}
		}
		return out;
	}

	public void setCalculos(Calculo[] calculos) {
		if (calculos != null && subApartados != null) {
			throw new IllegalStateException("Un apartado tiene cálculos o subapartados, pero no las dos cosas a la vez.");
		}
		this.calculos = null;
		if (calculos != null) {
			this.calculos = new String[calculos.length];
			for (int i = 0; i < calculos.length; i++) {
				this.calculos[i] = calculos[i].toString();
			}
		}
	}

	public List<ApartadoEnStore> getSubApartados() {
		return subApartados;
	}

	public void setSubApartados(List<ApartadoEnStore> subApartados) {
		if (calculos != null && subApartados != null) {
			throw new IllegalStateException("Un apartado tiene cálculos o subapartados, pero no las dos cosas a la vez.");
		}
		this.subApartados = subApartados;
	}

	public Apartado comoApartado() {
		if (calculos == null && subApartados == null) {
			throw new IllegalStateException("Un apartado tiene que tener cálculos o subapartados.");
		}
		Apartado out = null;
		if (calculos != null) {
			//ApartadoConCalculos apartadoConCalculos = ;
			ArrayList<Calculo> calculosParaApartado = new ArrayList<Calculo>();
			for (int i = 0; i < calculos.length; i++) {
				calculosParaApartado.add(Calculo.valueOf(calculos[i]));
			}
			out = new ApartadoConCalculos(enunciado,calculosParaApartado.toArray(new Calculo[calculosParaApartado.size()]));
		} else {
			ArrayList<Apartado> subApartadosParaRPC = new ArrayList<Apartado>();
			for (ApartadoEnStore subApartado : subApartados) {
				subApartadosParaRPC.add(subApartado.comoApartado());
			}
			out = new ApartadoConSubApartados(enunciado,subApartadosParaRPC.toArray(new Apartado[subApartadosParaRPC.size()]));
		}
		return out;
	}

	public Key getKey() {
		return key;
	}

	public void setProblema(ProblemaEnStore problema) {
		this.problema = problema;
	}

	public ProblemaEnStore getProblema() {
		return problema;
	}

	public void setApartadoPadre(ApartadoEnStore apartadoPadre) {
		this.apartadoPadre = apartadoPadre;
	}

	public ApartadoEnStore getApartadoPadre() {
		return apartadoPadre;
	}

}
