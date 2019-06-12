package com.example.redsocial.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Notificacion;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.FeedFragment;
import com.example.redsocial.NotificationFragment;
import com.example.redsocial.R;
import com.example.redsocial.SearchFragment;
import com.example.redsocial.UserFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class RecyclerViewAdapterNotification extends RecyclerView.Adapter<RecyclerViewAdapterNotification.ViewHolder> {


    private Context context;
    private ArrayList<Notificacion> notifs;
    private NotificationFragment notificationFragment= null;
    private UserFragment userFragment = null;
    private SearchFragment searchFragment = null;
    private UserPreferences userPreferences;

    public RecyclerViewAdapterNotification(Context context, ArrayList<Notificacion> notifs, NotificationFragment notificationFragment) {
        this.context = context;
        this.notificationFragment = notificationFragment;
        this.userPreferences = new UserPreferences(context);
        this.notifs = sortByDate(notifs);
    }

    @NonNull
    @Override
    public RecyclerViewAdapterNotification.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifications_layout, viewGroup, false);
        RecyclerViewAdapterNotification.ViewHolder holder = new RecyclerViewAdapterNotification.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterNotification.ViewHolder viewHolder, int i) {
        if(notifs.get(i).getTipo().equals("SolicitudAmistad")){
            viewHolder.texto.setText(notifs.get(i).getTexto() + "\n" + getDistanciaFecha(notifs.get(i).getFechaEnvio()));
            if(!notifs.get(i).getSenderImageUrl().equals("")){
                Glide.with(context).load(notifs.get(i).getSenderImageUrl()).into(viewHolder.imgUser);
            }
            else{
                viewHolder.imgUser.setVisibility(View.GONE);
            }
        }
        //De existir mas notificaciones se deben escribir los inicializadores aqui

    }

    private String getDistanciaFecha(Date date){

        Date actual = new Date();
        long diff = actual.getTime() - date.getTime();
        int diffMinutes = (int) Math.floor(diff / (60 * 1000) % 60);
        int diffHours = (int) Math.floor(diff / (60 * 60 * 1000));

        if(diffHours >= 24){
            return "hace " + Double.toString((int) Math.floor(diffHours / 24)) + " dias";
        }
        if(diffHours >= 1){
            return "hace " + Integer.toString(diffHours) + " horas";
        }
        if(diffMinutes >= 60){
            return "hace " + Double.toString((int) Math.floor(diffMinutes / 60)) + " horas";
        }
        if(diffMinutes >= 1){
            return "hace " + Integer.toString(diffMinutes) + " minutos";
        }

        return "hace menos de un minuto";
    }

    @Override
    public int getItemCount() { return notifs.size(); }

    public ArrayList<Notificacion> sortByDate(ArrayList<Notificacion> notifs){
        Collections.sort(notifs, new Comparator<Notificacion>() {
            @Override
            public int compare(Notificacion o1, Notificacion o2) {
                Date actual = new Date();
                long diff = o1.getFechaEnvio().getTime() - o2.getFechaEnvio().getTime();
                if(diff > 0) {
                    return -1;
                }
                else{
                    return 1;
                }
            }
        });
        return notifs;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView texto;
        private ImageView imgUser;
        private Button btnAceptar, btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.i("view", "txt");
            texto = itemView.findViewById(R.id.txtUserNotif);
            Log.i("view", "img");
            imgUser = itemView.findViewById(R.id.imgUserNotif);
            Log.i("view", "acep");
            btnAceptar = itemView.findViewById(R.id.btnAddFriendNotif);
            Log.i("view", "can");
            btnCancelar = itemView.findViewById(R.id.btnCancelNotif);

        }
    }

}


