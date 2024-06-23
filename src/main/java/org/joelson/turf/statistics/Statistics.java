package org.joelson.turf.statistics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class Statistics implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final HashSet<Country> countries;
    private final HashSet<Region> regions;
    private final HashSet<Municipality> municipalities;
    private final HashSet<Zone> zones;
    private final HashSet<Round> rounds;
    private final HashSet<User> users;
    private final HashSet<Visits> visits;

    public Statistics() {
        countries = new HashSet<>();
        regions = new HashSet<>();
        municipalities = new HashSet<>();
        zones = new HashSet<>();
        rounds = new HashSet<>();
        users = new HashSet<>();
        visits = new HashSet<>();
    }

    private static String getCountryCode(String name) {
        /*for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            if (l.getDisplayCountry().equals(name)) {
                return iso;
            }
        }*/
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayCountry().equals(name)) {
                return locale.getISO3Country();
            }
        }
        return "-1";
    }

    public boolean addCountry(Country country) {
        return countries.add(Objects.requireNonNull(country));
    }

    public Set<Country> getCountries() {
        return Collections.unmodifiableSet(countries);
    }

    public boolean addRegion(Region region) {
        return regions.add(Objects.requireNonNull(region));
    }

    public Set<Region> getRegions() {
        return Collections.unmodifiableSet(regions);
    }

    public boolean addMunicipality(Municipality municipality) {
        return municipalities.add(Objects.requireNonNull(municipality));
    }

    public Municipality getMunicipality(String name) {
        for (Municipality municipality : municipalities) {
            if (municipality.getName().equals(name)) {
                return municipality;
            }
        }
        return null;
    }

    public boolean addZone(Zone zone) {
        return zones.add(Objects.requireNonNull(zone));
    }

    public Zone getZone(String name) {
        for (Zone zone : zones) {
            if (zone.getName().equals(name)) {
                return zone;
            }
        }
        return null;
    }

    public boolean addRound(Round round) {
        return rounds.add(Objects.requireNonNull(round));
    }

    public Round getRound(int id) {
        for (Round round : rounds) {
            if (round.getId() == id) {
                return round;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        return users.add(Objects.requireNonNull(user));
    }

    public User getUser(String name) {
        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    public boolean addVisits(Visits visits) {
        if (this.visits.stream().anyMatch(
                v -> v.getZone().equals(visits.getZone()) && v.getUser().equals(visits.getUser()) && v.getRound()
                        .equals(visits.getRound()))) {
            return false;
        }
        return this.visits.add(visits);
    }

    @Override
    public String toString() {
        return "Statistics{countries:" + countries + ",regions:" + regions + ",municipalities:" + municipalities
                + ",zones:" + zones + ",rounds:" + rounds + ",users:" + users + ",visits:" + visits + '}';
    }

    public void importRegions(List<org.joelson.turf.turfgame.apiv4.Region> regions) {
        for (org.joelson.turf.turfgame.apiv4.Region region : regions) {
            String countryName = region.getCountry();
            if (countryName == null) {
                countryName = getCountryCode(region.getName());
            }
            Country country = findOrAddCountry(countryName);
            findOrAddRegion(country, region.getId(), region.getName());
        }
    }

    private Country findOrAddCountry(String name) {
        for (Country country : countries) {
            if (country.getName().equals(name)) {
                return country;
            }
        }
        Country country = new Country(name);
        addCountry(country);
        return country;
    }

    private Region findOrAddRegion(Country country, int id, String name) {
        for (Region region : regions) {
            boolean issueRegion = false;
            if (region.getName().equals(name)) {
                if (region.getId() == id) {
                    throw new IllegalStateException("Region '" + name + "' has id " + id + " - the same as " + region);
                }
                if (!country.equals(region.getCountry())) {
                    // is OK if belonging to different countries
                } else if (!name.equals("Argentina") && !name.equals("Kenya") && !name.equals("Utah")
                        && !name.equals("template")) {
                    // https://issues.turfgame.com/view/8013
                    throw new IllegalStateException(
                            "Region '" + name + "' has both id " + region.getId() + " and " + id + '!');
                }
            }
        }
        Region region = new Region(id, name, country);
        addRegion(region);
        return region;
    }
}
