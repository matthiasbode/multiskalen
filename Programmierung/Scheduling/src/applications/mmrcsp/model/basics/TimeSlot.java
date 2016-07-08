/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applications.mmrcsp.model.basics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import math.FieldElement;
import math.LongValue;

/**
 * Klasse beschreibt ein Zeitfenster.
 *
 * @author hofmann, thees
 * @param <F>
 */
public class TimeSlot<F extends FieldElement<F>> implements Comparable<TimeSlot> {

    protected F fromWhen, untilWhen;
    private static Date buffer = new Date();
    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy;HH:mm:ss");
    private static SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat longDurationDayFormat = new SimpleDateFormat("d");
    private static SimpleDateFormat longDurationDayFormatMillis = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Dieser Zeitpunkt steht fuer negativ Unendlich. Hat ein TimeSlot diesen
     * Wert als Anfangszeitpunkt, so ist dieser Zeitpunkt unveraenderlich.
     */
    public static final long MAX_FROM = Long.MIN_VALUE / 2;

    /**
     * Dieser Zeitpunkt steht fuer positiv Unendlich. Hat ein TimeSlot diesen
     * Wert als Endzeitpunkt, so ist dieser Zeitpunkt unveraenderlich.
     */
    public static final long MAX_UNTIL = Long.MAX_VALUE / 2;

