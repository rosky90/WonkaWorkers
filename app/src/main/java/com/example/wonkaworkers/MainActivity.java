
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

import android.content.Intent;
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

////////////////////////////////////////////////////////////////////////////////////////////////////

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    //////////  VARIABLE DECLARATION    ////////////////////////////////////////////////////////////

    //////////  USER INTERFACE OBJECTS  ////////////////////////////////////////////////////////////

    RadioGroup genderFilter;
    RadioButton all;
    RadioButton male;
    RadioButton female;
    Spinner spnProfessions;
    ListView listWorkers;

    //////////  CLASS OBJECTS   ////////////////////////////////////////////////////////////////////

    ListAdapter stylazer;                           //Adapter User-Friendly to the ListView
    ArrayList crew;                                 //ArrayList with all the workers
    ArrayList sexualizedCrew;                       //ArrayList with the male or female workers
    List<String> professions = new ArrayList<>();   //List with the professions of the crew
    boolean sexualized=false;                       //Boolean to help us combine filters

    //////////  ACTIVITY CONSTRUCTION (onCreate)   /////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////  VARIABLES - WIDGETS RELATIONS   ////////////////////////////////////////////////

        listWorkers = findViewById(R.id.listWorkers);
        genderFilter = findViewById(R.id.gender_filter);
        all = findViewById(R.id.all);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        spnProfessions = findViewById(R.id.spnr_professions);
        genderFilter.setOnCheckedChangeListener(this);

        //////////  WIDGETS METHODS  ///////////////////////////////////////////////////////////////

        /**
         * The next method generates an event for when the user selects a job from de Spinner
         * the ListView will show all the workers whom profession atribute equals the parameter
         * from the Spinner. This method uses de boolean "sexualized" to control if the gender filter
         * is activated or not. if it is it will show only de male/female workers instead of the whole crew
         */

        spnProfessions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String profession;

                profession = spnProfessions.getSelectedItem().toString();

                if (sexualized){
                    filterByProfession(profession,sexualizedCrew);
                }else{
                    filterByProfession(profession,crew);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        /**
         * The next method generates an event for when the user chooses a worker to see more info,
         * this method take the worker's ID and pass it to the Activity "OompaInfoActivity"
         */

        listWorkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                OompaBasicInfo worker;

                worker = (OompaBasicInfo) listWorkers.getItemAtPosition(position);

                Intent intntScape = new Intent(MainActivity.this, OompaInfoActivity.class);
                intntScape.putExtra("idWorker",worker.getId());
                startActivity(intntScape);


            }
        });

        //The next line of code trigger the task especified below asynchronously

        new getJsonTask().execute("https://2q2woep105.execute-api.eu-west-1.amazonaws.com/napptilus/oompa-loompas?page=1");

    }

    //////////  CLASS METHODS  /////////////////////////////////////////////////////////////////////

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
         * In the next method will recieve the String returned in the previous method ("doInBackground()")
         * We know that te expected result is a JSON array, that's why the method implements an if block
         * to make sure of that. After that, using Gson library, will create an Array of objects extracted
         * from the JSON. Will use the "getProfessions()" function to create the Spinner in order with
         * the jobs that the crew perform. And finally show'em on the ListView
         *
         * @param result - String returned from the "doInBackground()" method
         */

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

    /**
     * This method is an Override from the RadioGroup class. This method will check which button is
     * pressed and act in consecuence. If "All" is pressed then will set the boolean false and paint
     * the ListView with all the crew. If it's any of the other two buttons it will filter by the
     * gender chose, establish the boolean as true, and paint the list once filtered
     *
     * @param radioGroup
     * @param i
     */

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        if (all.isChecked()){
            sexualized=false;
            makeList(crew);
        }else if (male.isChecked()){
            sexualizedCrew = filterByGender(0);
            sexualized=true;
            makeList(sexualizedCrew);
        }else {
            sexualizedCrew = filterByGender(1);
            sexualized=true;
            makeList(sexualizedCrew);
        }

    }

    /**
     * This function will get the workers, extract the jobs they do and put it in a List without
     * redundancies (using a method below explained)
     *
     * @param workers - ArrayList of workers to etract their jobs
     * @return - List of the jobs from the workers
     */

    private List getProfessions(ArrayList<OompaBasicInfo> workers){

        List<String> ocupations = new ArrayList<>();

        for (int i=0;i<workers.size();i++){

            OompaBasicInfo worker = workers.get(i);

            ocupations.add(worker.getProfession());

        }

        ocupations = depercateRedundancies(ocupations);

        return ocupations;

    }

    /**
     * This function will get the List of the workers's jobs and eliminate all the redundancies
     *
     * @param redundantItems - List without care if has repeated elements
     * @return - List of the elements getted without repetitions
     */

    private List depercateRedundancies(List redundantItems){

        List<String> finalList = new ArrayList<>();

        finalList.add("All");

        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(redundantItems);
        finalList.addAll(hashSet);

        return finalList;

    }

    /**
     * This procedure will take a List of elements and it will display it in the Spinner
     *
     * @param elementsToShow - List of elements to display in the Spinner
     */

    private void createSpinner (List elementsToShow){

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item
                ,elementsToShow);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnProfessions.setAdapter(spinnerArrayAdapter);

    }

    /**
     * This procedure will construct the ListAdapter and apply it to the ListView with the ArrayList
     * of workers given
     *
     * @param workersToShow - ArrayList of OompaBasicInfo to show in the ListView
     */

    private void makeList (ArrayList<OompaBasicInfo> workersToShow){

        stylazer = new ListAdapter(getApplicationContext(),R.layout.show_item,R.id.black,workersToShow);

        listWorkers.setAdapter(stylazer);

    }

    /**
     * The next function will take the crew ArrayList, defined and constructed above, and cast it in
     * function if we want the male workers or the female workers.
     *
     * @param selection - 0 = filter Men / 1 = filter Women
     * @return -ArrayList of the crew filtered by sex
     */

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

    /**
     * The next procedure will check if the option "All" is selected on the Spinner, in that cas it
     * will be painting the "crew" ArrayList on the ListView, if not it will take the profession parameter
     * and compares it with the job of every worker that is inside the second parameter and, in that case,
     * it will add the worker to a new Arraylist and, once it break the loop will paint it into the ListView
     *
     * @param profession - String with the profession that it is wanted to filter
     * @param workersToFilter - ArrayList to filter by profession
     */

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
