package com.cjwatts.fractalexplorer.main.io;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.algorithms.BaseFractalAlgorithm;
import com.cjwatts.fractalexplorer.main.util.Complex;

public class Favourite implements Comparable<Favourite> {

	private String name;
	private BaseFractalAlgorithm algorithm;
	private Complex selected;
	private FractalColourScheme scheme;
	
	/**
	 * Mark details of a favourite fractal for later
	 * 
	 * @param name
	 * @param algorithm
	 * @param selected
	 * @param scheme
	 */
	public Favourite(
			String name,
			BaseFractalAlgorithm algorithm,
			Complex selected,
			FractalColourScheme scheme) {
		this.name = name;
		this.algorithm = algorithm;
		this.selected = selected;
		this.scheme = scheme;
	}
	
	@Override
	public int compareTo(Favourite o) {
		return this.name.compareTo(o.name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BaseFractalAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(BaseFractalAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	public Complex getSelected() {
		return selected;
	}

	public void setSelected(Complex selected) {
		this.selected = selected;
	}

	public FractalColourScheme getScheme() {
		return scheme;
	}

	public void setScheme(FractalColourScheme scheme) {
		this.scheme = scheme;
	}

}
