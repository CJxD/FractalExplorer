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

	/*
	 * Generated hash code function
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((seed == null) ? 0 : seed.hashCode());
		return result;
	}

	/*
	 * Generated equals function
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JuliaAlgorithm other = (JuliaAlgorithm) obj;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (seed == null) {
			if (other.seed != null)
				return false;
		} else if (!seed.equals(other.seed))
			return false;
		return true;
	}
}
