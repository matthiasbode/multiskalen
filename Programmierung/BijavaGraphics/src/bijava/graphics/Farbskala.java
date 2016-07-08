package bijava.graphics;

/** Die Klasse Farbskala enh�lt Funktionen, die dem Nutzer verschiedene Farbskalaen (z.B. von Blau nach Rot) in 
drei Eindimensionale Felder schreibt. Diese Felder enthalten die Farbwerte C(yan), M(agenter) und Y(ellow) der Farbskala 
im CMYK - Farbmodell (der K - Wert ist stets gleich 0) und lassen sich ueber entsprechende R�ckgabefunktionen der Klasse
Farbskala ausgeben.
Eine Ausnahme hiervon bildet die Graustufen - Skala. Sie liefert den K - Wert des CMYK - Modus (asl Eindimensionales Feld) 
direkt als R�ckgabewert der Funktion.
@author Jan Onne Backhaus
@version 1.0 
*/

public class Farbskala
{
		
	double elemente;	// Anzahl der Farben in der Farbskala
	double[] cyan;		// cyan = 1-red
	double[] magenta;	// magenta = 1-green
	double[] yellow;	// yelow = 1-blue	
	
		/* KONSTRUKTOR */
/**
Der Konstruktor f�llt den elemente - Wert der Klasse, damit nun von den Objektfunktionen auf ihn zugegriffen werden kann.
@param elemente gibt die Anzahl der Farbabstufungen der gew�nschten Farbskala an.
*/
	public Farbskala(double elemente)
	{
		this.elemente=elemente;
	}
	
	/* -------------------------------------------------------------------------------------------------------------*/
	/* ---------------------------------------- R�ckgabefunktionen -------------------------------------------------*/
	/* -------------------------------------------------------------------------------------------------------------*/
	
/**
Gibt ein eindimensionales Feld mit dem Cyan - Farbanteil der n Farbelemente zur�ck. 
*/	
	public double[] getCyan()
	{return cyan;}
/**
Gibt ein eindimensionales Feld mit dem Magenta - Farbanteil der n Farbelemente zur�ck. 
*/		
	public double[] getMagenta()
	{return magenta;}
/**
Gibt ein eindimensionales Feld mit dem Yellow - Farbanteil der n Farbelemente zur�ck. 
*/	
	public double[] getYellow()
	{return yellow;}
	
	
	
	/* -------------------------------------------------------------------------------------------------------------*/
	/* ---------------------------------------- Verschiedene FARBSKALEN --------------------------------------------*/
	/* -------------------------------------------------------------------------------------------------------------*/
	
	

	
	/* ---------------------------------------- Funktion streiche -------------------------------------------------*/
	/* Funktion zum streichen von Nachkommastellen	*/
	/**
	Funktion zum streichen von Nachkommastellen.
	ACHTUNG: Es hat sich in einem Test ergeben, da� die Funktion Fehlerhaft ist. Auf Grund eines (wahrscheinlichen)
	Rundungsfehlers werden die Zahlen teilweise sogar noch l�nger, als vorher. Dies ist in Bezug auf Interleaf nicht schlimm,
	da die Farbwerte durchaus mehr, als nur die in der Dokumentation beschriebenen 3 Nachkommastellen haben d�rfen.
	@param zahl Die zu bearbeitende Zahl
	@param k Die Anzahl der Nachkommastellen, die nach dem Streichen noch vorhanden sein sollen.
	*/
	
	public double streiche(double zahl, int k)	
	{
		int mutipl = 1;							// der Multiplikator 
		for(int i =0; i<k;i++) mutipl=mutipl*10; 
				
		int zwischensumme;
		zahl = zahl*(double)mutipl;
		zwischensumme = (int)zahl;
		zahl = (double)zwischensumme;
		zahl = zahl/(double)mutipl;
		return zahl;
	}
	
	/* --------------------------------------Schwarz - Wei� - Skala ----------------------------------------------*/
	/* ---------------------------------------- Funktion saSkala -------------------------------------------------*/
	/* swSkala ist eine Farbskala von Wei� nach Schwarz. Ihr Verlauf ist Liniear.*/
	
	/*
	public void swSkala()
	{	
		for(int i=0; i<=elemente; i++)
		{
		double ergebnis = (100./elemente)*i;	// Unterteilen der Skala in x elemente	
		ergebnis = streiche(ergebnis, 3);
		IOS.writeln ("c"+i+" =  0, 0, 0, "+ergebnis+",");	// Schreiben in die Datei
				
		
		}
	}
	*/
	
