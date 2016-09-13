package pl.rosiakit;

import pl.rosiakit.bo.*;
import pl.rosiakit.crawler.KombusCrawler;
import pl.rosiakit.crawler.ListType;
import pl.rosiakit.crawler.ScheduleCrawler;
import pl.rosiakit.crawler.ZTMPoznanCrawler;
import pl.rosiakit.crawler.dto.DepartureDTO;
import pl.rosiakit.crawler.dto.LineDTO;
import pl.rosiakit.crawler.dto.PlatformDTO;
import pl.rosiakit.crawler.dto.TimetableDTO;
import pl.rosiakit.hibernate.HibernateUtil;
import pl.rosiakit.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class SchedulesDownloader {

    private final PlatformBo platformBo = new PlatformBoImpl();
    private final LineBo lineBo = new LineBoImpl();
    private final StopBo stopBo = new StopBoImpl();
    private final ConnectionBo connectionBo = new ConnectionBoImpl();
    private final DepartureBo departureBo = new DepartureBoImpl();
    private final RouteBo routeBo = new RouteBoImpl();
    private LocalDate ScheduleValidSince;

    private void saveScheduleToDatabase(ScheduleCrawler crawler){
        System.out.println("-----------------------------");
        System.out.println(crawler.toString()+"...");
        System.out.println("-----------------------------");

        ScheduleValidSince = crawler.validSince();

        Set<LineDTO> linesNotSaved = new HashSet<>();

        Set<LineDTO> crawlerLines = crawler.getLines();
        int i = 0;
        for(LineDTO lineDto : crawlerLines){
            try {
                System.out.println("Przetwarzanie lini " + lineDto.name + " (" + (++i) +"/"+crawlerLines.size()+")... ");
                Line line = this.saveOrUpdateLine(lineDto);

                System.out.print("\tpobieranie przystankow...");

                Set<PlatformDTO> allStops = crawler.getAllStops(lineDto);
                System.out.println(" OK!");

                System.out.print("\tzapisywanie przystankow...");
                this.saveAllStops(allStops);
                System.out.println(" OK!");


                System.out.print("\tpobieranie zbioru tras...");
                Set<List<PlatformDTO>> routes = crawler.getLineRoutes(lineDto);
                System.out.println(" OK!");


                System.out.print("\tzapisywanie odjazdow...");
                for (List<PlatformDTO> routeData : routes) {
                    for (PlatformDTO platformDto : routeData) {
                        TimetableDTO timetable = crawler.getPlatformTimetable(platformDto);
                        this.saveTimetable(timetable, platformBo.findById(platformDto.platform), line);
                    }
                }
                System.out.println(" OK!");

                System.out.print("\tzapisywanie polaczen peronow");
                for (List<PlatformDTO> routeData : routes) {
                    // Generowanie trasy
                    System.out.print(".");
                    this.saveConnections(routeData);
                }
                System.out.println(" OK!");

                System.out.print("\tzapisywanie tras linii.");
                this.saveRoutes(routes, line);
                System.out.println(" OK!");
            }
            catch (Exception e){
                System.err.println("Linia "+lineDto.name+" ("+lineDto.agencyName+") nie została pobrana!");
                linesNotSaved.add(lineDto);
            }

        }

        System.out.println("Niepobrane linie: ");
        StringBuilder sb = new StringBuilder();
        for(LineDTO line : linesNotSaved){
            sb.append("\"");
            sb.append(line.name);
            sb.append("\", ");
        }
        System.out.println(sb.toString());

    }

    private List<Route> saveRoutes(Set<List<PlatformDTO>> routes, Line line){

        routeBo.deleteLineRoutes(line);

        LinkedList<Route> routesToSave = new LinkedList<>();
        ListIterator<Route> it = routesToSave.listIterator();

        int direction = 0;
        for(List<PlatformDTO> routeDto : routes){
            int ordinalNumber = 1;

            for(int i = 1; i < routeDto.size(); ++i){
                PlatformDTO prevStop = routeDto.get(i-1);
                PlatformDTO currStop = routeDto.get(i);

                Platform prevPlatform = platformBo.findById(prevStop.platform);
                Platform currPlatform = platformBo.findById(currStop.platform);

                Connection conn = connectionBo.findConnectionBySourceTarget(prevPlatform, currPlatform);

                Route route = new Route();
                route.setDirection(direction);
                route.setOrdinalNumber(ordinalNumber++);
                route.setConnection(conn);
                route.setLine(line);
                route.setValidTo(ScheduleValidSince);
                it.add(route);

            }
            ++direction;
        }

        routeBo.saveAllLineRoutes(routesToSave);

        return routesToSave;
    }


    private List<Connection> saveConnections(List<PlatformDTO> routeData){
        List<Connection> route = new ArrayList<>();

        Platform sourcePlatform, targetPlatform;
        Connection currConnection;

        // first graph's vertex
        sourcePlatform = platformBo.findById(routeData.get(0).platform);

        // second vertex and creating edge
        targetPlatform = platformBo.findById(routeData.get(1).platform);
        int travelTime = routeData.get(0).travelTimeToNextPlatform;
        this.saveOrUpdateConnection(sourcePlatform, targetPlatform, travelTime);

        sourcePlatform = targetPlatform;

        // other vertices and edges
        for(int i=2; i < routeData.size(); ++i){
            targetPlatform = platformBo.findById(routeData.get(i).platform);
            travelTime = routeData.get(i-1).travelTimeToNextPlatform;
            currConnection = this.saveOrUpdateConnection(sourcePlatform, targetPlatform, travelTime);

            route.add(currConnection);
            sourcePlatform = targetPlatform;
        }

        return route;
    }

    private Connection saveOrUpdateConnection(Platform source, Platform target, int travelTime){
        Connection connection = connectionBo.findConnectionBySourceTarget(source, target);
        if(connection == null){
            connection = this.saveConnection(source, target, travelTime);
        }
        else{
            connectionBo.saveConnection(connection);
        }

        return connection;
    }

    private Connection saveConnection(Platform source, Platform target, int travelTime){

        Connection connection = new Connection();
        connection.setSource(source);
        connection.setTarget(target);
        connection.setTravelTime(travelTime);
        connectionBo.saveConnection(connection);

        return connection;
    }

    private void saveTimetable(TimetableDTO timetable, Platform platform, Line line){

        LinkedList<Departure> departuresToSave = new LinkedList<>();

        ListIterator<Departure> it = departuresToSave.listIterator();

        for(DepartureDTO departure : timetable.weekdays){
            Departure dep = this.prepareDeparture(departure, platform, line, 8);
            it.add(dep);
        }

        for(DepartureDTO departure : timetable.saturdays){
            Departure dep = this.prepareDeparture(departure, platform, line, 6);
            it.add(dep);
        }

        for(DepartureDTO departure : timetable.holidays){
            Departure dep = this.prepareDeparture(departure, platform, line, 7);
            it.add(dep);
        }

        departureBo.saveAll(departuresToSave);
    }


    private Departure prepareDeparture(DepartureDTO departureDto, Platform platform, Line line, int dayType){

        Departure departure = new Departure();
        departure.setDepartureTime(departureDto.time);
        departure.setLine(line);
        departure.setPlatform(platform);
        departure.setValidSince(ScheduleValidSince);
        departure.setDayType(dayType);
        return departure;

    }

    private void saveAllStops(Set<PlatformDTO> stops){
        stops.stream().filter(stopData -> stopData.platform != null && stopData.stopName != null).forEach(stopData -> {
            Stop stop = this.saveStop(stopData);
            this.savePlatform(stopData.platform, stop, stopData);
        });
    }

    private Stop saveStop(PlatformDTO stopData){
        Stop stop = stopBo.findSingleStopByName(stopData.stopName);

        if(stop == null){
            stop = new Stop();
            stop.setName(stopData.stopName);
            stopBo.saveStop(stop);
        }

        return stop;
    }

    private Platform savePlatform(String id, Stop stop, PlatformDTO platformDto){
        Platform platform = new Platform();
        platform.setId(id);
        platform.setStop(stop);

        try {
            float lat = Float.parseFloat(platformDto.lat);
            float lng = Float.parseFloat(platformDto.lng);

            platform.setLat(lat);
            platform.setLng(lng);
        }
        catch(NumberFormatException e){
            System.err.println("Cannot set position to platform "+id+ " error:" + e.getMessage());
        }

        if(platformBo.findById(platform.getId()) == null){
            platformBo.savePlatform(platform);
        }

        return platform;
    }

    private Line saveOrUpdateLine(LineDTO lineDto){

        Line lineInDB = lineBo.findLineByAgencyAndName(lineDto.agencyName, lineDto.name);

        if(lineInDB != null){
            lineInDB.setValidSince(ScheduleValidSince);
            lineBo.saveLine(lineInDB);
        }
        else{
            lineInDB = this.saveNewLine(lineDto);
        }

        return lineInDB;
    }

    private Line saveNewLine(LineDTO lineDto){
        Line line = new Line();
        line.setName(lineDto.name);
        line.setAgencyName(lineDto.agencyName);
        line.setType(VehicleType.valueOf(lineDto.type.toString()));
        line.setValidSince(ScheduleValidSince);

        lineBo.saveLine(line);
        return line;
    }


    public static void main(String[] args){
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        SchedulesDownloader app = new SchedulesDownloader();
        List<ScheduleCrawler> crawlers = new LinkedList<>();

        String[] kombusList = {"1","2","3","4","5","6","10","11","12","13","14","15","16","17","18","19","20"};
        String[] ztmBlackList = {"201","231","232","233","234","235","236","237","238","239","240","242","243","244",
                "245","246","247","248","249","251","252"};

        crawlers.add(new KombusCrawler(kombusList, ListType.WHITELIST));
        crawlers.add(new ZTMPoznanCrawler(ztmBlackList, ListType.BLACKLIST));

        crawlers.forEach(app::saveScheduleToDatabase);

        HibernateUtil.shutdown();
    }

}
