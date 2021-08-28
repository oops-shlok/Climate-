package com.example.hackathondelta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView current,temp_input;
    String url = "https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String apikey = "5455ed0d36d582ec585622a779858ed6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current = (TextView) findViewById(R.id.current);
        temp_input=(TextView) findViewById(R.id.temp_input);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherAPI weatherAPI = retrofit.create(weatherAPI.class);
        Call<DataClass> dataClassCall = weatherAPI.getweather(current.getText().toString().trim(),apikey);
        dataClassCall.enqueue(new Callback<DataClass>() {
            @Override
            public void onResponse(Call<DataClass> call, Response<DataClass> response) {
                if(response.code()==404){
                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }else if(!(response.isSuccessful())){
                    Toast.makeText(MainActivity.this,response.code(),Toast.LENGTH_SHORT).show();
                    return;
                }
                DataClass dataClass = response.body();
                Double temp = dataClass.getMain().getTemp();
                Integer temperature = (int) (temp-273.15);
                temp_input.setText(String.valueOf(temperature)+" C");
            }

            @Override
            public void onFailure(Call<DataClass> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            try {
                String city = hereloc(location.getLatitude(), location.getLongitude());
                current.setText(city);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    try {
                        String city = hereloc(location.getLatitude(), location.getLongitude());
                        current.setText(city);
                    }catch(Exception e){
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"Not Found",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String hereloc(double lat, double lon){
        String cityname = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address>addresses;
        try{
            addresses=geocoder.getFromLocation(lat,lon,10);
            if(addresses.size()>0){
                for(Address adr:addresses){
                    if(adr.getLocality()!=null&& adr.getLocality().length()>0){
                        cityname = adr.getLocality();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityname;
    }


}