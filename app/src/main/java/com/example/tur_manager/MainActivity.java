package com.example.tur_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private EditText user_field;
    private TextView textView;
    private Button button;
    private MultiAutoCompleteTextView res;

    ArrayList<String> cities = new ArrayList<>();

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

                HashMap<String, String> iataMap = new HashMap<>();
                res.setText("");

                if(user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_user_input, Toast.LENGTH_LONG).show();

                String input = user_field.getText().toString();
                String[] city = input.split(" ");
                for(String str:city) {
                    cities.add(str);
                }

                for (int i=0; i < cities.size()-1; i++) {
                    String origin = cities.get(i);
                    String destination = cities.get(i+1);
                    String iataUrl = "https://www.travelpayouts.com/widgets_suggest_params?q=Из%20"+origin+"%20в%20"+destination;
                    IataCode iata = new IataCode();
                    iata.execute(iataUrl);
                    try {
                        String[] str = iata.get();
                        iataMap.put(origin, str[0]);
                        iataMap.put(destination, str[1]);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
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
    public void getContent(HashMap<String, String> iataMap) throws ExecutionException, InterruptedException {
        String startCity = cities.get(0);
        String url, destination, origin = iataMap.get(startCity), minKey = origin;
        double value, minValue;
        cities.remove(0);
        while (!cities.isEmpty()) { // пока массив не пуст
            minValue=1000000;
            for (String key : cities) { // обрабатываем каждый город
                destination = iataMap.get(key); // город прибытия
                url = "https://api.travelpayouts.com/aviasales/v3/prices_for_dates?origin=" + origin + "&destination=" + destination + "&direct=true&limit=1&token=514ed26973e8ac0aa6fb96f3e3de891d";
                Content content = new Content();
                content.execute(url);
                value = content.get(); // получаем самый дешёвый билет из класса, где обрабатывается JSON
                System.out.println(origin + " - " + destination + ": " + value);
                System.out.println(cities.toString());
                if (value < minValue && value > 0) {
                    minValue = value;
                    minKey = key;
                }
            }
            if (minValue==1000000) {
                cities.removeAll(cities);

                List keyReverse = new ArrayList();
                keyReverse.addAll(iataMap.keySet());
                Collections.reverse(keyReverse);
                cities.addAll(keyReverse);

                cities.remove(minKey);
                origin = iataMap.get(startCity);
                cities.remove(startCity);
                res.setText("");
                System.out.println(keyReverse);
                System.out.println("Пришлось удалить "+minKey);
                continue;
            }
            res.append(origin + " - " + minKey + ": " + minValue + "\n");
            origin = iataMap.get(minKey); // будем вылетать из того города, куда прибыли
            cities.remove(minKey); // удаляем ключ из карты
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
        }
    }
    private class IataCode extends AsyncTask<String, String, String[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog==null)
                progressDialog = ProgressDialog.show(MainActivity.this, "Waiting...", "Подгружаем IATA-коды городов...");
            else
                progressDialog.show();
        }
        @Override
        protected String[] doInBackground(String... strings) {
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

                String[] str = new String[2];
                JSONObject jsonObject = new JSONObject(buffer.toString());
                str[0] = jsonObject.getJSONObject("origin").getString("iata");
                str[1] = jsonObject.getJSONObject("destination").getString("iata");
                return str;
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
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }
}