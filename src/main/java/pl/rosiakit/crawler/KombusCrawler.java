
package pl.rosiakit.crawler;

import org.apache.commons.lang.StringEscapeUtils;
import pl.rosiakit.crawler.dto.LineDTO;
import pl.rosiakit.crawler.dto.PlatformDTO;

import java.net.URL;
import java.time.LocalDate;

/**
 * Crawler for Sroda Wielkopolska (Kombus). Kombus website uses the same technology as ZTMPoznan.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class KombusCrawler extends ZTMPoznanCrawler implements ScheduleCrawler{

    public KombusCrawler(String[] list, ListType type){
        super(list, type);
    }
    
    public KombusCrawler(){
        super();   
    }
    
    public KombusCrawler(String[] list, ListType type, LocalDate date){
        super(list, type, date);
    }    

    public KombusCrawler(LocalDate date){
        super(date);
    }

    @Override
    public String city() {
        return "Środa Wielkopolska";
    }

    @Override
    protected URL getRoutesByNameJsonUrl(){    
        String address = "http://kombus.pl/timetables/proxy.php?url="
                + "http://178.250.45.138/dbServices/gtfs-kombus/routes_by_name.json.php"
                + "?dbname=production_gtfs&json=dane";
        
        return this.createURLFromAddress(address);
    }
    
    @Override
    protected URL getRouteDirectionsJsonUrl(LineDTO line){
        String address = "http://kombus.pl/timetables/proxy.php?url="
                + "http://178.250.45.138/dbServices/gtfs-kombus/route_directions.json.php"
                + "%3Froute_name%3D" + line.name
                + "%26agency_name%3D" + line.agencyName
                + "%26dbname%3Dproduction_gtfs";
        
        return this.createURLFromAddress(address);
    }
    
    @Override
    protected URL getTimetableJsonUrl(PlatformDTO stop){
        String address = "http://kombus.pl/timetables/proxy.php?url="
                + "http://178.250.45.138/dbServices/gtfs-kombus/timetable.json.php%3F"
                + "route_name%3D" + stop.routeName
                + "%26direction%3D" + stop.direction
                + "%26stop_id%3D" + stop.id
                + "%26mode%3DwgLinii"
                + "%26agency_name%3D" + stop.agencyName
                + "%26dbname%3Dproduction_gtfs";
        
        return this.createURLFromAddress(address);
    }

    @Override
    protected String prepareJsonString(String jsonString) {
        jsonString = StringEscapeUtils.unescapeJava(jsonString);
        
        int digitsNumber = 0;
        for (int i = 0; i < jsonString.length(); i++) {
            if (!Character.isDigit(jsonString.charAt(i))) {
                break;
            }
            else{
                ++digitsNumber;
            }
        }
        
        jsonString = jsonString.substring(digitsNumber+1, jsonString.length()-2);
        return jsonString;
    }

    @Override
    public String toString() {
        return "KombusCrawler";
    }
}
