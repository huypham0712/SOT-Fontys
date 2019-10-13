package com.example.sotjaxrsandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView lvAllCustomer, lvAllGame;

    //PLEASE CHANGE THIS TO YOUR LAPTOP/PC IP ADDRESS
    //OTHERWISE YOU WILL NOT BE ABLE TO CONNECT TO THE LOCAL API SERVER
    public static String BASEURL = "http://145.93.85.171:8080/store/rest/";

    private ListView lvAvailableCustomers, lvAvailableGames;
    private EditText cusId, gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_customer_service:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerFragment()).commit();
                break;

            case R.id.nav_game_service:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GameFragment()).commit();
                break;

            case R.id.nav_webshop_service:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WebshopFragment()).commit();
                break;

            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onButtonViewAllCustomerClick(View v) {
        lvAllCustomer = findViewById(R.id.lvAllCustomer);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        Call<ArrayList<Customer>> allCustomers = service.getAllCustomer();

        allCustomers.enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(Call<ArrayList<Customer>> call, Response<ArrayList<Customer>> response) {
                if (response.isSuccessful()){
                    ArrayList<Customer> customerArrayList = response.body();
                    CustomerAdapter customerAdapter = new CustomerAdapter(customerArrayList, getApplicationContext());
                    lvAllCustomer.setAdapter(customerAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Could not update the customer list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        lvAllCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editCustomer = new Intent(getApplicationContext(), EditCustomerActivity.class);
                Customer customer = ((Customer) parent.getAdapter().getItem(position));
                editCustomer.putExtra("name", customer.getName());
                editCustomer.putExtra("email", customer.getEmail());
                editCustomer.putExtra("id", customer.getId());

                startActivity(editCustomer);
            }
        });
    }

    public void onButtonViewAllGameClick(View v){
        lvAllGame = findViewById(R.id.lvAllGames);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GameService service = retrofit.create(GameService.class);
        Call<ArrayList<Game>> allGamesCall = service.getAllGames();

        allGamesCall.enqueue(new Callback<ArrayList<Game>>() {
            @Override
            public void onResponse(Call<ArrayList<Game>> call, Response<ArrayList<Game>> response) {
                if (response.isSuccessful()){
                    ArrayList<Game> gameArrayList = response.body();
                    GameAdapter gameAdapter = new GameAdapter(gameArrayList, getApplicationContext());
                    lvAllGame.setAdapter(gameAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Could not update the game list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Game>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        lvAllGame.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editGame = new Intent(getApplicationContext(), EditGameActivity.class);
                Game game = ((Game) parent.getAdapter().getItem(position));
                editGame.putExtra("name", game.getName());
                editGame.putExtra("price", game.getPrice());
                editGame.putExtra("genre", game.getGenre().toString());
                editGame.putExtra("id", game.getId());

                startActivity(editGame);
            }
        });
    }

    public void onFindCusByIdClick(View v) {

        final ListView lvAllCustomer = findViewById(R.id.lvAllCustomer);
        final EditText editTextCustomerId = findViewById(R.id.editTextCustomerId);

        if (editTextCustomerId.getText().toString().equals("")){
            Toast.makeText(this, "Please provide the id of the customer you want to search for", Toast.LENGTH_SHORT).show();
            return;
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Customer> customerCall = service.findCustomer(Integer.parseInt(String.valueOf(editTextCustomerId.getText())));

        customerCall.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    ArrayList<Customer> result = new ArrayList<>();
                    result.add(response.body());
                    CustomerAdapter customerAdapter = new CustomerAdapter(result, getApplicationContext());
                    lvAllCustomer.setAdapter(customerAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Could not find the customer with id " + editTextCustomerId.getText().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        lvAllCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editCustomer = new Intent(getApplicationContext(), EditCustomerActivity.class);
                Customer customer = ((Customer) parent.getAdapter().getItem(position));
                editCustomer.putExtra("name", customer.getName());
                editCustomer.putExtra("email", customer.getEmail());
                editCustomer.putExtra("id", customer.getId());

                startActivity(editCustomer);
            }
        });
    }

    public void onFindGameById(View v){
        final ListView lvAllGames = findViewById(R.id.lvAllGames);
        final EditText editTextGameID = findViewById(R.id.editTextGameId);

        if (editTextGameID.getText().toString().equals("")){
            Toast.makeText(this, "Please provide the game Id you want to search!!", Toast.LENGTH_SHORT).show();
            return;
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GameService service = retrofit.create(GameService.class);
        final Call<Game> gameCall = service.findGame(Integer.parseInt(String.valueOf(editTextGameID.getText())));

        gameCall.enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful()){
                    ArrayList<Game> result = new ArrayList<>();
                    result.add(response.body());
                    GameAdapter gameAdapter = new GameAdapter(result, getApplicationContext());
                    lvAllGames.setAdapter(gameAdapter);
                } else {
                    Toast.makeText(MainActivity.this, "Could not find game with id " + editTextGameID.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        lvAllGames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editGame = new Intent(getApplicationContext(), EditGameActivity.class);
                Game game = ((Game) parent.getAdapter().getItem(position));
                editGame.putExtra("name", game.getName());
                editGame.putExtra("price", game.getPrice());
                editGame.putExtra("genre", game.getGenre().toString());
                editGame.putExtra("id", game.getId());

                startActivity(editGame);
            }
        });
    }

    public void onBtnCreateClick(View v){
        Intent openCreateActivity = new Intent(this, CreateCustomerActivity.class);
        startActivity(openCreateActivity);
    }

    public void onBtnCreateGameClick(View v){
        Intent openCreateGameActivity = new Intent(this, CreateGameActivity.class);
        startActivity(openCreateGameActivity);
    }

    public void onBtnSellGameClick(View v){
        EditText tbCusId = findViewById(R.id.editTextSellGameCustomerId);
        EditText tbGameId = findViewById(R.id.editTextSellGameGameId);

        if (tbCusId.getText().toString().equals("") || tbGameId.getText().toString().equals("")){
            Toast.makeText(this, "Please select a customer and a game!!", Toast.LENGTH_SHORT).show();
            return;
        }

        int cusId = Integer.parseInt(tbCusId.getText().toString());
        int gameId = Integer.parseInt(tbGameId.getText().toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebshopService webshopService = retrofit.create(WebshopService.class);
        Call<Void> sellGameCall = webshopService.sellGame(cusId, gameId);

        sellGameCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Successfully sell the game for the customer!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: The customer already has this game or the balance is not enough!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onBtnRefreshDataClick(View v){
        lvAvailableCustomers = findViewById(R.id.lvAvailableCustomers);
        lvAvailableGames = findViewById(R.id.lvAvailableGames);
        cusId = findViewById(R.id.editTextSellGameCustomerId);
        gameId = findViewById(R.id.editTextSellGameGameId);

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
                    CustomerAdapter customerAdapter = new CustomerAdapter(customerArrayList, getApplicationContext());
                    lvAvailableCustomers.setAdapter(customerAdapter);
                    Toast.makeText(MainActivity.this, "Successfully refreshing data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Could not update customer list!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Customer>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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
                    GameAdapter gameAdapter = new GameAdapter(gameArrayList, getApplicationContext());
                    lvAvailableGames.setAdapter(gameAdapter);
                    Toast.makeText(MainActivity.this, "Successfully refreshing data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Could not update game list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Game>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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

    public void onBtnDepositClick(View v){
        EditText etCusId = findViewById(R.id.editTextSellGameCustomerId);
        if (etCusId.getText().toString().equals("")){
            Toast.makeText(this, "Please select a customer!!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent toDepositActivity = new Intent(this, DepositActivity.class);
        toDepositActivity.putExtra("id", etCusId.getText().toString());
        startActivity(toDepositActivity);
    }
}
