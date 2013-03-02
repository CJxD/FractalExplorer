package com.cjwatts.fractalexplorer.main.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.algorithms.FractalAlgorithm;
import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class FractalRenderer {
    private double xmin, xmax, ymin, ymax;
    protected int sizeX, sizeY;
    
    protected FractalAlgorithm algorithm;
    private FractalColourScheme scheme = FractalColourScheme.DEFAULT;
    
    public FractalRenderer(FractalAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public abstract BufferedImage render();
    
    /**
     * Calculates the complex Cartesian coordinates for the given pixel
     * 
     * @param x X coordinate relative to top left
     * @param y Y coordinate relative to top left
     * @param width Width of the graph
     * @param height Height of the graph
     */
    public Complex getCartesian(int x, int y) {
        // Move the graph into the centre of the container
        double calcX = x - sizeX / 2;
        double calcY = y - sizeY / 2;
        
        // Scale the axes
        calcX *= (xmax - xmin) / sizeX;
        calcY *= (ymax - ymin) / sizeY;
        
        // Calculate zoom offsets
        // Add the average of the x and y space
        calcX += (xmin + xmax) / 2;
        calcY += (ymin + ymax) / 2;
        
        return new Complex(calcX, calcY);
    }
    
    /**
     * Calculates the colour for the given pixel
     * 
     * @param x X coordinate relative to top left
     * @param y Y coordinate relative to top left
     * @param width Width of the graph
     * @param height Height of the graph
     */
    public Color getPixelColour(int x, int y) {
        // Get complex Cartesian coordinates
        Complex coords = getCartesian(x, y);
        
        // Get the iteration divergence
        Map.Entry<Complex, Integer> d = algorithm.firstDivergence(coords);
        
        // Use Normalised Iteration Count to get a smoother, non-interger
        // gradient
        // See: http://linas.org/art-gallery/escape/escape.html
        double modSquared = d.getKey().modulusSquared();
        Double mu = d.getValue() - (Math.log(Math.log(modSquared))) / Math.log(2);
        
        // Iteration count hit maximum
        if (mu.equals(Double.NaN))
            mu = (double) algorithm.getIterations();
        
        return scheme.calculateColour(mu / algorithm.getIterations());
    }
    
    /**
     * Get the algorithm for the fractal equations
     */
    public FractalAlgorithm getAlgorithm() {
        return this.algorithm;
    }
    
    /**
     * Set the algorithm for the fractal equations
     * @param algorithm
     */
    public void setAlgorithm(FractalAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param point1
     * @param point2
     */
    public void setComplexBounds(Complex point1, Complex point2) {
        setComplexBounds(point1.real(), point2.real(), point1.imaginary(), point2.imaginary());
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param xmin
     * @param xmax
     * @param ymin
     * @param ymax
     */
    public void setComplexBounds(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }
    
    /**
     * Get the dimensions of the render image
     */
    public Dimension getImageSize() {
        return new Dimension(sizeX, sizeY);
    }
    
    /**
     * Set the dimensions of the render image
     * @param d
     */
    public void setImageSize(Dimension d) {
        this.sizeX = d.width;
        this.sizeY = d.height;
    }
    
    /**
     * Get the colour scheme of the fractal pattern
     */
    public FractalColourScheme getColourScheme() {
        return this.scheme;
    }
    
    /**
     * Set the colour scheme of the fractal pattern
     * @param scheme
     */
    public void setColourScheme(FractalColourScheme scheme) {
        this.scheme = scheme;
    }
}
