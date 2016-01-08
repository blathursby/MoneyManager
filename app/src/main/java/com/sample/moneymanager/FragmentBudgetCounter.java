package com.sample.moneymanager;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FragmentBudgetCounter extends Fragment {

    TextView monthlyTransport;
    TextView monthlyFood;
    TextView monthlyUtils;
    TextView monthlyOther;
    TextView yearlyTransport;
    TextView yearlyFood;
    TextView yearlyUtils;
    TextView yearlyOther;
    TextView yearName;
    TextView monthName;
    ImageView monthPrev;
    ImageView monthNext;
    ImageView yearNext;
    ImageView yearPrev;

    int currentMonth;
    int currentYear;

    public static FragmentBudgetCounter newInstance() {
        FragmentBudgetCounter fragment = new FragmentBudgetCounter();
        return fragment;
    }

    public FragmentBudgetCounter() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_budget_counter, container, false);
        monthlyTransport = (TextView) v.findViewById(R.id.budget_counter_monthly_transport);
        monthlyFood = (TextView) v.findViewById(R.id.budget_counter_monthly_food);
        monthlyUtils = (TextView) v.findViewById(R.id.budget_counter_monthly_utils);
        monthlyOther = (TextView) v.findViewById(R.id.budget_counter_monthly_other);
        yearlyTransport = (TextView) v.findViewById(R.id.budget_budget_yearly_transport);
        yearlyFood = (TextView) v.findViewById(R.id.budget_budget_yearly_food);
        yearlyUtils = (TextView) v.findViewById(R.id.budget_budget_yearly_utils);
        yearlyOther = (TextView) v.findViewById(R.id.budget_budget_yearly_other);
        yearName = (TextView) v.findViewById(R.id.budget_year_name);
        monthName = (TextView) v.findViewById(R.id.budget_month_name);
        monthPrev = (ImageView) v.findViewById(R.id.budget_month_prev);
        monthPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevMonth();
            }
        });
        monthNext = (ImageView) v.findViewById(R.id.budget_month_next);
        monthNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextMonth();
            }
        });
        yearPrev = (ImageView) v.findViewById(R.id.budget_year_prev);
        yearPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevYear();
            }
        });
        yearNext = (ImageView) v.findViewById(R.id.budget_year_next);
        yearNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextYear();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GregorianCalendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentMonth = month;
        currentYear = year;
        getMonthlyBudget(month, year);
        getYearlyBudget(year);
    }

    private void loadNextMonth() {
        if(currentMonth<11) {
            currentMonth++;
            getMonthlyBudget(currentMonth, currentYear);
        }
    }

    private void loadPrevMonth() {
        if(currentMonth>0) {
            currentMonth--;
            getMonthlyBudget(currentMonth, currentYear);
        }
    }

    private void loadNextYear() {
        currentYear++;
        getYearlyBudget(currentYear);
    }

    private void loadPrevYear() {
        currentYear--;
        getYearlyBudget(currentYear);
    }

    private void getMonthlyBudget(int month, int year) {
        monthName.setText(getMonthName(month));
        Database db = new Database(getActivity().getBaseContext());
        int transport = db.getSingleMonthlyCategoryExpense("Transport", month, year);
        int food = db.getSingleMonthlyCategoryExpense("Food", month, year);
        int utils = db.getSingleMonthlyCategoryExpense("Utilities", month, year);
        int other = db.getSingleMonthlyCategoryExpense("Other", month, year);
        db.close();
        db = null;

        monthlyTransport.setText("" + transport + "$");
        monthlyFood.setText("" + food + "$");
        monthlyUtils.setText("" + utils + "$");
        monthlyOther.setText("" + other + "$");
    }

    private void getYearlyBudget(int year) {
        yearName.setText("" + year);
        Database db = new Database(getActivity().getBaseContext());
        int transport = db.getSingleYearlyCategoryExpense("Transport", year);
        int food = db.getSingleYearlyCategoryExpense("Food", year);
        int utils = db.getSingleYearlyCategoryExpense("Utilities", year);
        int other = db.getSingleYearlyCategoryExpense("Other", year);
        db.close();
        db = null;

        yearlyTransport.setText("" + transport + "$");
        yearlyFood.setText("" + food + "$");
        yearlyUtils.setText("" + utils + "$");
        yearlyOther.setText("" + other + "$");
    }

    private String getMonthName(int month) {
        String name = "";
        switch(month) {
            case 0:
                name = "January";
                break;
            case 1:
                name = "February";
                break;
            case 2:

                name = "March";
                break;
            case 3:
                name = "April";
                break;
            case 4:
                name = "May";
                break;
            case 5:
                name = "June";
                break;
            case 6:

                name = "July";
                break;
            case 7:
                name = "August";
                break;
            case 8:
                name = "September";
                break;
            case 9:
                name = "October";
                break;
            case 10:
                name = "November";
                break;
            case 11:
                name = "December";
                break;
        }
        return name;
    }
}
