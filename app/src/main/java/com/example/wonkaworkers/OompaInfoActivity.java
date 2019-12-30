package com.example.wonkaworkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class OompaInfoActivity extends AppCompatActivity {

    private int idWorker;
    private TextView txtOompaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oompa_info);

        Bundle extraFromIntent = getIntent().getExtras();

        idWorker = extraFromIntent.getInt("idWorker");

        txtOompaName = findViewById(R.id.oompa_name);

        txtOompaName.setText(String.valueOf(idWorker));
    }
}
