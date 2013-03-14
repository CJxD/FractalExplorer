package com.cjwatts.fractalexplorer.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.algorithms.BaseFractalAlgorithm;
import com.cjwatts.fractalexplorer.main.algorithms.MandelbrotAlgorithm;
import com.cjwatts.fractalexplorer.main.panels.FractalPanel;
import com.cjwatts.fractalexplorer.main.util.Complex;

/**
 * A static class to make a benchmark of the time taken to render the Mandelbrot algorithm
 * Reports a breakdown of time usage in CSV format
 */
public class TimeBenchmark {
	static int n;
	static int width = 300;
	static int height = 300;
	static String filename = "benchmark.csv";
	
	public static void main(String[] args) {
		// args[0] = Number of times to complete - default 10
		try {
			n = Integer.parseInt(args[0]);
		} catch (Exception e) {
			n = 10;
		}
		
		// Set up fractal
		FractalColourScheme scheme = FractalColourScheme.DEFAULT;
		BaseFractalAlgorithm algorithm = new MandelbrotAlgorithm(100, 2);
		FractalPanel fractal = new FractalPanel(algorithm);
		fractal.setColourScheme(scheme);
		fractal.setSize(width, height);
		
		Complex test1 = new Complex(-1.995, 1.55925);
		Complex test2 = new Complex(-0.931647, -0.66725521);
		Complex test3 = new Complex(0.151654, 0.313412);
		
		int tileX = 50, tileY = FractalPanel.Renderer.MAX_TILE_AREA / 50;
		
		// Get classes
		Class<Complex> cClass = Complex.class;
		Class<BaseFractalAlgorithm> faClass = BaseFractalAlgorithm.class;
		Class<FractalPanel> fpClass = FractalPanel.class;
		Class<FractalPanel.Renderer> fprClass = FractalPanel.Renderer.class;
		Class<FractalColourScheme> fcsClass = FractalColourScheme.class;
		
		// Set up results
		double[] complexAdd = new double[n];
		double[] complexSquare = new double[n];
		double[] complexMod = new double[n];
		double[] coordinateConversion = new double[n];
		double[] algorithmTime = new double[n];
		double[] colourMapping = new double[n];
		double[] pixelRender = new double[n];
		double[] tileRender = new double[n];
		double[] totalRender = new double[n];
		
		// Loop through n tests
		for (int i = 0; i < n; i++) {
			try {
				// Complex addition
				complexAdd[i] = getTime(
						cClass.getMethod("add", cClass),
						test2,
						test3);
				
				// Complex squaring
				complexSquare[i] = getTime(
						cClass.getMethod("square"),
						test2);
				
				// Complex modulus
				complexMod[i] = getTime(
						cClass.getMethod("modulusSquared"),
						test2);
				
				// Arbitrary coordinate conversion
				coordinateConversion[i] = getTime(
						fpClass.getMethod("getCartesian", int.class, int.class),
						fractal,
						width / 3,
						height / 3);
				
				// Fully diverging complex number
				algorithmTime[i] = getTime(
						faClass.getMethod("divergenceRatio", cClass),
						algorithm,
						test1);
				
				// Diverging complex number
				algorithmTime[i] += getTime(
						faClass.getMethod("divergenceRatio", cClass),
						algorithm,
						test2);
				
				// Non-diverging complex number
				algorithmTime[i] += getTime(
						faClass.getMethod("divergenceRatio", cClass),
						algorithm,
						test3);
				
				// Average
				algorithmTime[i] /= 3;
				
				// Fully diverging colour
				colourMapping[i] = getTime(
						fcsClass.getMethod("calculateColour", double.class),
						scheme,
						0.0);
				
				// Diverging colour
				colourMapping[i] += getTime(
						fcsClass.getMethod("calculateColour", double.class),
						scheme,
						0.667);
				
				// Non-diverging colour
				colourMapping[i] += getTime(
						fcsClass.getMethod("calculateColour", double.class),
						scheme,
						1.0);
				
				// Average
				colourMapping[i] /= 3;
				
				// Fully diverging pixel
				pixelRender[i] = getTime(
						fpClass.getMethod("getPixelColour", int.class, int.class),
						fractal,
						width,
						height);
				
				// Diverging pixel
				pixelRender[i] = getTime(
						fpClass.getMethod("getPixelColour", int.class, int.class),
						fractal,
						width / 3,
						(2 * height) / 5);
				
				// Non-diverging pixel
				pixelRender[i] = getTime(
						fpClass.getMethod("getPixelColour", int.class, int.class),
						fractal,
						width / 2,
						height / 2);
				
				// Average
				pixelRender[i] /= 3;
				
				// Tile rendering
				tileRender[i] = getTime(
						fprClass.getMethod("run"),
						fractal.new Renderer(0, 0, tileX, tileY));
				
				// Total rendering
				totalRender[i] = getTime(
						fpClass.getMethod("repaint"),
						fractal);
				
			} catch (InvocationTargetException ex) {
				// Abandon this test if it fails
				// If the exception is in the program, print it out
				Throwable ex2 = ex.getTargetException();
				System.err.println("Test " + i + " failed: " + ex2.getMessage());
				ex2.printStackTrace();
				n--;
				
			} catch (Exception ex) {
				// Abandon this test if it fails
				System.err.println("Test " + i + " failed: " + ex.getMessage());
				ex.printStackTrace();
				n--;
			}
		}
		
		// Headers
		String output = "Complex Add,Complex Square,Complex Mod,Coordinate Conversion,Algorithm Time,Colour Mapping,Pixel Render,Tile Render,Total Render\n";
		
		// Sort test results
		Arrays.sort(complexAdd);
		Arrays.sort(complexSquare);
		Arrays.sort(complexMod);
		Arrays.sort(coordinateConversion);
		Arrays.sort(algorithmTime);
		Arrays.sort(colourMapping);
		Arrays.sort(pixelRender);
		Arrays.sort(tileRender);
		Arrays.sort(totalRender);
		
		// Take median of tests and print
		output += complexAdd[n/2] + ",";
		output += complexSquare[n/2] + ",";
		output += complexMod[n/2] + ",";
		output += coordinateConversion[n/2] + ",";
		output += algorithmTime[n/2] + ",";
		output += colourMapping[n/2] + ",";
		output += pixelRender[n/2] + ",";
		output += tileRender[n/2] + ",";
		output += totalRender[n/2];
		
		try {
			// Write to file
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(output);
			writer.close();
			System.out.println("Results written to " + filename);
		} catch (IOException ex) {
			System.err.println("Could not write to file, displaying instead:");
			System.out.println(output);
		}
	}
	
	/**
	 * @param m Method to time
	 * @param obj Object to invoke method on
	 * @param params Parameters to pass
	 * @return Seconds taken
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static double getTime(Method m, Object obj, Object... params) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		double start = System.nanoTime();
		m.invoke(obj, params);
		return (System.nanoTime() - start) / 1000000000.0;
	}
}
