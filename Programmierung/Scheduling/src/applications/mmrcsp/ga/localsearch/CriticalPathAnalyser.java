/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.ga.localsearch;

import applications.mmrcsp.model.basics.ExtendedActivityOnNodeGraph;
import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.schedule.Schedule;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import math.FieldElement;
import math.LongValue;
import org.util.Pair;

/**
 *
 * @author bode
 */
public class CriticalPathAnalyser {

    public static <O extends Operation> Set<CriticalPath<O>> getCriticalPaths(Schedule schedule, ExtendedActivityOnNodeGraph<O> aon, O first, O last) {
        HashSet<CriticalPath<O>> criticalPaths = new HashSet<>();
        /**
         * Letze Operation des Ablaufplans.
         */

        CriticalPath startPath = new CriticalPath();
        startPath.appendVertex(last);
        criticalPaths.add(startPath);

      

        PathLoop:
        while (true) {
            /**
             * Test, ob alle Pfade zum Anfang gekommen sind.
             */
            boolean toContinue = false;
            for (CriticalPath<O> criticalPath : criticalPaths) {
                if(!criticalPath.getVertexAt(0).equals(first)){
                    toContinue = true;
                    break;
                }
            }
            if(!toContinue){
                break;
            }
            
            HashSet<CriticalPath<O>> currentCriticalPaths = new HashSet<>(criticalPaths);
            criticalPaths = new HashSet<>();
            
            for (CriticalPath<O> criticalPath : currentCriticalPaths) {
                //Erster Knoten
                O current = (O) criticalPath.getVertexAt(0);
                //die Vorgänger von diesem Knoten
                LinkedHashSet<O> predecessors = aon.getPredecessors(current);
                if (predecessors.isEmpty()) {
                    break;
                }
                
                FieldElement latestEndOfPredecessors = new LongValue(Long.MIN_VALUE);

                HashSet<O> latestPredecessors = new HashSet<>();
                
                PredLoop:
                for (O preCan : predecessors) {
                    /**
                     * Es wird der späteste Beendigungszeitpunkt bestimmt.
                     */
                    FieldElement endOfmScaleTop = schedule.get(preCan).add(preCan.getDuration());
                    if (endOfmScaleTop.isGreaterThan(latestEndOfPredecessors) || endOfmScaleTop.equals(latestEndOfPredecessors)) {
                        latestEndOfPredecessors = endOfmScaleTop;
                        latestPredecessors.add(preCan);
                    }
                }
                
                for (O o : latestPredecessors) {
                    CriticalPath<O> path = new CriticalPath<>(criticalPath);
                    path.appendVertexInFront(o);
                    criticalPaths.add(path);
                }
            }
        }

        return criticalPaths;

    }

    public static <O extends Operation> ArrayList<CriticalPath<O>> getCriticalBlocks(CriticalPath<O> criticalPath, ExtendedActivityOnNodeGraph<O> aon) {
        ArrayList<CriticalPath<O>> blocks = new ArrayList<>();
 
        O vertex = criticalPath.getVertexAt(0);
        
        CriticalPath<O> currentPath = new CriticalPath<>();
        currentPath.appendVertex(vertex);

        for (int i = 1; i < criticalPath.getNumberOfVertices(); i++) {
            O nextVertex = criticalPath.getVertexAt(i);
            if(aon.isDisjunctiveConnected(new Pair<O, O>(vertex,nextVertex))){
                currentPath.appendVertex(nextVertex);
            } else {
                blocks.add(currentPath);
                currentPath = new CriticalPath();
                currentPath.appendVertex(nextVertex);
            }
            vertex = nextVertex;
        }
        blocks.add(currentPath);
        return blocks;

    }

    /**
     * Nachbarschaft: Eine Operation innerhalb des Blocks verschieben, bei der
     * sowohl Vorgänger und Nachfolger auch innerhalb des Blocks liegen. Kann
     * ungültig sein: Verschieben zur ersten oder letzten möglichen Positon als
     * Nachbarschaft.
     *
     * Hinweis: Abschätzung, welcher Move gut wäre.
     */
}
