package bijava.graphics;

import java.io.*;
//import bauinf.io.*;

/**A class for writing Interleaf ascii files with graphics primitves.
 * The origin of the coordiate system ist northwest. Coordinates are
 * specified in inches relative to the origin. Primitives with higher
 * z-coordinate hide those with lower z-coordinate.
 *
 * @author  Dipl.-Ing. Frank Sellerhof
 * @version .
 */
public class InterleafOutput 
{	
	private PrintWriter out=null;
	private int lc = 7;
	private int tc = 7;
	private int fc = 7;
	private double lw = 0.25;
	private CoordTransform trans=null;
	private double[][] colorpalette = null;
	private int fSkalaTyp;				// gibt den Typ der Farbskala an (z.B. 0 = Schwarz - Wei� - Skala)
	private int fSkalaElemente;			// gibt die Anz. der Unterteilungen bzw. der Farbelemente der Skala an

/**
	Constructor for the output into a single document.
	All primitives are displayed in the same frame.
	(todo: creation of named frames)
	The Interleaffile written is named "file".ildoc
	Before graphics prmitives can be written the 
	document has to be opened. After the last primitive is written
	the document has to be closed.
	@param file name of the (new) Interleaf document
	@see #OpenDocument()
	@see #CloseDocument()
*/
	public InterleafOutput( String file, double xmin,double ymin,double xmax,double ymax, int fSkalaTyp, int fSkalaElemente ) throws FileNotFoundException 
	{
			out = new PrintWriter( new FileOutputStream(file+".ildoc"));
			trans = new CoordTransform(xmin,ymin,xmax,ymax,6.,4.,0.);
			this.fSkalaTyp = fSkalaTyp;		// editiert von: Jan Onne Backhaus
			this.fSkalaElemente = fSkalaElemente+20;	// edititert von: Jan Onne Backhaus
	}

/**
	Set the actual colorindex for lines.
	@param c colorindex for linecolor. Default: 7 (black).
	@see #getLineColor()
*/
        public void setLineColor( int c ) {
		lc = c;
	}
/**
	Get the actual colorindex for lines.
	@see #setLineColor(int)
*/
	public int getLineColor() {
		return lc;
	}
/**
	Set the actual colorindex for text output.
	@param c colorindex for linecolor. Default: 7 (black).
	@see #getTextColor()
*/
        public void setTextColor( int c ) {
		tc = c;
	}
/**
	Get the actual colorindex for text output.
	@see #setTextColor(int)
*/
	public int getTextColor() {
		return tc;
	}

/**
	Set the actual colorindex for filling.
	@param c colorindex for fillcolor. Default: 7 (black).
	@see #getFillColor()
*/
	public void setFillColor( int c ) {
		fc = c;
	}
/**
	Get the actual colorindex for filling.
	@see #setFillColor(int)
*/
	public int getFillColor() {
		return fc;
	}
/**
	Set the actual line width.
	@param w linewidth. Default: 0.25
	@see #getLineWidth()
*/
	public void setLineWidth( double w ) {
		lw = w;
	}
/**
	Get the actual line width.
	@see #setLineWidth(double)
*/
	public double getLineWidth() {
		return lw;
	}
/**
	Open the document. Prepare for writing graphics primitives.
	A document may only be opened once.
	@see #CloseDocument()
*/
	public void OpenDocument() {
		out.println("<!OPS, Version = 8.4>");
		out.println("");
		out.println("<!Page Number Stream,");
		out.println(" 	Name =			\"Seite\",");
		out.println("	Starting Page # = 	Inherit>");
		out.println("");
		out.println("<!Document,");
		out.println("	Header Page =		no,");
		out.println("	Final Output Device =	\"ileaf\",");
		out.println("	Default Printer =	\"nearest-ileaf\",");
		out.println("	Measurement Unit =	MM,");
		out.println("	Default Page Stream Name = \"Seite\",");
		out.println("	Lisp = \"(if (not (fboundp 'ileaf::r))<#0a>");
		out.println("                   (defun ileaf::r (data)<#0a>");
		out.println("                     (tell *load-object* mid:put-data 'ileaf::saved-data<#0a>");
		out.println("                           data)))(ileaf::r '(:pup-cmpn-names (\"\"Absatz\"\") ");
		out.println(":il-hleaf-uid-list nil)<#0a>");
		out.println(")\">");
		out.println("");
		out.println("<!Font Definitions,");
		out.println("	F6 = Times 10,");
		out.println("	F2 = Times 12>");
		out.println("");
		out.println("<!Color Definitions,");
/*		for( int i=0;i<100;i++)
		out.println("	C"+i+" = 0, 0, 0, "+i+",");
*/
/*                int anz_farben = 11;
                for (int i=0; i<anz_farben; i++) {
                    int wert = (int) (100.*(i/(anz_farben-1.)));
                    out.println("	C"+(i+13)+" = 0, 0, 0, "+wert+",");
                }
*/		out.println("	C1 = 0, 100, 100, 0,");
		out.println("	C2 = 50, 100, 0, 0,");
		out.println("	C3 = 100, 0, 0, 0,");
		out.println("	C6 = 0, 0, 100, 0,");
		out.println("	C8 = 0, 0.005, 100, 0,");
		out.println("	C9 = 0, 50, 100, 0,");
		out.println("	C10 = 0, 28.055, 40, 49.445,");
		out.println("	C0 = 0, 0, 0, 0,");
		out.println("	C11 = 0, 0, 0, 12.5,");
		out.println("	C4 = 0, 0, 0, 25,");
		out.println("	C5 = 0, 0, 0, 50,");
		out.println("	C12 = 0, 0, 0, 75,");
		out.println("	C7 = 0, 0, 0, 100,");

		/*
		Im Folgenden werden die in dem Dokument verf�gbaren Farben definiert. Die Farbverl�ufe werden der Klasse Farbskala
		entnommen. Zwei Parameter (die dem Konstruktor dieser KLasse �bergeben werden m�ssen) geben die Art des Farbverlaufes
		bzw. der Farbskala an.
		fSkalaTyp	- dieser Wert bestimmt die Art der Farbskala (Rot -> Blau oder Gruen -> Gelb)
		fSkalaElemente	- dieser Wert bestimmt die Schritte, in denen die Farbskala durchlaufen werden soll (also die Anzahl
				  der zu definierenden Farben).
				  Es ist zu beachten, da� einige Farbskalen diesen Wert f�r sich leicht nach oben ab�ndern
				  (n�heres ist der entsprechenden Farbskala in der Klasse: Farbskala zu entnehemen )
		*/
		
		/*
		SW - Skala einf�gen
		*/
		// fSkalaTyp = 0 ==> Schwarz - Wei� - Skala
		if (0 == 0)	
		{
			double[] werte;
			werte = new double[20];
			
			Farbskala farbskala;
			farbskala = new Farbskala(20);
			
			werte = farbskala.feldSWskala();
						
			for(int i = 0; i<werte.length; i++)		// Schleife �ber Feld mit Farbwerten, die die Werte ins Dokument Schreibt 
			{
				out.println("	C"+(i+14)+" = 0, 0, 0,"+ werte[i] +" ,");
			}
			
			
			//feldSWskala
		}
		
		/*
		Blau - Gruen - Farbskala einf�gen
		*/
		
		// fSkalaTyp = 1 ==> Blau - Gruen - Skala
		if (fSkalaTyp != 0)
		{
			double[] c; double[] m; double[] y;
			c = new double[fSkalaElemente-20];
			m = new double[fSkalaElemente-20];
			y = new double[fSkalaElemente];
			
			Farbskala farbskala;
			farbskala = new Farbskala(fSkalaElemente-20);
			
			
			if (fSkalaTyp == 1) {farbskala.RotBlau();};
			if (fSkalaTyp == 2) {farbskala.GruenRot();};		// Gruen - Rot - Skala
			if (fSkalaTyp == 3) {farbskala.GruenBlau();};		// Gruen - Blau - Skala
			if (fSkalaTyp == 4) {farbskala.BlauGruen();};		// Blau - Gruen - Skala
			if (fSkalaTyp == 5) {farbskala.RotGelb();};		// Rot - Gelb - Skala
			if (fSkalaTyp == 6) {farbskala.GruenGelb();};		// Gruen - Gelb - Skala
			if (fSkalaTyp == 7) {farbskala.BlauGelb();};		// Blau - Gelb - Skala
			if (fSkalaTyp == 8) {farbskala.BlauWeissRot();};		// Blau - Wei� - Rot - Skala
			if (fSkalaTyp == 9) {farbskala.BlauWeissGruen();};	// Blau - Wei� - Gruen - Skala
			if (fSkalaTyp == 10) {farbskala.BlauGruenGelbRot();};	// Blau - Gruen - Gelb - Rot - Skala

			
			c = farbskala.getCyan();
			m = farbskala.getMagenta();
			y = farbskala.getYellow();
			
			for(int i = 0; i<c.length; i++)		// Schleife �ber Feld mit Farbwerten, die die Werte ins Dokument Schreibt 
			{
				out.println("	C"+(i+35)+" = "+c[i] +", "+m[i] +", "+y[i] +",0,");
			}
		
		}
		
		
		
		
		out.println("	C13 = 0, 0, 0, 0>");			// Der letzte Wert ist immer wei� und hei�t: "c13" (ist frei gew�hlt)
		
//		out.println("	C100 = 0, 0, 0, 100>");


		out.println("");
		out.println("<!Pattern Definitions,");
		out.println("	P3 = 0c0c0c0c0c0c0c0cfcfcfcfc000000000c0c0c0c0c0c0c0cfcfcfcfc00000000,");
		out.println("	P4 = 08080c0c0c0c1c1cfcfc78780000000008080c0c0c0c1c1cfcfc787800000000,");
		out.println("	P0 = 40014001e003180c04100220022001400140014003e00c181004200220024001,");
		out.println("	P1 = e0e0e0e0e0e0e0e0b1b1b1b11b1b1b1b0e0e0e0e0e0e0e0e1b1b1b1bb1b1b1b1,");
		out.println("	P2 = aaaa0000aaaa0000aaaa0000aaaa0000aaaa0000aaaa0000aaaa0000aaaa0000,");
		out.println("	P6 = aaaa5555aaaa5555aaaa5555aaaa5555aaaa5555aaaa5555aaaa5555aaaa5555,");
		out.println("	P7 = 5555ffff5555ffff5555ffff5555ffff5555ffff5555ffff5555ffff5555ffff,");
		out.println("	P5 = ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff,");
		out.println("	P8 = ffffffffffffffff0000000000000000ffffffffffffffff0000000000000000,");
		out.println("	P9 = f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0f0,");
		out.println("	P10 = f0f078783c3c1e1e0f0f8787c3c3e1e1f0f078783c3c1e1e0f0f8787c3c3e1e1,");
		out.println("	P11 = 0f0f1e1e3c3c7878f0f0e1e1c3c387870f0f1e1e3c3c7878f0f0e1e1c3c38787,");
		out.println("	P12 = c0c06060303018180c0c060603038181c0c06060303018180c0c060603038181,");
		out.println("	P13 = 8181030306060c0c181830306060c0c08181030306060c0c181830306060c0c0,");
		out.println("	P14 = f0f0f0f0f0f0f0f00f0f0f0f0f0f0f0ff0f0f0f0f0f0f0f00f0f0f0f0f0f0f0f,");
		out.println("	P15 = fdfd0000dfdfdfdfdfdf0000fdfdfdfdfdfd0000dfdfdfdfdfdf0000fdfdfdfd>");
		out.println("");
		out.println("<!Page,");
		out.println("	Height =		11.6929134 Inches,");
		out.println("	Width =			8.2677165 Inches,");
		out.println("	Top Margin =		1.1417326 Inches,");
		out.println("	Bottom Margin =		1.4960628 Inches,");
		out.println("	Left Margin =		1.2598421 Inches,");
		out.println("	Right Margin =		0.9842521 Inches,");
		out.println("	Hyphenation =		on,");
		out.println("	Consecutive Hyphens =	3,");
		out.println("	Revision Bar Placement = Left,");
		out.println("	Feathering =		off>");
		out.println("");
		out.println("<!Autonumber Stream, \"Liste\", 1>");
		out.println("");
		out.println("<!Class, \"Absatz\",");
		out.println("	Top Margin =		0.0393701 Inches,");
		out.println("	Bottom Margin =		0.0393701 Inches,");
		out.println("	Font =			F2@Z7@Lge,");
		out.println("	Alt Font =		F2@Z7@Lge,");
		out.println("	Line Spacing =		1.1667 lines,");
		out.println("	Left Tab =		0/0.9842521/1.9685042/2.9527555 Inches,");
		out.println("	Composition =		Optimum>");
		out.println("");
		out.println("<!Master Frame,");
		out.println("	Name =			\"Ganzseitig\",");
		out.println("	Placement =		Overlay,");
		out.println("	Horizontal Alignment =	Center,");
		out.println("	Vertical Alignment =	Center,");
		out.println("	Horizontal Reference =	Page With Both Margins,");
		out.println("	Vertical Reference =	Page With Both Margins,");
		out.println("	Width =			2 Inches,");
		out.println("	Width =			Page With Both Margins,");
		out.println("	Height =		1 Inches,");
		out.println("	Height =		Page With Both Margins,");
		out.println("	Diagram =");
		out.println("V11,");
		out.println("(g9,0,0,");
		out.println(" (E16,0,0,,5,1,1,0.0533333,1,15,0,0,1,0,0,0,1,5,127,7,0,0,7,0,1,1,0.0666667,0.06");
		out.println("  66667,6,6,0,0.0666667,6))>");
		out.println("");
		out.println("<Page Header, Frame =");
		out.println("V11,");
		out.println("(g9,1,0,");
		out.println(" (t14,1,4,,6.0077165,0.6066667,2,7,0,0,,wst:timsps10,)");
		out.println(" (t14,2,4,,2.9971916,0.6066667,1,7,0,0,,wst:timsps10,)");
		out.println(" (t14,3,4,,0,0.6066667,0,7,0,0,,wst:timsps10,)");
		out.println(" (E16,0,0,,5,1,1,0.0533333,1,15,0,0,1,0,0,0,1,5,127,7,0,0,7,0,1,1,0.0666667,0.06");
		out.println("  66667,6,6,0,0.0666667,6))>");
		out.println("");
		out.println("<Page Footer, Frame =");
		out.println("V11,");
		out.println("(g9,1,0,");
		out.println(" (t14,1,4,,6.0077165,0.7866667,2,7,0,0,,wst:timsps10,)");
		out.println(" (t14,2,4,,3.0038582,0.7866667,1,7,0,0,,wst:timsps10,\\X80a0)");
		out.println(" (t14,3,4,,0,0.7866667,0,7,0,0,,wst:timsps10,)");
		out.println(" (E16,0,0,,5,1,1,0.0533333,1,15,0,0,1,0,0,0,1,5,127,7,0,0,7,0,1,1,0.0666667,0.06");
		out.println("  66667,6,6,0,0.0666667,6))>");
		out.println("");
		out.println("<!End Declarations>");
		out.println("");
		out.println("<\"Absatz\">");
		out.println("");
		out.println("<|,\"1\">");
		out.println("<Frame,");
		out.println("	Name =			\"Ganzseitig\",");
		out.println("	Placement =		Overlay,");
		out.println("	Horizontal Alignment =	Center,");
		out.println("	Vertical Alignment =	Center,");
		out.println("	Horizontal Reference =	Page With Both Margins,");
		out.println("	Vertical Reference =	Page With Both Margins,");
		out.println("	Width =			8.2677165 Inches,");
		out.println("	Width =			Page With Both Margins,");
		out.println("	Height =		11.6929134 Inches,");
		out.println("	Height =		Page With Both Margins,");
		out.println("	Page # =		\"1\",");
		out.println("	Diagram =");
		out.println("V11,");
		out.println("(g9,1,0,");
	}
/**
	Close the docuent. A closed document may not be opened for a second time.
	@see #OpenDocument()
*/
	public void CloseDocument() {
		out.println(" (E16,0,0,,5,1,1,0.0533333,1,15,0,0,1,0,0,0,1,5,127,7,0,0,7,1,1,1,0.0666667,0.06");
		out.println("  66667,6,6,0,0.0666667,6))>");
		out.close();
		System.out.println("Ende InterleafOutput");
	}
/**
	Open a new level in the hierarchy of groups. A group may contain several other groups.
	@see #CloseGroup()
*/
	public void OpenGroup() {
		out.println(" (g9,1,0,");
	}

/**
	Close the actual level of group hierarchy.
	@see #OpenGroup()
*/
	public void CloseGroup() {
		out.println(")");
	}
/**
	Write a single line with specified coordinates.
	@param xa x-coordinate of startpoint
	@param ya y-coordinate of startpoint
	@param xe x-coordinate of endpoint
	@param ye y-coordinate of endpoint
	@param z z-coordinate of line
*/
	public void WriteLine(double xa,double ya,double xe, double ye, int z) {
                double _xa = trans.worldToWindowx(xa);
                double _ya = trans.worldToWindowy(ya);
                double _xe = trans.worldToWindowx(xe);
                double _ye = trans.worldToWindowy(ye);
		java.text.DecimalFormat form = new java.text.DecimalFormat("#####.000");
		
		try
		{
			out.println("(v7,"+z+",0,,"+form.parse(form.format(_xa))+","+form.parse(form.format(_ya))+","+form.parse(form.format(_xe))+","+form.parse(form.format(_ye))+","+lc+",0,"+lw+",0)");

		}
		catch (Exception ex )
		{
		}

	}
/**
	Write an open Polyline with specified coordinates.	
	@param x[] x-coordinates of polyline
	@param y[] y-coordinates of polyline
	@param z z-coordinate of polyline
	@see #WritePolygon(double[] ,double[],int)
	@see #FillPolygon(double[] ,double[],int )
*/
	public void WritePolyLine(double[] x,double y[], int z) {
		OpenGroup();
		for( int i=0;i<x.length-1;i++)
			WriteLine(x[i],y[i],x[i+1],y[i+1],z);
		CloseGroup();
	}
/**
	Write a closed Polygon with specified coordinates.		
	@param x[] x-coordinates of polygon
	@param y[] y-coordinates of polygon
	@param z z-coordinate of polygon
	@see #FillPolygon(double[] ,double[],int )
*/	
	public void WritePolygon(double[] x,double y[],int z) {
		OpenGroup();
		for( int i=0;i<x.length;i++)
			WriteLine(x[i],y[i],x[(i+1)%x.length],y[(i+1)%x.length],z);
		CloseGroup();
	}

/**
	Write a filled, closed Polygon		
	color management has to be extracted in a later version
	@param x[] x-coordinates of polygon
	@param y[] y-coordinates of polygon
	@param z z-coordinate of polygon
	@see #WritePolygon(double[] ,double[],int )
*/	
	public void FillPolygon(double[] x,double y[], int z) {

			

		out.println("(p8,"+z+",8,,5,"+fc+",0");
		OpenGroup();
		OpenGroup();


		for( int i=0;i<x.length;i++)
//			WriteLine(x[i],y[i],x[(i+1)%x.length],y[(i+1)%x.length],z);
			WriteLine(	trans.worldToWindowx(x[i]),
						trans.worldToWindowy(y[i]),
						trans.worldToWindowx(x[(i+1)%x.length]),
						trans.worldToWindowy(y[(i+1)%x.length]),
						z);

		CloseGroup();
		CloseGroup();
		out.println(")");

	}
/**
	Write an ellipse with specified center and width, height
	@param x x-coordinate of center
	@param y y-coordinate of center
	@param w width of bounding box of ellipse
	@param h height of bounding box of ellipse
	@param z z-coordinate of ellipse
	@see #FillEllipse(double,double,double,double,int)
*/	
	public void WriteEllipse(double x,double y,double w, double h, int z) {
                double _x = trans.worldToWindowx(x);
                double _y = trans.worldToWindowy(y);
                double _w = trans.scaleLength(w);
                double _h = trans.scaleLength(h);
		out.println(" (e9,"+z+",0,,"+(_x-_w)+","+(_y-_h)+","+(_x-_w)+","+(_y+_h)+","+(_x+_w)+","+(_y-_h)+",5,127,5,"+lc+",0,"+lw+",0)");
	}

/**
	Fill an ellipse with specified center and width, height
	@param x x-coordinate of center
	@param y y-coordinate of center
	@param w width of bounding box of ellipse
	@param h height of bounding box of ellipse
	@param z z-coordinate of ellipse
	@see #WriteEllipse(double,double,double,double,int)
*/	
	public void FillEllipse(double x,double y, double w, double h, int z) {
		out.println(" (e9,"+z+",0,,"+(x-w)+","+(y-h)+","+(x-w)+","+(y+h)+","+(x+w)+","+(y-h)+","+fc+",0,5,"+lc+",0,"+lw+",0)");
	}
/**
	Write an rectangle with specified center and width, height
	@param x x-coordinate of center
	@param y y-coordinate of center
	@param w width of rectangle
	@param h height of rectangle
	@param z z-coordinate of rectangle
	@see #FillRectangle(double,double,double,double,int)
*/	
	public void WriteRectangle(double x,double y,double w, double h, int z) {
		OpenGroup();
			WriteLine(x-w,y-h,x+w,y-h,z);
			WriteLine(x+w,y-h,x+w,y+h,z);
			WriteLine(x+w,y+h,x-w,y+h,z);
			WriteLine(x-w,y+h,x-w,y-h,z);
		CloseGroup();
	}
/**
	Fill an rectangle with specified center and width, height
	@param x x-coordinate of center
	@param y y-coordinate of center
	@param w width of rectangle
	@param h height of rectangle
	@param z z-coordinate of rectangle
	@see #WriteRectangle(double,double,double,double,int)
*/	
	public void FillRectangle(double x,double y,double w, double h, int z) {
		out.println("(p8,"+z+",8,,5,"+fc+",0");
		OpenGroup();
		OpenGroup();
			WriteLine(x-w,y-h,x+w,y-h,z);
			WriteLine(x+w,y-h,x+w,y+h,z);
			WriteLine(x+w,y+h,x-w,y+h,z);
			WriteLine(x-w,y+h,x-w,y-h,z);
		CloseGroup();
		CloseGroup();
		out.println(")");
	}
/**
	Write a text with specified size
	@param x x-coordinate
	@param y y-coordinate
	@param z z-coordinate of text
*/	
	public void WriteText(double x,double y, int z, int size,String text) {
            double _x = trans.worldToWindowx(x);
            double _y = trans.worldToWindowy(y);
                out.println("(t14,"+z+",0,,"+_x+","+_y+",1,"+tc+",0,0,,wst:thames"+size+","+text+")");
	}

/**
	For test purpose only
*/
	public static void main(String[] args) 
	{
		InterleafOutput iout=null;

		try
		{
			iout = new InterleafOutput("/home/backhaus/desktop.ileaf/"+args[0],1.,1.,1.,1.,10,83);
		}
		catch ( FileNotFoundException e )
		{
			System.out.println(""+e);
			System.exit(0);
		}
		
 		
		iout.OpenDocument();
/*
		iout.OpenGroup();

		for( int i=0;i<1000;i++)
		iout.WriteEllipse(1.+Math.abs(10.*Math.random()),1.+Math.abs(10.*Math.random()),.01,.01,1);

		iout.CloseGroup();
*/

		iout.setTextColor(3);
		iout.setLineColor(5);
		iout.setFillColor(1);

		 iout.OpenGroup();
		  iout.WriteLine(1.,1.,2.,1.5,1);
		  iout.WriteLine(2.,2.,3.,3.,1);
		 iout.CloseGroup();
		
		 iout.OpenGroup();
		  iout.WriteEllipse(1.5,1.25,.5,.25,1);
		  iout.FillEllipse(2.5,2.5,.5,.5,1);
		 iout.CloseGroup();

		iout.setLineWidth(10.);

		 double[] x = {1.,2.,2.,1.};
		 double[] y = {1.,1.,2.,2.};
//		iout.WritePolyLine(x,y);
		iout.FillPolygon(x,y,1);
		iout.FillRectangle(3.,3.,0.2,0.3,1);
		iout.WriteRectangle(3.,3.,0.2,0.3,1);
		iout.WriteText(3.,3.,1,120,"Frank");

		iout.CloseDocument();
	}
}

 class CoordTransform {
	/*
	 *	Die persistenten Attribute der Abbildungsmatrix
	 */
	private double	RDBscale;
	private double	RDBxoffset;
	private double	RDByoffset;
	private double	RDBrand;
        
        private double windowheight;
	
	public double getRand() {
		return RDBrand;
	}

    public CoordTransform(  double worldxmin, double worldymin, double worldxmax, 
			    double worldymax, double windowwidth, double windowheight, double rand ) {
	/*	Tempor�re Variablen */

	windowwidth=windowwidth-2*rand;
	this.windowheight=windowheight-2*rand;

	double worlddx	= worldxmax - worldxmin;
	double worlddy	= worldymax - worldymin;
	double scalex = windowwidth  / worlddx;
	double scaley = windowheight / worlddy;
	double scale  = 1.;
	double xoffset= 0.;
	double yoffset= 0.;

	if(scalex < scaley) {
	    scale = scalex;
	    xoffset = -(worldxmin*scale);
	    yoffset = ( windowheight - (worlddy*scale) )/2.-(worldymin*scale);
	} else {
	    scale = scaley;
	    yoffset = -(worldymin*scale);
	    xoffset = ( windowwidth - (worlddx*scale) )/2.-(worldxmin*scale);
	}

	xoffset = ( windowwidth  - (worlddx*scale) )/2. - (worldxmin*scale);
	yoffset = ( windowheight - (worlddy*scale) )/2. - (worldymin*scale);

	xoffset+=rand;
	yoffset+=rand;

	/*
	 *	Initialisieren der eigenen Attribute
	*/
	RDBscale	= scale;
	RDBxoffset= xoffset;
	RDByoffset= yoffset;
	RDBrand	= rand;	
    }
	

    public CoordTransform() {
	RDBscale	= 1.;
	RDBxoffset= 0.;
	RDByoffset= 0.;
    }

    public double windowToWorldx(int x) {
	return (x - RDBxoffset) / RDBscale;
    }
    public double windowToWorldy(int y) {
	return (y - RDByoffset) / RDBscale;
    }
    public double worldToWindowx(double x) {
	return (RDBxoffset + (x * RDBscale));
    }
    public double worldToWindowy(double y) {
	return windowheight-(RDByoffset + (y * RDBscale));
    }
    public double scaleLength(double l) {
	return l*RDBscale;
    }
    public double getXoffset() {
	return RDBxoffset;
    }
    public double getYoffset() {
	return RDByoffset;
    }
}


