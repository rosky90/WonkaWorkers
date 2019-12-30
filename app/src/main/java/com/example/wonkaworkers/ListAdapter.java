package com.example.wonkaworkers;

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

    Context context;
    private ArrayList<OompaBasicInfo> workers;
    int resource;

    public ListAdapter(Context context, int resource, int textViewResourceId, ArrayList<OompaBasicInfo> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.workers = objects;
        this.resource = resource;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){

        if(convertView ==null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.show_item,null,true);
        }

        OompaBasicInfo worker = getItem(position);
        TextView txtName = convertView.findViewById(R.id.name_worker);
        TextView txtProfession = convertView.findViewById(R.id.profession_worker);
        ImageView imgWorker = convertView.findViewById(R.id.icon_listView);

        txtName.setText(worker.getFirst_name().concat(" ").concat(worker.getLast_name()));
        txtProfession.setText(worker.getProfession());
        Picasso.with(context).load(worker.getImage()).into(imgWorker);

        return super.getView(position,convertView,parent);

    }

}
