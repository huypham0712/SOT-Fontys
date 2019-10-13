package com.example.sotjaxrsandroidapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.sotjaxrsandroidapp.MainActivity.BASEURL;

public class CreateGameActivity extends AppCompatActivity {
    Spinner spinnerGameGenre;
    EditText tbGameName, tbGamePrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        spinnerGameGenre = findViewById(R.id.spinnerCreateNewGameGenre);
        tbGameName = findViewById(R.id.etCreateNewGameName);
        tbGamePrice = findViewById(R.id.etCreateNewGamePrice);

        List<GameGenre> gameGenres = new ArrayList<>();
        gameGenres.add(GameGenre.ACTION);
        gameGenres.add(GameGenre.ADVENTURE);
        gameGenres.add(GameGenre.SCIENTIFIC);
        gameGenres.add(GameGenre.SIMULATION);
        gameGenres.add(GameGenre.STRATEGY);
        gameGenres.add(GameGenre.ROLEPLAYING);

        ArrayAdapter<GameGenre> dataAdapter = new ArrayAdapter<GameGenre>(this, R.layout.support_simple_spinner_dropdown_item, gameGenres);

        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spinnerGameGenre.setAdapter(dataAdapter);
    }

    public void onBtnCreateGameClick(View v){
        if (tbGameName.getText().toString().equals("") || tbGamePrice.getText().toString().equals("")){
            Toast.makeText(this, "The game name and price cannot be empty!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GameService service = retrofit.create(GameService.class);
        final Call<Void> gameCall = service.createGame(tbGameName.getText().toString()
                , (GameGenre) spinnerGameGenre.getSelectedItem()
                , Double.parseDouble(tbGamePrice.getText().toString()));

        gameCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(CreateGameActivity.this, "Successfully created new game!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {c
                    Toast.makeText(CreateGameActivity.this, "Could not create new game with name " + tbGameName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateGameActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
