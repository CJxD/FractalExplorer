package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class JuliaAlgorithm extends FractalAlgorithm {
    
    private static final String name = "Julia";
    protected Complex seed;
    
    public JuliaAlgorithm(Complex seed, int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
        this.seed = seed;
    }
    
    @Override
    public double divergenceRatio(Complex point) {
        Complex current = point;
        
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
