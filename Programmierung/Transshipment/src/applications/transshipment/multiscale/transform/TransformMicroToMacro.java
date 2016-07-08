/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.multiscale.transform;

import applications.mmrcsp.model.operations.Operation;
import applications.mmrcsp.model.operations.SubOperation;
import applications.mmrcsp.model.problem.SchedulingProblem;
import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.schedule.Schedule;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.transshipment.model.operations.setup.IdleSettingUpOperation;
import applications.transshipment.model.operations.storage.StoreOperation;
import applications.transshipment.model.operations.storage.SubStoreOperation;
import applications.transshipment.model.operations.storage.SuperStoreOperation;
import applications.transshipment.model.operations.transport.TransportOperation;
import applications.transshipment.model.schedule.LoadUnitJobSchedule;
import applications.transshipment.model.schedule.rules.StorageRule;
import applications.transshipment.model.structs.TrainType;
import java.util.List;

/**
 * Wandelt einen Mikroskopischen Schedule in einen Makroskopischen Schedule um
 *
 * @author bode
 */
public class TransformMicroToMacro implements TransshipmentTransformator {

    /**
     *
     * @param microSchedule MicroSchedule
     * @param macroProblem MacroProblem
     * @return
     */
    @Override
    public LoadUnitJobSchedule transform(Schedule microSchedule, SchedulingProblem macroProblem) {
        if (!(microSchedule instanceof LoadUnitJobSchedule)) {
            throw new IllegalArgumentException("Kann nur auf LoadUnitJobSchedules angewandt werden");
        }
        LoadUnitJobSchedule originalSchedule = (LoadUnitJobSchedule) microSchedule;
        LoadUnitJobSchedule result = new LoadUnitJobSchedule(new InstanceHandler(macroProblem.getScheduleManagerBuilder()));
        result.getDidNotFinish().addAll(originalSchedule.getDidNotFinish());
        result.getDnfJobs().addAll(originalSchedule.getDnfJobs());
        result.getScheduledJobs().addAll(originalSchedule.getScheduledJobs());

        for (Operation operation : originalSchedule.getStartTimes().keySet()) {
            if (operation instanceof TransportOperation || operation instanceof IdleSettingUpOperation) {
                boolean add = true;
                for (Resource requieredResource : operation.getRequieredResources()) {

                    ScheduleRule rule = result.getHandler().get(requieredResource);
                    if (rule == null) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    result.schedule(operation, microSchedule.getStartTimes().get(operation));
                }
            }
            if (operation instanceof StoreOperation) {
                if (operation instanceof SuperStoreOperation) {
                    /**
                     * Nicht Berücksichtigung von Unteroperationen.
                     */

                    SuperStoreOperation superStore = ((SuperStoreOperation) operation).clone();
                    StorageRule rule = (StorageRule) result.getHandler().get(superStore.getResource());
                    List possibleStoreOperations = rule.getPossibleStoreOperations(superStore.getLoadUnit(), result, microSchedule.get(operation), operation.getDuration());
                    if (possibleStoreOperations.isEmpty()) {
                        throw new UnknownError("Keine Einplanoperation gefunden!");
                    }
                    Operation newOp = (Operation) possibleStoreOperations.get(0);
                    result.schedule(newOp, microSchedule.getStartTimes().get(operation));
                } else if (!(operation instanceof SubStoreOperation)) {
                    result.schedule(operation, microSchedule.getStartTimes().get(operation));
                }
            }
        }
        return result;
    }

}
