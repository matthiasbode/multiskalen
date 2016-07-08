/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.Parameters;
import ga.crossover.Crossover;
import applications.timetable.model.TimeTableMatrix.TimeTable;
import applications.timetable.model.TimeTableMatrix.TimeTableElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import util.RandomUtilities;

/**
 *
 * @author Matthias
 */
public class TimeTableMatrixCrossover implements Crossover<TimeTableMatrix> {

    public static void addToMap(HashMap<Grade, ArrayList<Period>> map, Grade class1, Period p) {
        ArrayList<Period> list = map.get(class1);
        if (list == null) {
            list = new ArrayList<Period>();
            map.put(class1, list);
        }
        list.add(p);
    }

    public Collection<TimeTableMatrix> recombine(TimeTableMatrix i1, TimeTableMatrix i2, double xOverRate) {
        TimeTableMatrix c1 = i1;
        TimeTableMatrix c2 = i2;

        if (Parameters.getRandom().nextDouble() > xOverRate) {
            return new ArrayList<>();
        }
        ProblemDefinition p = c1.getProblem();
        TimeTableMatrix result = new TimeTableMatrix(p);
        ArrayList<Grade> classesToCombine = new ArrayList<Grade>(p.getModules().keySet());
        Collections.shuffle(classesToCombine, Parameters.getRandom());

        /**
         * Speichert mögliche Kollisionen, um diese zu vermeiden. Bei einer
         * Kollison mit einer bereits ausgewählten Periode von c1 muss auch bei
         * der nächsten Klasse c1 gewählt werden.
         */
        HashMap<Grade, ArrayList<Period>> periodsWithCollision = new HashMap<Grade, ArrayList<Period>>();



        /**
         * Gehe zufällig über alle Klassen und bestimme deren neuen Stundenplan
         */
        while (!classesToCombine.isEmpty()) {

            /**
             * Auswahl Klasse
             */
            Grade currentClass = classesToCombine.remove(0);

            /**
             * Hole alle Perioden, die durch zuvor bearbeitet Klassen für die
             * folgende Klasse auch aus c1 kommen müssen.
             */
            ArrayList<Period> periodsToBuildCycle = new ArrayList<Period>();

            /**
             * Falls diese Liste leer ist, wähle zufällig
             */
            if (periodsWithCollision.isEmpty()) {
                Period periodToBuildCycle = p.getPeriods().get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, p.getPeriods().size() - 1));
                periodsToBuildCycle.add(periodToBuildCycle);
            } else {
                periodsToBuildCycle.addAll(periodsWithCollision.get(currentClass));
            }

            /*
             * Gehen über alle Perioden, von denen aus ein Zyklus erstellt werden soll
             * Füge den erzeugten Zyklus zu der Menge der Perioden, die von C1 kommen sollen 
             * hinzu
             */
            LinkedHashSet<Period> allPeriodsFromC1 = new LinkedHashSet<Period>();
            for (Period periodToBuildCycle : periodsToBuildCycle) {
                allPeriodsFromC1.addAll(TimeTableMatrix.getCycle(c1, c2, currentClass, periodToBuildCycle));
            }

            System.out.println("Periods im Zyklus für " + currentClass);
            for (Period period : allPeriodsFromC1) {
                System.out.println(period);
            }

            /**
             * Kollisionen mit nachfolgenden Klassen abtesten und hinzufügen
             */
            for (Grade class2 : classesToCombine) {
                if (currentClass.equals(class2)) {
                    continue;
                }
                TimeTable timeTable1 = c1.getTimeTable();
                TimeTable timeTable2 = c2.getTimeTable();

                /**
                 * Finde alle Kollisionen mit anderen Kursen bei den Perioden,
                 * die im aktuellen Zyklus gewählt wurden für currentClass
                 */
                for (Period period : allPeriodsFromC1) {
                    TimeTableElement tte1 = timeTable1.get(currentClass, period);
                    TimeTableElement tte2 = timeTable2.get(class2, period);
                    boolean section = false;
                    if (tte1 == null || tte2 == null) {
                        continue;
                    }
                    if (tte1.lessons == null || tte2.lessons == null) {
                        continue;
                    }
                    collisionLoop:
                    for (Lesson lessontte1 : tte1.lessons.keySet()) {
                        ArrayList<Lecturer> lecturerstte1 = lessontte1.getLecturers();
                        for (Lesson lessontte2 : tte2.lessons.keySet()) {
                            ArrayList<Lecturer> lecturerstte2 = lessontte2.getLecturers();
                            for (Lecturer lecturer : lecturerstte2) {
                                if (lecturerstte1.contains(lecturer)) {
                                    section = true;
                                    break collisionLoop;
                                }
                            }
                        }
                    }

                    if (section) {
                        addToMap(periodsWithCollision, class2, period);
                    }
                }
            }


            /**
             * Plane ein
             */
            for (Lesson lesson : p.getLessons()) {
                for (Room room : p.getRooms()) {
                    for (Period period : p.getPeriods()) {
                        int value = allPeriodsFromC1.contains(period) ? c1.get(currentClass, lesson, room, period) : c2.get(currentClass, lesson, room, period);
                        result.set(currentClass, lesson, room, period, value);
                    }
                }
            }

        }

        ArrayList<TimeTableMatrix> list = new ArrayList<>();
        list.add(result);
        return list;
    }
}
