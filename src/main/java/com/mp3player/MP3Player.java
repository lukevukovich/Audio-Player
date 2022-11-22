package com.mp3player;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.media.*;
import java.io.File;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.util.Duration;

public class MP3Player extends Application
{

    public static void main(String[] args)
    {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception
    {

        AtomicReference<Integer> index = new AtomicReference<>(0);
        AtomicReference<Boolean> paused = new AtomicReference<>(false);
        AtomicReference<Boolean> oneSong = new AtomicReference<>(false);
        Boolean noSongs = false, noFolder = false;
        SongTimer timer = new SongTimer();

        File folder = new File("songs");
        if (!folder.exists())
        {
            noFolder = true;
            folder.mkdir();
        }
        Thread.currentThread().getContextClassLoader().getResourceAsStream(folder.toString());

        File[] songs = folder.listFiles();
        if (songs.length == 0)
        {
            noSongs = true;
        }
        else if (songs.length == 1)
        {
            oneSong.set(true);
        }

        if (!noSongs)
        {
            Controller controller = new Controller();

            AtomicReference<Media> media = new AtomicReference<>(new Media(songs[index.get()].toURI().toString()));
            AtomicReference<MediaPlayer> player = new AtomicReference<>(new MediaPlayer(media.get()));

            ListView<String> songList = new ListView<>();
            Slider volume = new Slider();
            ProgressBar progress = new ProgressBar();
            RadioButton shuffle = new RadioButton("\uD83D\uDD00");
            RadioButton repeat = new RadioButton("\uD83D\uDD01");
            AtomicReference<String> title = new AtomicReference<>();

            controller.setController(stage, media, player, songs, timer, index, songList, volume, progress, shuffle, repeat, title);

            title = new AtomicReference<>(controller.setTitle());

            songList.setPrefHeight(125);
            songList.getStylesheets().add("songlist.css");
            for (int i = 0; i < songs.length; i++)
            {
                songList.getItems().add(i, (i + 1) + " - " + songs[i].getName());
            }
            songList.getSelectionModel().select(0);

            volume.setPrefWidth(100);
            volume.setValue(100);
            volume.getStylesheets().add("slider.css");
            volume.setOnMouseDragged(dragEvent ->
            {
                controller.setPlayerVolume();
            });

            progress.setPrefSize(440, 25);
            progress.setProgress(0);
            progress.setStyle("-fx-accent: grey;");
            progress.setOnMouseClicked(mouseEvent -> 
            {
                double location = mouseEvent.getX();
                double total = 440;
                double ratio = location/total;

                double duration = controller.getMedia().getDuration().toSeconds();
                double setCurrent = ratio*duration;

                if (paused.get().equals(true))
                {
                    progress.setProgress(ratio);
                }

                controller.getPlayer().seek(new Duration(setCurrent*1000));
            });

            player.get().play();
            timer.beginTimer(media.get(), player.get(), progress);
            stage.setTitle(title.get());
            player.get().setOnEndOfMedia(() ->
            {
                controller.setEndOfMedia();
            });

            //⏸▶
            Button pausePlay = new Button("⏸");
            controller.setButtonProperties(pausePlay);
            pausePlay.setOnAction(actionEvent ->
            {
                if (paused.get().equals(false))
                {
                    controller.getPlayer().pause();
                    controller.getTimer().cancelTimer();
                    pausePlay.setText("▶");
                    paused.set(true);
                }
                else
                {
                    controller.getPlayer().play();
                    controller.getTimer().beginTimer(controller.getMedia(), controller.getPlayer(), controller.getProgress());
                    pausePlay.setText("⏸");
                    paused.set(false);
                }
            });

            songList.setOnMouseClicked(mouseEvent ->
            {
                controller.getIndex().set(songList.getSelectionModel().getSelectedIndex());
                controller.setSong(controller.getIndex().get());

                if (paused.get().equals(true))
                {
                    pausePlay.setText("⏸");
                    progress.setProgress(0);
                    paused.set(false);
                }
            });
            songList.setOnKeyPressed(keyEvent ->
            {
                if (keyEvent.getCode() == KeyCode.ENTER)
                {
                    controller.getIndex().set(songList.getSelectionModel().getSelectedIndex());
                    controller.setSong(controller.getIndex().get());

                    if (paused.get().equals(true))
                    {
                        pausePlay.setText("⏸");
                        progress.setProgress(0);
                        paused.set(false);
                    }
                }
            });

            Button next = new Button("⏭");
            controller.setButtonProperties(next);
            next.setOnAction(actionEvent ->
            {
                if (oneSong.get().equals(true))
                {
                    controller.setSong(index.get());
                }
                else if (shuffle.isSelected())
                {
                    controller.shuffleSong();
                }
                else
                {
                    controller.nextSong();
                }
                songList.getSelectionModel().select(controller.getIndex().get());

                if (paused.get().equals(true))
                {
                    controller.getPlayer().pause();
                    controller.getTimer().cancelTimer();
                    pausePlay.setText("▶");
                    progress.setProgress(0);
                }
            });

            Button prev = new Button("⏮");
            controller.setButtonProperties(prev);
            prev.setOnAction(actionEvent ->
            {
                if (controller.getPlayer().getCurrentTime().toSeconds() >= 10)
                {
                    controller.setSong(controller.getIndex().get());
                }
                else
                {
                    if (oneSong.get().equals(true))
                    {
                        controller.setSong(index.get());
                    }
                    else if (shuffle.isSelected())
                    {
                        controller.shuffleSong();
                    }
                    else
                    {
                        controller.prevSong();
                    }
                    songList.getSelectionModel().select(controller.getIndex().get());

                    if (paused.get().equals(true))
                    {
                        controller.getPlayer().pause();
                        controller.getTimer().cancelTimer();
                        pausePlay.setText("▶");
                        progress.setProgress(0);
                    }
                }
            });

            Button plus10 = new Button("⏩");
            controller.setButtonProperties(plus10);
            plus10.setOnAction(actionEvent ->
            {
                double current = controller.getPlayer().getCurrentTime().toSeconds() + 10;
                double duration = controller.getMedia().getDuration().toSeconds();
                double progressValue = current/duration;

                if (paused.get().equals(true))
                {
                    progress.setProgress(progressValue);
                }

                controller.getPlayer().seek(new Duration((current)*1000));
            });

            Button minus10 = new Button("⏪");
            controller.setButtonProperties(minus10);
            minus10.setOnAction(actionEvent ->
            {
                double current = controller.getPlayer().getCurrentTime().toSeconds() - 10;
                double duration = controller.getMedia().getDuration().toSeconds();
                double progressValue = current/duration;

                if (paused.get().equals(true))
                {
                    progress.setProgress(progressValue);
                }

                controller.getPlayer().seek(new Duration((current)*1000));
            });

            HBox buttons = new HBox(minus10, prev, pausePlay, next, plus10, volume);
            buttons.setAlignment(Pos.CENTER);
            buttons.setSpacing(10);

            HBox progressAndRadioButtons = new HBox(progress, shuffle, repeat);
            progressAndRadioButtons.setAlignment(Pos.CENTER_LEFT);
            progressAndRadioButtons.setSpacing(20);

            VBox all = new VBox(songList, buttons, progressAndRadioButtons);
            buttons.setAlignment(Pos.CENTER);
            all.setPadding(new Insets(10));
            all.setSpacing(10);

            Scene scene = new Scene(all);

            stage.setScene(scene);

            File icon = new File("music.png");
            Thread.currentThread().getContextClassLoader().getResourceAsStream(icon.toString());
            stage.getIcons().add(new Image("file:" + icon));

            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();

            stage.setOnCloseRequest(windowEvent ->
            {
                controller.getPlayer().stop();
                controller.getTimer().cancelTimer();
                stage.close();
                System.exit(0);
            });
        }
        else
        {
            Label none = new Label();
            if (noFolder)
                none.setText("Songs Folder Created");
            else if (noSongs)
                none.setText("No Songs Found");

            none.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
            none.setAlignment(Pos.CENTER);

            Scene scene = new Scene(none, 250 , 75);

            stage.setScene(scene);
            stage.setTitle("MP3 Player");

            File icon = new File("music.png");
            Thread.currentThread().getContextClassLoader().getResourceAsStream(icon.toString());
            stage.getIcons().add(new Image("file:" + icon));

            stage.setResizable(false);
            stage.show();
        }

    }

}
