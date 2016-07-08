package bijava.geometry.dim3;

import javax.media.j3d.BoundingBox;
import javax.vecmath.Vector3f;

public class Triangle3d extends Polygon3d {
    double area = 0.;
    double xmin, xmax, ymin, ymax; // bounding Box
    double VoronoiRadius;
    Point3d VoronoiPoint;
    
    /** triangele element */
    public Triangle3d(Point3d points0,Point3d points1,Point3d points2) {
        super(new Point3d[]{points0, points1, points2});
        
        area = (
                - points0.x * points2.y
                - points1.x * points0.y
                - points2.x * points1.y
                + points0.x * points1.y
                + points1.x * points2.y
                + points2.x * points0.y
                ) / (double) 2.0;
        
        xmin = Math.min(points0.x, Math.min(points1.x,points2.x));
        xmax = Math.max(points0.x, Math.max(points1.x,points2.x));
        ymin = Math.min(points0.y, Math.min(points1.y,points2.y));
        ymax = Math.max(points0.y, Math.max(points1.y,points2.y));
        
        double c=Math.sqrt(Math.pow(points[0].x-points[1].x,2)+Math.pow(points[0].y-points[1].y,2));
        double b=Math.sqrt(Math.pow(points[1].x-points[2].x,2)+Math.pow(points[1].y-points[2].y,2));
        double a=Math.sqrt(Math.pow(points[2].x-points[0].x,2)+Math.pow(points[2].y-points[0].y,2));
        double s=0.5*(a+b+c);
        double z=s*(s-a)*(s-b)*(s-c);
        
        double w= sign((a*a+b*b-c*c)/a*b) * Math.sqrt(Math.abs(z*z/c*c-0.25));
        double x=0.5*(points[1].x-points[0].x)-w*(points[1].y-points[0].y)+points[0].x;
        double y=0.5*(points[1].y-points[0].y)-w*(points[1].x-points[0].x)+points[0].y;
        
        VoronoiRadius=0.25*(a*b*c)/Math.sqrt(z);
        VoronoiPoint = new Point3d(x,y,0.);
        
    }
    
    public Vector3f getNormal(){
        Vector3f normal = new Vector3f();
        
        normal.x = (float)((points[0].y - points[1].y) * (points[0].z + points[1].z) +
                (points[1].y - points[2].y) * (points[1].z + points[2].z) +
                (points[2].y - points[0].y) * (points[2].z + points[0].z));
        
        normal.y = (float)((points[0].z - points[1].z) * (points[0].x + points[1].x) +
                (points[1].z - points[2].z) * (points[1].x + points[2].x) +
                (points[2].z - points[0].z) * (points[2].x + points[0].x));
        
        normal.z = (float)((points[0].x - points[1].x) * (points[0].y + points[1].y) +
                (points[1].x - points[2].x) * (points[1].y + points[2].y) +
                (points[2].x - points[0].x) * (points[2].y + points[0].y));
        
        float l = normal.length();
        
        normal.x = normal.x / l;
        normal.y = normal.y / l;
        normal.z = normal.z / l;
        
        return normal;
    }
    
//    public BoundingBox getBoundingBox() {
//        double minX=Math.min(points[0].x, points[1].x);
//        minX=Math.min(minX, points[2].x);
//        double minY=Math.min(points[0].y, points[1].y);
//        minY=Math.min(minY, points[2].y);
//        double minZ=Math.min(points[0].z, points[1].z);
//        minZ=Math.min(minZ, points[2].z);
//
//        double maxX=Math.max(points[0].x, points[1].x);
//        maxX=Math.max(maxX, points[2].x);
//        double maxY=Math.max(points[0].y, points[1].y);
//        maxY=Math.max(maxY, points[2].y);
//        double maxZ=Math.max(points[0].z, points[1].z);
//        maxZ=Math.max(maxZ, points[2].z);
//
//        Point3d lower=new Point3d(minX, minY, minZ);
//        Point3d upper=new Point3d(maxX, maxY, maxZ);
//        return new BoundingBox(lower, upper);
//    }
    
