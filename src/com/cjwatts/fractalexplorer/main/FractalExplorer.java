package com.cjwatts.fractalexplorer.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cjwatts.fractalexplorer.main.algorithms.*;
import com.cjwatts.fractalexplorer.main.io.Favourite;
import com.cjwatts.fractalexplorer.main.io.Favourites;
import com.cjwatts.fractalexplorer.main.panels.BaseFractalPanel;
import com.cjwatts.fractalexplorer.main.panels.FractalPanel;
import com.cjwatts.fractalexplorer.main.panels.JuliaPanel;
import com.cjwatts.fractalexplorer.main.util.AxisSpinner;
import com.cjwatts.fractalexplorer.main.util.Complex;
import com.cjwatts.fractalexplorer.main.util.IterationSpinner;

public class FractalExplorer extends JFrame {
    
    private static final long serialVersionUID = 1L;

    public static final Font TEXT_FONT = new Font("Arial", 0, 11);
    
    protected BaseFractalPanel majorFractal;
    protected JuliaPanel minorFractal;
    private JPanel majorWrapper, minorWrapper;
    
    protected int iterationCount = 100;
    protected double escapeRadius = 2.0;
    
    private Complex lastSelected = new Complex(0, 0);
    
    protected static Favourites favouriteList = new Favourites();
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
        } catch (InstantiationException ex) {
        } catch (IllegalAccessException ex) {
        } catch (UnsupportedLookAndFeelException ex) {
        }
        
        // Load favourites
        try {
            favouriteList.load();
        } catch (FileNotFoundException ignore) {
        } catch (IOException ex) {
            System.err.println("Unable to load favourites!");
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FractalExplorer explorer = new FractalExplorer("Fractal Explorer");
                explorer.init();
                explorer.setVisible(true);
            }
        });
    }
    
    public FractalExplorer(String title) {
        super(title);
    }
    
    public void init() {
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Generate major fractal panel
        majorFractal = new BaseFractalPanel(new MandelbrotAlgorithm(iterationCount, escapeRadius));
        majorFractal.setColourScheme(FractalColourScheme.DEFAULT);
        
        // Generate minor fractal pattern
        minorFractal = new JuliaPanel(new JuliaAlgorithm(majorFractal.getAlgorithm(), lastSelected));
        minorFractal.setColourScheme(FractalColourScheme.DEFAULT);
        
     	// Put the fractals in wrappers for simple rearrangement
        majorWrapper = new JPanel();
        majorWrapper.setLayout(new BorderLayout());
        majorWrapper.add(majorFractal);
        minorWrapper = new JPanel();
        minorWrapper.setLayout(new BorderLayout());
        minorWrapper.add(minorFractal);
        
        // Generate selected coordinate label
        final JLabel selectedCoords = new JLabel(lastSelected.toString());
        selectedCoords.setFont(TEXT_FONT);
        
        // Generate progress bar
        JProgressBar loadProgress = new JProgressBar();
        
        // Generate view buttons
        JPanel viewButtons = new JPanel();
        JButton swap = new JButton("Swap Views");
        swap.setFont(TEXT_FONT);
        JButton resetView = new JButton("Reset View");
        resetView.setFont(TEXT_FONT);
        JButton fullScreen = new JButton("Full Screen");
        fullScreen.setFont(TEXT_FONT);
        
        JPanel controls = new JPanel();
        
        // Generate algorithm chooser
        JLabel lblAlgorithm = new JLabel("Algorithm");
        lblAlgorithm.setFont(TEXT_FONT);
        final JComboBox<String> algorithm = new JComboBox<String>();
        algorithm.setFont(TEXT_FONT);
        // Add the algorithms to the combo box
        for (Class<? extends BaseFractalAlgorithm> a : BaseFractalAlgorithm.getSubClasses()) {
            try {
                algorithm.addItem(a.newInstance().getName());
            } catch (Exception ignore) {
            }
        }
        algorithm.setSelectedItem(majorFractal.getAlgorithm().getName());
        
        // Generate iteration control
        JLabel lblIterations = new JLabel("Iterations");
        lblIterations.setFont(TEXT_FONT);
        final IterationSpinner iterations = new IterationSpinner();
        iterations.setFont(TEXT_FONT);
        iterations.setValue(iterationCount);
        
        // Generate axes control
        JLabel lblReal = new JLabel("Real Axis");
        JLabel lblRealTo = new JLabel("to");
        JLabel lblImaginary = new JLabel("Imaginary Axis");
        JLabel lblImaginaryTo = new JLabel("to");
        
        lblReal.setFont(TEXT_FONT);
        lblRealTo.setFont(TEXT_FONT);
        lblImaginary.setFont(TEXT_FONT);
        lblImaginaryTo.setFont(TEXT_FONT);
        
        final AxisSpinner realFrom = new AxisSpinner();
        final AxisSpinner realTo = new AxisSpinner();
        final AxisSpinner imaginaryFrom = new AxisSpinner();
        final AxisSpinner imaginaryTo = new AxisSpinner();
        
        realFrom.setFont(TEXT_FONT);
        realTo.setFont(TEXT_FONT);
        imaginaryFrom.setFont(TEXT_FONT);
        imaginaryTo.setFont(TEXT_FONT);
        
        realFrom.setValue(-2.00);
        realTo.setValue(2.00);
        imaginaryFrom.setValue(-1.60);
        imaginaryTo.setValue(1.60);
        
        // Generate favourites selector
        JLabel lblFavourites = new JLabel("Favourites");
        lblFavourites.setFont(TEXT_FONT);
        final JComboBox<String> favourites = new JComboBox<String>();
        favourites.setFont(TEXT_FONT);
        favourites.setEditable(true);
        for (Favourite f : favouriteList) {
            favourites.addItem(f.getName());
        }
        
        // Generate add and remove buttons
        JButton addFavourite = new JButton("+");
        /*try {
            ImageIcon add = new ImageIcon(
                    ImageIO.read(new File("res/add.png")));
            addFavourite = new JButton(add);
        } catch (IOException ex) {
            // Fallback
            addFavourite = new JButton("+");
        }*/
        //addFavourite.setBorder(BorderFactory.createEmptyBorder());
        //addFavourite.setContentAreaFilled(false);
         
        JButton removeFavourite = new JButton("-");
        /*try {
            ImageIcon remove = new ImageIcon(
                    ImageIO.read(new File("res/remove.png")));
            removeFavourite = new JButton(remove);
        } catch (IOException ex) {
            // Fallback
            removeFavourite = new JButton("-");
        }*/
        //removeFavourite.setBorder(BorderFactory.createEmptyBorder());
        //removeFavourite.setContentAreaFilled(false);
        
        // Generate colour scheme editor
        JPanel colourSchemePanel = new JPanel();
        JLabel lblColourScheme = new JLabel("Colour Scheme");
        lblColourScheme.setFont(TEXT_FONT);
        final JComboBox<String> colourScheme = new JComboBox<String>();
        colourScheme.setFont(TEXT_FONT);
        colourScheme.addItem("Default");
        colourScheme.addItem("Blue Sky");
        colourScheme.addItem("Red Sky");
        colourScheme.addItem("Sea of Gold");
        
        /*
         * THE LAYOUT
         * 
         * So basically everything here is positioned relatively and using default gap sizes
         * so if you change anything it should rearrange nicely.
         * 
         * Any numbers you do see are preferred sizes for aesthetics such as the size of the minor fractal.
         * This could be changed to GroupLayout.PREFERRED_SIZE when using the preferred size attribute
         * instead if required.
         * 
         * Warning - no comments in the layout. Hopefully the names of components are good enough.
         */
        
        /*
         * Controls (bottom-left)
         */
        GroupLayout controlLayout = new GroupLayout(controls);
        controlLayout.setHorizontalGroup(
            controlLayout.createSequentialGroup()
                .addGroup(controlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(lblAlgorithm)
                    .addComponent(lblIterations)
                    .addGroup(controlLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(algorithm, 140, 140, GroupLayout.PREFERRED_SIZE)
                    )
                    .addGroup(controlLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(iterations, 64, 64, GroupLayout.PREFERRED_SIZE)
                    )
                )
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(controlLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(lblImaginary)
                    .addGroup(controlLayout.createSequentialGroup()
                        .addComponent(realFrom, 64, 64, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblRealTo)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(realTo, 64, 64, GroupLayout.PREFERRED_SIZE)
                    )
                    .addComponent(lblReal)
                    .addGroup(controlLayout.createSequentialGroup()
                        .addComponent(imaginaryTo, 64, 64, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(lblImaginaryTo)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(imaginaryFrom, 64, 64, GroupLayout.PREFERRED_SIZE)
                    )
                )
        );
        controlLayout.setVerticalGroup(
            controlLayout.createSequentialGroup()
                .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblAlgorithm)
                    .addComponent(lblReal)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(algorithm)
                    .addComponent(realFrom)
                    .addComponent(lblRealTo)
                    .addComponent(realTo)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(lblIterations)
                    .addComponent(lblImaginary)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(iterations)
                    .addComponent(imaginaryTo)
                    .addComponent(lblImaginaryTo)
                    .addComponent(imaginaryFrom)
                )
        );
        controls.setLayout(controlLayout);
        
        /*
         * View buttons (bottom-center)
         */
        GroupLayout viewButtonLayout = new GroupLayout(viewButtons);
        viewButtonLayout.setHorizontalGroup(
            viewButtonLayout.createParallelGroup(Alignment.TRAILING, false)
                .addComponent(resetView, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(fullScreen, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(swap, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );
        viewButtonLayout.setVerticalGroup(
            viewButtonLayout.createSequentialGroup()
                .addComponent(swap)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(resetView)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(fullScreen)
        );
        viewButtons.setLayout(viewButtonLayout);
        
        /*
         * Colour scheme and favourites (center-right)
         */
        GroupLayout colorSchemeLayout = new GroupLayout(colourSchemePanel);
        colorSchemeLayout.setHorizontalGroup(
            colorSchemeLayout.createParallelGroup(Alignment.LEADING)
                .addComponent(lblFavourites)
                .addComponent(lblColourScheme)
                .addGroup(colorSchemeLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(colorSchemeLayout.createSequentialGroup()
                            .addComponent(favourites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addGap(1)
                            .addComponent(addFavourite, 20, 20, GroupLayout.PREFERRED_SIZE)
                            .addComponent(removeFavourite, 20, 20, GroupLayout.PREFERRED_SIZE)
                        )
                        .addComponent(colourScheme)
                    )
                    .addContainerGap()
               )
        );
        colorSchemeLayout.setVerticalGroup(
            colorSchemeLayout.createSequentialGroup()
                .addComponent(lblFavourites)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(colorSchemeLayout.createParallelGroup(Alignment.CENTER)
                    .addComponent(favourites, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeFavourite)
                    .addComponent(addFavourite)
                )
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(lblColourScheme)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(colourScheme, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
        colourSchemePanel.setLayout(colorSchemeLayout);
        
        /*
         * General layout
         */
        GroupLayout contentLayout = new GroupLayout(content);
        contentLayout.setHorizontalGroup(
            contentLayout.createSequentialGroup()
                .addGroup(contentLayout.createParallelGroup(Alignment.LEADING)
                    .addComponent(majorWrapper, 400, 400, Short.MAX_VALUE)
                    .addGroup(contentLayout.createSequentialGroup()
                        .addComponent(controls)
                        .addPreferredGap(ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(viewButtons)
                    )
                    .addComponent(loadProgress)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(contentLayout.createParallelGroup(Alignment.LEADING, false)
                    .addComponent(selectedCoords)
                    .addComponent(colourSchemePanel)
                    .addComponent(minorWrapper, 200, 200, 200)
                )
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(contentLayout.createSequentialGroup()
                    .addComponent(majorWrapper, 320, 320, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(loadProgress, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(contentLayout.createParallelGroup(Alignment.TRAILING, false)
                        .addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(viewButtons)
                    )
                    .addContainerGap()
                )
                .addGroup(contentLayout.createSequentialGroup()
                    .addComponent(minorWrapper, 160, 160, 160)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(selectedCoords)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(colourSchemePanel)
                )
        );
        content.setLayout(contentLayout);
        
        // Change listener for graph controls
        class FractalUpdater implements ChangeListener, ItemListener {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Reset ALL graph attributes (except algorithm)
                iterationCount = iterations.getInteger();
                majorFractal.getAlgorithm().setIterations(iterationCount);
                minorFractal.getAlgorithm().getBase().setIterations(iterationCount);
                majorFractal.setComplexBounds(realFrom.getDouble(), realTo.getDouble(), imaginaryFrom.getDouble(), imaginaryTo.getDouble());
            }
            
            @Override
            public void itemStateChanged(ItemEvent e) {
                // Update algorithm
                BaseFractalAlgorithm a = BaseFractalAlgorithm.getByName(algorithm.getSelectedItem().toString());
                a.setEscapeRadius(escapeRadius);
                a.setIterations(iterationCount);
                majorFractal.setAlgorithm(a);
                minorFractal.setAlgorithm(new JuliaAlgorithm(a, lastSelected));
            }
        }
        FractalUpdater updater = new FractalUpdater();
        
        algorithm.addItemListener(updater);
        iterations.addChangeListener(updater);
        realFrom.addChangeListener(updater);
        realTo.addChangeListener(updater);
        imaginaryFrom.addChangeListener(updater);
        imaginaryTo.addChangeListener(updater);
        
        // View swap
        swap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FractalPanel major = getMajorPanel();
                FractalPanel minor = getMinorPanel();
                setMajorPanel(minor);
                setMinorPanel(major);
                major.getCache().invalidate();
                minor.getCache().invalidate();
                repaint();
            }
        });
        
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
        
        // Favourites handler
        addFavourite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected;
                if ((selected = favourites.getSelectedItem()) != null) {
                    Favourite existing;
                    if ((existing = favouriteList.getByName(selected.toString())) != null) {
                        // If the entry already exists, remove it
                        favouriteList.remove(existing);
                    } else {
                        // If the entry doesn't exist, add it to the combo box
                        favourites.addItem(selected.toString());
                    }
                    favouriteList.add(new Favourite(
                            selected.toString(),
                            majorFractal.getAlgorithm(),
                            lastSelected,
                            majorFractal.getColourScheme()));;
                }
            }
        });
        removeFavourite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected;
                if ((selected = favourites.getSelectedItem()) != null) {
                    favouriteList.remove(favouriteList.getByName(selected.toString()));
                    favourites.removeItem(selected);
                }
            }
        });
        favourites.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object selected;
                if ((selected = favourites.getSelectedItem()) != null) {
                    // Switch the display to the selected favourite data
                    Favourite f = favouriteList.getByName(selected.toString());
                    if (f != null) {
                    	lastSelected = f.getSelected();
                        
                        FractalColourScheme scheme = f.getScheme();
                        majorFractal.setAlgorithm(f.getAlgorithm());
                        minorFractal.setAlgorithm(new JuliaAlgorithm(f.getAlgorithm(), lastSelected));
                        majorFractal.setColourScheme(scheme);
                        minorFractal.setColourScheme(scheme);
                    }
                }
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
            	lastSelected = fractal.getCartesian(e.getX(), e.getY());
                minorFractal.setAlgorithm(new JuliaAlgorithm(majorFractal.getAlgorithm(), lastSelected));
                selectedCoords.setText(lastSelected.round(3).toString());
                
                // Start a possible drag operation
                dragStart = e.getPoint();
            }
            
            /*
             * Handle crosshairs
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                fractal.paintCrosshairs(e.getPoint());
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
        this.setMinimumSize(new Dimension(439, 497));
        this.pack();
    }
    
    /**
     * @return The current fractal panel in the major fractal wrapper
     */
    public FractalPanel getMajorPanel() {
        return (FractalPanel) majorWrapper.getComponent(0);
    }

    /**
     * @param majorPanel The fractal panel to put in the major fractal wrapper
     */
    public void setMajorPanel(FractalPanel majorPanel) {
        majorWrapper.removeAll();
        majorWrapper.add(majorPanel);
    }

    /**
     * @return The current fractal panel in the minor fractal wrapper
     */
    public FractalPanel getMinorPanel() {
        return (FractalPanel) minorWrapper.getComponent(0);
    }

    /**
     * @param minorPanel The fractal panel to put in the minor fractal wrapper
     */
    public void setMinorPanel(FractalPanel minorPanel) {
        minorWrapper.removeAll();
        minorWrapper.add(minorPanel);
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
            
            // Full screen escape
            frame.addKeyListener(new KeyListener() {
                
                @Override
                public void keyPressed(KeyEvent e) {
                    // Exit full screen when ESC pressed
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
                GraphicsConfiguration config = FractalExplorer.this.getGraphicsConfiguration();
                device = config.getDevice();

                // Remove the fractal from the explorer wrapper
                majorWrapper.remove(majorFractal);
                // Add the fractal to the full screen frame
                frame.getContentPane().add(majorFractal);
                // Set the full screen window
                device.setFullScreenWindow(frame);
                isFullScreen = true;
            }
        }
        
        public void exitFullScreen() {
            if (isFullScreen) {
                // Remove the fractal from the full screen frame
                frame.getContentPane().remove(majorFractal);
                // Add the fractal back to the explorer wrapper
                majorWrapper.add(majorFractal);
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