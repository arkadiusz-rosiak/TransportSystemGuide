package pl.rosiakit.finder;

import pl.rosiakit.bo.DepartureBo;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.*;

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

    private static DepartureBo departureBo;

    private int minTransfers = Integer.MAX_VALUE;

    private Stop source;

    private Stop target;

    private Set<Line> blacklist;

    private JourneyFactory(Set<Line> blacklist){
        if(departureBo == null){
            throw new NullPointerException("You must set DepartureBo before using JourneyFactory");
        }

        this.blacklist = blacklist;
    }

    static void setDepartureBo(DepartureBo departureBo){
        JourneyFactory.departureBo = departureBo;
    }

    public void setSource(Stop source) {
        this.source = source;
    }

    public void setTarget(Stop target) {
        this.target = target;
    }

    static Journey createNextJourneyAfter(Journey journey){
        LocalTime journeyDeparture = journey.getDepartureTime();
        journeyDeparture = journeyDeparture.plusMinutes(1);

        JourneyPattern pattern = journey.getPattern();
        return createJourneyBasedOn(pattern, journeyDeparture, journey.getDaytype());
    }

    static Journey createJourneyBasedOn(JourneyPattern pattern, LocalTime departureTime, DayType daytype){

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

                List<Departure> departures
                        = departureBo.findLineDeparturesAfter(platform, daytype.getValue(), line, prevDeparture);

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

        return new Journey(pattern, departureTimes, daytype);
    }

    static Set<JourneyPattern> preparePatternsViaPoints(Stop source, Stop target,
                                                        List<Stop> travelPoints, Set<Line> blacklist){
        JourneyFactory jf = new JourneyFactory(blacklist);
        jf.setSource(source);
        jf.setTarget(target);
        jf.generatePatternsViaPoints(travelPoints);
        return jf.getFoundPatterns();
    }

    private Set<JourneyPattern> getFoundPatterns(){
        return new HashSet<>(foundPatterns);
    }

    private void generatePatternsViaPoints(List<Stop> travelPoints){
        this.tryToFindNonStopJourney(source, target);
        this.findCombinedPatterns(travelPoints);
        this.retainOnlyCorrectPatterns();
    }

    private void retainOnlyCorrectPatterns(){
        Set<JourneyPattern> correctPatterns = this.foundPatterns.stream().filter(this::isCorrectPattern)
                .collect(Collectors.toSet());

        foundPatterns.retainAll(correctPatterns);
    }

    private boolean isCorrectPattern(JourneyPattern pattern){
        return isLengthMoreThanZero(pattern) && hasStartInSource(pattern) && hasEndInTarget(pattern);
    }

    private boolean isLengthMoreThanZero(JourneyPattern pattern){
        return pattern.getPath().size() > 0;
    }

    private boolean hasStartInSource(JourneyPattern pattern){
        return pattern.getPath().get(0).getSource().getStop().equals(source);
    }

    private boolean hasEndInTarget(JourneyPattern pattern){
        int lastIndex = pattern.getPath().size()-1;
        return pattern.getPath().get(lastIndex).getTarget().getStop().equals(target);
    }

    private void tryToFindNonStopJourney(Stop source, Stop target){
        Set<Line> commonLines = JourneysFinder.getCommonLines(source, target);

        commonLines.removeAll(blacklist);

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
        travelPoints.add(0, this.source);
        travelPoints.add(this.target);
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

        commonLines.removeAll(blacklist);

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
