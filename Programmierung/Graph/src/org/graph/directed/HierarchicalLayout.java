/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.graph.directed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.graph.algorithms.TopologicalSort;
import org.util.DiscreteSet;

/**
 *
 * @author moehle
 * Quelle: Emden R. Gansner, Eleftherios Koutsofios, Stephen C. North and Kiem-Pong Vo,
 * IEEE Transactions on software engineering, VOL. 19, No. 3, March 1993, S. 214, "A Technique for Drawing Directed Graphs"
 */
public class HierarchicalLayout<V> {
    private DirectedGraph<V> graph;
    private int max_iterations;

    public HierarchicalLayout(DirectedGraph<V> graph) {
        this.graph = graph;
        this.max_iterations = 2;

    }

    public HierarchicalLayout(DirectedGraph<V> graph, int max_iterations) {
        this.graph = graph;
        this.max_iterations = max_iterations;
    }

    public ArrayList<ArrayList<V>> ordering() {
        ArrayList<ArrayList<V>> order = new ArrayList<ArrayList<V>>();
        List<Set<V>> topologicalSort = TopologicalSort.topologicalSort(graph);
        for (int i = 0; i < topologicalSort.size(); i++) {
            Set<V> vertices = topologicalSort.get(i);
            ArrayList<V> rank_as_list = new ArrayList<V>(vertices);
            order.add(rank_as_list);
        }

        int nulls = 1;
        ArrayList<ArrayList<V>> best = copy(order);
        
        for (int i = 0; i < max_iterations; i++) {
            wmedian(order, i);
            transpose(order,nulls);
            if(crossing(order,nulls) < crossing(best,nulls))
                best = copy(order);
        }

// -----------------------------------------------------------------------------
        // maximale Anzahl an Knoten in einer Knotenklasse
        int maxAnz = 0;
        for(int i = 0; i< best.size(); i++){
           if (topologicalSort.get(i).size()>maxAnz)
               maxAnz = topologicalSort.get(i).size();
       }

        // null zwischen alle Knoten adden
        nulls=0;
        for (int klasse = 0; klasse < best.size(); klasse++) {
            int temp=0;
            int klasseSize = best.get(klasse).size();
            if(klasseSize<maxAnz)
                temp = (maxAnz-klasseSize)*2;
            int i = 0;
            for (; i < best.get(klasse).size(); i+=2) {
                best.get(klasse).add(i, null);
            }
            best.get(klasse).add(i, null);
            if(temp!=0){
                int j =i;
                while ( j< i+temp) {
                    best.get(klasse).add(null);
                    j++;
                }
            }
        }

        order=copy(best);
        for (int i = 0; i < max_iterations; i++) {
            transpose(order,nulls);
            if(crossing(order,nulls) < crossing(best,nulls))
                best = copy(order);
        }

        System.out.println("best "+best);
        System.out.println("best crossing "+crossing(best,nulls));
        return best;
    }


    private void wmedian(ArrayList<ArrayList<V>> order, int i) {
        HashMap<V, Integer> median_knoten = new HashMap<V, Integer>();
        if(i % 2 == 0){
            int counter_klasse = 0;
            for (ArrayList<V> klasse : order) {
                for (V knoten : klasse){
                    median_knoten.put(knoten, median_value(knoten, counter_klasse-1, order));
                }
                counter_klasse ++;
            }
        }
    }

