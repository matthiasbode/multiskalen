/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.db;

/**
 *
 * @author bode
 */
public class TrainFromDB {
    public long desiredArival;
    public long isArival;
    public int nr;

    public TrainFromDB(long desiredArival, long isArival, int nr) {
        this.desiredArival = desiredArival;
        this.isArival = isArival;
        this.nr = nr;
    }

    @Override
    public String toString() {
        return "TrainFromDB{" + "desiredArival=" + desiredArival + ", isArival=" + isArival + ", nr=" + nr + '}';
    }
    
    
    
    
}
