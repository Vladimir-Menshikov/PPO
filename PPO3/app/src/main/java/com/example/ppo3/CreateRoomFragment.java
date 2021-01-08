package com.example.ppo3;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

public class CreateRoomFragment extends Fragment
{
    private OnCreateRoomListener createRoomListener;

    private CheckBox protectByPINCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_create_room, container, false);

        protectByPINCheckBox = view.findViewById(R.id.protect_by_pin_checkbox);
        Button createRoomButton = view.findViewById(R.id.create_room_button);

        createRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (createRoomListener != null)
                {
                    createRoomListener.onCreateRoom(protectByPINCheckBox.isChecked());
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof OnCreateRoomListener)
        {
            createRoomListener = (OnCreateRoomListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCreateRoomListener");
        }
    }

    public interface OnCreateRoomListener
    {
        void onCreateRoom(boolean protect);
    }
}