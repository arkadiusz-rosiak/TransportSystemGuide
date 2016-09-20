package pl.rosiakit.finder;

import com.fasterxml.jackson.annotation.JsonView;
import pl.rosiakit.graph.PlatformsEdge;
import pl.rosiakit.model.JsonViewsContainer;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-08-18
 */
public class Journey implements Comparable{

    private List<LocalTime> departures = new ArrayList<>();

    @JsonView(JsonViewsContainer.JourneyView.class)
    private Platform source;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private Platform target;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private int travelTime;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private LocalTime departureTime;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private LocalTime arrivalTime;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private boolean isDirect;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private int comfortIndex;

    @JsonView(JsonViewsContainer.JourneyView.class)
    private List<JourneyStep> steps = new ArrayList<>();

    private final JourneyPattern pattern;

    Journey(JourneyPattern pattern, List<LocalTime> departures){
        if(pattern.getLines().size() == departures.size()){
            this.pattern = pattern;
            this.departures.addAll(departures);
            this.splitJourneyIntoSteps();

            this.source = this.getSourcePlatform();
            this.target = this.getTargetPlatform();
            this.travelTime = this.getTravelTime();
            this.departureTime = this.getDepartureTime();
            this.arrivalTime = this.getArrivalTime();
            this.isDirect = this.isDirect();
            this.comfortIndex = (int) Math.round(this.getComfortIndex());
        }
        else{
            throw new IllegalArgumentException("Departures list size must be equal to line map size");
        }
    }

    private void splitJourneyIntoSteps(){
        JourneyStep prevStep = null;
        JourneyStep currStep;
        Platform prevTargetPlatform = null;
        PlatformsEdge prevEdge = null;

        for(PlatformsEdge edge : getPath()){
            Line line = getLineOnGraphEdge(edge);
            LocalTime departureTime = getDepartureTimeOnGraphEdge(edge);

            Platform sourcePlatform = edge.getSource();

            boolean samePlatform = true;
            if(prevTargetPlatform != sourcePlatform){
                samePlatform = false;
            }

            if(prevStep == null){
                currStep = new JourneyStep(STEP_TYPE.ENTER_TO_VEHICLE, line, departureTime, sourcePlatform);
                steps.add(currStep);
            }
            else if(prevStep.getLine() != line && !samePlatform){
                LocalTime arrivalTime = getArrivalTimeOnGraphEdge(prevEdge);
                currStep = new JourneyStep(STEP_TYPE.GET_OFF_THE_VEHICLE, prevStep.getLine(), arrivalTime, prevTargetPlatform);
                steps.add(currStep);

                currStep = new JourneyStep(STEP_TYPE.WALK_TO_ANOTHER_PLATFORM, line, arrivalTime, sourcePlatform);
                steps.add(currStep);

                currStep = new JourneyStep(STEP_TYPE.ENTER_TO_VEHICLE, line, departureTime, sourcePlatform);
                steps.add(currStep);
            }
            else if(prevStep.getLine() != line && prevStep.getType() != STEP_TYPE.WALK_TO_ANOTHER_PLATFORM){
                LocalTime arrivalTime = getArrivalTimeOnGraphEdge(prevEdge);
                currStep = new JourneyStep(STEP_TYPE.GET_OFF_THE_VEHICLE, prevStep.getLine(), arrivalTime, prevTargetPlatform);
                steps.add(currStep);

                currStep = new JourneyStep(STEP_TYPE.WAIT_ON_THIS_PLATFORM_FOR_VEHICLE, line, departureTime, sourcePlatform);
                steps.add(currStep);

                currStep = new JourneyStep(STEP_TYPE.ENTER_TO_VEHICLE, line, departureTime, sourcePlatform);
                steps.add(currStep);
            }
            else{
                currStep = new JourneyStep(STEP_TYPE.GO_THROUGH_STOP, line, departureTime, sourcePlatform);
                steps.add(currStep);
            }

            prevTargetPlatform = edge.getTarget();
            prevStep = currStep;
            prevEdge = edge;
        }

        PlatformsEdge lastEdge = getLastEdge();

        LocalTime arrivalTime = this.getArrivalTimeOnGraphEdge(lastEdge);
        Platform lastPlatform = lastEdge.getTarget();

        if(prevStep != null){
            currStep = new JourneyStep(STEP_TYPE.GET_OFF_THE_VEHICLE, prevStep.getLine(), arrivalTime, lastPlatform);
            steps.add(currStep);

            currStep = new JourneyStep(STEP_TYPE.END_OF_THE_JOURNEY, prevStep.getLine(), arrivalTime, lastPlatform);
            steps.add(currStep);
        }
    }