    public boolean contains(Point3d p) {
        if(p==null)
            return false;
        
        double epsilon=1e-6;
        double a=this.getArea();
        double[] erg=new double[3];
        //erste Teilfläche
        erg[0]=points[1].x*(points[2].y-p.y)+points[2].x*(p.y-points[1].y)+p.x*(points[1].y-points[2].y);
        erg[0]/=2.*a;
        if (erg[0]<=-epsilon)
            return false;
        //zweite Teilfläche
        erg[1]=points[2].x*(points[0].y-p.y)+points[0].x*(p.y-points[2].y)+p.x*(points[2].y-points[0].y);
        erg[1]/=2.*a;
        if (erg[1]<=-epsilon)
            return false;
        //dritte Teilfläche
        erg[2]=points[0].x*(points[1].y-p.y)+points[1].x*(p.y-points[0].y)+p.x*(points[0].y-points[1].y);
        erg[2]/=2.*a;
        if (erg[2]<=-epsilon)
            return false;
        
        return true;
    }
    public double[] getNaturefromCart(Point3d p) {
        if(p==null)
            return null;
        
        double epsilon=1e-6;
        double a=this.getArea();
        double[] erg=new double[3];
        //erste Teilfläche
        erg[0]=points[1].x*(points[2].y-p.y)+points[2].x*(p.y-points[1].y)+p.x*(points[1].y-points[2].y);
        erg[0]/=2.*a;
        if (erg[0]<=-epsilon)
            return null;
        //zweite Teilfläche
        erg[1]=points[2].x*(points[0].y-p.y)+points[0].x*(p.y-points[2].y)+p.x*(points[2].y-points[0].y);
        erg[1]/=2.*a;
        if (erg[1]<=-epsilon)
            return null;
        //dritte Teilfläche
        erg[2]=points[0].x*(points[1].y-p.y)+points[1].x*(p.y-points[0].y)+p.x*(points[0].y-points[1].y);
        erg[2]/=2.*a;
        if (erg[2]<=-epsilon)
            return null;
        
        if(erg[0]<=epsilon)
            erg[0]=0;
        else if(erg[0]>=1-epsilon)
            erg[0]=1;
        
        if(erg[1]<=epsilon)
            erg[1]=0;
        else if(erg[1]>=1-epsilon)
            erg[1]=1;
        
        if(erg[2]<=epsilon)
            erg[2]=0;
        else if(erg[2]>=1-epsilon)
            erg[2]=1;
        
        return erg;
    }
    
    public double getArea(){
        return area;
    }
    public boolean inDelaunayCircle(Point3d p){
        double distance2d=Math.sqrt(Math.pow(VoronoiPoint.x-p.x,2)+Math.pow(VoronoiPoint.y-p.y,2));
        return (distance2d < VoronoiRadius);
    }
    double getVoronoiRadius(){
        return VoronoiRadius;
    }
    
    Point3d getVoronoiPoint(){
        return VoronoiPoint;
    }
    
    /** Elementsize connecting to a vector */
    public double VectorSize(double vx,double vy){
        double dl=0., dx1, dx2, dy1, dy2, fac, xs=0., ys=0.;
        int i1=0, i2=1, i3=2, i=0;
        
        if (Math.sqrt(vx*vx+vy*vy) >= 0.0001){
            do{
                i1=(0+i)%3;
                i2=(1+i)%3;
                i3=(2+i)%3;
                
                dx1 = points[i1].x - points[i3].x;
                dx2 = points[i2].x - points[i1].x;
                dy1 = points[i1].y - points[i3].y;
                dy2 = points[i2].y - points[i1].y;
                
                
                if( (vy*dx2-vx*dy2) != 0.) {
                    fac=(vx*dy1-vy*dx1)/(vy*dx2-vx*dy2);
                    
                    xs = points[i1].x + fac
                            * (points[i2].x - points[i1].x);
                    ys = points[i1].y + fac
                            * (points[i2].y - points[i1].y);
                } else {
                    if((Math.abs(vx)<=0.001) && (Math.abs(dx2)<=0.001)){
                        dl = Math.abs(points[i1].y - points[i2].y );
                    } else {
                        dl = Math.abs(points[i1].x - points[i2].x );
                    }
                }
                
                i++;
            } while( !((xs <= xmax) && (xs >= xmin)  && (ys <= ymax) && (ys >= ymin)  )
            &&  ( i <= 3) && (dl == 0.) );
            
            if (!((xs <= xmax) && (xs >= xmin) && (ys <= ymax) && (ys >= ymin))
            && (dl == 0.))
                System.out.println("error in elemet size computation");
            
            
            if (dl == 0.) {
                xs = xs - points[i3].x;
                ys = ys - points[i3].y;
                dl = Math.sqrt(xs*xs+ys*ys);
            }
        } else {
//          		 xs = xmax - xmin;
//         		 ys = ymax - ymin;
//         		 dl = Math.sqrt(xs*xs+ys*ys)/2.;
            
            dl=points[0].distance(points[1]);
            dl=Math.min(dl,points[1].distance(points[2]));
            dl=Math.min(dl,points[2].distance(points[0]));
        }
        return(dl);
    }
    
    double sign(double a){
        if(a>0.)
            return 1.;
        else if(a<0.)
            return -1.;
        else
            return 0.;
    }
    
}