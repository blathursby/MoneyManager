package com.sample.moneymanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class FragmentSettings extends Fragment implements AdapterView.OnItemSelectedListener {

    LinearLayout income;
    LinearLayout transport;
    LinearLayout food;
    LinearLayout utils;
    LinearLayout other;

    TextView incomeVal;
    TextView transportVal;
    TextView foodVal;
    TextView utilsVal;
    TextView otherVal;

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    public FragmentSettings() {
        super();
    }

    public static FragmentSettings newInstance() {
        FragmentSettings fragment = new FragmentSettings();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_settings, container, false);

        income = (LinearLayout) v.findViewById(R.id.settings_income);
        transport = (LinearLayout) v.findViewById(R.id.settings_transport_budget);
        food = (LinearLayout) v.findViewById(R.id.settings_food_budget);
        utils = (LinearLayout) v.findViewById(R.id.settings_utils_budget);
        other = (LinearLayout) v.findViewById(R.id.settings_other_budget);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentInteractionListener)getActivity()).onSettingsItemClicked("Income");
            }
        });
        transport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentInteractionListener)getActivity()).onSettingsItemClicked("Transport");
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentInteractionListener)getActivity()).onSettingsItemClicked("Food");
            }
        });
        utils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentInteractionListener)getActivity()).onSettingsItemClicked("Utils");
            }
        });
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentInteractionListener)getActivity()).onSettingsItemClicked("Other");
            }
        });
        incomeVal = (TextView) v.findViewById(R.id.settings_income_value);
        transportVal = (TextView) v.findViewById(R.id.settings_transport_budget_value);
        foodVal = (TextView) v.findViewById(R.id.settings_food_budget_value);
        utilsVal = (TextView) v.findViewById(R.id.settings_utils_budget_value);
        otherVal = (TextView) v.findViewById(R.id.settings_other_budget_value);

        spinner = (Spinner) v.findViewById(R.id.settings_budget_spinner);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populate();
    }

    public void populate() {
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        incomeVal.setText(prefs.getInt("Income", 0) + "$");
        transportVal.setText(prefs.getInt("Transport", 0) + "$");
        foodVal.setText(prefs.getInt("Food", 0) + "$");
        utilsVal.setText(prefs.getInt("Utils", 0) + "$");
        otherVal.setText(prefs.getInt("Other", 0) + "$");
        spinner.setSelection(adapter.getPosition(prefs.getString("Type", "Fixed")));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Type", (String) parent.getItemAtPosition(position));
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
