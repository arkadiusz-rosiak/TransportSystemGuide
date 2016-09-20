package pl.rosiakit.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rosiakit.bo.StopBo;
import pl.rosiakit.finder.Journey;
import pl.rosiakit.finder.JourneysFinder;
import pl.rosiakit.model.JsonResponse;
import pl.rosiakit.model.JsonViewsContainer;
import pl.rosiakit.model.Stop;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-19
 */

@RestController
public class JourneyController {

    private final StopBo stopBo;

    @Inject
    public JourneyController(StopBo stopBo) {
        this.stopBo = stopBo;
    }

    @RequestMapping("/v1/journey/{source}/{target}")
    @JsonView(JsonViewsContainer.JourneyView.class)
    public JsonResponse  findJourneys(@PathVariable int source, @PathVariable int target) {

        JourneysFinder finder = new JourneysFinder();
        Stop sourceStop = stopBo.findStopById(source);
        Stop targetStop = stopBo.findStopById(target);


        List<Journey> routes = new ArrayList<>(finder.findJourneys(sourceStop, targetStop));


        return new JsonResponse(200, routes);

    }

}
