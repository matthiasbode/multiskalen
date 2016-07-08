package bijava.graphics;
import java.awt.*;
import javax.swing.*;
//==========================================================================//
/** Die Klasse "JCanvas" stelle Eigenschaften und Methoden
 *  f&uuml;r Leinw&auml;de zur Verf&uuml;gung, die doppelt gepuffert sind.
 *
 *  <p><strong>Version:</strong><br><dd>1.1, November 2004</dd></p>
 *  <p><strong>Author:</strong><br><dd>Universit&auml;t Hannover</dd>
 *                                 <dd>Institut f&uuml;r Bauinformatik</dd>
 *                                 <dd>Dr.-Ing. habil. Peter Milbradt</dd>
 *                                 <dd>Dr.-Ing. Martin Rose</dd></p>        */
//==========================================================================//
public class JCanvas extends JComponent
{ Graphics gdp;                              // grafischer Kontext des Puffers
  Image    image;                            //                     Bildpuffer

//--------------------------------------------------------------------------//
/** Erzeugt eine leere Leinwand.
                                           */
//--------------------------------------------------------------------------//
  public JCanvas() { gdp = null; image = null; }

//--------------------------------------------------------------------------//
/** Liefert den grafischen Kontext der Leinwand.
 *  Der grafische Kontext entspricht dem Kontext des Puffers.               */
//--------------------------------------------------------------------------//
  public Graphics getGraphics() { if(image == null) initImage(); return gdp; }

//--------------------------------------------------------------------------//
/** Malt die Leinwand.
 *  Diese Methode wird bei Ver&auml;nderungen der Komponente automatisch
 *  aufgerufen.
 *  Sie zeichnet das Pufferbild einfach nochmal.
 *  Ist kein Pufferbild vorhanden, so wir mit Hilfe der Methode "paintCanvas"
 *  ein aktuelles Pufferbild erzeugt.
 *  Die Methode kann nicht &uuml;berladen werden.                           */
//--------------------------------------------------------------------------//
  public final void paintComponent(Graphics g)
  { if(image == null) { initImage(); paintCanvas(gdp); }
    g.drawImage(image, 0, 0, this);
//    System.out.println("paintComponent");
  }

//--------------------------------------------------------------------------//
/** Erzeugt eine leere Leinwand.
                                           */
//--------------------------------------------------------------------------//
  private void initImage()
  { image = createImage(getSize().width, getSize().height);
    gdp   = image.getGraphics();
  }

//--------------------------------------------------------------------------//
/** Malt die Leinwand.
 *  Um ein ableitetes Objekt der Klasse JCanvas zu erhalten
 *  muss dieses Methode &uuml;berladen werden.
 *
 *  @param g grafischer Kontext, in den die Leinwand gemalt werden soll.    */
//--------------------------------------------------------------------------//
  public void paintCanvas(Graphics g){}

//--------------------------------------------------------------------------//
/** &Uuml;bermalt die aktuelle Leinwand. 
                                  */
//--------------------------------------------------------------------------//
  public void repaint()
  { if(image != null) paintCanvas(gdp); super.repaint(); }
}
