/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.test;

/**
 *
 * @author bode
 */
public class ProblemBuilder {
//
//    private HashMap<Grade, ArrayList<Module>> modules = new HashMap<Grade, ArrayList<Module>>();
//    private HashMap<Period, ArrayList<Room>> freeRooms = new HashMap<Period, ArrayList<Room>>();
//    private ArrayList<Lecturer> lecturer = new ArrayList<Lecturer>();
//    private HashSet<TimeTableMatrix> initialPopulation = new HashSet<TimeTableMatrix>();
//
//    public static void main(String[] args) {
//        ProblemBuilder c = new ProblemBuilder();
//        ProblemDimensionsOfFreedom buildProblem = c.buildProblem();
//        XMLImport.exportXML(new File("/home/bode/Desktop/pdof.xml"), buildProblem, false);
//    }
//
//    public ProblemDimensionsOfFreedom buildProblem() {
//        /**
//         * Periods
//         */
//        ArrayList<String> dayOfWeek = new ArrayList<String>();
//        dayOfWeek.add("Montag");
//        dayOfWeek.add("Dienstag");
//        dayOfWeek.add("Mittwoch");
//        dayOfWeek.add("Donnerstag");
//        dayOfWeek.add("Freitag");
//        ArrayList<Period> periods = new ArrayList<Period>();
//        GregorianCalendar cal = new GregorianCalendar(2012, 1, 13, 8, 0);
//        long dauerPeriod = 90 * 60 * 1000l;
//        long currentStart = cal.getTimeInMillis();
//
//        for (int i = 0; i < 5; i++) {
//            for (int j = 0; j < 6; j++) {
//                TimeSlot ts = new TimeSlot(currentStart, currentStart + dauerPeriod);
//                currentStart = currentStart + dauerPeriod + 15 * 60 * 1000;
//                if (i != 2) {
//                    Period period = new Period(ts, dayOfWeek.get(i) + "/" + (j + 1));
//                    periods.add(period);
//                    System.out.println(period);
//                } else {
//                    if (j <= 2) {
//                        Period period = new Period(ts, dayOfWeek.get(i) + "/" + (j + 1));
//                        periods.add(period);
//                        System.out.println(period);
//                    }
//                }
//                if (j == 2) {
//                    currentStart += 45 * 60 * 1000;
//                }
//
//            }
//            currentStart += 45 * 60 * 1000 + 12 * 60 * 60 * 1000;
//        }
//
//
//        /*0*/ lecturer.add(new Lecturer("Laß"));
//        /*1*/ lecturer.add(new Lecturer("Haberland"));
//        /*2*/ lecturer.add(new Lecturer("Riemeier"));
//        /*3*/ lecturer.add(new Lecturer("Neuweiler"));
//        /*4*/ lecturer.add(new Lecturer("Schlurmann"));
//        /*5*/ lecturer.add(new Lecturer("Schütz"));
//        /*6*/ lecturer.add(new Lecturer("Verworn"));
//        /*7*/ lecturer.add(new Lecturer("Schaumann"));
//        /*8*/ lecturer.add(new Lecturer("von der Haar"));
//        /*9*/ lecturer.add(new Lecturer("Rotert"));
//        /*10*/ lecturer.add(new Lecturer("Siefer"));
//        /*11*/ lecturer.add(new Lecturer("Kollenberg"));
//        /*12*/ lecturer.add(new Lecturer("Iwan"));
//        /*13*/ lecturer.add(new Lecturer("Ehlers"));
//        /*14*/ lecturer.add(new Lecturer("Thieken"));
//        /*15*/ lecturer.add(new Lecturer("Rolfes"));
//        /*16*/ lecturer.add(new Lecturer("Grießmann"));
//        /*17*/ lecturer.add(new Lecturer("Staudemeister"));
//        /*18*/ lecturer.add(new Lecturer("Rinke"));
//        /*19*/ lecturer.add(new Lecturer("Bode"));
//        /*20*/ lecturer.add(new Lecturer("Achmus"));
//        /*21*/ lecturer.add(new Lecturer("Pardev"));
//        /*22*/ lecturer.add(new Lecturer("Hansen"));
//        /*23*/ lecturer.add(new Lecturer("Schmidt"));
//        /*24*/ lecturer.add(new Lecturer("Berg"));
//        /*25*/ lecturer.add(new Lecturer("Gottschalk"));
//
//
//        /**
//         * 6.Semester BAUING
//         */
//        ArrayList<Module> sem6 = new ArrayList<Module>();
//        Grade g6 = new Grade("6.Semster Bauing", 160);
//        sem6.add(new Module("Stahlbau", lecturer.get(7), g6.getAmountOfStudents()));
//        sem6.add(new Module("Tragwerksdynamik", lecturer.get(15), lecturer.get(16), g6.getAmountOfStudents()));
//        sem6.add(new Module("Umweltdatenanalyse", lecturer.get(1), g6.getAmountOfStudents()));
//        sem6.add(new Module("Unterirdisches Bauen", lecturer.get(17), g6.getAmountOfStudents()));
//        sem6.add(new Module("GuN", lecturer.get(18), lecturer.get(19), lecturer.get(19), g6.getAmountOfStudents()));
//        sem6.add(new Module("Erd + Grund", lecturer.get(20), lecturer.get(14), lecturer.get(14), g6.getAmountOfStudents()));
//        sem6.add(new Module("Stahlbau", lecturer.get(7), lecturer.get(25), lecturer.get(25), g6.getAmountOfStudents()));
//        sem6.add(new Module("Flächentragwerke", lecturer.get(15), g6.getAmountOfStudents()));
//        sem6.add(new Module("Bau+Sicherheit", lecturer.get(12), lecturer.get(21), g6.getAmountOfStudents()));
//        sem6.add(new Module("Massivbau", lecturer.get(22), lecturer.get(23), lecturer.get(23), g6.getAmountOfStudents()));
//        modules.put(g6, sem6);
//
//        /**
//         * 4.Semester BAUING
//         */
//        ArrayList<Module> sem4 = new ArrayList<Module>();
//        Grade g4 = new Grade("4.Semster Bauing", 250);
//        sem4.add(new Module("Projektgruppen", lecturer.get(0), lecturer.get(0), g4.getAmountOfStudents()));
//        sem4.add(new Module("Hydro + WaWi", lecturer.get(1), lecturer.get(2), g4.getAmountOfStudents()));
//        Module hydro = new Module("Strömungen in Hydro", lecturer.get(3), lecturer.get(4), lecturer.get(5), g4.getAmountOfStudents());
//        hydro.addLesson(new Lesson("Strömungen in Hydro - Tut2", lecturer.get(6)));
//        hydro.addLesson(new Lesson("Strömungen in Hydro - Tut3", lecturer.get(6)));
//        hydro.addLesson(new Lesson("Strömungen in Hydro - Tut4", lecturer.get(6)));
//        sem4.add(hydro);
//        sem4.add(new Module("stat. unb. Tragwerke", lecturer.get(9), lecturer.get(9), lecturer.get(4), g4.getAmountOfStudents()));
//        sem4.add(new Module("KIB", lecturer.get(7), g4.getAmountOfStudents()));
//        sem4.add(new Module("Eisenbahn", lecturer.get(10), lecturer.get(11), g4.getAmountOfStudents()));
//        sem4.add(new Module("Preisgestaltung", lecturer.get(12), lecturer.get(13), g4.getAmountOfStudents()));
//        modules.put(g4, sem4);
//
//        ArrayList<Lesson> lessons = new ArrayList<Lesson>();
//        for (Grade grade : modules.keySet()) {
//            ArrayList<Module> ms = modules.get(grade);
//            for (Module module : ms) {
//                lessons.addAll(module.getLessons());
//            }
//        }
//        ArrayList<Grade> grades = new ArrayList<Grade>(modules.keySet());
//        ArrayList<Room> rooms = new ArrayList<Room>();
//        rooms.add(new Room("3407-016", 100));
//        rooms.add(new Room("3408-220 MZ2", 100));
//        rooms.add(new Room("3408-523 MZ2", 100));
//        rooms.add(new Room("3416-001 HBA", 100));
//        rooms.add(new Room("3101-A104", 100));
//        rooms.add(new Room("1101-B306", 100));
//        
//       
//        
//        ProblemDimensionsOfFreedom p = new ProblemDimensionsOfFreedom( grades, lessons, rooms, periods, lecturer);
//        return p;
//    }
//
//    public HashSet<TimeTableMatrix> getInitialPopulation() {
//        return initialPopulation;
//    }
//
//    public HashMap<Grade, ArrayList<Module>> getModules() {
//        return modules;
//    }
//    
//    
    
    
}
