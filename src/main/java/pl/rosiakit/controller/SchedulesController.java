package pl.rosiakit.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rosiakit.SchedulesDownloader;
import pl.rosiakit.crawler.KombusCrawler;
import pl.rosiakit.crawler.ListType;
import pl.rosiakit.crawler.ScheduleCrawler;
import pl.rosiakit.crawler.ZTMPoznanCrawler;
import pl.rosiakit.finder.JourneysFinder;
import pl.rosiakit.model.JsonResponse;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-20
 */

@RestController
@CrossOrigin(origins = "*")
public class SchedulesController {

    @RequestMapping("/v1/schedules/update")
    public JsonResponse updateSchedules(@RequestParam(value="crawler", required = true) String crawlerName,
                                        @RequestParam(value="lines", required = true) String lines) {

        SchedulesDownloader downloader = new SchedulesDownloader();

        String[] whiteList = lines.split(",");

        ScheduleCrawler crawler = getScheduleCrawlerFromName(crawlerName, whiteList);

        if(crawler != null){
            try {
                downloader.saveScheduleToDatabase(crawler);
                JourneysFinder.refreshData();
                return new JsonResponse(200, "Schedule has been updated");
            }
            catch(Exception e){
                return new JsonResponse(500, "An error occurred");
            }
        }
        else{
            return new JsonResponse(400, "Provided crawler name is not valid");
        }
    }

    private ScheduleCrawler getScheduleCrawlerFromName(String name, String[] lines){

        if(name.toLowerCase().equals("ztmpoznan")){
            return new ZTMPoznanCrawler(lines, ListType.WHITELIST);
        }

        if(name.toLowerCase().equals("kombus")){
            return new KombusCrawler(lines, ListType.WHITELIST);
        }

        return null;
    }

}
