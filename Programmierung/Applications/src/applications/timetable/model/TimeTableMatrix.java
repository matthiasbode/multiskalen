/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.individuals.Individual;
import ga.individuals.Individual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.collections15.map.MultiKeyMap;

/**
 * Map aus Klasse, Lesson, Room und Periode
 *
 * @author bode
 */
public class TimeTableMatrix extends Individual<Integer> {

    private MultiKeyMap map = new MultiKeyMap();
    private static int counter = 0;
    private int index;
    @XmlTransient
    private ProblemDefinition p;

    public TimeTableMatrix() {
        super();
    }

    public TimeTableMatrix(ProblemDefinition p) {
        super();
        this.p = p;
        index = counter++;
    }

    public int get(Grade key1, Lesson key2, Room key3, Period key4) {
        Object get = map.get(key1, key2, key3, key4);
        if (get == null) {
            return 0;
        }
        return (Integer) get;
    }

    public void set(Grade key1, Lesson key2, Room key3, Period key4, Integer value) {
        map.put(key1, key2, key3, key4, value);
    }

    public TimeTable getTimeTable() {
        TimeTable timetable = new TimeTable(TimeTableElement.class, p.getModules().size(), p.getPeriods().size());
        for (Grade class1 : p.getModules().keySet()) {
            for (Lesson lesson : p.getLessons()) {
                for (Room room : p.getRooms()) {
                    for (Period period : p.getPeriods()) {
                        if (this.get(class1, lesson, room, period) == 1) {
                            //                            timetable.set(new TimeTableElement(lesson, room), p.getClasses().indexOf(class1), p.getPeriods().indexOf(period));
                            TimeTableElement currentElement = timetable.get(class1, period);
                            if (currentElement == null) {
                                timetable.set(new TimeTableElement(lesson, room), class1.getIndex(), period.getIndex());
                            } else {
                                currentElement.lessons.put(lesson, room);
                            }
                        }
                    }
                }
            }
        }
        return timetable;
    }

    public void printTimeTable() {
        TimeTable timeTable = getTimeTable();
        for (Grade class1 : p.getModules().keySet()) {
            System.out.println("Class:" + class1);
            for (Period period : p.getPeriods()) {
                TimeTableElement ttElem = timeTable.get(class1, period);
                System.out.println(ttElem);
            }

        }
    }

    public PeriodAndRoom getPeriodAndRoomForLesson(Lesson l) {
        for (Grade grade : p.getModules().keySet()) {
            for (Room room : p.getRooms()) {
                for (Period period : p.getPeriods()) {
                    if (get(grade, l, room, period) == 1) {
                        return new PeriodAndRoom(period, room, grade);
                    }
                }
            }
        }
        return null;
    }

