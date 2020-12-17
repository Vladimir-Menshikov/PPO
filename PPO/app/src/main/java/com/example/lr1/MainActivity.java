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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickTime(View v)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("category", "Time");
        startActivity(intent);
    }
    public void onClickDistance(View v)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("category", "Distance");
        startActivity(intent);
    }
    public void onClickWeight(View v)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("category", "Weight");
        startActivity(intent);
    }
}