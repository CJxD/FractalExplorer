package com.cjwatts.fractalexplorer.main.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
    
    private double rmin = DEFAULT_REAL_MIN;
    private double rmax = DEFAULT_REAL_MAX;
    private double imin = DEFAULT_IMAGINARY_MIN;
    private double imax = DEFAULT_IMAGINARY_MAX;
    
    // Put crosshairs and zoom rectangle off-screen
    private Point crosshairs;
    private Rectangle zoom;
    
    private FractalAlgorithm algorithm;
    private FractalColourScheme scheme = FractalColourScheme.DEFAULT;
    
    private final RenderCache cache = new RenderCache();
    private JProgressBar progressBar;
    private SwingWorker<Integer, Integer> worker;
    
    /**
     * Create a new fractal panel with a given algorithm
     * @param algorithm
     */
    public FractalPanel(FractalAlgorithm algorithm) {
        this.setAlgorithm(algorithm);
    }
    
    /**
     * Create a new fractal panel with a given algorithm
     * Report progress to the given ProgressBar
     * @param algorithm
     * @param progressBar JProgressBar to attach
     */
    public FractalPanel(FractalAlgorithm algorithm, JProgressBar progressBar) {
        this(algorithm);
        this.progressBar = progressBar;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        final int width = this.getWidth();
        final int height = this.getHeight();
        
        // Check whether a re-render is required, or just another paint
        if (cache.isDirty()) {
            // If the previous worker isn't finished, cancel it
            if (worker != null && !worker.isDone())
                worker.cancel(true);
            
            worker = new SwingWorker<Integer, Integer>() {
                
                private Renderer renderer = new Renderer(width, height);
                
                @Override
                protected Integer doInBackground() throws Exception {
                    if (progressBar != null) {
                        // Run a new render and monitor progress
                        new Thread(renderer).start();
                        while (!renderer.isRendered()) {
                            publish(renderer.getProgress());
                        }
                    } else {
                        // Otherwise, just run a render in this thread
                        renderer.run();
                    }
                    return renderer.getProgress();
                }
                
                @Override
                protected void process(List<Integer> chunks) {
                    if (progressBar != null) {
                        progressBar.setValue(chunks.get(0));
                    }
                }
                
                @Override
                protected void done() {
                    // Reset progress bar
                    if (progressBar != null) {
                        progressBar.setValue(0);
                    }
                    // Set the render image
                    cache.setImage(renderer.getRender());
                    repaint();
                }
            };
            worker.execute();
        }
        
        // Draw the fractal
        g2.drawImage(cache.getImage(), 0, 0, null);
        
        Color c = scheme.getGridlineColour();
        g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
        
        // Draw crosshairs
        if (crosshairs != null) {
            g2.drawLine(crosshairs.x, 0, crosshairs.x, this.getHeight());
            g2.drawLine(0, crosshairs.y, this.getWidth(), crosshairs.y);
            
            // Crosshair coordinate text
            g2.setFont(FractalExplorer.TEXT_FONT);
            g2.drawString(getCartesian(
                    crosshairs.x, crosshairs.y).round(3).toString(),
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
        
        private int hashCode;
        private BufferedImage image;
        private boolean invalid = false;
        
        /**
         * Mark the cache as dirty to force a re-render
         */
        public synchronized void invalidate() {
            this.invalid = true;
        }
        
        /**
         * Determine whether the cache entry needs updating
         * @return True if cache needs updating
         */
        public synchronized boolean isDirty() {
            int oldHash = hashCode;
            hashCode = FractalPanel.this.hashCode();
            
            return invalid || oldHash != hashCode;
        }
        
        /**
         * Get the image from the cache
         * @return
         */
        public synchronized BufferedImage getImage() {
            return image;
        }
        
        /**
         * Set the image in the cache
         * @param image
         */
        public synchronized void setImage(BufferedImage image) {
            this.image = image;
            this.invalid = false;
        }
        
    }
    
    /**
     * Rendering mechanism to off-load image rendering into multiple sub-threads
     */
    public class Renderer implements Runnable {
        
        // Thread pool
        private ExecutorService pool;
        // Timeout in milliseconds
        private long timeout = 1000000;
        
        // Tile storage
        private WritableRaster[][] tiles;
        private ColorModel model;
        
        // Metrics
        private int width, height;
        private int tilesX, tilesY;
        private int tileW, tileH;
        
        // Progress actually just counts the number of tiles rendered, not
        // percentage
        private Integer progress = 0;
        
        /**
         * Create a new fractal image renderer
         * @param width
         * @param height
         */
        public Renderer(int width, int height) {
            // Create a renderer based on the number of cores/threads available
            this(width, height, Runtime.getRuntime().availableProcessors());
        }
        
        /**
         * Create a new fractal renderer with a specific number of threads in the pool
         * @param width
         * @param height
         * @param numThreads
         */
        public Renderer(int width, int height, int numThreads) {
            pool = Executors.newFixedThreadPool(numThreads);
            
            this.width = width;
            this.height = height;
            
            // Calculate number of tiles in X and Y
            this.tilesX = (int) Math.floor(Math.sqrt(numThreads));
            this.tilesY = numThreads / tilesX;
            
            this.tileW = width / tilesX;
            this.tileH = height / (numThreads / tilesY);
            
            // Initialise tiles
            this.tiles = new WritableRaster[tilesX][tilesY];
            this.model = new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF);
            for (int i = 0; i < tilesX; i++) {
                for (int j = 0; j < tilesY; j++) {
                    tiles[i][j] = model.createCompatibleWritableRaster(tileW, tileH);
                }
            }
        }
        
        @Override
        public void run() {
            progress = 0;
            for (int i = 0; i < tilesX; i++) {
                for (int j = 0; j < tilesY; j++) {
                    pool.execute(new RenderThread(tiles[i][j],
                            i * tileW,      // This is the horizontal position of the tile
                            j * tileH));    // and this is the vertical position
                }
            }
            pool.shutdown();
            
            try {
                pool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                pool.shutdownNow();
                return;
            }
        }
        
        private class RenderThread implements Runnable {
            
            private WritableRaster tile;
            private int rmin, imin, rmax, imax;
            
            public RenderThread(WritableRaster tile, int x, int y) {
                this.tile = tile;
                this.rmin = x;
                this.imin = y;
                this.rmax = x + tile.getWidth();
                this.imax = y + tile.getHeight();
            }
            
            @Override
            public void run() {
                int rgb;
                // Loop across each pixel
                for (int x = rmin; x < rmax; x++) {
                    for (int y = imin; y < imax; y++) {
                        // Draw the pixel with calculated colour
                        rgb = getPixelColour(x, y).getRGB();
                        tile.setPixel(x - rmin, y - imin,
                                new int[] {
                                    // Bit-shifting and modulus to extract r,g,b
                                    rgb >> 16,
                                    (rgb >> 8) % 256,
                                    rgb % 256
                                });
                    }
                }
                synchronized (progress) {
                    progress++;
                }
            }
        }
        
        /**
         * @return The rendered image
         */
        public BufferedImage getRender() {
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = output.createGraphics();
            BufferedImage temp;
            for (int i = 0; i < tilesX; i++) {
                for (int j = 0; j < tilesY; j++) {
                    temp = new BufferedImage(model, tiles[i][j], model.isAlphaPremultiplied(), null);
                    g2.drawImage(temp, i * tileW, j * tileH, tileW, tileH, null);
                }
            }
            // Clean up all the temporary stuff
            temp = null;
            System.gc();
            
            return output;
        }
        
        /**
         * @return The percentage progress of the render job
         */
        public int getProgress() {
            int p;
            synchronized (progress) {
                p = this.progress;
            }
            return (100 * p) / (tilesX * tilesY);
        }
        
        /**
         * @return True if render is complete
         */
        public boolean isRendered() {
            return progress == (tilesX * tilesY);
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
            calcX *= (rmax - rmin) / width;
            calcY *= (imax - imin) / height;
            
            // Calculate zoom offsets
            // Add the average of the x and y space
            calcX += (rmin + rmax) / 2;
            calcY += (imin + imax) / 2;
            
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
     * Attach a progress bar to report progress to
     * @param progressBar
     */
    public void attachProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }
    
    /**
     * Remove any attached progress bars
     */
    public void removeProgressBar() {
        this.progressBar = null;
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param point1 Bottom left (minimum) complex point
     * @param point2 Top right (maximum) complex point
     */
    public void setComplexBounds(Complex point1, Complex point2) {
        setComplexBounds(point1.real(), point2.real(), point1.imaginary(), point2.imaginary());
    }
    
    /**
     * Set the complex coordinate bounds of the fractal panel
     * 
     * @param rmin
     * @param rmax
     * @param imin
     * @param imax
     */
    public void setComplexBounds(double rmin, double rmax, double imin, double imax) {
        this.rmin = rmin;
        this.rmax = rmax;
        this.imin = imin;
        this.imax = imax;
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
     * Generated hash code function (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
        result = prime * result + ((scheme == null) ? 0 : scheme.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rmax);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(rmin);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(imax);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(imin);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    
    /*
     * Generated equals function (non-Javadoc)
     * 
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
        if (Double.doubleToLongBits(rmax) != Double.doubleToLongBits(other.rmax))
            return false;
        if (Double.doubleToLongBits(rmin) != Double.doubleToLongBits(other.rmin))
            return false;
        if (Double.doubleToLongBits(imax) != Double.doubleToLongBits(other.imax))
            return false;
        if (Double.doubleToLongBits(imin) != Double.doubleToLongBits(other.imin))
            return false;
        return true;
    }
    
}