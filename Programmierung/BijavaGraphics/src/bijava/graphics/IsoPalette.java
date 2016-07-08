package bijava.graphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 */
public class IsoPalette {

    public enum Palette {

        GelbBraun(new Color[]{new Color(150, 0, 33), new Color(33, 0, 33)}, false),
        GruenBlauBraun(new Color[]{new Color(150, 0, 33), new Color(0, 255, 0), new Color(0, 255, 0), new Color(0, 0, 255),}, false),
        BlauGruen(new Color[]{new Color(0, 0, 255), new Color(0, 255, 0)}, false),
        GruenBlau(new Color[]{new Color(0, 255, 0), new Color(0, 0, 255)}, false),
        RotBlau(new Color[]{new Color(255, 0, 0), new Color(0, 0, 255),}, false),
        BlauRot(new Color[]{new Color(0, 0, 255), new Color(255, 0, 0),}, false),
        BraunBlauGruenWicht(new Color[]{new Color(0, 0, 255), new Color(0, 255, 0), new Color(150, 0, 33),}, true),
        GruenBlauBraunGelbWicht(new Color[]{new Color(255, 255, 0), new Color(150, 0, 33)},  new Color[]{new Color(0, 255, 0),new Color(0, 0, 255)}, true),
        GelbBraunBlauGruenWicht(new Color[]{new Color(0, 0, 255), new Color(0, 255, 0)}, new Color[]{new Color(150, 0, 33), new Color(255, 255, 0),}, true),

        GruenBlauBraunWicht(new Color[]{new Color(150, 0, 33), new Color(0, 255, 0), new Color(0, 0, 255),}, true),
        BlauRotDiff(new Color[]{new Color(150, 0, 33)}, new Color(255, 0, 0), new Color[]{new Color(0, 0, 255)}, false),
        BlauRotDiffWicht(new Color[]{new Color(0, 0, 255), new Color(200, 200, 255)}, new Color(255, 255, 255), new Color[]{new Color(255, 200, 200), new Color(255, 0, 0)}, true),
        Stufenpalette(new Color[]{Color.WHITE,Color.BLUE,Color.GREEN,Color.red,Color.cyan,Color.yellow,Color.MAGENTA,Color.BLACK},false,true),
        Black(new Color[]{Color.BLACK, Color.BLACK}, false);

        public Color[] colors;
        public Color[] colmin;
        public Color colnull;
        public Color[] colmax;
        public boolean gewichtet;
        public boolean differenzen;
        public boolean stufen=false;
        public boolean bereich=false;

        /**
         * Erzeugt gewichtet oder ungewichtete Farbpaletten
         */
        private Palette(Color[] colors, boolean gewichtet) {
            this.colors = colors;
            this.gewichtet = gewichtet;
            this.differenzen = false;
            colmin = new Color[]{colors[0]};
            colmax = new Color[]{colors[colors.length - 1]};
            colnull = null;
        }

        private Palette(Color[] colors, boolean gewichtet, boolean stufen) {
            if (stufen) {
                this.colors = colors;
                this.gewichtet = gewichtet;
                this.stufen = stufen;
                this.differenzen = false;
                colmin = new Color[]{colors[0]};
                colmax = new Color[]{colors[colors.length - 1]};
                colnull = null;
            } 
        }

        /**
         * Erzeugt gewichtet oder ungewichtete Farbpaletten für Differenzen
         */
        private Palette(Color[] colmin, Color colnull, Color[] colmax, boolean gewichtet) {

            this.gewichtet = gewichtet;
            this.differenzen = true;
            colors = new Color[colmin.length + 1 + colmax.length];
            this.colnull = colnull;
            this.colmax = colmax;
            this.colmin = colmin;
            int k = 0;
            for (int j = 0; j < colmin.length; j++) {
                colors[k] = colmin[j];
                k++;
            }

            colors[k] = colnull;
            k++;

            for (int j = 0; j < colmax.length; j++) {
                colors[k] = colmax[j];
                k++;
            }
        }

