package pl.rosiakit.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-24
 */

@Entity
@Table(name = "cities")
public class City implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @JsonView(JsonViewsContainer.StopsDetails.class)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "city",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Stop> stops;

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

    public Set<Stop> getStops() {
        return stops;
    }

    public void setStops(Set<Stop> stops) {
        this.stops = stops;
    }
}
