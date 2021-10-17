package default_package;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PostItNoteStage {
    double sizeX;
    double sizeY;
    double positionX;
    double positionY;
    BorderPane content;
    BorderPane buttonArea;
    Button newPostItNote;
    Button deletePostItNote;
    TextArea textArea;
    Font buttonFont;
    ContextMenu rightClickMenu;
    MenuItem cut, copy, paste, about, exit;
    ClipboardContent clipboardContent;

    public PostItNoteStage(double sizeX, double sizeY, double positionX, double positionY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.positionX = positionX;
        this.positionY = positionY;

        Stage stage = new Stage();
        stage.setX(positionX);
        stage.setY(positionY);

        content = new BorderPane();
        buttonArea = new BorderPane();

        content.setStyle("-fx-background-color: blue");
        buttonArea.setStyle("-fx-background-color: red");
        content.setTop(buttonArea);

        newPostItNote = new Button("+");
        deletePostItNote = new Button("×");

        buttonArea.setLeft(newPostItNote);
        buttonArea.setRight(deletePostItNote);

        textArea = new TextArea();
        content.setCenter(textArea);

        buttonArea.setStyle("-fx-background-color: rgb(248, 247, 182)");
        newPostItNote.setStyle("-fx-background-color: rgb(248, 247, 182)");
        deletePostItNote.setStyle("-fx-background-color: rgb(248, 247, 182)");

        buttonFont = Font.font("Arial", FontWeight.BOLD, 20);

        newPostItNote.setFont(buttonFont);
        newPostItNote.setTextFill(Color.GREY);

        deletePostItNote.setFont(buttonFont);
        deletePostItNote.setTextFill(Color.GREY);

        Scene scene = new Scene(content, sizeX, sizeY);


        EventHandler<ActionEvent> newButton = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                double x = stage.getX();
                double y = stage.getY();
                double newX = x + stage.getWidth();
                double newY = y;
                Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                if (stage.getX() + stage.getWidth() * 2 > screen.getWidth()) {
                    newX = 0;
                    newY = stage.getY() + stage.getHeight();
                }
                new PostItNoteStage(sizeX, sizeY, newX, newY);
            }
        };
        newPostItNote.setOnAction(newButton);

        EventHandler<ActionEvent> deleteButton = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        };
        deletePostItNote.setOnAction(deleteButton);

        rightClickMenu = new ContextMenu();

        cut = new MenuItem("Cut");
        EventHandler<ActionEvent> cutEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clipboardContent = new ClipboardContent();
                String selectedText = textArea.getSelectedText();
                clipboardContent.putString(selectedText);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
                textArea.replaceSelection("");
            }
        };
        cut.setOnAction(cutEvent);
        rightClickMenu.getItems().add(cut);

        /**
         * Follow this template	and	add	the	rest of	the	items: copy,paste,about and exit.
         */
        copy = new MenuItem("Copy");
        EventHandler<ActionEvent> copyEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clipboardContent = new ClipboardContent();
                String selectedText = textArea.getSelectedText();
                clipboardContent.putString(selectedText);
                Clipboard.getSystemClipboard().setContent(clipboardContent);
            }
        };
        copy.setOnAction(copyEvent);
        rightClickMenu.getItems().add(copy);

        paste = new MenuItem("Paste");
        EventHandler<ActionEvent> pasteEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Clipboard.getSystemClipboard().hasString()) {
                    textArea.appendText((String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT));
                }
            }
        };
        paste.setOnAction(pasteEvent);
        rightClickMenu.getItems().add(paste);

        about = new MenuItem("About");
        EventHandler<ActionEvent> aboutEvent = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                GridPane gridPane = new GridPane();
                Text text = new Text();
                Image image = new Image("file:../summer.jpg", 155, 200, false, false);
                ImageView imageView = new ImageView(image);
                gridPane.add(imageView, 0, 0);

                alert.setHeaderText("Post-It Note");
                text.setText("Digital Post-It Note using JavaFX\nVersion: JavaFX 17\nAuthor: Christy\nCopyright(c) 2021");
                gridPane.add(text, 300, 0);
                alert.getDialogPane().setContent(gridPane);
                alert.show();
            }
        };
        about.setOnAction(aboutEvent);
        rightClickMenu.getItems().add(about);

        exit = new MenuItem("Exit");
        rightClickMenu.getItems().add(exit);


        textArea.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, Event::consume);
        EventHandler<MouseEvent> rightClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    rightClickMenu.show(content, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        };

        content.setOnMouseClicked(rightClick);
        textArea.setOnMouseClicked(rightClick);

        EventHandler<MouseEvent> dragEvent = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                stage.setX(mouseEvent.getScreenX());
                stage.setY(mouseEvent.getScreenY());
            }
        };
        buttonArea.setOnMouseDragged(dragEvent);

        /**
         * Define an internal class DrawUtil
         * to implement the ability to draw the Post-It Note and resize it.
         */
        class DrawUtil {
            private boolean isRight; //whether it is in the right boundary adjustment window state
            private boolean isBottomRight; //whether the window is adjusted in the bottom right corner
            private boolean isBottom; //whether it is in the bottom boundary adjustment window state
            private final static int RESIZE_WIDTH = 5; //the range of the adjustment window state and the border distance
            private final static double MIN_WIDTH = 200; //minimum width of the stage
            private final static double MIN_HEIGHT = 200; //minimum height of the stage

            public void resize(Stage stage, TextArea text) {

                EventHandler<MouseEvent> judgeCursorType = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        mouseEvent.consume();
                        double x = mouseEvent.getSceneX();
                        double y = mouseEvent.getSceneY();
                        double width = stage.getWidth();
                        double height = stage.getHeight();
                        Cursor cursorType = Cursor.DEFAULT; //if no cursor is defined, then set default cursor type

                        isRight = isBottomRight = isBottom = false; //first reset all adjustment window states to false

                        if (y >= height - RESIZE_WIDTH) {
                            if (x <= RESIZE_WIDTH) {
                                //adjust the window state in the bottom left corner

                            } else if (x >= width - RESIZE_WIDTH) {
                                //adjust the window state in the bottom right corner
                                isBottomRight = true;
                                cursorType = Cursor.SE_RESIZE; //the south-east-resize cursor type
                            } else {
                                //the lower boundary adjusts the window state
                                isBottom = true;
                                cursorType = Cursor.S_RESIZE; //the south-resize cursor type
                            }
                        } else if (x >= width - RESIZE_WIDTH) {
                            //the right boundary adjusts the window state
                            isRight = true;
                            cursorType = Cursor.E_RESIZE; //the east-resize cursor type
                        }
                        //Finally, change the mouse cursor.
                        text.setCursor(cursorType);
                    }
                };
                text.setOnMouseMoved(judgeCursorType);

                EventHandler<MouseEvent> implementDrag = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        double x = mouseEvent.getSceneX();
                        double y = mouseEvent.getScreenY();
                        //save the x, y coordinates, width, and height of the window after the window changes
                        //used to prejudge whether it will be less than the minimum width and minimum height
                        double nextX = stage.getX();
                        double nextY = stage.getY();
                        double nextWidth = stage.getWidth();
                        double nextHeight = stage.getHeight();

                        if (isRight || isBottomRight) {
                            //adjust the window status on all the right
                            nextWidth = x;
                        }
                        if (isBottomRight || isBottom) {
                            //adjust the window status on all the bottom
                            nextHeight = y;
                        }
                        if (nextWidth <= MIN_WIDTH) {
                            //If the width of the window changes to less than the minimum width,
                            //the width is adjusted to the minimum width.
                            nextWidth = MIN_WIDTH;
                        }
                        if (nextHeight <= MIN_HEIGHT) {
                            //If the height of the window changes to less than the minimum height,
                            //the height is adjusted to the minimum height.
                            nextHeight = MIN_HEIGHT;
                        }
                        //Finally, the unified change of the window's x, y coordinates and width, height.
                        //can prevent frequent refresh screen flash situation
                        stage.setX(nextX);
                        stage.setY(nextY);
                        stage.setWidth(nextWidth);
                        stage.setHeight(nextHeight);
                    }
                };
                text.setOnMouseDragged(implementDrag);
            }
        }

        DrawUtil drawUtil = new DrawUtil();
        drawUtil.resize(stage, textArea);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();

        Region region = (Region) textArea.lookup(".content");
        region.setStyle("-fx-background-color: rgb(253, 253, 201)");
    }
}