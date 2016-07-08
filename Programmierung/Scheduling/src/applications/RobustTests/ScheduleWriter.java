/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.RobustTests;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.schedule.Schedule;
import fuzzy.number.discrete.interval.FuzzyInterval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import math.FieldElement;

/**
 *
 * @author bode
 */
public class ScheduleWriter {
    
    public void analysis(Schedule s, SchedulingProblem problem, File f) {
        
        try {
            
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("#########################");
            bw.newLine();
            bw.write("Schedule");
            bw.newLine();
            bw.write("#########################");
            bw.newLine();
            bw.newLine();
            bw.newLine();
            List<Resource> resources = new ArrayList<>(s.getResources());
            Collections.sort(resources, new Comparator<Resource>() {
                @Override
                public int compare(Resource o1, Resource o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            
            for (Resource resource : resources) {
                bw.write(resource.toString());
                bw.newLine();
                bw.write("----------------------------------");
                bw.newLine();
                
                for (Operation operation : s.getOperationsForResource(resource)) {
                    FieldElement start = s.getStartTimes().get(operation);
                    
                    bw.write(TimeSlot.longToFormattedDateString(start.longValue()).toString());
                    bw.write(" - ");
                    bw.write(TimeSlot.longToFormattedDateString(start.add(operation.getDuration()).longValue()).toString());
                    
                    bw.write("\t :");
                    bw.write(operation.toString());
                    
                    if (start instanceof FuzzyInterval) {
                        bw.newLine();
                        FuzzyInterval fuzStart = (FuzzyInterval) start;
                        FuzzyInterval fuzEnd = (FuzzyInterval) start.add(operation.getDuration());
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzStart.getC1()).toString());
                        bw.write(" - ");
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzEnd.getC1()).toString());
                        bw.newLine();
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzStart.getC2()).toString());
                        bw.write(" - ");
                        bw.write(TimeSlot.longToFormattedDateString((long) fuzEnd.getC2()).toString());
                        bw.newLine();
                        bw.write(s.fuzzyWorkloadParameters.get(operation).overlapCase.toString());
                        bw.write("Support: " + (fuzStart.getC2()- fuzStart.getC1()));
                        
                    }
                    
                    bw.newLine();
                }
                bw.newLine();
                bw.newLine();
                bw.write("####################################");
                bw.newLine();
                
            }
            
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.newLine();
            bw.write("####################################");
            bw.newLine();
            
            bw.close();
            fw.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        
    }
}
