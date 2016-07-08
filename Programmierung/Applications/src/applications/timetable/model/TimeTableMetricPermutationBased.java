/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.individuals.Individual;
import ga.metric.Metric;
import applications.timetable.model.TimeTableMatrix.PeriodAndRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Da die Kandidaten als Matrix Grade x Lesson x Room x Period aufgefasst werden können,
 * und Class und Lesson nur zur Zuweisung wichtig sind, bietet sich folgende Metrik an:
 * 
 * TimeTable A 
 * TimeTable B
 * 
 * Sortiere Räume nach ihrer Größe
 * Sortiere Periode nach ihrem Zeitraum
 * 
 * Suche für Class und Lesson in A Raum und Periode
 * Suche für Class und Lesson in B Raum und Periode
 * 
 * Bestimme die Anzahl der Permutationen als Abstand in den Raum- und PeriodeListen 
 * zwischen Raum und Periode in A und Raum und Periode in B
 * @author bode
 */
public class TimeTableMetricPermutationBased implements Metric<TimeTableMatrix> {

    ProblemDefinition problem;
    ArrayList<Room> r;
    ArrayList<Period> p;

    public TimeTableMetricPermutationBased(ProblemDefinition problem) {
        this.problem = problem;
        p = new ArrayList<Period>(problem.getPeriods());
        Collections.sort(p, new Comparator<Period>() {

            public int compare(Period o1, Period o2) {
                return o1.getTimeSlot().compareTo(o2.getTimeSlot());
            }
        });
        r = new ArrayList<Room>(problem.getRooms());
        Collections.sort(r, new Comparator<Room>() {

            public int compare(Room o1, Room o2) {
                return Integer.compare(o1.getSeats(), o2.getSeats());
            }
        });
    }

    public double distance(TimeTableMatrix a, TimeTableMatrix b) {
        double distance = 0;
        for (Period period : p) {
            for (Grade grade : problem.getModules().keySet()) {
                HashSet<Lesson> allLessonsInPeriodPerClassA = a.getAllLessonsInPeriodPerClass(grade, period);
                HashSet<Lesson> allLessonsInPeriodPerClassB = b.getAllLessonsInPeriodPerClass(grade, period);
                if(!allLessonsInPeriodPerClassA.equals(allLessonsInPeriodPerClassB)){
                    distance++;
                }
            }
        }
        distance /= 2.;
        
        for (Lesson lesson : problem.getLessons()) {
            PeriodAndRoom periodAndRoomForLessonA = a.getPeriodAndRoomForLesson(lesson);
            PeriodAndRoom periodAndRoomForLessonB = b.getPeriodAndRoomForLesson(lesson);
            int indexOfRA = r.indexOf(periodAndRoomForLessonA.r);
            int indexOfRB = r.indexOf(periodAndRoomForLessonB.r);
            distance += Math.abs(indexOfRA - indexOfRB);
        }
        return distance;
    }
}
