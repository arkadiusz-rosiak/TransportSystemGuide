package pl.rosiakit.model;

/**
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 * @date 2016-09-16
 */
public class JsonViewsContainer {

    public interface ResponseView{}

    public interface LinesSummary extends ResponseView{}

    public interface StopsSummary extends ResponseView{}

    public interface PlatformsSummary extends ResponseView{}

    public interface StopsDetails extends StopsSummary, PlatformsSummary{}

    public interface StopsWithDistances extends StopsDetails{}

    public interface JourneyView extends StopsSummary, PlatformsSummary, LinesSummary{}
}
