package cs1302.gallery;

// HTTPS
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// JSON/GSON
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

// JavaFX
import javafx.application.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.*;

// UI
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;

// Other
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an iTunes Gallery App.
 * 
 * <p>
 * Last substantial modification: 29-11-2022, 22:14 EST
 * 
 * @author Yitao Tian
 */
public class GalleryApp extends Application {

    /**
     * Enum for the current (running) state of the application.
     * Purely for humans (i.e. the app doesn't
     * make decisions based on current state).
     */
    private enum State {
        INITIAL, // only set to INITIAL once in app lifetime!
        AFTER_DOWNLOAD, // intended to be default state (of inactivity)
        DURING_DOWNLOAD, // state of activity
        PLAYING; // state of activity
    } // state (enum)

    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL) // always redirects, except from HTTPS to HTTP
            .build(); // builds and returns a HttpClient object
    private HttpRequest request;

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // enable nice output when printing
            .create(); // builds and returns a Gson object

    private Stage stage;
    private Scene scene;
    private VBox root;

    private HBox searchBar; // Search Bar
    private Button playButton;
    private TextField queryField;
    private final Label queryFieldLabel = new Label("Search:", queryField);
    private ComboBox<String> queryDropDown;
    private final Label queryDropDownLabel = new Label("Media Type:", queryDropDown);
    private final ObservableList<String> mediaTypes = FXCollections.observableList(
            Arrays.asList("movie",
                    "podcast",
                    "music",
                    "musicVideo",
                    "audiobook",
                    "shortFilm",
                    "tvShow",
                    "software",
                    "ebook",
                    "all"));
    private Button getButton;

    private Label message; // Message to user
    private final Map<String, Label> messages = Map.ofEntries(
            Map.entry("BEFORE_DOWNLOAD",
                    new Label("Type in a term, select a media type, then click the button.")),
            Map.entry("DURING_DOWNLOAD",
                    new Label("Getting images...")),
            Map.entry("AFTER_DOWNLOAD",
                    new Label("URL_PLACEHOLDER")),
            Map.entry("ERROR",
                    new Label("Last attempt to get images failed...")));

    private TilePane mainContent; // Main Content
    private List<ImageView> imageList; // List of all found images
    private Map<Boolean, List<ImageView>> imageGroups; // Partition of imageLists
    private final Image defaultImage = new Image("file:resources/default.png");

    private HBox statusBar; // Status Bar
    private ProgressBar progressBar; // TK(future) add label for %?
    private final Label itunesAttribution = new Label("Images provided by the iTunes Search API");

    private ObjectProperty<State> state;

    private final Thread apiThread = new Thread(
            new Runnable() {

                private ItunesResponse itunesResponse;

                /** Override so that it does downloading. */
                @Override
                public void run() { // Get Images
                    getButton.setDisable(true); // disabled
                    // format query URL
                    formatQueryURL();
                    // accept response into JSON
                    try {
                        HttpResponse<String> response = HTTP_CLIENT.send(request,
                                BodyHandlers.ofString());
                        if (response.statusCode() != 200) { // check response
                            throw new IOException(response.toString());
                        } // if
                        itunesResponse = GSON.<ItunesResponse>fromJson(response.body().trim(),
                                ItunesResponse.class);
                        if (itunesResponse.resultCount < 21) { // not enough images (nothing downloaded)
                            complainAndReset(); // alert the user and stuff
                        } else {
                            // download images && put into Imageviews
                            downloadIntoImageViews(itunesResponse);
                            // After downloading, update UI elements
                            updateUIAfterDownload();
                        } // if-else
                        state.set(State.AFTER_DOWNLOAD); // enables buttons and fills progress bar
                    } catch (IOException | InterruptedException | JsonSyntaxException e) {
                        System.err.println(e);
                        e.printStackTrace();
                    } // try-catch
                } // run

                /** Alert the user, and afterwards reset button abilities. */
                private void complainAndReset() {
                    String errorMessage = "URI: " + request.toString() + "\n" + // URL of failed query
                            "Exception: java.lang.IllegalArgumentException: "
                            + itunesResponse.resultCount + "distinct results found, "
                            + "but 21 or more are needed."; // toString of related exception
                    // source:
                    // https://openjfx.io/javadoc/17/javafx.controls/javafx/scene/control/Alert.html
                    new Alert(AlertType.ERROR, errorMessage).showAndWait()
                            .filter(r -> r == ButtonType.OK)
                            .ifPresent(r -> {
                                // ignore `r` (i.e. clicking of button by user)
                                if (imageList.stream()
                                        .anyMatch(i -> i.getImage().equals(defaultImage))) {
                                    // do NOT re-enable buttons
                                    reinitializeButtons();
                                } // if
                                message = messages.get("ERROR");
                                // ^update to show error message, after alert window closed
                            }); // Alert()...ifPresent()
                } // complain

                /** Update UI elements after a <strong>successful</strong> download. */
                private void updateUIAfterDownload() {
                    message = messages.get("AFTER_DOWNLOAD");
                    // message replaced w/ query URL used
                    message.setText(request.toString());
                    // displayed images updated, with first 21 images downloaded
                    imageGroups = imageList.stream()
                            .collect(Collectors.partitioningBy(s -> imageList.indexOf(s) <= 20));
                    // Update ALL downloaded images AT ONCE
                    mainContent.getChildren().setAll(imageGroups.get(true));
                } // updateUIAfterDownload

                /**
                 * Take image URIs, create {@code Image}s from said URIs,
                 * then populate the {@code ImageView}s with said {@code Image}s.
                 * 
                 * @param ir represents the iTunes API's response, containing relevant
                 *           name-values
                 */
                private void downloadIntoImageViews(ItunesResponse ir) {
                    updateProgress(0.0);
                    for (int i = 0; i < ir.resultCount; i++) {
                        // source:
                        // https://openjfx.io/javadoc/17/javafx.graphics/javafx/scene/image/ImageView.html
                        imageList.get(i).setImage(new Image(ir.results[i].artworkUrl100));
                        imageList.get(i).setFitWidth(256);
                        imageList.get(i).setPreserveRatio(true);
                        imageList.get(i).setSmooth(true);
                        imageList.get(i).setCache(true);
                        // progress bar loading%...
                        updateProgress(1.0 * i / ir.resultCount);
                    } // for
                } // downloadIntoImageViews

                /** Format the URL used to query the iTunes API. */
                private void formatQueryURL() {
                    String term = URLEncoder.encode(queryField.getText(), StandardCharsets.UTF_8);
                    String media = URLEncoder.encode(queryDropDown.getValue(), StandardCharsets.UTF_8);
                    String query = "?term=%1$s&media=%2$smedia=200".formatted(term, media);
                    request = HttpRequest.newBuilder()
                            .uri(URI.create("https://itunes.apple.com/search" + query))
                            .build();
                } // formatQueryURL
            } // new Runnable
    ); // API_THREAD = new Thread

    private ChangeListener<State> runStateChangeListener = new ChangeListener<State>() {
        /** Currently only changes {@code message} Property based on {@code state}. */
        @Override
        public void changed(ObservableValue<? extends State> observable,
                State oldValue,
                State newValue) {
            message = messages.get(((State) state.getBean()).name());
            // ^update message based on state
            switch (newValue) {
                case DURING_DOWNLOAD:
                    apiThread.start(); // download!
                    state.set(State.AFTER_DOWNLOAD);
                    // ^i know this is explicitly bad practice,
                    // ^but i needed an automatic state change TK(future)
                    break;
                case PLAYING:
                    // Turn to Pause button
                    playButton.setOnAction(e -> {
                        playButton.setText("Play"); // TURNS to "Play" AFTER clicking
                        state.set(State.AFTER_DOWNLOAD);
                    }); // playButton.setOnAction
                    // Random Replacement
                    KeyFrame keyFrame = new KeyFrame(Duration.seconds(2),
                        event -> {
                            Random rand = new Random();
                            int visibleIndex = rand.nextInt(20);
                            int invisibleSize = imageGroups.get(false).size();
                            ImageView toInvisible = imageGroups.get(true)
                                    .remove(visibleIndex);
                            imageGroups.get(false).add(toInvisible); // order irrelevant
                            ImageView toVisible = imageGroups.get(false)
                                    .remove(rand.nextInt(invisibleSize));
                            imageGroups.get(true).set(visibleIndex, toVisible);
                            // ^order matters (same tile replaced)
                            mainContent.getChildren().set(visibleIndex,
                                    imageGroups.get(true).get(visibleIndex));
                            // ^TK(future) crappy; need to rewrite code
                            // ^sets/updates a single tile
                        }); // new KeyFrame
                    Timeline timeline = new Timeline();
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.getKeyFrames().add(keyFrame);
                    timeline.play();
                    break;
                case AFTER_DOWNLOAD:
                    playButton.setDisable(false); // enabled
                    getButton.setDisable(false); // enabled
                    // progress bar 100%
                    updateProgress(1.0);
                    break;
                default:
                    break;
            } // switch
        } // changed
    }; // runStateChangeListener = new ChangeListener

    /**
     * Default constructor. Constructs a {@code GalleryApp} object.
     */
    public GalleryApp() {
        this.stage = null;
        this.scene = null;
        this.root = new VBox();
        this.searchBar = new HBox();
        this.mainContent = new TilePane();
        this.statusBar = new HBox();
    } // GalleryApp

    /** {@inheritDoc} */
    @Override
    public void init() {

        System.out.println("init() called");

        // ----------------------(Search Bar)---------------------- //
        // Play/Pause Button
        playButton = new Button("Play"); // "Pause"
        playButton.setOnAction(e -> {
            playButton.setText("Pause"); // TURNS to "Pause" AFTER clicking
            state.set(State.PLAYING);
        }); // playButton.setOnAction

        // Query Term Field
        queryField = new TextField("Radiohead"); // default query item provided

        // Query Media Type Dropdown
        queryDropDown = new ComboBox<String>(mediaTypes);
        queryDropDown.getSelectionModel().select("music"); // default: "music"

        // Get Images Button
        getButton = new Button("Get Images");
        getButton.setOnAction(e -> state.set(State.DURING_DOWNLOAD)); // get images!

        // Buttons Listener
        state = new SimpleObjectProperty<GalleryApp.State>(State.INITIAL);
        state.addListener(runStateChangeListener);
        reinitializeButtons();

        // ----------------------(Main Content)---------------------- //
        imageList = Arrays.asList(new ImageView[20]); // prepare 20 blank slots/nulls
        imageList = imageList.stream()
                            .filter(iv -> iv == null)
                            .map(iv -> newDefaultImageView())
                            .collect(Collectors.toList());
        // ^populate those 20 blanks with the default image
        imageGroups = imageList.stream()
                .collect(Collectors.partitioningBy(s -> imageList.indexOf(s) <= 20));
        mainContent.setHgap(0); // no gaps between tiles
        mainContent.setMaxSize(256, 256); // square tiles
        mainContent.setPrefColumns(5); // five columns of tiles
        TilePane.setMargin(mainContent, Insets.EMPTY); // no margins

        // ----------------------(Status Bar)---------------------- //
        // Progress Bar
        progressBar = new ProgressBar();
        progressBar.setProgress(0.0F); // zero progress yet

    } // init

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        System.out.println("start() called");
        // Put all the (initialized) Nodes together, assemble the Scene, go onstage
        // Startup
        this.stage = stage;
        this.scene = new Scene(this.root, 1280, 720);
        this.scene.setFill(Color.BLACK);
        this.stage.setOnCloseRequest(event -> Platform.exit());
        this.stage.setTitle("GalleryApp!");
        this.stage.setScene(this.scene);
        this.stage.sizeToScene();
        this.stage.show();
        Platform.runLater(() -> this.stage.setResizable(false));

        // add children
        searchBar.getChildren().addAll(playButton,
                queryFieldLabel,
                queryDropDownLabel,
                getButton);
        mainContent.getChildren().addAll(imageGroups.get(true));
        // ^initialize tilePane with first 20 imageviews
        statusBar.getChildren().addAll(progressBar, itunesAttribution);
        root.getChildren().addAll(searchBar, mainContent, statusBar);

    } // start

    /**
     * Helper method.
     * 
     * <p>
     * Re-initializes both button states.
     * <ul>
     * <li>Disables Play Button</li>
     * <li>Enables Get Images Button</li>
     * </ul>
     */
    private void reinitializeButtons() {
        playButton.setDisable(true); // initial: disable
        getButton.setDisable(false); // initial: enable
    } // reinitializeButtons

    /**
     * Helper method.
     * 
     * <p>
     * Update progress bar with finalized "progress" variable.
     * 
     * @param progress finalized progress variable, as a percent
     */
    private void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    } // updateProgress

    /**
     * Helper method. Constructs a new default {@code Image} inside an
     * {@code ImageView}.
     */
    private ImageView newDefaultImageView() {
        return new ImageView(defaultImage); 
    } // newDefaultImageView

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // feel free to modify this method
        System.out.println("stop() called");
    } // stop

} // GalleryApp