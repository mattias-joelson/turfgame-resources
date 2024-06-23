package org.joelson.turf.zundin;

import java.time.Duration;

import static org.joelson.turf.zundin.Parser.StringPosition;
import static org.joelson.turf.zundin.Parser.validNumber;
import static org.joelson.turf.zundin.Parser.validString;

public class MonthlyZone {

    private final String name;
    private final int tp;
    private final int pph;
    private final String municipality;
    private final int points;
    private final Duration averageDuration;
    private final int takes;
    private final int revisits;
    private final int assists;

    public MonthlyZone(
            String name, int tp, int pph, String municipality, int points, Duration averageDuration, int takes,
            int revisits, int assists) {
        this.name = validString(name);
        this.tp = validNumber(tp, 65);
        this.pph = validNumber(pph, 0);
        this.municipality = validString(municipality);
        this.points = points;
        this.averageDuration = averageDuration;
        this.takes = validNumber(takes, 0);
        this.revisits = validNumber(revisits, 0);
        this.assists = validNumber(assists, 0);
    }

    public static MonthlyZone fromHTML(String html) {
        StringPosition namePosition = Parser.getString(html, Parser.ZONE_NAME_LINK_TAG, new StringPosition("", 0));
        StringPosition municipalityPosition = Parser.getString(html, Parser.TABLE_CELL_TAG, namePosition);
        StringPosition tpPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, municipalityPosition);
        StringPosition pphPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, tpPosition);
        StringPosition pointsPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, pphPosition);
        StringPosition averageDurationPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, pointsPosition);
        StringPosition visitsPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, averageDurationPosition);
        StringPosition takesPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, visitsPosition);
        StringPosition revisitsPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, takesPosition);
        StringPosition assistsPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, revisitsPosition);


        return new MonthlyZone(namePosition.stringValue(), tpPosition.integerValue(), pphPosition.integerValue(),
                municipalityPosition.stringValue(), pointsPosition.integerValue(),
                averageDurationPosition.durationValue(), takesPosition.integerValue(), revisitsPosition.integerValue(),
                assistsPosition.integerValue());
    }

    public String getName() {
        return name;
    }

    public int getTP() {
        return tp;
    }

    public int getPPH() {
        return pph;
    }

    public String getMunicipality() {
        return municipality;
    }

    public int getPoints() {
        return points;
    }

    public int getTakePoints() {
        return tp * (takes + assists) + (int) Math.floor(tp / 2.) * revisits;
    }

    public int getPPHPoints() {
        return getPoints() - getTakePoints();
    }

    public Duration getAverageDuration() {
        return averageDuration;
    }

    public int getVisits() {
        return takes + revisits + assists;
    }

    public int getTakes() {
        return takes;
    }

    public int getRevisits() {
        return revisits;
    }

    public int getAssists() {
        return assists;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MonthlyZone that) {
            return name.equals(that.getName()) && municipality.equals(that.getMunicipality()) && tp == that.getTP()
                    && pph == that.getPPH() && points == that.getPoints() && averageDuration.compareTo(
                    that.getAverageDuration()) == 0 && takes == that.getTakes() && revisits == that.getRevisits()
                    && assists == that.getAssists();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
