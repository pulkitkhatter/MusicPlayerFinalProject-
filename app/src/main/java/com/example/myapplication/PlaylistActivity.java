package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class PlaylistActivity extends AppCompatActivity {

    private LinearLayout playlistLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Initialize views
        TextView headerTextView = findViewById(R.id.headerTextView);
        playlistLayout = findViewById(R.id.playlistLayout);

        // Set up FAB click listener
        findViewById(R.id.fabAddPlaylist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPlaylistDialog();
            }
        });

        // Example: Set a header text
        headerTextView.setText("My Playlists");

        // Load playlist data from SharedPreferences
        loadPlaylistData();
    }

    private void showAddPlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Playlist");
        builder.setMessage("What do you want to name your playlist?");

        // Set up the input field
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the positive button
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String playlistName = input.getText().toString().trim();
                if (!playlistName.isEmpty()) {
                    // Add the playlist and update the LinearLayout
                    addPlaylistTextView(playlistName);
                }
            }
        });

        // Set up the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void addPlaylistTextView(final String playlistName) {
        TextView textView = new TextView(this);
        textView.setText(playlistName);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(18);

        // Add some padding to the TextView
        int padding = getResources().getDimensionPixelSize(R.dimen.playlist_text_padding);
        textView.setPadding(padding, padding, padding, padding);

        // Set the custom background drawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(ContextCompat.getDrawable(this, R.drawable.bordered_background));
        } else {
            textView.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bordered_background));
        }

        // Create layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = getResources().getDimensionPixelSize(R.dimen.playlist_text_margin);
        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);

        // Set up the click listener for navigation
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNowPlayingActivity(playlistName);
            }
        });

        // Set up the long click listener for deletion
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteConfirmationDialog(playlistName, (TextView) v);
                return true;
            }
        });

        // Add the TextView to the LinearLayout
        playlistLayout.addView(textView);

        // Save playlist data when a new playlist is added
        savePlaylistData();
    }

    private void showDeleteConfirmationDialog(final String playlistName, final TextView textView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Playlist");
        builder.setMessage("Are you sure you want to delete this playlist?");

        // Set up the positive button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove the TextView from the LinearLayout and update the data
                playlistLayout.removeView(textView);
                savePlaylistData();
            }
        });

        // Set up the negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        builder.show();
    }

    private void openNowPlayingActivity(String playlistName) {
        Intent intent = new Intent(this, PlaylistSongs.class);
        intent.putExtra("playlistName", playlistName);
        startActivity(intent);
    }

    private void loadPlaylistData() {
        // Load playlist data from SharedPreferences
        // For simplicity, we're using a comma-separated string to store playlist names
        String playlistData = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("playlists", "");
        String[] playlists = playlistData.split(",");

        // Add TextViews for each playlist
        for (String playlist : playlists) {
            if (!playlist.isEmpty()) {
                addPlaylistTextView(playlist);
            }
        }
    }

    private void savePlaylistData() {
        // Save playlist data to SharedPreferences
        // For simplicity, we're using a comma-separated string to store playlist names
        StringBuilder playlistData = new StringBuilder();
        for (int i = 0; i < playlistLayout.getChildCount(); i++) {
            TextView textView = (TextView) playlistLayout.getChildAt(i);
            playlistData.append(textView.getText().toString());
            playlistData.append(",");
        }

        getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().putString("playlists", playlistData.toString()).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save playlist data when the activity is destroyed
        savePlaylistData();
    }
}
