package com.cjwatts.fractalexplorer.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cjwatts.fractalexplorer.main.algorithms.*;
import com.cjwatts.fractalexplorer.main.util.AxisSpinner;
import com.cjwatts.fractalexplorer.main.util.Complex;
import com.cjwatts.fractalexplorer.main.util.IterationSpinner;

public class FractalExplorer extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private JPanel fractalWrapper;
    protected FractalPanel majorFractal, minorFractal;
    
    protected int iterationCount = 100;
    protected double escapeRadius = 2.0;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e1) {
        } catch (InstantiationException e1) {
        } catch (IllegalAccessException e1) {
        } catch (UnsupportedLookAndFeelException e1) {
        }
        
        FractalExplorer explorer = new FractalExplorer("Fractal Explorer");
        explorer.init();
        explorer.setVisible(true);
    }
    
    public FractalExplorer(String title) {
        super(title);
    }
    
    public void init() {
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Generate major fractal panel
        majorFractal = new FractalPanel(new MandelbrotAlgorithm(iterationCount, escapeRadius));
        majorFractal.setColourScheme(FractalColourScheme.DEFAULT);
        
        // Put the fractal in a wrapper for simple full screening
        fractalWrapper = new JPanel();
        fractalWrapper.setLayout(new BorderLayout());
        fractalWrapper.add(majorFractal);
        
        // Generate minor fractal pattern
        Complex initSeed = new Complex(0, 0);
        minorFractal = new FractalPanel(new JuliaAlgorithm(initSeed, iterationCount, escapeRadius));
        minorFractal.setColourScheme(FractalColourScheme.DEFAULT);
        
        // Generate coordinate labels
        final JLabel hoverCoords = new JLabel();
        final JLabel selectedCoords = new JLabel(initSeed.toString());
        
        // Generate progress bar
        JProgressBar loadProgress = new JProgressBar();
        
        // Generate view buttons
        JButton resetView = new JButton("Reset View");
        JButton fullScreen = new JButton("Full Screen");
        
        JPanel controls = new JPanel();
        
        // Generate algorithm chooser
        JLabel lblAlgorithm = new JLabel("Algorithm");
        final JComboBox<String> algorithm = new JComboBox<String>();
        // Add the algorithms to the combo box
        algorithm.addItem("Mandelbrot");
        algorithm.addItem("Burning Ship");
        algorithm.setSelectedItem(majorFractal.getAlgorithm().getName());
        
        // Generate iteration control
        JLabel lblIterations = new JLabel("Iterations");
        final IterationSpinner iterations = new IterationSpinner();
        iterations.setValue(iterationCount);
        
        // Generate axes control
        JLabel lblReal = new JLabel("Real Axis");
        JLabel lblRealTo = new JLabel("to");
        JLabel lblImaginary = new JLabel("Imaginary Axis");
        JLabel lblImaginaryTo = new JLabel("to");
        
        final AxisSpinner realFrom = new AxisSpinner();
        final AxisSpinner realTo = new AxisSpinner();
        final AxisSpinner imaginaryFrom = new AxisSpinner();
        final AxisSpinner imaginaryTo = new AxisSpinner(); 
        
        realFrom.setValue(-2.00);
        realTo.setValue(2.00);
        imaginaryFrom.setValue(-1.60);
        imaginaryTo.setValue(1.60);
        
        // Generate colour scheme editor
        JPanel colourSchemePanel = new JPanel();
        JLabel lblColourScheme = new JLabel("Colour Scheme");
        final JComboBox<String> colourScheme = new JComboBox<String>();
        colourScheme.addItem("Default");
        colourScheme.addItem("Blue Sky");
        colourScheme.addItem("Red Sky");
        colourScheme.addItem("Sea of Gold");
        
        GroupLayout controlLayout = new GroupLayout(controls);
        controlLayout.setHorizontalGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addGroup(controlLayout.createParallelGroup(Alignment.LEADING).addComponent(lblAlgorithm).addGroup(controlLayout.createSequentialGroup().addContainerGap().addComponent(algorithm, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)).addComponent(lblIterations).addGroup(controlLayout.createSequentialGroup().addContainerGap().addComponent(iterations, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))).addGap(38).addGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addComponent(realFrom, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblRealTo).addPreferredGap(ComponentPlacement.RELATED).addComponent(realTo, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)).addComponent(lblReal).addComponent(lblImaginary).addGroup(controlLayout.createSequentialGroup().addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblImaginaryTo).addPreferredGap(ComponentPlacement.RELATED).addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        controlLayout.setVerticalGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblAlgorithm).addComponent(lblReal)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(algorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(realFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblRealTo).addComponent(realTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblIterations).addComponent(lblImaginary)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(iterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblImaginaryTo).addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        controls.setLayout(controlLayout);
        
        GroupLayout contentLayout = new GroupLayout(content);
        contentLayout.setHorizontalGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addComponent(loadProgress, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(hoverCoords, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)).addGroup(contentLayout.createSequentialGroup().addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(18).addGroup(contentLayout.createParallelGroup(Alignment.LEADING).addComponent(fullScreen, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE).addComponent(resetView, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))).addComponent(fractalWrapper, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(contentLayout.createParallelGroup(Alignment.LEADING, false).addComponent(selectedCoords, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE).addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE).addComponent(minorFractal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
        contentLayout.setVerticalGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addComponent(fractalWrapper, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE).addGap(6).addGroup(contentLayout.createParallelGroup(Alignment.TRAILING).addComponent(loadProgress, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE).addComponent(hoverCoords)).addGap(7).addGroup(contentLayout.createParallelGroup(Alignment.TRAILING).addGroup(contentLayout.createSequentialGroup().addComponent(resetView).addPreferredGap(ComponentPlacement.RELATED).addComponent(fullScreen).addGap(18)).addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addGroup(contentLayout.createSequentialGroup().addComponent(minorFractal, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(selectedCoords).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)));
        
        GroupLayout colorSchemeLayout = new GroupLayout(colourSchemePanel);
        colorSchemeLayout.setHorizontalGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addGroup(colorSchemeLayout.createSequentialGroup().addGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addComponent(lblColourScheme).addGroup(colorSchemeLayout.createSequentialGroup().addGap(10).addComponent(colourScheme, 0, 158, Short.MAX_VALUE))).addContainerGap()));
        colorSchemeLayout.setVerticalGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addGroup(colorSchemeLayout.createSequentialGroup().addComponent(lblColourScheme).addPreferredGap(ComponentPlacement.RELATED).addComponent(colourScheme, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap(223, Short.MAX_VALUE)));
        colourSchemePanel.setLayout(colorSchemeLayout);
        content.setLayout(contentLayout);
        
        // Change listener for graph controls
        class FractalUpdater implements ChangeListener, ItemListener {
            
            @Override
            public void stateChanged(ChangeEvent e) {
                // Reset ALL graph attributes (except algorithm)
                iterationCount = iterations.getInteger();
                majorFractal.getAlgorithm().setIterations(iterationCount);
                minorFractal.getAlgorithm().setIterations(iterationCount);
                majorFractal.setComplexBounds(realFrom.getDouble(), realTo.getDouble(), imaginaryFrom.getDouble(), imaginaryTo.getDouble());
            }
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                // Update algorithm
                if (algorithm.getSelectedItem().equals("Mandelbrot")) {
                    majorFractal.setAlgorithm(new MandelbrotAlgorithm(iterationCount, escapeRadius));
                } else if (algorithm.getSelectedItem().equals("Burning Ship")) {
                    majorFractal.setAlgorithm(new BurningShipAlgorithm(iterationCount, escapeRadius));
                }
            }
        }
        FractalUpdater updater = new FractalUpdater();
        
        algorithm.addItemListener(updater);
        iterations.addChangeListener(updater);
        realFrom.addChangeListener(updater);
        realTo.addChangeListener(updater);
        imaginaryFrom.addChangeListener(updater);
        imaginaryTo.addChangeListener(updater);
        
        // View reset
        resetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realFrom.setValue(FractalPanel.DEFAULT_REAL_MIN);
                realTo.setValue(FractalPanel.DEFAULT_REAL_MAX);
                imaginaryFrom.setValue(FractalPanel.DEFAULT_IMAGINARY_MIN);
                imaginaryTo.setValue(FractalPanel.DEFAULT_IMAGINARY_MAX);
            }
        });
        
        // View full screen
        final FullScreen fs = new FullScreen();
        fullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fs.enterFullScreen();
            }
        });
        
        // Colour scheme changer
        colourScheme.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                String scheme = (String) colourScheme.getSelectedItem();
                if (scheme.equals("Default")) {
                    majorFractal.setColourScheme(FractalColourScheme.DEFAULT);
                    minorFractal.setColourScheme(FractalColourScheme.DEFAULT);
                } else if (scheme.equals("Blue Sky")) {
                    majorFractal.setColourScheme(FractalColourScheme.BLUE_SKY);
                    minorFractal.setColourScheme(FractalColourScheme.BLUE_SKY);
                } else if (scheme.equals("Red Sky")) {
                    majorFractal.setColourScheme(FractalColourScheme.RED_SKY);
                    minorFractal.setColourScheme(FractalColourScheme.RED_SKY);
                } else if (scheme.equals("Sea of Gold")) {
                    majorFractal.setColourScheme(FractalColourScheme.SEA_OF_GOLD);
                    minorFractal.setColourScheme(FractalColourScheme.SEA_OF_GOLD);
                }
            }
        });
        
        // MouseAdapter for graph clicking functions
        class FractalActionListener extends MouseAdapter {
            
            Point dragStart;
            Rectangle zoomArea;
            
            FractalPanel fractal;
            
            FractalActionListener(FractalPanel fractalPanel) {
                this.fractal = fractalPanel;
            }
            
            /*
             * Handle Julia set selection
             */
            @Override
            public void mousePressed(MouseEvent e) {
                // Spawn a new julia algorithm at the selected coordinates
                Complex coords = fractal.getCartesian(e.getX(), e.getY());
                minorFractal.setAlgorithm(new JuliaAlgorithm(coords, iterationCount, escapeRadius));
                selectedCoords.setText(coords.round(3).toString());
                
                // Start a possible drag operation
                dragStart = e.getPoint();
            }
            
            /*
             * Handle crosshairs
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                fractal.paintCrosshairs(e.getPoint());

                // Update the coordinate text
                Complex coords = fractal.getCartesian(e.getX(), e.getY());
                hoverCoords.setText(coords.round(3).toString());
            }
            
            /*
             * Handle rectangle zoom visualisation
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                // Make a new rectangle at the correct size
                zoomArea = new Rectangle(dragStart);
                zoomArea.add(e.getPoint());
                
                fractal.paintZoom(zoomArea);
            }
            
            /*
             * Handle rectangle zoom
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                // Make sure zoom area isn't a ridiculously small size
                if (zoomArea != null) {
                    // Make sure zoom area isn't a ridiculously small size
                    Dimension size = zoomArea.getSize();
                    if (size.getWidth() * size.getHeight() > 50) {
                        // Get rectangle vertices as complex numbers
                        // Min x and min y
                        Complex point1 = fractal.getCartesian(zoomArea.x, zoomArea.y);
                        // Max x and max y
                        Complex point2 = fractal.getCartesian(zoomArea.x + zoomArea.width, zoomArea.y + zoomArea.height);
                        
                        // Set the viewport via the AxisSpinners (to update both spinners and graph)
                        realFrom.setValue(point1.real());
                        realTo.setValue(point2.real());
                        imaginaryFrom.setValue(point1.imaginary());
                        imaginaryTo.setValue(point2.imaginary());
                        
                        // Reset zoom area
                        zoomArea = null;
                        fractal.paintZoom(zoomArea);
                    }
                }
            }
        }
        FractalActionListener majorListener = new FractalActionListener(majorFractal);
        majorFractal.addMouseListener(majorListener);
        majorFractal.addMouseMotionListener(majorListener);
        
        this.setContentPane(content);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 640, 480);
        this.setMinimumSize(new Dimension(455, 365));
        this.pack();
    }
    
    /**
     * Full screen class to show an expanded fractal
     */
    public class FullScreen {
        
        private GraphicsDevice device;
        private JFrame frame;
        private boolean isFullScreen;
        
        public FullScreen() {
            frame = new JFrame();
            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());
            frame.setContentPane(content);
            frame.setUndecorated(true);
            
            // Fullscreen escape
            frame.addKeyListener(new KeyListener() {
                
                @Override
                public void keyPressed(KeyEvent e) {
                    // Exit fullscreen when ESC pressed
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        exitFullScreen();
                    }
                }
                
                @Override
                public void keyReleased(KeyEvent e) {
                }
                
                @Override
                public void keyTyped(KeyEvent e) {
                }
            });
        }
        
        public void enterFullScreen() {
            if (!isFullScreen) {
                // Get the current device
                GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                device = graphicsEnvironment.getDefaultScreenDevice();
                
                if (device.isFullScreenSupported()) {
                    // Remove the fractal from the explorer wrapper
                    fractalWrapper.remove(majorFractal);
                    // Add the fractal to the frame
                    frame.getContentPane().add(majorFractal);
                    // Set the full screen window
                    device.setFullScreenWindow(frame);
                    isFullScreen = true;
                }
            }
        }
        
        public void exitFullScreen() {
            if (isFullScreen) {
                // Remove the fractal from the frame
                frame.getContentPane().remove(majorFractal);
                // Add the fractal back to the explorer wrapper
                fractalWrapper.add(majorFractal);
                // Disable full screen
                device.setFullScreenWindow(null);
                // Dispose frame
                frame.dispose();
                // Revalidate window
                FractalExplorer.this.validate();
                isFullScreen = false;
            }
        }
    }
}