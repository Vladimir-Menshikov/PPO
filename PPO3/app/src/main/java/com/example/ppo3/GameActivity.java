package com.example.ppo3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements
        BoardGridView.OnMakeMoveHandler
{

    private DatabaseReference databaseReference;
    private DatabaseReference gameReference;

    private BoardGridView gridView;
    private Game emptyGame;

    private ArrayList<Rect> ships;

    private boolean finished = false;
    private int role;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        final String gameId = intent.getStringExtra("gameId");
        role = intent.getIntExtra("role", -1);

        if (gameId == null) throw new AssertionError("Game id is null");
        if (role == -1) throw new AssertionError("Role is -1");

        ships = intent.getParcelableArrayListExtra("ships");

        emptyGame = new Game();

        gridView = findViewById(R.id.game_field);
        gridView.setGame(emptyGame);
        gridView.setRoleAndShips(role, ships);
        gridView.setOnMakeMoveHandler(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        gameReference = databaseReference.child("game").child(gameId);

        if (role == 1)
        {
            gameReference.child("field1").setValue(updateFieldWithShips(emptyGame.getField1(), ships));
            gameReference.child("playersConnected").setValue(2);
        }
        else {
            gameReference.setValue(emptyGame);
            gameReference.child("field2").setValue(updateFieldWithShips(emptyGame.getField2(), ships));
            gameReference.child("playersConnected").setValue(1);
        }

        ValueEventListener gameListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (finished)
                {
                    return;
                }

                Game updated_game = dataSnapshot.getValue(Game.class);

                if (updated_game == null)
                {
                    return;
                }

                if (updated_game.isFinished() && !finished)
                {
                    finished = true;

                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final DatabaseReference statRef = databaseReference.child("Stats");
                    String stat;

                    if (updated_game.getGameState() == gridView.getRole())
                    {
                        Toast.makeText(GameActivity.this, "You win", Toast.LENGTH_LONG)
                                .show();
                        stat = "Victory";
                        gameReference.removeValue(); //removes game not id

                    }
                    else {
                        Toast.makeText(GameActivity.this, "You lose", Toast.LENGTH_LONG)
                                .show();
                        stat = "Defeat";
                    }

                    statRef.child(userId).push().setValue(stat);

                    return;
                }

                boolean anything_changed = false;
                int shipsKilled = 0;

                for (Rect s : ships)
                {
                    boolean killed = true;
                    for (int x = s.left; x <= s.right; x++)
                    {
                        for (int y = s.top; y <= s.bottom; y++)
                        {
                            if (gridView.getRole() == Game.GameState.FIRST_MOVE)
                            {
                                if (updated_game.getField1().get(x).get(y) != Game.CellState.HIT)
                                {
                                    killed = false;
                                }
                            }
                            else {
                                if (updated_game.getField2().get(x).get(y) != Game.CellState.HIT)
                                {
                                    killed = false;
                                }
                            }
                        }
                    }
                    if (killed)
                    {
                        shipsKilled += 1;
                        for (int x = s.left - 1; x <= s.right + 1; x++)
                        {
                            for (int y = s.top - 1; y <= s.bottom + 1; y++)
                            {
                                if (x >= 0 && x < 10 && y >= 0 && y < 10)
                                {
                                    if (gridView.getRole() == Game.GameState.FIRST_MOVE)
                                    {
                                        if (updated_game.getField1().get(x).get(y) == Game.CellState.EMPTY)
                                        {
                                            updated_game.getField1().get(x).set(y, Game.CellState.MISS);
                                            anything_changed = true;
                                        }
                                    }
                                    else
                                        {
                                        if (updated_game.getField2().get(x).get(y) == Game.CellState.EMPTY)
                                        {
                                            updated_game.getField2().get(x).set(y, Game.CellState.MISS);
                                            anything_changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                if (shipsKilled == 10)
                {
                    updated_game.setFinished(true);
                    anything_changed = true;
                }

                if (anything_changed)
                {
                    gameReference.setValue(updated_game);
                    return;
                }

                TextView moveTextView = findViewById(R.id.text_view_game_state);
                String move = (
                        (role == 1 && updated_game.getGameState() == Game.GameState.FIRST_MOVE) ||
                                (role == 2 && updated_game.getGameState() == Game.GameState.SECOND_MOVE)
                ) ? "Your move" : "Second player move";
                moveTextView.setText(move);

                TextView connectedTextView = findViewById(R.id.text_view_players_connected);
                String playersConnected = updated_game.getPlayersConnected() + " players connected.";
                connectedTextView.setText(playersConnected);

                gridView.setGame(updated_game);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };

        gameReference.addValueEventListener(gameListener);
    }

    List<List<Game.CellState>> updateFieldWithShips(List<List<Game.CellState>> field, List<Rect> ships)
    {
        for (Rect s : ships)
        {
            for (int x = s.left; x <= s.right; x++)
            {
                for (int y = s.top; y <= s.bottom; y++)
                {
                    field.get(x).set(y, Game.CellState.SHIP);
                }
            }
        }
        return field;
    }

    @Override
    public void onMakeMove(Game game)
    {
        gameReference.setValue(game);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (!finished)
            gameReference.child("playersConnected").setValue(1);

        if (role == 1)
            gameReference.removeValue();

    }
}