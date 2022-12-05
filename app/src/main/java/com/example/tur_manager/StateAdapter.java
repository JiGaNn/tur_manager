package com.example.tur_manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.drawable.DrawableUtils;

import java.util.List;

public class StateAdapter  extends RecyclerView.Adapter<StateAdapter.ViewHolder>{

    private final LayoutInflater inflater;
    private final List<State> states;

    StateAdapter(Context context, List<State> states) {
        this.states = states;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public StateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StateAdapter.ViewHolder holder, int position) {
        if(position == states.size()) {
            holder.costView.setText("Итого: " + State.all);
        } else {
            State state = states.get(position);
            holder.originView.setText(state.getOrigin());
            holder.destinationView.setText(state.getDestination());
            holder.costView.setText(state.getCost() + "");
            holder.link = state.getLink();
        }
    }

    @Override
    public int getItemCount() {
        return states.size()+1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView originView, destinationView, costView;
        public String link="";
        ViewHolder(View view){
            super(view);
            originView = view.findViewById(R.id.origin);
            destinationView = view.findViewById(R.id.destination);
            costView = view.findViewById(R.id.cost);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(link!="") {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}