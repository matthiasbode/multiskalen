/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.modes;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.operations.Operation;
import java.util.ArrayList;

/**
 * Ein Routing beschreibt eine Operationensequenz zur Abarbeitung eines Jobs.
 *
 * @author bode
 */
public class JobOperationList<E extends JobOperation> extends ArrayList<E> implements Comparable<JobOperationList> {

    private double weight;
    private final int number;
    private static int counter = 0;
    private MultiModeJob job;

    public JobOperationList(MultiModeJob job) {
        this.number = counter++;
        this.job = job;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(JobOperationList o) {
        int compare = Double.compare(this.weight, o.weight);
        if (compare == 0) {
            return Integer.compare(this.number, o.number);
        } else {
            return compare;
        }
    }

    public MultiModeJob getJob() {
        return job;
    }

    public void setJob(MultiModeJob job) {
        this.job = job;
    }
    

    @Override
    public String toString() {
        return "List: " +this.weight + ": "+super.toString() ;
    }

    public boolean isFirst(Operation o) {
        return this.indexOf(o) == 0;
    }

    public boolean isLast(Operation o) {
        return this.indexOf(o) == this.size() - 1;
    }
    
    public E getFirst(){
        return this.get(0);
    }
    
    public E getLast(){
        return this.get(size()-1);
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e);
        if(add){
            e.setRouting(this);
        }
        return add;  
    }
    
    public boolean isDirectTransport(){
        return this.size() == 1;
    }
    
}
