package pl.rosiakit.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

@Entity
@Table(name = "`lines`")
public class Line implements Serializable, Comparable{
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    @JsonView(JsonViewsContainer.LinesSummary.class)
    private String name;
        
    @Column(nullable = false)
    private LocalDate validSince;

    @Column(nullable = false)
    @JsonView(JsonViewsContainer.LinesSummary.class)
    private String agencyName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonView(JsonViewsContainer.LinesSummary.class)
    private VehicleType type;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String line) {
        this.name = line;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public LocalDate getValidSince() {
        return validSince;
    }

    public void setValidSince(LocalDate scheduleDate) {
        this.validSince = scheduleDate;
    }

    public void setValidSince(int y, int m, int d) {
        this.setValidSince(LocalDate.of(y, m, d));
    }

    @Override
    public int compareTo(Object o) {
        Line other = (Line) o;

        if(other.getName().length() != this.getName().length()){
            return this.getName().length() - other.getName().length();
        }

        return this.getName().compareToIgnoreCase(other.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;

        Line line = (Line) o;

        if (getName() != null ? !getName().equals(line.getName()) : line.getName() != null) return false;
        return getAgencyName() != null ? getAgencyName().equals(line.getAgencyName()) : line.getAgencyName() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getAgencyName() != null ? getAgencyName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Line{" + name + '}';
    }

        
}
