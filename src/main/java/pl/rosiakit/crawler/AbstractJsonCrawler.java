
package pl.rosiakit.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.*;

/**
 * This abstract class helps with fetching data from JSON resources. Stores downloaded website data as cache
 * @author Arkadiusz Rosiak (http://www.rosiak.it)
 */

abstract class AbstractJsonCrawler {
    /**
     * Type of the list stored in this.list
     */
    ListType listType = ListType.BLACKLIST;
    
    /**
     * Contains white/blacklist
     */
    protected List<String> list;

    protected LocalDate date = LocalDate.now();
    
    private final Map<URL, String> cache;
    
    private Set<String> notDownloadedUrls = new HashSet<>();
    
    /**
     * @param list String array with line names as elements that should (not) be downloaded (white/blacklist).
     * @param type Type of the list. Whitelist or blacklist.
     */
    AbstractJsonCrawler(String[] list, ListType type){
        this.cache = new HashMap<>();
        this.list = new ArrayList<>();
        this.listType = type;

        Collections.addAll(this.list, list);
    }
    
    AbstractJsonCrawler(){
        this.cache = new HashMap<>();
        this.list = new ArrayList<>();    
    }
    
    /**
     * @param list String array with line names as elements that should (not) be downloaded (white/blacklist).
     * @param type Type of the list. Whitelist or blacklist.
     * @param date date for which you want to download schedule
     */
    AbstractJsonCrawler(String[] list, ListType type, LocalDate date){
        this(list, type);
        this.date = date;
    }
    
    /**
     * @param date date for which you want to download schedule
     */
    AbstractJsonCrawler(LocalDate date){
        this();
        this.date = date;
    }

    /**
     * @return date for which you want to download schedule
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Method that first tries to fetch website data from cache. If url is not present in cache function visit website
     * and put data into cache.
     * @param url URL object with correct url.
     * @return website content as String
     */
    String readUrl(URL url){
        String fromCache = this.cache.get(url);

        if(fromCache != null){
            return fromCache;
        }
        else if(url != null){
            URLConnection conn = this.openURLConnection(url);
            String lines = this.getAllLinesFromURL(conn);

            cache.put(url, lines);
            return lines;
        }
        else{
            return "";
        }
    }
    
    private URLConnection openURLConnection(URL url){
        try{
            return url.openConnection();
        }
        catch(IOException e){
            this.handleDownloadingURLException(url.toString());
            return null;
        }
    }
    
    private String getAllLinesFromURL(URLConnection conn){
        
        StringBuilder sb = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))){            
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        catch(IOException e){
            this.handleDownloadingURLException(conn.getURL().toString());
        }
        
        return sb.toString();
    }

    /**
     * Method that convert string into correct GSON JsonElement
     * @param webContent downloaded content as string
     * @return GSON JsonElement or NULL if string is not valid json string
     */
    JsonElement getWebContentAsJsonElement(String webContent){
        try{
            return new JsonParser().parse(webContent);
        }
        catch(Exception e){
            System.err.println("Error in parsing json string into JsonElement");
            System.err.println(webContent);
            return null;
        }        
    }

    /**
     * Method that fetch specific child from json as string
     * @param attribute attribute (child) name
     * @param element json element that contains provided attribute
     * @return attribute value as string or empty string when error occurred
     */
    String getJsonChildAsString(String attribute, JsonElement element){
        String value = "";
        
        JsonElement el = this.getJsonChildAsJsonElement(attribute, element);
        
        if(el != null && !el.isJsonNull()){
            value = el.getAsString();
        }
        
        return value;
    }

    /**
     * Method that fetch specific child from json as integer
     * @param attribute attribute (child) name
     * @param element json element that contains provided attribute
     * @return attribute value as int or Integer.MIN_VALUE when error occurred
     */
    int getJsonChildAsInt(String attribute, JsonElement element){
        int value = Integer.MIN_VALUE;
        
        JsonElement el = this.getJsonChildAsJsonElement(attribute, element);
        
        if(el != null && !el.isJsonNull()){
            try{
                value = el.getAsInt();
            }
            catch(NumberFormatException e){
                System.err.println(e.getMessage());
            }
        }
        
        return value;
    }

    /**
     * Method that fetch specific children from json as JsonArray
     * @param attribute attribute (child) name
     * @param element json element that contains provided attribute
     * @return attribute children as JsonArray
     */
    JsonArray getJsonChildAsArray(String attribute, JsonElement element){
        JsonArray value = new JsonArray();
        
        JsonElement el = this.getJsonChildAsJsonElement(attribute, element);
        
        if(el != null && !el.isJsonNull()){
            value = el.getAsJsonArray();
        }
        
        return value;
    }


    /**
     * Method that fetch specific child from json as JsonElement
     * @param attribute attribute (child) name
     * @param element json element that contains provided attribute
     * @return attribute value as JsonElement
     */
    JsonElement getJsonChildAsJsonElement(String attribute, JsonElement element){
        if(element != null && element.isJsonObject()){
            return element.getAsJsonObject().get(attribute);
        }
        else{
            return null;
        }
    }

    /**
     * Method that creates URL object from given website address
     * @param address website address
     * @return URL object created from given website address
     */
    URL createURLFromAddress(String address){
        try{
            return new URL(address);
        }
        catch(MalformedURLException e){
            this.handleDownloadingURLException(address);
            return null;
        }
    }
    
    private void handleDownloadingURLException(String address){
        System.err.println("!! Downloading content failed for website: " + address);
        this.notDownloadedUrls.add(address);
    }
    
    public Set<String> getNotDownloadedUrl(){
        return this.notDownloadedUrls;
    }
}