    public List<JourneyStep> getSteps(){
        return this.steps;
    }

    private List<PlatformsEdge> getPath(){
        return this.pattern.getPath();
    }

    public Map<PlatformsEdge, Line> getLines(){
        return this.pattern.getLines();
    }

    private Line getLineOnGraphEdge(PlatformsEdge edge){
        return getLines().get(edge);
    }


    public int getTravelTime(){
        try{
            LocalTime departure = getDepartureTime();
            LocalTime arrival = getArrivalTime();

            return (int) Duration.between(departure, arrival).toMinutes();
        }
        catch(Exception e){
            System.err.println("getTravelTime(): "+e);
            return Integer.MAX_VALUE;
        }
    }

    public LocalTime getDepartureTime(){
        return departures.get(0);
    }

    public LocalTime getArrivalTime(){
        return getArrivalTimeOnGraphEdge(getLastEdge());
    }

    private LocalTime getDepartureTimeOnGraphEdge(PlatformsEdge edge){
        int index = getPath().indexOf(edge);

        if(index >= 0){
            return departures.get(index);
        }
        else{
            throw new IllegalArgumentException("Path does not contain this edge");
        }
    }

    private LocalTime getArrivalTimeOnGraphEdge(PlatformsEdge edge){
        try{
            LocalTime departure = getDepartureTimeOnGraphEdge(edge);
            return departure.plusMinutes((long) edge.getTime());
        }
        catch (Exception e){
            throw new IllegalArgumentException("Path does not contain this edge");
        }
    }

    public JourneyPattern getPattern(){
        return this.pattern;
    }

    public Platform getSourcePlatform(){
        return pattern.getPath().get(0).getSource();
    }

    public Platform getTargetPlatform(){
        return getLastEdge().getTarget();
    }

    private PlatformsEdge getLastEdge(){
        return getPath().get(getPath().size()-1);
    }

    public boolean isDirect(){
        Line prevLine = pattern.getLines().get(pattern.getPath().get(0));
        for(int i = 1; i < pattern.getPath().size(); ++i){
            Line currLine = pattern.getLines().get(pattern.getPath().get(i));
            if(currLine != prevLine){
                return false;
            }
            prevLine = currLine;
        }

        return true;
    }

    public double getComfortIndex(){
        return getTransfersCount();
    }

    private int getLinesCount(){
        Set<Line> uniqueLines = new HashSet<>(getLines().values());
        return uniqueLines.size();
    }

    private double getTransfersCount(){
        return getLinesCount()-1;
    }

    private int getEdgesCount(){
        return getPath().size();
    }

    @Override
    // jesli koniec podrozy jest wczesniej, to podroz jest "mniejsza"
    public int compareTo(Object obj) {

        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            return -1;
        }
        if (getClass() != obj.getClass()) {
            return -1;
        }

        final Journey other = (Journey) obj;

        if(!this.getArrivalTime().equals(other.getArrivalTime())){
            return this.getArrivalTime().compareTo(other.getArrivalTime());
        }

        if(this.getTransfersCount() != other.getTransfersCount()){
            return (int) this.getTransfersCount() - (int) other.getTransfersCount();
        }

        if(this.getPath().size() != other.getPath().size()){
            return this.getPath().size() - other.getPath().size();
        }

        return other.getDepartureTime().compareTo(this.getDepartureTime()); // !! DESC
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journey)) return false;

        Journey journey = (Journey) o;

        if (departures != null ? !departures.equals(journey.departures) : journey.departures != null) return false;
        return getPattern() != null ? getPattern().equals(journey.getPattern()) : journey.getPattern() == null;

    }

    @Override
    public int hashCode() {
        int result = departures != null ? departures.hashCode() : 0;
        result = 31 * result + (getPattern() != null ? getPattern().hashCode() : 0);
        return result;
    }
}
