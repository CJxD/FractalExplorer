package com.cjwatts.fractalexplorer.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.cjwatts.fractalexplorer.main.render.FractalRenderer;

public class FractalPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    protected FractalRenderer renderer;
    protected final RenderCache cache = new RenderCache();
    
    public FractalPanel(FractalRenderer renderer) {
        this.setBackground(Color.BLACK);
        this.renderer = renderer;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();
        
        // Always try to draw from the cache first
        try {
            g2.drawImage(cache.getImage(), 0, 0, width, height, null);
        } catch (NullPointerException ignore) {
        }
        
        // Check whether a re-render is required
        if (cache.isDirty()) {
            renderer.setImageSize(new Dimension(width, height));
            // Create a new thread to render the image
            new Thread() {
                @Override
                public void run() {
                    BufferedImage render = renderer.render();
                    try {
                        cache.setImage(render);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    // Call a new repaint
                    repaint();
                }
            }.start();
        }
    }
    
    /**
     * Data structure to hold cached renders
     */
    private class RenderCache {
        
        int hashCode;
        BufferedImage image;
        
        public RenderCache() {
            this.hashCode = calculateHash();
        }
        
        private int calculateHash() {
            // Use prime numbers to generate a hash code
            // This probably isn't foolproof, but it should be good enough
            int hash = 1;
            try {
                hash = hash * 137 + renderer.hashCode();
                hash = hash * 139 + getWidth();
                hash = hash * 141 + getHeight();
            } catch (NullPointerException ignore) {
            }
            return hash;
        }
        
        /**
         * Determine whether the cache entry needs updating
         * @return True if cache needs updating
         */
        public boolean isDirty() {
            int oldHash = hashCode;
            hashCode = calculateHash();
            
            return oldHash != hashCode;
        }
        
        /**
         * Get the image from the cache
         * @return
         */
        public BufferedImage getImage() {
            return image;
        }
        
        /**
         * Set the image in the cache
         * @param image
         */
        public void setImage(BufferedImage image) {
            this.image = image;
        }
        
    }
    
    /**
     * Get the fractal rendering system bound to this FractalPanel
     */
    public FractalRenderer getRenderer() {
        return this.renderer;
    }
    
    /**
     * Set the fractal rendering system bound to this FractalPanel
     * 
     * @param renderer
     */
    public void setRenderer(FractalRenderer renderer) {
        this.renderer = renderer;
    }
    
}