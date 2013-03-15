package com.cjwatts.fractalexplorer.main.algorithms;

import java.util.Set;

import org.reflections.Reflections;

import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class BaseFractalAlgorithm extends FractalAlgorithm {
    
    protected int iterations = 100;
    protected double escapeRadius = 2.0;
    protected double escapeSquared = 4.0;
    
    protected String name;
    
    private static final double LOG_2 = Math.log(2);
    
    public BaseFractalAlgorithm() {
    }
    
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
            // Find an algorithm with a matching name and return a new instance
            // of it
            for (Class<? extends BaseFractalAlgorithm> a : algorithms) {
                BaseFractalAlgorithm instance = a.newInstance();
                if (instance.getName().equals(name)) {
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
    public String getName() {
        return name;
    }
    
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
    
    /*
     * Generated hash code function (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(escapeRadius);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(escapeSquared);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + iterations;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    /*
     * Generated equals function (non-Javadoc)
     * 
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
        BaseFractalAlgorithm other = (BaseFractalAlgorithm) obj;
        if (Double.doubleToLongBits(escapeRadius) != Double.doubleToLongBits(other.escapeRadius))
            return false;
        if (Double.doubleToLongBits(escapeSquared) != Double.doubleToLongBits(other.escapeSquared))
            return false;
        if (iterations != other.iterations)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
