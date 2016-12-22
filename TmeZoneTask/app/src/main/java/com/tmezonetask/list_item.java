package com.tmezonetask;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.TimeZone;

public class list_item extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final EditText editSearch = (EditText)findViewById(R.id.editSearch);
        final Button buttonSearch = (Button)findViewById(R.id.buttonSearch);
        final TextView textResult = (TextView)findViewById(R.id.textResult);
        final TextView lastTime = (TextView)findViewById(R.id.textLastTime);
        final Button buttonAdd = (Button)findViewById(R.id.buttonAdd);


        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryText = getText(editSearch.getText().toString());
                JsonParsing jsonParsing = new JsonParsing();
                String result = "";
                String timeZone = "";
                try {

                    result = jsonParsing.parseData(createUrl("http://maps.googleapis.com/maps" +
                            "/api/geocode/json?address="+queryText+"&sensor=false"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultArray = jsonObject.getJSONArray("results");
                    JSONObject resultArray0 = resultArray.getJSONObject(0);
                    JSONObject objgeometry = resultArray0.getJSONObject("geometry");
                    JSONObject objlocation = objgeometry.getJSONObject("location");
                    double lat = objlocation.getDouble("lat");
                    double lng = objlocation.getDouble("lng");
                    timeZone = jsonParsing.parseData(createUrl("https://maps.googleapis.com/maps/api/timezone/json?" +
                            "location="+lat+","+lng+"&timestamp=1331766000&language=es&" +
                            "key=AIzaSyD2DkGPWnEJ-xNHEmVIUGGSE3D7dGcswMc"));

                    JSONObject timezoneJsonObject = new JSONObject(timeZone);
                    String timeZoneId = timezoneJsonObject.getString("timeZoneId");
                    textResult.setText(timeZoneId);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    buttonAdd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JsonParsing jsonParsing = new JsonParsing();
            try{
                jsonParsing.saveToFireBase("zoneId",textResult.getText().toString(), lastTime);
            } catch (Exception ex){
                throw ex;
            }
        }
    });


    }

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }

    private String getText(String query){
        String queryText="";
        for (char ch:query.toCharArray()){
            if (((int)ch)== 32){
                queryText = queryText+"%20";
            }
            else {
                queryText = queryText+ch;
            }
        }
        return queryText;
    }

}
