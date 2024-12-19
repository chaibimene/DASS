package com.example.dass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Firebase Database Reference
    private DatabaseReference databaseRef;

    // UI elements
    private ImageView lineB1, lineB2, lineB3;
    private ImageView lineL1, lineL2, lineL3;
    private ImageView lineR1, lineR2, lineR3;
    private ImageView stopSign, feu, vitesse;
    private Button btnReverse, btnForward;
    private TextView localisation, speedTextView;
    private ProgressBar circularSpeedIndicator;

    private int sensorSpeed = 0; // Speed from `sensor-data`
    private int detectedSpeed = 0; // Speed from `detected-signs`

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Database reference
        databaseRef = FirebaseDatabase.getInstance("https://dass-2a23c-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference();

        // Initialize UI elements
        initializeUI();

        // Start listening to Firebase updates
        listenToSensorData();
        listenToDetectedSigns();

        // Set up button click listeners
        btnReverse.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, RearCameraActivity.class);
            startActivity(i);
        });
        btnForward.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, FrontCameraActivity.class);
            startActivity(i);
        });
        localisation.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, LocalisationActivity.class);
            startActivity(i);
        });
    }

    /**
     * Initialize UI elements.
     */
    private void initializeUI() {
        // Initialize line views for sensors
        lineL1 = findViewById(R.id.lineLeft1);
        lineL2 = findViewById(R.id.lineLeft2);
        lineL3 = findViewById(R.id.lineLeft3);

        lineR1 = findViewById(R.id.lineRight1);
        lineR2 = findViewById(R.id.lineRight2);
        lineR3 = findViewById(R.id.lineRight3);

        lineB1 = findViewById(R.id.lineBack1);
        lineB2 = findViewById(R.id.lineBack2);
        lineB3 = findViewById(R.id.lineBack3);

        // Speed display components
        speedTextView = findViewById(R.id.speedTextView);
        circularSpeedIndicator = findViewById(R.id.circularSpeedIndicator);

        // Stop sign and additional indicators
        stopSign = findViewById(R.id.stopSign);
        feu = findViewById(R.id.imageView2);
        vitesse = findViewById(R.id.imageView3);

        // Buttons and localisation text
        btnForward = findViewById(R.id.btnForward);
        btnReverse = findViewById(R.id.btnReverse);
        localisation = findViewById(R.id.localisation);

        // Hide all sensor lines and additional indicators initially
        hideAllLinesBack();
        hideAllLinesLeft();
        hideAllLinesRight();
        stopSign.setVisibility(View.INVISIBLE);
        feu.setVisibility(View.INVISIBLE);
        vitesse.setVisibility(View.INVISIBLE);
    }

    /**
     * Listen to real-time sensor data updates from Firebase.
     */
    private void listenToSensorData() {
        DatabaseReference sensorDataRef = databaseRef.child("sensor-data");

        sensorDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sensorSpeed = snapshot.child("Speed").getValue(Integer.class) != null
                        ? snapshot.child("Speed").getValue(Integer.class)
                        : 0;

                // Update speed text and progress
                speedTextView.setText(sensorSpeed + " km/h");
                circularSpeedIndicator.setProgress(sensorSpeed);

                // Check and update speed sign visibility
                updateSpeedSignVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching sensor data: " + error.getMessage());
            }
        });
    }

    /**
     * Listen to real-time detected signs updates from Firebase.
     */
    private void listenToDetectedSigns() {
        DatabaseReference detectedSignsRef = databaseRef.child("detected-signs");

        detectedSignsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                detectedSpeed = snapshot.child("speed").getValue(Integer.class) != null
                        ? snapshot.child("speed").getValue(Integer.class)
                        : 0;

                String type = snapshot.child("type").getValue(String.class) != null
                        ? snapshot.child("type").getValue(String.class)
                        : "";

                // Update UI based on detected sign type
                switch (type.toLowerCase()) {
                    case "stop_sign":
                        stopSign.setVisibility(View.VISIBLE);
                        feu.setVisibility(View.INVISIBLE);
                        vitesse.setVisibility(View.INVISIBLE);
                        break;

                    case "red_light":
                        feu.setVisibility(View.VISIBLE);
                        stopSign.setVisibility(View.INVISIBLE);
                        vitesse.setVisibility(View.INVISIBLE);
                        break;

                    default:
                        stopSign.setVisibility(View.INVISIBLE);
                        feu.setVisibility(View.INVISIBLE);
                        break;
                }

                // Check and update speed sign visibility
                updateSpeedSignVisibility();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching detected signs: " + error.getMessage());
            }
        });
    }

    /**
     * Update the visibility of the speed sign based on speed comparison.
     */
    private void updateSpeedSignVisibility() {
        if (detectedSpeed > sensorSpeed) {
            vitesse.setVisibility(View.VISIBLE);
        } else {
            vitesse.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Utility methods to hide lines initially.
     */
    private void hideAllLinesBack() {
        lineB1.setVisibility(View.INVISIBLE);
        lineB2.setVisibility(View.INVISIBLE);
        lineB3.setVisibility(View.INVISIBLE);
    }

    private void hideAllLinesLeft() {
        lineL1.setVisibility(View.INVISIBLE);
        lineL2.setVisibility(View.INVISIBLE);
        lineL3.setVisibility(View.INVISIBLE);
    }

    private void hideAllLinesRight() {
        lineR1.setVisibility(View.INVISIBLE);
        lineR2.setVisibility(View.INVISIBLE);
        lineR3.setVisibility(View.INVISIBLE);
    }
}