    private void transpose(ArrayList<ArrayList<V>> order, int nulls) {
        // Vorsortierung: Knoten mit einem Vorgaenger direkt unter dem Vorgaengerknoten anordnen
        // nur beim ersten Durchlauf- nicht wenn die Nullen eingefuegt wurden
        if (nulls ==1){
        for (int klasse = 0; klasse < order.size(); klasse++) {
                for (int i = 0; i < order.get(klasse).size(); i++) {
                    //nur ein Vorgaenger --> direkt unter Knoten anordnen
                    Set<V> pred = graph.getPredecessors(order.get(klasse).get(i));
                    if(pred.size()==1){
                        for (V vertex : pred) {
                            int k = pos(vertex, order);
                            for (int j = 0; j < order.get(k).size(); j++) {
                                if(vertex.equals(order.get(k).get(j))){
                                    V v = order.get(klasse).get(i);
                                    order.get(klasse).remove(order.get(klasse).get(i));
                                    order.get(klasse).add(j, v);
                                }
                            }
                        }
                    }
                }
            }
        }

        boolean improved = true;
        while (improved){

            improved = false;

            for (int klasse = 0; klasse < order.size(); klasse++) {
                for (int i = 0; i < order.get(klasse).size()-1; i++) {
                    V v = order.get(klasse).get(i);
                    V w = order.get(klasse).get(i+1);
                    int schalter=-1;
                    if(v==null && w==null)
                        continue;
                    else if(v==null)
                        schalter = 0;
                    else if(w==null)
                        schalter = 1;

                    ArrayList<ArrayList<V>> test_order = copy(order);
                    test_order.get(klasse).set(i, w);
                    test_order.get(klasse).set(i+1, v);


                    if (crossing(order,nulls)>crossing(test_order,nulls)){
                        improved = true;
                        order.get(klasse).set(i, test_order.get(klasse).get(i));
                        order.get(klasse).set(i+1, test_order.get(klasse).get(i+1));
                    }

                    ArrayList<ArrayList<V>> test_order2 = copy(order);
                    if (schalter==0){
                        int z=1;
                        while((i-z)>=0 && order.get(klasse).get(i-z)==null){
                            int m;
                            if(improved==true){
                                m=i;
                                test_order2.get(klasse).set(m, null);
                                test_order2.get(klasse).set(i-z, order.get(klasse).get(m));
                            }
                            else{
                                m=i+1;
                                test_order2.get(klasse).set(m, null);
                                test_order2.get(klasse).set(i-z, order.get(klasse).get(m));
                            }

                            if (crossing(order,nulls)>=crossing(test_order2,nulls)){
                                improved = true;
                                order.get(klasse).set(m, null);
                                order.get(klasse).set(i-z, test_order2.get(klasse).get(i-z));
                                test_order2=copy(order);
                            }
                            z++;
                        }
                    }

                    if(schalter==1){ //if w==null
                        int z=2;
                        while((i+z)<order.get(klasse).size() && order.get(klasse).get(i+z)==null){
                            int m;
                            if(improved==true){
                                m=i+1;
                                test_order2.get(klasse).set(m, null);
                                test_order2.get(klasse).set(i+z, order.get(klasse).get(m));
                            } else{
                                m=i;
                                test_order2.get(klasse).set(m, null);
                                test_order2.get(klasse).set(i+z, order.get(klasse).get(m));
                            }

                            if (crossing(order,nulls)>crossing(test_order2,nulls)){
                                improved = true;
                                order.get(klasse).set(m, null);
                                order.get(klasse).set(i+z, test_order2.get(klasse).get(i+z));
                                test_order2=copy(order);
                            }
                            z++;
                        }
                    }
                }
            }  
        }
    }

