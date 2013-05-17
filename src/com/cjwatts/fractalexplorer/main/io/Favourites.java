package com.cjwatts.fractalexplorer.main.io;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cjwatts.fractalexplorer.main.FractalColourScheme;
import com.cjwatts.fractalexplorer.main.algorithms.BaseFractalAlgorithm;
import com.cjwatts.fractalexplorer.main.algorithms.MandelbrotAlgorithm;
import com.cjwatts.fractalexplorer.main.panels.FractalPanel;
import com.cjwatts.fractalexplorer.main.util.Complex;

/**
 * A sorted list of favourite fractals
 * 
 * Automatically saves to disk after modification
 */
public class Favourites implements List<Favourite> {
    
    private String filename = "favourites.xml";
    private List<Favourite> list = new ArrayList<Favourite>();
    
    /*
     * Warning: There is a LOT of nesting going on with the XML stuff Be
     * prepared for dragons! At least it's stable - stupid SnakeYAML
     */
    
    /**
     * Load the favourites file into memory
     * @throws IOException
     */
    public void load() throws IOException {
        try {
            // Get XML document
            // (thanks to
            // http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/)
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filename);
            
            // Where to store loaded favourites before adding to list
            List<Favourite> loaded = new ArrayList<Favourite>();
            
            // Required node level objects
            Node n1, n2, n3, n4, n5;
            // Required node list level objects
            NodeList l0, l1, l2, l3, l4;
            
            // <favourites>
            l0 = doc.getElementsByTagName("favourite");
            
            // Attributes to attain
            String name = null;
            BaseFractalAlgorithm algorithm = null;
            Complex selected = null;
            Complex[] bounds = new Complex[2];
            FractalColourScheme scheme = null;
            
            for (int i = 0; i < l0.getLength(); i++) {
                // <favourite>
                n1 = l0.item(i);
                name = n1.getAttributes().getNamedItem("name").getTextContent();
                l1 = n1.getChildNodes();
                for (int j = 0; j < l1.getLength(); j++) {
                    n2 = l1.item(j);
                    // <algorithm>
                    if (n2.getNodeName().equals("algorithm")) {
                        l2 = n2.getChildNodes();
                        
                        // Attibutes to attain
                        String aName = null;
                        Integer aIterations = null;
                        Double aEscapeRadius = null;
                        
                        aName = n2.getAttributes().getNamedItem("name").getTextContent();
                        for (int k = 0; k < l2.getLength(); k++) {
                            n3 = l2.item(k);
                            // <iterations>
                            if (n3.getNodeName().equals("iterations")) {
                                aIterations = Integer.parseInt(n3.getTextContent());
                            }
                            // <escaperadius>
                            else if (n3.getNodeName().equals("escaperadius")) {
                                aEscapeRadius = Double.parseDouble(n3.getTextContent());
                            }
                        }
                        algorithm = BaseFractalAlgorithm.getByName(aName);
                        // Panic default
                        if (algorithm == null)
                            algorithm = new MandelbrotAlgorithm();
                        algorithm.setIterations(aIterations);
                        algorithm.setEscapeRadius(aEscapeRadius);
                    }
                    // </algorithm>
                    // <selected>
                    if (n2.getNodeName().equals("selected")) {
                        l2 = n2.getChildNodes();
                        
                        // Attibutes to attain
                        Double sReal = null, sImaginary = null;
                        
                        for (int k = 0; k < l2.getLength(); k++) {
                            n3 = l2.item(k);
                            // <real>
                            if (n3.getNodeName().equals("real")) {
                                sReal = Double.parseDouble(n3.getTextContent());
                            }
                            // <imaginary>
                            else if (n3.getNodeName().equals("imaginary")) {
                                sImaginary = Double.parseDouble(n3.getTextContent());
                            }
                        }
                        selected = new Complex(sReal, sImaginary);
                    }
                    // </selected>
                    // <bounds>
                    if (n2.getNodeName().equals("bounds")) {
                        l2 = n2.getChildNodes();
                        
                        // Attibutes to attain
                        Double aReal = null, aImaginary = null;
                        Double bReal = null, bImaginary = null;
                        
                        for (int k = 0; k < l2.getLength(); k++) {
                            n3 = l2.item(k);
                            // <bottomleft>
                            if (n3.getNodeName().equals("bottomleft")) {
                                l3 = n3.getChildNodes();
                                for (int l = 0; l < l3.getLength(); l++) {
                                    n4 = l3.item(l);
                                    // <real>
                                    if (n4.getNodeName().equals("real")) {
                                        aReal = Double.parseDouble(n4.getTextContent());
                                    }
                                    // <imaginary>
                                    else if (n4.getNodeName().equals("imaginary")) {
                                        aImaginary = Double.parseDouble(n4.getTextContent());
                                    }
                                }
                            }
                            // </bottomleft>
                            // <topright>
                            else if (n3.getNodeName().equals("topright")) {
                                l3 = n3.getChildNodes();
                                for (int l = 0; l < l2.getLength(); l++) {
                                    n4 = l3.item(l);
                                    // <real>
                                    if (n4.getNodeName().equals("real")) {
                                        bReal = Double.parseDouble(n4.getTextContent());
                                    }
                                    // <imaginary>
                                    else if (n4.getNodeName().equals("imaginary")) {
                                        bImaginary = Double.parseDouble(n4.getTextContent());
                                    }
                                }
                            }
                            //</topright>
                        }
                        if (aReal == null || aImaginary == null || bReal == null || bImaginary == null) {
                            aReal = FractalPanel.DEFAULT_REAL_MIN;
                            aImaginary = FractalPanel.DEFAULT_IMAGINARY_MIN;
                            bReal = FractalPanel.DEFAULT_REAL_MAX;
                            bImaginary = FractalPanel.DEFAULT_IMAGINARY_MAX;
                        }
                        bounds[0] = new Complex(aReal, aImaginary);
                        bounds[1] = new Complex(bReal, bImaginary);
                    }
                    // </bounds>
                    // <scheme>
                    if (n2.getNodeName().equals("scheme")) {
                        l2 = n2.getChildNodes();
                        
                        // Attributes should be fed directly into the colour
                        // scheme
                        scheme = new FractalColourScheme();
                        
                        for (int k = 0; k < l2.getLength(); k++) {
                            n3 = l2.item(k);
                            // <gridlines>
                            if (n3.getNodeName().equals("gridlines")) {
                                scheme.setGridlineColour(deserialiseColour(n3.getTextContent()));
                            }
                            // <colours>
                            else if (n3.getNodeName().equals("colours")) {
                                l3 = n3.getChildNodes();
                                for (int l = 0; l < l3.getLength(); l++) {
                                    // <colourstop>
                                    n4 = l3.item(l);
                                    l4 = n4.getChildNodes();
                                    
                                    // Attibutes to attain
                                    Double csProgress = 0.0;
                                    Color csColour = Color.BLACK;
                                    
                                    for (int m = 0; m < l4.getLength(); m++) {
                                        n5 = l4.item(m);
                                        // <progress>
                                        if (n5.getNodeName().equals("progress")) {
                                            csProgress = Double.parseDouble(n5.getTextContent());
                                        }
                                        // <colour>
                                        else if (n5.getNodeName().equals("colour")) {
                                            csColour = deserialiseColour(n5.getTextContent());
                                        }
                                    }
                                    scheme.addColourStop(csProgress, csColour);
                                    // </colourstop>
                                }
                            }
                            // </colours>
                        }
                    }
                    // </scheme>
                }
                // Commit the data
                loaded.add(new Favourite(name, algorithm, selected, bounds, scheme));
            }
            // Request a cleanup of all those objects now
            System.gc();
            
            // Sort the favourites by name
            Collections.sort(loaded);
            
            // Add favourites to list - list will be re-saved
            list.addAll(loaded);
            
            // Sort once more in case the list already had items in it
            Collections.sort(list);
            
        } catch (Exception ex) {
            // Re-throw XML generation exceptions as IOExceptions
            if (ex instanceof IOException)
                throw (IOException) ex;
            else
                throw new IOException(ex);
        }
    }
    
    /**
     * Save the favourites file to disk
     * @throws IOException
     */
    public void save() throws IOException {
        try {
            // Generate XML builder
            // (thanks to
            // http://www.mkyong.com/java/how-to-create-xml-file-in-java-dom/)
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            
            // Required element level objects
            Element e0, e1, e2, e3, e4, e5;
            
            // <favourites>
            e0 = doc.createElement("favourites");
            for (Favourite f : list) {
                // <favourite name="">
                e1 = doc.createElement("favourite");
                e1.setAttribute("name", f.getName());
                // <algorithm name="">
                e2 = doc.createElement("algorithm");
                e2.setAttribute("name", f.getAlgorithm().getName());
                // <iterations>
                e3 = doc.createElement("iterations");
                e3.appendChild(doc.createTextNode(f.getAlgorithm().getIterations() + ""));
                e2.appendChild(e3);
                // <escaperadius>
                e3 = doc.createElement("escaperadius");
                e3.appendChild(doc.createTextNode(f.getAlgorithm().getEscapeRadius() + ""));
                e2.appendChild(e3);
                // </algorithm>
                e1.appendChild(e2);
                // <selected>
                e2 = doc.createElement("selected");
                // <real>
                e3 = doc.createElement("real");
                e3.appendChild(doc.createTextNode(f.getSelected().real() + ""));
                e2.appendChild(e3);
                // <imaginary>
                e3 = doc.createElement("imaginary");
                e3.appendChild(doc.createTextNode(f.getSelected().imaginary() + ""));
                e2.appendChild(e3);
                // </selected>
                // <bounds>
                e2 = doc.createElement("bounds");
                // <bottomleft>
                e3 = doc.createElement("bottomleft");
                // <real>
                e4 = doc.createElement("real");
                e4.appendChild(doc.createTextNode(f.getBounds()[0].real() + ""));
                e3.appendChild(e4);
                // <imaginary>
                e4 = doc.createElement("imaginary");
                e4.appendChild(doc.createTextNode(f.getBounds()[0].imaginary() + ""));
                e3.appendChild(e4);
                e2.appendChild(e3);
                // </bottomleft>
                // <topright>
                e3 = doc.createElement("topright");
                // <real>
                e4 = doc.createElement("real");
                e4.appendChild(doc.createTextNode(f.getBounds()[1].real() + ""));
                e3.appendChild(e4);
                // <imaginary>
                e4 = doc.createElement("imaginary");
                e4.appendChild(doc.createTextNode(f.getBounds()[1].imaginary() + ""));
                e3.appendChild(e4);
                // </topright>
                e2.appendChild(e3);
                // </bounds>
                e1.appendChild(e2);
                // <scheme>
                e2 = doc.createElement("scheme");
                // <gridlines>
                e3 = doc.createElement("gridlines");
                e3.appendChild(doc.createTextNode(serialiseColour(f.getScheme().getGridlineColour())));
                e2.appendChild(e3);
                // <colours>
                e3 = doc.createElement("colours");
                for (Map.Entry<Double, Color> entry : f.getScheme().entrySet()) {
                    // <colourstop>
                    e4 = doc.createElement("colourstop");
                    // <progress>
                    e5 = doc.createElement("progress");
                    e5.appendChild(doc.createTextNode(entry.getKey() + ""));
                    e4.appendChild(e5);
                    // <colour>
                    e5 = doc.createElement("colour");
                    e5.appendChild(doc.createTextNode(serialiseColour(entry.getValue())));
                    e4.appendChild(e5);
                    // </colourstop>
                    e3.appendChild(e4);
                }
                // </colours>
                e2.appendChild(e3);
                // </scheme>
                e1.appendChild(e2);
                // </favourite>
                e0.appendChild(e1);
            }
            // </favourites>
            doc.appendChild(e0);
            
            // Request a cleanup of all those objects now
            System.gc();
            
            // Prepare Optimus Prime
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            // Write output
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(filename);
            transformer.transform(source, result);
            
        } catch (Exception ex) {
            // Re-throw XML generation exceptions as IOExceptions
            if (ex instanceof IOException)
                throw (IOException) ex;
            else
                throw new IOException(ex);
        }
    }
    
    /**
     * @return String representation of colour for nice XML
     */
    private String serialiseColour(Color c) {
        return c.getRed() + " " + c.getGreen() + " " + c.getBlue();
    }
    
    /**
     * @param c Serialised colour string
     * @return Colour from string
     * @throws NumberFormatException if colour not valid
     */
    private Color deserialiseColour(String c) throws NumberFormatException {
        String[] parts = c.split(" ");
        int r, g, b, a;
        Color result;
        if (parts.length >= 3) {
            r = Integer.parseInt(parts[0]);
            g = Integer.parseInt(parts[1]);
            b = Integer.parseInt(parts[2]);
            result = new Color(r, g, b);
        } else {
            throw new NumberFormatException(c + " is not a valid colour!");
        }
        // Bonus: Secret alpha channel support
        if (parts.length == 4) {
            a = Integer.parseInt(parts[3]);
            result = new Color(r, g, b, a);
        }
        return result;
    }
    
    @Override
    public boolean add(Favourite arg0) {
        boolean result = list.add(arg0);
        if (result)
            try {
                Collections.sort(list);
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
    }
    
    @Override
    public void add(int arg0, Favourite arg1) {
        list.add(arg0, arg1);
        try {
            save();
        } catch (IOException ex) {
            System.err.println("Autosave failed - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    @Override
    public boolean addAll(Collection<? extends Favourite> arg0) {
        boolean result = list.addAll(arg0);
        if (result)
            try {
                Collections.sort(list);
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
    }
    
    @Override
    public boolean addAll(int arg0, Collection<? extends Favourite> arg1) {
        boolean result = list.addAll(arg0, arg1);
        if (result)
            try {
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
    }
    
    @Override
    public void clear() {
        list.clear();
        try {
            save();
        } catch (IOException ex) {
            System.err.println("Autosave failed - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    @Override
    public boolean contains(Object arg0) {
        return list.contains(arg0);
    }
    
    @Override
    public boolean containsAll(Collection<?> arg0) {
        return list.containsAll(arg0);
    }
    
    @Override
    public Favourite get(int arg0) {
        return list.get(arg0);
    }
    
    /**
     * Return an entry by its name
     * @param name
     */
    public Favourite getByName(String name) {
        return getByName(name, 0, list.size() - 1);
    }
    
    /**
     * Recursive helper function for binary search
     * @param name
     * @param lower Lower bound of search
     * @param upper Upper bound of search
     */
    private Favourite getByName(String name, int lower, int upper) {
        if (upper - lower < 0)
            return null;
        // Compare the middle element to the search name
        int mid = (lower + upper) / 2;
        int compare = name.compareTo(list.get(mid).getName());
        
        if (compare < 0)
            return getByName(name, lower, mid - 1);
        else if (compare > 0)
            return getByName(name, mid + 1, upper);
        else
            return list.get(mid);
    }
    
    @Override
    public int indexOf(Object arg0) {
        return list.indexOf(arg0);
    }
    
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    @Override
    public Iterator<Favourite> iterator() {
        return list.iterator();
    }
    
    @Override
    public int lastIndexOf(Object arg0) {
        return list.lastIndexOf(arg0);
    }
    
    @Override
    public ListIterator<Favourite> listIterator() {
        return list.listIterator();
    }
    
    @Override
    public ListIterator<Favourite> listIterator(int arg0) {
        return list.listIterator(arg0);
    }
    
    @Override
    public boolean remove(Object arg0) {
        boolean result = list.remove(arg0);
        if (result)
            try {
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
    }
    
    @Override
    public Favourite remove(int arg0) {
        Favourite result = list.remove(arg0);
        try {
            save();
        } catch (IOException ex) {
            System.err.println("Autosave failed - " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }
    
    @Override
    public boolean removeAll(Collection<?> arg0) {
        boolean result = list.removeAll(arg0);
        if (result)
            try {
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
    }
    
    @Override
    public boolean retainAll(Collection<?> arg0) {
        boolean result = list.retainAll(arg0);
        if (result)
            try {
                save();
            } catch (IOException ex) {
                System.err.println("Autosave failed - " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        return result;
        
    }
    
    @Override
    public Favourite set(int arg0, Favourite arg1) {
        Favourite result = list.set(arg0, arg1);
        try {
            save();
        } catch (IOException ex) {
            System.err.println("Autosave failed - " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }
    
    @Override
    public int size() {
        return list.size();
    }
    
    @Override
    public List<Favourite> subList(int arg0, int arg1) {
        return list.subList(arg0, arg1);
    }
    
    @Override
    public Object[] toArray() {
        return list.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] arg0) {
        return list.toArray(arg0);
    }
    
}
