
package pl.rosiakit.finder;

import pl.rosiakit.model.Line;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Stop;

import java.time.LocalTime;
import java.util.Objects;

/**
 *
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

enum STEP_TYPE{
    ENTER_TO_VEHICLE,
    GO_THROUGH_STOP,
    GET_OFF_THE_VEHICLE,
    WALK_TO_ANOTHER_PLATFORM,
    WAIT_ON_THIS_PLATFORM_FOR_VEHICLE,
    END_OF_THE_JOURNEY
}

public class JourneyStep {

    private final STEP_TYPE type;
    private final Line line;
    private final LocalTime time;
    private final Platform platform;

    JourneyStep(STEP_TYPE type, Line line, LocalTime time, Platform platform) {
        this.type = type;
        this.line = line;
        this.time = time;
        this.platform = platform;
    }

    public STEP_TYPE getType() {
        return type;
    }

    public Line getLine() {
        return line;
    }

    public LocalTime getTime() {
        return time;
    }

    public Stop getStop() {
        return getPlatform().getStop();
    }

    public Platform getPlatform(){
        return platform;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + Objects.hashCode(this.line);
        hash = 83 * hash + Objects.hashCode(this.time);
        hash = 83 * hash + Objects.hashCode(this.platform);
        return hash;
    }

    @SuppressWarnings("RedundantIfStatement")
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
        final JourneyStep other = (JourneyStep) obj;
        if (!Objects.equals(this.line, other.line)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        if (!Objects.equals(this.platform, other.platform)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(type == STEP_TYPE.ENTER_TO_VEHICLE){
            sb.append(" >- ");
            sb.append(time).append(" ").append(line.getName()).append(" ");
            sb.append(getStop());
        }
        else if(type == STEP_TYPE.GO_THROUGH_STOP){
            sb.append(" -- ");
            sb.append(time).append(" ");
            sb.append(line.getName()).append(" ");
            sb.append(getStop());
        }
        else if(type == STEP_TYPE.GET_OFF_THE_VEHICLE){
            sb.append(" -> ");
            sb.append(time).append(" ");
            sb.append(line.getName()).append(" ");
            sb.append(getStop());
        }
        else if(type == STEP_TYPE.WAIT_ON_THIS_PLATFORM_FOR_VEHICLE){
            sb.append(" (i) Oczekuj na tym peronie na pojazd linii ");
            sb.append(line.getName());
        }
        else if(type == STEP_TYPE.WALK_TO_ANOTHER_PLATFORM){
            sb.append(" (i) Przejdź na peron ").append(platform.getId());
        }
        else if(type == STEP_TYPE.END_OF_THE_JOURNEY){
            sb.append(" (i) Jesteś u celu");
        }
        else{
            sb.append("JourneyStep{" + "type=").append(type).append(", line=").append(line).append(", time=").append(time).append(", platform=").append(platform.getId()).append('}');
        }
        return sb.toString();
    }

}
