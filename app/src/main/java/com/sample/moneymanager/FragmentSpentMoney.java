package com.sample.moneymanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class FragmentSpentMoney extends Fragment {

    ListView listView;
    SpentMoneyAdapter adapter;

    public static FragmentSpentMoney newInstance() {
        FragmentSpentMoney fragment = new FragmentSpentMoney();
        return fragment;
    }

    public FragmentSpentMoney() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_spent_money, container, false);
        listView = (ListView) v.findViewById(R.id.list_spent_money);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new SpentMoneyAdapter(getActivity().getBaseContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpentMoneyItem item = (SpentMoneyItem) adapter.getItem(position);
                ((FragmentInteractionListener) getActivity()).onSpentMoneyItemClicked(item.getText());
            }
        });

        update();
    }

    private void addItem(String text, boolean header, int value, String category) {
        SpentMoneyItem item = new SpentMoneyItem(text, header, value);
        item.setCategory(category);
        adapter.addItem(item);
    }

    public void update() {
        updateExpenses();
        adapter.notifyDataSetChanged();
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

}
