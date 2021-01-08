package com.example.ppo3;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Room
{
    private String roomId;
    private String ownerId;
    private String password;
    private String opponentId;

    public Room()
    {
        // Default constructor required for calls to DataSnapshot.getValue(Room.class)
    }

    public Room (String roomId, String ownerId, String password)
    {
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.password = password;
    }

    public String getRoomId()
    {
        return roomId;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public String getPassword()
    {
        return password;
    }

    public String getOpponentId()
    {
        return opponentId;
    }

    public void setOpponentId(String opponentId)
    {
        this.opponentId = opponentId;
    }
}
