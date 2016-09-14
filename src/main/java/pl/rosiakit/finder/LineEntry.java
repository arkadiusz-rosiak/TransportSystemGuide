package pl.rosiakit.finder;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Route;
import pl.rosiakit.model.Stop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is containing all routes on line.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-05
 */
class LineEntry {

    private final Line line;

    private Map<Integer, RouteEntry> routes = new HashMap<>();

    LineEntry(Line line){
        this.line = line;
    }

    /**
     *
     * @param route List of pl.rosiakit.model.Route obtained from the database.
     */
    void addRoute(List<Route> route){

        Route firstPart = route.get(0);
        Route lastPart = route.get(route.size()-1);

        Platform source = firstPart.getConnection().getSource();
        Platform target = lastPart.getConnection().getTarget();

        int dir = firstPart.getDirection();

        RouteEntry routeEntry = routes.getOrDefault(dir, new RouteEntry(line, source, target));
        routeEntry.addRoute(route);

        routes.put(dir, routeEntry);
    }


    public Map<Integer, RouteEntry> getRoutes(){
        return this.routes;
    }


    Set<List<PlatformsEdge>> getRoutesOnStop(Stop stop){
        Set<List<PlatformsEdge>> routesFound;

        routesFound = routes.values().stream().filter(entry -> entry.containsStop(stop))
                .map(RouteEntry::getRoute).collect(Collectors.toSet());

        return routesFound;
    }

    boolean isGoingThroughStop(Stop stop){
        for(RouteEntry entry : routes.values()){
            if(entry.containsStop(stop)){
                return true;
            }
        }

        return false;
    }

    Set<Integer> getRoutesDirections(){
        return this.routes.keySet();
    }

    List<PlatformsEdge> getRoute(int direction){
        if(this.routes.containsKey(direction)){
            return this.routes.get(direction).getRoute();
        }
        else{
            throw new IllegalArgumentException("Routes does not contain direction: "+direction);
        }
    }

    Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> getRoutesGraphs(){
        Set<SimpleDirectedWeightedGraph<Platform, PlatformsEdge>> graphs;
        graphs = routes.values().stream().map(RouteEntry::getRouteGraph).collect(Collectors.toSet());

        return graphs;
    }

    public Line getLine(){
        return this.line;
    }

    @Override
    public String toString() {
        return "LineEntry{" +
                "routes=" + routes.size() +
                ", line=" + line +
                '}';
    }
}
