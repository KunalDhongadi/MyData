package com.kunaldhongadi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Services> mServices;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Services> services) {
        this.mInflater = LayoutInflater.from(context);
        this.mServices = services;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_retrieval_list, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Services service_Name = mServices.get(position);
        holder.setServiceName(service_Name.getsServiceName());
        holder.setDate(service_Name.getsDate());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mServices.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView serviceName;
        TextView sDate;

        ViewHolder(View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.service_name);
            sDate = itemView.findViewById(R.id.retrieved_date);
        }

        public void setServiceName(String name) {
            serviceName.setText(name);
        }

        public void setDate(String date) {
            sDate.setText(date);
        }

    }

}
