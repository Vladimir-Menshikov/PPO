package com.example.ppo2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import com.jaredrummler.android.colorpicker.ColorShape;

import java.io.Console;

public class AddActivity extends AppCompatActivity implements ColorPickerDialogListener {

    private EditText etTitle;
    private EditText etPrepare;
    private EditText etWork;
    private EditText etChill;
    private EditText etCycles;
    private EditText etSets;
    private EditText etSetChill;
    private Button btnColor;
    private Button btnSave;

    private DatabaseAdapter adapter;

    private int color;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etPrepare = (EditText)findViewById(R.id.etPrepare);
        etWork = (EditText)findViewById(R.id.etWork);
        etChill = (EditText)findViewById(R.id.etChill);
        etCycles = (EditText)findViewById(R.id.etCycles);
        etSets = (EditText)findViewById(R.id.etSets);
        etSetChill = (EditText)findViewById(R.id.etSetChill);
        btnColor = (Button) findViewById(R.id.btnColor);
        btnSave = (Button) findViewById(R.id.btnColor);

        adapter = new DatabaseAdapter(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            id = extras.getInt("id");
        }
        // если 0, то добавление
        if (id > 0)
        {
            // получаем элемент по id из бд
            adapter.open();

            Sequence sequence = adapter.getSequence(id);
            etTitle.setText(sequence.title);
            etPrepare.setText(String.valueOf(sequence.prepare));
            etWork.setText(String.valueOf(sequence.work));
            etChill.setText(String.valueOf(sequence.chill));
            etCycles.setText(String.valueOf(sequence.cycles));
            etSets.setText(String.valueOf(sequence.sets));
            etSetChill.setText(String.valueOf(sequence.setChill));
            color = sequence.color;
            adapter.close();
        }
        else{
        }
    }

    @Override
    public void onColorSelected(int dialogId, int color)
    {
        this.color = color;
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void createColorPickerDialog()
    {
        ColorPickerDialog.newBuilder()
                .setColor(Color.RED)
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowCustom(false)
                .setAllowPresets(true)
                .setColorShape(ColorShape.CIRCLE)
                .setShowAlphaSlider(false)
                .setShowColorShades(false)
                .show(this);
// полный список атрибутов класса ColorPickerDialog смотрите ниже
    }
    public void onBtnColorClick(View view) {
                createColorPickerDialog();
    }

    public void onBtnSaveClick(View view)
    {
        String title = etTitle.getText().toString();
        int prepare = Integer.parseInt(etPrepare.getText().toString());
        int work = Integer.parseInt(etWork.getText().toString());
        int chill = Integer.parseInt(etChill.getText().toString());
        int cycles = Integer.parseInt(etCycles.getText().toString());
        int sets = Integer.parseInt(etSets.getText().toString());
        int setChill = Integer.parseInt(etSetChill.getText().toString());
        Sequence sequence = new Sequence(id, this.color, title, prepare, work, chill, cycles, sets, setChill);
        adapter.open();
        if (id > 0)
        {
            adapter.update(sequence);
        } else {
            adapter.insert(sequence);
        }
        adapter.close();
        goHome();
    }

    private void goHome()
    {
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}