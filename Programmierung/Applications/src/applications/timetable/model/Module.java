/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.timetable.model;

import java.util.ArrayList;

/**
 *
 * @author bode
 */
public class Module implements Comparable<Module> {

    private String name;
    private ArrayList<Lesson> lessons;
    private int amountOfStudents;
    private ArrayList<Grade> grades = new ArrayList<Grade>();
    public Module() {
    }

    
    public Module(String name, Lecturer prof, int amountOfStudents) {
        this.name = name;
        this.amountOfStudents = amountOfStudents;
        this.lessons = new ArrayList<Lesson>();
        ArrayList<Lecturer> lecturers = new ArrayList<Lecturer>();
        lecturers.add(prof);
        lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        lessons.add(new Lesson(name + "- Übung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
    }
    
    public Module(String name, Lecturer prof, Lecturer wimi, int amountOfStudents) {
        this.name = name;
        this.amountOfStudents = amountOfStudents;
        this.lessons = new ArrayList<Lesson>();
        ArrayList<Lecturer> lecturers = new ArrayList<Lecturer>();
        lecturers.add(prof);
        lecturers.add(wimi);
        lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        lessons.add(new Lesson(name + "- Übung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
    }
    
    public Module(String name, Lecturer prof, Lecturer wimi, Lecturer tutor, int amountOfStudents) {
        this.name = name;
        this.amountOfStudents = amountOfStudents;
        this.lessons = new ArrayList<Lesson>();
        ArrayList<Lecturer> lecturers = new ArrayList<Lecturer>();
        lecturers.add(prof);
        lecturers.add(wimi);
        lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        lessons.add(new Lesson(name + "- Übung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        lessons.add(new Lesson(name + "- Tutorium", new ArrayList<Lecturer>(lecturers),amountOfStudents));
    }
    
    public Module(String name, ArrayList<Lecturer> lecturers, int numberOfLessons, int amountOfStudents) {
        this.name = name;
        this.amountOfStudents = amountOfStudents;
        this.lessons = new ArrayList<Lesson>();
        if(numberOfLessons==1){
        lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        }
        else if(numberOfLessons==2){
        lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        lessons.add(new Lesson(name + "- Übung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
        }
        else{
            lessons.add(new Lesson(name + "-Vorlesung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
            lessons.add(new Lesson(name + "- Übung", new ArrayList<Lecturer>(lecturers),amountOfStudents));
            for (int i = 2; i < numberOfLessons; i++) {
                lessons.add(new Lesson(name + "- Tutorium", new ArrayList<Lecturer>(lecturers),amountOfStudents));
            }
        }
    }

    public Module(String name, ArrayList<Lesson> lessons, int amountOfStudents) {
        this.name = name;
        this.lessons = lessons;
        this.amountOfStudents = amountOfStudents;
    }

    public Module(String name, ArrayList<Lesson> lessons) {
        this.name = name;
        this.lessons = lessons;
    }
    
    public void addGrade(Grade g){
        for (Lesson lesson : lessons) {
            lesson.addGrade(g);
        }
    }
    
    public void addLesson(Lesson l){
        lessons.add(l);
    }

    public int getAmountOfStudents() {
        return amountOfStudents;
    }

    public void setAmountOfStudents(int amountOfStudents) {
        this.amountOfStudents = amountOfStudents;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(ArrayList<Lesson> lessons) {
        this.lessons = lessons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(Module o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }
    
    
    
}
