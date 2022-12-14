package cs1302.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public abstract interface ApiInteractable {
    
    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // enable nice output when printing
            .create(); // builds and returns a Gson object

    public abstract static class ApiRecord{};
    public abstract static class ApiResult{
        transient String userQuery;
    };

    /**
     * Gets endpoint URL.
     * @return the API's endpoint URL
     */
    abstract String getEndpoint();

    /**
     * Gets API name, in the form "[publisher]api".
     * Corresponds with the property names in resources/config.properties.
     * 
     * @return the API's name in said format
     */
    abstract String getApiName();
    
    /**
     * Return an {@code Optional} describing the root element of the JSON
     * response for a "search" query.
     * 
     * @param q query string
     * @return an {@code Optional} describing the root element of the response
     */
    public default <R extends ApiResult> Optional<R> search(String q) {
        System.out.printf("Searching for: %s\n", q);
        System.out.println("This may take some time to download...");
        try {
            String apiName = getApiName();
            String url = String.format(
                    "%1$s"
                    + (apiName.substring(apiName.length() - 1).equals("0") ? "" : "%2$s"),
                    getEndpoint(),
                    "&apikey=" + ApiInteractable.getAPIAuthorization(apiName));
            String json = fetchResponseString(url, q);
            R result = GSON.fromJson(json, new TypeToken<R>(){}.getType());
            result.userQuery = q;
            return Optional.<R>ofNullable(result);
        } catch (IllegalArgumentException | IOException e) {
            return Optional.<R>empty();
        } // try
    } // search

    /**
     * Returns the response body string data from a URL.
     * 
     * @param urlAsString location of desired content as {@code String} type
     * @param q           query string
     * @return response body string
     * @throws IOException if an I/O error occurs when sending or receiving
     * @see <a href="https://curlconverter.com/java/">cURL to Java Converter</a>
     */
    abstract String fetchResponseString(String urlAsString, String q) throws IOException;

    /**
     * 
     * @param apiString
     * @return
     */
    static String[] getAPIAuthorization(String apiString) {
        String configPath = "resources/config.properties";
        Properties config = new Properties();
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            config.load(configFileStream);
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try-catch
        return new String[] {
                config.getProperty(apiString + ".apikey"),
                config.getProperty(apiString + ".apisecret", "")
        };
    } // getAPIKey()

} // ApiInterface