package cs1302.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaletteCreateApi implements ApiInteractable {

    /**
     * Represents a color, as per the API documentation.
     */
    private static class Color {
        String html_code;
        String closest_palette_color;
    } // Color
    
    /**
     * Represents a color JSON object, as per the API documentation.
     */
    private static class ColorObject {
        Color[] background_colors;
        Color[] image_colors;
        Color[] foreground_colors;
    } // ColorObject

    /**
     * Represents a Get Colors API result.
     */
    private static class PaletteResult extends ApiInteractable.ApiResult {
        ColorObject colors;
    } // ArtResult
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getEndpoint() {
        return "https://api.imagga.com/v2/colors";
    } // getEndpoint()

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApiName() {
        return "imaggaapi0";
    } // getApiName()

    /**
     * {@inheritDoc}
     * 
     * @see <a href="https://docs.imagga.com/?java#colors">
     * Java GET Request for Imagga API Example</a>
     */
    @Override
    public String fetchResponseString(String urlAsString, String image_url) throws IOException {
        String credentialsToEncode = "&lt;" 
            + ApiInteractable.getAPIAuthorization(getApiName())[0]
            + "&gt;" 
            + ":" + "&lt;"
            + ApiInteractable.getAPIAuthorization(getApiName())[1]
            + "&gt;";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        URL url = new URL(String.format("%1$s?image_url=%2$s", urlAsString, image_url));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + basicAuth);

        int responseCode = connection.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader connectionInput = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String jsonResponse = connectionInput.readLine();
        connectionInput.close();
        return jsonResponse;
    } // fetchResponseString(String)

    /**
     * Condenses the colors into an {@code ArrayList}
     * of each of their HTML codes.
     * 
     * @param result JSON Object representing, in particular, five image colors
     * @return an {@code ArrayList} of each color's HTML code as a "#" String
     */
    private static ArrayList<String> condense(PaletteResult result) {
        return Arrays.stream(result.colors.image_colors)
            .map(e -> e.html_code)
            .collect(Collectors.toCollection(ArrayList::new));
    } // coalesce()
    
} // PaletteCreateApi