    private int crossing(ArrayList<ArrayList<V>> order, int nulls) {
        int crossing = 0;
        for (int i = 0; i < order.size(); i++) {
            ArrayList<V> rank = order.get(i);
            for (int j = 0; j < rank.size(); j++) {
                V vertex = rank.get(j);
                if (vertex != null){
                    Set<V> successors = graph.getSuccessors(vertex);
                    for (int k = i; k < order.size(); k++) {
                        ArrayList<V> rank_other = order.get(k);
                        int l;
                        if(k==i && nulls==1)
                            l = j+1;
                        else if(k==i && nulls == 0)
                            l=j;
                        else
                            l = 0;
                        for (; l < rank_other.size(); l++) {
                            V other = rank_other.get(l);
                            if (other != null){

                                Set<V> successors_other = graph.getSuccessors(other);

                                for (V succ : successors) {
                                    for (V succ_other : successors_other) {
                                        int[] vertexPos =  new int []{i,j}; //pos2(vertex, order);
                                        int[] succPos = pos2(succ, order);
                                        int[] otherPos =   new int []{k,l}; //pos2(other, order);
                                        int[] otherSuccPos = pos2(succ_other, order);

                                        if(succPos[0] <= otherPos[0])
                                            continue;
                                        if(vertexPos[1]==succPos[1] && vertexPos[1]==otherPos[1] && otherPos[1] != otherSuccPos[1] || vertexPos[1]==succPos[1] && vertexPos[1]==otherSuccPos[1] && otherPos[1] != otherSuccPos[1])
                                            continue;
                                        if(succ.equals(succ_other))
                                            continue;


                                        if(nulls==0){
                                            if(vertexPos[1]==succPos[1] && vertexPos[1]==otherPos[1] && otherPos[1] == otherSuccPos[1]){
                                                if(vertexPos[0]<otherPos[0] && succPos[0]>otherPos[0] || vertexPos[0]<otherSuccPos[0] && succPos[0]>otherSuccPos[0]){
                                                    crossing++;
//                                                    System.out.println("HIER    (" + vertex + "," + succ + ") --> (" + other + "," + succ_other +")");
                                                    continue;
                                                 }
                                             }

                                            if(vertexPos[1]==succPos[1]){
                                                for (int m = vertexPos[0]+1; m < succPos[0]; m++) {
                                                    if(order.get(m).get(j)!=null){
                                                        crossing++;
//                                                        System.out.println("Ueberschneidung (" + vertex + "," + succ + ") --> (" + other + "," + succ_other +")");
                                                        continue;
                                                    }
                                                }
                                            }

            //                              gleicher Anfangs- oder Endknoten --> gleiche Steigung=crossing
                                            if(vertex.equals(other) || succ.equals(succ_other)){
                                                double mv = ((double)(succPos[0]-vertexPos[0])) / ((double)(succPos[1]-vertexPos[1]));
                                                double mo = ((double) otherSuccPos[0]- (double) otherPos[0]) / ((double) otherSuccPos[1]-(double) otherPos[1]);
                                                if(mv==mo){
                                                    crossing++;
//                                                    System.out.println("same vertex (" + vertex + "," + succ + ") --> (" + other + "," + succ_other +")");
                                                }
                                                continue;
                                            }
                                            if(succPos[0] > otherPos[0]) {
//                                                if((vertexPos[1] <= otherPos[1] && succPos[1] > otherSuccPos[1]) || (vertexPos[1] >= otherPos[1] && succPos[1] < otherSuccPos[1])){
                                                if(schnitt(vertexPos, succPos, otherPos, otherSuccPos)){
                                                   crossing++;
//                                                   System.out.println("(" + vertex + "," + succ + ") --> (" + other + "," + succ_other +")");
                                                }
                                            }

                                        }
                                        if(nulls==1){
                                            if(succPos[0] > otherPos[0]) {
                                                if((vertexPos[1] <= otherPos[1] && succPos[1] > otherSuccPos[1]) || (vertexPos[1] >= otherPos[1] && succPos[1] < otherSuccPos[1])){
                                                    if(schnitt(vertexPos, succPos, otherPos, otherSuccPos)){
                                                       crossing++;
//                                                       System.out.println("(" + vertex + "," + succ + ") --> (" + other + "," + succ_other +")");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return crossing;
    }

 
    private boolean schnitt(int[] vertexPos, int[] succPos, int[] otherPos, int[] otherSuccPos){

//        if((vertexPos[1] <= otherPos[1] && succPos[1] > otherSuccPos[1]) || (vertexPos[1] >= otherPos[1] && succPos[1] < otherSuccPos[1])){
            if(vertexPos[1]== succPos[1]){
//                System.out.println("1");
                double x = vertexPos[1];
                double bo;
                double mo = (double)(otherSuccPos[0] -otherPos[0]) / (double)(otherSuccPos[1] -otherPos[1]);
                if(otherPos[1]>otherSuccPos[1]){
                     bo = otherSuccPos[0]-mo*otherSuccPos[1];
                }else{
                     bo = otherPos[0]-mo*otherPos[1];
                }
                double y = bo + mo * x;

                if (y<=vertexPos[0] && y>=succPos[0] )
                    return true;
                if (y>=vertexPos[0] && y<=succPos[0])
                    return true;
                else
                    return false;
            }
            else if(otherPos[1] == otherSuccPos[1]){
//                System.out.println("2");
                double x = otherPos[1];
                double bv;
                double mv = (double)(succPos[0] -vertexPos[0]) / (double)(succPos[1] -vertexPos[1]);
                if(vertexPos[1]>succPos[1]){
                    bv = succPos[0]-mv*succPos[1];
                }else{
                    bv = vertexPos[0]-mv*vertexPos[1];
                }
                double y = bv + mv * x;
                if (y<=otherPos[0] && y>=otherSuccPos[0])
                    return true;
                if (y>=otherPos[0] && y<=otherSuccPos[0])
                    return true;
                else
                    return false;
            }
            else{
//                System.out.println("3");
                double bv;
                double bo;
                double mv = ((double)(succPos[0]-vertexPos[0])) / ((double)(succPos[1]-vertexPos[1]));
                double mo = ((double) otherSuccPos[0]- (double) otherPos[0]) / ((double) otherSuccPos[1]-(double) otherPos[1]);

                if (vertexPos[1] <succPos[1]){ bv = vertexPos[0]-mv*vertexPos[1];}
                else{bv = succPos[0]-mv*succPos[1];}

                if (otherPos[1] <otherSuccPos[1]){bo = otherPos[0]-mo*otherPos[1];}
                else{ bo = otherSuccPos[0]-mo*otherSuccPos[1];}

                double x = (bo-bv) / (mv-mo);
                double y =  bv+mv*x;

                if (    x>=vertexPos[1] && x<=succPos[1]
                     || x<=vertexPos[1] && x>=succPos[1]){
                    if(    x>=otherPos[1] && x<=otherSuccPos[1]
                        || x<=otherPos[1] && x>=otherSuccPos[1]){
                        if (    y>=vertexPos[0] && y<=succPos[0]
                             || y<=vertexPos[0] && y>=succPos[0]){
                            if(     y>=otherPos[0] && y<=otherSuccPos[0]
                                 || y<=otherPos[0] && y>=otherSuccPos[0]){
                                return true;                                    
                            }
                        }
                    }
                }
            }
//        }
        return false;
    }

    private int pos(V v, ArrayList<ArrayList<V>> order) {
        for (ArrayList<V> rank : order) {
            for (int i = 0; i < rank.size(); i++) {
                if(rank.get(i).equals(v))
                    return i;
            }
        }
        return 0;
    }

    private int[] pos2(V v, ArrayList<ArrayList<V>> order) {
        for (int i = 0; i < order.size(); i++) {
            ArrayList<V> rank = order.get(i);
            for (int j = 0; j < rank.size(); j++) {
                if(v!=null && rank.get(j)!=null && rank.get(j).equals(v))
                    return new int[]{i,j};
            }
        }
        throw new UnsupportedOperationException("This should never happen!");
    }

    private ArrayList<ArrayList<V>> copy(ArrayList<ArrayList<V>> order) {
        ArrayList<ArrayList<V>> best = new ArrayList<ArrayList<V>>();
        for (ArrayList<V> arrayList : order) {
            ArrayList<V> best_each_rank = new ArrayList<V>();
            for (V vertex : arrayList) {
                best_each_rank.add(vertex);
            }
            best.add(best_each_rank);
        }
        return best;
    }

    private int median_value(V vertex, int adj_rank, ArrayList<ArrayList<V>> order){

        if(adj_rank < 0)
            return -1;

        ArrayList<Integer> p = new ArrayList<Integer>();
        DiscreteSet<V> pre = new DiscreteSet<V>(graph.getPredecessors(vertex));
        ArrayList<V> order_pre_klasse = order.get(adj_rank);

        // geordnete Liste mit allen Vorgaengern erstellen
        for (int i = 0; i < order_pre_klasse.size(); i++) {
            V v = order_pre_klasse.get(i);
            if(pre.contains(v))
                p.add(i);
        }
       
        int m = p.size() / 2;
        if (p.isEmpty())
            return -1;
        else if(p.size() % 2 == 1)
            return p.get(m);
        else if(p.size() == 2)
            return (p.get(0)+p.get(1))/2;
        else{
            int left = p.get(m-1)-p.get(0);
            int right = p.get(p.size()-1)-p.get(m);
            return (p.get(m-1)*right + p.get(m)+left) / (left+right);
        }
    }
    
    
    public static void main(String[] args) {
        
        DefaultDirectedGraph<String> graph = new DefaultDirectedGraph<String>();

        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");
        graph.addVertex("f");
        graph.addVertex("g");
        graph.addVertex("h");
        graph.addVertex("i");
        //------------------
        graph.addVertex("j");
        graph.addVertex("k");
        graph.addVertex("l");
        graph.addVertex("m");
        graph.addVertex("n");
        graph.addVertex("o");
        graph.addVertex("p");


        graph.addEdge("b", "a");
        graph.addEdge("b", "d");
        graph.addEdge("b", "e");
        graph.addEdge("b", "c");
        graph.addEdge("c", "e");
        graph.addEdge("d", "a");
        graph.addEdge("d", "e");
        graph.addEdge("d", "g");
        graph.addEdge("e", "g");
        graph.addEdge("e", "h");
        graph.addEdge("f", "c");
        graph.addEdge("f", "e");
        graph.addEdge("f", "h");
        graph.addEdge("f", "i");
        graph.addEdge("g", "h");
        graph.addEdge("i", "h");
        //----------------------
        graph.addEdge("i", "j");
        graph.addEdge("d", "l");
        graph.addEdge("l", "g");
        graph.addEdge("b", "k");
        graph.addEdge("h", "j");
        graph.addEdge("g", "j");
        graph.addEdge("j", "n");
        graph.addEdge("a", "j");
        graph.addEdge("a", "p");
        graph.addEdge("a", "m");
        graph.addEdge("m", "p");
        graph.addEdge("m", "n");
        graph.addEdge("k", "m");
        graph.addEdge("k", "n");
        graph.addEdge("k", "o");
        graph.addEdge("n", "o");

        long start = System.currentTimeMillis();
        org.util.ExportToGraphML.exportToGraphML(graph, "C:\\Users\\Bode\\Desktop\\test.graphml");
//        org.util.ExportToYED.exportToGraphMLTopological(graph, "/home/moehle/Desktop/test.graphml");
        System.out.println("Zeit in ms "+ (System.currentTimeMillis()-start));
    }
}
