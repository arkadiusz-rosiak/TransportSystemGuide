package pl.rosiakit.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Stop;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-06
 */
public class StopsEdge extends DefaultWeightedEdge{

    private Set<Line> lines = new HashSet<>();

    public StopsEdge(){}

    public StopsEdge(Set<Line> lines) {
        this.lines.addAll(lines);
    }

    public Set<Line> getLines() {
        return lines;
    }

    public void addAllLines(Set<Line> lines){
        this.lines.addAll(lines);
    }

    public double getTime(){
        return super.getWeight();
    }

    @Override
    public String toString() {
        return this.getSource() + " ["+this.getWeight()+"]> " + this.getTarget();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StopsEdge)) return false;

        StopsEdge other = (StopsEdge) o;
        return this.getSource().equals(other.getSource()) && this.getTarget().equals(other.getTarget());
    }

    @Override
    public int hashCode() {
        int result = getSource() != null ? getSource().hashCode() : 0;
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        return result;
    }

    @Override
    public Stop getSource(){
        return (Stop) super.getSource();
    }

    @Override
    public Stop getTarget(){
        return (Stop) super.getTarget();
    }

}
