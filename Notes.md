# Bugs / Gotchas:
* Geocoder is broken on emulators < 4.0 - http://stackoverflow.com/questions/3619924/android-geocoder-why-do-i-get-the-service-is-not-available (best comment: "Oh good grief, this is enough to make the baby jesus cry")


# Weather Services
### National Weather Service

takes raw latitude / longitude, but is a pain to parse

http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdXMLclient.php?listLatLon=38.99,-77.02%2039.70,-104.80%2047.6,-122.30&product=time-series&begin=2004-01-01T00:00:00&end=2013-04-20T00:00:00&Unit=e&maxt=maxt&mint=mint

Does this provide better global coverage??

### Google

http://www.google.co.uk/ig/api?weather=98121

requires converting lat/long -> zipcode.  Response is wildly easy to parse (though it's still XML)

Do zipcodes work globally?  YES!  From the UK: HP16 9RE.  Might have issues in some bizzare place, but may not have a good weather service there either.

Using Lat / Long - it does work! - http://stackoverflow.com/questions/2780836/using-google-weather-api-with-lat-and-lon-how-to-format



### Yahoo

http://weather.yahooapis.com/forecastrss?w=2502265

Requires an extra API call to get the WOEID from Yahoo.  Nothing interesting that Google doesn't provide.
