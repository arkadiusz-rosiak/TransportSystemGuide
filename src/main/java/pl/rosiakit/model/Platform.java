
package pl.rosiakit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

@Entity
@Table(name = "platforms")
public class Platform implements Serializable{
          
    @Id
    private String id;

    @Column
    private String name;

    @Column
    private float lat;

    @Column
    private float lng;

    @ManyToOne(optional = false)
    @JoinColumn(name="stop")
    private Stop stop;
        
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "platform",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Departure> departures = new HashSet<>();
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        stop.getPlatforms().add(this);
        this.stop = stop;
    }       
    
    public Set<Departure> getDepartures() {
        return departures;
    }

    public void setDepartures(Set<Departure> departures) {
        this.departures = departures;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.id);
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
        final Platform other = (Platform) obj;

        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Platform{" + this.id +'|'+this.stop.getName()+'}';
    }
}
