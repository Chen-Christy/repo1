package default_package;

import javafx.application.Application;
import javafx.stage.Stage;

public class PostItNote extends Application {
    public PostItNote() {
        PostItNoteStage mainWindow = new PostItNoteStage(200, 200, 0, 0);
    }

    public static void main(String[] args) {
        System.out.println("Starting Post-It Note application...");
        System.out.println("Author: Christy");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        PostItNoteStage mainWindow = new PostItNoteStage(200, 200, 0, 0);
    }
}