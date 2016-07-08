/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import org.apache.commons.collections15.map.MultiKeyMap;

 

/**
 *
 * @author bode
 */
public class Test extends MultiKeyMap {

    public int get(Grade key1, Lesson key2, Room key3, Period key4) {
        return (Integer) super.get(key1, key2, key3, key4);
    }

    public void set(Grade key1, Lesson key2, Room key3, Period key4, Integer value) {
        super.put(key1, key2, key3, key4, value);
    }
    
    public static void main(String[] args) {
        Test t = new Test();
        Grade g = new Grade("huhu");
        Lesson l = new Lesson("tut", new Lecturer("tut"));
        Room r = new Room("room", 12);
        Period p = new Period(TimeSlot.nullTimeSlot, "1");
        
        t.set(g,l,r,p,1);
        int get = t.get(g,l,r,p);
        System.out.println(get);
    }
}
