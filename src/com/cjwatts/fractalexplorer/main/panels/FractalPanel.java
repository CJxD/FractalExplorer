package com.cjwatts.fractalexplorer.main.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.FractalExplorer;
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
    private SwingWorker<Void, Void> worker;
    
    public FractalPanel(FractalAlgorithm algorithm) {
        this.setAlgorithm(algorithm);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        final int width = this.getWidth();
        final int height = this.getHeight();
        
        // Check whether a re-render is required, or just another paint
        if (cache.isDirty()) {
            // If the previous worker isn't finished, cancel it
            if (worker != null && !worker.isDone()) worker.cancel(true);
            worker = new SwingWorker<Void, Void>() {
                private Renderer renderer = new Renderer(0, 0, width, height);
                
                @Override
                protected Void doInBackground() throws Exception {
                    // Run a new render instance in this thread
                    renderer.run();
                    return null;
                }
                
                @Override
                protected void done() {
                    cache.setImage(renderer.getRender());
                    repaint();
                }
            };
            worker.execute();
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
            g2.setFont(FractalExplorer.TEXT_FONT);
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
    public class RenderCache {
        int hashCode;
        BufferedImage image;
        private boolean invalid = false;
        
        public RenderCache() {
            this.hashCode = FractalPanel.this.hashCode();
        }
        
        /**
         * Mark the cache as dirty to force a re-render
         */
        public void invalidate() {
            this.invalid = true;
        }
        
        /**
         * Determine whether the cache entry needs updating
         * @return True if cache needs updating
         */
        public boolean isDirty() {
            int oldHash = hashCode;
            hashCode = FractalPanel.this.hashCode();
            
            return invalid || oldHash != hashCode;
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
            this.invalid = false;
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
                        return;
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
        
        return scheme.calculateColour(algorithm.escapeTime(coords));
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
     * @return The fractal image cache
     */
    public RenderCache getCache() {
        return this.cache;
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

	/* 
	 * Generated hash code function
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
		long temp;
		temp = Double.doubleToLongBits(xmax);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(xmin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ymax);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(ymin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/*
	 * Generated equals function
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FractalPanel other = (FractalPanel) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (scheme == null) {
			if (other.scheme != null)
				return false;
		} else if (!scheme.equals(other.scheme))
			return false;
		if (Double.doubleToLongBits(xmax) != Double
				.doubleToLongBits(other.xmax))
			return false;
		if (Double.doubleToLongBits(xmin) != Double
				.doubleToLongBits(other.xmin))
			return false;
		if (Double.doubleToLongBits(ymax) != Double
				.doubleToLongBits(other.ymax))
			return false;
		if (Double.doubleToLongBits(ymin) != Double
				.doubleToLongBits(other.ymin))
			return false;
		return true;
	}
    
}