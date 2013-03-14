package com.cjwatts.fractalexplorer.main;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * A class mapping ratios of first divergence to colours
 */
public class FractalColourScheme {
    
    protected TreeMap<Double, Color> colours = new TreeMap<Double, Color>();
    protected Color gridline;
    
    // <colourschemes>
    public static final FractalColourScheme DEFAULT;
    public static final FractalColourScheme BLUE_SKY;
    static {
        Color darkblue = new Color(7, 29, 64);
        Color lightblue = new Color(24, 102, 120);
        BLUE_SKY = new FractalColourScheme();
        BLUE_SKY.setBackgroundColour(darkblue);
        BLUE_SKY.setForegroundColour(Color.BLACK);
        BLUE_SKY.setGridlineColour(Color.WHITE);
        
        BLUE_SKY.addColourStop(0.3, Color.WHITE);
        BLUE_SKY.addColourStop(0.1, lightblue);
    }
    public static final FractalColourScheme RED_SKY;
    static {
        Color darkblue = new Color(3, 46, 79);
        Color red = new Color(255, 195, 16);
        Color yellow = new Color(255, 84, 16);
        RED_SKY = new FractalColourScheme();
        RED_SKY.setBackgroundColour(darkblue);
        RED_SKY.setForegroundColour(Color.BLACK);
        RED_SKY.setGridlineColour(Color.WHITE);
        
        RED_SKY.addColourStop(0.4, Color.WHITE);
        RED_SKY.addColourStop(0.3, red);
        RED_SKY.addColourStop(0.2, yellow);
    }
    public static final FractalColourScheme SEA_OF_GOLD;
    static {
        Color darkblue = new Color(0, 0, 40);
        Color lightblue = new Color(20, 70, 165);
        Color gold = new Color(210, 130, 0);
        SEA_OF_GOLD = new FractalColourScheme();
        SEA_OF_GOLD.setBackgroundColour(darkblue);
        SEA_OF_GOLD.setForegroundColour(Color.BLACK);
        SEA_OF_GOLD.setGridlineColour(Color.WHITE);
        
        SEA_OF_GOLD.addColourStop(0.8, gold);
        SEA_OF_GOLD.addColourStop(0.6, Color.WHITE);
        SEA_OF_GOLD.addColourStop(0.4, lightblue);
        SEA_OF_GOLD.addColourStop(0.25, darkblue);
        SEA_OF_GOLD.addColourStop(0.15, gold);
        SEA_OF_GOLD.addColourStop(0.1, Color.WHITE);
        SEA_OF_GOLD.addColourStop(0.05, lightblue);
    }
    static {
        DEFAULT = SEA_OF_GOLD;
    }
    
    // </colourschemes>
    
    /**
     * Calculates the colour as a mix between the two closest colour stops
     * 
     * @param progress The gradient progress expressed as a ratio 0.0 <= x <= 1.0
     */
    public Color calculateColour(double progress) {
        if (colours.size() < 2) {
            throw new IllegalStateException("There are not enough colours in this colour scheme!");
        }
        
        // Return single colours for minimum and maximum divergence
        if (progress <= colours.firstKey()) {
            return colours.firstEntry().getValue();
        }
        
        else if (progress >= colours.lastKey()) {
            return colours.lastEntry().getValue();
        }
        
        // Otherwise, calculate a colour based on its nearest colour stops
        return calculateGradient(progress);
    }
    
    private Color calculateGradient(Double progress) {
        Map.Entry<Double, Color> c1 = colours.floorEntry(progress);
        Map.Entry<Double, Color> c2 = colours.ceilingEntry(progress);
        
        // Calculate gradient ratio
        double range = c2.getKey() - c1.getKey();
        double gradient = (progress - c1.getKey()) / range;
        
        int r1 = c1.getValue().getRed();
        int g1 = c1.getValue().getGreen();
        int b1 = c1.getValue().getBlue();
        
        int r2 = c2.getValue().getRed();
        int g2 = c2.getValue().getGreen();
        int b2 = c2.getValue().getBlue();
        
        // Average the colours between c1 and c2
        int r = (int) Math.round(r1 + ((r2 - r1) * gradient));
        int g = (int) Math.round(g1 + ((g2 - g1) * gradient));
        int b = (int) Math.round(b1 + ((b2 - b1) * gradient));
        
        return new Color(r, g, b);
    }
    
    /**
     * Add a colour stop to the scheme's gradient generator
     * 
     * @param progress Gradient progress ratio satisfying 0.0 <= x <= 1.0
     * @param c Colour to add
     */
    public void addColourStop(Double progress, Color c) {
        if (progress >= 0.0 && progress <= 1.0) {
            colours.put(progress, c);
        } else {
            throw new IllegalArgumentException(progress + " is not a valid ratio.");
        }
    }
    
    /**
     * @return Entry set of this colour scheme
     */
    public Set<Entry<Double, Color>> entrySet() {
        return colours.entrySet();
    }
    
    /**
     * @param location
     * @return The explicit colour stop at the given location, if any
     */
    public Color getColourStop(Double location) {
        return colours.get(location);
    }
    
    /**
     * Get the colour of the graph background
     * Equivalent to colour at 0.0
     */
    public Color getBackgroundColour() {
        return getColourStop(0.0);
    }
    
    /**
     * Get the colour of the graph foreground
     * Equivalent to colour at 1.0
     */
    public Color getForegroundColour() {
        return getColourStop(1.0);
    }
    
    /**
     * Get the colour of any gridlines on the graph
     */
    public Color getGridlineColour() {
        return this.gridline;
    }
    
    /**
     * Remove a colour stop with the given location, if exists
     * @param location
     */
    public void removeColourStop(Double location) {
        colours.remove(location);
    }
    
    /**
     * Set the colour of the graph background
     * Equivalent to colour at 0.0
     * 
     * @param c
     */
    public void setBackgroundColour(Color c) {
        addColourStop(0.0, c);
    }
    
    /**
     * Set the colour of the graph foreground
     * Equivalent to colour at 1.0
     * 
     * @param c
     */
    public void setForegroundColour(Color c) {
        addColourStop(1.0, c);
    }
    
    /**
     * Set the colour of any gridlines on the graph
     */
    public void setGridlineColour(Color c) {
        this.gridline = c;
    }
}
