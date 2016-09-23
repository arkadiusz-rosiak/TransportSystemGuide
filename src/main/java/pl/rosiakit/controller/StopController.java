package pl.rosiakit.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import pl.rosiakit.bo.PlatformBo;
import pl.rosiakit.bo.StopBo;
import pl.rosiakit.model.JsonResponse;
import pl.rosiakit.model.JsonViewsContainer;
import pl.rosiakit.model.Platform;
import pl.rosiakit.model.Stop;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */

@RestController
@CrossOrigin(origins = "*")
public class StopController {

    private StopBo stopBo;
    private PlatformBo platformBo;

    @Inject
    public StopController(StopBo stopBo, PlatformBo platformBo) {
        Assert.notNull(stopBo, "stopBo must not be null");
        Assert.notNull(platformBo, "platformBo must not be null");

        this.stopBo = stopBo;
        this.platformBo = platformBo;
    }

    @RequestMapping("/v1/stops")
    @JsonView(JsonViewsContainer.StopsDetails.class)
    public JsonResponse findStopsByName(@RequestParam(value="name", required = false) String name,
                                        @RequestParam(value="containing", required = false) String containing) {
        if(name != null){
            return new JsonResponse(200, stopBo.findStopsByName(name));
        }
        else if(containing != null){
            return new JsonResponse(200, stopBo.findStopsContainingName(containing));
        }
        else{
            return new JsonResponse(200, stopBo.findAllStops());
        }
    }

    @RequestMapping("/v1/stops/{id}")
    @JsonView(JsonViewsContainer.StopsDetails.class)
    public JsonResponse findStopById(@PathVariable int id) {
        Stop stop = stopBo.findStopById(id);

        if(stop != null){
            return new JsonResponse(200, stop);
        }
        else{
            return new JsonResponse(404, "Stop does not exists");
        }
    }


    @RequestMapping("/v1/stops/nearest/{lat}/{lng:.+}")
    @JsonView(JsonViewsContainer.StopsWithDistances.class)
    public JsonResponse findNearestStops(@PathVariable float lat, @PathVariable float lng) {

        List<Platform> nearestPlatforms = platformBo.findPlatformsNearestToPoint(lat, lng, 1000);

        List<Stop> stops = new ArrayList<>();
        nearestPlatforms.stream().filter(p -> !stops.contains(p.getStop())).forEach(p -> stops.add(p.getStop()));

        if(!stops.isEmpty()){
            return new JsonResponse(200, stops);
        }
        else{
            return new JsonResponse(404, "No stops found in the nearest (1km) area.");
        }
    }

}
