package lk.ijse.seranity_mental_health_therapy_center;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOFactory;
import lk.ijse.seranity_mental_health_therapy_center.bo.BOTypes;
import lk.ijse.seranity_mental_health_therapy_center.bo.custom.UserBO;
import lk.ijse.seranity_mental_health_therapy_center.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        try {
            UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOTypes.USER);


            User existing = userBO.getUserByUsername("admin");
            if (existing != null) {
                userBO.deleteUser(existing.getId());
                System.out.println("✔ Old admin deleted!");
            }
            User admin = new User();
            admin.setId("U001");
            admin.setUsername("admin");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setRole("ADMIN");
            userBO.saveUser(admin);
            System.out.println("✔ Admin recreated with BCrypt password!");

        } catch (Exception e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource(
                        "/lk/ijse/seranity_mental_health_therapy_center/view/login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}