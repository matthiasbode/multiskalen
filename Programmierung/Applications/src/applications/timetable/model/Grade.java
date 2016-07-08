/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

/**
 *
 * @author bode
 */
public class Grade implements MatrixColumn, Comparable<Grade> {

    private static int counter = 0;
    private int index;
    private String id;
    private int amountOfStudents;

    public Grade() {
    }

    public Grade(String id, int amountOfStudents) {
        this.id = id;
        this.amountOfStudents = amountOfStudents;
        this.index = counter++;
    }

    public Grade(String id) {
        this.id = id;
        this.amountOfStudents = 0;
        this.index = counter++;
    }

    
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAmountOfStudents() {
        return amountOfStudents;
    }

    public void setAmountOfStudents(int amountOfStudents) {
        this.amountOfStudents = amountOfStudents;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    public int compareTo(Grade o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grade other = (Grade) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    
}
