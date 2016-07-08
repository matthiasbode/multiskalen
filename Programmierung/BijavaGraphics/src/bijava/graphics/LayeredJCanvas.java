package bijava.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;

//==========================================================================//
/**
 * Die Klasse "LayeredJCanvas" stelle Eigenschaften und Methoden f&uuml;r Leinw&auml;de
 * mit verschiednen Layern zur Verf&uuml;gung.
 * 
 * <p>
 * <strong>Version:</strong><br>
 * <dd>1.1, November 2004</dd>
 * </p>
 * <p>
 * <strong>Author:</strong><br>
 * <dd>Universit&auml;t Hannover</dd>
 * <dd>Institut f&uuml;r Bauinformatik</dd>
 * <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 * <dd>Dr.-Ing. Martin Rose</dd>
 * <dd>cand.-Ing. Jan Stilhammer</dd>
 * </p>
 */
// ==========================================================================//
public class LayeredJCanvas extends JComponent {
    Vector<Layer> layers;
    public static final String BASE_LAYER = "base_layer";
    private static final IllegalArgumentException LAYER_NOT_FOUND = new IllegalArgumentException("Layer nicht gefunden");

    // --------------------------------------------------------------------------//
    /**
     * Erzeugt eine leere Leinwand.
     */
    // --------------------------------------------------------------------------//
    public LayeredJCanvas() {
        layers = new Vector<Layer>();
        layers.add(new Layer(BASE_LAYER));
    }
    
    public void addLayer(String layerName){
        Layer layer = new Layer(layerName);
        if(layers.contains(layer)){
            throw new IllegalArgumentException("Layer dieses Namens bereits vorhanden");
        }else layers.add(layer);
    }

    // --------------------------------------------------------------------------//
    /**
     * Liefert den grafischen Kontext der Leinwand. Der grafische Kontext
     * entspricht dem Kontext des Puffers.
     */
    // --------------------------------------------------------------------------//
    public Graphics getGraphics(String LayerName) {
        Layer l = layers.get(getLayerIndex(LayerName));
        if (l.image == null)
            l.initImage();
        return l.gdp;
    }
    
    public int getLayerIndex(String layer){
        Iterator<Layer> iter = layers.iterator();
        int index = 0;
        while(iter.hasNext()){
            if(iter.next().name.equals(layer))return index;
            index++;
        }
        throw LAYER_NOT_FOUND;
    }
    
    public void setLayerPosition(String layer,int pos){
        if(pos==0)throw new IllegalArgumentException("Pos. 0 ist das BASE_LAYER, und kann nicht geaendert werden");
        else if(pos>layers.size())pos=layers.size()+1;
        Layer moveIt = layers.elementAt(getLayerIndex(layer));
        layers.remove(moveIt);
        layers.insertElementAt(moveIt,pos);
    }
    
    public void moveToTop(String layer){
        setLayerPosition(layer,layers.size());
    }
    
    public void moveToBottom(String layer){
        setLayerPosition(layer,1);
    }
    
    public void moveUp(String layer){
        if(getLayerIndex(layer)<layers.size())setLayerPosition(layer,getLayerIndex(layer)+1);
    }
    
    public void moveDown(String layer){
        if(getLayerIndex(layer)>1)setLayerPosition(layer,getLayerIndex(layer)-1);
    }
    
    public boolean isLayerVisible(String layer){
        return layers.elementAt(getLayerIndex(layer)).isVisible();
    }
    
    public void setLayerVisible(String layer, boolean mode){
        layers.elementAt(getLayerIndex(layer)).setVisible(mode);
    }

    // --------------------------------------------------------------------------//
    /**
     * Malt die Leinwand. Diese Methode wird bei Ver&auml;nderungen der
     * Komponente automatisch aufgerufen. Sie zeichnet das Pufferbild einfach
     * nochmal. Ist kein Pufferbild vorhanden, so wir mit Hilfe der Methode
     * "paintCanvas" ein aktuelles Pufferbild erzeugt. Die Methode kann nicht
     * &uuml;berladen werden.
     */
    // --------------------------------------------------------------------------//
    public final void paintComponent(Graphics g) {
//        System.out.println("Canvas: " +getSize().toString());
        System.out.println("paintComponent");
        //g.setPaintMode();
        Iterator<Layer> it = layers.iterator();
        while (it.hasNext()){
            Layer l = it.next();
            if (l.image == null) {
                l.initImage();
                //paintCanvas(l.gdp);
            }
            System.out.println("drawImage");
            if(l.isVisible())g.drawImage(l.image, 0, 0, this);
        }
        // System.out.println("paintComponent");
    }

    

    // --------------------------------------------------------------------------//
    /**
     * Malt die Leinwand. Um ein ableitetes Objekt der Klasse JCanvas zu
     * erhalten muss dieses Methode &uuml;berladen werden.
     * 
     * @param g
     *            grafischer Kontext, in den die Leinwand gemalt werden soll.
     */
    // --------------------------------------------------------------------------//
    public void paintCanvas() {
    }
    
    public void paintLayer(String layerName, Graphics g) {
    }

    // --------------------------------------------------------------------------//
    /**
     * &Uuml;bermalt die aktuelle Leinwand.
     */
    // --------------------------------------------------------------------------//
    public void repaint() {
        Iterator<Layer> it = layers.iterator();
        while (it.hasNext()){
            Layer l = it.next();
            if (l.image != null)
            paintCanvas();
        }
        super.repaint();
    }
    
    
    private class Layer{
        Graphics gdp; // grafischer Kontext des Puffers
        BufferedImage image; // Bildpuffer
        String name;
        boolean visible;

        public Layer(String name) {
            this.gdp = null;
            this.image = null;
            this.name = name;
            this.visible=true;
        }

        public void initImage() {
            System.out.println("Layer " + name +": " +getSize().toString());
            image = new BufferedImage(getSize().width, getSize().height, BufferedImage.BITMASK);
            
            gdp = image.getGraphics();            
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean mode) {
            this.visible = mode;
        }
        
        public boolean equals(Object obj){
            if (obj instanceof Layer) {
                Layer l = (Layer) obj;
                return (this.name.equals(l.name));
            } else
                return false;
        }
        

    }
}
