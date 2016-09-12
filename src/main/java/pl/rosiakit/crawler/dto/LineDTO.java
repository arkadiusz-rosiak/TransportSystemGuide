
package pl.rosiakit.crawler.dto;

import java.util.Objects;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class LineDTO{

    /**
     * Line stopName
     */
    public String name;

    /**
     * Line type (tram, bus, etc)
     */
    public VehicleType type;

    /**
     * Name of the agency that runs the line
     */
    public String agencyName;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.name);
        hash = 73 * hash + Objects.hashCode(this.type);
        hash = 73 * hash + Objects.hashCode(this.agencyName);
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
        final LineDTO other = (LineDTO) obj;

        return Objects.equals(this.name, other.name) &&
                Objects.equals(this.type, other.type) &&
                Objects.equals(this.agencyName, other.agencyName);
    }

    @Override
    public String toString() {
        return "LineDTO{" + "stopName=" + name + ", type=" + type + ", agencyName=" + agencyName + '}';
    }
}
