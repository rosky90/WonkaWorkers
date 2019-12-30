package com.example.wonkaworkers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    ListAdapter stylazer;
    RadioGroup genderFilter;
    RadioButton all;
    RadioButton male;
    RadioButton female;
    Spinner spnProfessions;
    ListView listWorkers;

    ArrayList crew;
    ArrayList sexualizedCrew;
    List<String> professions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listWorkers = findViewById(R.id.listWorkers);
        genderFilter = findViewById(R.id.gender_filter);
        all = findViewById(R.id.all);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        spnProfessions = findViewById(R.id.spnr_professions);
        genderFilter.setOnCheckedChangeListener(this);

        spnProfessions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String profession;

                profession = spnProfessions.getSelectedItem().toString();

                    filterByProfession(profession,crew);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new getJsonTask().execute("https://2q2woep105.execute-api.eu-west-1.amazonaws.com/napptilus/oompa-loompas?page=1");

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        if (all.isChecked()){
            makeList(crew);
        }else if (male.isChecked()){
            sexualizedCrew = filterByGender(0);
            makeList(sexualizedCrew);
        }else {
            sexualizedCrew = filterByGender(1);
            makeList(sexualizedCrew);
        }

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
            OompaBasicInfo[] workersList;


            if (!jSonIntel.startsWith("[") && (!jSonIntel.endsWith("]"))) {
                jSonIntel = jSonIntel.substring(jSonIntel.indexOf('['), jSonIntel.length() - 2);
            }

            Gson gson = new Gson();

            workersList = gson.fromJson(jSonIntel,OompaBasicInfo[].class);

            crew = new ArrayList<>(Arrays.asList(workersList));

            professions = getProfessions(crew);

            createSpinner(professions);

            makeList(crew);

        }
    }

    private List getProfessions(ArrayList<OompaBasicInfo> workers){

        List<String> ocupations = new ArrayList<>();

        for (int i=0;i<workers.size();i++){

            OompaBasicInfo worker = workers.get(i);

            ocupations.add(worker.getProfession());

        }

        ocupations = depercateRedundancies(ocupations);

        return ocupations;

    }

    private List depercateRedundancies(List redundantItems){

        List<String> finalList = new ArrayList<>();

        finalList.add("All");

        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(redundantItems);
        finalList.addAll(hashSet);

        return finalList;

    }

    private void createSpinner (List elementsToShow){

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item
                ,elementsToShow);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProfessions.setAdapter(spinnerArrayAdapter);

    }

    private void makeList (ArrayList<OompaBasicInfo> workersToShow){

        stylazer = new ListAdapter(getApplicationContext(),R.layout.show_item,R.id.black,workersToShow);

        listWorkers.setAdapter(stylazer);

    }

    private ArrayList filterByGender (int selection){

        switch (selection){

            case 0:

                ArrayList maleCrew = new ArrayList();

                for (int i=0;i<crew.size();i++){

                    OompaBasicInfo worker = (OompaBasicInfo) crew.get(i);

                    if (worker.getGender().equalsIgnoreCase("M")){
                        maleCrew.add(worker);
                    }

                }

                return maleCrew;

            case 1:

                ArrayList femaleCrew = new ArrayList();

                for (int i=0;i<crew.size();i++){

                    OompaBasicInfo worker = (OompaBasicInfo) crew.get(i);

                    if (worker.getGender().equalsIgnoreCase("F")){
                        femaleCrew.add(worker);
                    }
                }

                return femaleCrew;

        }

        return null;

    }

    private void filterByProfession (String profession,ArrayList<OompaBasicInfo> workersToFilter){

        ArrayList<OompaBasicInfo> professionals = new ArrayList<>();
        boolean allJobs = false;

        if (profession.equalsIgnoreCase("All")){
            allJobs=true;
        }

        if (!allJobs) {

            for (int i = 0; i < workersToFilter.size(); i++) {

                OompaBasicInfo professional = workersToFilter.get(i);

                if (professional.getProfession().equalsIgnoreCase(profession)) {
                    professionals.add(professional);
                }

            }

            makeList(professionals);

        }else{

            makeList(workersToFilter);

        }

    }

}
