package com.example.lr1;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class DataFragment extends Fragment implements View.OnClickListener
{
    private OnFragmentInteractionListener mListener;

    Spinner spnFrom;
    Spinner spnTo;

    interface OnFragmentInteractionListener
    {
        void onFromCopyClick();
        void onToCopyClick();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        spnFrom = (Spinner) view.findViewById(R.id.spnFrom);
        spnTo = (Spinner) view.findViewById(R.id.spnTo);
        return view;
    }

    @Override
    public void onClick(View view)
    {
    }
}