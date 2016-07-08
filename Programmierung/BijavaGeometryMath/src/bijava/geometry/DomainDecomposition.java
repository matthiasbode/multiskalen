package bijava.geometry;

import java.util.*;

public class DomainDecomposition {
    
    Vector<NaturalElement> elements = new Vector<NaturalElement>();
    
    public void addElement(NaturalElement element) {
        if (!elements.contains(element))
            elements.addElement(element);
    }
    
    public NaturalElement getElement(int i) {
        return elements.elementAt(i);
    }
    
    public Enumeration allElements() {
        return elements.elements();
    }
    
    public int getNumberofElements() {
        return elements.size();
    }
}
