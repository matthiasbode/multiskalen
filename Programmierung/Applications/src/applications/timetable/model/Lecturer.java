/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

/**
 *
 * @author bode
 */
public class Lecturer implements Comparable<Lecturer> {

    private String name;

    public Lecturer() {
    }

    
    public Lecturer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int compareTo(Lecturer o) {
        return this.name.compareTo(o.name);
    }
    
    
    
    
    
}
