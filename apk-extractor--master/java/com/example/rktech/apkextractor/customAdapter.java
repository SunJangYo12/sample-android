package com.example.rktech.apkextractor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RKTECH on 28/01/2018.
 */

public class customAdapter extends ArrayAdapter {

    ArrayList<String> Listname=new ArrayList<>();
    ArrayList<String> ListpackageList=new ArrayList<>();
    ArrayList<Drawable> Listlogo=new ArrayList<>();
    Activity activity;
    public customAdapter( Activity activity,ArrayList<String> Listname,
                         ArrayList<String> ListpackageList,
                         ArrayList<Drawable> Listlogo) {
        super(activity,R.layout.custom_layout,Listname);
        this.activity=activity;
        this.Listlogo=Listlogo;
        this.Listname=Listname;
        this.ListpackageList=ListpackageList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.custom_layout,null);

        //find view's here.
        TextView txtAPKNAME,txtPackageName;
        ImageView imageView;

         txtAPKNAME=view.findViewById(R.id.txtName);
        txtPackageName=view.findViewById(R.id.txtPackage);
        imageView=view.findViewById(R.id.imageView);

        //now set the values.
        txtAPKNAME.setText(Listname.get(position));
        txtPackageName.setText(ListpackageList.get(position));
        imageView.setImageDrawable(Listlogo.get(position));

        return view;
    }
}
