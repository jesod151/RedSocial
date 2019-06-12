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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.redsocial.DataBaseObjects.Friend;
import com.example.redsocial.DataBaseObjects.Notificacion;
import com.example.redsocial.DataBaseObjects.Post;
import com.example.redsocial.FeedFragment;
import com.example.redsocial.NotificationFragment;
import com.example.redsocial.R;
import com.example.redsocial.SearchFragment;
import com.example.redsocial.UserFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
            if (notifs.get(i).isLeido()){
                viewHolder.desaparecer();
            }else {
                viewHolder.texto.setText(notifs.get(i).getTexto() + "\n" + getDistanciaFecha(notifs.get(i).getFechaEnvio()));
                if (!notifs.get(i).getSenderImageUrl().equals("")) {
                    Glide.with(context).load(notifs.get(i).getSenderImageUrl()).into(viewHolder.imgUser);
                } else {
                    viewHolder.imgUser.setVisibility(View.GONE);
                }
                viewHolder.btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference notifDocument = db.collection("Notifications").document(notifs.get(i).getNotifId());
                        notifDocument.update("leido", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                DocumentReference newFriend1 = db.collection("Friends").document();
                                Friend friend1 = new Friend(newFriend1.getId(), notifs.get(i).getSender(), notifs.get(i).getReceiver(), new Date());
                                newFriend1.set(friend1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Toast.makeText(context, "Invitacion de amistad enviada a "+ users.get(i).getNombre(), Toast.LENGTH_LONG).show();
                                    }
                                });;

                                DocumentReference newFriend2 = db.collection("Friends").document();
                                Friend friend2 = new Friend(newFriend2.getId(), notifs.get(i).getReceiver(), notifs.get(i).getSender(), new Date());
                                newFriend2.set(friend2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Toast.makeText(context, "Invitacion de amistad enviada a "+ users.get(i).getNombre(), Toast.LENGTH_LONG).show();
                                    }
                                });;
                            }
                        });
                        viewHolder.texto.setText("Has aceptado la solicitud. Ahora tienes un nuevo amigo.");
                        viewHolder.btnAceptar.setVisibility(View.GONE);
                        viewHolder.btnCancelar.setVisibility(View.GONE);

                    }
                });
                viewHolder.btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference notifDocument = db.collection("Notifications").document(notifs.get(i).getNotifId());
                        notifDocument.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Has cancelado la solicitud", Toast.LENGTH_SHORT);
                            }
                        });
                        viewHolder.desaparecer();

                    }
                });
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
            texto = itemView.findViewById(R.id.txtUserNotif);
            imgUser = itemView.findViewById(R.id.imgUserNotif);
            btnAceptar = itemView.findViewById(R.id.btnAddFriendNotif);
            btnCancelar = itemView.findViewById(R.id.btnCancelNotif);

        }

        public void desaparecer(){
            texto.setVisibility(View.GONE);
            imgUser.setVisibility(View.GONE);
            btnCancelar.setVisibility(View.GONE);
            btnAceptar.setVisibility(View.GONE);
        }
    }

}


