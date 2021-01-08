package com.example.ppo3;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CreatedRoomFragment extends Fragment
        implements ValueEventListener
{
    private OnCreatedRoomListener createdRoomListener;

    private String roomId;
    private String password;


    public CreatedRoomFragment(String roomId, String password)
    {
        this.roomId = roomId;
        this.password = password;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_created_room_data, container, false);


        final EditText roomIdEditText = view.findViewById(R.id.room_id_edit_text);
        Button removeRoomButton = view.findViewById(R.id.remove_room_button);
        TextInputLayout roomIdLayout = view.findViewById(R.id.room_id_layout);

        roomIdEditText.setText(roomId);

        removeRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (createdRoomListener != null)
                {
                    createdRoomListener.onRemoveRoom(roomId);
                }
            }
        });

        roomIdLayout.setEndIconOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ClipboardManager clipboard = (ClipboardManager) getActivity().
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, roomIdEditText.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Room ID copied", Toast.LENGTH_SHORT).show();
            }
        });

        if (password != null)
        {
            EditText passwordEditText = view.findViewById(R.id.password_edit_text);
            LinearLayout passwordLayout = view.findViewById(R.id.password_layout);

            passwordLayout.setVisibility(View.VISIBLE);
            passwordEditText.setText(password);
        }

        return view;
    }

    @Override
    public void onDestroyView()
    {
        if (createdRoomListener != null)
        {
            createdRoomListener.onRemoveRoom(roomId);
        }
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof OnCreatedRoomListener)
        {
            createdRoomListener = (OnCreatedRoomListener) context;
        }
        else
            {
            throw new RuntimeException(context.toString()
                    + " must implement OnRemoveRoomListener");
        }
    }



    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
    {
        String opponentId = dataSnapshot.getValue(String.class);

        if (opponentId != null)
        {
            GameInitializer gameInitializer = new GameInitializer();
            gameInitializer.randomShuffle();

            String gameId = dataSnapshot.getRef().getParent().getKey();
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.putExtra("gameId", gameId);
            intent.putExtra("role", 1);
            intent.putExtra("ships", gameInitializer.getShips());
            startActivity(intent);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }

    public interface OnCreatedRoomListener
    {
        void onRemoveRoom(String roomId);
    }
}