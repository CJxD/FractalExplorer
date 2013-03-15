package com.cjwatts.fractalexplorer.main.algorithms;

import com.cjwatts.fractalexplorer.main.util.Complex;

public abstract class FractalAlgorithm {
    
    /**
     * Calculates whether or not the complex number is in the fractal set using the escape time algorithm.
     * 
     * For complex numbers not inside the set, a ratio between
     * first divergence and maximum iteration count [0, 1) is returned.
     * 
     * @param seed
     * @return 0 for instant divergence, 1 for never diverges
     */
    public abstract double escapeTime(Complex seed);
}
