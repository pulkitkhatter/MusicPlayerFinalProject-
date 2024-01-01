package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<AudioModel> audioList = new ArrayList<>();
    RecyclerView recyclerView;
    AudioAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AudioAdapter(this, audioList);
        recyclerView.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabLayout);
        TabItem songsTab = findViewById(R.id.SongsTab);
        TabItem playlistTab = findViewById(R.id.PlaylistTab);

        // Set up a listener for tab clicks
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                switch (tab.getPosition()) {
                    case 0:
                        // Songs tab selected
                        // Example: Open another activity
                        break;
                    case 1:
                        // Playlist tab selected
                        // Example: Open another activity
                        Intent playlistIntent = new Intent(MainActivity.this, PlaylistActivity.class);
                        startActivity(playlistIntent);
                        break;
                    // Add more cases for additional tabs if needed
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });

        // To Get All Audio Files from the device
        ContentResolver contentResolver = getContentResolver();
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION};
        Cursor cursor = contentResolver.query(audioUri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                AudioModel audioModel = new AudioModel(title, path, duration);
                audioList.add(audioModel);
            }
            adapter.notifyDataSetChanged();
            cursor.close();
        }

        // Select the "Songs" tab by default
        TabLayout.Tab defaultTab = tabLayout.getTabAt(0);
        if (defaultTab != null) {
            defaultTab.select();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Select the "Songs" tab when the user returns to MainActivity
        TabLayout.Tab songsTab = tabLayout.getTabAt(0);
        if (songsTab != null) {
            songsTab.select();
        }
    }
}
