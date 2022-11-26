package com.example.tur_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private EditText user_field;
    private TextView textView;
    private Button button;
    private MultiAutoCompleteTextView res;

    String[] cities = new String[50];

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = findViewById(R.id.result);
        user_field = findViewById(R.id.cities);
        textView = findViewById(R.id.title);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                res.setText("");
                HashMap<String, String> iataMap = new HashMap<>();

                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();

                String input = user_field.getText().toString();
                cities = input.split(" ");
                String origin = "", destination = "";
                for (int i=0; i < cities.length-1; i++) {
                    origin = cities[i];
                    destination = cities[i+1];
                    String iataUrl = "https://www.travelpayouts.com/widgets_suggest_params?q=Из%20"+origin+"%20в%20"+destination;
                    IataCode iata = new IataCode();
                    iata.execute(iataUrl);
                    try {
                        JSONObject jsonObject = iata.get();
                        iataMap.put(origin, jsonObject.getJSONObject("origin").getString("iata"));
                        iataMap.put(destination, jsonObject.getJSONObject("destination").getString("iata"));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                textView.setText(iataMap.toString());
                try {
                    getContent(iataMap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // алгоритм нахождения оптимального тура
    public void getContent(HashMap<String, String> imap) throws ExecutionException, InterruptedException {
        String url, minKey = "", destination, origin = imap.get(cities[0]);
        double value, minValue=1000000;
        imap.remove(cities[0]);
        while (!imap.isEmpty()) { // пока карта не пуста
            for (String key : imap.keySet()) { // обрабатываем каждый ключ карты
                destination = imap.get(key); // город прибытия
                url = "https://api.travelpayouts.com/aviasales/v3/prices_for_dates?origin=" + origin + "&destination=" + destination + "&direct=true&limit=10&token=514ed26973e8ac0aa6fb96f3e3de891d";
                Content content = new Content();
                content.execute(url);
                value = content.get(); // получаем самый дешёвый билет из класса, где обрабатывается JSON
                if (value < minValue && value > 0) {
                    minValue = value;
                    minKey = key;
                }
            }
            res.append(origin + " - " + minKey + ": " + minValue + "\n");
            origin = imap.get(minKey); // будем вылетать из того города, куда прибыли
            imap.remove(minKey); // удаляем ключ из карты
        }
    }
    private class Content extends AsyncTask<String, String, Double>{
        private double value;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setText("Рассчитываем оптимальный тур...");
        }
        @Override
        protected Double doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) !=null )
                    buffer.append(line).append("\n");

                JSONObject jsonObject = new JSONObject(buffer.toString());
                value = jsonObject.getJSONArray("data").getJSONObject(0).getDouble("price");
                return value;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return 0.0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            finish();
        }
    }
    private class IataCode extends AsyncTask<String, String, JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if(progressDialog==null)
//                progressDialog = ProgressDialog.show(MainActivity.this, "Waiting...", "Подгружаем IATA-коды городов...");
//            else
//                progressDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) !=null )
                    buffer.append(line).append("\n");

                return new JSONObject(buffer.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            finish();
//            progressDialog.dismiss();
        }
    }
}