package com.cjwatts.fractalexplorer.test;

import java.awt.Color;
import java.awt.EventQueue;

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

public class TestFrame extends JFrame {
    
    private static final long serialVersionUID = 2589672828891645291L;
    private JPanel content;
    
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
        content = new JPanel();
        content.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(content);
        
        // Generate major fractal panel
        JPanel majorFractal = new JPanel();
        majorFractal.setBackground(Color.BLUE);
        
        // Generate minor fractal pattern
        JPanel minorFractal = new JPanel();
        minorFractal.setBackground(Color.BLUE);
        
        // Generate coordinate labels
        final JLabel hoverCoords = new JLabel("1.234-5.678i");
        final JLabel selectedCoords = new JLabel("3.456+7.890i");
        
        // Generate progress bar
        JProgressBar loadProgress = new JProgressBar();
        
        // Generate view buttons
        JButton resetView = new JButton("Reset View");
        JButton fullScreen = new JButton("Full Screen");
        
        JPanel controls = new JPanel();
        
        // Generate algorithm chooser
        JLabel lblAlgorithm = new JLabel("Algorithm");
        final JComboBox algorithm = new JComboBox();
        // Add the algorithms to the combo box
        algorithm.addItem("Mandelbrot");
        algorithm.addItem("Burning Ship");
        algorithm.setSelectedItem("Mandelbrot");
        
        // Generate iteration control
        JLabel lblIterations = new JLabel("Iterations");
        final JSpinner iterations = new JSpinner();
        iterations.setValue(100);
        
        // Generate axes control
        JLabel lblReal = new JLabel("Real Axis");
        JLabel lblRealTo = new JLabel("to");
        JLabel lblImaginary = new JLabel("Imaginary Axis");
        JLabel lblImaginaryTo = new JLabel("to");
        
        final JSpinner realFrom = new JSpinner();
        final JSpinner realTo = new JSpinner();
        final JSpinner imaginaryFrom = new JSpinner();
        final JSpinner imaginaryTo = new JSpinner();
        
        // Format axes spinners
        ((JSpinner.NumberEditor) realFrom.getEditor()).getFormat().setMinimumFractionDigits(2);
        ((JSpinner.NumberEditor) realTo.getEditor()).getFormat().setMinimumFractionDigits(2);
        ((JSpinner.NumberEditor) imaginaryFrom.getEditor()).getFormat().setMinimumFractionDigits(2);
        ((JSpinner.NumberEditor) imaginaryTo.getEditor()).getFormat().setMinimumFractionDigits(2);
        
        realFrom.setValue(-2.00);
        realTo.setValue(2.00);
        imaginaryFrom.setValue(-1.60);
        imaginaryTo.setValue(1.60);
        
        // Generate colour scheme editor
        JPanel colourSchemePanel = new JPanel();
        JLabel lblColourScheme = new JLabel("Colour Scheme");
        JComboBox colourScheme = new JComboBox();
        
        GroupLayout controlLayout = new GroupLayout(controls);
        controlLayout.setHorizontalGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addGroup(controlLayout.createParallelGroup(Alignment.LEADING).addComponent(lblAlgorithm).addGroup(controlLayout.createSequentialGroup().addContainerGap().addComponent(algorithm, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)).addComponent(lblIterations).addGroup(controlLayout.createSequentialGroup().addContainerGap().addComponent(iterations, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))).addGap(38).addGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addComponent(realFrom, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblRealTo).addPreferredGap(ComponentPlacement.RELATED).addComponent(realTo, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)).addComponent(lblReal).addComponent(lblImaginary).addGroup(controlLayout.createSequentialGroup().addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(lblImaginaryTo).addPreferredGap(ComponentPlacement.RELATED).addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        controlLayout.setVerticalGroup(controlLayout.createParallelGroup(Alignment.LEADING).addGroup(controlLayout.createSequentialGroup().addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblAlgorithm).addComponent(lblReal)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(algorithm, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(realFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblRealTo).addComponent(realTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblIterations).addComponent(lblImaginary)).addPreferredGap(ComponentPlacement.RELATED).addGroup(controlLayout.createParallelGroup(Alignment.BASELINE).addComponent(iterations, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(imaginaryFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblImaginaryTo).addComponent(imaginaryTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        controls.setLayout(controlLayout);
        
        GroupLayout contentLayout = new GroupLayout(content);
        contentLayout.setHorizontalGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addComponent(loadProgress, GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(hoverCoords, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)).addGroup(contentLayout.createSequentialGroup().addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(18).addGroup(contentLayout.createParallelGroup(Alignment.LEADING).addComponent(fullScreen, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE).addComponent(resetView, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))).addComponent(majorFractal, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(contentLayout.createParallelGroup(Alignment.LEADING, false).addComponent(selectedCoords, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE).addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE).addComponent(minorFractal, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
        contentLayout.setVerticalGroup(contentLayout.createParallelGroup(Alignment.LEADING).addGroup(contentLayout.createSequentialGroup().addComponent(majorFractal, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE).addGap(6).addGroup(contentLayout.createParallelGroup(Alignment.TRAILING).addComponent(loadProgress, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE).addComponent(hoverCoords)).addGap(7).addGroup(contentLayout.createParallelGroup(Alignment.TRAILING).addGroup(contentLayout.createSequentialGroup().addComponent(resetView).addPreferredGap(ComponentPlacement.RELATED).addComponent(fullScreen).addGap(18)).addComponent(controls, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addGroup(contentLayout.createSequentialGroup().addComponent(minorFractal, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(selectedCoords).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(colourSchemePanel, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)));
        
        GroupLayout colorSchemeLayout = new GroupLayout(colourSchemePanel);
        colorSchemeLayout.setHorizontalGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addGroup(colorSchemeLayout.createSequentialGroup().addGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addComponent(lblColourScheme).addGroup(colorSchemeLayout.createSequentialGroup().addGap(10).addComponent(colourScheme, 0, 158, Short.MAX_VALUE))).addContainerGap()));
        colorSchemeLayout.setVerticalGroup(colorSchemeLayout.createParallelGroup(Alignment.LEADING).addGroup(colorSchemeLayout.createSequentialGroup().addComponent(lblColourScheme).addPreferredGap(ComponentPlacement.RELATED).addComponent(colourScheme, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap(223, Short.MAX_VALUE)));
        colourSchemePanel.setLayout(colorSchemeLayout);
        content.setLayout(contentLayout);
    }
}
