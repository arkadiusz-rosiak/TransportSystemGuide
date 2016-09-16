package pl.rosiakit.finder;

import pl.rosiakit.bo.DepartureBo;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.Departure;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Stop;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-18
 */
class JourneyFactory {

    private List<List<JourneyPatternPart>> patternParts;

    private Set<JourneyPattern> foundPatterns = new HashSet<>();

    private static DepartureBo departureBo = DepartureBo.getInstance();

    private int minTransfers = Integer.MAX_VALUE;

    private JourneyFactory(){ }

    static Journey createNextJourneyAfter(Journey journey){
        LocalTime journeyDeparture = journey.getDepartureTime();
        journeyDeparture = journeyDeparture.plusMinutes(1);

        JourneyPattern pattern = journey.getPattern();
        return createJourneyBasedOn(pattern, journeyDeparture);
    }

    static Journey createJourneyBasedOn(JourneyPattern pattern, LocalTime departureTime){

        List<LocalTime> departureTimes = new ArrayList<>();
        LocalTime prevDeparture = departureTime;
        PlatformsEdge prevEdge = null;
        Line prevLine = null;

        PlatformsEdge lastEdge = pattern.getPath().get(pattern.getPath().size()-1);

        for(int i = 0; i < pattern.getPath().size(); ++i){
            PlatformsEdge edge = pattern.getPath().get(i);
            Line line = pattern.getLines().get(edge);

            Line nextLine = null;
            if(i + 1 < pattern.getPath().size()){
                PlatformsEdge nextEdge = pattern.getPath().get(i+1);
                nextLine = pattern.getLines().get(nextEdge);
            }

            if(prevEdge != null){
                long minutes = (long) prevEdge.getTime();
                prevDeparture = prevDeparture.plusMinutes(minutes);
            }

            // Kontynujemy jazde, wiec nie musimy znac dokladnej daty odjazdu
            if(prevLine != null && line.equals(prevLine) && line.equals(nextLine) && !edge.equals(lastEdge)){
                departureTimes.add(prevDeparture);
            }
            else{
                Platform platform = edge.getSource();

                if(prevEdge != null && !prevEdge.getTarget().equals(edge.getSource())){
                    prevDeparture = prevDeparture.plusMinutes(2);
                }

                List<Departure> departures = departureBo.findLineDeparturesAfter(platform, 8, line, prevDeparture);

                if(!departures.isEmpty()) {
                    prevDeparture = departures.get(0).getDepartureTime();
                    departureTimes.add(prevDeparture);
                }
                else{
                    return null;
                }
            }

            prevLine = line;
            prevEdge = edge;
        }

        return new Journey(pattern, departureTimes);
    }

    static Set<JourneyPattern> preparePatternsViaPoints(List<Stop> travelPoints){
        JourneyFactory jf = new JourneyFactory();
        jf.generatePatternsViaPoints(travelPoints);
        return jf.getFoundPatterns();
    }


    private Set<JourneyPattern> getFoundPatterns(){
        return new HashSet<>(foundPatterns);
    }

    private void generatePatternsViaPoints(List<Stop> travelPoints){

        Stop source = travelPoints.get(0);
        Stop target = travelPoints.get(travelPoints.size()-1);

        this.tryToFindNonStopJourney(source, target);
        this.findCombinedPatterns(travelPoints);
    }

    private void tryToFindNonStopJourney(Stop source, Stop target){

        Set<Line> commonLines = JourneysFinder.getCommonLines(source, target);

        for(Line line : commonLines){

            List<PlatformsEdge> path = JourneysFinder.findLinePathBetweenStops(line, source, target);

            Map<PlatformsEdge, Line> lines = new HashMap<>();
            for(PlatformsEdge edge : path){
                lines.put(edge, line);
            }

            JourneyPattern jp = new JourneyPattern(path, lines);
            foundPatterns.add(jp);
        }
    }

    private void findCombinedPatterns(List<Stop> travelPoints){
        patternParts = generatePatternParts(travelPoints);

        for(int option = 0; option < patternParts.get(0).size(); ++option){
            visit(0, option, new ArrayList<>());
        }
    }

    private void visit(int part, int option, List<JourneyPatternPart> partsFollowed){

        List<JourneyPatternPart> thisPartFamily = patternParts.get(part);

        partsFollowed.add(thisPartFamily.get(option));

        if(part + 1 < patternParts.size()){

            List<JourneyPatternPart> nextPartFamily = patternParts.get(part+1);

            for(int i=0; i < nextPartFamily.size(); ++i){
                visit(part+1, i, new ArrayList<>(partsFollowed));
            }

        }

        if(part == patternParts.size()-1){
            JourneyPattern jp = new JourneyPattern(partsFollowed);
            if(jp.getTransfers() <= minTransfers){
                foundPatterns.add(jp);
                minTransfers = jp.getTransfers();
            }
        }

    }

    private List<List<JourneyPatternPart>> generatePatternParts(List<Stop> travelPoints){
        List<List<JourneyPatternPart>> patternParts = new ArrayList<>();

        for(int i=0; i<travelPoints.size()-1; ++i){
            Stop source = travelPoints.get(i);
            Stop target = travelPoints.get(i+1);

            Map<List<PlatformsEdge>, Set<Line>> pathsAndLines = findPathsAndLinesBetweenStops(source, target);

            List<JourneyPatternPart> currParts = new ArrayList<>();

            for(List<PlatformsEdge> path : pathsAndLines.keySet()){
                currParts.addAll(pathsAndLines.get(path).stream().map(line -> new JourneyPatternPart(path, line))
                        .collect(Collectors.toList()));
            }

            patternParts.add(currParts);
        }

        return patternParts;
    }

    private Map<List<PlatformsEdge>, Set<Line>> findPathsAndLinesBetweenStops(Stop source, Stop target){
        Map<List<PlatformsEdge>, Set<Line>> paths = new HashMap<>();

        Set<Line> commonLines = JourneysFinder.getCommonLines(source, target);

        for(Line line : commonLines){
            List<PlatformsEdge> path = JourneysFinder.findLinePathBetweenStops(line, source, target);

            if(!path.isEmpty()){
                Set<Line> linesOnPath = paths.getOrDefault(path, new HashSet<>());
                linesOnPath.add(line);
                paths.put(path, linesOnPath);
            }
        }


        return paths;
    }


}
