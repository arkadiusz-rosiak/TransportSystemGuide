
package pl.rosiakit.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

@Entity
@Table(name = "routes")
public class Route implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private int direction;
    
    @Column(nullable = false)
    private int ordinalNumber;
    
    @ManyToOne(optional = false)
    @JoinColumn
    private Line line;
    
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @JoinColumn
    private Connection connection;
    
    @Column(nullable = false)
    private LocalDate validTo; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }
    
    public boolean isValid(){
        return this.validTo.compareTo(LocalDate.now()) > 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.id;
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
        final Route other = (Route) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Route{" + "ordinalNumber=" + ordinalNumber + ", line=" 
                + line + ", connection=" + connection + '}';
    }
    
}
