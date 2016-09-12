
package pl.rosiakit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
@Entity
@Table(name = "connections")
public class Connection implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Platform source;
    
    @ManyToOne
    @JoinColumn
    private Platform target;
    
    @Column
    private long travelTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "connection",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Route> routes = new HashSet<>();

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public int getId() {
        return id;
    }

    public Platform getSource() {
        return source;
    }

    public void setSource(Platform source) {
        this.source = source;
    }

    public Platform getTarget() {
        return target;
    }

    public void setTarget(Platform target) {
        this.target = target;
    }

    public long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(long travelTime) {
        this.travelTime = travelTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
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

        final Connection other = (Connection) obj;

        return this.id == other.id;
    }

    @Override
    public String toString() {        
        return "Connection{"+ source.getId() + " -> " + target.getId() +" (time="+ travelTime +")}";
    }
    
    
}
