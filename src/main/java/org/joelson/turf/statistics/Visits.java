package org.joelson.turf.statistics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Visits implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Zone zone;
    private final User user;
    private final Round round;

    private final int tp;
    private final int pph;
    private final int takes;
    private final int assists;
    private final int revisits;

    public Visits(Zone zone, User user, Round round, int tp, int pph, int takes, int assists, int revisits) {
        this.zone = Objects.requireNonNull(zone);
        this.user = Objects.requireNonNull(user);
        this.round = Objects.requireNonNull(round);

        this.tp = tp;
        this.pph = pph;
        this.takes = takes;
        this.assists = assists;
        this.revisits = revisits;
    }

    public Zone getZone() {
        return zone;
    }

    public User getUser() {
        return user;
    }

    public Round getRound() {
        return round;
    }

    public int getTP() {
        return tp;
    }

    public int getPPH() {
        return pph;
    }

    public int getTakes() {
        return takes;
    }

    public int getAssists() {
        return assists;
    }

    public int getRevisits() {
        return revisits;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Visits visits) {
            return zone.equals(visits.zone) && user.equals(visits.user) && round.equals(visits.round) && tp == visits.tp
                    && pph == visits.pph && takes == visits.takes && assists == visits.assists
                    && revisits == visits.revisits;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return 961 * zone.hashCode() + 31 * user.hashCode() + round.hashCode();
    }

    @Override
    public String toString() {
        return "Visits{zone:" + zone + ",user:" + user + ",round:" + round + ",tp:" + tp + ",pph:+" + pph + ",takes:"
                + takes + ",assists:" + assists + ",revisits:" + revisits + '}';
    }
}
