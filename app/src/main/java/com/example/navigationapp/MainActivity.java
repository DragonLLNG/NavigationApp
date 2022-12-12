package com.example.navigationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "demo";
    private static int AUTOCOMPLETE_REQUEST_CODE_START = 1;
    private static int AUTOCOMPLETE_REQUEST_CODE_END = 0;
    private List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);
    FragmentActivity binding;
    Direction direction = new Direction();
    private final OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the autocomplete intent.
        if (!Places.isInitialized()) {
            Places.initialize(this, "AIzaSyBCluV-ZK2DE2ul9xm1mALBnpD7corMkXM", Locale.US);
        }

        //Start
        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              getStart();
            }
        });

        //End
        findViewById(R.id.buttonEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEnd();
            }
        });


        //Direction
        findViewById(R.id.buttonGetDirection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/directions/json").newBuilder()
                        .addQueryParameter("destination", direction.getEnd())
                        .addQueryParameter("origin", direction.getStart())
                        .addQueryParameter("key", "AIzaSyBCluV-ZK2DE2ul9xm1mALBnpD7corMkXM")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if(response.isSuccessful()){
                            String body = response.body().string();
                            Log.d(TAG, "onResponse: "+body);
                        }
                    }
                });
            }
        });





    }

    public void getStart(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_START);
    }
    public void getEnd(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_END);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE_START) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                TextView startLocation = findViewById(R.id.textViewStartLocation);
                startLocation.setText(place.getAddress());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());

                direction.setStart(place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

        else if(requestCode == AUTOCOMPLETE_REQUEST_CODE_END) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                TextView startLocation = findViewById(R.id.textViewEndLocation);
                startLocation.setText(place.getAddress());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                direction.setEnd(place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



}