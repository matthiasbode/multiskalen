/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.transshipment.generator.projekte.duisburg;

import applications.mmrcsp.model.basics.TimeSlot;
import applications.transshipment.TransshipmentParameter;
import applications.transshipment.generator.projekte.ParameterInputFile;
import applications.transshipment.generator.projekte.TrainGenerator;
import applications.transshipment.model.loadunits.LoadUnitTypen;
import applications.transshipment.model.structs.RailroadTrack;
import applications.transshipment.model.structs.Slot;
import applications.transshipment.model.structs.Terminal;
import applications.transshipment.model.structs.Train;
import applications.transshipment.model.structs.Wagon;
import fuzzy.number.discrete.FuzzyFactory;
import fuzzy.number.discrete.interval.DiscretizedFuzzyInterval;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bode
 */
public class DuisburgTrainGenerator implements TrainGenerator {

    @Override
    public List<Train> generateTrains(Terminal terminal, ParameterInputFile parameters) {
        TimeSlot alloverTimeSlot = terminal.getTemporalAvailability().getAllOverTimeSlot();
        ArrayList<Train> trains = new ArrayList<>();

        for (int i = 0; i < parameters.getInt("numberOfTrains"); i++) {
            Train train = new Train(parameters.getInt("numberOfWagons"), LoadUnitTypen.Typ20, LoadUnitTypen.Typ40);
            trains.add(train);
            int anz20feetmax = 0;
            int anz40feetmax = 0;

            for (Wagon w : train.getWagons()) {
                for (Slot s : w.getStorageLocations()) {
                    Rectangle2D rec = s.getGeneralOperatingArea().getBounds2D();
//               output.CTSO_Logger.println("grosse Slot "+rec+ " "+rec.getWidth());
                    if (rec.getWidth() >= LoadUnitTypen.Typ20.length && rec.getWidth() <= (LoadUnitTypen.Typ20.length + 0.2)) {
//               if (rec.getWidth()== LoadUnitTypen.Typ20.length) {
                        anz20feetmax++;
                    }

                    if (rec.getWidth() >= LoadUnitTypen.Typ40.length && rec.getWidth() <= (LoadUnitTypen.Typ40.length + 0.2)) {
//               if (rec.getWidth() == LoadUnitTypen.Typ40.length) {

                        anz40feetmax++;
                    }
                }
            }
            System.out.println(train.getNumber() + " :" + anz20feetmax + "/" + anz40feetmax);
        }

        ArrayList<Train> trainList = new ArrayList<>(trains);
        ArrayList<RailroadTrack> gleisList = new ArrayList<>(terminal.getGleise());

        long simzeit = 1000 * 60 * 60 * 8L;
        long abstandeinfahrtzuege = parameters.getLong("zeitlicherAbstandZwischenZweiZuegen");
        long luecke = parameters.getLong("gleisneubelegungsdauer") + parameters.getLong("begutachtungsdauer");

        int anzBuendel = trains.size() / terminal.getGleise().size();
        int rest = trains.size() % terminal.getGleise().size();
        int anzZuegeLetztesBuendel;

        if (rest > 0) {
            anzBuendel++;
            anzZuegeLetztesBuendel = rest;
        } else {
            anzZuegeLetztesBuendel = terminal.getGleise().size();
        }

        long umschlagdauer = (simzeit - (anzBuendel - 1) * luecke - (anzZuegeLetztesBuendel - 1) * abstandeinfahrtzuege) / anzBuendel;

        for (int i = 0; i < trainList.size(); i++) {
            Train train = trainList.get(i);
            int gleis = i % gleisList.size();
            long ankunft = alloverTimeSlot.getFromWhen().longValue() + i / terminal.getGleise().size() * (umschlagdauer + luecke) + gleis * abstandeinfahrtzuege;
            RailroadTrack track = gleisList.get(gleis);

            TimeSlot ts = TimeSlot.create(ankunft, ankunft + umschlagdauer);
            if (!TransshipmentParameter.fuzzyMode.equals(TransshipmentParameter.FuzzyMode.crisp)) {
                DiscretizedFuzzyInterval startTs = FuzzyFactory.createLinearInterval(ankunft, TransshipmentParameter.defaultUncertainty);
                DiscretizedFuzzyInterval endeTs = FuzzyFactory.createLinearInterval(ankunft + umschlagdauer, TransshipmentParameter.defaultUncertainty);
                ts = new TimeSlot(startTs, endeTs);
            }

            train.setTemporalAvailability(ts);
            Train.Arrangement ar = Train.Arrangement.left; //i % 2 == 0 ? Train.Arrangement.left : Train.Arrangement.right;
            train.setTrack(track, 0, ar);
        }
        for (Train train : trainList) {
            TransshipmentParameter.logger.info(train.getNumber() + ":" + train.getTemporalAvailability().getAllOverTimeSlot().toString() + " Gleis: " + train.getTrack());
        }

        return trains;
    }

    @Override
    public String toString() {
        return "DuisburgTrainGenerator{" + '}';
    }

}
