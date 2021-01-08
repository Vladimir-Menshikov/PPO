package com.example.ppo3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class ProfileFragment extends Fragment
{
    private OnProfileListener profileListener;

    private Uri profilePictureUri;
    private String firstName;

    public ProfileFragment (Uri profilePictureUri, String firstName)
    {
        this.profilePictureUri = profilePictureUri;
        this.firstName = firstName;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView profileFirstName = view.findViewById(R.id.profile_first_name);
        ImageView profilePictureImageView = view.findViewById(R.id.profile_picture);
        Button signOutButton = view.findViewById(R.id.sign_out_button);
        Button statsButton = view.findViewById(R.id.stats_button);

        profileFirstName.setText(firstName);
        Glide.with(getContext()).load(profilePictureUri).
                into(profilePictureImageView);

        signOutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (profileListener != null)
                {
                    profileListener.onSignOut();
                }
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), StatsActivity.class);
                startActivity(intent);
            }
        });

        profileFirstName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (profileListener != null)
                {
                    profileListener.onChangeName();
                }
            }
        });

        profilePictureImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (profileListener != null)
                {
                    profileListener.onChangePicture();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach (@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof OnProfileListener)
        {
            profileListener = (OnProfileListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignOutListener");
        }
    }

    public interface OnProfileListener
    {
        void onSignOut();
        void onChangeName();
        void onChangePicture();
    }
}