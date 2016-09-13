
package pl.rosiakit.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.commons.lang.StringEscapeUtils;
import pl.rosiakit.crawler.dto.*;

import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The main and only Poznan Transport System Organiser (ztm.poznan.pl) crawler. Website has very complicated structure
 * and the very worst thing is that there are lines that have even three routes displayed on one timetable (sic!).
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class ZTMPoznanCrawler extends AbstractJsonCrawler implements ScheduleCrawler{

    private List<PlatformDTO> stops = new ArrayList<>();
    private Queue<String> omittedPlatforms = new ConcurrentLinkedQueue<>();
    private Set<String> checkedPlatforms = new HashSet<>();
    private LocalDate validSince = LocalDate.now();
    
    /**
     * Constructors just like in AbstractJsonCrawler
     */    
    public ZTMPoznanCrawler(String[] list, ListType type){
        super(list, type);
    }
    
    public ZTMPoznanCrawler(){
        super();   
    }
    
    public ZTMPoznanCrawler(String[] list, ListType type, LocalDate date){
        super(list, type, date);
    }    

    public ZTMPoznanCrawler(LocalDate date){
        super(date);
    }

    @Override
    public LocalDate validSince() {
        return this.validSince;
    }   
    
    @Override
    public Set<LineDTO> getLines(){
                
        URL url = getRoutesByNameJsonUrl();        
        String webContent = this.readUrl(url);

        return this.parseLinesFromJson(webContent);
    }

    private Set<LineDTO> parseLinesFromJson(String webContent){
        
        JsonElement response = this.getWebContentAsJsonElement(webContent);       
        JsonArray jsonLines = this.getJsonChildAsArray("lines", response);
               
        Set<LineDTO> lines = new HashSet<>();
        
        for(JsonElement singleLine : jsonLines){
            LineDTO line = this.parseSingleLineFromJson(singleLine);
            
            if(this.isLineAllowedByList(line.name)){
                lines.add(line);
            }
        }
        
        return lines;
    }
    
    private LineDTO parseSingleLineFromJson(JsonElement lineData){
        String name = this.getJsonChildAsString("name", lineData);
        int type = this.getJsonChildAsInt("type", lineData);
        String agency = this.getJsonChildAsString("agencyName", lineData);
        
        LineDTO line = new LineDTO();
        line.name = name;
        line.agencyName = agency;
        
        if(type == 0){
            line.type = VehicleType.TRAM; 
        }
        else if(type == 3){
            line.type = VehicleType.BUS;
        }
                
        return line;
    }
    
    private boolean isLineAllowedByList(String name){
        if(this.listType == ListType.BLACKLIST && !this.list.contains(name)){
            return true;
        }
        if(this.listType == ListType.WHITELIST && this.list.contains(name)){
            return true;
        }
        return false;
    }

    @Override
    public Set<PlatformDTO> getAllStops(LineDTO line) {
        Set<PlatformDTO> stopsOnLine = new HashSet<>();
        
        JsonArray directions = this.getDirectionsAsJsonArray(line); 
                
        for(JsonElement direction : directions){
            stopsOnLine.addAll(this.parseRouteFromDirection(direction));
        }
        
        return stopsOnLine;
    }

    @Override
    public TimetableDTO getPlatformTimetable(PlatformDTO platform) {
        TimetableDTO timetableDto = new TimetableDTO();
        
        JsonElement timetable = this.getTimetableAsJsonElement(platform);
        JsonArray timetables = this.getJsonChildAsArray("timetables", timetable);
        
        for(JsonElement day : timetables){
            int dayType = this.getDayType(day);
            
            switch (dayType) {
                case 8:
                    timetableDto.weekdays = parseTimetableDay(day);
                    break;
                case 6:
                    timetableDto.saturdays = parseTimetableDay(day);
                    break;
                case 7:
                    timetableDto.holidays = parseTimetableDay(day);
                    break;
                default:
                    break;
            }
        }
        
        Map<String, String> meta = this.parseTimetableMeta(timetable);
        timetableDto.legend = meta.get("legend");
        
        
        return timetableDto;
    }
    
    private List<DepartureDTO> parseTimetableDay(JsonElement day){
        List<DepartureDTO> departures = new ArrayList<>();
        JsonArray hours = this.getJsonChildAsArray("hours", day);
        
        for(JsonElement JSONhour : hours){            
            departures.addAll(this.parseHourDepartures(JSONhour));            
        }
        
        return departures;
    }
    
    private List<DepartureDTO> parseHourDepartures(JsonElement JSONhour){
        List<DepartureDTO> departures = new ArrayList<>();
        int hour = this.getJsonChildAsInt("hour", JSONhour);
            
        JsonArray JSONdepartures = this.getJsonChildAsArray("departures", JSONhour);

        for(JsonElement departure : JSONdepartures){             
            int minutes = this.getJsonChildAsInt("minutes", departure);
            String notes = this.getJsonChildAsString("annotations", departure);

            DepartureDTO dep = new DepartureDTO();
            dep.time = LocalTime.of(hour, minutes);

            for(char note: notes.toCharArray()) {
                dep.annotations.add(note+"");
            }                
            departures.add(dep);
        }   
        return departures;
    }
    
    private int getDayType(JsonElement day){
        return this.getJsonChildAsInt("dayType", day);
    }
    
    @Override
    public Set<List<PlatformDTO>> getLineRoutes(LineDTO line) {
        Set<List<PlatformDTO>> routes = new HashSet<>();
        
        omittedPlatforms = new ConcurrentLinkedQueue<>();
        checkedPlatforms = new HashSet<>();

        JsonArray directions = this.getDirectionsAsJsonArray(line); 

        for(JsonElement direction : directions){
            routes.addAll(this.getRoutesFromDirection(direction));
        }
        
        return routes;
    }
    
    private Set<List<PlatformDTO>> getRoutesFromDirection(JsonElement direction){
        stops = new ArrayList<>();

        List<PlatformDTO> directionRoute = this.parseRouteFromDirection(direction);

        return this.getRealRoutesOnDirectionRoute(directionRoute);
    }
    
    private Set<List<PlatformDTO>> getRealRoutesOnDirectionRoute(List<PlatformDTO> directionRoute){
        Set<List<PlatformDTO>> routes = new HashSet<>();
        
        for(PlatformDTO start : directionRoute){

            List<PlatformDTO> routeFromStartNode = this.getRealRouteFromStop(start);

            if(!this.isSubroute(routeFromStartNode, routes) && routeFromStartNode.size() > 0){
                List<PlatformDTO> fullRoute = this.addRoutesBeginnings(routeFromStartNode, directionRoute);
                routes.add(fullRoute);
            }                
        }
                
        return routes;
    }
        
    private List<PlatformDTO> addRoutesBeginnings(List<PlatformDTO> routeToAdd, List<PlatformDTO> directionRoute){
        List<PlatformDTO> route = new ArrayList<>();
        route.addAll(routeToAdd);
        
        PlatformDTO startNode = directionRoute.get(0);
        
        if(!routeToAdd.isEmpty()){
            PlatformDTO stop = routeToAdd.get(0);

            if(directionRoute.contains(stop) && directionRoute.indexOf(stop) > 3 && !stop.equals(startNode)){
                PlatformDTO prevStop = this.getPrevStop(stop);
                routeToAdd.add(0, prevStop);

                while(this.getPrevStop(prevStop) != null && this.getPrevStop(prevStop) != prevStop){                        
                    prevStop = this.getPrevStop(prevStop);
                    route.add(0, prevStop);
                }
            } 
        }
        
        return route;
    }
    
    
    private List<PlatformDTO> getRealRouteFromStop(PlatformDTO start){
        List<PlatformDTO> route = new ArrayList<>();
        
        PlatformDTO stop = start;
        PlatformDTO nextStop = this.getNextStop(stop);

        if(stop != null && nextStop != null){
            route.add(stop);
        }


        while(nextStop != null){
            route.add(nextStop);

            if(this.isExceptionalStop(nextStop)){
                route.add(this.getExceptionalStopSuccessor(nextStop));
            }

            stop = route.get(route.size()-1);

            nextStop = this.getNextStop(stop);
        }

        return route;
    }
    
    
    private Map<String, String> getExceptionalPlatformsList(){
        Map<String, String> exceptions = new HashMap<>();
        exceptions.put("JUCM41", "JUNI41");
        return exceptions;
    }

    private boolean isExceptionalStop(PlatformDTO stop){
        return this.getExceptionalPlatformsList().containsKey(stop.platform);
    }
    
    private PlatformDTO getExceptionalStopSuccessor(PlatformDTO stop){
        String platform = this.getExceptionalPlatformsList().get(stop.platform);

        if(platform != null){
            return this.getStopWithPlatform(platform);
        }        
        else{
            return new PlatformDTO();
        }
    }
    
    private boolean isSubroute(List<PlatformDTO> routeToCheck, Set<List<PlatformDTO>> routes){
        
        for(List<PlatformDTO> route : routes){
            
            if(route.containsAll(routeToCheck) && routeToCheck.size() > 0){
                
                int index = route.indexOf(routeToCheck.get(0));
                
                List subList = new ArrayList<>(route.subList(index, route.size()));
                
                if(subList.size() != routeToCheck.size()){
                    break;
                }
                
                if(subList.equals(routeToCheck)){
                    return true;
                }                
            }
            
        }        
        
        return false;
    }
 
    private List<PlatformDTO> parseRouteFromDirection(JsonElement direction){
        JsonArray routeJSON = this.getJsonChildAsArray("stops", direction);
  
        List<PlatformDTO> directionStops = new ArrayList<>();
        
        for(int i = 0; i < routeJSON.size(); ++i){
            JsonElement stopJSON = routeJSON.get(i);
            PlatformDTO stop = this.parseSingleStopFromJson(stopJSON);
            stop = this.addDirectionTagsToStop(stop, direction);
            stop = this.addPlatformToStop(stop);
            directionStops.add(stop);
        }
                
        this.stops.addAll(directionStops);
        return directionStops;
    }
    
    private PlatformDTO parseSingleStopFromJson(JsonElement stopJSON){
        PlatformDTO stop = new PlatformDTO();
        
        stop.id = this.getJsonChildAsString("id", stopJSON);
        stop.stopName = this.getJsonChildAsString("name", stopJSON);
        stop.lat = this.getJsonChildAsString("lat", stopJSON);
        stop.lng = this.getJsonChildAsString("lng", stopJSON);
        
        return stop;
    }
    
    private PlatformDTO addDirectionTagsToStop(PlatformDTO stop, JsonElement direction){
        stop.agencyName = this.getJsonChildAsString("agencyName", direction);
        stop.direction = this.getJsonChildAsString("direction", direction);
        stop.routeName = this.getJsonChildAsString("routeName", direction);
        return stop;
    }
    
    private PlatformDTO addPlatformToStop(PlatformDTO stop){
            
        JsonElement timetable = this.getTimetableAsJsonElement(stop);
        
        Map<String, String> meta = this.parseTimetableMeta(timetable);

        String platform = meta.get("platform");
        
        if(platform.isEmpty()){
            platform = createPlatformNameFromStopName(stop.stopName +""+stop.id);
        }
        
        stop.platform = platform;
              
        return stop;
    }
    
    private String createPlatformNameFromStopName(String stopName){
        String normalizedStop = this.normalizeNonLatin(stopName);
        String nonVowelStop = normalizedStop.replaceAll("[ ,.aeiouy]", "");

        return nonVowelStop.toUpperCase();
    }
    
    private String normalizeNonLatin(String subjectString){
        subjectString = Normalizer.normalize(subjectString, Normalizer.Form.NFD);
        return subjectString.replaceAll("[^ -~]", "");
    }
    
    private Map<String, String> parseTimetableMeta(JsonElement timetable){
        Map<String, String> meta = new HashMap<>();

        JsonElement currentStop = this.getJsonChildAsJsonElement("currentStop", timetable);
        
        String platform = this.getJsonChildAsString("code", currentStop);
        meta.put("platform", platform);
        
        String stop = this.getJsonChildAsString("name", currentStop);
        meta.put("stop", stop);
        
        
        JsonElement route = this.getJsonChildAsJsonElement("route", timetable);
        JsonArray descriptions = this.getJsonChildAsArray("descriptions", route);        
        String legend = this.getJsonChildAsString("text", descriptions.get(0));
        meta.put("legend", legend);

        return meta;
    }
    
    private PlatformDTO getPrevStop(PlatformDTO beforeStop){
        
        JsonElement timetable = this.getTimetableAsJsonElement(beforeStop);
        
        JsonElement routeElement = this.getJsonChildAsJsonElement("route", timetable);
        JsonArray routeStops = this.getJsonChildAsArray("stops", routeElement);
        
        PlatformDTO testStop;
        
        for(int i = routeStops.size()-1; i>=0; --i){
            String platform = this.getJsonChildAsString("code", routeStops.get(i));
            testStop = this.getStopWithPlatform(platform);
                                    
            if(getTravelTimeBetweenStops(testStop, beforeStop) > 0){
                return testStop;
            }
            
        }

        return null;
    }

    private int getTravelTimeBetweenStops(PlatformDTO from, PlatformDTO to){
        
        JsonElement timetable = this.getTimetableAsJsonElement(from);
        
        JsonElement routeElement = this.getJsonChildAsJsonElement("route", timetable);
        JsonArray routeStops = this.getJsonChildAsArray("stops", routeElement);
        
        for(JsonElement el : routeStops){
            String platform = this.getJsonChildAsString("code", el);
            int time = this.getJsonChildAsInt("time", el);

            if(platform.equals(to.platform)){
                return time;
            }
        }
        
        return Integer.MIN_VALUE;
    }
    
    
    private PlatformDTO getNextStop(PlatformDTO afterStop){
        JsonElement timetable = this.getTimetableAsJsonElement(afterStop);

        JsonElement routeElement = this.getJsonChildAsJsonElement("route", timetable);

        JsonArray routeStops = this.getJsonChildAsArray("stops", routeElement);

        boolean started = false;
        for(JsonElement el : routeStops){
            String platform = this.getJsonChildAsString("code", el);

            if(platform.isEmpty()){
                String stopName = this.getJsonChildAsString("name", el);
                String stopId = this.getJsonChildAsString("id", el);
                platform = this.createPlatformNameFromStopName(stopName+""+stopId);
            }

            int time = this.getJsonChildAsInt("time", el);

            if((time == Integer.MIN_VALUE || time == 0) && platform.equals(afterStop.platform)){
                started = true;
            }
            else if(started && time > 0){
                afterStop.travelTimeToNextPlatform = time;
                return this.getStopWithPlatform(platform);
            }
            else if(started && time < 0 && omittedPlatforms.isEmpty() && !checkedPlatforms.contains(platform)){ 
                omittedPlatforms.add(platform);
            }
        }
        
        return null;
    }
    
    private PlatformDTO getStopWithPlatform(String platform){
               
       for(PlatformDTO stop : stops){
           if(stop.platform.equals(platform)){     
               return stop;
           }
       } 
       
       return new PlatformDTO();
    }
                
    private JsonElement getTimetableAsJsonElement(PlatformDTO stop){
        URL url = this.getTimetableJsonUrl(stop);
        String webContent = this.readUrl(url);
        webContent = this.prepareJsonString(webContent);

        return this.getWebContentAsJsonElement(webContent); 
    }
    
    private JsonArray getDirectionsAsJsonArray(LineDTO line){
        URL url = this.getRouteDirectionsJsonUrl(line);
        String webContent = this.readUrl(url);
        webContent = this.prepareJsonString(webContent);        
                
        JsonElement response = this.getWebContentAsJsonElement(webContent); 
        
        return this.getJsonChildAsArray("directions", response);
    }
    
    /**
     * Bo strona ztm W NIEKTORYCH PRZYPADKACH zwraca zle sformatowany json
     */
    protected String prepareJsonString(String jsonString){
        jsonString = StringEscapeUtils.unescapeJava(jsonString);
        jsonString = jsonString.substring(1, jsonString.length()-2);
        return jsonString;
    }
    
    protected URL getRoutesByNameJsonUrl(){    
        String address = "http://ztm.poznan.pl/gtfs-ztm/routes_by_name.json.php"
                    + "?dbname=production_gtfs&json=dane";
        
        return this.createURLFromAddress(address);
    }
    
    protected URL getRouteDirectionsJsonUrl(LineDTO line){
        String address = "http://ztm.poznan.pl/gtfs-ztm/route_directions.json.php?"
                + "route_name=" + line.name
                + "&agency_name=" + line.agencyName
                + "&dbname=production_gtfs&json=dane";
        
        return this.createURLFromAddress(address);
    }
    
    protected URL getTimetableJsonUrl(PlatformDTO stop){
        String address = "http://ztm.poznan.pl/gtfs-ztm/timetable.json.php?"
                + "route_name=" + stop.routeName
                + "&direction=" + stop.direction
                + "&stop_id=" + stop.id
                + "&mode=wgLinii"
                + "&agency_name=" + stop.agencyName
                + "&json=dane";
        
        return this.createURLFromAddress(address);
    }

    @Override
    public String toString() {
        return "ZTMPoznanCrawler";
    }
}
