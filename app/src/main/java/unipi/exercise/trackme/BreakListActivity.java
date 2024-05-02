package unipi.exercise.trackme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import unipi.exercise.trackme.adapter.BreakAdapter;
import unipi.exercise.trackme.model.Break;

public class BreakListActivity extends AppCompatActivity implements BreakAdapter.OnItemClickListener{
    SQLiteDatabase database;
    List<Break> breakList = new ArrayList<>();

    RecyclerView viewList;
    private static final String LONG = "longitude";
    private static final String LAT = "latitude";
    private static final String TIMESTAMP = "timestamp";
    private static final String BREAK = "speedChange";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_break_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        database = openOrCreateDatabase("trackMe.db",MODE_PRIVATE,null);
        getAllDBBreakData();
        viewList = findViewById(R.id.recyclerView);
        BreakAdapter adapter = new BreakAdapter(this, breakList, this);
        viewList.setAdapter(adapter);
        viewList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onItemClick(Break breakIncident) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("selected_break", (Serializable) breakIncident);
        startActivity(intent);
    }
    private void getAllDBBreakData() {
        Cursor cursor = database.rawQuery("Select * from Breaks",null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LONG));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LAT));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP));
                int speedChange = cursor.getInt(cursor.getColumnIndexOrThrow(BREAK));
                Break breakItem = new Break(longitude, latitude, timestamp, speedChange);
                this.breakList.add(breakItem);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}