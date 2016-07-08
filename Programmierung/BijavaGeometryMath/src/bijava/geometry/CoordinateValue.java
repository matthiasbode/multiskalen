package bijava.geometry;


public class CoordinateValue {
    
    public EuclideanPoint  coordinate;
    public double   value;
    public double[] gradient;
    
    
    public CoordinateValue(EuclideanPoint p, double v) {
        this.coordinate = p;
        this.value = v;
        this.gradient=new double[2];
    }
    
    
    public CoordinateValue( EuclideanPoint p, double v, double[] grad ) {
        this.coordinate = p;
        this.value = v;
        this.gradient = new double[] {grad[0], grad[1]};
    }
    
    
    public CoordinateValue( CoordinateValue cv ) {
        this.coordinate = cv.coordinate;
        this.value = cv.value;
        this.gradient = new double[] {cv.gradient[0], cv.gradient[1]};
    }
    
    public double getValue(){
        return value;
    }
    
    public double[] getGradient(){
        return gradient;
    }
    
    public EuclideanPoint getPoint(){
        return coordinate;
    }
    
    public void setPoint(EuclideanPoint point){
        this.coordinate=point;
    }
    
}