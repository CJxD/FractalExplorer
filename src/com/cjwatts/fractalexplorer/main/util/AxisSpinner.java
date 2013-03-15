package com.cjwatts.fractalexplorer.main.util;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AxisSpinner extends JSpinner {
    
    private static final long serialVersionUID = 1L;
    private static final double STEP_RATIO = 0.1;
    
    private SpinnerNumberModel model;
    
    public AxisSpinner() {
        super();
        // Model setup
        model = new SpinnerNumberModel(0.0, -10.0, 10.0, 0.1);
        this.setModel(model);
        
        // Step recalculation
        this.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                Double value = Math.abs(getDouble());
                // Steps are sensitive to the current value
                model.setStepSize(STEP_RATIO * value);
            }
        });
    }
    
    /**
     * Returns the current value as a Double
     */
    public Double getDouble() {
        return (Double) getValue();
    }
    
}