    public void set(int i, Integer t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer get(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int size() {
        return map.size();
    }

    @Override
    public List<Integer> getList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class PeriodAndRoom {

        public Period p;
        public Room r;
        public Grade grade;

        public PeriodAndRoom(Period p, Room r, Grade g) {
            this.p = p;
            this.r = r;
            this.grade = g;
        }
    }

    public static class TimeTable extends MatrixCoding<TimeTableElement> {

        public TimeTable(java.lang.Class<TimeTableElement> cls, int numberOfClasses, int numberOfPeriods) {
            super(cls, new int[]{numberOfClasses, numberOfPeriods});
        }

        public TimeTableElement get(Grade cls, Period p) {
            return super.get(cls.getIndex(), p.getIndex());
        }

        public int getNumberOfClasses() {
            return super.getDimensions()[0];
        }

        public int getNumberOfPeriods() {
            return super.getDimensions()[1];
        }
    }

    public static class TimeTableElement {

        HashMap<Lesson, Room> lessons;

        public TimeTableElement(HashMap<Lesson, Room> lessons) {
            this.lessons = lessons;
        }

        private TimeTableElement(Lesson lesson, Room room) {
            lessons = new HashMap<Lesson, Room>();
            lessons.put(lesson, room);
        }

        @Override
        public String toString() {
            String res = "<html>";
            int count = 0;
            for (Lesson lesson : lessons.keySet()) {
                res += lesson.getName() + "(" + lessons.get(lesson).getName() + ")";
                if (count < lessons.size() - 1) {
                    res += "<br>";
                }
                count++;
            }
            res += "</html>";
            return res;
        }
    }

//    public int get(Grade cls, Lesson l, Room r, Period p) {
////        return matrix.get(classes.indexOf(cls), lessons.indexOf(l), rooms.indexOf(r), periods.indexOf(p));
//        return super.get(cls.getIndex(), l.getIndex(), r.getIndex(), p.getIndex());
//    }
//
//    public void set(Grade cls, Lesson l, Room r, Period p, int value) {
//        super.set(value, cls.getIndex(), l.getIndex(), r.getIndex(), p.getIndex());
//    }
    public HashMap<Lesson, Period> getAllLessonsInPeriods(Collection<Period> pi) {
        HashMap<Lesson, Period> lessonsInPi = new HashMap<Lesson, Period>();
        for (Grade cls : p.getModules().keySet()) {
            for (Lesson lesson : p.getLessons()) {
                for (Room room : p.getRooms()) {
                    for (Period period : pi) {
                        if (get(cls, lesson, room, period) == 1) {
                            lessonsInPi.put(lesson, period);
                        }
                    }
                }
            }
        }
        return lessonsInPi;
    }

    public HashSet<Lesson> getAllLessonsInPeriodPerClass(Grade g, Period pe) {
        HashSet<Lesson> lessonsInP = new HashSet<Lesson>();
        for (Lesson lesson : p.getLessons()) {
            for (Room room : p.getRooms()) {
                if (get(g, lesson, room, pe) == 1) {
                    lessonsInP.add(lesson);
                }
            }
        }
        return lessonsInP;
    }

    @Override
    public String toString() {
        return "TimeTableMatrix{" + "index=" + index + '}';
    }

    public ProblemDefinition getProblem() {
        return p;
    }

    public static LinkedHashSet<Period> getCycle(TimeTableMatrix f, TimeTableMatrix g, Grade cls, Period currentPeriod) {
        LinkedHashSet<Period> result = new LinkedHashSet<Period>();
        /**
         * Bestimme zufällig Klasse und Periode
         */
        /**
         * Solange die aktuelle Periode noch nicht Teil des Zyklus ist
         */
        cycleLoop:
        while (!result.contains(currentPeriod)) {
            result.add(currentPeriod);

            Lesson lessonInG = null;
            secondLoop:
            for (Lesson lesson : g.getProblem().getLessons()) {
                for (Room room : g.getProblem().getRooms()) {
                    int scheduled = g.get(cls, lesson, room, currentPeriod);
                    if (scheduled == 1) {
                        lessonInG = lesson;
                        break secondLoop;
                    }
                }
            }

            /**
             * Falls leere Periode gefunden, suche in second auch leere Periode
             * --> Für eine Periode alle Lessons == 0
             */
            if (lessonInG == null) {
                ArrayList<Period> schuffeldPeriods = new ArrayList<Period>(g.getProblem().getPeriods());
                Collections.shuffle(schuffeldPeriods, Parameters.getRandom());
                periodLoop:
                for (Period period : schuffeldPeriods) {
                    for (Lesson lesson : f.getProblem().getLessons()) {
                        for (Room room : f.getProblem().getRooms()) {
                            if (f.get(cls, lesson, room, period) == 1) {
                                continue periodLoop;
                            }
                        }
                    }
                    boolean scheduledInG = false;
                    floop:
                    for (Lesson lesson : g.getProblem().getLessons()) {
                        for (Room room : g.getProblem().getRooms()) {
                            if (g.get(cls, lesson, room, period) == 1) {
                                scheduledInG = true;
                                break floop;
                            }
                        }
                    }
                    if (!scheduledInG) {
                        continue periodLoop;
                    }
                    currentPeriod = period;
                    continue cycleLoop;
                }
            }

            /**
             * Suche Periode in first, in der die Lesson von g gescheduled ist
             */
            searchInFLoop:
            for (Room room : f.getProblem().getRooms()) {
                for (Period period : f.getProblem().getPeriods()) {
                    int scheduled = f.get(cls, lessonInG, room, period);
                    if (scheduled == 1) {
                        currentPeriod = period;
                        continue cycleLoop;
                    }
                }
            }

        }

        return result;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
