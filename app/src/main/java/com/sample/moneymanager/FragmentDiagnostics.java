package com.sample.moneymanager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class FragmentDiagnostics extends Fragment {

    TextView transport;
    TextView food;
    TextView utils;
    TextView other;

    TextView extransport;
    TextView exfood;
    TextView exutils;
    TextView exother;

    Button rescale;

    public static FragmentDiagnostics newInstance() {
        FragmentDiagnostics fragment = new FragmentDiagnostics();
        return fragment;
    }

    public FragmentDiagnostics() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_diagnostics, container, false);

        transport = (TextView) v.findViewById(R.id.diagnostics_transport);
        food = (TextView) v.findViewById(R.id.diagnostics_food);
        utils = (TextView) v.findViewById(R.id.diagnostics_utils);
        other = (TextView) v.findViewById(R.id.diagnostics_other);

        extransport = (TextView) v.findViewById(R.id.diagnostics_expense_transport);
        exfood = (TextView) v.findViewById(R.id.diagnostics_expense_food);
        exutils = (TextView) v.findViewById(R.id.diagnostics_expense_utils);
        exother = (TextView) v.findViewById(R.id.diagnostics_expense_other);

        rescale = (Button) v.findViewById(R.id.rescale_btn);
        rescale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rescaleBudget();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    populate();
    }

    private void cmp(TextView view, int current, int budget) {
        switch(compare(current, budget)) {
            case 0:
                view.setBackgroundResource(R.drawable.corners_green);
                break;
            case 1:
                view.setBackgroundResource(R.drawable.corners_yellow);
                break;
            case 2:
                view.setBackgroundResource(R.drawable.corners_red);
                break;
        }
    }

    private int compare(int current, int budget) {
        float f = (float) current / (float) budget;
        if(f < 0.7f) {
            return 0;
        }
        if(f<1.0f) {
            return 1;
        }
        if(f>1.0f) {
            return 2;
        }
        return 0;
    }

    private void rescaleBudget() {
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String type = prefs.getString("Type", "Fixed");
        if(type.equals("Fixed")) {
            Snackbar.make(getView(), "Fixed budget can't be rescaled", Snackbar.LENGTH_LONG).show();
            return;
        } else if(type.equals("Flexible")) {
            Snackbar.make(getView(), "Budget rescaled", Snackbar.LENGTH_LONG).show();
        }

        GregorianCalendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        Database db = new Database(getActivity().getBaseContext());
        int transportVal = db.getSingleMonthlyCategoryExpense("Transport", month, year);
        int foodVal = db.getSingleMonthlyCategoryExpense("Food", month, year);
        int utilsVal = db.getSingleMonthlyCategoryExpense("Utilities", month, year);
        int otherVal = db.getSingleMonthlyCategoryExpense("Other", month, year);
        db.close();
        db = null;

        int transportPref =  prefs.getInt("Transport", 0);
        int foodPref = prefs.getInt("Food", 0);
        int utilsPref = prefs.getInt("Utils", 0);
        int othePref = prefs.getInt("Other", 0);

        int extran = transportPref - transportVal;
        int exfod = foodPref - foodVal;
        int exuti = utilsPref - utilsVal;
        int exoth = othePref - otherVal;

        int budget = transportPref + foodPref + utilsPref + othePref;
        int expenses = transportVal + foodVal + utilsVal + otherVal;
        if (budget < expenses) {
            snack("Expenses are higher than budget");
            return;
        }

        int pn = extran + exfod + exuti + exoth;
        if(pn <= 0) {
            snack("Not enough budget remaining");
            return;
        }

        pn = 0;
        if(extran>0) {
            pn += extran;
        }
        if(exfod>0) {
            pn += exfod;
        }
        if(exuti>0) {
            pn += exuti;
        }
        if(exoth>0) {
            pn += exoth;
        }


        int totalBalance = 0;
        SharedPreferences.Editor edit = prefs.edit();
        if (extran < 0) {
            if(pn > Math.abs(extran) + 20) {
                int trbudget = transportPref + Math.abs(extran) + 20;
                pn -= (Math.abs(extran) + 20);
                edit.putInt("Transport", trbudget);
                totalBalance += (Math.abs(extran) + 20);
                System.out.println("RESCALED TRANS");
            }else {

                snack("Transport: insufficient budget");
            }
        }

        if (exfod < 0 ) {
            if(pn > Math.abs(exfod) + 20) {
                int fodbudget = foodPref + Math.abs(exfod) + 20;
                pn -= (Math.abs(exfod) + 20);
                edit.putInt("Food", fodbudget);
                totalBalance += (Math.abs(exfod) + 20);
                System.out.println("RESCALED FOOD");
            } else {

                snack("Food: insufficient budget");
            }
        }

        if (exuti < 0) {
            if(pn > Math.abs(exuti) + 20) {
                int utibud = utilsPref + Math.abs(exuti) + 20;
                pn -= (Math.abs(exuti) + 20);
                edit.putInt("Utils", utibud);
                totalBalance += (Math.abs(exuti) + 20);
                System.out.println("RESCALED UTILS");
            } else {

                snack("Utils: insufficient budget");
            }
        }

        if (exoth  < 0) {
            if(pn > Math.abs(exoth) + 20) {
                int othbud = othePref + Math.abs(exoth) + 20;
                pn -= (Math.abs(exoth) + 20);
                edit.putInt("Other", othbud);
                totalBalance += (Math.abs(exoth) + 20);
                System.out.println("RESCALED OTHER");
            } else {

                snack("Other: insufficient budget");
            }
        }

        if (extran - 20 > 0 && totalBalance > 0) {
            System.out.println("RESCALED extran - 20 > 0");
            if(totalBalance < extran) {
                int tr = transportPref - totalBalance;
                edit.putInt("Transport", tr);
            } else {
                int tr = transportPref - extran + 20;
                edit.putInt("Transport", tr);
            }
            totalBalance = (totalBalance - extran) - 20;
        }


        if (exfod - 20 > 0 && totalBalance > 0) {

            System.out.println("RESCALED exfod - 20 > 0");
            if(totalBalance < exfod) {
                int tr = foodPref - totalBalance;
                edit.putInt("Food", tr);
            } else {
                int fodbudget = foodPref - exfod + 20;
                edit.putInt("Food", fodbudget);

            }
            totalBalance = (totalBalance - exfod) - 20;
        }

        if (exuti - 20> 0 && totalBalance > 0) {
            System.out.println("RESCALED exuti - 20 > 0");
            if(totalBalance < exuti) {
                int tr = utilsPref - totalBalance;
                edit.putInt("Utils", tr);
            } else {
                int utibud = utilsPref - exuti + 20;
                edit.putInt("Utils", utibud);
            }
            totalBalance = (totalBalance - exuti) - 20;
        }

        if (exoth - 20> 0 && totalBalance > 0) {
            System.out.println("RESCALED othbud - 20 > 0");
            if(totalBalance < exoth) {
                int tr = othePref - totalBalance;
                edit.putInt("Other", tr);
            } else {
                int othbud = othePref - exoth + 20;
                edit.putInt("Other", othbud);
            }
        }

        System.out.println("totalBalance: "+totalBalance);
        edit.commit();
        populate();
    }

    private void snack(String msg) {
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void populate() {
        GregorianCalendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        Database db = new Database(getActivity().getBaseContext());
        int transportVal = db.getSingleMonthlyCategoryExpense("Transport", month, year);
        int foodVal = db.getSingleMonthlyCategoryExpense("Food", month, year);
        int utilsVal = db.getSingleMonthlyCategoryExpense("Utilities", month, year);
        int otherVal = db.getSingleMonthlyCategoryExpense("Other", month, year);
        db.close();
        db = null;

        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        int transportPref =  prefs.getInt("Transport", 0);
        int foodPref = prefs.getInt("Food", 0);
        int utilsPref = prefs.getInt("Utils", 0);
        int othePref = prefs.getInt("Other", 0);

        transport.setText("" + transportVal + "$ of " + transportPref + "$");
        food.setText("" + foodVal + "$ of " + foodPref + "$");
        utils.setText("" + utilsVal + "$ of " + utilsPref + "$");
        other.setText("" + otherVal + "$ of " + othePref + "$");

        int extran = transportPref - transportVal;
        int exfod = foodPref - foodVal;
        int exuti = utilsPref - utilsVal;
        int exoth = othePref - otherVal;
        extransport.setText("" + extran + "$");
        exfood.setText("" + exfod + "$");
        exutils.setText("" + exuti + "$");
        exother.setText("" + exoth + "$");

        cmp(transport, transportVal, transportPref);
        cmp(food, foodVal, foodPref);
        cmp(utils, utilsVal, utilsPref);
        cmp(other, otherVal, othePref);
    }
}
