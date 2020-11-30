package com.example.lr1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CurrencyActivity extends AppCompatActivity implements KeyboardFragment.OnFragmentInteractionListener,
        DataFragment.OnFragmentInteractionListener
{
    String[] units = {"BYN", "USD", "EUR"};
    DataFragment dataFragment;
    Spinner spnFrom;
    Spinner spnTo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        dataFragment = (DataFragment)getFragmentManager().findFragmentById(R.id.dataCurrencyFragment);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnFrom = (Spinner) findViewById(R.id.spnFrom);
        spnTo = (Spinner) findViewById(R.id.spnTo);

        spnFrom.setAdapter(adapter);
        spnFrom.setSelection(0);

        spnTo.setAdapter(adapter);
        spnTo.setSelection(1);

        spnFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
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
        spnTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id)
            {
                convert();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onNumberClick(CharSequence number)
    {
        ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).append(number);
        convert();
    }

    @Override
    public void onEraseClick()
    {
        EditText input = ((EditText)dataFragment.getView().findViewById(R.id.etFrom));
        Editable currentText = input.getText();

        if (currentText.length() > 0)
        {
            currentText.delete(currentText.length() - 1, currentText.length());
            input.setText(currentText);
            if (currentText.length() != 0)
                convert();
            else
                ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText("");
        }
    }

    @Override
    public void onLongEraseClick()
    {
        ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).setText("");
        ((EditText)dataFragment.getView().findViewById(R.id.etTo)).setText("");
    }

    public void convert()
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

    @Override
    public void onFromCopyClick()
    {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = ((EditText)dataFragment.getView().findViewById(R.id.etFrom)).getText().toString();

        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onToCopyClick()
    {
        ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        String text;
        text = ((EditText)dataFragment.getView().findViewById(R.id.etTo)).getText().toString();

        ClipData myClip = ClipData.newPlainText("text", text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied",Toast.LENGTH_SHORT).show();
    }
}