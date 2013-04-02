package org.ugr.rtpstat.server.orm;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.ugr.rtpstat.client.orm.Datos;
import org.ugr.rtpstat.client.orm.Datos.TipoAmplitudIntervalos;
import org.ugr.rtpstat.client.orm.Datos.TipoVariable;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DatosEnStore /*extends Datos*/ {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key key;
	@Persistent
	private TipoVariable tipoVariable;
	@Persistent
	private TipoAmplitudIntervalos tipoAmplitudIntervalos;
	@Persistent
	private String descripcionVariable;
	@Persistent
	private int decimales;
	@Persistent
	private float[] valores;
	@Persistent
	private float amplitudIntervalos;
	@Persistent
	private float[] extremosInferiores;
	@Persistent
	private float[] extremosSuperiores;
	@Persistent
	private float[] frecuenciasAbsolutas;

	public DatosEnStore(Datos datos) {
		this.setAmplitudIntervalos(datos.getAmplitudIntervalos());
		this.setDecimales(datos.getDecimales());
		this.setDescripcionVariable(datos.getDescripcionVariable());
		this.setExtremosInferiores(datos.getExtremosInferiores());
		this.setExtremosSuperiores(datos.getExtremosSuperiores());
		this.setFrecuenciasAbsolutas(datos.getFrecuenciasAbsolutas());
		this.setTipoAmplitudIntervalos(datos.getTipoAmplitudIntervalos());
		this.setTipoVariable(datos.getTipoVariable());
		this.setValores(datos.getValores());
	}

	//@Override
	public float[] getFrecuenciasAbsolutas() {
		return frecuenciasAbsolutas;
	}

	//@Override
	public void setFrecuenciasAbsolutas(float[] frecuenciasAbsolutas) {
		this.frecuenciasAbsolutas = frecuenciasAbsolutas;
	}

	//@Override
	public void setExtremosSuperiores(float[] extremosSuperiores) {
		this.extremosSuperiores = extremosSuperiores;
	}

	//@Override
	public float[] getExtremosSuperiores() {
		return extremosSuperiores;
	}

	//@Override
	public void setExtremosInferiores(float[] extremosInferiores) {
		this.extremosInferiores = extremosInferiores;
	}

	//@Override
	public float[] getExtremosInferiores() {
		return extremosInferiores;
	}

	//@Override
	public float getAmplitudIntervalos() {
		return amplitudIntervalos;
	}

	//@Override
	public void setAmplitudIntervalos(float amplitudIntervalos) {
		this.amplitudIntervalos = amplitudIntervalos;
	}

	//@Override
	public float[] getValores() {
		return valores;
	}

	//@Override
	public void setValores(float[] valores) {
		this.valores = valores;
	}

	//@Override
	public void setTipoAmplitudIntervalos(TipoAmplitudIntervalos tipoAmplitudIntervalos) {
		this.tipoAmplitudIntervalos = tipoAmplitudIntervalos;
	}

	//@Override
	public TipoAmplitudIntervalos getTipoAmplitudIntervalos() {
		return tipoAmplitudIntervalos;
	}

	//@Override
	public void setDecimales(int decimales) {
		this.decimales = decimales;
	}

	//@Override
	public int getDecimales() {
		return decimales;
	}

	//@Override
	public void setDescripcionVariable(String descripcionVariable) {
		this.descripcionVariable = descripcionVariable;
	}
	//@Override
	public String getDescripcionVariable() {
		return descripcionVariable;
	}
	//@Override
	public void setTipoVariable(TipoVariable tipoVariable) {
		this.tipoVariable = tipoVariable;
	}
	//@Override
	public TipoVariable getTipoVariable() {
		return tipoVariable;
	}

	public Datos comoDatos() {
		Datos out = new Datos();
		out.setAmplitudIntervalos(this.getAmplitudIntervalos());
		out.setDecimales(this.getDecimales());
		out.setDescripcionVariable(this.getDescripcionVariable());
		out.setExtremosInferiores(this.getExtremosInferiores());
		out.setExtremosSuperiores(this.getExtremosSuperiores());
		out.setFrecuenciasAbsolutas(this.getFrecuenciasAbsolutas());
		out.setTipoAmplitudIntervalos(this.getTipoAmplitudIntervalos());
		out.setTipoVariable(this.getTipoVariable());
		out.setValores(this.getValores());
		return out;
	}
}
