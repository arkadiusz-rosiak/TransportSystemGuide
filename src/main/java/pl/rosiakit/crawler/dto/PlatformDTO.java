
package pl.rosiakit.crawler.dto;

import java.util.Objects;

/**
 * Class that represents platform details.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */
public class PlatformDTO {
    public String id;
    public String stopName;
    public String direction;
    public String agencyName;
    public String routeName;
    public String platform;
    public String lat;
    public String lng;
    public int travelTimeToNextPlatform;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.stopName);
        hash = 47 * hash + Objects.hashCode(this.direction);
        hash = 47 * hash + Objects.hashCode(this.agencyName);
        hash = 47 * hash + Objects.hashCode(this.routeName);
        hash = 47 * hash + Objects.hashCode(this.platform);
        hash = 47 * hash + Objects.hashCode(this.lat);
        hash = 47 * hash + Objects.hashCode(this.lng);
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
        final PlatformDTO other = (PlatformDTO) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.stopName, other.stopName)) {
            return false;
        }
        if (!Objects.equals(this.direction, other.direction)) {
            return false;
        }
        if (!Objects.equals(this.agencyName, other.agencyName)) {
            return false;
        }
        if (!Objects.equals(this.routeName, other.routeName)) {
            return false;
        }
        if (!Objects.equals(this.platform, other.platform)) {
            return false;
        }
        if (!Objects.equals(this.lat, other.lat)) {
            return false;
        }
        if (!Objects.equals(this.lng, other.lng)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PlatformDTO{" +
                "id='" + id + '\'' +
                ", stopName='" + stopName + '\'' +
                ", direction='" + direction + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", routeName='" + routeName + '\'' +
                ", platform='" + platform + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", travelTimeToNextPlatform=" + travelTimeToNextPlatform +
                '}';
    }
}

