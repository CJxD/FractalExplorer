package com.cjwatts.fractalexplorer.main.algorithms;

import java.util.AbstractMap;
import java.util.Map;

import com.cjwatts.fractalexplorer.main.util.Complex;

public class BurningShipAlgorithm extends FractalAlgorithm {
    
    private static final String name = "Burning Ship";
    
    public BurningShipAlgorithm(int iterations, double escapeRadius) {
        super(iterations, escapeRadius);
    }
    
    @Override
    public Map.Entry<Complex, Integer> firstDivergence(Complex seed) {
        Complex current = seed;
        
        // Keep iterating until either n is reached or divergence is found
        int i = 0;
        while (current.modulusSquared() < escapeSquared && i < iterations) {
            // Z(i+1) = (|ReZ(i)| * i|ImZ(i)|) + c
            double newReal = Math.abs(current.real());
            double newIm = Math.abs(current.imaginary());
            Complex d = new Complex(newReal, newIm);
            current = d.square().add(seed);
            i++;
        }
        
        return new AbstractMap.SimpleEntry<Complex, Integer>(current, i);
    }
    
    @Override
    public String getName() {
        return name;
    }
    
}
