package com.kunaldhongadi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DataListAdapter extends ArrayAdapter<DataListView> {
    private Context mcontext;
    int mResource;

    public DataListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<DataListView> objects) {
        super(context, resource, objects);
        this.mcontext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String label = getItem(position).getLabel();
        String data = getItem(position).getData();

        DataListView data1 = new DataListView(label,data);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView labelText = (TextView)convertView.findViewById(R.id.label);
        TextView dataText = (TextView)convertView.findViewById(R.id.data);

        labelText.setText(label);
        dataText.setText(data);

        return convertView;
    }

}
