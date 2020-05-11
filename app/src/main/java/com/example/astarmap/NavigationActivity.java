package com.example.astarmap;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

public class NavigationActivity extends AppCompatActivity {

    private int mIndexQuestion;
    ImageView map;
    Intent intent;
    HashMap<Integer,int[]> locationMap=new HashMap<>();
    int[] current;
    List<String> locations=new ArrayList<>();
    List<String> destinations=new ArrayList<>();
    //service binder and connection
    NaviagationService.pathfindBinder mbinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder= (NaviagationService.pathfindBinder) service;
            mbinder.imagetoArray(map);
            Log.d("NavigationService","onServiceConnected");
            //if(current==null) {mbinder.findpath(map,locationMap.get(9),locationMap.get(mIndexQuestion));}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("NavigationService","onServiceDisconnected");
        }
    };

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Log.d("NavigationActivity","onCreate");
        initlocation();

        intent= new Intent(this,NaviagationService.class);
        map=findViewById(R.id.map);
        initspinner();
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void initspinner() {
        Spinner spinner = (Spinner) findViewById(R.id.naviagtion_currentlocationSpinner);
        Spinner spinner_des=findViewById(R.id.naviagtion_destinationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter_des = new ArrayAdapter<>(this, R.layout.spinner_item, destinations);
        adapter_des.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_des.setAdapter(adapter_des);
        spinner_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(current!=null) {
                    map.setImageBitmap(null);
                    //map.setImageBitmap(TaskParameters.getStore_map());
                    int[] destination=locationMap.get(position-1);
                    if(destination!=null) mbinder.findpath(map,current,destination);
                    else Toast.makeText(NavigationActivity.this,"Please choose your destination",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(NavigationActivity.this,"Please choose your current location",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current=locationMap.get(position-1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                current=locationMap.get(9);
            }
        });
    }

    private void initlocation() {
        LinkedHashSet<String> set=new LinkedHashSet<>();
        set.add("Water");set.add("Bathroom");set.add("Waiting Room");set.add("Food");set.add("Nurse Station");
        set.add("Clerk's Desk");set.add("X-Ray");set.add("Exit");set.add("Parking Lot");set.add("Entrance");

        locations.add("What is your current location?");
        locations.addAll(set);
        destinations.add("What is your destination?");
        destinations.addAll(set);
        locationMap.put(0,new int[]{85,14});//water
        locationMap.put(1,new int[]{80,118});//bathroom
        locationMap.put(2,new int[]{80,99});//waitingroom
        locationMap.put(3,new int[]{85,30});//food
        locationMap.put(4,new int[]{18,99});//nurse station
        locationMap.put(5,new int[]{51,122});//clerk
        locationMap.put(6,new int[]{22,30});//xray
        locationMap.put(7,new int[]{89,109});//exit
        locationMap.put(8,new int[]{15,73});//parklot(elevator)
        locationMap.put(9,new int[]{50,132});//entrance
    }
    @Override
    protected void onDestroy() {
        //unbind&stop，和activity一起
        unbindService(connection);
        stopService(intent);
        super.onDestroy();
        Log.d("NavigationActivity","Destroy");
    }
}
