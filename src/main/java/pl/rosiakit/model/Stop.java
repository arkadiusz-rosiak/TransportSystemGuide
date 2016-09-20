
package pl.rosiakit.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

@Entity
@Table(name = "stops")
public class Stop implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(JsonViewsContainer.StopsSummary.class)
    private int id;
    
    @Column(nullable = false)
    @JsonView(JsonViewsContainer.StopsSummary.class)
    private String name;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "stop",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(JsonViewsContainer.StopsDetails.class)
    private Set<Platform> platforms = new HashSet<>();
    
    @Column
    @Deprecated
    private String lat;
    
    @Column
    @Deprecated
    private String lng;

    public Stop() {  }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }    
    
    public void addPlatform(Platform platform){
        this.platforms.add(platform);
    }

    @Deprecated
    public String getLat() {
        for(Platform p : platforms){
            return p.getLat()+"";
        }
        return "0";
    }

    @Deprecated
    public void setLat(String lat) {
        this.lat = lat;
    }

    @Deprecated
    public String getLng() {
        for(Platform p : platforms){
            return p.getLng()+"";
        }
        return "0";
    }

    @Deprecated
    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;

        Stop stop = (Stop) o;

        return getId() == stop.getId();

    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return name;
    }

}
