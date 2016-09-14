package pl.rosiakit.finder;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Route;
import pl.rosiakit.model.Stop;

import java.util.*;

/**
 * This class is representing single route of the vehicle. It contains route as directed graph (with platforms on vertices)
 * but you can get route both as full graph and as only graph edges.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-05
 */
class RouteEntry {

    private final Line line;

    private final SimpleDirectedWeightedGraph<Platform, PlatformsEdge> route;

    private final Platform source;

    private final Platform target;

    private Map<Platform, Integer> platformsOnRoute = new HashMap<>();

    private List<PlatformsEdge> edges = new ArrayList<>(30);

    RouteEntry(Line line, Platform source, Platform target){
        route = new SimpleDirectedWeightedGraph<>(PlatformsEdge.class);
        this.line = line;
        this.source = source;
        this.target = target;
    }

    SimpleDirectedWeightedGraph<Platform, PlatformsEdge> getRouteGraph(){
        return this.route;
    }

    void addRoute(List<Route> route){
        route.forEach(this::addRoutePart);
    }

    private void addRoutePart(Route part){

        Platform source = part.getConnection().getSource();
        Platform target = part.getConnection().getTarget();

        addRouteVertex(source);
        addRouteVertex(target);

        addPlatformOrder(source);
        addPlatformOrder(target);

        long time = part.getConnection().getTravelTime();

        if(!source.equals(target)) {
            createEdgeBetweenPlatforms(source, target, time);
            edges.add(route.getEdge(source, target));
        }
    }

    private void addPlatformOrder(Platform platform){
        if(!platformsOnRoute.containsKey(platform)){
            platformsOnRoute.put(platform, platformsOnRoute.size()+1);
        }
    }

    boolean containsStop(Stop stop){
        for(Platform p : stop.getPlatforms()){
            if(platformsOnRoute.containsKey(p)){
                return true;
            }
        }

        return false;
    }

    List<PlatformsEdge> getRouteFromStopToStop(Stop source, Stop target){
        List<PlatformsEdge> path = new ArrayList<>();

        boolean started = false;
        for(PlatformsEdge edge : this.edges){
            if(edge.getSource().getStop().equals(source)){
                started = true;
                path.clear();
            }

            if(started){
                path.add(edge);
            }

            if(started && edge.getTarget().getStop().equals(target)) {
                return path;
            }
        }

        return Collections.emptyList();
    }

    private void addRouteVertex(Platform platform){
        try {
            route.addVertex(platform);
        }
        catch(Exception e){
            System.err.println("addRoutePartVertices() : "+ e);
        }
    }

    private void createEdgeBetweenPlatforms(Platform source, Platform target, long travelTime){
        Set<Line> lines = new HashSet<>();
        lines.add(line);

        PlatformsEdge edge = new PlatformsEdge(lines);

        if(!source.equals(target)){
            this.connectPlatforms(source, target, edge);
        }

        if(route.containsEdge(edge)){
            route.setEdgeWeight(edge, travelTime);
        }
    }

    private void connectPlatforms(Platform source, Platform target, PlatformsEdge edge){
        try {
            route.addEdge(source, target, edge);
        }
        catch(Exception e){
            System.err.println("connectPlatforms() : "+ e);
        }
    }

    public List<PlatformsEdge> getRoute(){
        return this.getRouteFromStopToStop(getSourceStop(), getTargetStop());
    }

    public Stop getSourceStop(){
        return this.source.getStop();
    }

    public Stop getTargetStop(){
        return this.target.getStop();
    }

    @Override
    public String toString() {
        return "RouteEntry{" +
                "route=" + route +
                '}';
    }
}
