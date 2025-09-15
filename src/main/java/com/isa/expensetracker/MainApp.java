package com.isa.expensetracker;

import com.isa.expensetracker.db.Db;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main JavaFX entrypoint for ExpenseTracker.
 **/
public class MainApp extends Application {

    // Change this if you move your FXML (e.g., to "/fxml/main.fxml")
    private static final String MAIN_FXML = "/com/isa/expensetracker/main.fxml";
    private static final String APP_CSS   = "/css/app.css";
    private static final String APP_ICON  = "/icons/app.png";

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize DB (Flyway migrations run here)
        try {
            Db.init();
        } catch (Exception ex) {
            // Show a friendly error before failing
            showErrorAndExit(
                    "Database initialization failed",
                    """
                    ExpenseTracker couldn't initialize the database.
    
                    • Check that PostgreSQL is running and your credentials are correct.
                    • Ensure migrations exist at: src/main/resources/migrations/V1__init.sql
                    • If you changed the schema name or locations, update Db.java accordingly.
    
                    Details:
                    """ + ex.getMessage()
            );
            throw ex; // let JavaFX stop after showing the dialog
        }

        // Resolve FXML
        URL fxmlUrl = getClass().getResource(MAIN_FXML);
        if (fxmlUrl == null) {
            showErrorAndExit(
                    "FXML not found",
                    """
                    Could not find main FXML on classpath:
    
                      """ + MAIN_FXML + """

                Make sure the file exists at:
                  src/main/resources""" + MAIN_FXML + """

                (Folder must be named "resources", not "resourse")
                """
            );
            throw new IllegalStateException("Missing FXML: " + MAIN_FXML);
        }

        // Load UI
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        // Optional CSS
        URL cssUrl = getClass().getResource(APP_CSS);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        // Optional icon
        try {
            var iconStream = getClass().getResourceAsStream(APP_ICON);
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) { /* icon is optional */ }

        stage.setTitle("ExpenseTracker");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(600);
        stage.show();
    }

    private void showErrorAndExit(String header, String content) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ExpenseTracker");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception ignored) {
            // In case JavaFX isn't fully initialized, just print
            System.err.println(header + "\n" + content);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