    /*
     * Mir ist noch unklar, ob ein TimeSlot eine (oder mehrere) zugeordnete
     * Ressourcen kennen muss.
     */
    /**
     *
     * @param from
     * @param until
     */
    public TimeSlot() {
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.fromWhen);
        hash = 67 * hash + Objects.hashCode(this.untilWhen);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimeSlot<?> other = (TimeSlot<?>) obj;
        if (!Objects.equals(this.fromWhen, other.fromWhen)) {
            return false;
        }
        if (!Objects.equals(this.untilWhen, other.untilWhen)) {
            return false;
        }
        return true;
    }
    
    

    public TimeSlot(F from, F until) {
        if (from == null || until == null) {
            throw new IllegalArgumentException("Null gesetzt");
        }
        if (from.isGreaterThan(until)) {
            throw new UnsupportedOperationException("kein zulaessiger TimeSlot from " + from + " to " + until + "\n" + longToFormattedDateString(from.longValue()) + "-" + longToFormattedDateString(until.longValue()));
        }
        fromWhen = from;
        untilWhen = until;
    }

    /**
     * Gibt den Zeitpunkt zurueck, an dem dieser TimeSlot beginnt.
     *
     * @return Zeitpunkt als long.
     */
    public F getFromWhen() {
        return fromWhen;
    }

    /**
     * Gibt den Zeitpunkt zurueck, an dem dieser TimeSlot endet.
     *
     * @return Zeitpunkt als long.
     */
    public F getUntilWhen() {
        return untilWhen;
    }

    public void setFromWhen(F fromWhen) {
        this.fromWhen = fromWhen;
    }

    public void setUntilWhen(F untilWhen) {
        this.untilWhen = untilWhen;
    }

    public F getMiddle() {
        F diff = untilWhen.sub(fromWhen);
        return fromWhen.add(diff.mult(1 / 2.));
    }

    public F getDuration() {
        return untilWhen.sub(fromWhen);
    }

    //    /**
    //     * Gibt an, um wie viel sich dieser TimeSlot mit dem uebergebenen
    //     * ueberschneidet.
    //     * @param other Der andere TimeSlot
    //     * @return Ueberschneidung der beiden TimeSlots; -1, falls keine Ueberschneidung
    //     * vorliegt. Bei 0 haben beide TimeSlots genau einen gemeinsamen Zeitpunkt.
    //     */
    //    public F intersectionDuration(TimeSlot other) {
    //        if(other == null || isDisjunctTo(other)) return -1l;
    //        return Math.min(other.untilWhen, untilWhen) - Math.max(fromWhen, other.fromWhen);
    //    }
    //
    //    /**
    //     * Gibt an, ob dieser TimeSlot disjunkt zu dem uebergebenen ist.
    //     * @param other Der andere TimeSlot
    //     * @return <code>true</code>, falls beide TimeSlots keinen gemeinsamen
    //     * Zeitraum haben.
    //     */
    //    public boolean isDisjunctTo(TimeSlot other) {
    //        return (other.untilWhen<=fromWhen || other.fromWhen>=untilWhen);
    //    }
    //
    //    /**
    //     * Gibt an, ob der uebergebene Zeitpunkt innerhalb diese Intervalls liegt.
    //     * Dabei zaehlen die Grenzen als innerhalb (geschlossenes Intervall).
    //     * @param time Zeitpunkt, der ueberprueft werden soll.
    //     * @return <code>true</code> g.d.w. fromWhen <= time <= untilWhen.
    //     */
    //
    //    /**
    //     * Gibt an, ob der uebergebene Zeitpunkt innerhalb diese Intervalls liegt.
    //     * Dabei zaehlen die Grenzen als ausserhalb (offenes Intervall).
    //     * @param time Zeitpunkt, der ueberprueft werden soll.
    //     * @return <code>true</code> g.d.w. fromWhen <= time <= untilWhen.
    //     */
    //    public boolean containsWithoutEdge(long time) {
    //        return fromWhen < time && time < untilWhen;
    //    }
    //
    //    /**
    //     * Gibt an, ob sich der uebergebene TimeSlot komplett innerhalb dieses TimeSlots
    //     * befindet. Das Ergebnis ist <code>contains(other.fromWhen) && contains(other.untilWhen)</code>.
    //     * @param other Der andere TimeSlot
    //     * @return <code>true</code>, falls sich sowohl der Anfang als auch das Ende
    //     * des anderen Zeitintervalls innerhalb dieses Intervalls befindet.
    //     * @see #contains(long)
    //     */
    //    public boolean contains(TimeSlot other) {
    //        return contains(other.fromWhen) && contains(other.untilWhen);
    //    }
    //
    //    /**
    //     * Gibt an, ob dieser TimeSlot VOR dem angegebenen Zeitpunkt beendet ist.
    //     * @param time
    //     * @return
    //     */
    //    public boolean isBefore(long time) {
    //        return untilWhen<=time;
    //    }
    //
    //    /**
    //     * Gibt an, ob dieser TimeSlot vor Beginn des uebergebenen TimeSlots beendet
    //     * ist.
    //     * @param o
    //     * @return
    //     */
    //    public boolean isBefore(TimeSlot o) {
    //        return untilWhen<=o.fromWhen;
    //    }
    //
    //    /**
    //     * Gibt an, ob dieser TimeSlot NACH dem angegebenen Zeitpunkt beginnt.
    //     * @param time
    //     * @return
    //     */
    //    public boolean isAfter(long time) {
    //        return fromWhen>=time;
    //    }
    //    /**
    //     * Gibt an, ob dieser TimeSlot nach Ende des uebergebenen TimeSlots beginnt.
    //     * @param o
    //     * @return
    //     */
    //    public boolean isAfter(TimeSlot o) {
    //        return fromWhen>=o.untilWhen;
    //    }
    //
    //    /**
    //     * Statische Methode gibt den maximalen TimeSlot von
    //     * {@link #MAX_FROM} bis {@link #MAX_UNTIL} zurueck.
    //     *
    //     * Der Simulationszeitraum wird dabei nicht beachtet!
    //     *
    //     * @return
    //     */
    //    public static TimeSlot getMaximumTimeSlot() {
    //        return new TimeSlot(MAX_FROM, MAX_UNTIL);
    //    }
    //
    //    /**
    //     * Erstellt einen TimeSlot vom fruehesten Start {@link #MAX_FROM}
    //     * bis zum angegebenen Endzeitpunkt.
    //     *
    //     * Der Simulationszeitraum wird dabei nicht beachtet!
    //     *
    //     * @param end Endzeitpunkt des TimeSlots
    //     * @return
    //     */
    //    public static TimeSlot getTimeSlotFromBeginning(long end) {
    //        return new TimeSlot(MAX_FROM, end);
    //    }
    //
    //    /**
    //     * Erstellt einen TimeSlot vom angegebenen Startzeitpunkt bis zum maximalen
    //     * Ende {@link #MAX_UNTIL}.
    //     *
    //     * Der Simulationszeitraum wird dabei nicht beachtet!
    //     *
    //     * @param begin
    //     * @return
    //     */
    //    public static TimeSlot getTimeSlotUntilEnding(long begin) {
    //        return new TimeSlot(begin, MAX_UNTIL);
    //    }
    //
    /**
     * Verschiebt den TimeSlot um die uebergebene Zeit. Diese kann positiv wie
     * negativ sein. Wenn 0 uebergeben wird, bleibt der TimeSlot unveraendert.
     *
     * @param dist Wert, um den beide Grenzen des Zeitintervalls verschoben
     * werden sollen.
     */
    public void move(F dist) {
        fromWhen = fromWhen.add(dist);
        untilWhen = untilWhen.add(dist);
    }

    //
    //    /**
    //     * Vereinigt diesen TimeSlot mit dem uebergebenen und gibt das Ergebnis als
    //     * neuen TimeSlot zurueck. Dies geht nur, wenn die beiden TimeSlots nicht
    //     * disjunkt sind. Ist dies der Fall, wird eine Exception geworfen.
    //     * @param other Der andere TimeSlot
    //     * @return Die Vereinigung der beiden TimeSlots als neues Objekt.
    //     * @throws MalformedTimeSlotException Falls die beiden TimeSlots disjunkt
    //     * sind.
    //     */
    //    public TimeSlot union(TimeSlot other) {
    //        if(isDisjunctTo(other)){
    //            throw new MalformedTimeSlotException("TimeSlots "
    //                +this+" and "+other+" are disjunct and cannot be united."); // ginge, wenn Start- und Endzeitpunkt gleich
    //        }
    //        return new TimeSlot(Math.min(fromWhen, other.fromWhen),
    //                            Math.max(untilWhen, other.untilWhen));
    //    }
    //
    //    /**
    //     * Gibt den schnitt dieses TimeSlots mit dem uebergebenen als neues Objekt aus.
    //     * Der sich ergebende TimeSlot ist leer, wenn die beiden TimeSlots disjunkt
    //     * sind.
    //     * @param other Der andere TimeSlot
    //     * @return Der Schnitt der beiden TimeSlots als neues Objekt.
    //     */
    public TimeSlot<F> section(TimeSlot<F> other) {
        F max = fromWhen.isGreaterThan(other.fromWhen) ? fromWhen : other.fromWhen;
        F min = untilWhen.isLowerThan(other.untilWhen) ? untilWhen : other.untilWhen;

        if (max.isGreaterThan(min) || max.equals(min)) {
            return null;
        }
        return new TimeSlot(max, min);
    }

    //
    //    public boolean isNullTimeSlot() {
    //        return this==nullTimeSlot;
    //    }
    //
    //    @Override
    //    public String toString() {
    //        buffer.setTime(fromWhen);
    //        String s = format.format(buffer);
    //        buffer.setTime(untilWhen);
    //        String e = format.format(buffer);
    //
    //        return "TimeSlot: ["+((fromWhen == MAX_FROM) ? "-Inf" : s)+","+
    //                             ((untilWhen == MAX_UNTIL) ? "Inf" : e)+"]";
    //    }
    //
    //TODO: Methode gehoert wohl woanders hin :-)
    @Override
    public String toString() {
        return "TimeSlot{" + longToFormattedDateString(fromWhen.longValue()) + ", " + longToFormattedDateString(untilWhen.longValue()) + '}';
    }

    public static String longToFormattedDateString(long time) {
        if (time == 0) {
            return " null(0)-Zeitpunkt ";
        }
        buffer.setTime(time);
        String s = format.format(buffer);
        return s;
    }

    public static String longToFormattedDurationMillis(long duration) {
        if (duration == 0) {
            return " null(0)-Duration ";
        }
        buffer.setTime(duration - 60 * 60000);
        String s = longDurationDayFormatMillis.format(buffer);
        if (duration >= 24 * 60 * 60000) {
            buffer.setTime(duration - 25 * 60 * 60000);
            s = longDurationDayFormat.format(buffer) + " Tage+" + s;
        }
        return s;
    }
 

    public static String longToFormattedDuration(long duration) {
        if (duration == 0) {
            return " null(0)-Duration ";
        }
        buffer.setTime(duration - 60 * 60000);
        String s = durationFormat.format(buffer);
        if (duration >= 24 * 60 * 60000) {
            buffer.setTime(duration - 25 * 60 * 60000);
            s = longDurationDayFormat.format(buffer) + " Tage+" + s;
        }
        return s;
    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if(!(obj instanceof TimeSlot)) return false;
//        TimeSlot t = (TimeSlot) obj;
//        if(nullTimeSlot.equals(t)) return false;
//        return t.fromWhen==fromWhen && t.untilWhen == untilWhen;
//    }
//

    @Override
    public TimeSlot clone() {
        return new TimeSlot(fromWhen, untilWhen);
    }
//
//    /**
//     * Gibt die Laenge dieses TimeSlots aus. Dies entspricht der Differenz der
//     * beiden Grenzen.
//     * @return Laenge des TimeSlots.
//     */
//    public long length() {
//        return untilWhen-fromWhen;
//    }
//

    /**
     * Vergleicht diesen TimeSlot mit dem uebergebenen. Der TimeSlot, der
     * frueher beginnt, gilt hierbei als kleiner und es wird die Differenz der
     * Startzeitpunkte ausgegeben. Falls die beiden Startzeitpunkt identisch
     * sind, wird die Differenz der Endzeitpunkte ausgegeben. Somit ist das
     * Ergebnis dieser Methode genau denn 0, wenn die beiden TimeSlots in Start-
     * und Endzeitpunkt identisch sind.<br>
     * Um Fehler durch den Ueberlauf bei der Konvertierung von long zu int zu
     * vermeiden, wird {@link Integer#MAX_VALUE } bzw. {
     *
     * @ling Integer#MIN_VALUE} ausgegeben, wenn das exakte Berechnungsergebnis
     * den Rahmen sprengen wuerde.
     * <br>
     * Diese Methode ist symmetrisch.
     * @param o Der andere TimeSlot
     * @return Ein negativer Wert, wenn dieser TimeSlot frueher beginnt oder
     * beide gleichzeitig beginnen und dieser frueher endet. Falls die TimeSlots
     * identisch sind, wird 0 ausgegeben.
     */
    @Override
    public int compareTo(TimeSlot o) {
        if (equals(o)) {
            return 0;
        }
        long l;
        int a;
        if (fromWhen.longValue() > 0 && o.fromWhen.longValue() < 0 && fromWhen.longValue() > Long.MAX_VALUE + o.fromWhen.longValue()) {
            l = Long.MAX_VALUE;
        } else if (fromWhen.longValue() < 0 && o.fromWhen.longValue() > 0 && fromWhen.longValue() < Long.MIN_VALUE + o.fromWhen.longValue()) {
            l = Long.MIN_VALUE;
        } else {
            l = (fromWhen.longValue() - o.fromWhen.longValue());
        }
        /**
         * This-Start ist größer oder kleiner o-Start
         */
        if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
            a = (int) l;
        } else {
            a = (l > Integer.MAX_VALUE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        if (a != 0) {
            return a;
        }
        if (untilWhen.longValue() > 0 && o.untilWhen.longValue() < 0 && untilWhen.longValue() > Long.MAX_VALUE + o.untilWhen.longValue()) {
            l = Long.MAX_VALUE;
        } else if (untilWhen.longValue() < 0 && o.untilWhen.longValue() > 0 && untilWhen.longValue() < Long.MIN_VALUE + o.untilWhen.longValue()) {
            l = Long.MIN_VALUE;
        } else {
            l = (untilWhen.longValue() - o.untilWhen.longValue());
        }
        if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
            a = (int) l;
        } else {
            a = (l > Integer.MAX_VALUE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        return a;
    }
//
//    /**
//     * Diese Methode teilt diesen TimeSlot in zwei TimeSlots auf, die als Array
//     * neuer Objekte zurueckgegeben werden. Der erste der beiden zurueckgegebenen
//     * TimeSlots ist der laengst moegliche TimeSlot, der ganz innerhalb dieses
//     * TimeSlots liegt und endet, bevor der uebergebene other beginnt. Der zweite
//     * TimeSlot ist der laengst moegliche TimeSlot, der gaenzlich innerhalb dieses
//     * TimeSlots liegt und nach dem Ende von other beginnt.<p>
//     * Falls der uebergebene TimeSlot nicht komplett innerhalb dieses TimeSlots
//     * liegt, ist das erste oder zweite Element der Rueckgabe <code>null</code>,
//     * je nach dem, ob der uebergebene TimeSlot vor diesem beginnt oder nach diesem
//     * endet.<p>
//     * Bsp.: <br>
//     * <code>
//     * this:&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx    <br>
//     * other:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxx<br>
//     * return:&nbsp;xxxxxxxxxxxxx&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxxxxxxxxxx<br>
//     * </code>
//     * <code>
//     * this:&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxxxxxxxxxxxx    <br>
//     * other:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;xxxxxxxxxxxxx<br>
//     * return:&nbsp;xxxxxxxxxxxxx&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[null] <br>
//     * </code>
//     * @param other Der TimeSlot, der diesen teilt.
//     * @return
//     */
//    public TimeSlot[] substract(TimeSlot other) {
//        TimeSlot[] r = new TimeSlot[2];
//        if(fromWhen < other.fromWhen)
//            r[0] = new TimeSlot(fromWhen, Math.min(untilWhen, other.fromWhen));
//        if(other.untilWhen < untilWhen)
//            r[1] = new TimeSlot(Math.max(fromWhen, other.untilWhen), untilWhen);
//        return r;
//    }
//
//    /**
//     * Verschiebt beide Grenzen dieses TimeSlots um num nach aussen. Somit ist
//     * der resultierende TimeSlot um 2*num laenger. <b>Diese Methode veraendert
//     * den aktiven TimeSlot und gibt ihn zurueck.</b>
//     * @param num Wert, um den die Grenzen ausgeweitet werden
//     * @return Dieser TimeSlot mit erweiterten Grenzen
//     * @throws MalformedTimeSlotException Wenn durch diese Umformung ein ungueltiger
//     * Zeitslot entstehen wuerde.
//     */
//    public TimeSlot increase(int num) throws MalformedTimeSlotException {
//        if(fromWhen>untilWhen-2*num)
//            throw new MalformedTimeSlotException("Cannot change to an invalid TimeSlot!");
//        if(!(fromWhen  == MAX_FROM))  fromWhen  -= num;
//        if(!(untilWhen == MAX_UNTIL)) untilWhen += num;
//        return this;
//    }
//
//    /**
//     * Bewegt beide Grenzen dieses TimeSlots um den uebergebenen Wert aufeinander
//     * zu. Das Resultat ist somit um 2*num kuerzer. <b>Diese Methode veraendert
//     * den aktiven TimeSlot und gibt ihn zurueck.</b>
//     * @param num Wert, um den die Grenzen des TimeSlots verkuerzt werden sollen
//     * @return Dieser TimeSlot mit veraenderten Grenzen
//     * @throws MalformedTimeSlotException Wenn durch diese Umformung ein ungueltiger
//     * Zeitslot entstehen wuerde.
//     */
//    public TimeSlot decrease(int num) throws MalformedTimeSlotException {
//        return increase(-num);
//    }
//
//    public static class MalformedTimeSlotException extends IllegalArgumentException {
//
//        public MalformedTimeSlotException(String s) {
//            super(s);
//        }
//        
//    }
//
//
    /**
     * stellt die Nicht-Verfuegbarkeit dar
     */
    public static final TimeSlot nullTimeSlot = new TimeSlot(new LongValue(0), new LongValue(0)) {
        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return false;
        }

    };
//    
//    public static void main (String[] args) {
//        System.out.println(longToFormattedDateString(1290747006801l));
//        System.out.println(longToFormattedDuration(25*60*60000));
//        System.out.println(new TimeSlot(1296753958421l,1296753958421l+1251530));
//        
//    }

    public static TimeSlot<LongValue> create(long fromL, long untilL) {
        return new TimeSlot<>(new LongValue(fromL), new LongValue(untilL));
    }

    public boolean contains(long time) {
        return fromWhen.longValue() <= time && time < untilWhen.longValue();
    }

    /**
     * Statische Methode gibt den maximalen TimeSlot von {@link #MAX_FROM} bis
     * {@link #MAX_UNTIL} zurueck.
     *
     * Der Simulationszeitraum wird dabei nicht beachtet!
     *
     * @return
     */
    public static TimeSlot<LongValue> getMaximumTimeSlot() {
        return create(MAX_FROM, MAX_UNTIL);
    }

    public boolean isDisjunctTo(TimeSlot other) {
        return (other.untilWhen.isLowerThan(fromWhen) || other.fromWhen.isGreaterThan(untilWhen));
    }
}
