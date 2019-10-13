package com.example.sotjaxrsandroidapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.sotjaxrsandroidapp.MainActivity.BASEURL;

public class DepositActivity extends AppCompatActivity {

    TextView tvRegulations;
    EditText etCusName, etCusBalance, etCusNrOwnedGames, etDepositAmount, etLibraryValue;
    Intent source;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();

        source = getIntent();
        tvRegulations = findViewById(R.id.tvDepositRegulation);
        etCusName = findViewById(R.id.etDepositCusName);
        etCusBalance = findViewById(R.id.etDepositCusBalance);
        etCusNrOwnedGames = findViewById(R.id.etDepositOwnedGames);
        etDepositAmount = findViewById(R.id.etDepositAmount);
        etLibraryValue = findViewById(R.id.etLibraryValue);

        etCusName.setEnabled(false);
        etCusBalance.setEnabled(false);
        etCusNrOwnedGames.setEnabled(false);
        etLibraryValue.setEnabled(false);

        tvRegulations.setText("Deposit regulations: " +
                "\n\t- The deposit amount will be increased by 20% if the customer's game library value costs more than 50 euros." +
                "\n\t- The deposit amount will be increased by 10% if the customer's game library value costs more than 25 euros." +
                "\n\t- The deposit amount stays the same for all other cases!!");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Customer> customerCall = service.findCustomer(Integer.parseInt(String.valueOf(source.getStringExtra("id"))));

        customerCall.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    Customer customer = response.body();
                    etCusName.setText(customer.getName());
                    etCusBalance.setText(String.valueOf(round(customer.getBalance(),2)));
                    etCusNrOwnedGames.setText(String.valueOf(customer.getOwnedGames().size()));
                    etLibraryValue.setText(String.valueOf(round(calculateLibraryValue(customer),2)));
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find the customer with id " + source.getStringExtra("id"), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateLibraryValue(Customer customer) {
        double total = 0;
        for (Game g :
                customer.getOwnedGames()) {
            total += g.getPrice();
        }
        return total;
    }

    public void onBtnDepositMoneyClick(View v){
        if (etDepositAmount.getText().toString().equals("")){
            Toast.makeText(this, "Please provide valid amount of money!", Toast.LENGTH_SHORT).show();
            return;
        }

        int cusId = Integer.parseInt(source.getStringExtra("id"));
        final double amount = Double.parseDouble(etDepositAmount.getText().toString());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WebshopService webshopService = retrofit.create(WebshopService.class);
        Call<Double> webshopCall = webshopService.addBalance(cusId, amount);

        webshopCall.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()){
                    Toast.makeText(DepositActivity.this, "Successfully added " + round((double)response.body(),2) + " to customer " + etCusName.getText(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DepositActivity.this, "Could not add " + amount + " to the current customer!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Toast.makeText(DepositActivity.this, "Error: Could not process the request!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
