import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatApp extends Application {

    private static final Logger LOGGER = LogManager.getLogger(ChatApp.class);

    private static final ObservableList<SearchAction> data = FXCollections.observableArrayList();
    private static final AnswerService docsAnswerService = new AnswerService();
    private final TableView<SearchAction> table = new TableView<>();
    private final TextArea lastAnswer = new TextArea();
    private final TextField input = new TextField();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        loadChatsFromDatabase();
        LOGGER.info("Starting...");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("JavaGPT");
        titleLabel.getStyleClass().add("title-label");

        input.setPromptText("Start talking with JavaGPT");
        input.setOnAction(e -> doSearch(input.getText()));

        Button searchButton = new Button("Ask");
        searchButton.setOnAction(e -> doSearch(input.getText()));

        Button refreshButton = new Button("🔃"); // Add this line for the new Refresh button
        refreshButton.setOnAction(e -> refreshChatHistory()); // Set the action to refresh chat history

        Button clearButton = new Button("Clear Chat History");
        clearButton.setOnAction(e -> {
            data.clear(); // Clear the observable list
            clearChatHistoryFromDatabase(); // Add this line to clear history from the database
            lastAnswer.clear();
        });
        HBox inputBox = new HBox(20, input, searchButton, clearButton, refreshButton); // Include refreshButton here

        // HBox inputBox = new HBox(20, input, searchButton, clearButton);
        // inputBox.setPadding(new Insets(10, 0, 20, 0));

        TableColumn<SearchAction, Boolean> finishedColumn = new TableColumn<>("Finished");
        finishedColumn.setCellValueFactory(cellData -> cellData.getValue().getFinishedProperty());
        finishedColumn.setPrefWidth(70); // Set the preferred width to 100

        TableColumn<SearchAction, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().getTimestampProperty());
        timestampColumn.setPrefWidth(200); // Set the preferred width to 200

        TableColumn<SearchAction, String> questionColumn = new TableColumn<>("User");
        questionColumn.setCellValueFactory(cellData -> cellData.getValue().getQuestionProperty());
        questionColumn.setPrefWidth(300); // Set the preferred width to 200

        TableColumn<SearchAction, String> answerColumn = new TableColumn<>("JavaGPT");
        answerColumn.setCellValueFactory(cellData -> cellData.getValue().getAnswerProperty());
        answerColumn.setPrefWidth(300); // Set the preferred width to 300

        table.getColumns().addAll(timestampColumn, finishedColumn, questionColumn, answerColumn);
        table.setItems(data);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lastAnswer.textProperty().bind(newSelection.getAnswerProperty());
            }
        });

        lastAnswer.setEditable(false);
        lastAnswer.setWrapText(true);

        VBox centerBox = new VBox(10, table, lastAnswer);
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox.setVgrow(lastAnswer, Priority.ALWAYS);

        root.setTop(titleLabel);
        root.setCenter(centerBox);
        root.setBottom(inputBox);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
        Image appIcon = new Image(getClass().getResourceAsStream("/logo.png"));
        stage.getIcons().add(appIcon);
        stage.setTitle("JavaGPT by YESBHAUTIK");
        stage.setScene(scene);
        stage.show();

        data.add(new SearchAction("JavaGPT started", true));

        var initAction = new SearchAction("Initializing JavaGPT, please hold on...");
        data.add(initAction);
        lastAnswer.textProperty().bind(initAction.getAnswerProperty());
        new Thread(() -> docsAnswerService.init(initAction)).start();
    }

    private void doSearch(String question) {
        if (question.isEmpty()) {
            return;
        }

        var searchAction = new SearchAction(question);
        data.add(0, searchAction);
        lastAnswer.textProperty().bind(searchAction.getAnswerProperty());
        new Thread(() -> docsAnswerService.ask(searchAction)).start();
        input.clear();
    }

    private void loadChatsFromDatabase() {
        String sql = "SELECT timestamp, question, answer, finished FROM chat_messages ORDER BY timestamp DESC";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String timestamp = rs.getString("timestamp");
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                Boolean finished = rs.getBoolean("finished");
                SearchAction chat = new SearchAction(question, finished);
                chat.appendAnswer(answer); // Assuming appendAnswer method can handle full answer string
                data.add(chat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearChatHistoryFromDatabase() {
        String sql = "DELETE FROM chat_messages";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshChatHistory() {
        data.clear(); // Clear the current chat history from the UI
        loadChatsFromDatabase(); // Reload chat history from the database
    }
}