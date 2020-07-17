package sg.edu.rp.webservices.l09_gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnStart,btnStop,btnCheck;
    TextView tvLat,tvLong;
    FusedLocationProviderClient client;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    String locationsData,folderLocation;
    double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);
        btnStart = findViewById(R.id.btnStart);
        btnCheck = findViewById(R.id.btnCheck);
        btnStop = findViewById(R.id.btnStop);

        client = LocationServices.getFusedLocationProviderClient(this);

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            }
        }

        Task<Location> task = client.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (checkPermission() == true){
                    if (location != null){
                        tvLat.setText("Latitude: " + location.getLatitude());
                        tvLong.setText("Longtitude: " + location.getLongitude());
                    }else{
                        String msg = "No Last Known Location Found";
                        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(100);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if (locationResult != null){
                    Location data = locationResult.getLastLocation();
                    lat = data.getLatitude();
                    lon = data.getLongitude();
                    String latitude = "Latitude: " + lat ;
                    String longtitude = "Longitude: " + lon;
                    File targetFile = new File(folderLocation,"data1.txt");
                    try{
                        FileWriter writer = new FileWriter(targetFile,true);
                        writer.write(latitude + "," + longtitude + "\n");
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,"Failed to write!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            }
        };

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                startService(i);
                if (checkPermission() == true){
                    client.requestLocationUpdates(locationRequest,locationCallback,null);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,MyService.class);
                stopService(i);
                client.removeLocationUpdates(locationCallback);


            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File targetFile = new File(folderLocation, "data1.txt");

                if (targetFile.exists() == true){
                    String data="";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);

                        String line = br.readLine();
                        while (line != null){
                            data += line  + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();

                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this,"Failed to read!",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this,data,Toast.LENGTH_SHORT).show();
                    Log.d("Content",data);
                }

            }
        });


    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }else{
            String msg = "Permission not Granted";
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);
            return false;
        }
    }
}
