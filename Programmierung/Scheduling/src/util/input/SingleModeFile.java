/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.input;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.basics.util.ActivityOnNodeBuilder;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.operations.OperationImplementation;
import applications.mmrcsp.model.problem.timeRestricted.DefaultTimeRestictedSchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.restrictions.ResourceRestrictions;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.schedule.rules.DefaultScheduleRules;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import math.LongValue;
import util.CopyUtils;

/**
 *
 * @author behrensd
 */
public class SingleModeFile {

    static long start = new Date(2015-1900, 0, 1).getTime();
    private final InputStream stream;

    public SingleModeFile(InputStream stream) {
        this.stream = stream;
    }

    public DefaultTimeRestictedSchedulingProblem<Operation> getScheduleFromFile() {
        int jobs = 0;
        int res = 0;
        ArrayList<Resource> resources = new ArrayList<>();
        ArrayList<Operation> ops = new ArrayList<>();
        HashMap<Resource, Double> capacity = new HashMap<>();
        TimeRestrictions tr = new TimeRestrictions();
        DefaultScheduleRules builder = new DefaultScheduleRules();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            CopyUtils.copy(stream, outStream);
        } catch (IOException ex) {
            Logger.getLogger(SingleModeFuzzyFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        InputStream firstStream = new ByteArrayInputStream(outStream.toByteArray());
        InputStream secondStream = new ByteArrayInputStream(outStream.toByteArray());

        Scanner s = new Scanner(firstStream);
        System.out.println("Begin parsing...");
        System.out.println("----------------------------------------");
        System.out.println("Searching for job and resource counts...");

        if (s.findWithinHorizon("jobs \\(incl. supersource/sink \\)", 0) != null) {
            jobs = Integer.parseInt(s.nextLine().split(":")[1].trim());
        }
        int horizon = 0;
        if (s.findWithinHorizon("horizon", 0) != null) {
            horizon = Integer.parseInt(s.nextLine().split(":")[1].trim());
        }

        if (s.findWithinHorizon("renewable", 0) != null) {
            res = Integer.parseInt(s.nextLine().split(":")[1].split("R")[0].trim());
        }
        if (s.findWithinHorizon("nonrenewable", 0) != null) {
            res += Integer.parseInt(s.nextLine().split(":")[1].split("N")[0].trim());
        }
        System.out.println("Project contains " + jobs + " jobs and " + res + " resources");
        System.out.println("Creating resource instances...");
        for (int i = 0; i < res; i++) {
            resources.add(new ResourceImplementation((Integer.toString(i + 1))));
        }
        System.out.println("Creating operations...");
        if (s.findWithinHorizon("REQUESTS/DURATIONS:", 0) != null) {
            s.nextLine();
            s.nextLine();
            s.nextLine();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.contains("*")) {
                    break;
                }
                line = line.trim().replaceAll(" +", " ");
                String[] split = line.split(" ");
                long duration = Long.parseLong(split[2]) * 60 * 1000;
                Operation o = new OperationImplementation(duration, Integer.parseInt(split[0]));
                for (int i = 0; i < res; i++) {
                    double demand = Double.parseDouble(split[i + 3]);
                    if (demand != 0) {
                        o.setDemand(resources.get(i), new DoubleValue(demand));
                    }
                }
                ops.add(o);
            }
        }
        System.out.println("Creating time restrictions...");
        s.close();
        s = new Scanner(secondStream);
        if (s.findWithinHorizon("PRECEDENCE RELATIONS:", 0) != null) {
            s.nextLine();
            s.nextLine();
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.contains("*")) {
                    break;
                }
                line = line.trim().replaceAll(" +", " ");
                String[] split = line.split(" ");

                int num = Integer.parseInt(split[0]);
                int suc = Integer.parseInt(split[2]);

//                    if (suc == 0) {
//                    }
                for (int i = 3; i < 3 + suc; i++) {
                    tr.putMinRestriction(ops.get(num - 1), ops.get(Integer.parseInt(split[i]) - 1), ops.get(num - 1).getDuration());
                }
            }
        }
        if (jobs == ops.size() && res == resources.size()) {
            System.out.println("Found all " + jobs + " jobs and " + res + " resources");
            System.out.println("----------------------------------------");
        } else {
            System.out.println("Missing " + (jobs - ops.size()) + " jobs and " + (res - resources.size()) + " resources...");
            System.out.println("----------------------------------------");
            System.out.println("Aborting...");
            return null;
        }
        System.out.println("Searching for resource capacities...");
        if (s.findWithinHorizon("RESOURCEAVAILABILITIES:", 0) != null) {
            s.nextLine();
            s.nextLine();
            String line = s.nextLine().trim().replaceAll(" +", " ");
            String[] split = line.split(" ");
            for (int i = 0; i < split.length; i++) {
                double cap = Double.parseDouble(split[i]);
                capacity.put(resources.get(i), cap);
            }
        }
        System.out.println("Creating schedule rules...");
        for (Resource r : resources) {
            builder.put(r, capacity.get(r));
        }
        System.out.println("Creating resource restrictions...");
        ResourceRestrictions resourceRestrictions = new ResourceRestrictions(capacity);
        System.out.println("Creating graph...");
        ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);
        System.out.println("Creating scheduling problem...");
        TimeSlot ts = new TimeSlot(new LongValue(start), new LongValue(start).add(new LongValue(horizon * 60 * 1000)));
        DefaultTimeRestictedSchedulingProblem<Operation> d = new DefaultTimeRestictedSchedulingProblem<>(ts, null, ops, resourceRestrictions.getResources(), builder, graph);
        d.setTemporalAvailability(ts);
        System.out.println("----------------------------------------");
        System.out.println("File read successfully...");
        return d;

    }

}
