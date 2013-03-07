package com.cjwatts.fractalexplorer.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.FractalPanel;
import com.cjwatts.fractalexplorer.main.algorithms.JuliaAlgorithm;
import com.cjwatts.fractalexplorer.main.algorithms.MandelbrotAlgorithm;
import com.cjwatts.fractalexplorer.main.util.AxisSpinner;
import com.cjwatts.fractalexplorer.main.util.Complex;
import com.cjwatts.fractalexplorer.main.util.IterationSpinner;

public class TestFrame extends JFrame {
    
    private static final long serialVersionUID = 2589672828891645291L;
    
    private JPanel fractalWrapper;
    protected JPanel majorFractal, minorFractal;
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e1) {
        } catch (InstantiationException e1) {
        } catch (IllegalAccessException e1) {
        } catch (UnsupportedLookAndFeelException e1) {
        }
        
        EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                try {
                    TestFrame frame = new TestFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Create the frame.
     */
    public TestFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(content);
        
        // Generate major fractal panel
        majorFractal = new JPanel();
        majorFractal.setBackground(Color.BLACK);
        
        // Put the fractal in a wrapper for simple full screening
        fractalWrapper = new JPanel();
        fractalWrapper.setLayout(new BorderLayout());
        fractalWrapper.add(majorFractal);
        
        // Generate minor fractal pattern
        Complex initSeed = new Complex(0, 0);
        minorFractal = new JPanel();
        minorFractal.setBackground(Color.BLACK);
        
        Font textFont = new Font("Arial", 0, 11);
        
        // Generate selected coordinate label
        final JLabel selectedCoords = new JLabel(initSeed.toString());
        selectedCoords.setFont(textFont);
        
        // Generate progress bar
        JProgressBar loadProgress = new JProgressBar();
        
        // Generate view buttons
        JButton swap = new JButton("Swap Views");
        swap.setFont(textFont);
        JButton resetView = new JButton("Reset View");
        resetView.setFont(textFont);
        JButton fullScreen = new JButton("Full Screen");
        fullScreen.setFont(textFont);
        
        JPanel controls = new JPanel();
        
        // Generate algorithm chooser
        JLabel lblAlgorithm = new JLabel("Algorithm");
        lblAlgorithm.setFont(textFont);
        final JComboBox algorithm = new JComboBox();
        algorithm.setFont(textFont);
        // Add the algorithms to the combo box
        algorithm.addItem("Mandelbrot");
        algorithm.addItem("Burning Ship");
        algorithm.setSelectedItem("Mandelbrot");
        
        // Generate iteration control
        JLabel lblIterations = new JLabel("Iterations");
        lblIterations.setFont(textFont);
        final IterationSpinner iterations = new IterationSpinner();
        iterations.setFont(textFont);
        iterations.setValue(100);
        
        // Generate axes control
        JLabel lblReal = new JLabel("Real Axis");
        JLabel lblRealTo = new JLabel("to");
        JLabel lblImaginary = new JLabel("Imaginary Axis");
        JLabel lblImaginaryTo = new JLabel("to");
        
        lblReal.setFont(textFont);
        lblRealTo.setFont(textFont);
        lblImaginary.setFont(textFont);
        lblImaginaryTo.setFont(textFont);
        
        final AxisSpinner realFrom = new AxisSpinner();
        final AxisSpinner realTo = new AxisSpinner();
        final AxisSpinner imaginaryFrom = new AxisSpinner();
        final AxisSpinner imaginaryTo = new AxisSpinner();
        
        realFrom.setFont(textFont);
        realTo.setFont(textFont);
        imaginaryFrom.setFont(textFont);
        imaginaryTo.setFont(textFont);
        
        realFrom.setValue(-2.00);
        realTo.setValue(2.00);
        imaginaryFrom.setValue(-1.60);
        imaginaryTo.setValue(1.60);
        
        // Generate colour scheme editor
        JPanel colourSchemePanel = new JPanel();
        JLabel lblColourScheme = new JLabel("Colour Scheme");
        lblColourScheme.setFont(textFont);
        
        final JComboBox colourScheme = new JComboBox();
        colourScheme.setFont(textFont);
        colourScheme.addItem("Default");
        colourScheme.addItem("Blue Sky");
        colourScheme.addItem("Red Sky");
        colourScheme.addItem("Sea of Gold");
        
        GroupLayout controlLayout = new GroupLayout(controls);
        controlLayout.setHorizontalGroup(
            controlLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(controlLayout.createSequentialGroup()
                    .addGroup(controlLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblAlgorithm)
                        .addGroup(controlLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(algorithm, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblIterations)
                        .addGroup(controlLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(iterations, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
                    .addGap(18)
                    .addGroup(controlLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblImaginary)
                        .addGroup(controlLayout.createSequentialGroup()
                            .addComponent(realFrom, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblRealTo)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(realTo, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
                        .addComponent(lblReal)
                        .addGroup(controlLayout.createSequentialGroup()
                            .addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblImaginaryTo)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)))
                    .addGap(82))
        );
        controlLayout.setVerticalGroup(
            controlLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(controlLayout.createSequentialGroup()
                    .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblAlgorithm)
                        .addComponent(lblReal, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(algorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(realFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblRealTo)
                        .addComponent(realTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblIterations)
                        .addComponent(lblImaginary))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(controlLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(iterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblImaginaryTo)
                        .addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        controls.setLayout(controlLayout);
        
        GroupLayout contentLayout = new GroupLayout(content);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(contentLayout.createSequentialGroup()
                    .addGroup(contentLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(contentLayout.createSequentialGroup()
                            .addComponent(controls, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(contentLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(contentLayout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(resetView, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(fullScreen, GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                                .addComponent(swap, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
                            .addGap(9))
                        .addComponent(fractalWrapper, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                        .addComponent(loadProgress, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(contentLayout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(selectedCoords, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                        .addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                        .addComponent(minorFractal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(contentLayout.createSequentialGroup()
                    .addComponent(fractalWrapper, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(loadProgress, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
                    .addGap(7)
                    .addGroup(contentLayout.createParallelGroup(Alignment.TRAILING, false)
                        .addGroup(contentLayout.createSequentialGroup()
                            .addComponent(swap)
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(resetView)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(fullScreen))
                        .addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addGroup(contentLayout.createSequentialGroup()
                    .addComponent(minorFractal, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(selectedCoords)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
        );
        
        GroupLayout colorSchemeLayout = new GroupLayout(colourSchemePanel);
        colorSchemeLayout.setHorizontalGroup(
            colorSchemeLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(colorSchemeLayout.createSequentialGroup()
                    .addGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(lblColourScheme)
                        .addGroup(colorSchemeLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(colourScheme, 0, 158, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        colorSchemeLayout.setVerticalGroup(
            colorSchemeLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(colorSchemeLayout.createSequentialGroup()
                    .addComponent(lblColourScheme)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(colourScheme, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(223, Short.MAX_VALUE))
        );
        colourSchemePanel.setLayout(colorSchemeLayout);
        content.setLayout(contentLayout);
    }
}
