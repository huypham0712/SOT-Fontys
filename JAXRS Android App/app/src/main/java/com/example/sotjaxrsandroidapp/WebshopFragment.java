package com.example.sotjaxrsandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.sotjaxrsandroidapp.MainActivity.BASEURL;

public class WebshopFragment extends Fragment {

    ListView lvAvailableGames, lvAvailableCustomers;
    EditText cusId, gameId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webshop, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        lvAvailableCustomers = getActivity().findViewById(R.id.lvAvailableCustomers);
        lvAvailableGames = getActivity().findViewById(R.id.lvAvailableGames);
        cusId = getActivity().findViewById(R.id.editTextSellGameCustomerId);
        gameId = getActivity().findViewById(R.id.editTextSellGameGameId);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService customerService = retrofit.create(CustomerService.class);
        Call<ArrayList<Customer>> allCustomers = customerService.getAllCustomer();

        allCustomers.enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                if (response.isSuccessful()){
                    ArrayList<Customer> customerArrayList = response.body();
                    CustomerAdapter customerAdapter = new CustomerAdapter(customerArrayList, getContext());
                    lvAvailableCustomers.setAdapter(customerAdapter);
                } else {
                    Toast.makeText(getContext(), "Could not update the customer list!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        lvAvailableCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Customer customer = ((Customer) parent.getAdapter().getItem(position));
                cusId.setText(String.valueOf(customer.getId()));
            }
        });

        GameService gameService = retrofit.create(GameService.class);
        Call<ArrayList<Game>> allGamesCall = gameService.getAllGames();

        allGamesCall.enqueue(new Callback<ArrayList<Game>>() {
            @Override
            public void onResponse(Call<ArrayList<Game>> call, Response<ArrayList<Game>> response) {
                if (response.isSuccessful()){
                    ArrayList<Game> gameArrayList = response.body();
                    GameAdapter gameAdapter = new GameAdapter(gameArrayList, getContext());
                    lvAvailableGames.setAdapter(gameAdapter);
                } else {
                    Toast.makeText(getContext(), "Could not update the game list!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Game>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        lvAvailableGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Game game = ((Game) parent.getAdapter().getItem(position));
                gameId.setText(String.valueOf(game.getId()));
            }
        });
    }
}
