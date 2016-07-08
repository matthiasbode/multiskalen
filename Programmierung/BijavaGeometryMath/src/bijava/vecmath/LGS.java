package bijava.vecmath;

/**
 *   		  LGS - Klasse zum Loesen linearer Gleichungssysteme der Gestalt A * x = b <br>
 *   		  	A ist eine m x n Matrix<br>
 *
 *   		  Die Loesung wird mit dem Algorithmus nach Gauss erzeugt. <br>
 *   		  			(s.a. H.Grassmann "Algebra und Geometrie" ) <br>
 *
 *   @author Axel Schwoeppe <br>
 */
final public class LGS {

	static private DMatrix lgs; /*..zu loesendes lineares Gleichungssystem..*/
	static private int m, n; /*..Zeilen und Spalten der Matrix des Systems..*/

	static final double EPSILON = 1.0E-7; //..Rechengenauigkeit!..

	/*..Konstruktor..*/ //..brauch man den ???..
	LGS() {
		lgs = (DMatrix) null;
		m = n = 0;
	}

	private LGS(DMatrix mat) {
		lgs = new DMatrix(mat);

		m = lgs.getRows();
		n = lgs.getCols() - 1;
	}

	/*..Methoden..*/

	/** Loesen des MxN-Gleichungssystems A*x=b <br>
	 *	@param	lgsMxN	Matrix, die in den ersten Spalten die Koeffizientenmatrix des <br>
	 *			Gleichungssystems und in der letzten Spalte die rechte Seite <br>
	 *			des Systems enthaelt.
	 *
	 *	@return	Die zurueckgelieferte Matrix beinhaltet die Loesungsmenge des Systems. <br>
	 *   		Ein System kann drei unterschiedliche Arten von Loesungen erzeugen.<br>
	 *   		  	1. Das System besitzt keine Loesung:
	 *   		  	   Der Wert null wird zurueckgeliefert.
	 *   		  	2. Das System besitzt eine eindeutige Loesung:
	 *   			   Eine m x 1 Matrix (Loesungsvektor) wird zurueckgeliefert.
	 *   			3. Das System besitzt eine mehrdeutige Loesung:
	 *   			   Eine m x n Matrix wird zurueckgeliefert. Die erste Spalte
	 *   			   enthaelt den Ursprung, die weiteren die Basisvektoren des
	 *   			   Loesungsraumes.
	 */
	static public DMatrix gauss(DMatrix lgsMxN) {
		lgs = new DMatrix(lgsMxN);

		m = lgs.getRows();
		n = lgs.getCols() - 1;

		boolean done = false;
		for (int k = 0; k < m; k++) {

			int j = k;
			if (Math.abs(lgs.getItem(k, j)) < EPSILON) {
				int i = k;
				while (Math.abs(lgs.getItem(i, j)) < EPSILON && !done) {
					i++;
					if (i == m) {
						i = k;
						if (++j > n)
							done = true;
					}
				}
				if (!done)
					tauscheZeilen(i, k);
			}

			if (!done) {
				multZeile(k, (1. / lgs.getItem(k, j)));

				for (int i = 0; i < k; i++)
					subZeilen(lgs.getItem(i, j) / lgs.getItem(k, j), k, i);
				for (int i = m - 1; i > k; i--)
					subZeilen(lgs.getItem(i, j) / lgs.getItem(k, j), k, i);
			}
		}

		return getSolution(ausgezeichneteSpalten());
	}

	/*..tauschen der i. Zeile mit der k. Zeile......................*/
	static private void tauscheZeilen(int i, int k) {
		double tmp;
		for (int l = 0; l < (n + 1); l++) {
			tmp = lgs.getItem(i, l);
			lgs.setItem(i, l, lgs.getItem(k, l));
			lgs.setItem(k, l, tmp);
		}
	}

	/*..multipliziere i. Zeile mit s................................*/
	static void multZeile(int i, double s) {
		for (int l = 0; l < (n + 1); l++)
			lgs.setItem(i, l, lgs.getItem(i, l) * s);
	}

	/*..subtrahiere das vf-fache der k. Zeile von der i. Zeile......*/
	static void subZeilen(double vf, int k, int i) {
		for (int l = 0; l < (n + 1); l++)
			lgs.setItem(i, l, (lgs.getItem(i, l) - vf * lgs.getItem(k, l)));
	}

