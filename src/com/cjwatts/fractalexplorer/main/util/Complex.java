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
     * @return Modulus of the complex number
     */
    public double modulus() {
        return Math.sqrt(Math.pow(re, 2) + Math.pow(im, 2));
    }
    
    /**
     * @return Squared modulus of the complex number
     */
    public double modulusSquared() {
        return Math.pow(re, 2) + Math.pow(im, 2);
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
        double rNew = Math.pow(re, 2) - Math.pow(im, 2);
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
