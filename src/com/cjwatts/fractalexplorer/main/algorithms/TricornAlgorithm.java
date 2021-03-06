package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class TricornAlgorithm extends BaseFractalAlgorithm {
    
    private static String name = "Tricorn";
    
    public TricornAlgorithm() {
        super();
    }
    
    public TricornAlgorithm(int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
    }
    
    @Override
    public double escapeTime(Complex point, Complex seed) {
        // Keep iterating until either n is reached or divergence is found
        int i = 0;
        while (point.modulusSquared() < escapeSquared && i < iterations) {
            // Z(i+1) = complement(Z(i) * Z(i)) + c
            point = point.complement().square().add(seed);
            i++;
        }
        
        return normalise(point, i);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