	/*..Auffinden der ausgezeichneten Spalten.......................*/
	/*..es wird ein boolean-Feld zurueckgegeben, in dem die.........*/
	/*..ausgezeichneten Spalten den Wert wahr beinhalten............*/
	static boolean[] ausgezeichneteSpalten() {

		boolean[] kr = new boolean[n];
		boolean gef = false, flag;

		for (int j = 0; j < m; j++) {
			int i = 0;
			while (!gef && i < n) {
				if (Math.abs(lgs.getItem(j, i) - 1.) < EPSILON) {
					flag = true;
					for (int l = 0; l < j; l++)
						if (lgs.getItem(l, i) != 0)
							flag = false;
					for (int l = j + 1; l < m; l++)
						if (Math.abs(lgs.getItem(l, i)) > EPSILON)
							flag = false;
					if (flag)
						gef = true;
				}
				i++;
			}
			if (gef) {
				kr[i - 1] = true;
				gef = false;
			}
		}

		return kr;
	}

	/*..konstruieren der Loesungmenge.............................*/
	static DMatrix getSolution(boolean[] kr) {
		DMatrix erg = (DMatrix) null;
		//..Matrix, die mit der Loesung zu belegen ist..
		int r = 0; //..Anzahl der ausgezeichneten Spalten..........

		/*..Berechnung der Anzahl der ausgezeichneten Spalten aus dem Boolean-Feld........*/
		for (int i = 0; i < kr.length; i++)
			if (kr[i])
				r++;

		/*..wenn keine Loesung, liefere NULL zurueck..*/
		for (int i = r; i < lgs.getRows(); i++)
			if (Math.abs(lgs.getItem(i, lgs.getCols() - 1)) > EPSILON) {
				erg = (DMatrix) null;
				return erg;
			}

		/*..wenn eindeutige Loesung, liefere Loesungvektor zurueck..*/
		if (r == lgs.getCols() - 1) {
			erg = new DMatrix(lgs.getCols() - 1, 1);
			for (int i = 0; i < erg.getRows(); i++)
				erg.setItem(i, 0, lgs.getItem(i, lgs.getCols() - 1));

			return erg;
		}

		/*..wenn mehrdeutige Losung, liefere Matrix zurueck, in der die erste Spalte.....*/
		/*..den Ursprung, alle weiteren Spalten die Basisvektoren des durch die Loesung..*/
		/*..gegebenen Raumes enthalten...................................................*/
		int n = lgs.getCols() - 1 - r + 1; //..+1 wegen Ursprung!
		int count1 = 1, count2 = 1;
		erg = new DMatrix(lgs.getCols() - 1, n);

		for (int i = 0; i < kr.length; i++) {
			if (!kr[i])
				erg.setItem(i, count1++, 1);
			else {
				int count3 = 1;
				erg.setItem(i, 0, lgs.getItem(count2 - 1, lgs.getCols() - 1));
				for (int l = 0; l < kr.length; l++)
					if (!kr[l])
						erg.setItem(
							i,
							count3++,
							- (lgs.getItem(count2 - 1, l)));

				count2++;
			}
		}
		return erg;
	}

	static public int rangMat(DMatrix mat) {
		return new LGS(mat).rang();
	}

	private int rang() {
		alg();

		int rang = 0;

		// Auffinden der ausgezeichenten Spalten
		boolean[] kr = new boolean[n + 1];
		boolean gef = false, flag;

		for (int j = 0; j < m; j++) {
			int i = 0;
			while (!gef && i < n + 1) {
				if (Math.abs(lgs.getItem(j, i) - 1.) < EPSILON) {
					flag = true;
					for (int l = 0; l < j; l++)
						if (lgs.getItem(l, i) != 0)
							flag = false;
					for (int l = j + 1; l < m; l++)
						if (Math.abs(lgs.getItem(l, i)) > EPSILON)
							flag = false;
					if (flag)
						gef = true;
				}
				i++;
			}
			if (gef) {
				kr[i - 1] = true;
				gef = false;
			}
		}

		/*..Berechnung der Anzahl der ausgezeichneten Spalten aus dem Boolean-Feld........*/
		for (int i = 0; i < kr.length; i++)
			if (kr[i])
				rang++;

		return rang;
	}

	/* eigentliche Algorithmus */
	private void alg() {
		boolean done = false;
		for (int k = 0; k < m; k++) {

			int j = k;
			if (Math.abs(lgs.getItem(k, j)) < EPSILON) {
				int i = k;
				while (Math.abs(lgs.getItem(i, j)) < EPSILON && !done) {
					i++;
					if (i == m) {
						i = k;
						if (++j > n)
							done = true;
					}
				}
				if (!done)
					tauscheZeilen(i, k);
			}

			if (!done) {
				multZeile(k, (1. / lgs.getItem(k, j)));

				for (int i = 0; i < k; i++)
					subZeilen(lgs.getItem(i, j) / lgs.getItem(k, j), k, i);
				for (int i = m - 1; i > k; i--)
					subZeilen(lgs.getItem(i, j) / lgs.getItem(k, j), k, i);
			}
		}
	}
}