        private Palette(Color[] colmin, Color[] colmax, boolean gewichtet) {

            this.gewichtet = gewichtet;
            this.differenzen = true;
            this.bereich=true;
            colors = new Color[colmin.length + colmax.length];
            this.colmax = colmax;
            this.colmin = colmin;
            int k = 0;
            for (int j = 0; j < colmin.length; j++) {
                colors[k] = colmin[j];
                k++;
            }

            for (int j = 0; j < colmax.length; j++) {
                colors[k] = colmax[j];
                k++;
            }
        }

    }

    public double[] isoValues;
    public Color[] colors;
    private double minVal,  maxVal;
    private ArrayList<Color> collist = new ArrayList<Color>();
    public Palette pal;
    private int stufen = 8;
    private boolean stufenpalette = false;
    private String name = "Standard";

    public IsoPalette(double[] iso, Color[] col) {
        name = "Eigen";
        isoValues = iso;
        colors = col;
        minVal = iso[0];
        maxVal = iso[iso.length - 1];
    }

//    public Paletten(double minVal,double maxVal) {
//        this.minVal=minVal;
//        this.maxVal=maxVal;
//        name="Standard";
//        
//        colors = new Color[]{
//                    new Color(0,0,255),
//                    new Color(0,255,255),
//                    new Color(0,255,0),
//                    new Color(255,255,0),
//                    new Color(255,0,0),
//                    new Color(255,0,255)                  
//        };
//        isoValues = new double[colors.length];
//        double val=minVal;
//        double delta=(maxVal-minVal)/(colors.length-1.);
//        for(int i=0;i<colors.length;i++){
//            isoValues[i]=val;
//            val+=delta;
//        }      
//    }
    public IsoPalette(double minVal, double maxVal) {
        this.minVal = minVal;
        this.maxVal = maxVal;
        name = "Standard";

        isoValues = new double[10];
        double dVal = (maxVal - minVal) / (isoValues.length - 1);
        for (int i = 0; i < isoValues.length; i++) {
            isoValues[i] = minVal + i * dVal;
        }
        colors = new Color[]{
                    new Color(127, 0, 255),
                    new Color(0, 0, 255),
                    new Color(0, 127, 255),
                    new Color(0, 255, 255),
                    new Color(0, 255, 255),
                    new Color(0, 255, 0),
                    new Color(127, 255, 0),
                    new Color(255, 255, 0),
                    new Color(255, 127, 0),
                    new Color(255, 0, 0)
                };
    }

    /**
     * Konstruktor fuer Farbpaletten
     * unter der Verwendung von Standardfarbeverläufen
     * gewichtet und ungewichtet mit Differenzen oder ohne
     *
     * @param minVal    minimaler Wert
     * @param maxVal    maximaler Wert
     * @param pal       ein der Standardfarbverlauef
     *
     */
    public IsoPalette(double minVal, double maxVal, Palette pal) {
        this.pal = pal;
//        System.out.println("Gewicht "+pal.gewichtet+ " "+pal.toString());
        if (pal.differenzen) {
            name = pal.toString();
            this.minVal = minVal;
            this.maxVal = maxVal;
            if (minVal >= 0 || maxVal <= 0) {
                System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
            }

            colors = new Color[pal.colmin.length + 1 + pal.colmax.length];

            int k = 0;
            for (int j = 0; j < pal.colmin.length; j++) {
                colors[k] = pal.colmin[j];
                k++;
            }

            colors[k] = pal.colnull;
            k++;

            for (int j = 0; j < pal.colmax.length; j++) {
                colors[k] = pal.colmax[j];
                k++;
            }

            isoValues = new double[colors.length];

            double val = minVal;
            double delta = (0 - minVal) / pal.colmin.length;
            for (int i = 0; i < pal.colmin.length; i++) {
                isoValues[i] = val;
                val += delta;
            }
            val = 0;
            isoValues[pal.colmin.length] = val;
            delta = (maxVal - 0) / pal.colmax.length;
            val += delta;
            for (int i = pal.colmin.length + 1; i < (pal.colmin.length + 1 + pal.colmax.length); i++) {
                isoValues[i] = val;
                val += delta;
            }
        } else {
            this.minVal = minVal;
            this.maxVal = maxVal;
            this.name = pal.toString();
            stufenpalette = false;
            colors = pal.colors;


            isoValues = new double[colors.length];
            if (pal.gewichtet) {
                if (colors.length < 3) {
                }
                isoValues[0] = minVal;
                isoValues[1] = 0;
                isoValues[2] = maxVal;
            } else {
                double val = minVal;
                double delta = (maxVal - minVal) / (colors.length - 1.);
                for (int i = 0; i < colors.length; i++) {
                    isoValues[i] = val;
                    val += delta;
                }
            }
        }
    }

