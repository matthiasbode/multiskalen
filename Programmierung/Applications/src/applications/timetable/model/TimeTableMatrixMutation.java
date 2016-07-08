/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import ga.individuals.Individual;
import ga.Parameters;
import ga.mutation.Mutation;
import applications.timetable.model.TimeTableMatrix.PeriodAndRoom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import util.RandomUtilities;

/**
 *
 * @author Matthias
 */
public class TimeTableMatrixMutation implements Mutation<TimeTableMatrix> {

    private int mmin;
    private int mmax;

    public TimeTableMatrixMutation(int mmin, int mmax) {
        this.mmin = mmin;
        this.mmax = mmax;
    }

    @Override
    public TimeTableMatrix mutate(TimeTableMatrix ind, double xMutationRate) {
        TimeTableMatrix c = ind;
        if (Parameters.getRandom().nextDouble() > xMutationRate) {
            return c;
        }
        /**
         * Wähle zufällige Anzahl an Perioden. Die in diesen Perioden
         * gescheduleden Lessons können permutiert werden über die
         * Intialisierungsroutine. Der Rest wird einfach übernommen
         */
        searchLoop:
        while (true) {
            HashSet<Period> periods = new HashSet<Period>();
            int numberOfPeriods = RandomUtilities.getRandomValue(Parameters.getRandom(), mmin, mmax);
            while (periods.size() < numberOfPeriods) {
                Period period = c.getProblem().getPeriods().get(RandomUtilities.getRandomValue(Parameters.getRandom(), 0, c.getProblem().getPeriods().size() - 1));
                periods.add(period);
            }

            HashMap<Lesson, Period> allLessonsInPeriods = c.getAllLessonsInPeriods(periods);
            HashMap<Period, ArrayList<Room>> periodsAndRooms = new HashMap<Period, ArrayList<Room>>();
            for (Period period : periods) {
                ArrayList<Room> rooms = c.getProblem().getFreeRooms().get(period);
                if (rooms.isEmpty()) {
                    continue searchLoop;
                }
                periodsAndRooms.put(period, rooms);
            }

            TimeTableMatrix individual = Initialization.buildValidIndividual(new ArrayList<Lesson>(allLessonsInPeriods.keySet()), periodsAndRooms, c.getProblem());
            if (individual == null) {
                continue searchLoop;
            }
            ArrayList<Lesson> leftOverLessons = new ArrayList<Lesson>(c.getProblem().getLessons());
            leftOverLessons.removeAll(allLessonsInPeriods.keySet());
            for (Lesson lesson : leftOverLessons) {
                PeriodAndRoom periodAndRoomForLesson = c.getPeriodAndRoomForLesson(lesson);
                if (periodAndRoomForLesson != null) {
                    individual.set(periodAndRoomForLesson.grade, lesson, periodAndRoomForLesson.r, periodAndRoomForLesson.p, 1);
                }
            }
            return individual;
        }

    }

}
