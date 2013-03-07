package com.cjwatts.fractalexplorer.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Map;

import javax.swing.JPanel;

import com.cjwatts.fractalexplorer.main.algorithms.FractalAlgorithm;
import com.cjwatts.fractalexplorer.main.util.Complex;

public class FractalPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    public static final double DEFAULT_REAL_MIN = -2.0;
    public static final double DEFAULT_REAL_MAX = 2.0;
    public static final double DEFAULT_IMAGINARY_MIN = -1.6;
    public static final double DEFAULT_IMAGINARY_MAX = 1.6;
    
    private double xmin = DEFAULT_REAL_MIN;
    private double xmax = DEFAULT_REAL_MAX;
    private double ymin = DEFAULT_IMAGINARY_MIN;
    private double ymax = DEFAULT_IMAGINARY_MAX;
    
    // Put crosshairs and zoom rectangle off-screen
    private Point crosshairs;
    private Rectangle zoom;
    
    private FractalAlgorithm algorithm;
    private FractalColourScheme scheme = FractalColourScheme.DEFAULT;
    
    private final RenderCache cache = new RenderCache();
    
    public FractalPanel(FractalAlgorithm algorithm) {
        this.setBackground(Color.BLACK);
        this.setAlgorithm(algorithm);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();
        
        // Check whether a re-render is required, or just another paint
        if (cache.isDirty()) {
            Renderer renderer = new Renderer(0, 0, width, height);
            try {
                // Start primary thread
                renderer.start();
                // Wait to finish
                renderer.join();
                
                cache.setImage(renderer.getRender());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Draw the fractal
        g2.drawImage(cache.getImage(), 0, 0, width, height, null);
        
        Color c = scheme.getGridlineColour();
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
        
        // Draw crosshairs
        if (crosshairs != null) {
            g2.drawLine(crosshairs.x, 0, crosshairs.x, this.getHeight());
            g2.drawLine(0, crosshairs.y, this.getWidth(), crosshairs.y);
            
            // Crosshair coordinate text
            g2.drawString(
                    getCartesian(crosshairs.x, crosshairs.y).round(3).toString(),
                    crosshairs.x + 15,
                    crosshairs.y + 15);
        }
        
        // Draw zoom rectangle
        if (zoom != null) {
            g2.fill(zoom);
        }
    }
    
    /**
     * Data structure to hold cached renders
     */
    protected class RenderCache {
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
                hash = hash * 31 + algorithm.hashCode();
                hash = hash * 37 + scheme.hashCode();
            } catch (NullPointerException ignore) {
            }
            hash = hash * 41 + new Double(xmin).hashCode();
            hash = hash * 43 + new Double(xmax).hashCode();
            hash = hash * 47 + new Double(ymin).hashCode();
            hash = hash * 53 + new Double(ymax).hashCode();
            hash = hash * 59 + getWidth();
            hash = hash * 61 + getHeight();
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
     * Recursive renderer to off-load image rendering to multiple threads
     */
    public class Renderer extends Thread {
        
        public static final int MAX_TILE_AREA = 2000;
        
        protected int x1, y1, x2, y2;
        
        protected BufferedImage render;
        
        public Renderer(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        /**
         * Fetch the rendered image
         */
        public BufferedImage getRender() throws IllegalStateException {
            if (render == null) {
                throw new IllegalStateException("Fractal is not rendered.");
            }
            
            return render;
        }
        
        @Override
        public void run() {
            int sizeX = x2 - x1;
            int sizeY = y2 - y1;
            
            render = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
            
            // If image is too big, split into 4
            if (sizeX * sizeY > MAX_TILE_AREA) {
                int halfX = x1 + sizeX / 2;
                int halfY = y1 + sizeY / 2;
                Renderer[] renderers = new Renderer[4];
                renderers[0] = new Renderer(x1, y1, halfX, halfY);
                renderers[1] = new Renderer(halfX, y1, x2, halfY);
                renderers[2] = new Renderer(x1, halfY, halfX, y2);
                renderers[3] = new Renderer(halfX, halfY, x2, y2);
                
                // Start threads
                for (Renderer r : renderers) {
                    r.start();
                }
                // Wait for threads
                for (Renderer r : renderers) {
                    try {
                        r.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                
                Graphics2D g2 = render.createGraphics();
                for (Renderer r : renderers) {
                    // Paint each subimage onto the main image
                    g2.drawImage(r.getRender(), r.x1 - this.x1, r.y1 - this.y1, r.x2 - r.x1, r.y2 - r.y1, null);
                }
            }
            // Otherwise, render
            else {
                // setPixel is faster than using setRGB
                WritableRaster raster = render.getRaster();
                int rgb;
                // Loop across each pixel
                for (int x = x1; x < x2; x++) {
                    for (int y = y1; y < y2; y++) {
                        // Draw the pixel with calculated colour
                        rgb = getPixelColour(x, y).getRGB();
                        raster.setPixel(x - x1, y - y1, new int[] {
                                // Bit-shifting and modulus to extract r,g,b
                        rgb >> 16, (rgb >> 8) % 256, rgb % 256 });
                    }
                }
            }
        }
    }
    
    /**
     * Calculates the complex Cartesian coordinates for the given pixel
     * 
     * @param x X coordinate relative to top left
     * @param y Y coordinate relative to top left
     * @param width Width of the graph
     * @param height Height of the graph
     */
    public Complex getCartesian(int x, int y) {
        int width = this.getWidth();
        int height = this.getHeight();
        
        if (width == 0 || height == 0) {
        	throw new IllegalStateException("Fractal panel does not have valid dimensions. Width:" + width + " Height:" + height);
        } else {
	        // Move the graph into the centre of the container
	        double calcX = x - width / 2;
	        double calcY = y - height / 2;
	        
	        // Scale the axes
	        calcX *= (xmax - xmin) / width;
	        calcY *= (ymax - ymin) / height;
	        
	        // Calculate zoom offsets
	        // Add the average of the x and y space
	        calcX += (xmin + xmax) / 2;
	        calcY += (ymin + ymax) / 2;
	        
	        return new Complex(calcX, calcY);
        }
    }
    
    /**
     * Calculates the colour for the given pixel
     * 
     * @param x X coordinate relative to top left
     * @param y Y coordinate relative to top left
     * @param width Width of the graph
     * @param height Height of the graph
     */
    public Color getPixelColour(int x, int y) {
        // Get complex Cartesian coordinates
        Complex coords = getCartesian(x, y);
        
        return scheme.calculateColour(algorithm.divergenceRatio(coords));
    }
    
    /**
     * Paints crosshairs at the given location
     * If null, crosshairs are not drawn
     */
    public void paintCrosshairs(Point p) {
        this.crosshairs = p;
        this.repaint();
    }
    
    /**
     * Paints zoom rectangle
     * If null, zoom rectangle is not drawn
     */
    public void paintZoom(Rectangle r) {
        this.zoom = r;
        this.repaint();
    }
    
    /**
     * Get the algorithm for the fractal equations
     */
    public FractalAlgorithm getAlgorithm() {
        return this.algorithm;
    }
    
    /**
     * Set the algorithm for the fractal equations
     * @param algorithm
     */
    public void setAlgorithm(FractalAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.repaint();
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param point1
     * @param point2
     */
    public void setComplexBounds(Complex point1, Complex point2) {
        setComplexBounds(point1.real(), point2.real(), point1.imaginary(), point2.imaginary());
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param xmin
     * @param xmax
     * @param ymin
     * @param ymax
     */
    public void setComplexBounds(double xmin, double xmax, double ymin, double ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.repaint();
    }
    
    /**
     * Get the colour scheme of the fractal pattern
     */
    public FractalColourScheme getColourScheme() {
        return this.scheme;
    }
    
    /**
     * Set the colour scheme of the fractal pattern
     * @param scheme
     */
    public void setColourScheme(FractalColourScheme scheme) {
        this.scheme = scheme;
        this.repaint();
    }
    
}