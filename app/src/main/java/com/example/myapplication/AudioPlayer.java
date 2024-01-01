package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayer extends AppCompatActivity {

    String audio_path;
    String songTitle;

    TextView playerpos, playerdur, songTitleTextView;

    SeekBar seekBar;

    ImageView rewind, play, pause, forward, albumCover;

    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    private ImageButton btnPrev, btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        // Initialize views
        playerpos = findViewById(R.id.playerPosition);
        playerdur = findViewById(R.id.playerDuration);
        seekBar = findViewById(R.id.seek_bar);
        rewind = findViewById(R.id.btn_rew);
        pause = findViewById(R.id.btn_pause);
        play = findViewById(R.id.btn_ply);
        forward = findViewById(R.id.btn_frwd);
        albumCover = findViewById(R.id.albumCover);
        songTitleTextView = findViewById(R.id.songTitle);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        // Get audio path and song title from intent
        Intent intent = getIntent();
        audio_path = intent.getStringExtra("Audio_path");
        songTitle = intent.getStringExtra("Audio_title");

        // Initialize MediaPlayer
        mediaPlayer = new MediaPlayer();

        // Initialize handler for seekBar updates
        handler = new Handler();

        try {
            // Set data source and prepare MediaPlayer
            mediaPlayer.setDataSource(audio_path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set max duration for the seekBar
        seekBar.setMax(mediaPlayer.getDuration());

        // Set duration text
        int duration = mediaPlayer.getDuration();
        playerdur.setText(convertFormat(duration));

        // Set click listeners for buttons
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
                handler.postDelayed(runnable, 0);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                handler.removeCallbacks(runnable);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if (mediaPlayer.isPlaying() && duration != currentPos) {
                    currentPos = currentPos + 5000;
                    if (currentPos > duration) {
                        currentPos = duration;
                    }
                    playerpos.setText(convertFormat(currentPos));
                    mediaPlayer.seekTo(currentPos);
                }
            }
        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPos = mediaPlayer.getCurrentPosition();
                if (mediaPlayer.isPlaying() && currentPos > 5000) {
                    currentPos = currentPos - 5000;
                    playerpos.setText(convertFormat(currentPos));
                    mediaPlayer.seekTo(currentPos);
                }
            }
        });

        // Display album cover
        albumCover.setImageResource(R.drawable.music_icon); // Change to your actual resource

        // Update song title dynamically
        songTitleTextView.setText("Now Playing: " + songTitle);

        // Set up a runnable to update the SeekBar and position text
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();

                    // Update seekBar and position text on the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(currentPosition);
                            playerpos.setText(convertFormat(currentPosition));
                        }
                    });

                    handler.postDelayed(this, 500); // Update every 500 milliseconds
                }
            }
        };

        // Start the initial update
        handler.postDelayed(runnable, 0);

        // Set SeekBar change listener for smooth dragging
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                    playerpos.setText(convertFormat(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Remove callbacks when the user starts dragging
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume updates when the user stops dragging
                handler.postDelayed(runnable, 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        finish();
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}
