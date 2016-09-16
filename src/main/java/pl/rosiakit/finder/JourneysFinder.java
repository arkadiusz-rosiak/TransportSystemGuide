package pl.rosiakit.finder;

import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import pl.rosiakit.bo.LineBo;
import pl.rosiakit.bo.RouteBo;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.graph.StopsEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Route;
import pl.rosiakit.model.Stop;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-05
 */
public class JourneysFinder {

    private static Map<Line, LineEntry> lines = new HashMap<>();

    private SimpleDirectedWeightedGraph<Stop, StopsEdge> stopsGraph;

    private LocalTime departureTime = LocalTime.now();

    static {
        getLinesFromDB();
        getRoutesFromDB();
    }

    private static void getLinesFromDB(){
        LineBo lineBo = LineBo.getInstance();
        List<Line> lines = lineBo.findAllLines();
        for(Line line : lines){
            LineEntry entry = new LineEntry(line);
            JourneysFinder.lines.put(line, entry);
        }
    }

    private static void getRoutesFromDB(){
        RouteBo routeBo = RouteBo.getInstance();

        for(Line line : lines.keySet()){
            LineEntry entry = lines.get(line);

            Map<Integer, List<Route>> routes = routeBo.findLineRoutesSplitByDirection(line);

            for(int direction : routes.keySet()) {
                List<Route> route = routes.get(direction);
                entry.addRoute(route);
            }
        }
    }

    public void setDepartureTime(LocalTime time){
        this.departureTime = time;
    }

    public Set<Journey> findJourneys(Stop from, Stop to) {

        if(from == null || to == null || from.equals(to)){
            throw new IllegalArgumentException("Wybrane przystanki nie są poprawne!");
        }

        Set<List<Stop>> travelPoints = this.findTravelPoints(from, to);
        Set<JourneyPattern> travelPatterns = this.generateJourneyPatternsViaPoints(travelPoints);

        System.out.println(travelPoints);

        return this.generateJourneys(travelPatterns);

    }

    private Set<Journey> generateJourneys(Set<JourneyPattern> travelPatterns){

        return travelPatterns.stream().filter(pattern -> pattern.getPath().size() > 0)
                .map(pattern -> JourneyFactory.createJourneyBasedOn(pattern, departureTime))
                .collect(Collectors.toSet());
    }

    private Set<JourneyPattern> generateJourneyPatternsViaPoints(Set<List<Stop>> travelPoints){

        Set<JourneyPattern> patternsFound = new HashSet<>();
        Set<JourneyPattern> filteredPatterns = new HashSet<>();

        for(List<Stop> oneTravelPoints : travelPoints){
            patternsFound.addAll(JourneyFactory.preparePatternsViaPoints(oneTravelPoints));
        }

        int minEdgesCount = findMinimumEdgesCount(patternsFound);
        Set<JourneyPattern> minEdgesPatterns = patternsWithEdgesCountLowerThan(minEdgesCount, patternsFound);

        int minTransfersFound = findMinimumTransferCount(minEdgesPatterns);
        filteredPatterns = patternsWithTransferCountLowerThan(minTransfersFound, minEdgesPatterns);

        for(JourneyPattern pattern : patternsFound){
            if(pattern.getTransfers() == 0){
                filteredPatterns.add(pattern);
            }
        }

        return filteredPatterns;
    }

    private Set<JourneyPattern> patternsWithEdgesCountLowerThan(int maxEdgesCount, Set<JourneyPattern> patterns){

        return patterns.stream().filter(pattern -> pattern.getPath().size() <= maxEdgesCount)
                .collect(Collectors.toSet());
    }

    private Set<JourneyPattern> patternsWithTransferCountLowerThan(int maxTransferCount, Set<JourneyPattern> patterns){

        return patterns.stream().filter(pattern -> pattern.getTransfers() <= maxTransferCount)
                .collect(Collectors.toSet());
    }

    private int findMinimumEdgesCount(Set<JourneyPattern> patterns){
        int minEdgesFound = Integer.MAX_VALUE;

        for(JourneyPattern pattern : patterns){
            if(pattern.getTransfers() < minEdgesFound){
                minEdgesFound = pattern.getPath().size();
            }
        }

        return minEdgesFound;
    }