	/* ---------------------------------------- Funktion feldSWskala -------------------------------------------------*/
	/**
	Farbskala in n Schritten von wei� nach schwarz. Ihr Verlauf ist Liniear.
	Die Funktion liefert die Farbwerte in einem 1.dim Feld f�r den CMYK - Modus
	ACHTUNG: Es wird nur der Schwarzwert gegeben. Die anderen (C,M und Y) sind eigenst�ndig auf 0 zu setzen
	*/
	
	public double[] feldSWskala()
	{	
		double[] fbWerte;
		fbWerte = new double[(int)elemente];
		
		for(int i=0; i<elemente; i++)
		{
		double ergebnis = (100./elemente)*i;	// Unterteilen der Skala in x elemente	
		ergebnis = streiche(ergebnis, 3);
		//IOS.writeln ("c"+i+" =  0, 0, 0, "+ergebnis+",");	// Schreiben in die Datei
		fbWerte[i] = ergebnis;
		}
		
		return fbWerte;
	}
	
	
	/* -------------------------------------------------------------------------------------------------------------*/
	/* ---------------------------------------- 2 - FARBEN - FARBSKALEN --------------------------------------------*/
	/* -------------------------------------------------------------------------------------------------------------*/
	
	
	/* --------------------------------------- Blau - Gruen - Skala ------------------------------------------------*/
	/* ---------------------------------------- Funktion BlauGruen -------------------------------------------------*/
	/**
	Die Funktion BlauGruen erstellt die Werte eines Farbverlaufes von Blau nach Gruen und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	
	public void BlauGruen()		// Farbskala von Blau nach Gruen 
	{
		
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];
		
		
		for(int i=0; i<=elemente; i++)
		{
		
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		
		// schreiben der Farbdaten in die drei Felder
		
		cyan[i] 	= 100;			// magenta bleibt unver�ndert = 0
		magenta[i] 	= 100-ergebnis;		// abw�rtslaufend		
		yellow[i]	= 0+ergebnis;		// aufw�rtslaufend
		
		// Ausgeben der Daten auf den Bildschirm zu Kontrolle
 //IOS.writeln("cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]);
		
		}
		
		
	}
	
	
	/* --------------------------------------- Gruen - Rot - Skala ------------------------------------------------*/
	/* ---------------------------------------- Funktion GruenRot -------------------------------------------------*/
	/**
	Die Funktion GruenRot erstellt die Werte eines Farbverlaufes von Gruen nach Rot und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	
	public void GruenRot()		 
	{
		
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];
		
		
		for(int i=0; i<=elemente; i++)
		{
		
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		
		// schreiben der Farbdaten in die drei Felder
		
		cyan[i] 	= 100-ergebnis;	
		magenta[i] 	= 0+ergebnis;	
		yellow[i]	= 100;		
		
		// Ausgeben der Daten auf den Bildschirm zu Kontrolle
 // IOS.writeln("cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]);
		
		}
		
		
	}
	
	/* --------------------------------------- Gruen - Blau - Skala ------------------------------------------------*/
	/* ---------------------------------------- Funktion GruenBlau -------------------------------------------------*/
	/**
	Die Funktion GruenRot erstellt die Werte eines Farbverlaufes von Gruen nach Rot und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	
	public void RotBlau()		 
	{
		
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];
		
		
		for(int i=0; i<=elemente; i++)
		{
		
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		
		// schreiben der Farbdaten in die drei Felder
		
		cyan[i] 	= ergebnis;	
		magenta[i] 	= 100;	
		yellow[i]	= 100-ergebnis;		
		
		// Ausgeben der Daten auf den Bildschirm zu Kontrolle
 // IOS.writeln("cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]);
		
		}
		
		
	}
	/**
	Die Funktion Gruenblau erstellt die Werte eines Farbverlaufes von Gruen nach Blau und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	
	public void GruenBlau()		 
	{
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];

		for(int i=0; i<=elemente; i++)
		{
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		// schreiben der Farbdaten in die drei Felder
		cyan[i] 	= 100;	
		magenta[i] 	= 0+ergebnis;	
		yellow[i]	= 100-ergebnis;		
		}
	}

	/**
	Die Funktion RotGelb erstellt die Werte eines Farbverlaufes von Rot nach Gelb und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	public void RotGelb()		 
	{
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];

		for(int i=0; i<=elemente; i++)
		{
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		// schreiben der Farbdaten in die drei Felder
		cyan[i] 	= 0;	
		magenta[i] 	= 100-ergebnis;	
		yellow[i]	= 100;		
		}
	}
/**
	Die Funktion GruenGelb erstellt die Werte eines Farbverlaufes von Gruen nach Gelb und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
		public void GruenGelb()		 
	{
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];

		for(int i=0; i<=elemente; i++)
		{
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		// schreiben der Farbdaten in die drei Felder
		cyan[i] 	= 100-ergebnis;	
		magenta[i] 	= 0;	
		yellow[i]	= 100;		
		}
	}
/**
	Die Funktion BlauGelb erstellt die Werte eines Farbverlaufes von Blau nach Gelb und speichert sie in drei
	Feldern. Die Felder beziehen sich auf den CMYK - Modus (K - Wert immer 0).
	*/
	public void BlauGelb()		 
	{
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];

