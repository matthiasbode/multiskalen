package bijava.vecmath;

public class Flip {
    boolean flip;
    
    public Flip(boolean b){
        flip = b;
    }
    
    public void setValue(boolean b){
        flip = b;
    }
    
    public boolean getValue(){
        return flip;
    }
    
    public String toString(){
        return " "+flip+" ";
    }
}

