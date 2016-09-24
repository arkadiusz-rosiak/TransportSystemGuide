
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
    
    @ManyToOne(optional = false)
    @JsonView(JsonViewsContainer.StopsDetails.class)
    private City city;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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
