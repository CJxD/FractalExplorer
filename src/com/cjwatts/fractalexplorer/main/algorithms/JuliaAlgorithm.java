package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class JuliaAlgorithm extends FractalAlgorithm {
	
    protected BaseFractalAlgorithm base;
    protected Complex seed;
    
    /**
     * Create a new Julia computation based on another fractal algorithm
     * @param base
     */
    public JuliaAlgorithm(BaseFractalAlgorithm base, Complex seed) {
    	super();
    	this.base = base;
    	this.seed = seed;
    }
    
    @Override
    public double escapeTime(Complex point) {
        return base.escapeTime(point, seed);
    }

    /**
     * @return Seed of the Julia set
     */
    public Complex getSeed() {
		return seed;
	}

    /**
     * @param seed Seed of the Julia set
     */
	public void setSeed(Complex seed) {
		this.seed = seed;
	}

	/**
     * @return Base algorithm for this Julia set
     */
	public BaseFractalAlgorithm getBase() {
		return base;
	}

	/**
	 * @param base Algorithm to set as the base for this Julia set
	 */
	public void setBase(BaseFractalAlgorithm base) {
		this.base = base;
	}
    
}
