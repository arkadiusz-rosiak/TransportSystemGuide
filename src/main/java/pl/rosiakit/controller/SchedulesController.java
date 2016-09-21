package pl.rosiakit.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.rosiakit.SchedulesDownloader;
import pl.rosiakit.crawler.ListType;
import pl.rosiakit.crawler.ScheduleCrawler;
import pl.rosiakit.crawler.ZTMPoznanCrawler;
import pl.rosiakit.finder.JourneysFinder;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-20
 */

@RestController
@CrossOrigin(origins = "*")
public class SchedulesController {

    @RequestMapping("/v1/schedules/update")
    public boolean updateSchedules(@RequestParam(value="name", required = false) String name) {

        SchedulesDownloader downloader = new SchedulesDownloader();
        String[] whiteList = {"14"};
        ScheduleCrawler crawler = new ZTMPoznanCrawler(whiteList, ListType.WHITELIST);

        downloader.saveScheduleToDatabase(crawler);
        JourneysFinder.refreshData();

        return true;
    }
}
