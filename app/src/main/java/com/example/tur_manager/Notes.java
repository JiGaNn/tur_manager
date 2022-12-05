package com.example.tur_manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tur_manager.DB.MyDBManager;

public class Notes extends AppCompatActivity {
    //база данных
    private MyDBManager myDBManager;
    private Button btnBack;
    private Button btnSave;
    private EditText edZametka;
    private TextView tvZametki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        myDBManager = new MyDBManager(this);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        edZametka = findViewById(R.id.edZametka);
        tvZametki = findViewById(R.id.tvZametki);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDBManager.openDb();
        for(String title: myDBManager.getFromDb()){
            tvZametki.append(title);
            tvZametki.append("\n");
        }
    }

    public void onClickSave(View view) {
        tvZametki.setText("");
        myDBManager.insertToDb(edZametka.getText().toString(), edZametka.getText().toString());
        for(String title: myDBManager.getFromDb()){
            tvZametki.append(title);
            tvZametki.append("\n");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDBManager.closeDb();
    }

    public void onClickBack(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}