		for(int i=0; i<=elemente; i++)
		{
		// Einteilung der Farbskala in n gleich gro�e Elemente mit h�chstens 3 Nachkommastellen
		double ergebnis = (100./elemente)*i;
		ergebnis = streiche(ergebnis, 3);
		// schreiben der Farbdaten in die drei Felder
		cyan[i] 	= 100-ergebnis;	
		magenta[i] 	= 100-ergebnis;	
		yellow[i]	= ergebnis;		
		}
	}

	
	
	/* -------------------------------------------------------------------------------------------------------------*/
	/* ---------------------------------------- Viel FARBEN FARBSKALEN ---------------------------------------------*/
	/* -------------------------------------------------------------------------------------------------------------*/
	
	
	/* ---------------------------------------Blau - Wei� - Gruen - Skala ----------------------------------------------*/
	/* ---------------------------------------- Funktion BlauWei�Gruen -------------------------------------------------*/
	
	
	/**
	Die Funktion stellt die Werte fuer eine Farbskala von Blau ueber Wei� nach Gruen zur Verfuegung.
	ACHTUNG:
	(1)
	Die Zahl der Farbschritte (Elemente) steht f�r die Zahl der Elemente, die nicht Wei� sind. Es wird immer noch ein Element
	mit der Farbe Wei� hinzuaddiert.
		
	(2)
	Die Zahl der Schritte(Elemente), in der die Farbskala durchlaufen werden soll mu� immer gerade sein. 
	Ist dies nicht der Fall, so wandelt die Funktion die Zahl der Elemente in eine gerade Zahl um (Elemente  = Elemente +1).
	
	(3)
	Da je nach der Wahl der Elementenanzahl die Schrittgr��e von iner zur n�chsten Farbe variieren, kann es in der Mitte
	der Farbskala zu mehreren Elementen (in der Regel zwei bis drei) Elemente mit dem Farbwert "Wei�" kommen.
		
	zu (1) und (2)
	Es kann passieren, da� die von der Funktion erstellte Farbskala bis zu 1 Elemente mehr eth�lt, als man eingegeben hat.
	Dies passiert immer dann, wenn eine ungerade Zahl der Elemente benutzt wird. 
	
	*/
	
	
	public void BlauWeissGruen()
	{

	
	if ((elemente%2)!=0.0) elemente = elemente +1; // hier werden die Elemente durch das Addieren von 1 auf eine Gerade Zahl gebracht
	int elementeHalbe = (int) (elemente/2);
	
	cyan = new double[(int)elemente+1+1+1];
	magenta = new double[(int)elemente+1+1+1];
	yellow = new double[(int)elemente+1+1+1];
	
	
	for(int i = 0; i<=elemente ; i++)
	{
		if (i<elementeHalbe)	// Verlauf von Blau nach Wei�
		{
			cyan[i] = streiche(100. - ((100./elementeHalbe)*i), 3);
			magenta[i] = streiche(100. - ((100./elementeHalbe)*i),3);
			yellow[i] = 0;
			//IOS.writeln("B->W "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
		}
		
		if (i==elementeHalbe)	// Wei�
		{
			cyan[i] = 0;
			magenta[i] = 0;
			yellow[i] = 0;
			//IOS.writeln("W "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
		}
		
		if (i>=elementeHalbe+1)	// Verlauf von Wei� nach Gruen
		{
			cyan[i+1] = streiche((100./elementeHalbe)*(i-elementeHalbe),3);
			magenta[i+1] = 0;
			yellow[i+1] = streiche((100./elementeHalbe)*(i-elementeHalbe),3);
			//IOS.writeln("W->G "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
		}
	}
	}


	/* ---------------------------------------Blau - Wei� - Rot - Skala ----------------------------------------------*/
	/* ---------------------------------------- Funktion BlauWei�Rot -------------------------------------------------*/
	/**
	Die Funktion BlauWei�Rot erstellt eine Farbskala von Blau �ber Wei� nach Rot und schreibt die CMY - Farbwerte
	in die entprechenden Felder (cyan[], magenta[], yellow[])
	Die Methode l�uft genau so ab, wie auch die BlauWei�Gruen Methode. Der Einzige Unterschied besteht in der Farbauswahl
	(3. if - Abfrage in der for - Schleife).
	ACHTUNG: Besonderheiten der Funktion siehe: Blau - Wei� - Gruen - Skala; "Funktion BlauWei�Gruen"
	*/
	
	public void BlauWeissRot()
	{
	if ((elemente%2)!=0.0) elemente = elemente +1; // hier werden die Elemente durch das Addieren von 1 auf eine Gerade Zahl gebracht
	int elementeHalbe = (int) (elemente/2);
	
	cyan = new double[(int)elemente+1+1+1];
	magenta = new double[(int)elemente+1+1+1];
	yellow = new double[(int)elemente+1+1+1];
	
	
	for(int i = 0; i<=elemente ; i++)
		{
			if (i<elementeHalbe)	// Verlauf von Blau nach Wei�
			{
				cyan[i] = streiche(100. - ((100./elementeHalbe)*i), 3);
				magenta[i] = streiche(100. - ((100./elementeHalbe)*i),3);
				yellow[i] = 0;
				//IOS.writeln("B->W "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
			}
		/*
			if (i==elementeHalbe)	// Wei�
			{
				cyan[i] = 0;
				magenta[i] = 0;
				yellow[i] = 0;
				//IOS.writeln("W "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
			}
		*/	
			if (i>=elementeHalbe+1)	// Verlauf von Wei� nach Gruen
			{
				cyan[i+1] = 0;
				magenta[i+1] = streiche((100./elementeHalbe)*(i-elementeHalbe),3);
				yellow[i+1] = streiche((100./elementeHalbe)*(i-elementeHalbe),3);
				//IOS.writeln("W->G "+" c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
			}
		//IOS.writeln("c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");
		}
	}
	
	
	/* ---------------------------------------Blau - Gruen - Gelb - Rot - Skala -------------------------------------------*/
	/* ---------------------------------------- Funktion BlauGruenGelbRot -------------------------------------------------*/
	/**
	Die Funktin erstellt eine Farbskala von Blau �ber Gruen und Gelb nach Rot und speichert ihre Werte in drei Feldern
	(Cyan, Magenta, Yellow) der Klasse.
	ACHTUNG: Die Funktion erwartet eine Elementenzahl (Farbabchritte), die durch 3 teilbar ist.
	F�r den Fall, da� die Elementenzahl nicht durch 3 teilbar ist, wird die Funktion die Zahl auf die n�chst h�here
	durch 3 teilbare Zahl erh�hen.
	*/
	public void BlauGruenGelbRot()
	{
		if (elemente%3!=0)	// �berpr�fen, ob durch 3 teilbar und dann evtl. erh�hen
		{
		//	IOS.writeln("elemente vorher: "+elemente);
			elemente+=(3-elemente%3);
		//	IOS.writeln("elemente nacher: "+elemente);
		}
		
		cyan = new double[(int)elemente+1];
		magenta = new double[(int)elemente+1];
		yellow = new double[(int)elemente+1];
		
		int elementeDrittel = (int)(elemente/3);
		//IOS.writeln("elemente sind: "+elemente);
		//IOS.writeln("elementeDrittel sind: "+elementeDrittel);
		
		
		for(int i = 0; i<=(int)elemente; i++)
		{
	
			if(i<=elementeDrittel) 		// Blau --> Gruen
			{
				cyan[i]		= 100.;
				magenta[i]	= streiche(100.-(100./elementeDrittel)*i,3);
				yellow[i]	= streiche((100./elementeDrittel)*i,3);
			}
			
			if(i<=elementeDrittel*2 && i>elementeDrittel)		// Gruen --> Gelb
			{
				cyan[i]		= streiche(100.-(100./elementeDrittel)*(i-elementeDrittel),3);
				magenta[i]	= 0.0;
				yellow[i]	= 100.;
			}
			
			if(i>elementeDrittel*2)		// Gelb --> Rot
			{
				cyan[i]		= 0.0;
				magenta[i]	= streiche((100./elementeDrittel)*(i-2*elementeDrittel),3);
				yellow[i]	= 100;
			}
			//IOS.writeln("c"+i+" = (cyan = "   +cyan[i]+" / magenta = "+magenta[i]+" / yellow = " +yellow[i]+")");			
		}
		
	}
	
	
	
	
	
	
	/*
	Test - Funktion
	
	public static void main (String[] args)
	{
		
		Farbskala farbskala;
		farbskala = new Farbskala(23);
//	farbskala.BlauGruen();
//	farbskala.feldSWskala();
	farbskala.BlauWei�Gruen();
	}
	*/
	
}
