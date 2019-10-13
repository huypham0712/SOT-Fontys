package com.example.sotjaxrsandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.sotjaxrsandroidapp.MainActivity.BASEURL;

public class EditCustomerActivity extends AppCompatActivity {
    EditText cusName, cusEmail;
    Intent source;
    Customer oldData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        source = getIntent();

        cusName = findViewById(R.id.editTextNewCustomerName);
        cusEmail = findViewById(R.id.editTextEmail);

        cusName.setText(source.getStringExtra("name"));
        cusEmail.setText(source.getStringExtra("email"));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Customer> customerCall = service.findCustomer(source.getIntExtra("id", 0));

        customerCall.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                if (response.isSuccessful()){
                    oldData = response.body();
                } else {
                    Toast.makeText(EditCustomerActivity.this, "Could not retrieve information for editing customer!1", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUpdateClick(View v){
        if (cusName.getText().toString().equals("") || cusEmail.getText().toString().equals("")){
            Toast.makeText(this, "The customer name and email cannot be empty!!1", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Customer newData = new Customer(cusName.getText().toString(), cusEmail.getText().toString());
        newData.setId(oldData.getId());
        newData.setBalance(oldData.getBalance());
        newData.setOwnedGames(oldData.getOwnedGames());

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Void> customerCall = service.updateCustomer(newData);

        customerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    oldData = newData;
                    Toast.makeText(EditCustomerActivity.this, "Successfully update customer data!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditCustomerActivity.this, "Could not update customer data!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCustomerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onDeleteClick(View v){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Void> customerCall = service.deleteCustomer(oldData.getId());

        customerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditCustomerActivity.this, "Successfully remove customer!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditCustomerActivity.this, "Could not remove the current customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditCustomerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
