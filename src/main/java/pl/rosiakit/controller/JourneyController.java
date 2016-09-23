package pl.rosiakit.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.*;
import pl.rosiakit.bo.StopBo;
import pl.rosiakit.finder.Journey;
import pl.rosiakit.finder.JourneysFinder;
import pl.rosiakit.model.DayType;
import pl.rosiakit.model.JsonResponse;
import pl.rosiakit.model.JsonViewsContainer;
import pl.rosiakit.model.Stop;

import javax.inject.Inject;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */

@RestController
@CrossOrigin(origins = "*")
public class JourneyController {

    private final StopBo stopBo;

    @Inject
    public JourneyController(StopBo stopBo) {
        this.stopBo = stopBo;
    }

    @RequestMapping("/v1/journey/{source}/{target}")
    @JsonView(JsonViewsContainer.JourneyView.class)
    public JsonResponse findJourneys(@PathVariable int source, @PathVariable int target,
                                     @RequestParam(value="h", required = false) String hour,
                                     @RequestParam(value="m", required = false) String minutes,
                                     @RequestParam(value="daytype", required = false) String daytype) {

        JourneysFinder finder = new JourneysFinder();

        Stop sourceStop = stopBo.findStopById(source);
        Stop targetStop = stopBo.findStopById(target);

        if(sourceStop == null || targetStop == null || sourceStop.equals(targetStop)){
            return new JsonResponse(400, "Podane przystanki nie są prawidłowe");
        }

        finder.setDepartureTime(getDepartureTimeFromRequestParam(hour, minutes));
        finder.setDaytype(getDaytypeFromRequestParam(daytype));

        List<Journey> journeysFound = finder.findJourneys(sourceStop, targetStop);

        if(!journeysFound.isEmpty()){
            return new JsonResponse(200, journeysFound);
        }
        else{
            return new JsonResponse(404, "Droga nie zostala odnaleziona");
        }
    }

    private DayType getDaytypeFromRequestParam(String daytype){

        if(daytype == null){
            return DayType.WEEKDAY;
        }

        if(daytype.toLowerCase().equals("weekday")){
            return DayType.WEEKDAY;
        }

        if(daytype.toLowerCase().equals("saturday")){
            return DayType.SATURDAY;
        }

        if(daytype.toLowerCase().equals("holiday")){
            return DayType.HOLIDAY;
        }

        return DayType.WEEKDAY;
    }

    private LocalTime getDepartureTimeFromRequestParam(String hour, String minutes){
        try{
            int h = parseHourFromString(hour);
            int m = parseMinutesFromString(minutes);

            return LocalTime.of(h, m);
        }
        catch(IllegalArgumentException e){
            return LocalTime.now();
        }
    }

    private int parseHourFromString(String hour){
        try{
            int h = Integer.parseInt(hour);
            if(h >= 0 && h < 24){
                return h;
            }
            else{
                throw new IllegalArgumentException("Hour value must be >= 0 and < 24");
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("Hour value must be >= 0 and < 24");
        }
    }

    private int parseMinutesFromString(String minutes){
        try{
            int m = Integer.parseInt(minutes);
            if(m >= 0 && m < 60){
                return m;
            }
            else{
                throw new IllegalArgumentException("Minutes value must be >= 0 and < 60");
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("Minutes value must be >= 0 and < 60");
        }
    }

}
