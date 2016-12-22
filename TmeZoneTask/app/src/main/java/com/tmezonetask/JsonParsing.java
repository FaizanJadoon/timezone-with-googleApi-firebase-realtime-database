package com.tmezonetask;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Faizan on 21/12/2016.
 */
public class JsonParsing {

    private FirebaseDatabase database;
    private DatabaseReference myref;

   public String parseData(URL url) throws IOException {
       String jsonResponse= "";
       HttpURLConnection urlConnection = null;
       InputStream inputStream = null;
       try {
           urlConnection = (HttpURLConnection)url.openConnection();
           urlConnection.setRequestMethod("GET");
           urlConnection.setReadTimeout(10000);
           urlConnection.setConnectTimeout(15000);
           urlConnection.connect();
           inputStream = urlConnection.getInputStream();
           jsonResponse = readFromStream(inputStream);
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } finally {
           if (urlConnection != null) {
               urlConnection.disconnect();
           }
           if (inputStream != null) {
               // function must handle java.io.IOException here
               inputStream.close();
           }
       }
       return jsonResponse;
   }
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public void saveToFireBase(String key, String value, final TextView lastTime){
        try {
            database = FirebaseDatabase.getInstance();
            myref = database.getReference(key);
            myref.setValue(value);
            ValueEventListener valueEventListener = myref.addValueEventListener(new ValueEventListener() {
                String str = "";
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String result = dataSnapshot.getValue(String.class);
                    lastTime.setText(result.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception ex){
            throw ex;
        }
    }
}
