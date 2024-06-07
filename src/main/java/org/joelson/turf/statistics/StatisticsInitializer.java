package org.joelson.turf.statistics;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class StatisticsInitializer {

    private StatisticsInitializer() throws InstantiationException {
        throw new InstantiationException("Should not be instantiated!");
    }

    public static Statistics initialize() {
        Statistics statistics = new Statistics();

        Country spain = new Country("es");
        statistics.addCountry(spain);
        Country greatBritain = new Country("gb");
        statistics.addCountry(greatBritain);
        Country finland = new Country("fi");
        statistics.addCountry(finland);
        Country norway = new Country("no");
        statistics.addCountry(norway);
        Country sweden = new Country("se");
        statistics.addCountry(sweden);

        Region gotland = new Region(138, "Gotland", sweden);
        statistics.addRegion(gotland);
        addMunicipality(statistics, gotland, "Gotland");

        Region gavleborg = new Region(144, "Gävleborg", sweden);
        statistics.addRegion(gavleborg);
        addMunicipality(statistics, gavleborg, "Bollnäs kommun");
        addMunicipality(statistics, gavleborg, "Gävle kommun");
        addMunicipality(statistics, gavleborg, "Hofors kommun");
        addMunicipality(statistics, gavleborg, "Hudiksvalls kommun");
        addMunicipality(statistics, gavleborg, "Ljusdals kommun");
        addMunicipality(statistics, gavleborg, "Nordanstigs kommun");
        addMunicipality(statistics, gavleborg, "Ockelbo kommun");
        addMunicipality(statistics, gavleborg, "Ovanåker");
        addMunicipality(statistics, gavleborg, "Sandvikens kommun");
        addMunicipality(statistics, gavleborg, "Söderhamn");

        Region jamtland = new Region(128, "Jämtland", sweden);
        statistics.addRegion(gavleborg);
        addMunicipality(statistics, jamtland, "Bräcke kommun");
        addMunicipality(statistics, jamtland, "Ragunda kommun");

        Region norrbotten = new Region(126, "Norrbotten", sweden);
        statistics.addRegion(gavleborg);
        addMunicipality(statistics, jamtland, "Piteå kommun");

        Region stockholm = new Region(141, "Stockholm", sweden);
        statistics.addRegion(stockholm);
        addMunicipality(statistics, stockholm, "Botkyrka kommun");
        addMunicipality(statistics, stockholm, "Danderyds kommun");
        addMunicipality(statistics, stockholm, "Ekerö kommun");
        addMunicipality(statistics, stockholm, "Haninge kommun");
        addMunicipality(statistics, stockholm, "Huddinge kommun");
        addMunicipality(statistics, stockholm, "Järfälla kommun");
        addMunicipality(statistics, stockholm, "Lidingö kommun");
        addMunicipality(statistics, stockholm, "Nacka kommun");
        addMunicipality(statistics, stockholm, "Nykvarns kommun");
        addMunicipality(statistics, stockholm, "Nynäshamns kommun");
        addMunicipality(statistics, stockholm, "Norrtälje kommun");
        addMunicipality(statistics, stockholm, "Salems kommun");
        addMunicipality(statistics, stockholm, "Sigtuna kommun");
        addMunicipality(statistics, stockholm, "Solna kommun");
        addMunicipality(statistics, stockholm, "Sollentuna kommun");
        addMunicipality(statistics, stockholm, "Stockholms kommun");
        addMunicipality(statistics, stockholm, "Sundbybergs kommun");
        addMunicipality(statistics, stockholm, "Tyresö kommun");
        addMunicipality(statistics, stockholm, "Täby kommun");
        addMunicipality(statistics, stockholm, "Vaxholms kommun");
        addMunicipality(statistics, stockholm, "Vallentuna kommun");
        addMunicipality(statistics, stockholm, "Upplands Väsby kommun");
        addMunicipality(statistics, stockholm, "Upplands-Bro kommun");

        Region sodermanland = new Region(140, "Södermanland", sweden);
        statistics.addRegion(sodermanland);
        addMunicipality(statistics, sodermanland, "Katrineholms kommun");
        addMunicipality(statistics, sodermanland, "Strängnäs kommun");

        Region uppsala = new Region(142, "Uppsala", sweden);
        statistics.addRegion(uppsala);
        addMunicipality(statistics, uppsala, "Enköpings kommun");
        addMunicipality(statistics, uppsala, "Heby kommun");
        addMunicipality(statistics, uppsala, "Håbo");
        addMunicipality(statistics, uppsala, "Knivsta kommun");
        addMunicipality(statistics, uppsala, "Tierps kommun");
        addMunicipality(statistics, uppsala, "Uppsala kommun");
        addMunicipality(statistics, uppsala, "Älvkarleby kommun");
        addMunicipality(statistics, uppsala, "Östhammars kommun");

        Region varmland = new Region(130, "Värmland", sweden);
        statistics.addRegion(varmland);
        addMunicipality(statistics, varmland, "Kristinehamns kommun");

        Region vasterbotten = new Region(127, "Västerbotten", sweden);
        statistics.addRegion(vasterbotten);
        addMunicipality(statistics, vasterbotten, "Bjurholms kommun");
        addMunicipality(statistics, vasterbotten, "Nordmalings kommun");
        addMunicipality(statistics, vasterbotten, "Norsjö kommun");
        addMunicipality(statistics, vasterbotten, "Skellefteå kommun");
        addMunicipality(statistics, vasterbotten, "Umeå kommun");
        addMunicipality(statistics, vasterbotten, "Vindelns kommun");
        addMunicipality(statistics, vasterbotten, "Vännäs kommun");

        Region vasternorrland = new Region(145, "Västernorrland", sweden);
        statistics.addRegion(vasternorrland);
        addMunicipality(statistics, vasternorrland, "Härnösands kommun");
        addMunicipality(statistics, vasternorrland, "Kramfors kommun");
        addMunicipality(statistics, vasternorrland, "Sollefteå kommun");
        addMunicipality(statistics, vasternorrland, "Sundsvalls kommun");
        addMunicipality(statistics, vasternorrland, "Timrå kommun");
        addMunicipality(statistics, vasternorrland, "Ånge kommun");
        addMunicipality(statistics, vasternorrland, "Örnsköldsviks kommun");

        Region vastmanland = new Region(143, "Västmanland", sweden);
        statistics.addRegion(vastmanland);
        addMunicipality(statistics, vastmanland, "Hallstahammars kommun");
        addMunicipality(statistics, vastmanland, "Köpings kommun");
        addMunicipality(statistics, vastmanland, "Sala kommun");
        addMunicipality(statistics, vastmanland, "Västerås");

        Region vastraGotaland = new Region(132, "Västra Götaland", sweden);
        statistics.addRegion(vastraGotaland);
        addMunicipality(statistics, vastraGotaland, "Göteborgs kommun");
        addMunicipality(statistics, vastraGotaland, "Vårgårda kommun");

        Region ostergotland = new Region(138, "Östergötland", sweden);
        statistics.addRegion(ostergotland);
        addMunicipality(statistics, ostergotland, "Linköpings kommun");

        Region orebro = new Region(131, "Örebro", sweden);
        statistics.addRegion(uppsala);
        addMunicipality(statistics, orebro, "Degerfors kommun");
        addMunicipality(statistics, orebro, "Hallsbergs kommun");
        addMunicipality(statistics, orebro, "Lindesbergs kommun");
        addMunicipality(statistics, orebro, "Örebro kommun");

        Region aland = new Region(177, "Åland", finland);
        statistics.addRegion(aland);
        addMunicipality(statistics, aland, "Mariehamn");

        Region ostlandet = new Region(179, "Østlandet", norway);
        statistics.addRegion(ostlandet);
        addMunicipality(statistics, ostlandet, "Kongsvinger");
        addMunicipality(statistics, ostlandet, "Oslo");

        Region spainRegion = new Region(211, "Spain", spain);
        statistics.addRegion(spainRegion);
        addMunicipality(statistics, spainRegion, "Spain");

        Region scotland = new Region(200, "Scotland", greatBritain);
        statistics.addRegion(scotland);
        addMunicipality(statistics, scotland, "City of Edinburgh");
        addMunicipality(statistics, scotland, "North Ayrshire");
        addMunicipality(statistics, scotland, "Renfrewshire");

        statistics.addUser(new User(80119, "Oberoff"));

        statistics.addRound(new Round(113, "June", ZonedDateTime.of(2019, 11, 3, 11, 0, 0, 0, ZoneId.of("UTC+00:00"))));

        return statistics;
    }

    private static void addMunicipality(Statistics statistics, Region region, String name) {
        statistics.addMunicipality(new Municipality(name, region));
    }
}