    // Konstruktor fuer Farbpaletten zur Darstellung von Differenzen
    public IsoPalette(double minVal, double maxVal, Color[] colmin, Color colnull, Color[] colmax) {
        name = "Eigen";

        if (minVal >= 0 || maxVal <= 0) {
            System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
        }

        colors = new Color[colmin.length + 1 + colmax.length];

        int k = 0;
        for (int j = 0; j < colmin.length; j++) {
            colors[k] = colmin[j];
            k++;
        }

        colors[k] = colnull;
        k++;

        for (int j = 0; j < colmax.length; j++) {
            colors[k] = colmax[j];
            k++;
        }

        isoValues = new double[colors.length];

        double val = minVal;
        double delta = (0 - minVal) / colmin.length;
        for (int i = 0; i < colmin.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
        val = 0;
        isoValues[colmin.length] = val;
        delta = (maxVal - 0) / colmax.length;
        val += delta;
        for (int i = colmin.length + 1; i < (colmin.length + 1 + colmax.length); i++) {
            isoValues[i] = val;
            val += delta;
        }
    }

    public static IsoPalette DiffWicht(double minVal, double maxVal, Palette pal) {


        if (minVal >= 0 || maxVal <= 0) {
            System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
        }

        double[] isoValues = new double[pal.colors.length];
        isoValues[0] = minVal;
        isoValues[1] = minVal * 1 / 10.;
        isoValues[2] = 0.;
        isoValues[3] = maxVal * 1 / 10.;
        isoValues[4] = maxVal;
        return new IsoPalette(isoValues, pal.colors);

    }

    public Palette getFarbverlauf()
    {
        return this.pal;
    }

    public void setFarbverlauf(Palette pal) {
        this.pal=pal;
//        System.out.println("Gewicht "+pal.gewichtet+ " stufenpalette "+pal.stufen+  " "+pal.toString());


        if (pal.stufen) {
            name = "StufenPalette";
            stufenpalette = true;
            collist.clear();
            for (int j = 0; j < pal.colors.length; j++) {
                collist.add(pal.colors[j]);
            }

            if (stufen > collist.size()) {
                stufen = collist.size();
            }
        }
        else if (!pal.differenzen) {
            colors = pal.colors;
            isoValues = new double[colors.length];
            stufenpalette = false;
            if (pal.gewichtet) {
                if (colors.length < 3) {
                }

                if(colors.length>3){


                }

                isoValues[0] = minVal;
                isoValues[1] = 0;
                isoValues[2] = maxVal;
//                System.out.println("Hier wert erzeugt");
            } else {
                double val = minVal;
                double delta = (maxVal - minVal) / (colors.length - 1.);
                for (int i = 0; i < colors.length; i++) {
                    isoValues[i] = val;
                    val += delta;
                }
            }
        } else if(pal.bereich){
           stufenpalette = false;
            if (minVal >= 0 || maxVal <= 0) {
                System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
            }

            colors = new Color[pal.colmin.length + pal.colmax.length];

            int k = 0;
            for (int j = 0; j < pal.colmin.length; j++) {
                colors[k] = pal.colmin[j];
                k++;
            }

            for (int j = 0; j < pal.colmax.length; j++) {
                colors[k] = pal.colmax[j];
                k++;
            }
           
//           System.out.println("Anazl farbwerte"+colors.length);
           isoValues = new double[colors.length];

            double val = minVal;
            double delta = (0 - minVal) / (pal.colmin.length-1);
            for (int i = 0; i < pal.colmin.length-1; i++) {
                isoValues[i] = val;
                val += delta;
            }
            val = 0;
            isoValues[pal.colmin.length-1] = val;
            isoValues[pal.colmin.length] = val;

            delta = (maxVal - 0) / (pal.colmax.length-1);
            val += delta;
            for (int i = pal.colmin.length +1 ; i < (pal.colmin.length + 1 + pal.colmax.length-1); i++) {
                isoValues[i] = val;
//                System.out.println("zweite schleife "+i+ " "+val+ " "+delta);
                val += delta;
            }

//            System.out.println("Typ bereich");
            
           
        }
        else {
            name = pal.toString();
            stufenpalette = false;
//            this.minVal = minVal;
//            this.maxVal = maxVal;
            if (minVal >= 0 || maxVal <= 0) {
                System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
            }

            colors = new Color[pal.colmin.length + 1 + pal.colmax.length];

            int k = 0;
            for (int j = 0; j < pal.colmin.length; j++) {
                colors[k] = pal.colmin[j];
                k++;
            }

            colors[k] = pal.colnull;
            k++;

            for (int j = 0; j < pal.colmax.length; j++) {
                colors[k] = pal.colmax[j];
                k++;
            }

            isoValues = new double[colors.length];

            double val = minVal;
            double delta = (0 - minVal) / pal.colmin.length;
            for (int i = 0; i < pal.colmin.length; i++) {
                isoValues[i] = val;
                val += delta;
            }
            val = 0;
            isoValues[pal.colmin.length] = val;
            delta = (maxVal - 0) / pal.colmax.length;
            val += delta;
            for (int i = pal.colmin.length + 1; i < (pal.colmin.length + 1 + pal.colmax.length); i++) {
                isoValues[i] = val;
                val += delta;
            }
        }
    }

    public Color getColor(double iso) { // TODO: hier sollte beizeiten nochmal eine Abfrage auf Double.NaN rein...
        int i = 0;

        if (stufenpalette) {

            double dx = (maxVal - minVal) / ((double) stufen);
            int ind = ((int) ((iso - minVal) / dx + 0.5));
            if (ind >= collist.size()) {
                ind = collist.size() - 1;
            }
            return collist.get(ind);
        }

        //bestimme das Intervall, welches dem Hoehenwert zugeordnet werden kann
        for (; i < isoValues.length; i++) {
            if (iso < isoValues[i]) {
                break;
            }
        }

        if (i == 0) {
            return colors[0];
        } else if (i == isoValues.length) {
            return colors[i - 1];
        } else {
            //interpoliere zwischen i-1 bis i
            int r0 = colors[i - 1].getRed();
            int g0 = colors[i - 1].getGreen();
            int b0 = colors[i - 1].getBlue();

            int r1 = colors[i].getRed();
            int g1 = colors[i].getGreen();
            int b1 = colors[i].getBlue();

            double lambda = (iso - isoValues[i - 1]) / (isoValues[i] - isoValues[i - 1]);
            int r = r0 + (int) (lambda * (double) (r1 - r0));
            int g = g0 + (int) (lambda * (double) (g1 - g0));
            int b = b0 + (int) (lambda * (double) (b1 - b0));
//            System.out.println(" r "+r+ " ,g "+g+ " ,b "+b+ " ,lambda "+lambda);

            return new Color(r, g, b);
        }
    }

    /**
     * Erzeugt eine IsoPalette mit einem Farbverlauf von Blau (bei minimalem
     * Wert) nach Rot (bei maximalem Wert) mit <code>numVals</code> Stufen
     * 
     * @param minVal    minimaler Wert
     * @param maxVal    maximaler Wert
     * @param numVals   Anzahl der Werte bzw. Farben
     * @return  eine neue IsoPalette
     * @deprecated
     */
    public static IsoPalette BlueWhiteRed(double minVal, double maxVal, int numVals) {

        double[] isoValues = new double[numVals];
        Color[] colors = new Color[numVals];

        // Schrittweite ausrechnen
        double dv = (maxVal - minVal) / numVals;

        // Isowerte definieren
        for (int i = 0; i < numVals; i++) {
            isoValues[i] = minVal + i * dv;
        }

        int numValBlue = numVals / 2;
        int numValRed = numVals - numValBlue;

        // Blaue Farben
        for (int i = 0; i < numValBlue; i++) {
            colors[i] = new Color(((float) (i) / (float) (numValBlue)), ((float) (i) / (float) (numValBlue)), (float) 1);
        }

        // Rote Farben
        for (int i = 0; i < numValRed; i++) {
            colors[i + numValBlue] = new Color((float) 1, (float) (numValRed - i) / (float) numValRed, (float) (numValRed - i) / (float) numValRed);
        }

        return new IsoPalette(isoValues, colors);
    }

    public void StufenPalette() // Farbskala von Blau nach Gruen
    {
        name = "StufenPalette";
        stufenpalette = true;
        collist.clear();
        collist.add(Color.WHITE);
        collist.add(Color.BLUE);
        collist.add(Color.GREEN);
        collist.add(Color.RED);
        collist.add(Color.CYAN);
        collist.add(Color.YELLOW);
        collist.add(Color.MAGENTA);
        collist.add(Color.BLACK);

        if (stufen > collist.size()) {
            stufen = collist.size();
        }

        this.pal=Palette.Stufenpalette;
    }

    /**
    * @deprecated Bitte die Farbverlaeufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GelbBraun() {
        name = "GelbBraun";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(150, 0, 33),
                    new Color(33, 0, 33)
                };
        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
         this.pal=Palette.GelbBraun;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GruenBlauBraun() // Farbskala von Gruen nach Blau
    {
        name = "GruenBlauBraun";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(150, 0, 33),
                    new Color(0, 255, 0),
                    new Color(0, 255, 0),
                    new Color(0, 0, 255),};

        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
         this.pal=Palette.GruenBlauBraun;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GruenBlauBraunWicht() {
        name = "GruenBlauBraunWicht";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(150, 0, 33),
                    new Color(0, 255, 0),
                    new Color(0, 0, 255),};

        isoValues = new double[colors.length];
        isoValues[0] = minVal;
        isoValues[1] = 0;
        isoValues[2] = maxVal;
        this.pal=Palette.GruenBlauBraunWicht;
//
//        double val=minVal;
//        double delta=(maxVal-minVal)/(colors.length-1.);
//        for(int i=0;i<colors.length;i++){
//            isoValues[i]=val;
//            val+=delta;
//        }
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void BraunBlauGruenWicht() {
        name = "BraunBlauGruenWicht";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(0, 0, 255),
                    new Color(0, 255, 0),
                    new Color(150, 0, 33),};

        isoValues = new double[colors.length];
        isoValues[0] = minVal;
        isoValues[1] = 0;
        isoValues[2] = maxVal;
         this.pal=Palette.BraunBlauGruenWicht;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GruenBlauBraunGelbWicht() {
        name = "GruenBlauBraunWicht";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(255, 255, 0),
                    new Color(150, 0, 33),
                    new Color(0, 255, 0),
                    new Color(0, 0, 255),};

        isoValues = new double[colors.length];
        isoValues[0] = minVal;
        isoValues[1] = 0;
        isoValues[2]=0;
        isoValues[3] = maxVal;
         this.pal=Palette.GruenBlauBraunGelbWicht;
    }

     /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GelbBraunBlauGruenWicht() {
        name = "GelbBraunBlauGruenWicht";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(0, 0, 255),
                    new Color(0, 255, 0),
                    new Color(150, 0, 33),
                    new Color(255, 255, 0),};

        isoValues = new double[colors.length];
        isoValues[0] = minVal;
        isoValues[1] = 0;
        isoValues[3] = maxVal;
         this.pal=Palette.GelbBraunBlauGruenWicht;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void BlauGruen() {
        name = "BlauGruen";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(0, 0, 255),
                    new Color(0, 255, 0)
                };
        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
        this.pal=Palette.BlauGruen;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void GruenBlau() // Farbskala von Gruen nach Blau
    {
        name = "GruenBlau";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(0, 255, 0),
                    new Color(0, 0, 255),};
        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
         this.pal=Palette.GruenBlau;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void RotBlau() // Farbskala von Rot nach Blau
    {
        name = "RotBlau";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(255, 0, 0),
                    new Color(0, 0, 255),};
        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
         this.pal=Palette.RotBlau;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void BlauRot() // Farbskala von Blau nach Rot
    {
        name = "BlauRot";
        stufenpalette = false;
        colors = new Color[]{
                    new Color(0, 0, 255),
                    new Color(255, 0, 0),};
        isoValues = new double[colors.length];
        double val = minVal;
        double delta = (maxVal - minVal) / (colors.length - 1.);
        for (int i = 0; i < colors.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
         this.pal=Palette.BlauRot;
    }

    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void BlauRotDiff() {
        name = "BlauRotDiff";
        stufenpalette = false;
        if (minVal >= 0 || maxVal <= 0) {
            System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
        }

//        Color[] colmin = new Color[]{  new Color(0,0,255)};
//        Color colnull = new Color(255,255,255);
//        Color[] colmax = new Color[]{new Color(255,0,0)};

        Color[] colmin = new Color[]{new Color(150, 0, 33)};
        Color colnull = new Color(255, 0, 0);
        Color[] colmax = new Color[]{new Color(0, 0, 255)};

        colors = new Color[colmin.length + 1 + colmax.length];

        int k = 0;
        for (int j = 0; j < colmin.length; j++) {
            colors[k] = colmin[j];
            k++;
        }

        colors[k] = colnull;
        k++;

        for (int j = 0; j < colmax.length; j++) {
            colors[k] = colmax[j];
            k++;
        }

        isoValues = new double[colors.length];

        double val = minVal;
        double delta = (0 - minVal) / colmin.length;
        for (int i = 0; i < colmin.length; i++) {
            isoValues[i] = val;
            val += delta;
        }
        val = 0;
        isoValues[colmin.length] = val;
        delta = (maxVal - 0) / colmax.length;
        val += delta;
        for (int i = colmin.length + 1; i < (colmin.length + 1 + colmax.length); i++) {
            isoValues[i] = val;
            val += delta;
        }

         this.pal=Palette.BlauRotDiff;
    }


    /**
    * @deprecated Bitte die Farbverläufe aus der Enumeration Palette benutzen
    */
    @Deprecated
    public void BlauRotDiffWicht() {
        name = "BlauRotDiffWicht";
        stufenpalette = false;
        if (minVal >= 0 || maxVal <= 0) {
            System.out.println("minVal nicht kleiner Null oder maxVal nicht groeszer Null");
        }

        Color[] colmin = new Color[]{new Color(0, 0, 255),
            new Color(200, 200, 255)
        };
        Color colnull = new Color(255, 255, 255);

        Color[] colmax = new Color[]{
            new Color(255, 200, 200),
            new Color(255, 0, 0)};

        colors = new Color[colmin.length + 1 + colmax.length];

        int k = 0;
        for (int j = 0; j < colmin.length; j++) {
            colors[k] = colmin[j];
            k++;
        }

        colors[k] = colnull;
        k++;

        for (int j = 0; j < colmax.length; j++) {
            colors[k] = colmax[j];
            k++;
        }

        isoValues = new double[colors.length];
        isoValues[0] = minVal;
        isoValues[1] = minVal * 1 / 10.;
        isoValues[2] = 0.;
        isoValues[3] = maxVal * 1 / 10.;
        isoValues[4] = maxVal;

//         this.pal=Palette.BlauRotDiffWicht;
    }

    public JComponent getColorbar() {
        return new JComponent() {

            public void paintComponent(Graphics g) {
                Color c = g.getColor();
                double width = this.getSize().getWidth();
                double height = this.getSize().getHeight();
                for (double v = minVal; v < maxVal; v += (maxVal - minVal) / height) {
                    g.setColor(getColor(v));
                    double y = (v - minVal) / (maxVal - minVal) * height;
                    g.drawLine((int) 0, (int) y, (int) width, (int) y);

                }
                Font myFont = new Font("Arial", Font.ITALIC | Font.PLAIN, 14);
                g.setFont(myFont);
                g.setColor(Color.WHITE);
                String s1 = "" + Math.round(minVal * 100.) / 100.;
                String s2 = "" + Math.round(maxVal * 100.) / 100.;
                Rectangle2D w1 = myFont.getStringBounds(s1, ((Graphics2D) g).getFontRenderContext());
                Rectangle2D w2 = myFont.getStringBounds(s2, ((Graphics2D) g).getFontRenderContext());
                if (w1.getWidth() < w2.getWidth()) {
                    this.setMinimumSize(new Dimension((int) w2.getWidth(), 0));
                } else {
                    this.setMinimumSize(new Dimension((int) w1.getWidth(), 0));
                }
                g.drawString(s1, 5, 15);
                g.drawString(s2, 5, (int) (height - 5));
                g.setColor(Color.BLACK);
                double y = (0 - minVal) / (maxVal - minVal) * height;
//                                g.drawString("0" , 5, (int) y);


            }
        };
    }

    public double getMinVal() {
        return minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMinVal(double minVal) {
        if (this.pal == null) {
            this.minVal = minVal;
            double val = minVal;
            double delta = (maxVal - minVal) / (colors.length - 1.);
            for (int i = 0; i < colors.length; i++) {
                isoValues[i] = val;
                val += delta;
            }
        } else {
            this.minVal = minVal;
            setFarbverlauf(this.pal);


//            this.minVal = minVal;
//            if (!this.pal.gewichtet) {
//                double val = minVal;
//                double delta = (maxVal - minVal) / (colors.length - 1.);
//                for (int i = 0; i < colors.length; i++) {
//                    isoValues[i] = val;
//                    val += delta;
//                }
//            } else {
//                isoValues[0] = minVal;
//                isoValues[1] = 0;
//                isoValues[2] = maxVal;
//
//            }
        }
    }

    public void setMaxVal(double maxVal) {
        if (pal == null) {
            this.maxVal = maxVal;
            double val = minVal;
            double delta = (maxVal - minVal) / (colors.length - 1.);
            for (int i = 0; i < colors.length; i++) {
                isoValues[i] = val;
                val += delta;
            }
        } else {
            this.maxVal = maxVal;
            setFarbverlauf(this.pal);

//            this.maxVal = maxVal;
//
//            if (!this.pal.gewichtet) {
//                double val = minVal;
//                double delta = (maxVal - minVal) / (colors.length - 1.);
//                for (int i = 0; i < colors.length; i++) {
//                    isoValues[i] = val;
//                    val += delta;
//                }
//            } else {
//                isoValues[0] = minVal;
//                isoValues[1] = 0;
//                isoValues[2] = maxVal;
//            }
        }
    }

    public String getName() {
        return name;
    }

    public String toString() {
        String s = "isoValue\t| Color\n===========================\n";

        for (int i = 0; i < isoValues.length; i++) {
            s += isoValues[i] + "\t| " + colors[i] + "\n";
        }
        return s;
    }

    private static final JFrame f = new javax.swing.JFrame();
    private static final IsoPalette palette  = new IsoPalette(-5, 25, Palette.RotBlau);

    public static void main(String[] args) {
     if(true){
        System.out.println(palette.getColor(-25));
        System.out.println(palette.getColor(0));
        System.out.println(palette.getColor(1));
        System.out.println(palette.getColor(25));
        palette.setFarbverlauf(Palette.RotBlau);
        palette.setFarbverlauf(Palette.BlauRotDiff);
      
        f.setSize(600, 500);
        f.add(palette.getColorbar(),BorderLayout.CENTER);

        JComboBox box = new JComboBox(Palette.values());
        box.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                 Palette p =(Palette) cb.getSelectedItem();
                 palette.setFarbverlauf(p);
//                 System.out.println(palette.pal.gewichtet);
                 f.repaint();
            }
        });

        f.add(box, BorderLayout.EAST);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
     }
     
    }
}
