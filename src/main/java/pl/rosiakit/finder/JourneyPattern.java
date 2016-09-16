package pl.rosiakit.finder;

import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.Line;

import java.util.*;

/**
 * This class is containing path and all line that runs on every edge
 */
class JourneyPatternPart{
    List<PlatformsEdge> path;
    Line line;

    JourneyPatternPart(List<PlatformsEdge> path, Line line){
        this.path = path;
        this.line = line;
    }

    @Override
    public String toString() {
        return line.getName();
    }
}

/**
 * Class contains journey's all details but without timetable
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-18
 */
class JourneyPattern {

    private List<PlatformsEdge> path = new ArrayList<>();

    private Map<PlatformsEdge, Line> lines = new HashMap<>();

    private int transfers = 0;

    JourneyPattern(List<JourneyPatternPart> patternParts){

        for(JourneyPatternPart part : patternParts){
            for(PlatformsEdge edge : part.path){
                this.path.add(edge);
                this.lines.put(edge, part.line);
            }
        }

        transfers = new HashSet<>(this.lines.values()).size()-1;
    }

    JourneyPattern(List<PlatformsEdge> path, Map<PlatformsEdge, Line> lines){

        for(PlatformsEdge edge : path){
            if(lines.containsKey(edge)){
                this.path.add(edge);
                this.lines.put(edge, lines.get(edge));
            }
            else
            {
                throw new IllegalArgumentException("Lines map does not contain all path edges!");
            }
        }
    }

    List<PlatformsEdge> getPath() {
        return new ArrayList<>(path);
    }

    int getTransfers() {
        return transfers;
    }

    public Map<PlatformsEdge, Line> getLines() {
        return new HashMap<>(lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JourneyPattern)) return false;

        JourneyPattern that = (JourneyPattern) o;

        return getLines() != null ? getLines().equals(that.getLines()) : that.getLines() == null;

    }

    @Override
    public int hashCode() {
        return getLines() != null ? getLines().hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(PlatformsEdge edge : path){
            sb.append(edge.getSource().getStop().getName());
            sb.append(" -> ");
            sb.append(edge.getTarget().getStop().getName());
            sb.append(": ");
            sb.append(lines.get(edge));
            sb.append("\n");
        }

        return sb.toString();
    }
}
