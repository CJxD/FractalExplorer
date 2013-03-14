package com.cjwatts.fractalexplorer.main.util;

/**
 * Class representing complex numbers - immutable
 */
public final class Complex {
    
    private final double re;
    private final double im;
    
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    /**
     * @return The complex sum when complex number d is added
     * @param d
     */
    public Complex add(Complex d) {
        return new Complex(re + d.real(), im + d.imaginary());
    }
    
    /**
     * @return The complex conjugate
     */
    public Complex complement() {
        return new Complex(re, -im);
    }
    
    /**
     * @return Modulus of the complex number
     */
    public double modulus() {
        return Math.sqrt(re * re + im * im);
    }
    
    /**
     * @return Squared modulus of the complex number
     */
    public double modulusSquared() {
        return re * re + im * im;
    }
    
    /**
     * @return Rounded complex number
     */
    public Complex round(int precision) {
        long scaleFactor = (long) Math.pow(10, precision);
        // Shift up 10^n places, round, then shift back down again
        double rNew = ((double) Math.round(re * scaleFactor)) / scaleFactor;
        double iNew = ((double) Math.round(im * scaleFactor)) / scaleFactor;
        return new Complex(rNew, iNew);
    }
    
    /**
     * @return Square of the complex number
     */
    public Complex square() {
    	// Real = a^2 - b^2
    	// Im = 2ab
        double rNew = re * re - im * im;
        double iNew = 2 * (re * im);
        return new Complex(rNew, iNew);
    }
    
    @Override
    public String toString() {
        String sign = im < 0 ? "" : "+";
        return re + sign + im + "i";
    }
    
    /**
     * @return Real part of the complex number
     */
    public double real() {
        return re;
    }
    
    /**
     * @return Imaginary part of the complex number
     */
    public double imaginary() {
        return im;
    }
}
