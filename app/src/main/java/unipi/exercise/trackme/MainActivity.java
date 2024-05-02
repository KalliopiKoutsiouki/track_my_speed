package unipi.exercise.trackme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView loadingText;
    ProgressBar progressBar;
    Button btnLocPerm;
    Button btnShowRes;
    boolean tracking = false;
    private static final int LOCATION_CODE = 1717;
    List<Float> speedRecordList = new ArrayList<>();
    SQLiteDatabase database;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.instantiateComponents();
        this.setStartButtonBehavior();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        this.instantiateDB();
    }

    private void setStartButtonBehavior() {
        btnLocPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tracking) {
                    stopTracking();
                    btnLocPerm.setText("Start");
                    progressBar.setVisibility(View.GONE);
                    loadingText.setVisibility(View.GONE);
                } else {
                    startTracking();
                    btnLocPerm.setText("Stop");
                    progressBar.setVisibility(View.VISIBLE);
                    loadingText.setVisibility(View.VISIBLE);
                }
                tracking = !tracking; // Toggle the tracking state
            }
        });
    }
    private void instantiateComponents() {
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
        btnLocPerm = findViewById(R.id.btnLocPerm);
        btnShowRes = findViewById(R.id.btnShowRes);
    }

    private void instantiateDB() {
        database = openOrCreateDatabase("trackMe.db",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Breaks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "longitude REAL," +
                "latitude REAL," +
                "timestamp TEXT," +
                "speedChange REAL" +
                ")");
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000, 0, locationListener);
    }

    private void stopTracking() {
        Log.d("LIST", speedRecordList.get(0).toString());
        speedRecordList.clear();
        btnShowRes.setVisibility(View.VISIBLE);
        locationManager.removeUpdates(locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Log.d("Speed ", Float.toString(location.getSpeed()));
            float currentSpeed = location.getSpeed();
            speedRecordList.add(location.getSpeed());
            if (speedRecordList.size() >= 2) {
                float previousSpeed = speedRecordList.get(speedRecordList.size() - 2);
                float speedChange = currentSpeed - previousSpeed;
                if (speedChange <= -6) {
                    String insertStatement = "INSERT INTO Breaks (longitude, latitude, timestamp, speedChange) " +
                            "VALUES ('" + location.getLongitude() + "', " +
                            "'" + location.getLatitude() + "', " +
                            "'" + getCurrentTimestamp() + "', " +
                            "'" + speedChange + "')";
                    database.execSQL(insertStatement);
                }
            }
        }
    };

    // this is the callback of ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_CODE){
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 0, locationListener);
            }
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void openResultList(View view) {
        startActivity(new Intent(this, BreakListActivity.class));
    }
}