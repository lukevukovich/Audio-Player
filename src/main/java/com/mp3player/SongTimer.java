package com.mp3player;

import javafx.scene.control.ProgressBar;
import javafx.scene.media.*;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicReference;

public class SongTimer
{

    private Timer timer;
    private Boolean running;

    public SongTimer()
    {
        this.running = false;
    }

    public void beginTimer(Media media, MediaPlayer player, ProgressBar progress)
    {
        this.timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                running = true;

                double current = player.getCurrentTime().toSeconds();
                double duration = media.getDuration().toSeconds();

                progress.setProgress(current/duration);
            }
        };

        this.timer.scheduleAtFixedRate(task, 100, 100);
    }

    public void cancelTimer()
    {
        this.timer.cancel();
        running = false;
    }

    public Boolean getRunning()
    {
        return running;
    }

}
