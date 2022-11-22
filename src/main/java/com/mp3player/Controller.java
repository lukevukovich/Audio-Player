package com.mp3player;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Controller
{

    private Stage stage;
    private AtomicReference<Media> media;
    private AtomicReference<MediaPlayer> player;
    private File[] songs;
    private SongTimer timer;
    private AtomicReference<Integer> index;
    private ListView<String> songList;
    private Slider volume;
    private ProgressBar progress;
    private RadioButton shuffle;
    private RadioButton repeat;
    private AtomicReference<String> title;

    public void setController(Stage stg, AtomicReference<Media> m, AtomicReference<MediaPlayer> p, File[] s, SongTimer t, AtomicReference<Integer> i,
                              ListView<String> sl, Slider v, ProgressBar prog, RadioButton sfle, RadioButton r, AtomicReference<String> tit)
    {
        this.stage = stg;
        this.media = new AtomicReference<>(m.get());
        this.player = new AtomicReference<>(p.get());
        this.songs = s;
        for (int j = 0; j < s.length; j++)
        {
            this.songs[j] = s[j];
        }
        this.timer = t;
        this.index= new AtomicReference<>(i.get());
        this.songList = sl;
        this.volume = v;
        this.progress = prog;
        this.shuffle = sfle;
        this.repeat = r;
        this.title = new AtomicReference<>(tit.get());
    }

    public void setIndex(AtomicReference<Integer> i)
    {
        this.index.set(i.get());
    }

    public void setPlayerVolume()
    {
        this.player.get().setVolume(this.volume.getValue()/100);
    }

    public Media setMedia()
    {
        this.media.set(new Media(this.songs[this.index.get()].toURI().toString()));
        return this.media.get();
    }

    public MediaPlayer setMediaPlayer()
    {
        this.player.set(new MediaPlayer(this.media.get()));
        return this.player.get();
    }

    public File[] getSongs()
    {
        File[] s = new File[this.songs.length];
        for (int i = 0; i < this.songs.length; i++)
        {
            s[i] = this.songs[i];
        }
        return s;
    }

    public String setTitle()
    {
        this.title.set((this.index.get() + 1) + "  |  " + this.songs[this.index.get()].toString().substring(6));
        return this.title.get();
    }

    public void setButtonProperties(Button button)
    {
        button.setPrefSize(80, 50);
        button.setAlignment(Pos.CENTER);
        button.setFont(Font.font("", FontWeight.BOLD, 25));
    }

    public Media getMedia()
    {
        return this.media.get();
    }

    public MediaPlayer getPlayer()
    {
        return this.player.get();
    }

    public AtomicReference<Integer> getIndex()
    {
        return this.index;
    }

    public ProgressBar getProgress()
    {
        return this.progress;
    }

    public SongTimer getTimer()
    {
        return this.timer;
    }

    public void setEndOfMedia()
    {
        if (this.repeat.isSelected())
        {
            setSong(this.index.get());
        }
        else if (this.shuffle.isSelected())
        {
            shuffleSong();
        }
        else
        {
            nextSong();
        }
        this.songList.getSelectionModel().select(this.index.get());
    }

    public void nextSong()
    {
        this.player.get().stop();

        if (this.timer.getRunning() == true)
        {
            this.timer.cancelTimer();
            this.progress.setProgress(0);
        }

        this.index.set(this.index.get() + 1);

        try
        {
            this.media.set(setMedia());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.index.set(0);
            this.media.set(setMedia());
        }

        this.player.set(setMediaPlayer());
        this.player.get().setVolume(this.volume.getValue()/100);
        this.player.get().play();

        this.title.set(setTitle());
        this.stage.setTitle(this.title.get());

        this.songList.scrollTo(this.getIndex().get());

        this.player.get().setOnEndOfMedia(() ->
        {
            setEndOfMedia();
        });

        if (this.timer.getRunning() == false)
        {
            this.timer.beginTimer(this.media.get(), this.player.get(), this.progress);
        }

        System.gc();
    }

    public void prevSong()
    {
        this.player.get().stop();

        if (this.timer.getRunning() == true)
        {
            this.timer.cancelTimer();
            this.progress.setProgress(0);
        }

        this.index.set(this.index.get() - 1);

        try
        {
            this.media.set(setMedia());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.index.set(this.songs.length - 1);
            this.media.set(setMedia());
        }

        this.player.set(setMediaPlayer());
        this.player.get().setVolume(this.volume.getValue()/100);
        this.player.get().play();

        this.title.set(setTitle());
        this.stage.setTitle(this.title.get());

        this.songList.scrollTo(this.getIndex().get());

        this.player.get().setOnEndOfMedia(() ->
        {
            setEndOfMedia();
        });

        if (this.timer.getRunning() == false)
        {
            this.timer.beginTimer(this.media.get(), this.player.get(), this.progress);
        }

        System.gc();
    }

    public void setSong(int newIndex)
    {
        this.player.get().stop();

        if (this.timer.getRunning() == true)
        {
            this.timer.cancelTimer();
            this.progress.setProgress(0);
        }

        this.index.set(newIndex);

        try
        {
            this.media.set(setMedia());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.index.set(0);
            this.media.set(setMedia());
        }

        this.player.set(setMediaPlayer());
        this.player.get().setVolume(this.volume.getValue()/100);
        this.player.get().play();

        this.title.set(setTitle());
        this.stage.setTitle(this.title.get());

        this.songList.scrollTo(this.getIndex().get());

        this.player.get().setOnEndOfMedia(() ->
        {
            setEndOfMedia();
        });

        if (this.timer.getRunning() == false)
        {
            this.timer.beginTimer(this.media.get(), this.player.get(), this.progress);
        }

        System.gc();
    }

    public void shuffleSong()
    {
        Random rand = new Random();
        int newIndex = rand.nextInt(this.songs.length);

        while(true)
        {
            if (this.index.get() == newIndex)
            {
                newIndex = rand.nextInt(this.songs.length);
            }
            else
            {
                break;
            }
        }

        this.player.get().stop();

        if (this.timer.getRunning() == true)
        {
            this.timer.cancelTimer();
            this.progress.setProgress(0);
        }

        this.index.set(newIndex);

        try
        {
            this.media.set(setMedia());
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            this.index.set(0);
            this.media.set(setMedia());
        }

        this.player.set(setMediaPlayer());
        this.player.get().setVolume(this.volume.getValue()/100);
        this.player.get().play();

        this.title.set(setTitle());
        this.stage.setTitle(this.title.get());

        this.songList.scrollTo(this.getIndex().get());

        this.player.get().setOnEndOfMedia(() ->
        {
            setEndOfMedia();
        });

        if (this.timer.getRunning() == false)
        {
            this.timer.beginTimer(this.media.get(), this.player.get(), this.progress);
        }

        System.gc();
    }

}
