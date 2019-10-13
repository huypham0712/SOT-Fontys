package com.example.sotjaxrsandroidapp;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    TextView tvIntro;
    Button btnToCustomerService, btnToGameService, btnToWebshopService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();

        tvIntro = getActivity().findViewById(R.id.tvHomeIntro);
        tvIntro.setText("Welcome to the SOT JAXRS Client in Android Application" +
                "\n" +
                "\nThe application simulates a manager application for administration in controlling a game store's customers and games!" +
                "\n" +
                "\nThe application provides three services which are:" +
                "\n\t- The Customer Service which contains CRUD operations on Customer" +
                "\n\t- The Game Service which contains CRUD operation on Game" +
                "\n\t- The Webshop Service which is a composite service that performs the procedures that requires both Customer and Game service!!" +
                "\n" +
                "\nPLEASE FOLLOW THIS SECTION CAREFULLY: " +
                "\n" +
                "\n\t1. To make sure the application can consume the APIs (which will be host on this laptop/PC), " +
                "you MUST change the IP address in \"BASEURL\" string to your PC/LAPTOP's IP ADDRESS." +
                "\n" +
                "\n\t2. In the file \"network_security_config.xml\" which located in the directory \"res/xml\", " +
                "you MUST change the IP address in <domain> into your PC/LAPTOP's IP ADDRESS as well." +
                "\n" +
                "\n\t3. Only make the change to the IP address, make sure the IP addresses in bullet 1 and 2 are the same!");

        btnToCustomerService = getActivity().findViewById(R.id.btnToCustomerService);
        btnToGameService = getActivity().findViewById(R.id.btnToGameService);
        btnToWebshopService = getActivity().findViewById(R.id.btnToWebshopService);

        btnToCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new CustomerFragment()).commit();
            }
        });

        btnToGameService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GameFragment()).commit();
            }
        });

        btnToWebshopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new WebshopFragment()).commit();
            }
        });
    }
}
