package org.joelson.turf.zundin;

import java.util.Objects;

import static org.joelson.turf.zundin.Parser.StringPosition;
import static org.joelson.turf.zundin.Parser.validNumber;
import static org.joelson.turf.zundin.Parser.validString;

public class TodayZone {

    public static final String AREA_TABLE_CELL_TAG = "<td align='left'>";
    public static final String ACTIVITY_TABLE_CELL_TAG = "<td bgcolor='";
    public static final String USER_NAME_LINK_TAG = "<a href='today.php?userid=";

    private final String date;
    private final String eagerTime;
    private final String zoneName;
    private final String areaName;
    private final int tp;
    private final int pph;
    private final String activity;
    private final String userId;
    private final int take;

    public TodayZone(
            String date, String eagerTime, String zoneName, /*int zoneId,*/ String areaName, int tp, int pph,
            String activity, String userId, int take) {
        this.date = validString(date);
        this.eagerTime = eagerTime;
        this.zoneName = validString(zoneName);
        this.areaName = Objects.requireNonNull(areaName);
        this.tp = validNumber(tp, 32); // revisit halves tp
        this.pph = validNumber(pph, -1);
        this.activity = validString(activity);
        this.userId = userId;
        this.take = validNumber(take, -1);
    }

    public static TodayZone fromHTML(String html) {
        StringPosition datePosition = Parser.getDateString(html, new StringPosition("", 0));
        StringPosition eagerTimePosition = Parser.getString(html, Parser.TABLE_CELL_TAG, datePosition);
        StringPosition namePosition = Parser.getString(html, Parser.ZONE_NAME_LINK_TAG, eagerTimePosition);
        StringPosition areaPosition = Parser.getString(html, AREA_TABLE_CELL_TAG, namePosition);
        StringPosition tpPosition = Parser.getString(html, Parser.RIGHT_TABLE_CELL_TAG, areaPosition);
        StringPosition pphPosition = Parser.getString(html, Parser.LEFT_PADDED_TABLE_CELL_TAG, tpPosition);
        StringPosition activityPosition = Parser.getString(html, ACTIVITY_TABLE_CELL_TAG, pphPosition);
        StringPosition userIdPosition = Parser.getString(html, USER_NAME_LINK_TAG, activityPosition);
        if (userIdPosition.getPosition() < activityPosition.getPosition()) {
            // neutralizer
            userIdPosition = Parser.getString(html, Parser.TABLE_CELL_TAG, activityPosition);
        }
        StringPosition takePosition = Parser.getString(html, Parser.TABLE_CELL_TAG, userIdPosition);

        return new TodayZone(datePosition.stringValue(), eagerTimePosition.stringValue(), namePosition.stringValue(),
                areaPosition.stringValue(), tpPosition.integerValue(), pphPosition.integerValue(-1),
                activityPosition.stringValue(), userIdPosition.stringValue(), takePosition.integerValue(-1));
    }

    public String getDate() {
        return date;
    }

    public String getEagerTime() {
        return eagerTime;
    }

    public String getZoneName() {
        return zoneName;
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

    public String getUserId() {
        return userId;
    }

    public int getTake() {
        return take;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TodayZone that) {
            return date.equals(that.getDate()) && Objects.equals(eagerTime, that.getEagerTime()) && zoneName.equals(
                    that.getZoneName()) && areaName.equals(that.getAreaName()) && tp == that.getTP()
                    && pph == that.getPPH() && activity.equals(that.getActivity()) && userId.equals(that.getUserId())
                    && take == that.getTake();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
