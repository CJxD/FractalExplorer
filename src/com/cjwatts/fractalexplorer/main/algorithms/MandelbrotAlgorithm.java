package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class MandelbrotAlgorithm extends BaseFractalAlgorithm {
    
    private static String name = "Mandelbrot";
    
    public MandelbrotAlgorithm() {
        super();
    }
    
    public MandelbrotAlgorithm(int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
    }
    
    @Override
    public double escapeTime(Complex point, Complex seed) {
        // Keep iterating until either n is reached or divergence is found
        int i = 0;
        while (point.modulusSquared() < escapeSquared && i < iterations) {
            // Z(i+1) = (Z(i) * Z(i)) + c
            point = point.square().add(seed);
            i++;
        }
        
        return normalise(point, i);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
