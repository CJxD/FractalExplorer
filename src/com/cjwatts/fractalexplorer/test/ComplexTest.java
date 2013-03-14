package com.cjwatts.fractalexplorer.test;

import com.cjwatts.fractalexplorer.main.util.Complex;

import junit.framework.TestCase;

public class ComplexTest extends TestCase {
    
    public void testParse() {
        Complex c = new Complex(3, 2);
        assertEquals("3.0+2.0i", c.toString());
        
        c = new Complex(15.354, -2.77);
        assertEquals("15.354-2.77i", c.toString());
    }
    
    public void testAdd() {
        Complex c = new Complex(3, 2);
        Complex d = new Complex(15.354, -2.77);
        assertEquals("18.354-0.77i", c.add(d).toString());
    }
    
    public void testComplement() {
        Complex c = new Complex(3, 2);
        assertEquals("3.0-2.0i", c.complement().toString());
        Complex d = new Complex(15.354, -2.77);
        assertEquals("15.354+2.77i", d.complement().toString());
    }
    
    public void testModulus() {
        Complex c = new Complex(188.46662, 15.77764);
        assertEquals("189.12588606056548", "" + c.modulus());
    }
    
    public void testModulusSquared() {
        Complex c = new Complex(1.000011, -19.76);
        assertEquals("391.4576220001211", "" + c.modulusSquared());
    }
    
    public void testRound() {
        Complex c = new Complex(3.43242354, 4.6656);
        assertEquals("3.43+4.67i", c.round(2).toString());
        
        c = new Complex(-0.22543, 199.99999);
        assertEquals("-0.225+200.0i", c.round(3).toString());
    }
    
    public void testSquare() {
        Complex c = new Complex(15.354, -2.77);
        assertEquals("228.07241599999998-85.06116i", c.square().toString());
    }
}
