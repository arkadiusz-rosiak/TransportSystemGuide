package pl.rosiakit.crawler;

import pl.rosiakit.crawler.dto.LineDTO;
import pl.rosiakit.crawler.dto.PlatformDTO;
import pl.rosiakit.crawler.dto.TimetableDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Interface used in SchedulesDownloader. All crawlers have to implements it. It determinates which elements should be
 * downloaded by every single crawler from agency website.
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

public interface ScheduleCrawler {

    /**
     * @return Set of lines (LineDTOs) that can be downloaded by crawler from agency website.
     */
    Set<LineDTO> getLines();
    
    /**
     * @param line lineDTO for which routes you want to download
     * @return Set of line routes. Every single route is represented by ordered list of platforms (platformDTO)
     */
    Set<List<PlatformDTO>> getLineRoutes(LineDTO line);

    /**
     * @param line lineDTO for which stops you want to download
     * @return Set of all stops (PlatformDTO) in all line routes.
     */
    Set<PlatformDTO> getAllStops(LineDTO line);
    
    /**
     * @param platform platformDTO for which timetable you want to download
     * @return TimetableDTO with departures on specific platform
     */
    TimetableDTO getPlatformTimetable(PlatformDTO platform);
    
    /**
     * @return date since when downloaded schedule is valid.
     */
    LocalDate validSince();

}