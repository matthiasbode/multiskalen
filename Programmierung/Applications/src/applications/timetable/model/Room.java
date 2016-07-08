/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

/**
 *
 * @author bode
 */
public class Room implements MatrixColumn, Comparable<Room> {

    private static int counter = 0;
    private int index;
    private String name;
    private int seats;

    public Room() {
    }

    public Room(String name, int seats) {
        this.name = name;
        this.seats = seats;
        this.index = counter++;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return  name + ", seats=" + seats ;
    }

    public int compareTo(Room o) {
       return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.index;
        return hash;
    }
    
    
}