    private int findMinimumTransferCount(Set<JourneyPattern> patterns){

        int minTransfersFound = Integer.MAX_VALUE;

        for(JourneyPattern pattern : patterns){
            if(pattern.getTransfers() < minTransfersFound){
                minTransfersFound = pattern.getTransfers();
            }
        }

        return minTransfersFound;
    }


    public static Set<List<PlatformsEdge>> getLineRoutes(Line line){
        LineEntry entry = lines.get(line);

        return entry.getRoutesDirections().stream().map(entry::getRoute).collect(Collectors.toSet());
    }

    public static Set<List<PlatformsEdge>> getRoutesOnStop(Stop stop){
        Set<List<PlatformsEdge>> routes = new HashSet<>();

        for(LineEntry entry : lines.values()){
            routes.addAll(entry.getRoutesOnStop(stop));
        }

        return routes;
    }

    public static Set<Line> getLinesOnStop(Stop stop){

        return lines.values().stream().filter(entry -> entry.isGoingThroughStop(stop))
                .map(LineEntry::getLine).collect(Collectors.toSet());
    }

    private Set<List<Stop>> findTravelPoints(Stop from, Stop to){

        Set<LineEntry> lineEntries = new HashSet<>(lines.values());
        Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> routes = getRouteGraphsFromLineEntries(lineEntries);

        SimpleDirectedWeightedGraph<Stop, StopsEdge> graph = createGraphBasedOnRoutes(routes);

        long startTime = System.nanoTime();

        Set<List<Stop>> points = PathsFinder.findTravelPoints(graph, from, to);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        System.out.println("Travel points found in "+duration/1000000.0 + "mS");

        return points;
    }

    private SimpleDirectedWeightedGraph<Stop, StopsEdge> createGraphBasedOnRoutes(
            Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> routes){

        this.stopsGraph = new SimpleDirectedWeightedGraph<>(
                new ClassBasedEdgeFactory<Stop, StopsEdge>(StopsEdge.class));

        for(SimpleDirectedWeightedGraph<Platform, PlatformsEdge> route : routes){

            for(Platform platform : route.vertexSet()){
                this.addStopToStopGraph(platform.getStop());
            }

            for(PlatformsEdge edge : route.edgeSet()){
                Stop source = edge.getSource().getStop();
                Stop target = edge.getTarget().getStop();

                this.connectStopsInStopGraph(source, target);
                this.addLinesToStopsGraph(source, target, edge.getLines());
            }
        }

        return this.stopsGraph;
    }

    private void addLinesToStopsGraph(Stop source, Stop target, Set<Line> lines){
        StopsEdge edge = stopsGraph.getEdge(source, target);

        if(edge != null){
            edge.addAllLines(lines);
        }
    }

    private void connectStopsInStopGraph(Stop source, Stop target){
        if(!source.equals(target)){
            StopsEdge edge = new StopsEdge();
            stopsGraph.addEdge(source, target, edge);
        }
    }

    private void addStopToStopGraph(Stop stop){
        if(!stopsGraph.containsVertex(stop)){
            stopsGraph.addVertex(stop);
        }
    }

    private Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> getRouteGraphsFromLineEntries(Set<LineEntry> entries){
        Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> routes = new HashSet<>();

        for(LineEntry entry : entries){
            routes.addAll(entry.getRoutesGraphs());
        }

        return routes;
    }

    private Set<LineEntry> getLineEntriesOnStop(Stop stop){
        Set<LineEntry> lineEntries = new HashSet<>();

        for(Line line : getLinesOnStop(stop)){
            LineEntry entry = lines.get(line);
            lineEntries.add(entry);
        }

        return lineEntries;
    }

    static List<PlatformsEdge> findLinePathBetweenStops(Line line, Stop from, Stop to){
        LineEntry entry = lines.get(line);

        for(RouteEntry route : entry.getRoutes().values()){
            List<PlatformsEdge> foundPath = new ArrayList<>();
            try {
                foundPath = route.getRouteFromStopToStop(from, to);
            }
            catch(Exception e){
                System.err.println("findLinePathBetweenStops() " + e.getMessage());
            }

            if(!foundPath.isEmpty()){
                return foundPath;
            }
        }

        return new ArrayList<>();
    }

    static Set<Line> getCommonLines(Stop s1, Stop s2){
        Set<Line> lines = getLinesOnStop(s1);
        lines.retainAll(getLinesOnStop(s2));

        return lines;
    }

}
