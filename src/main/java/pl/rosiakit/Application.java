package pl.rosiakit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pl.rosiakit.bo.*;
import pl.rosiakit.finder.JourneysFinder;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        prepareJourneysFinder(context);
        prepareSchedulesCrawler(context);
    }

    private static void prepareJourneysFinder(ConfigurableApplicationContext context){
        LineBo lineBo = (LineBo) context.getBean("lineBo");
        RouteBo routeBo = (RouteBo) context.getBean("routeBo");
        DepartureBo departureBo = (DepartureBo) context.getBean("departureBo");

        JourneysFinder.prepareData(lineBo, routeBo, departureBo);
    }

    private static void prepareSchedulesCrawler(ConfigurableApplicationContext context){
        PlatformBo platformBo = (PlatformBo) context.getBean("platformBo");
        StopBo stopBo = (StopBo) context.getBean("stopBo");
        ConnectionBo connectionBo = (ConnectionBo) context.getBean("connectionBo");
        LineBo lineBo = (LineBo) context.getBean("lineBo");
        RouteBo routeBo = (RouteBo) context.getBean("routeBo");
        DepartureBo departureBo = (DepartureBo) context.getBean("departureBo");

        SchedulesDownloader.setPlatformBo(platformBo);
        SchedulesDownloader.setStopBo(stopBo);
        SchedulesDownloader.setConnectionBo(connectionBo);
        SchedulesDownloader.setLineBo(lineBo);
        SchedulesDownloader.setRouteBo(routeBo);
        SchedulesDownloader.setDepartureBo(departureBo);
    }

}