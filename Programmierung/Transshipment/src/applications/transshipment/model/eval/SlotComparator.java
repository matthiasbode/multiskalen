package applications.transshipment.model.eval;

 
import applications.transshipment.model.eval.EvalFunction_Slot;
import applications.transshipment.model.structs.Slot;
import java.util.Comparator;

/**
 *
 * @author hofmann
 */
public class SlotComparator implements Comparator<Slot> {

    private EvalFunction_Slot function;

    public SlotComparator (EvalFunction_Slot function) {
        this.function=function;
    }

    @Override
    public int compare(Slot o1, Slot o2) {

        //TODO: fuer Performance Bewertung zwischenspeichern

        if (function.evaluate(o1) > function.evaluate(o2))
            return 1;
        if (function.evaluate(o1) < function.evaluate(o2))
            return -1;
        else return 0;
    }



}
