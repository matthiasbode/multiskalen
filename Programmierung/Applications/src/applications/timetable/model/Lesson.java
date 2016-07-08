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
public class Lesson implements MatrixColumn {

    private static int counter = 0;
    private String name;
    private int index;
    private ArrayList<Lecturer> lecturers;
    private int numberOfStudents;
    private ArrayList<Grade> grades = new ArrayList<Grade>();

    public Lesson(String name, ArrayList<Lecturer> lecturers) {
        this.name = name;
        this.lecturers = lecturers;
        this.index = counter++;
    }

    public Lesson(String name, ArrayList<Lecturer> lecturers, int numberOfStudents) {
        this.name = name;
        this.lecturers = lecturers;
        this.numberOfStudents = numberOfStudents;
        this.index = counter++;
    }

    
    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }

    
    public Lesson(String name, Lecturer lecturer) {
        this.lecturers = new ArrayList<Lecturer>();
        this.lecturers.add(lecturer);
        this.name = name;
        this.index = counter++;
    }

    public Lesson() {
        this.index = counter++;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(ArrayList<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addGrade(Grade g){
        if(!this.grades.contains(g))
        this.grades.add(g);
    }
    @Override
    public String toString() {
        return "{" + name + ", index=" + index + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Lesson other = (Lesson) obj;
        if (this.index != other.index) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.index;
        return hash;
    }
    
    
}
