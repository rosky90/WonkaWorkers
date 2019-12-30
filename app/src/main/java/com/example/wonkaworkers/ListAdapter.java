
////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                //
//                                  @author: Daniel Rosquellas Montero                            //
//                                                                                                //
//                                          WonkaWorkers                                          //
//                                                                                                //
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.example.wonkaworkers;

//////////  IMPORTS ////////////////////////////////////////////////////////////////////////////////

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<OompaBasicInfo> {

    //////////  VARIABLE DECLARATION    ////////////////////////////////////////////////////////////

    //////////  CLASS OBJECTS   ////////////////////////////////////////////////////////////////////

    Context context;
    private ArrayList<OompaBasicInfo> workers;
    int resource;

    //////////  CONSTRUCTOR    /////////////////////////////////////////////////////////////////////

    public ListAdapter(Context context, int resource, int textViewResourceId, ArrayList<OompaBasicInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.workers = objects;
        this.resource = resource;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        //In the next if block we inflate the layout resource

        if(convertView ==null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.show_item,null,true);
        }

        //With this we get every worker (one out a time)

        OompaBasicInfo worker = getItem(position);

        //////////  USER INTERFACE OBJECTS  ////////////////////////////////////////////////////////////

        TextView txtName = convertView.findViewById(R.id.name_worker);
        TextView txtProfession = convertView.findViewById(R.id.profession_worker);
        ImageView imgWorker = convertView.findViewById(R.id.icon_listView);

        //In the next block we set the data to the widgets for display it

        txtName.setText(worker.getFirst_name().concat(" ").concat(worker.getLast_name()));
        txtProfession.setText(worker.getProfession());
        Picasso.with(context).load(worker.getImage()).into(imgWorker);

        return super.getView(position,convertView,parent);

    }

}
