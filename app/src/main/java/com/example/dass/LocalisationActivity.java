package com.example.dass;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class LocalisationActivity extends AppCompatActivity {

    private MapView mapView;
    private Button reverseButton;

    // Firebase Database Reference
    private DatabaseReference databaseRef;

    // GeoPoint for current location
    private GeoPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_localisation);

        // Initialize UI elements
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(25.0);

        reverseButton = findViewById(R.id.reverseButton);

        reverseButton.setOnClickListener(v -> reverseAction());

        // Firebase Database Reference
        databaseRef = FirebaseDatabase.getInstance("https://dass-2a23c-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("sensor-data");

        // Listen to location updates from Firebase
        listenToLocationUpdates();
    }

    /**
     * Listen to location updates from the Firebase Realtime Database.
     */
    private void listenToLocationUpdates() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Fetch latitude and longitude from Firebase
                Double latitude = snapshot.child("Latitude").getValue(Double.class);
                Double longitude = snapshot.child("Longitude").getValue(Double.class);

                // Ensure data is not null
                if (latitude != null && longitude != null) {
                    currentLocation = new GeoPoint(latitude, longitude);
                    updateMapLocation(); // Update the map
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                System.err.println("Error fetching location: " + error.getMessage());
            }
        });
    }

    /**
     * Update the map location based on the current latitude and longitude.
     */
    private void updateMapLocation() {
        if (currentLocation != null) {
            mapView.getController().setCenter(currentLocation);
            Marker marker = new Marker(mapView);
            marker.setPosition(currentLocation);
            marker.setTitle("Current Location");
            mapView.getOverlays().clear();
            mapView.getOverlays().add(marker);
            mapView.invalidate();
        }
    }

    /**
     * Action for the reverse button to navigate back to MainActivity.
     */
    private void reverseAction() {
        Intent intent = new Intent(LocalisationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
