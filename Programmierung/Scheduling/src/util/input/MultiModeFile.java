/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.input;

import applications.mmrcsp.model.MultiModeJob;
import applications.mmrcsp.model.basics.JobOnNodeDiagramm;
import applications.mmrcsp.model.basics.TimeSlot;
import applications.mmrcsp.model.modes.DefaultJobOperation;
import applications.mmrcsp.model.modes.JobOperationList;
import applications.mmrcsp.model.problem.multiMode.MultiModeJobProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.ResourceImplementation;
import applications.mmrcsp.model.restrictions.TimeRestrictions;
import applications.mmrcsp.model.schedule.rules.DefaultScheduleRules;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import math.DoubleValue;
import math.LongValue;
import static util.input.SingleModeFile.start;

/**
 *
 * @author behrensd
 */
public class MultiModeFile {

    private final File file;
    static long start = 61378210800000L;

    public MultiModeFile(String file) {
        if (!file.endsWith(".mm")) {
            throw new IllegalArgumentException("Wrong filetype");
        }
        this.file = new File(file);
    }

    public MultiModeJobProblem getProblemFromFile() {
        try {
            Scanner s = new Scanner(file);
            int jobs = 0;
            int res = 0;
            ArrayList<Resource> resources = new ArrayList<>();
            ArrayList<MultiModeJob> ops = new ArrayList<>();
            ArrayList<Integer> modes = new ArrayList<>();
            HashMap<Resource, Double> capacity = new HashMap<>();
            TimeRestrictions tr = new TimeRestrictions();
            JobOnNodeDiagramm<MultiModeJob<DefaultJobOperation>> joND = new JobOnNodeDiagramm();
            DefaultScheduleRules builder = new DefaultScheduleRules();

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
                resources.add(new ResourceImplementation());
            }
            System.out.println("Counting modes...");
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

                    int mode = Integer.parseInt(split[1]);
                    modes.add(mode);
                }
            }
            System.out.println("Creating operations...");
            if (s.findWithinHorizon("REQUESTS/DURATIONS:", 0) != null) {
                s.nextLine();
                s.nextLine();
                s.nextLine();
                String line = s.nextLine().trim().replaceAll(" +", " ");;
                for (int i = 0; i < jobs; i++) {
                    String[] split = line.split(" ");
                    MultiModeJob<DefaultJobOperation> mmj = new MultiModeJob();
                    JobOperationList<DefaultJobOperation> jol = new JobOperationList<>(mmj);
                    long duration = Long.parseLong(split[split.length - 4]) * 60 * 1000;
                    DefaultJobOperation djo = new DefaultJobOperation(duration);
                    for (int j = 3; j <= 6; j++) {
                        double demand = Double.parseDouble(split[j]);
                        if (demand != 0) {
                            djo.setDemand(resources.get(j - 3), new DoubleValue(demand));
                        }
                    }
                    jol.add(djo);
                    for (int j = 1; j < modes.get(i); j++) {
                        line = s.nextLine().trim().replaceAll(" +", " ");
                        split = line.split(" ");
                        DefaultJobOperation d = new DefaultJobOperation(Long.parseLong(split[split.length - 4]));
                        for (int k = 2; k <= 5; k++) {
                            double demand = Double.parseDouble(split[k]);
                            if (demand != 0) {
                                d.setDemand(resources.get(k - 2), new DoubleValue(demand));
                            }
                        }
                        jol.add(d);
                    }
                    mmj.addRouting(jol);
                    joND.addVertex(mmj);
                    ops.add(mmj);
                    line = s.nextLine().trim().replaceAll(" +", " ");
                }
            }
            System.out.println("Creating time restrictions...");
            s.close();
            s = new Scanner(file);
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

                    MultiModeJob<DefaultJobOperation> m = ops.get(num - 1);
                    // Alle Nachfolger...
                    for (int i = 3; i < 3 + suc; i++) {
                        MultiModeJob successor = ops.get(Integer.parseInt(split[i]) - 1);
                        joND.addEdge(m, successor);
                        //Hier die Restricton(s) erzeugen
                        //tr.put....
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
//             ActivityOnNodeGraph erzeugen
//             System.out.println("Creating graph...");
//             ActivityOnNodeGraph graph = ActivityOnNodeBuilder.build(ops, tr);
            System.out.println("Creating scheduling problem...");
            TimeSlot ts = new TimeSlot(new LongValue(start), new LongValue(start).add(new LongValue(horizon * 60 * 1000)));

            MultiModeJobProblem mmjp = new MultiModeJobProblem(ts, ops, resources, builder, joND);

            System.out.println("----------------------------------------");
            System.out.println("File read successfully...");
            return mmjp;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MultiModeFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static void main(String[] args) {
        String url = "/home/bode/Downloads/c154_3.mm";

        MultiModeFile mmf = new MultiModeFile(url);
        mmf.getProblemFromFile();
    }

}
