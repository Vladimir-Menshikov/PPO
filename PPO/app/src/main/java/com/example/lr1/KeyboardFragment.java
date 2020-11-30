package com.example.lr1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class KeyboardFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener
{

    private OnFragmentInteractionListener mListener;


    interface OnFragmentInteractionListener
    {
        void onNumberClick(CharSequence number);
        void onEraseClick();
        void onLongEraseClick();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        Button btn1 = (Button) view.findViewById(R.id.btn1);
        Button btn2 = (Button) view.findViewById(R.id.btn2);
        Button btn3 = (Button) view.findViewById(R.id.btn3);
        Button btn4 = (Button) view.findViewById(R.id.btn4);
        Button btn5 = (Button) view.findViewById(R.id.btn5);
        Button btn6 = (Button) view.findViewById(R.id.btn6);
        Button btn7 = (Button) view.findViewById(R.id.btn7);
        Button btn8 = (Button) view.findViewById(R.id.btn8);
        Button btn9 = (Button) view.findViewById(R.id.btn9);
        Button btn0 = (Button) view.findViewById(R.id.btn0);
        Button btnPoint = (Button) view.findViewById(R.id.btnPoint);
        ImageButton btnErase = (ImageButton) view.findViewById(R.id.btnErase);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btnPoint.setOnClickListener(this);
        btnErase.setOnClickListener(this);
        btnErase.setOnLongClickListener(this);
        return view;
    }
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            mListener = (OnFragmentInteractionListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }
    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnErase:
                mListener.onEraseClick();
                break;
            default:
                Button button = (Button) view;
                mListener.onNumberClick(button.getText());
                break;
        }
    }

    @Override
    public boolean onLongClick(View view)
    {
        mListener.onLongEraseClick();
        return false;
    }
}