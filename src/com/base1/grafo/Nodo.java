package com.base1.grafo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Nodo {
	private ArrayList<Eje> ejes;
	private String caracteristica;
	private Boolean visitado;
	private Boolean hoja;

	public Nodo(String caracteristica) {
		this.setCaracteristica(caracteristica);
		ejes = new ArrayList<Eje>();
		this.setVisitado(false);
		this.setHoja(false);
	}

	public void setEje(Nodo destino, String valor, int peso, String pregunta) {
		ejes.add(new Eje(destino, valor, peso, pregunta));
	}

	/**
	 * Agrega un eje con un destino y sus valores
	 * 
	 * @param cDestino
	 * @param valor
	 * @param peso
	 * @param pregunta
	 */
	public void setEje(String cDestino, String valor, int peso, String pregunta) {
		setEje(new Nodo(cDestino), valor, peso, pregunta);
	}

	/**
	 * Devuelve el un hijo por su caracteristica
	 * 
	 * @param caracteristica
	 * @return
	 */
	public Nodo getNodo(String caracteristica) {
		for (Eje eje : ejes) {
			if (eje.getDestino().getCaracteristica().equals(caracteristica)) {
				return eje.getDestino();
			}
		}
		return null;
	}
	
	public Nodo getNodo(String valor, String caracteristica)
	{
		for (Eje eje : ejes) {
			if (eje.getDestino().getCaracteristica().equals(caracteristica) && eje.getValor().equals(valor)) {
				return eje.getDestino();
			}
		}
		return null;
	}
	/**
	 * 1 - Crea una array auxiliar de ejes 2 - A este array le voy agregar una
	 * cantidad de ejes relativa al peso de cada eje 3 - De este array voy a
	 * sacar un eje de forma aleatoria;
	 * 
	 * Notas, no es un metodo muy eficiente si hay grandes cantidades de
	 * caracteristicas
	 * 
	 * @return
	 */
	public Eje getRandEje() {
		ArrayList<Eje> ejesPorPeso = new ArrayList<Eje>();

		for (Eje eje : this.ejes) {
			int pesoAux = eje.getPeso();
			while (pesoAux != 0) {
				ejesPorPeso.add(eje);
				pesoAux--;
			}
		}

		int eppSize = ejesPorPeso.size();
		if (eppSize != 0) {
			return this.getRandEje(ejesPorPeso);
		}

		return null;
	}

	private Eje getRandEje(ArrayList<Eje> ejes) {
		int index = new Random().nextInt(ejes.size());
		return ejes.get(index);
	}

	// public void armarGrafo(ArrayList<CaracteristicaGrafo> csg) {
	// if (csg.size() == 0) {
	// return;
	// }
	//
	// CaracteristicaGrafo caracAux = csg.remove(0);
	// this.setCaracteristica(caracAux.getCaracteristica());
	// for (Valor valor : caracAux.getValores()) {
	// Collections.shuffle(csg);
	// Nodo destino = new Nodo("");
	// this.setEje(destino,valor.getEtiqueta(), valor.getPeso(),
	// valor.getPregunta());
	// destino.armarGrafo(csg);
	// }
	// }

	public void armarArbolD(ArrayList<CaracteristicaGrafo> csg) {
		if (csg.size() == 0) {
			return;
		}

		CaracteristicaGrafo cgAux = this.removeRandCG(csg);
		this.setCaracteristica(cgAux.getCaracteristica());
		for (Valor valor : cgAux.getValores()) {
			ArrayList<CaracteristicaGrafo> csgAux = new ArrayList<>(csg);
			Collections.shuffle(csgAux);
			String cDestino = csgAux.get(0).getCaracteristica();
			String sValor = valor.getEtiqueta();
			this.setEje(cDestino ,sValor, valor.getPeso(),valor.getPregunta());
			this.getNodo(sValor, cDestino).armarArbolD(csgAux);
		}
	}

	private CaracteristicaGrafo removeRandCG(
			ArrayList<CaracteristicaGrafo> array) {

		int index = new Random().nextInt(array.size());
		return array.remove(index);
	}

	/**
	 * @return the visitado
	 */
	public Boolean getVisitado() {
		return visitado;
	}

	/**
	 * @param visitado
	 *            the visitado to set
	 */
	public void setVisitado(Boolean visitado) {
		this.visitado = visitado;
	}

	/**
	 * @return the caracteristica
	 */
	public String getCaracteristica() {
		return caracteristica;
	}

	/**
	 * @param caracteristica
	 *            the caracteristica to set
	 */
	public void setCaracteristica(String caracteristica) {
		this.caracteristica = caracteristica;
	}

	public ArrayList<Eje> getEjes() {
		return this.ejes;
	}

	public Boolean getHoja() {
		return hoja;
	}

	public void setHoja(Boolean hoja) {
		this.hoja = hoja;
	}
}
