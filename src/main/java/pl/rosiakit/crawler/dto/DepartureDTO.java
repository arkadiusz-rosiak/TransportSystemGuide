
package pl.rosiakit.crawler.dto;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class represents departure from platform
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class DepartureDTO implements Comparable{

    /**
     * Departure time
     */
    public LocalTime time;

    /**
     * Notes to this departure (eg. low floor vehicle)
     */
    public Set<String> annotations = new HashSet<>();

    @Override
    public int compareTo(Object o) {    
        DepartureDTO other = (DepartureDTO) o;        
        return this.time.compareTo(other.time);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.time);
        hash = 47 * hash + Objects.hashCode(this.annotations);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DepartureDTO other = (DepartureDTO) obj;

        return Objects.equals(this.time, other.time) && Objects.equals(this.annotations, other.annotations);
    }

    @Override
    public String toString() {
        return time + "" + annotations;
    }    
}
