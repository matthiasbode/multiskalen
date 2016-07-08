package bijava.geometry.dimN;
import bijava.math.function.DerivationNd;
import bijava.math.function.ScalarFunctionNd;

public class NaturalCoordinateFunctionNd extends LocalCoordinateFunctionNd {
	
	//Haelt 4 Punkte vor
	private static PointNd bekannt[]=new PointNd[]
	          {new PointNd(),new PointNd(),new PointNd(),new PointNd()};
	private static double nat[][]=new double[4][];
	public static boolean vorhalten=true;
	private static int neuste=0;
	
	public NaturalCoordinateFunctionNd(ConvexPolyhedronNd cell, PointNd coord) {
		super.setElement(cell);
		super.setCoordinate(coord);
	}
	
	/** Gibt den Wert der Funktion an der Stelle des Punktes wieder
	 * Wenn der Punkt nicht im Elementliegt, dann wird 0 zurueckgegeben.
	 * @author pick
	 * @version 24.02.2005
	 * @param Punkt fuer den die Koordinaten ermitteltwerden
	 * @return Wert der Funktion an diesem Punkt
	 * 
	 */

	public double getValue(PointNd p) {
		if(!element.contains(p)) return 0;
		if(vorhalten)
		{
			int pos=-1;
			for(int i=0;i<4;i++)
				if(bekannt[(neuste+i+3)%4].equals(p))
				{

//					System.out.println(i);
					pos=(neuste+i+3)%4;
//					System.out.println("pos="+pos);
					i=0;
					while (i < nat[pos].length) {
						if (element.getNodes() [i] == super.getCoordinate())
							return nat[pos][i];
						i++;
					}
				}
			double[] result = super.getElement().getNaturalElementCoordinates(p);
			bekannt[neuste]=p;
			nat[neuste]=result;
			neuste=(neuste+1)%4;
			
			int i = 0;
			while (i < result.length) {
				if (element.getNodes() [i] == super.getCoordinate())
					return result[i];
				i++;
			}
			return Double.NaN;
			
		}
		double[] result = super.getElement().getNaturalElementCoordinates(p);
		
		int i = 0;
		while (i < result.length) {
			if (element.getNodes() [i] == super.getCoordinate())
				return result[i];
			i++;
		}
		return Double.NaN;
	}
	
	public int definitionDimension()
	{
		return getCoordinate().dim();
	}

	/**Liefert den Gradienten der Ansatzfunktion fuer einen Punkt im Element (Funktion bisher nicht getestet!).
	 *@param x Punkt an dem der Gradient zu bestimmen ist
	 *@return Feld der Anteile des Gradienten in der Reihenfolge der Koordinatenrichtungen des kartesischen Koordinatensystems*/
	public VectorNd getGradient(PointNd p) {
		
		if(definitionDimension()==2)
		{
			PointNd ecken[]=element.getNodes();
		
			//Bilden der 3 Punkte
			PointNd punkte[]=new PointNd[3];
	
			punkte[0]=ecken[0].sub(p).mult(0.00001).add(p);
			punkte[1]=ecken[1].sub(p).mult(0.00001).add(p);
			punkte[2]=ecken[2].sub(p).mult(0.00001).add(p);
			
	
			
			double wert[]=new double[3];
			for(int i=0;i<3;i++)
			{
				wert[i]=getValue(punkte[i]);
			}
				
			
			SimplexNd simp=new SimplexNd(punkte);
			double grund=simp.getVolume();
			
			double erg[]=new double[2];
			
			double scx[]=new double[3];
			scx[0]=(punkte[1].x[1]-punkte[2].x[1])/(2*grund);
			scx[1]=(punkte[2].x[1]-punkte[0].x[1])/(2*grund);
			scx[2]=(punkte[0].x[1]-punkte[1].x[1])/(2*grund);
			
			double scy[]=new double[3];
			scy[0]=(punkte[2].x[0]-punkte[1].x[0])/(2*grund);
			scy[1]=(punkte[0].x[0]-punkte[2].x[0])/(2*grund);
			scy[2]=(punkte[1].x[0]-punkte[0].x[0])/(2*grund);
			
			for(int i=0;i<3;i++)
			{
				erg[0]+=wert[i]*scx[i];
				erg[1]+=wert[i]*scy[i];
			}
			
			
			return new VectorNd(erg);
		}
		
		if(definitionDimension()==3)
		{
			double min=Double.POSITIVE_INFINITY;
			ConvexPolyhedronNd face[]=element.getFacetsOfOrder(1);
			for(int i=0;i<face.length;i++)
			{
				double vol=face[i].getVolume();
				if(vol<min) min=vol;
			}
			
//			System.out.println("Min= "+min);
			
		
			//Bilden der 3 Punkte
			PointNd punkte[]=new PointNd[3];
	
			for(int i=0;i<3;i++)
			{
				punkte[i]=new PointNd(p.dim());
				for(int j=0;j<p.dim();j++)
				{
					punkte[i].x[j]=p.x[j];
				}
			}
			
			double delta=min/100.;
			
			punkte[0].x[0]+=delta;
			punkte[1].x[1]+=delta;
			punkte[2].x[2]+=delta;
			
	
			
			double wert[]=new double[3];
			for(int i=0;i<3;i++)
			{
				wert[i]=getValue(punkte[i]);
//				System.out.println(i+" "+wert[i]);
			}
				
			
			double hier=getValue(p);
//			System.out.println("hier "+hier);
			
			double erg[]=new double[3];
			
			for(int i=0;i<3;i++)			
				erg[i]=(hier-wert[i])/(1.*delta);
			
			
			return new VectorNd(erg);
		}
		
		return null;
		
	}
	
	public ScalarFunctionNd[] getDerivation()
	{
		DerivationNd dev=new DerivationNd(this);
		return dev.getDirectionalDerivation();
		
	}

    public int getDim() {
        return element.getDimension();
    }
}
