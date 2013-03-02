package com.cjwatts.fractalexplorer.main.algorithms;

import java.util.Map;

import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class FractalAlgorithm {
    
    protected int iterations;
    protected double escapeRadius;
    protected double escapeSquared;
    
    public FractalAlgorithm(int iterations, double escapeRadius) {
        setIterations(iterations);
        setEscapeRadius(escapeRadius);
    }
    
    /**
     * Calculates the first divergence in the fractal
     * set for a given complex number.
     * 
     * @param seed
     * @return First diverging complex number in set and its iteration count
     */
    public abstract Map.Entry<Complex, Integer> firstDivergence(Complex seed);
    
    /**
     * @return Name of the algorithm
     */
    public abstract String getName();
    
    public int getIterations() {
        return this.iterations;
    }
    
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
    
    public double getEscapeRadius() {
        return this.escapeRadius;
    }
    
    public void setEscapeRadius(double escapeRadius) {
        this.escapeRadius = escapeRadius;
        this.escapeSquared = Math.pow(escapeRadius, 2);
    }
}
