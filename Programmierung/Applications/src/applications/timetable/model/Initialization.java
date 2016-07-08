/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.individuals.Individual;
import ga.Parameters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import util.RandomUtilities;

/**
 *
 * @author bode
 */
public class Initialization {

    public static ArrayList<TimeTableMatrix> buildInitialPopulation(ProblemDefinition pdef, int numberOfIndividuals) {
        ArrayList<TimeTableMatrix> result = new ArrayList<TimeTableMatrix>();

        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
        for (Grade grade : pdef.getModules().keySet()) {
            ArrayList<Module> modules = pdef.getModules().get(grade);
            for (Module module : modules) {
                lessons.addAll(module.getLessons());
                for (Lesson lesson : lessons) {
                    lesson.addGrade(grade);
                }
            }
        }

        Collections.shuffle(lessons, Parameters.getRandom());
 

        individualLoop:
        for (int i = 0; i < numberOfIndividuals; i++) {
            /**
             * Kopie der freien Räume pro Period, die zum Erzeugen benötigt wird
             */
            HashMap<Period, ArrayList<Room>> periodsAndRooms = new HashMap<Period, ArrayList<Room>>();
            for (Period period : pdef.getFreeRooms().keySet()) {
                ArrayList<Room> rooms = pdef.getFreeRooms().get(period);
                if (!rooms.isEmpty()) {
                    periodsAndRooms.put(period, new ArrayList<Room>(rooms));
                }
            }

            TimeTableMatrix individual = buildValidIndividual(lessons, periodsAndRooms, pdef);
            if (individual == null) {
                i = i - 1;
                continue individualLoop;
            }
            result.add(individual);
        }

        return result;
    }

    public static TimeTableMatrix buildValidIndividual(ArrayList<Lesson> lessons, HashMap<Period, ArrayList<Room>> periodsAndRooms, ProblemDefinition pdef) {
        TimeTableMatrix tm = new TimeTableMatrix(pdef);

        int numberOfFreeRooms = 0;
        for (Period period : periodsAndRooms.keySet()) {
            ArrayList<Room> rooms = periodsAndRooms.get(period);
            if (!rooms.isEmpty()) {
                numberOfFreeRooms += rooms.size();
            }
        }



        ArrayList<Period> freePeriods = new ArrayList<Period>(periodsAndRooms.keySet());
        HashMap<Grade, ArrayList<Period>> freePeriodsPerGrades = new HashMap<Grade, ArrayList<Period>>();
        for (Grade grade : pdef.getModules().keySet()) {
            freePeriodsPerGrades.put(grade, freePeriods);
        }

        if (numberOfFreeRooms < lessons.size()) {
            System.err.println("Zu wenig freie Räume für die angegebenen Lehrveranstaltungen");
            return null;
//            throw new IllegalArgumentException("Zu wenig freie Räume für die angegebenen Lehrveranstaltungen");
        }
//            System.out.println("Anzahl an freien Raumperioden:" + numberOfFreeRooms);
//            System.out.println("Anzahl freier Perioden: " + freePeriods.size());
//            


        /**
         * Unter der Annahme, dass ein Lesson zunächst nur für eine Grade angeboten wird
         */
        int numberOfScheduled = 0;
        for (int j = 0; j < lessons.size(); j++) {
            Lesson lesson = lessons.get(j);
            System.out.println(j);
            Grade g = lesson.getGrades().get(0);
            searchLoop:
            while (true) {
                ArrayList<Period> freePeriodsPGrade = freePeriodsPerGrades.get(g);
                /**
                 * Keine gültige Konstellation mehr möglich
                 */
                if (freePeriodsPGrade.isEmpty()) {
                    return null;
                }

                int randomValue = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, freePeriodsPGrade.size() - 1);
                Period period = freePeriodsPGrade.get(randomValue);
                ArrayList<Room> rooms = periodsAndRooms.get(period);

                int randomValueR = RandomUtilities.getRandomValue(Parameters.getRandom(), 0, rooms.size() - 1);
                if(rooms.isEmpty()){
                    throw new UnknownError("Sollte nicht so sein!");
                }
                Room room = rooms.get(randomValueR);
                /**
                 * Test auf feasibility
                //                     */
                HashSet<Lesson> allLessonsInPeriods = tm.getAllLessonsInPeriodPerClass(g, period);
                if (!allLessonsInPeriods.isEmpty()) {
                    freePeriodsPGrade.remove(period);
                    continue searchLoop;
                }

                /**
                 * Teste auf Kollision bezüglich Dozenten
                 */
                for (Grade grade : pdef.getModules().keySet()) {
                    if (grade.equals(g)) {
                        continue;
                    }

                    for (Lesson otherLesson : lessons) {
                        for (Room r : pdef.getRooms()) {
                            if (tm.get(grade, otherLesson, r, period) == 1) {
                                for (Lecturer lecturer : lesson.getLecturers()) {
                                    if (otherLesson.getLecturers().contains(lecturer)) {
                                        continue searchLoop;
                                    }
                                }
                            }
                        }
                    }
                }


                //if (room.getSeats() < lesson.getNumberOfStudents()) {
                //continue searchLoop;
                //}

                /**
                 * Falls gefunden, füge hinzu und räume auf
                 */
                tm.set(g, lesson, room, period, 1);
                numberOfScheduled++;
                /**
                 * Falls Raumliste leer, streiche Periode
                 */
                rooms.remove(room);
                if (rooms.isEmpty()) {
                    periodsAndRooms.remove(period);
                    freePeriodsPGrade.remove(period);
                }
                break searchLoop;
            }
        }
//            System.out.println("Eingeplante Lessons:" + numberOfScheduled);
        return tm;
    }
}
