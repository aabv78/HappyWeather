package com.aabv78.happyweather;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city);

        final EditText editTextField = findViewById(R.id.newCity);
        ImageButton backButton = findViewById(R.id.backButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Destroy the ChangeCityController
            }
        });

        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                String newCity = editTextField.getText().toString();
                Intent newCityIntent = new Intent(ChangeCityActivity.this, WeatherController.class);

                // Adds what was entered in the EditText as an extra to the intent.
                Log.d("HW", "Sending new city: " + newCity);
                newCityIntent.putExtra("City", newCity);

                // We started this activity for a result, so now we are setting the result.
                setResult(Activity.RESULT_OK, newCityIntent);

                // This destroys the ChangeCityController.
                finish();
                return true;
            }
        });
    }
}
