package com.aabv78.happyweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {


    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String APP_ID = "4a0677cb6a3d4c6686f1f735078a67c0"; // App ID to use OpenWeather data
    final long MIN_TIME = 5000; // Time between location updates (5000 milliseconds or 5 seconds)
    final float MIN_DISTANCE = 1000; // Distance between location updates (1000m or 1km)
    final int REQUEST_CODE = 123;
    final int CHANGE_CITY_CODE = 456;
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    boolean useLocation = true;
    TextView CityLabel;
    ImageView WeatherImage;
    TextView TemperatureLabel;
    String initialCity="";
    String longitude;
    String latitude;
    LocationManager locationManager; // start and stops locations request
    LocationListener locationListener; //check if the location has changed


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller);
//        setContentView(R.layout.activity_main);

        CityLabel = (TextView) findViewById(R.id.locationTV);
        WeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        TemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);
        ImageButton reloadLocation = (ImageButton) findViewById(R.id.reloadLocation);


        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityActivity.class);
                // Using startActivityForResult since we just get back the city name.
                // Providing a random request code to check against later.
                startActivityForResult(myIntent, CHANGE_CITY_CODE);
            }
        });

        reloadLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HW", "reloadLocation, getting weather for current location");
                getWeatherForNewCity(initialCity);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HW", "onResume() called");
        if(useLocation) getWeatherForCurrentLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("HW", "onActivityResult() called");

        if (requestCode == CHANGE_CITY_CODE) {
            if (resultCode == RESULT_OK) {
                String city = data.getStringExtra("City");
                Log.d("HW", "New city is " + city);

                useLocation = false;
                getWeatherForNewCity(city);
            }
        }
    }

    public void getWeatherForNewCity(String city) {
        Log.d("HW", "Getting weather for new city");
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        callWeatherApi(params);
    }

    private void getWeatherForCurrentLocation() {

        //Toast.makeText(getApplicationContext(), "Getting weather for current location 1", Toast.LENGTH_SHORT).show();

        Log.d("HW", "WeatherController: getWeatherForCurrentLocation()");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("HW","onLocationChanged()");

                //Getting new longitude and latitude
                longitude = String.valueOf( location.getLongitude());
                latitude = String.valueOf( location.getLatitude());
                Log.d("HW","Longitude: " + longitude + ", Latitude:" + latitude);

                //Toast.makeText(getApplicationContext(), "Getting weather for current location 2", Toast.LENGTH_SHORT).show();
                initialCity="";

                //Parameters for the api calling
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                callWeatherApi(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        Toast.makeText(getApplicationContext(), "Getting current location 3", Toast.LENGTH_SHORT).show();
        locationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Log.d("HW","onRequestPermissionsResult - permission GRANTED");
                getWeatherForCurrentLocation();
            } else {
                Log.d("HW","onRequestPermissionsResult - permission DENIED");
            }
        }
    }

    private void callWeatherApi(RequestParams params) {

        // AsyncHttpClient belongs to the loopj dependency.
        AsyncHttpClient client = new AsyncHttpClient();

        // Making an HTTP GET request by providing a URL and the parameters.
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("HW", "Success: " + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);

                refreshUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {

                Log.e("HW", "Failure " + e.toString());
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();

                Log.d("HW", "Status code " + statusCode);
                Log.d("HW", "Response " + response.toString());
            }

        });
    }



    private void refreshUI(WeatherDataModel weather) {
        TemperatureLabel.setText(weather.getWeatherTemperature());
        Log.d("HW", "Temperature: " + weather.getWeatherTemperature());

        CityLabel.setText(weather.getWeatherCity());
        Log.d("HW", "City name: " + weather.getWeatherCity());

        int resourceID = getResources().getIdentifier(weather.getWeatherIcon(), "drawable", getPackageName());
        WeatherImage.setImageResource(resourceID);
        Log.d("HW", "Icon code: " + resourceID);

        if (initialCity.isEmpty()){
            initialCity= weather.getWeatherCity();
        }
    }


    // Freeing up resources when the app enters the paused state.
    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null) locationManager.removeUpdates(locationListener);
    }


}
