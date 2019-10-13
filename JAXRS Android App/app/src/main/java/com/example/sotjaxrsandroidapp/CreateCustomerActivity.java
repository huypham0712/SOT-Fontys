package com.example.sotjaxrsandroidapp;

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

public class CreateCustomerActivity extends AppCompatActivity {
    EditText tbCusName;
    EditText tbCusEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_customer);
        tbCusName = findViewById(R.id.editTextNewCustomerName);
        tbCusEmail = findViewById(R.id.editTextNewCustomerEmail);
    }

    public void onButtonCreateCustomerClick(View v){
        if (tbCusEmail.getText().toString().equals("") || tbCusName.getText().toString().equals("")){
            Toast.makeText(this, "The customer name and email cannot be empty!1", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CustomerService service = retrofit.create(CustomerService.class);
        final Call<Void> customerCall = service.createCustomer(tbCusName.getText().toString(), tbCusEmail.getText().toString());

        customerCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Toast.makeText(CreateCustomerActivity.this, "Successfully created new customer!!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreateCustomerActivity.this, "Could not create new customer with email " + tbCusEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateCustomerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
