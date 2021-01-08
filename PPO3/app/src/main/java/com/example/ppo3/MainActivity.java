package com.example.ppo3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        SignInFragment.OnSignInListener,
        ProfileFragment.OnProfileListener,
        CreateRoomFragment.OnCreateRoomListener,
        CreatedRoomFragment.OnCreatedRoomListener
{
    private static final int RC_PICTURE = 1;

    private SignInFragment signInFragment;
    private ProfileFragment profileFragment;
    private CreateRoomFragment createRoomFragment;
    private CreatedRoomFragment createdRoomFragment;
    private RoomListFragment roomListFragment;

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        createRoomFragment = new CreateRoomFragment();
        signInFragment = new SignInFragment();
        roomListFragment = new RoomListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.create_room_fragment_frame, createRoomFragment);
        fragmentTransaction.add(R.id.room_list_fragment_frame, roomListFragment);
        fragmentTransaction.commit();

        updateUI(auth.getCurrentUser());
    }

    private static void setEnabledViewGroup(ViewGroup viewGroup, boolean enabled)
    {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup)
            {
                setEnabledViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    private void updateUI(FirebaseUser currentUser)
    {
        setEnabledViewGroup((ViewGroup) findViewById(R.id.room_list_fragment_frame),
                currentUser != null);
        setEnabledViewGroup((ViewGroup) findViewById(R.id.create_room_fragment_frame),
                currentUser != null);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                beginTransaction();
        if (profileFragment != null)
        {
            fragmentTransaction.remove(profileFragment);
        }
        if (currentUser != null)
        {
            roomListFragment.setCurrentUserId(currentUser.getUid());

            Uri profilePictureUri = Uri.parse(currentUser.getPhotoUrl()
                    + "?height=125");
            profileFragment = new ProfileFragment(profilePictureUri,
                    currentUser.getDisplayName());

            fragmentTransaction.remove(signInFragment);
            fragmentTransaction.add(R.id.profile_frame, profileFragment);

            findViewById(R.id.profile_frame).setVisibility(View.VISIBLE);
        }
        else {

            fragmentTransaction.add(R.id.parent_frame, signInFragment);

            findViewById(R.id.profile_frame).setVisibility(View.GONE);
        }
        fragmentTransaction.commit();
    }

    private void updateCreateRoomUI()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.create_room_fragment_frame) instanceof
                CreatedRoomFragment)
        {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.create_room_fragment_frame, createRoomFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        if (fragment instanceof RoomListFragment)
        {
            database.getReference("rooms").
                    addChildEventListener((RoomListFragment) fragment);
        }
    }

    @Override
    public void onSignIn(AccessToken token)
    {
        AuthCredential credential = FacebookAuthProvider.
                getCredential(token.getToken());
        auth.signInWithCredential(credential).
                addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        updateUI(auth.getCurrentUser());
                    }
                });
    }

    @Override
    public void onSignIn(String token)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>()
                {
                    @Override
                    public void onSuccess(AuthResult authResult)
                    {
                        updateUI(auth.getCurrentUser());
                    }
                });
    }

    @Override
    public void onSignOut()
    {
        auth.signOut();

        LoginManager.getInstance().logOut();

        updateCreateRoomUI();
        updateUI(null);
    }

    @Override
    public void onChangeName()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Name");
        alertDialog.setMessage("Enter new name");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String newName = input.getText().toString();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build();

                        auth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            updateUI(auth.getCurrentUser());
                                        }
                                    }
                                });
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onChangePicture()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Avatar");
        alertDialog.setMessage("Select new avatar");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        Button uploadImage = new Button(this);
        Button useGravatar = new Button(this);

        uploadImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, RC_PICTURE);

            }
        });
        useGravatar.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View view)
            {
                String hash = MD5Util.md5Hex(auth.getCurrentUser().getEmail());
                String gravatarUrl = "https://www.gravatar.com/avatar/" + hash + "?s=204&d=404";

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Uri.parse(gravatarUrl))
                        .build();

                auth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    updateUI(auth.getCurrentUser());
                                }
                            }
                        });
            }
        });

        uploadImage.setText(R.string.upload_image);
        useGravatar.setText(R.string.gravatar);

        layout.addView(uploadImage);
        layout.addView(useGravatar);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICTURE)
        {
            if (resultCode == RESULT_OK)
            {
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                }
                catch (IOException e)
                {

                }

                uploadImage(bitmap);
            }
        }
    }

    private void uploadImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        final StorageReference mRef = storageReference.child(auth.getCurrentUser().getUid());

        UploadTask uploadTask = mRef.putBytes(byteArray);
        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(task.getResult())
                        .build();

                auth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    updateUI(auth.getCurrentUser());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onCreateRoom(boolean protect)
    {
        final DatabaseReference roomRef = database.
                getReference("rooms").push();

        final String roomId = roomRef.getKey();

        NumberFormat nf = new DecimalFormat("0000");
        final String password = protect ?
                nf.format(new Random().nextInt(10000)) : null;

        if (auth.getCurrentUser() != null)
        {
            Room room = new Room(roomId, auth.getCurrentUser().getUid(), password);

            roomRef.setValue(room).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    createdRoomFragment = new CreatedRoomFragment(roomId, password);

                    roomRef.child("opponentId").addValueEventListener(createdRoomFragment);

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                            beginTransaction();
                    fragmentTransaction.replace(R.id.create_room_fragment_frame,
                            createdRoomFragment);
                    fragmentTransaction.commit();
                }
            });
        }
    }

    @Override
    public void onRemoveRoom(String roomId)
    {
        database.getReference("rooms").child(roomId).removeValue().
                addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        updateCreateRoomUI();
                    }
                });
    }
}

