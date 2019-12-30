package com.example.wonkaworkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OompaInfoActivity extends AppCompatActivity {

    private int idWorker;
    OompaLoompa worker;

    private TextView txtOompaName, txtOompaProfession,txtOompaAge, txtOompaMail, txtOompaHeight,txtOompaCountry;
    private TextView txtOompaGender, txtOompaFavColor, txtOompaFavFood, txtOompaFavSong, txtOompaFavString,txtOompaFavQuote;
    Button btnDescription;
    ImageView imgOompa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oompa_info);
        txtOompaName = findViewById(R.id.oompa_name);
        txtOompaProfession = findViewById(R.id.oompa_job);
        txtOompaMail = findViewById(R.id.oompa_mail);
        txtOompaAge = findViewById(R.id.oompa_age);
        txtOompaHeight = findViewById(R.id.oompa_height);
        txtOompaCountry = findViewById(R.id.oompa_country);
        txtOompaGender = findViewById(R.id.oompa_gender);
        txtOompaFavColor = findViewById(R.id.oompa_color);
        btnDescription = findViewById(R.id.oompa_description);
        txtOompaFavFood = findViewById(R.id.oompa_food);
        txtOompaFavSong = findViewById(R.id.oompa_song);
        txtOompaFavString = findViewById(R.id.oompa_string);
        txtOompaFavQuote = findViewById(R.id.oompa_quote);
        imgOompa = findViewById(R.id.oompa_img);
        Bundle extraFromIntent = getIntent().getExtras();

        idWorker = extraFromIntent.getInt("idWorker");

        new getJsonTask().execute("https://2q2woep105.execute-api.eu-west-1.amazonaws.com/napptilus/oompa-loompas/"+idWorker);

    }

    private class getJsonTask extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            String jSonIntel = result;

            Gson gson = new Gson();

            worker = gson.fromJson(jSonIntel,OompaLoompa.class);

            paintFavColor(worker.getFavorite().getColor());

            Picasso.with(getApplicationContext()).load(worker.getImage()).into(imgOompa);
            txtOompaName.setText(worker.getFirst_name().concat(" ").concat(worker.getLast_name()));
            txtOompaProfession.setText(worker.getProfession());
            txtOompaAge.setText(String.valueOf(worker.getAge()));
            txtOompaMail.setText(worker.getEmail());
            txtOompaHeight.setText(String.valueOf(worker.getHeight()));
            txtOompaCountry.setText(worker.getCountry());
            txtOompaGender.setText(writeGender(worker.getGender()));
            txtOompaFavColor.setText(worker.getFavorite().getColor());
            txtOompaFavFood.setText(worker.getFavorite().getFood());

        }
    }

    private String writeGender(String gender){

        if (gender.equalsIgnoreCase("M")){
            return "Male";
        }else{
            return "Female";
        }

    }

    private void paintFavColor(String color){

        if (color.equalsIgnoreCase("blue")){
            txtOompaFavColor.setTextColor(getResources().getColor(R.color.blue));
        }else{
            txtOompaFavColor.setTextColor(getResources().getColor(R.color.red));
        }

    }

}
