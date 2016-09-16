package pl.rosiakit.finder;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import pl.rosiakit.graph.ShortestPathsFinder;
import pl.rosiakit.graph.StopsEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Stop;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-14
 */
class PathsFinder {

    static Set<List<Stop>> findTravelPoints(SimpleDirectedWeightedGraph<Stop, StopsEdge> graph,
                                                 Stop source, Stop target){

        Set<List<Stop>> bfsPoints = new HashSet<>();
        Set<List<StopsEdge>> bfsPaths = new ShortestPathsFinder<>(graph).findAllShortestPaths(source, target);

        bfsPoints.addAll(bfsPaths.stream().map(PathsFinder::findDiscontinuityPoints).collect(Collectors.toList()));


        Set<List<Stop>> travelPoints = new HashSet<>(bfsPoints);
        for(List<Stop> points : bfsPoints){
            travelPoints.addAll(createNewPointsListsByDeletingSomePoints(points));
        }

        return travelPoints;
    }

    private static Set<List<Stop>> createNewPointsListsByDeletingSomePoints(List<Stop> travelPoints){
        Set<List<Stop>> newPoints = new HashSet<>();

        for(int i = 1; i < travelPoints.size()-1; ++i){
            List<Stop> newList = new ArrayList<>(travelPoints);
            newList.remove(i);
            newPoints.add(newList);
        }

        return newPoints;
    }

    private static List<Stop> findDiscontinuityPoints(List<StopsEdge> path){
        List<Stop> points = new ArrayList<>();
        points.add(path.get(0).getSource());

        Set<Line> lines = new HashSet<>();

        for(StopsEdge edge : path){
            if(lines.isEmpty()){
                lines.addAll(edge.getLines());
            }
            else {
                lines.retainAll(edge.getLines());
            }

            if(lines.isEmpty()){
                points.add(edge.getSource());
            }
        }

        points.add(path.get(path.size()-1).getTarget());

        return points;
    }

}
