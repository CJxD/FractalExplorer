package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class MandelbrotAlgorithm extends FractalAlgorithm {
    
    private static final String name = "Mandelbrot";
    
    public MandelbrotAlgorithm(int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
    }
    
    @Override
    public double divergenceRatio(Complex seed) {
        Complex current = seed;
        
        // Keep iterating until either n is reached or divergence is found
        int i = 0;
        while (current.modulusSquared() < escapeSquared && i < iterations) {
            // Z(i+1) = (Z(i) * Z(i)) + c
            current = current.square().add(seed);
            i++;
        }
        
        return normalise(current, i);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
