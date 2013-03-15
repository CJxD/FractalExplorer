package com.cjwatts.fractalexplorer.main.util;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IterationSpinner extends JSpinner {
    
    private static final long serialVersionUID = 1L;
    private static final double STEP_RATIO = 0.1;
    
    private SpinnerNumberModel model;
    
    public IterationSpinner() {
        super();
        // Model setup
        model = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
        this.setModel(model);
        
        // Step recalculation
        this.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                Integer value = Math.abs(getInteger());
                // Steps are sensitive to the base 10 logarithm of the value
                double magnitude = Math.ceil(Math.log10(value));
                int stepSize = (int) Math.ceil(STEP_RATIO * Math.pow(10, magnitude));
                model.setStepSize(stepSize);
            }
        });
    }
    
    /**
     * Returns the current value as an Integer
     */
    public Integer getInteger() {
        return (Integer) getValue();
    }
    
}
