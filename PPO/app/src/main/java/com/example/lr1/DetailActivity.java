package com.example.lr1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity implements KeyboardFragment.OnFragmentInteractionListener,
        DataFragment.OnFragmentInteractionListener {
    String[] timeUnits = {"s", "min", "h"};
    String[] distanceUnits = {"mm", "m", "km"};
    String[] weightUnits = {"g", "kg", "t"};
    double [][] timeK = {{1,1./60,1./3600}, {60,1,1./60}, {3600,60,1}};
    double [][] distanceK = {{1,0.001,0.000001}, {1000,1,0.001}, {1000000,1000,1}};
    double [][] weightK = {{1,0.001,0.000001}, {1000,1,0.001}, {1000000,1000,1}};
    DataFragment dataFragment;
    Spinner spnFrom;
    Spinner spnTo;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dataFragment = (DataFragment) getFragmentManager().findFragmentById(R.id.dataFragment);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        ArrayAdapter<String> adapter;
        if (category.equals("Time"))
        {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeUnits);
        }
        else if (category.equals("Distance"))
        {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, distanceUnits);
        }
        else
        {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, weightUnits);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnFrom = (Spinner) findViewById(R.id.spnFrom);
        spnTo = (Spinner) findViewById(R.id.spnTo);

        spnFrom.setAdapter(adapter);
        spnFrom.setSelection(0);

        spnTo.setAdapter(adapter);
        spnTo.setSelection(1);

        spnFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id)
            {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });
        spnTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id)
            {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
            }
        });
    }

    @Override
    public void onNumberClick(CharSequence number)
    {
        ((EditText) dataFragment.getView().findViewById(R.id.etFrom)).append(number);
        convert();
    }

    @Override
    public void onEraseClick()
    {
        EditText input = ((EditText) dataFragment.getView().findViewById(R.id.etFrom));
        Editable currentText = input.getText();

        if (currentText.length() > 0)
        {
            currentText.delete(currentText.length() - 1, currentText.length());
            input.setText(currentText);
            if (currentText.length() != 0)
                convert();
            else
                ((EditText) dataFragment.getView().findViewById(R.id.etTo)).setText("");
        }
    }

    @Override
    public void onLongEraseClick()
    {
        ((EditText) dataFragment.getView().findViewById(R.id.etFrom)).setText("");
        ((EditText) dataFragment.getView().findViewById(R.id.etTo)).setText("");
    }

    public void convert()
    {
        String strValue = ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();
        double value;
        int from;
        int to;
        if (!strValue.isEmpty())
        {
            value = Double.parseDouble(strValue);
            from = spnFrom.getSelectedItemPosition();
            to = spnTo.getSelectedItemPosition();
        }
        else
            return;

        if (category.equals("Time"))
            value *= timeK[from][to];
        else if (category.equals("Distance"))
            value *= distanceK[from][to];
        else
            value *= weightK[from][to];
        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
    }

    @Override
    public void onFromCopyClick()
    {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = ((EditText) dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();

        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onToCopyClick()
    {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = ((EditText) dataFragment.getView().findViewById(R.id.etTo)).getText().toString();

        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
    }
}