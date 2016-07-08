/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.test;

/**
 *
 * @author bode
 */
public class Test {

//    public static ProblemDimensionsOfFreedom getProblem() {
//        ArrayList<String> dayOfWeek = new ArrayList<String>();
//        dayOfWeek.add("Montag");
//        dayOfWeek.add("Dienstag");
//        dayOfWeek.add("Mittwoch");
//        dayOfWeek.add("Donnerstag");
//        dayOfWeek.add("Freitag");
//
//        ArrayList<Period> periods = new ArrayList<Period>();
//        GregorianCalendar cal = new GregorianCalendar(2012, 1, 13, 8, 0);
//        long dauerPeriod = 90 * 60 * 1000l;
//        long currentStart = cal.getTimeInMillis();
//
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 6; j++) {
//                TimeSlot ts = new TimeSlot(currentStart, currentStart + dauerPeriod);
//                currentStart = currentStart + dauerPeriod + 15 * 60 * 1000;
//                Period period = new Period(ts, dayOfWeek.get(i) + "/" + (j + 1));
//                periods.add(period);
//                System.out.println(period);
//                System.out.println(period.getTimeSlot().getFromWhen());
//                System.out.println(period.getTimeSlot().getUntilWhen());
//                if(j == 2)
//                    currentStart += 45*60*1000;
//            }
//            currentStart += 45*60*1000 + 12*60*60*1000;
//        }
//
//
//        ArrayList<Lecturer> lecturer = new ArrayList<Lecturer>();
//        lecturer.add(new Lecturer("Meier"));
//        lecturer.add(new Lecturer("Müller"));
//        lecturer.add(new Lecturer("Wohlfahrt"));
//        lecturer.add(new Lecturer("Willi"));
//        lecturer.add(new Lecturer("Otto"));
//        lecturer.add(new Lecturer("Thorsten"));
//
//        ArrayList<Grade> classes = new ArrayList<Grade>();
//        Grade cl1 = new Grade("WI1", 250);
//        classes.add(cl1);
//
//        ArrayList<Room> rooms = new ArrayList<Room>();
//        /*0*/ rooms.add(new Room("1102", 500));
//        /*1*/ rooms.add(new Room("n206", 500));
//        /*2*/ rooms.add(new Room("nk01", 500));
//        /*3*/ rooms.add(new Room("g240", 500));
//        /*4*/ rooms.add(new Room("g250", 500));
//        /*5*/ rooms.add(new Room("1101", 500));
//        /*6*/ rooms.add(new Room("g257", 500));
//        /*7*/ rooms.add(new Room("c005", 500));
//        /*8*/ rooms.add(new Room("g149", 500));
//        /*9*/ rooms.add(new Room("a026", 500));
//
//        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
//        /*0*/ lessons.add(new Lesson("hardA", lecturer.get(0)));
//        /*1*/ lessons.add(new Lesson("bwl1A", lecturer.get(1)));
//        /*2*/ lessons.add(new Lesson("stdsA", lecturer.get(3)));
//        /*3*/ lessons.add(new Lesson("bwl1B", lecturer.get(2)));
//        /*4*/ lessons.add(new Lesson("bwl1C", lecturer.get(5)));
//        /*5*/ lessons.add(new Lesson("strpA", lecturer.get(2)));
//        /*6*/ lessons.add(new Lesson("algeA", lecturer.get(1)));
//        /*7*/ lessons.add(new Lesson("hardD", lecturer.get(4)));
//        /*8*/ lessons.add(new Lesson("hardB", lecturer.get(0)));
//        /*9*/ lessons.add(new Lesson("hardC", lecturer.get(3)));
//        /*10*/ lessons.add(new Lesson("anis1C", lecturer.get(2)));
//        /*11*/ lessons.add(new Lesson("strpB", lecturer.get(1)));
//        /*12*/ lessons.add(new Lesson("algeB", lecturer.get(5)));
//        /*13*/ lessons.add(new Lesson("strpC", lecturer.get(3)));
//        /*14*/ lessons.add(new Lesson("strpD", lecturer.get(1)));
//        /*15*/ lessons.add(new Lesson("strpE", lecturer.get(1)));
//        /*16*/ lessons.add(new Lesson("anis1A", lecturer.get(0)));
//        /*17*/ lessons.add(new Lesson("anis1B", lecturer.get(0)));
//
//
//        ProblemDimensionsOfFreedom p = new ProblemDimensionsOfFreedom(classes, lessons, rooms, periods, lecturer);
//        return p;
//    }
//
//    public static TimeTableMatrix getTimeTableMatrix(ProblemDimensionsOfFreedom p) {
//        TimeTableMatrix f = new TimeTableMatrix(p);
//        Grade cl1 = p.getClasses().get(0);
//        ArrayList<Lesson> lessons = p.getLessons();
//        ArrayList<Room> rooms = p.getRooms();
//        ArrayList<Period> periods = p.getPeriods();
//
//        f.set(cl1, lessons.get(0), rooms.get(0), periods.get(2), 1);
//        f.set(cl1, lessons.get(1), rooms.get(1), periods.get(3), 1);
//        f.set(cl1, lessons.get(2), rooms.get(2), periods.get(4), 1);
//        f.set(cl1, lessons.get(3), rooms.get(3), periods.get(6), 1);
//        f.set(cl1, lessons.get(4), rooms.get(3), periods.get(7), 1);
//        f.set(cl1, lessons.get(5), rooms.get(4), periods.get(8), 1);
//        f.set(cl1, lessons.get(6), rooms.get(4), periods.get(12), 1);
//        f.set(cl1, lessons.get(7), rooms.get(5), periods.get(13), 1);
//        f.set(cl1, lessons.get(8), rooms.get(5), periods.get(14), 1);
//        f.set(cl1, lessons.get(9), rooms.get(5), periods.get(15), 1);
//        f.set(cl1, lessons.get(10), rooms.get(6), periods.get(18), 1);
//        f.set(cl1, lessons.get(11), rooms.get(4), periods.get(19), 1);
//        f.set(cl1, lessons.get(12), rooms.get(7), periods.get(20), 1);
//        f.set(cl1, lessons.get(13), rooms.get(8), periods.get(21), 1);
//        f.set(cl1, lessons.get(14), rooms.get(8), periods.get(22), 1);
//        f.set(cl1, lessons.get(15), rooms.get(8), periods.get(23), 1);
//        f.set(cl1, lessons.get(16), rooms.get(9), periods.get(24), 1);
//        f.set(cl1, lessons.get(17), rooms.get(9), periods.get(25), 1);
//
//        TimeTableMatrix g = new TimeTableMatrix(p);
//        g.set(cl1, lessons.get(7), rooms.get(0), periods.get(2), 1);
//        g.set(cl1, lessons.get(1), rooms.get(0), periods.get(4), 1);
//        g.set(cl1, lessons.get(16), rooms.get(0), periods.get(6), 1);
//        g.set(cl1, lessons.get(17), rooms.get(0), periods.get(7), 1);
//        g.set(cl1, lessons.get(5), rooms.get(0), periods.get(8), 1);
//        g.set(cl1, lessons.get(2), rooms.get(0), periods.get(9), 1);
//        g.set(cl1, lessons.get(0), rooms.get(0), periods.get(12), 1);
//        g.set(cl1, lessons.get(8), rooms.get(0), periods.get(13), 1);
//        g.set(cl1, lessons.get(6), rooms.get(0), periods.get(15), 1);
//        g.set(cl1, lessons.get(12), rooms.get(0), periods.get(16), 1);
//        g.set(cl1, lessons.get(10), rooms.get(0), periods.get(18), 1);
//        g.set(cl1, lessons.get(11), rooms.get(0), periods.get(19), 1);
//        g.set(cl1, lessons.get(9), rooms.get(0), periods.get(20), 1);
//        g.set(cl1, lessons.get(13), rooms.get(0), periods.get(21), 1);
//        g.set(cl1, lessons.get(14), rooms.get(0), periods.get(22), 1);
//        g.set(cl1, lessons.get(15), rooms.get(0), periods.get(23), 1);
//        g.set(cl1, lessons.get(3), rooms.get(0), periods.get(25), 1);
//        g.set(cl1, lessons.get(4), rooms.get(0), periods.get(26), 1);
//
//
//
//        Collection<Period> cycle = TimeTableMatrix.getCycle(f, g, cl1, periods.get(25));
//        System.out.println("Zeitplan f:");
//        f.printTimeTable();
//
//
//        System.out.println("Zirkel");
//        for (Period period : cycle) {
//            System.out.println(period);
//        }
//
//        Set<Lesson> allLessonsInPeriodsF = f.getAllLessonsInPeriods(cycle).keySet();
//        System.out.println("Lessons In F");
//        for (Lesson lesson : allLessonsInPeriodsF) {
//            System.out.println(lesson);
//        }
//        Set<Lesson> allLessonsInPeriodsG = g.getAllLessonsInPeriods(cycle).keySet();
//
//        System.out.println("Lessons In G");
//        for (Lesson lesson : allLessonsInPeriodsG) {
//            System.out.println(lesson);
//        }
//        System.out.println(allLessonsInPeriodsF.equals(allLessonsInPeriodsG));
//        return f;
//
//    }
//
//    public static void main(String[] args) {
//        TimeTableMatrix timeTableMatrix = getTimeTableMatrix(getProblem());
//        XMLImport.<TimeTableMatrix>exportXML(new File("/home/bode/Desktop/ttm.xml"), timeTableMatrix, false);
//    }
}
