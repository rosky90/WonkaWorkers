
////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                //
//                                  @author: Daniel Rosquellas Montero                            //
//                                                                                                //
//                                          WonkaWorkers                                          //
//                                                                                                //
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.example.wonkaworkers;

//////////  IMPORTS ////////////////////////////////////////////////////////////////////////////////

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

    //////////  VARIABLE DECLARATION    ////////////////////////////////////////////////////////////

    //////////  CLASS OBJECTS   ////////////////////////////////////////////////////////////////////

    private int idWorker;
    OompaLoompa worker;
    Bundle extraFromIntent;

    //////////  USER INTERFACE OBJECTS  ////////////////////////////////////////////////////////////

    private TextView txtOompaName, txtOompaProfession,txtOompaAge, txtOompaMail, txtOompaHeight,txtOompaCountry;
    private TextView txtOompaGender, txtOompaFavColor, txtOompaFavFood, txtOompaFavSong, txtOompaFavString,txtOompaFavQuote;
    Button btnDescription;
    ImageView imgOompa;

    //////////  ACTIVITY CONSTRUCTION (onCreate)   /////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oompa_info);

        //////////  VARIABLES - WIDGETS RELATIONS   ////////////////////////////////////////////////

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

        extraFromIntent = getIntent().getExtras();                  //With this will extract the value sended through the intent
        idWorker = extraFromIntent.getInt("idWorker");         //And store it inside a variable

        //////////  WIDGETS METHODS  ///////////////////////////////////////////////////////////////


        /**
         * The next method generates an event for when the user clicks it will show a Dialog with the
         * worker description
         */

        btnDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDetailDialog(OompaInfoActivity.this,getResources().getString(R.string.btn_description),worker.getDescription());

            }
        });

        /**
         * The next method generates an event for when the user clicks it will show a Dialog with the
         * worker mail and give us the option to send and e-mail.
         */

        txtOompaMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMailDialog(OompaInfoActivity.this,worker.getEmail());

            }
        });

        /**
         * The next method generates an event for when the user clicks it will show a Dialog with the
         * worker's favorite song
         */

        txtOompaFavSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDetailDialog(OompaInfoActivity.this,getResources().getString(R.string.txtvw_fav_song),worker.getFavorite().getSong());

            }
        });

        /**
         * The next method generates an event for when the user clicks it will show a Dialog with the
         * worker's favorite random string
         */

        txtOompaFavString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDetailDialog(OompaInfoActivity.this,getResources().getString(R.string.txtvw_fav_string),worker.getFavorite().getRandom_string());

            }
        });

        /**
         * The next method generates an event for when the user clicks it will show a Dialog with the
         * worker's favorite quote
         */

        txtOompaFavQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDetailDialog(OompaInfoActivity.this,getResources().getString(R.string.txtvw_fav_quote),worker.getQuota());

            }
        });

        //The next line of code trigger the task especified below asynchronously

        new getJsonTask().execute("https://2q2woep105.execute-api.eu-west-1.amazonaws.com/napptilus/oompa-loompas/"+idWorker);

    }

    /**
     * The next task will connect to URL passed as parameter and extract all the info in there stored.
     * That will do it within the "doInBackground()" method and the String returned has to be catched
     * in the "onPostExecute()" method
     */

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

        /**
         * In the next method will recieve the String returned in the previous method ("doInBackground()").
         * Using Gson library, will create an Object of the extract data from the JSON. Will use the
         * Picasso library aswell to parse the URL of the image and paint it into the ImageView.
         * Then we use te widgets to display the information extracted from the JSON.
         *
         * @param result - String returned from the "doInBackground()" method
         */

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

    /**
     * This function will get the gender letter of the worker and depending on that will wirte "Male"
     * or "Female" in the gender TextView
     *
     * @param gender - String of the gender of the worker
     * @return - String with the complete nomenclature of the gender
     */

    private String writeGender(String gender){

        if (gender.equalsIgnoreCase("M")){
            return "Male";
        }else{
            return "Female";
        }

    }

    /**
     * This procedure will setthe text of the TextView expected to show the favorite color with that color
     *
     * @param color - String of the favorite color of the worker
     */

    private void paintFavColor(String color){

        if (color.equalsIgnoreCase("blue")){
            txtOompaFavColor.setTextColor(getResources().getColor(R.color.blue));
        }else{
            txtOompaFavColor.setTextColor(getResources().getColor(R.color.red));
        }

    }

    /**
     * This procedure will show a Dialog with the detailed information of the worker
     *
     * @param context - Context where the Dialog must show
     * @param dialogTitle - String Title of the Dialog
     * @param message - String for the message of the Dialog
     */

    private void showDetailDialog(Context context, String dialogTitle, String message){

        new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setMessage(message)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();

    }

    /**
     * This procedure will show a Dialog with the mail of the worker and it will allow us to
     * send an e-amil to that electronic address
     *
     * @param context - Context where the Dialog must show
     * @param mail - String for the e-mail of the worker
     */

    private void showMailDialog(Context context,String mail){

        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.txtvw_mail))
                .setMessage(mail)

                .setPositiveButton("Send e-mail", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intntMail = new Intent(Intent.ACTION_SENDTO);
                        intntMail.setData(Uri.parse("mailto:"+worker.getEmail()));
                        startActivity(Intent.createChooser(intntMail,"Choice email APP"));
                    }
                })

                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();

    }

}
