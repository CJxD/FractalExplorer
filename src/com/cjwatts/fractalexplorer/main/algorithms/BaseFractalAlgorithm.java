package com.cjwatts.fractalexplorer.main.algorithms;

import java.util.Set;

import org.reflections.Reflections;

import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class BaseFractalAlgorithm extends FractalAlgorithm {
	
	protected int iterations = 100;
    protected double escapeRadius = 2.0;
    protected double escapeSquared = 4.0;
    
    private static final double LOG_2 = Math.log(2);
    
    public BaseFractalAlgorithm() {}
    
    public BaseFractalAlgorithm(int iterations, double escapeRadius) {
        setIterations(iterations);
        setEscapeRadius(escapeRadius);
    }
    
    /**
     * Convenience method to return all known FractalAlgorithms
     */
    public static Set<Class<? extends BaseFractalAlgorithm>> getSubClasses() {
        // Get a list of all classes extending this in the same package
        Reflections reflections = new Reflections("com.cjwatts.fractalexplorer.main.algorithms");    
        return reflections.getSubTypesOf(BaseFractalAlgorithm.class);
    }
    
    /**
     * Convenience method to return a known FractalAlgorithm with the given name
     * @param name Name of the algorithm as specified by algorithm.getName()
     */
    public static BaseFractalAlgorithm getByName(String name) {
        Set<Class<? extends BaseFractalAlgorithm>> algorithms = getSubClasses();
        
        BaseFractalAlgorithm result = null;
        try {
            // Find an algorithm with a matching name and return a new instance of it
            for (Class<? extends BaseFractalAlgorithm> a : algorithms) {
                BaseFractalAlgorithm instance = a.newInstance();
                if (instance.getName() == name) {
                    result = instance;
                }
            }
        } catch (Exception ex) {
            System.err.println("Unable to check for other algorithms: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }
    
    public double escapeTime(Complex point) {
    	return escapeTime(point, point);
    }
    
    /**
     * Calculates normalised escape time [0, 1) for a given point and constant (seed)
     * 
     * @param point
     * @param seed
     * @return 0 for instant divergence, 1 for never diverges
     */
    protected abstract double escapeTime(Complex point, Complex seed);
    
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
        } else if (d == 0) {
        	return 0;
        } else {
        	double modSquared = c.modulusSquared();
        	return (d - (Math.log(Math.log(modSquared))) / LOG_2) / (iterations);
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
