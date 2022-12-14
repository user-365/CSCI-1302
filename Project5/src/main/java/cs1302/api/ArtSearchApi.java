package cs1302.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Harvard Art Museums Get Objects API.
 *
 * <p>
 * To run this example on Odin, use the following commands:
 *
 * <pre>
 * $ mvn clean compile
 * $ mvn exec:java -Dexec.mainClass=cs1302.api.ArtSearchApi
 * </pre>
 */
public class ArtSearchApi implements ApiInteractable {
    
    /** Represents an image, as per the API documentation. */
    private static class Image {
        String copyright;
        String alttext;
        String iiifbaseuri;
    } // Image

    /**
     * Represents a Get Objects API record (for a single Object).
     */
    private static class ArtRecord extends ApiInteractable.ApiRecord {
        int id;
        String labeltext;
        Image[] images;
    } // ArtDoc

    /**
     * Represents a Get Objects API result.
     */
    private static class ArtResult extends ApiInteractable.ApiResult {
        int totalrecords;
        ArtRecord[] records;
    } // ArtResult

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndpoint() {
        return "https://api.harvardartmuseums.org/object"
                + "?size=12" // twelve images
                + "&fields=labeltext,url,images" // pare down response
                + "&hasimage=1" // needs to have an image!
                + "&classification=Paintings"; // as opposed to sculptures etc.
    } // getEndpoint()

    @Override
    public String getApiName() {
        return "harvardapi";
    } // getApiName()
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String fetchResponseString(String urlAsString, String q) throws IOException {
        URL url = new URL(urlAsString);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.setRequestProperty("Content-Type", "application/json");
        httpConn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
        writer.write(
                "{\"query\": {\"query_string\": {\"query\":"
                        + "\"(labeltext:" + q
                        + ") AND (imagepermissionlevel:0)\"}}}");
        writer.flush();
        writer.close();
        httpConn.getOutputStream().close();
        InputStream responseStream = httpConn.getResponseCode() == 200
                ? httpConn.getInputStream()
                : httpConn.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        return s.hasNext() ? s.next().trim() : "";
    } // fetchResponseString(String)
    
    /**
     * Standardize the response objects (of type {@code ArtRecord})
     * into a {@code Map}, with key being the object's (Harvard Museums) id,
     * and each value being a {@code Map} itself, containing entries of:
     * <ul>
     * <li>{@code labelContext},</li>
     * <li>{@code imageCopyright},</li>
     * <li>{@code imageAltText},</li>
     * <li>{@code imageIiifFullURI}.</li>
     * </ul>
     * 
     * @param result the Get Objects API result
     * @return the data, standardized and contained in a {@code Map}
     */
    private static Map<Integer, Map<String, String>> standardize(ArtResult result) {
        Map<Integer, Map<String, String>> returnValue = new HashMap<Integer, Map<String, String>>();
        // print how many we found
        System.out.printf("numFound = %d\n", result.totalrecords);
        for (ArtRecord record : result.records) {
            System.out.println(record.id);
            HashMap<String, String> objectDataMap = new HashMap<String, String>();
            objectDataMap.put("labelContext", getSearchTermContext(result.userQuery, record.labeltext));
            objectDataMap.put("imageCopyright", record.images[0].copyright);
            objectDataMap.put("imageAltText", record.images[0].alttext);
            objectDataMap.put("imageIiifSquareURI", record.images[0].iiifbaseuri
                + "/square/300,300/0/default.jpg");
            returnValue.put(record.id, objectDataMap);
        } // for
        return returnValue;
    } // getImageURLs

    /**
     * Returns the search term and its surrounding context.
     * @param searchTerm the search term
     * @param fullText the text to be searched
     * @return the context of the search term
     */
    private static String getSearchTermContext(String searchTerm, String fullText) {
        return "..." + Pattern.compile("(\\w{0,2})\\s" + searchTerm + "\\s\\w{0,4}", 
                Pattern.CASE_INSENSITIVE).matcher(fullText).group() + "...";
    } // getSearchTermContext()

} // ArtSearchApi
