package org.joelson.turf.zundin;

import java.util.Objects;

import static org.joelson.turf.zundin.Parser.StringPosition;
import static org.joelson.turf.zundin.Parser.validNumber;
import static org.joelson.turf.zundin.Parser.validString;

public class TwodayZone {

    public static final String AREA_TABLE_CELL_TAG = "<td align='left'>";
    public static final String ACTIVITY_TABLE_CELL_TAG = "<td bgcolor='";
    public static final String USER_NAME_LINK_TAG = "<a href='today.php?userid=";
    public static final String USER_NAME_TWODAY_LINK_TAG = "<a href='2day.php?userid=";

    private final String date;
    private final String eagerTime;
    private final int zoneId;
    private final String areaName;
    private final int tp;
    private final int pph;
    private final String activity;
    private final int points;
    private final String duration;
    private final boolean neutralized;
    private final String takenFromUserId;
    private final String lostToUserId;
    private final int take;

    public TwodayZone(
            String date, String eagerTime, int zoneId, String areaName, int tp, int pph, String activity,
            boolean neutralized, int points, String duration, String takenFromUserId, String lostToUserId, int take) {
        this.date = validString(date);
        this.eagerTime = eagerTime;
        this.zoneId = zoneId;
        this.areaName = Objects.requireNonNull(areaName);
        this.tp = validNumber(tp, 32); // revisit halves tp
        this.pph = validNumber(pph, -1);
        this.activity = validString(activity);
        this.neutralized = neutralized;
        this.points = points;
        this.duration = duration;
        this.takenFromUserId = takenFromUserId;
        this.lostToUserId = lostToUserId;
        this.take = validNumber(take, -1);
    }

    public static TwodayZone fromHTML(String html) {
        StringPosition datePosition = Parser.getTwodayDateString(html, new StringPosition("", 0));
        StringPosition eagerTimePosition = Parser.getString(html, Parser.TABLE_CELL_TAG, datePosition);
        StringPosition namePosition = Parser.getString(html, Parser.ZONE_NAME_LINK_TAG, eagerTimePosition);
        StringPosition zoneIdPosition = Parser.getString(html, Parser.ZONE_NAME_LINK_TAG, "&", eagerTimePosition);
        StringPosition areaPosition = Parser.getString(html, AREA_TABLE_CELL_TAG, namePosition);
        StringPosition tpPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, areaPosition);
        StringPosition pphPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, tpPosition);
        // neutralized
        // durationPosition
        StringPosition activityPosition = Parser.getString(html, ACTIVITY_TABLE_CELL_TAG, pphPosition);
        StringPosition pointsPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, activityPosition);
        StringPosition durationPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, pointsPosition);
        StringPosition takenFromCellPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, durationPosition);
        StringPosition takenFromIdPosition;
        if (html.startsWith(
                "</td>", takenFromCellPosition.getPosition())) {
            takenFromIdPosition = Parser.getString(html, Parser.TABLE_CELL_TAG, takenFromCellPosition);
        } else {
            takenFromIdPosition = Parser.getString(html, USER_NAME_TWODAY_LINK_TAG, takenFromCellPosition);
        }
        StringPosition lostToCellPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, takenFromCellPosition);
        StringPosition lostToIdPosition;
        if (html.startsWith("</td>", lostToCellPosition.getPosition())) {
            lostToIdPosition = Parser.getString(html, Parser.TABLE_CELL_TAG, lostToCellPosition);
        } else {
            lostToIdPosition = Parser.getString(html, USER_NAME_TWODAY_LINK_TAG, lostToCellPosition);
        }
        StringPosition takePosition = Parser.getString(html, Parser.LEFT_TABLE_CELL_TAG, lostToIdPosition);
        boolean neutralized = false;
        if (activityPosition.stringValue().equals("Takeover") && takenFromIdPosition.stringValue().isEmpty()) {
            neutralized = true;
        } else if (activityPosition.stringValue().equals("Assist")
                && pointsPosition.integerValue() == tpPosition.integerValue() + 50) {
            neutralized = true;
        } // date fel, take fel

        return new TwodayZone(datePosition.stringValue(), eagerTimePosition.stringValue(),
                zoneIdPosition.integerValue(), areaPosition.stringValue(), tpPosition.integerValue(),
                pphPosition.integerValue(-1), activityPosition.stringValue(), neutralized,
                pointsPosition.integerValue(), durationPosition.stringValue(), takenFromIdPosition.stringValue(),
                lostToIdPosition.stringValue(), takePosition.integerValue(-1));
    }

    public String getDate() {
        return date;
    }

    public String getEagerTime() {
        return eagerTime;
    }

    public int getZoneId() {
        return zoneId;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getTP() {
        return tp;
    }

    public int getPPH() {
        return pph;
    }

    public String getActivity() {
        return activity;
    }

    public boolean isNeutralized() {
        return neutralized;
    }

    public int getPoints() {
        return points;
    }

    public String getDuration() {
        return duration;
    }

    public String getTakenFromUserId() {
        return takenFromUserId;
    }

    public String getLostToUserId() {
        return lostToUserId;
    }

    public int getTake() {
        return take;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TwodayZone that) {
            return date.equals(that.getDate()) && Objects.equals(eagerTime, that.getEagerTime())
                    && zoneId == that.zoneId && areaName.equals(that.getAreaName()) && tp == that.getTP()
                    && pph == that.getPPH() && activity.equals(that.getActivity())
                    && neutralized == that.isNeutralized() && points == that.getPoints() && Objects.equals(duration,
                    that.getDuration()) && Objects.equals(takenFromUserId, that.getTakenFromUserId()) && Objects.equals(
                    lostToUserId, that.getLostToUserId()) && take == that.getTake();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
