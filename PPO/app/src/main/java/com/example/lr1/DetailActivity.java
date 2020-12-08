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
    String[] currencyUnits = {"BYN", "USD", "EUR"};
    String[] distanceUnits = {"m", "mi", "ft"};
    String[] weightUnits = {"kg", "lbs", "car"};
    DataFragment dataFragment;
    Spinner spnFrom;
    Spinner spnTo;
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dataFragment = (DataFragment) getFragmentManager().findFragmentById(R.id.dataFragment);

        Intent intent = getIntent();
        value = intent.getStringExtra("value");
        ArrayAdapter<String> adapter;
        if (value.equals("Currency"))
        {
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyUnits);
        }
        else if (value.equals("Distance"))
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
        if (value.equals("Currency"))
        {
            currencyConvert();
        }
        else if (value.equals("Distance"))
        {
            distanceConvert();
        }
        else
        {
            weightConvert();
        }
    }

    public void currencyConvert()
    {
        String strValue = ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();
        double value;
        if (!strValue.isEmpty())
            value = Double.parseDouble(strValue);
        else
            return;
        switch (spnFrom.getSelectedItem().toString())
        {
            case "BYN":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "BYN":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "USD":
                        value *= 0.38;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "EUR":
                        value *= 0.33;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "USD":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "BYN":
                        value *= 2.6;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "USD":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "EUR":
                        value *= 0.86;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "EUR":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "BYN":
                        value *= 3.04;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "USD":
                        value *= 1.17;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "EUR":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;

        }
    }

    public void distanceConvert()
    {
        String strValue = ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();
        double value;
        if (!strValue.isEmpty())
            value = Double.parseDouble(strValue);
        else
            return;
        switch (spnFrom.getSelectedItem().toString())
        {
            case "m":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "m":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "mi":
                        value *= 0.0006213712;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "ft":
                        value *= 3.280839895;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "mi":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "m":
                        value *= 1609.344;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "mi":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "ft":
                        value *= 5280;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "ft":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "m":
                        value *= 0.3048;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "mi":
                        value *= 0.0001893939;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "ft":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;

        }
    }

    public void weightConvert()
    {
        String strValue = ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();
        double value;
        if (!strValue.isEmpty())
            value = Double.parseDouble(strValue);
        else
            return;
        switch (spnFrom.getSelectedItem().toString())
        {
            case "kg":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "kg":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "lbs":
                        value *= 2.2046226218;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "car":
                        value *= 5000;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "lbs":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "kg":
                        value *= 0.45359237;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "lbs":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "car":
                        value *= 2267.96185;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;
            case "car":
                switch (spnTo.getSelectedItem().toString())
                {
                    case "kg":
                        value *= 0.0002;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "lbs":
                        value *= 0.0004409245;
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                    case "car":
                        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText(Double.toString(value));
                        break;
                }
                break;

        }
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