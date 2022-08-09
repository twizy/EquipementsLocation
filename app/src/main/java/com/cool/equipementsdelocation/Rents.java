package com.cool.equipementsdelocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rents extends AppCompatActivity {

    public static BreakIterator data;
    public List<String> keysList;
    public EditText client_name_edt, client_contact_edt, amount_edt, conversion_edt;
    public Spinner planets_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rents);

        client_name_edt = (EditText) findViewById(R.id.id_client_name);
        client_contact_edt = (EditText) findViewById(R.id.id_client_contact);
        amount_edt = (EditText) findViewById(R.id.id_amount);
        conversion_edt = (EditText) findViewById(R.id.id_conversion);
        planets_spinner = (Spinner) findViewById(R.id.id_planets_spinner);

        try {
            loadConvTypes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        planets_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(!amount_edt.getText().toString().isEmpty())
                {
                    String toCurr = planets_spinner.getSelectedItem().toString();
                    double euroVlaue = Double.valueOf(amount_edt.getText().toString());

                    Toast.makeText(Rents.this, "Please Wait..", Toast.LENGTH_SHORT).show();
                    try {
                        convertCurrency(toCurr, euroVlaue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(Rents.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Rents.this, "Please Enter a Value to Convert..", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void loadConvTypes() throws IOException {

        String url = "https://api.exchangeratesapi.io/latest";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(Rents.this, mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();


                Rents.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(Rents.this, mMessage, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            JSONObject  b = obj.getJSONObject("rates");

                            Iterator keysToCopyIterator = b.keys();
                            keysList = new ArrayList<String>();

                            while(keysToCopyIterator.hasNext()) {

                                String key = (String) keysToCopyIterator.next();

                                keysList.add(key);

                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, keysList );
                            planets_spinner.setAdapter(spinnerArrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        });
    }

    public void convertCurrency(final String toCurr, final double euroVlaue) throws IOException {

        String url = "https://api.exchangeratesapi.io/latest";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                Toast.makeText(Rents.this, mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();
                Rents.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(Rents.this, mMessage, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            JSONObject  b = obj.getJSONObject("rates");

                            String val = b.getString(toCurr);

                            double output = euroVlaue*Double.valueOf(val);


                            conversion_edt.setText(String.valueOf(output));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

}
