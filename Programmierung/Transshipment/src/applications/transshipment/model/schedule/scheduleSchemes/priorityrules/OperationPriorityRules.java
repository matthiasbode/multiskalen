/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.schedule.scheduleSchemes.priorityrules;

import applications.mmrcsp.model.basics.ActivityOnNodeGraph;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.EarliestFinishTimeRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.EarliestStartTimeRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.GreatestRankPositionalWeight;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LateFinishTimeRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LateStartTimeRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LeastImmediateSuccessorsRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LeastRankPositionalWeight;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LeastVertexClassRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.LongestProcessingTimeRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.MaxSlackRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.MinSlackRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.MostImmediateSuccessorsRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.MostTotalSuccessorsRule;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.OperationRules;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.ShortestProcessingTimeRule;
import applications.mmrcsp.model.restrictions.precedence.EarliestAndLatestStartsAndEnds;
import applications.mmrcsp.model.schedule.Schedule;
import applications.transshipment.model.operations.transport.RoutingTransportOperation;
import applications.transshipment.model.schedule.scheduleSchemes.priorityrules.operations.MinSetupDistance;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bode
 */
public class OperationPriorityRules {

    public static long lengthOfInterval = 10 * 60 * 1000L;
    private final EnumMap<Identifier, OperationRules<RoutingTransportOperation>> map = new EnumMap<Identifier, OperationRules<RoutingTransportOperation>>(Identifier.class) {

        @Override
        public OperationRules<RoutingTransportOperation> get(Object key) {
            OperationRules<RoutingTransportOperation> result = super.get(key);
            if (result == null) {
                throw new NullPointerException("Keine Regel für " + key + " hinterlegt");
            }
            return result;
        }

    };

    public enum Identifier {

        LST,
        MINSLK,
        SPT,
        LPT,
        MIS,
        MTS,
        LVC,
        SETUP;
        //        EST,
//        EFT,
//                LFT,
        //                MAXSLK,
        //                LIS,
        //                GRPW,
//                LRPW,
    }

    public OperationPriorityRules(Schedule s, ActivityOnNodeGraph<RoutingTransportOperation> aon, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes) {

        map.put(Identifier.LST, new LateStartTimeRule());
        map.put(Identifier.MINSLK, new MinSlackRule());
        map.put(Identifier.SPT, new ShortestProcessingTimeRule());
        map.put(Identifier.LPT, new LongestProcessingTimeRule());
        map.put(Identifier.MIS, new MostImmediateSuccessorsRule());
        map.put(Identifier.MTS, new MostTotalSuccessorsRule());
        map.put(Identifier.LVC, new LeastVertexClassRule());
        map.put(Identifier.SETUP, new MinSetupDistance());
//        map.put(Identifier.EST, new EarliestStartTimeRule());
//        map.put(Identifier.EFT, new EarliestFinishTimeRule());
//        map.put(Identifier.LFT, new LateFinishTimeRule());
//        map.put(Identifier.MAXSLK, new MaxSlackRule());
//        map.put(Identifier.LIS, new LeastImmediateSuccessorsRule());
//        map.put(Identifier.GRPW, new GreatestRankPositionalWeight());
//        map.put(Identifier.LRPW, new LeastRankPositionalWeight());
        setAdditionalInformation(s, aon.vertexSet(), ealosaes, aon);
    }

    public void setAdditionalInformation(Schedule schedule, Collection<RoutingTransportOperation> operationsToSchedule, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        for (OperationRules<RoutingTransportOperation> operationRules : map.values()) {
            operationRules.setAdditionalInformation(schedule, operationsToSchedule, ealosaes, graph);
        }
    }

    public OperationComparator getMap(final Identifier r, Schedule schedule, Collection<RoutingTransportOperation> operationsToSchedule, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
        return new OperationComparator(map.get(r), schedule, operationsToSchedule, ealosaes, graph);
    }

    public OperationComparator getMap(final Identifier r) {
        return new OperationComparator(map.get(r), (LateStartTimeRule<RoutingTransportOperation>) map.get(Identifier.LST));
    }

    public static class OperationComparator implements Comparator<RoutingTransportOperation> {

        OperationRules<RoutingTransportOperation> base;
        LateStartTimeRule<RoutingTransportOperation> backup;

        public OperationComparator(OperationRules<RoutingTransportOperation> base, Schedule schedule, Collection<RoutingTransportOperation> operationsToSchedule, Map<RoutingTransportOperation, EarliestAndLatestStartsAndEnds> ealosaes, ActivityOnNodeGraph<RoutingTransportOperation> graph) {
            this.base = base;
            this.base.setAdditionalInformation(schedule, operationsToSchedule, ealosaes, graph);
            this.backup = new LateStartTimeRule<>();
            this.backup.setAdditionalInformation(schedule, operationsToSchedule, ealosaes, graph);
        }

        public OperationComparator(OperationRules<RoutingTransportOperation> base, LateStartTimeRule<RoutingTransportOperation> backup) {
            this.base = base;
            this.backup = backup;
        }

        @Override
        public int compare(RoutingTransportOperation o1, RoutingTransportOperation o2) {
            int compare = base.compare(o1, o2);
            if (compare == 0) {
                //return Integer.compare(o1.getId(), o2.getId());
                return backup.compare(o1, o2);
            }
            return compare;
        }

        @Override
        public String toString() {
            return "{" + "base=" + base.getClass() + '}';
        }
    }

    public Set<Identifier> getIdentifiers() {
        return map.keySet();
    }

}
