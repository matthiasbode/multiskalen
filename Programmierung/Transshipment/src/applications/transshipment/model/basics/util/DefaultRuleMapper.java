/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.model.basics.util;

import applications.mmrcsp.model.resources.Resource;
import applications.mmrcsp.model.resources.sharedResources.SharedResource;
import applications.mmrcsp.model.schedule.rules.InstanceHandler;
import applications.mmrcsp.model.schedule.rules.ScheduleRule;
import applications.mmrcsp.model.schedule.rules.ScheduleManagerBuilder;
import applications.transshipment.model.resources.lattice.CellResource2D;
import applications.transshipment.model.resources.lattice.CellResourceRule;
import applications.mmrcsp.model.schedule.rules.SharedResourceManager;
import applications.transshipment.TransshipmentParameter;
import static applications.transshipment.TransshipmentParameter.exactSetupTime;
import applications.transshipment.model.resources.conveyanceSystems.ConveyanceSystem;
import applications.transshipment.model.resources.conveyanceSystems.crane.Crane;
import applications.transshipment.model.resources.conveyanceSystems.crane.CraneRunway;
import applications.transshipment.model.resources.conveyanceSystems.crane.macro.CraneFuzzyCalculator;
import applications.transshipment.model.resources.conveyanceSystems.crane.macro.CraneMacroCalculator;
import applications.transshipment.model.resources.conveyanceSystems.crane.macro.MacroscopicCraneRule;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.MicroCraneRunwaySharedManager;
import applications.transshipment.model.resources.conveyanceSystems.crane.micro.MicroscopicCraneRule;
import applications.transshipment.model.resources.conveyanceSystems.fuzzyBlackBox.SimpleConveyanceSystemRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.Agent;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSHandover;
import applications.transshipment.model.resources.conveyanceSystems.lcs.LCSystem;
import applications.transshipment.model.resources.conveyanceSystems.lcs.macro.MacroscopicLCSystemRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.micro.MicroscopicAgentRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.micro.MicroscopicLCSystemRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.poly.AgentRule;
import applications.transshipment.model.resources.conveyanceSystems.lcs.poly.AreaManager;
import applications.transshipment.model.resources.conveyanceSystems.lcs.poly.PolytopeLCSystemRule;
import applications.transshipment.model.resources.storage.simpleStorage.LocationBasedStorage;
import applications.transshipment.model.resources.storage.simpleStorage.StorageLocation;
import applications.transshipment.model.resources.storage.simpleStorage.rules.MacroscopicLocationBasedStorageRule;
import applications.transshipment.model.resources.storage.simpleStorage.rules.MicroscopicLocationBasedStorageRule;
import applications.transshipment.model.resources.storage.simpleStorage.rules.RackMicroscopicRule;
import applications.transshipment.model.resources.storage.simpleStorage.rules.StorageLocationScheduleRule;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Terminal;

import applications.transshipment.multiscale.model.Scale;

/**
 *
 * @author bode
 */
public class DefaultRuleMapper implements Mapper {

    private ScheduleManagerBuilder scheduleRulesMacro;
    private ScheduleManagerBuilder scheduleRulesMicro;

    public DefaultRuleMapper() {

    }

    public void createMacro() {

        scheduleRulesMacro = new ScheduleManagerBuilder() {
            @Override
            public ScheduleRule build(Resource resource, InstanceHandler handler) {
                ScheduleRule rule = null;

                if (resource instanceof LCSystem) {
                    rule = new MacroscopicLCSystemRule((LCSystem) resource, handler, TransshipmentParameter.fuzzyMode);
                }
                if (resource instanceof Crane) {
                    if (TransshipmentParameter.fuzzyMode.equals(TransshipmentParameter.FuzzyMode.crisp)) {
                        rule = new MacroscopicCraneRule((Crane) resource, handler, false);
                        if (!exactSetupTime) {
                            CraneMacroCalculator cfc = new CraneMacroCalculator();
                            rule = new SimpleConveyanceSystemRule((Crane) resource, handler, cfc, 1.0, TransshipmentParameter.fuzzyMode);
                        }
                    } else {
                        CraneFuzzyCalculator cfc = new CraneFuzzyCalculator();
                        rule = new SimpleConveyanceSystemRule((Crane) resource, handler, cfc, 1.0, TransshipmentParameter.fuzzyMode);
                    }
                }

                if (resource instanceof Slot) {
                    return new StorageLocationScheduleRule((StorageLocation) resource, handler, TransshipmentParameter.fuzzyMode);
                }
                if (resource instanceof StorageLocation) {
                    return new StorageLocationScheduleRule((StorageLocation) resource, handler, TransshipmentParameter.fuzzyMode);
                }
                if (resource instanceof LocationBasedStorage) {
                    rule = new MacroscopicLocationBasedStorageRule((LocationBasedStorage) resource, handler, TransshipmentParameter.fuzzyMode);
                }
                return rule;
            }

            @Override
            public SharedResourceManager build(SharedResource r) {
//                if (r instanceof CraneRunway) {
//                    return new LatticeManager(r);
//                }
                return null;
            }
        };
    }

    private void createMicro() {
        scheduleRulesMicro = new ScheduleManagerBuilder() {
            @Override
            public ScheduleRule build(Resource resource, InstanceHandler handler) {

                if (resource instanceof LCSystem) {
                    if (TransshipmentParameter.USE_POLY_LCS) {
                        return new PolytopeLCSystemRule((LCSystem) resource, handler);
                    } else {
                        return new MicroscopicLCSystemRule((LCSystem) resource, handler);
                    }
                }
                if (resource instanceof Crane) {
                    return new MicroscopicCraneRule((Crane) resource, handler);
                }
                if (resource instanceof CellResource2D) {
                    return new CellResourceRule((CellResource2D) resource, handler);
                }
                if (resource instanceof Agent) {
                    if (TransshipmentParameter.USE_POLY_LCS) {
                        return new AgentRule((Agent) resource, handler);
                    }
                    return new MicroscopicAgentRule((Agent) resource, handler);
                }

                if (resource instanceof LCSHandover) {
                    return new RackMicroscopicRule((LCSHandover) resource, handler);
                }

                if (resource instanceof Slot) {
                    return new StorageLocationScheduleRule((StorageLocation) resource, handler, TransshipmentParameter.FuzzyMode.crisp);
                }
                if (resource instanceof StorageLocation) {
                    return new StorageLocationScheduleRule((StorageLocation) resource, handler, TransshipmentParameter.FuzzyMode.crisp);
                }
                if (resource instanceof LocationBasedStorage) {
                    return new MicroscopicLocationBasedStorageRule((LocationBasedStorage) resource, handler);
                }
                return null;

            }

            @Override
            public SharedResourceManager build(SharedResource r) {
                if (r instanceof CraneRunway) {
                    return new MicroCraneRunwaySharedManager((CraneRunway) r);
                }
                if (r instanceof LCSystem) {
                    return new AreaManager((LCSystem) r);
                }
                return null;
            }

        };
    }

    @Override
    public ScheduleManagerBuilder getScheduleRuleBuilder(Terminal terminal, Scale scale) {

        if (scale.equals(Scale.macro)) {
            if (scheduleRulesMacro == null) {
                createMacro();
            }
            return scheduleRulesMacro;
        } else {
            if (scheduleRulesMicro == null) {
                createMicro();
            }
            return scheduleRulesMicro;
        }
    }

    @Override
    public String toString() {
        return "DefaultRuleMapper";
    }

}
