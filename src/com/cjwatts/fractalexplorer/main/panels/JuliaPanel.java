package com.cjwatts.fractalexplorer.main.panels;

import com.cjwatts.fractalexplorer.main.algorithms.FractalAlgorithm;
import com.cjwatts.fractalexplorer.main.algorithms.JuliaAlgorithm;

public class JuliaPanel extends FractalPanel {
    
    private static final long serialVersionUID = 1L;
    
    public JuliaPanel(JuliaAlgorithm algorithm) {
        super(algorithm);
    }
    
    @Override
    public JuliaAlgorithm getAlgorithm() {
        return (JuliaAlgorithm) super.getAlgorithm();
    }
    
    @Override
    public void setAlgorithm(FractalAlgorithm algorithm) {
        this.setAlgorithm((JuliaAlgorithm) algorithm);
    }
    
    public void setAlgorithm(JuliaAlgorithm algorithm) {
        super.setAlgorithm(algorithm);
    }
    
}
