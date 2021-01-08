package com.example.ppo3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class RoomAdapter extends ArrayAdapter<Room>
{
    public RoomAdapter(ArrayList<Room> data, Context context)
    {
        super(context, R.layout.room_list_item, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Room room = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.room_list_item, parent, false);
            viewHolder.roomIdTextView = convertView.findViewById(R.id.room_id_text_view);
            viewHolder.ownerIdTextView = convertView.findViewById(R.id.room_owner_id_text_view);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.roomIdTextView.setText(room.getRoomId());
        viewHolder.ownerIdTextView.setText(room.getOwnerId());

        return convertView;
    }

    private static class ViewHolder
    {
        TextView roomIdTextView;
        TextView ownerIdTextView;
    }
}