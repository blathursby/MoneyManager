package com.sample.moneymanager;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class FragmentExpenses extends Fragment {


    ListView listView;
    SpentMoneyAdapter adapter;

    TextView monthText;
    ImageView prevMonth;
    ImageView nextMonth;

    Button export;

    int currentMonth;
    int currentYear;

    public static FragmentExpenses newInstance() {
        FragmentExpenses fragment = new FragmentExpenses();
        return fragment;
    }

    public FragmentExpenses() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_expenses, container, false);
        GregorianCalendar calendar = new GregorianCalendar();
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentMonth = month;
        currentYear = year;
        listView = (ListView) v.findViewById(R.id.list_view_statistics);
        monthText = (TextView) v.findViewById(R.id.expenses_month_text);
        prevMonth = (ImageView) v.findViewById(R.id.expenses_month_prev);
        nextMonth = (ImageView) v.findViewById(R.id.expenses_month_next);
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPrevMonth();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextMonth();
            }
        });
        export = (Button) v.findViewById(R.id.export_to_csv);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToCsv();
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SpentMoneyAdapter(getActivity().getBaseContext());
        listView.setAdapter(adapter);
        updateExpenses();
        adapter.notifyDataSetChanged();
    }

    private void exportToCsv() {
        try {
            exportToCsv(currentMonth, currentYear);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportToCsv(int month, int year) throws IOException {
        Database db = new Database(getActivity().getBaseContext());
        ArrayList<SpentMoneyItem> list = new ArrayList<>();
        SpentMoneyItem car = addItem("Car", false, db.getSingleExpense("Car", month, year), "Transport");
        SpentMoneyItem Bus = addItem("Bus", false, db.getSingleExpense("Bus", month, year), "Transport");
        SpentMoneyItem Fuel = addItem("Fuel", false, db.getSingleExpense("Fuel", month, year), "Transport");
        SpentMoneyItem Taxi = addItem("Taxi", false, db.getSingleExpense("Taxi", month, year), "Transport");

        SpentMoneyItem Groceries = addItem("Groceries", false, db.getSingleExpense("Groceries", month, year), "Food");
        SpentMoneyItem Meat = addItem("Meat", false, db.getSingleExpense("Meat", month, year), "Food");

        SpentMoneyItem Water = addItem("Water", false, db.getSingleExpense("Water", month, year), "Utilities");
        SpentMoneyItem Electricity = addItem("Electricity", false, db.getSingleExpense("Electricity", month, year), "Utilities");
        SpentMoneyItem Heating = addItem("Heating", false, db.getSingleExpense("Heating", month, year), "Utilities");
        SpentMoneyItem Phone = addItem("Phone", false, db.getSingleExpense("Phone", month, year), "Utilities");
        SpentMoneyItem Rent =  addItem("Rent", false, db.getSingleExpense("Rent", month, year), "Utilities");

        SpentMoneyItem Accessories = addItem("Accessories", false, db.getSingleExpense("Accessories", month, year), "Other");
        SpentMoneyItem Entertainment = addItem("Entertainment", false, db.getSingleExpense("Entertainment", month, year), "Other");
        SpentMoneyItem Electronics = addItem("Electronics", false, db.getSingleExpense("Electronics", month, year), "Other");
        SpentMoneyItem Restaurants = addItem("Restaurants", false, db.getSingleExpense("Restaurants", month, year), "Other");
        SpentMoneyItem Vacations = addItem("Vacations", false, db.getSingleExpense("Vacations", month, year), "Other");
        db.close();
        db = null;
        list.add(car);
        list.add(Bus);
        list.add(Fuel);
        list.add(Taxi);
        list.add(Groceries);
        list.add(Meat);
        list.add(Water);
        list.add(Electricity);
        list.add(Heating);
        list.add(Phone);
        list.add(Rent);
        list.add(Accessories);
        list.add(Entertainment);
        list.add(Electronics);
        list.add(Restaurants);
        list.add(Vacations);

        String dirPath = Environment.getExternalStorageDirectory() + "/moneymanager/csv/";
        String path = Environment.getExternalStorageDirectory() + "/moneymanager/csv/"+ month + "_" + year + ".csv";
        File f = new File(dirPath);
        if(!f.exists()) {
            f.mkdirs();
        }
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        String csv = "";
        String cols = "name,value,category,month,year\n";
        csv += cols;
        for(int i=0;i<list.size();i++) {
            String str = list.get(i).toString();
            str += "," + month + "," + year + "\n";
            csv += str;
        }
        out.write(csv.getBytes());
        out.flush();
        out.close();
    }

    private void loadNextMonth() {
        if(currentMonth<11) {
            currentMonth++;
        } else {
            currentMonth=0;
            currentYear++;
        }

        String month = getMonthName(currentMonth);
        monthText.setText(month + " " + currentYear);
        updateExpenses(currentMonth, currentYear);
    }

    private void loadPrevMonth() {
        if(currentMonth>0) {
            currentMonth--;
        } else {
            currentMonth=11;
            currentYear--;
        }

        String month = getMonthName(currentMonth);
        monthText.setText(month + " " + currentYear);
        updateExpenses(currentMonth, currentYear);
    }

    private SpentMoneyItem addItem(String text, boolean header, int value, String category) {
        SpentMoneyItem item = new SpentMoneyItem(text, header, value);
        item.setCategory(category);
        adapter.addItem(item);
        return item;
    }



    private void updateExpenses() {
        adapter.clear();
        Database db = new Database(getActivity().getBaseContext());
        addItem("Transport", true, 0, "");
        addItem("Car", false, db.getSingleExpense("Car"), "Transport");
        addItem("Bus", false, db.getSingleExpense("Bus"), "Transport");
        addItem("Fuel", false, db.getSingleExpense("Fuel"), "Transport");
        addItem("Taxi", false, db.getSingleExpense("Taxi"), "Transport");

        addItem("Food", true, 0, "");
        addItem("Groceries", false, db.getSingleExpense("Groceries"), "Food");
        addItem("Meat", false, db.getSingleExpense("Meat"), "Food");

        addItem("Utilities", true, 0, "");
        addItem("Water", false, db.getSingleExpense("Water"), "Utilities");
        addItem("Electricity", false, db.getSingleExpense("Electricity"), "Utilities");
        addItem("Heating", false, db.getSingleExpense("Heating"), "Utilities");
        addItem("Phone", false, db.getSingleExpense("Phone"), "Utilities");
        addItem("Rent", false, db.getSingleExpense("Rent"), "Utilities");

        addItem("Other", true, 0, "");
        addItem("Accessories", false, db.getSingleExpense("Accessories"), "Other");
        addItem("Entertainment", false, db.getSingleExpense("Entertainment"), "Other");
        addItem("Electronics", false, db.getSingleExpense("Electronics"), "Other");
        addItem("Restaurants", false, db.getSingleExpense("Restaurants"), "Other");
        addItem("Vacations", false, db.getSingleExpense("Vacations"), "Other");
        db.close();
        db = null;
    }

    private void updateExpenses(int month, int year) {
        adapter.clear();
        Database db = new Database(getActivity().getBaseContext());
        addItem("Transport", true, 0, "");
        addItem("Car", false, db.getSingleExpense("Car", month, year), "Transport");
        addItem("Bus", false, db.getSingleExpense("Bus", month, year), "Transport");
        addItem("Fuel", false, db.getSingleExpense("Fuel", month, year), "Transport");
        addItem("Taxi", false, db.getSingleExpense("Taxi", month, year), "Transport");

        addItem("Food", true, 0, "");
        addItem("Groceries", false, db.getSingleExpense("Groceries", month, year), "Food");
        addItem("Meat", false, db.getSingleExpense("Meat", month, year), "Food");

        addItem("Utilities", true, 0, "");
        addItem("Water", false, db.getSingleExpense("Water", month, year), "Utilities");
        addItem("Electricity", false, db.getSingleExpense("Electricity", month, year), "Utilities");
        addItem("Heating", false, db.getSingleExpense("Heating", month, year), "Utilities");
        addItem("Phone", false, db.getSingleExpense("Phone", month, year), "Utilities");
        addItem("Rent", false, db.getSingleExpense("Rent", month, year), "Utilities");

        addItem("Other", true, 0, "");
        addItem("Accessories", false, db.getSingleExpense("Accessories", month, year), "Other");
        addItem("Entertainment", false, db.getSingleExpense("Entertainment", month, year), "Other");
        addItem("Electronics", false, db.getSingleExpense("Electronics", month, year), "Other");
        addItem("Restaurants", false, db.getSingleExpense("Restaurants", month, year), "Other");
        addItem("Vacations", false, db.getSingleExpense("Vacations", month, year), "Other");
        db.close();
        db = null;
        adapter.notifyDataSetChanged();
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
