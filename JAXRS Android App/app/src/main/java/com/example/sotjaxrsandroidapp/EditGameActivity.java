package com.example.sotjaxrsandroidapp;

import android.content.Intent;
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

public class EditGameActivity extends AppCompatActivity {
    Spinner spinnerGameGenre;
    Intent source;
    EditText gameName, gamePrice;
    Game oldData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_game);
        spinnerGameGenre = findViewById(R.id.spinnerCreateNewGameGenre);

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

        source = getIntent();

        int gameGenreIndex = 0;
        for (int i = 0; i < spinnerGameGenre.getAdapter().getCount(); i++){
            if (spinnerGameGenre.getAdapter().getItem(i).toString()
                    .equals(source.getStringExtra("genre"))){
                gameGenreIndex = i;
            }
        }

        spinnerGameGenre.setSelection(gameGenreIndex);

        gameName = findViewById(R.id.etCreateNewGameName);
        gamePrice = findViewById(R.id.etCreateNewGamePrice);

        gameName.setText(source.getStringExtra("name"));
        gamePrice.setText(String.valueOf(source.getDoubleExtra("price", 0)));

        oldData = new Game(Double.parseDouble(String.valueOf(source.getDoubleExtra("price", 0)))
                            ,source.getStringExtra("name")
                            ,(GameGenre)spinnerGameGenre.getSelectedItem());

        oldData.setId(Integer.parseInt(String.valueOf(source.getIntExtra("id",0))));
    }

    public void onUpdateGameClick(View v){
        if (gameName.getText().toString().equals("") || gamePrice.getText().toString().equals("")){
            Toast.makeText(this, "The game name and game price cannot be empty!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Game newData = new Game(Double.parseDouble(gamePrice.getText().toString())
                ,gameName.getText().toString()
                , (GameGenre)spinnerGameGenre.getSelectedItem());
        newData.setId(oldData.getId());

        GameService service = retrofit.create(GameService.class);
        final Call<Void> customerCall = service.updateGame(newData);

        customerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    oldData = newData;
                    Toast.makeText(EditGameActivity.this, "Successfully update game data!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditGameActivity.this, "Could not update game data, the name must be unique!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditGameActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDeleteGameClick(View v){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GameService service = retrofit.create(GameService.class);
        final Call<Void> customerCall = service.deleteGame(oldData.getId());

        customerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditGameActivity.this, "Successfully remove game!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditGameActivity.this, "Could not remove the current game!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditGameActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
