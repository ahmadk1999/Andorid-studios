package com.example.androidlabproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class recycleviewadapter extends RecyclerView.Adapter<recycleviewadapter.ViewHolder>{


    private List<Properties>propertiesList;
    private ONoteListenter mOnNoteListenter;



    public recycleviewadapter(List<Properties> propertiesList, Context context,ONoteListenter oNoteListenter) {
        this.propertiesList = propertiesList;
        this.context = context;
        this.mOnNoteListenter = oNoteListenter;
    }

    private Context context;

    public recycleviewadapter() {

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view,mOnNoteListenter);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        Properties propertylist = propertiesList.get(position);
        holder.list_prices.setText(propertylist.getRentalPrice());
        holder.list_avaliableDateText.setText(propertylist.getAvailabilityDate()+"");


    }

    @Override
    public int getItemCount() {
        return propertiesList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ONoteListenter oNoteListenter;
        TextView list_prices;
        TextView list_avaliableDateText;
        public ViewHolder(@NonNull View itemView,ONoteListenter oNoteListenter) {
            super(itemView);
            list_prices= itemView.findViewById(R.id.price);
            list_avaliableDateText= itemView.findViewById(R.id.avaliable_Date);
            this.oNoteListenter= oNoteListenter;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    oNoteListenter.onclick(position);
                }
            });
        }


        @Override
        public void onClick(View view) {

        }
    }
    public interface ONoteListenter{
        void onclick(int position);
    }
    String getId(int position){
        return propertiesList.get(position).getId();
    }
}
