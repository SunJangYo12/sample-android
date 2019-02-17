package com.example.rktech.apkextractor;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button bt;
    ListView lsview;
    ArrayList<String> Listname=new ArrayList<>();
    ArrayList<String> ListpackageList=new ArrayList<>();
    ArrayList<Drawable> Listlogo=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt=findViewById(R.id.button);
        lsview=findViewById(R.id.listview);

        //load apk on button click.

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PackageManager packageManager=getPackageManager();
                for(ApplicationInfo applicationInfo:packageManager.getInstalledApplications(0)){

                    //just testing.
                    Log.i("InstalledAPK",applicationInfo.toString());
                    //get list of installed apk name.
                    Listname.add(applicationInfo.loadLabel(packageManager).toString());

                    //get list of installed package name with apk files.
                   ListpackageList.add(applicationInfo.sourceDir);

                   //get list of all apk logo.
                   Listlogo.add(applicationInfo.loadIcon(packageManager));
                }

                customAdapter customAdapter=new customAdapter(MainActivity.this,Listname,ListpackageList,Listlogo);
                lsview.setAdapter(customAdapter);
            }
        });
        lsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //create folder here where extractedAPK is folder name.
                File file=new File("/storage/emulated/0/extractedAPK/");
                if(!file.exists()){
                    //make folder if not exists.
                    file.mkdir();
                }

                //here we get packagepath .
                String PackagePath=((TextView)view.findViewById(R.id.txtPackage)).getText().toString();
                //let's test it.
                Log.e("PATH_APK",PackagePath);


                //get source directory.
                File source=new File(PackagePath);

                //get destination directory.
                File destination=new File(file.toString());


                try {
                    FileUtils.copyFileToDirectory(source,destination);
                    Toast.makeText(MainActivity.this, "File extracted at "+file, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
