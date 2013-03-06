package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class FractalAlgorithm {
    
    protected int iterations;
    protected double escapeRadius;
    protected double escapeSquared;
    
    private static final double LOG_2 = Math.log(2);
    
    public FractalAlgorithm(int iterations, double escapeRadius) {
        setIterations(iterations);
        setEscapeRadius(escapeRadius);
    }
    
    /**
     * Calculates whether or not the complex number is in the fractal set.
     * 
     * For complex numbers not inside the set, a ratio between
     * first divergence and maximum iteration count [0, 1) is returned.
     * 
     * @param seed
     * @return 0 for instant divergence, 1 for never diverges
     */
    public abstract double divergenceRatio(Complex seed);
    
    /**
     * Helper method for finding the Normalisation Iteration Count.
     * This enables a smooth gradient for the divergence ratio
     * 
     * @param c The diverging complex number
     * @param d The divergence iteration count
     * 
     * @see <a href="http://linas.org/art-gallery/escape/escape.html">http://linas.org/art-gallery/escape/escape.html</a>
     */
    protected double normalise(Complex c, int d) {
        if (d == iterations) {
        	return iterations;
        } else {
        	double modSquared = c.modulusSquared();
        	return d - (Math.log(Math.log(modSquared))) / (iterations * LOG_2);
        }
    }
    
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
