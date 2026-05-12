package lk.ijse.seranity_mental_health_therapy_center;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource(
                        "/lk/ijse/seranity_mental_health_therapy_center/Login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}