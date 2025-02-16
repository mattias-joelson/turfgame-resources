# Replikering av Turf API data mellan servrar

Genomgående samma tidsformat som används av Turf API. `afterDate` namngivet utifrån parametern
i [Turf API v5 feeds](https://api.turfgame.com/v5#feeds). Är den att betrakta som inclusive eller exclusive?
`beforeDate` namngivet utifrån `afterDate` och bör ha samma inclusive/exclusive.

`from` och `to` är inclusive tidpunkter.

## Att besvara

- Är det här ett praktiskt gränssnitt?
- Är det användbart?
- Vad saknas?
- Är det värt att tillhandahålla någon form av paginering?
- En parameter som anger max antal instanser i svaret?

## Feed Objects

Efterfråga feed objects av given typ. En endpoint för varje feed typ. Kan begränsas av ett möjligt tidsintervall.
Medvetet namngiven på samma sätt som i Turfs API.

Endpoints

- `GET /v5/feeds/chat`
- `GET /v5/feeds/medal`
- `GET /v5/feeds/takeover`
- `GET /v5/feeds/zone`

Parameters (optional)

- `afterDate=2013-08-27T12:11:14+0000` (URL encoded)
- `beforeDate=2013-08-28T12:15:56+0000` (URL encoded)

Beskrivning

- Feed objects **SKA** överenstämma till sin uppbyggnad med [Turf API v5 Feeds](https://api.turfgame.com/v5#feeds).
- Svaret **SKA** innehålla alla serverns feed objects mellan tidpunkterna angivna i svaret.
- Ordningen på feed objects **SKA** vara samma som i Turf API, det vill säga fallande tidsordning (senast tidpunkt
  först, tidigast tidpunkt sist). **Är det en onödig begränsning?**
- Även om servern har fler feed objects inom det efterfrågat intervallet så **KAN** den välja att svara med en delmängd
  om svaret bedöms vara för stort.

Exempel

```
{
    "from": "2013-08-27T12:11:14+0000",
    "to": "2013-08-27T12:13:33+0000",
    "feed": [
        ...
    ]
}
```

- Bad om alla feeds mellan 2013-08-27 12:11:14 och 2013-08-28 12:15:56.
- Fick alla feed objects server hade mellan 2013-08-27 12:11:14 och 2013-08-27 12:13:33.

## Intervals

Efterfrågar för vilka tidsintervall servern har feed objects av respektive typ. En endpoint för varje feed typ.
Kan begränsas av ett möjligt tidsintervall.

Endpoints

- `GET /v5/feeds/chat/interval`
- `GET /v5/feeds/medal/interval`
- `GET /v5/feeds/takeover/interval`
- `GET /v5/feeds/zone/interval`

Parameters (optional)

- `from=2013-08-27T12:11:14+0000` (URL encoded)
- `to=2013-08-28T12:15:56+0000` (URL encoded)

Beskrivning

- Svaret **SKA** innehålla alla serverns intervall mellan tidpunkterna angivna i svaret.
- Första intervallet **KAN** börja innan efterfrågat anropets `from`, och sista **KAN** sluta efter anropets `to`.
- Även om servern har fler intervall inom det efterfrågade intervallet så **KAN** den välja att svara med en delmängd
  om svaret bedöms vara för stort.

Exempel

```
{
    "from": "2013-08-27T11:03:05+0000",
    "to": "2013-08-31T12:07:01+0000",
    "interval": [
        {
            "from": "2013-08-27T11:03:05+0000",
            "to": "2013-08-27T12:13:33+0000"
        },
        {
            "from": "2013-08-28T05:47:38+0000",
            "to": "2013-08-31T12:07:01+0000"
        }
    ]
}
```

- Bad om alla intervall mellan 2013-08-27 12:11:14 och 2013-08-28 12:15:56.
- Fick alla intervall mellan 2013-08-27 11:03:05 och 2013-08-31 12:07:01.

## Andra versioner (V4 och V6/Unstable)

Samma endpoints som ovan, men med annat versionsnummer (t.ex. `GET /unstable/takeovers`). Interval besvarar för vilka
tidsintervall servern har data av angiven version, returnerad feed objects uppfyller formatet för respektive version.

## Komprimerade svar - `Accept-Encoding: gzip`

Ja tack! :-D

## API Limitations

Samma approach som i Turfs API? Max ett anrop per sekund?
