package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class BurningShipAlgorithm extends BaseFractalAlgorithm {
    
    private static final String name = "Burning Ship";
    
    public BurningShipAlgorithm() {
        super();
    }
    
    public BurningShipAlgorithm(int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
    }
    
    @Override
    public double escapeTime(Complex point, Complex seed) {
        // Keep iterating until either n is reached or divergence is found
        int i = 0;
        while (point.modulusSquared() < escapeSquared && i < iterations) {
            // Z(i+1) = (|ReZ(i)| * i|ImZ(i)|) + c
            double newReal = Math.abs(point.real());
            double newIm = Math.abs(point.imaginary());
            Complex d = new Complex(newReal, newIm);
            point = d.square().add(seed);
            i++;
        }
        
        return normalise(point, i);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
