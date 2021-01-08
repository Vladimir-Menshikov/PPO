package com.example.ppo3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import java.util.Arrays;

public class SignInFragment extends Fragment implements
        View.OnClickListener
{
    private static final String TAG = "SignInFragment";
    private static final int RC_GOOGLE = 2;

    private CallbackManager callbackManager;
    private OnSignInListener signInListener;
    private GoogleSignInClient googleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        if (signInListener != null)
                        {
                            signInListener.onSignIn(loginResult.getAccessToken());
                        }
                    }

                    @Override
                    public void onCancel()
                    {
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        Log.e(TAG, String.valueOf(exception));
                    }
                });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        Button fbSignInButton = view.findViewById(R.id.fb_sign_in_button);
        SignInButton googleSignInButton = view.findViewById(R.id.google_sign_in_button);
        fbSignInButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach (@NonNull Context context)
    {
        super.onAttach(context);
        if (context instanceof OnSignInListener)
        {
            signInListener = (OnSignInListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSignInListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RC_GOOGLE)
        {
            super.onActivityResult(requestCode, resultCode, data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                signInListener.onSignIn(account.getIdToken());
            }
            catch (ApiException e)
            {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fb_sign_in_button:
                LoginManager.getInstance().logInWithReadPermissions(this,
                        Arrays.asList("email"));
                break;
            case R.id.google_sign_in_button:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE);
                break;
        }
    }

    public interface OnSignInListener
    {
        void onSignIn(AccessToken token);
        void onSignIn(String token);
    }
}