package com.example.ppo3;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.ListFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;

public class RoomListFragment extends ListFragment
        implements ChildEventListener
{
    private String currentUserId;
    private ArrayList<Room> roomList;
    private ArrayList<DatabaseReference> roomRefList;
    private ArrayAdapter roomListAdapter;

    private String password, enterPassword;
    private Room room;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_room_list, container, false);

        roomList = new ArrayList<>();
        roomRefList = new ArrayList<>();
        roomListAdapter = new RoomAdapter(roomList, getActivity());
        setListAdapter(roomListAdapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView parent, View v, final int position, long id)
    {
        super.onListItemClick(parent, v, position, id);
        room = roomList.get(position);
        password = room.getPassword();
        if (!room.getOwnerId().equals(currentUserId))
        {
            if (password != null)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("PASSWORD");
                alertDialog.setMessage("Enter Password");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                enterPassword = input.getText().toString();
                                if (enterPassword.equals(password))
                                {
                                    enter(position);
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
            else
            {
                enter(position);
            }
        }
    }

    public void enter(int position)
    {
        room.setOpponentId(currentUserId);
        roomRefList.get(position).setValue(room);

        GameInitializer gameInitializer = new GameInitializer();
        gameInitializer.randomShuffle();

        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.putExtra("gameId", roomRefList.get(position).getKey());
        intent.putExtra("role", 2);
        intent.putExtra("ships", gameInitializer.getShips());
        startActivity(intent);
    }

    public void setCurrentUserId(String currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
    {
        roomList.add(dataSnapshot.getValue(Room.class));
        roomRefList.add(dataSnapshot.getRef());
        roomListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
    {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
    {
        int index = roomRefList.indexOf(dataSnapshot.getRef());
        roomRefList.remove(index);
        roomList.remove(index);
        roomListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
    {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError)
    {

    }
}