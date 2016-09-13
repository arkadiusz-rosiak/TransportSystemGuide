
package pl.rosiakit.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class PlatformsEdge extends DefaultWeightedEdge  {
        
    private Set<Line> lines = new HashSet<>();

    public PlatformsEdge(){}

    public PlatformsEdge(Set<Line> lines) {
        this.lines.addAll(lines);
    }

    public Set<Line> getLines() {
        return lines;
    }

    public double getTime(){
        return super.getWeight();
    }

    @Override
    public Platform getSource(){
        return (Platform) super.getSource();
    }

    @Override
    public Platform getTarget(){
        return (Platform) super.getTarget();
    }

    @Override
    public String toString() {        
        return this.getSource() + " ["+this.getWeight()+"]> " + this.getTarget();        
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlatformsEdge)) return false;

        PlatformsEdge other = (PlatformsEdge) o;
        return this.getSource().equals(other.getSource()) && this.getTarget().equals(other.getTarget());
    }

    @Override
    public int hashCode() {
        int result = getSource() != null ? getSource().hashCode() : 0;
        result = 31 * result + (getTarget() != null ? getTarget().hashCode() : 0);
        return result;
    }
}
