package com.example.lr1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    Button btnCurrency;
    Button btnDistance;
    Button btnWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCurrency = (Button) findViewById(R.id.btnCurrency);
        btnDistance = (Button) findViewById(R.id.btnDistance);
        btnWeight = (Button) findViewById(R.id.btnWeight);
    }

    public void onClickCurrency(View v)
    {
        Intent intent = new Intent(this, CurrencyActivity.class);
        startActivity(intent);
    }
    public void onClickDistance(View v)
    {
        Intent intent = new Intent(this, DistanceActivity.class);
        startActivity(intent);
    }
    public void onClickWeight(View v)
    {
        Intent intent = new Intent(this, WeightActivity.class);
        startActivity(intent);
    }
}