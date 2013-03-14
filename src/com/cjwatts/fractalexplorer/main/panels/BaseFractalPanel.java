package com.cjwatts.fractalexplorer.main.panels;

import com.cjwatts.fractalexplorer.main.algorithms.FractalAlgorithm;
import com.cjwatts.fractalexplorer.main.algorithms.BaseFractalAlgorithm;

public class BaseFractalPanel extends FractalPanel {
	
	private static final long serialVersionUID = 1L;

	public BaseFractalPanel(BaseFractalAlgorithm algorithm) {
		super(algorithm);
	}

	@Override
    public BaseFractalAlgorithm getAlgorithm() {
        return (BaseFractalAlgorithm) super.getAlgorithm();
    }

	@Override
	public void setAlgorithm(FractalAlgorithm algorithm) {
        this.setAlgorithm((BaseFractalAlgorithm) algorithm);
    }
	
	public void setAlgorithm(BaseFractalAlgorithm algorithm) {
        super.setAlgorithm(algorithm);
    }
	
}
