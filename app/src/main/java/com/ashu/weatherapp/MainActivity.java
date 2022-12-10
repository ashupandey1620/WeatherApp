package com.ashu.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV,tempTV,conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;

    private ArrayList<WeatherRVmodule> weatherRVmoduleArrayList;
    private RVAdapter rvAdapter;

    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;

    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL=findViewById(R.id.idRLHome);
        loadingPB=findViewById(R.id.idProgressBar);
        cityNameTV=findViewById(R.id.idTVCityName);
        tempTV=findViewById(R.id.idTVTemp);
        conditionTV=findViewById(R.id.idTVCondition);
        weatherRV=findViewById(R.id.idRVWeather);
        cityEdt=findViewById(R.id.idEdtCity);
        backIV=findViewById(R.id.idIVBackground);
        iconIV=findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);


        weatherRVmoduleArrayList = new ArrayList<>();
        rvAdapter = new RVAdapter(this,weatherRVmoduleArrayList);
        weatherRV.setAdapter(rvAdapter);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude(),location.getLatitude());

        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Enter City NAme", Toast.LENGTH_SHORT).show();
                }else
                {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
              if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
              {
                  Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
              }
              else{
                  Toast.makeText(this, "provide the permissions", Toast.LENGTH_SHORT).show();
                  finish();
              }
        }
    }


    private String getCityName(double longitude,double latitude)
    {
       String cityName ="not found";
        Geocoder gcd =  new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude ,10);
            for(Address adr : addresses)
            {
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;
                    }
                    else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "user city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch(IOException e)
        {
            e.printStackTrace();
        }
        return cityName;
    }



    private void getWeatherInfo(String cityName)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=65402b48c2404b0d934143935222711&q="+cityName+"&days=1&aqi=no&alerts=no" ;
        cityNameTV.setText(cityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVmoduleArrayList.clear();

                try {
                    String tempurature = response.getJSONObject("current").getString("temp_c");
                    tempTV.setText(tempurature+"°c");
                    int isDay = response.getJSONObject("current").getInt(" is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if(isDay==1)
                    {
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1577900984109-8f2ae0da1316?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIV);
                    }else
                    {
                        //night
                        Picasso.get().load("https://unsplash.com/photos/-jcP3xbN88w").into(backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcast1 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hour = forcast1.getJSONArray("hour");

                    for(int i=0;i<hour.length();i++)
                    {
                        JSONObject hourObject = hour.getJSONObject(i);
                        String time =hourObject.getString("time");
                        String tempe =hourObject.getString("temp_c");
                        String img =hourObject.getJSONObject("condition").getString("icon");
                        String wind =hourObject.getString("wind_kph");

                        weatherRVmoduleArrayList.add(new WeatherRVmodule(time,tempe,img,wind));
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}