
package pl.rosiakit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
@Entity
@Table(name = "departures")
public class Departure implements Serializable, Comparable<Departure>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Line line;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Platform platform;
    
    @Column(nullable = false)
    private LocalTime departureTime;
    

    @Column(nullable = false)
    private int dayType;

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }

    public int getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.id;
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
        final Departure other = (Departure) obj;
        return this.id == other.id;
    }

    @Override
    public int compareTo(Departure other){
        return departureTime.compareTo(other.getDepartureTime());
    }
    
    @Override
    public String toString() {
        return "Departure{" + "platform=" + platform.getId()
                + ", line=" + line + ", time=" + departureTime + '}';
    }
    
    
}
