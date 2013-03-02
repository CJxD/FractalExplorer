package com.cjwatts.fractalexplorer.main.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.cjwatts.fractalexplorer.main.algorithms.FractalAlgorithm;


public class ConcurrentRenderer extends FractalRenderer {

    public ConcurrentRenderer(FractalAlgorithm algorithm) {
        super(algorithm);
    }

    @Override
    public BufferedImage render() {
        RenderThread r = new RenderThread(0, 0, sizeX, sizeY);
        try {
            r.start();
            r.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return r.getRender();
    }
    
    /**
     * Recursive renderer to off-load image rendering to multiple threads
     */
    public class RenderThread extends Thread {
        
        private static final int MAX_TILE_AREA = 2000;
        
        protected int x1, y1, x2, y2;
        
        protected BufferedImage render;
        
        public RenderThread(int x1, int y1, int x2, int y2) {
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
                RenderThread[] renderers = new RenderThread[4];
                renderers[0] = new RenderThread(x1, y1, halfX, halfY);
                renderers[1] = new RenderThread(halfX, y1, x2, halfY);
                renderers[2] = new RenderThread(x1, halfY, halfX, y2);
                renderers[3] = new RenderThread(halfX, halfY, x2, y2);
                
                // Start threads
                for (RenderThread r : renderers) {
                    r.start();
                }
                // Wait for threads
                for (RenderThread r : renderers) {
                    try {
                        r.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                
                Graphics2D g2 = render.createGraphics();
                for (RenderThread r : renderers) {
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
    
}
