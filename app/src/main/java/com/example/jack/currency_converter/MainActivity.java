package com.example.jack.currency_converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private Spinner currency1Spinner, currency2Spinner;
    private ArrayAdapter<CharSequence> currencyArrayAdapter;
    private int[] currencyRateArray;
    private EditText currency1Amt, currency2Amt;
    private double exchangeRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        setupSpinners();
        setupEditText();
        getActionBar().setTitle("Currency Converter");
    }

    private void assignViews() {
        currency1Spinner = (Spinner) findViewById(R.id.currency1Spinner);
        currency2Spinner = (Spinner) findViewById(R.id.currency2Spinner);
        currency1Amt = (EditText) findViewById(R.id.currency1Amt);
        currency2Amt = (EditText) findViewById(R.id.currency2Amt);
    }

    private void setupSpinners() {
        currencyArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencyArray, android.R.layout.simple_spinner_item);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency1Spinner.setAdapter(currencyArrayAdapter);
        currency2Spinner.setAdapter(currencyArrayAdapter);

        currency1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrencyAmount(currency1Amt, currency2Amt,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currency2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateCurrencyAmount(currency2Amt, currency1Amt, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currencyRateArray = getResources().getIntArray(R.array.currencyRateArray);
    }

    class CurrencyTextWatcher implements TextWatcher {
        EditText source, target;
        CurrencyTextWatcher otherWatcher;
        boolean isWatching, isInverseExchangeRate;

        public CurrencyTextWatcher(EditText source, EditText target, boolean inverseRate) {
            this.source = source;
            this.target = target;
            this.isWatching = true;
            this.isInverseExchangeRate = inverseRate;
        }

        public void setOtherWatcher(CurrencyTextWatcher otherWatcher) {
            this.otherWatcher = otherWatcher;
        }
        public void setIsWatching(boolean isWatching) {
            this.isWatching = isWatching;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (isWatching) {
                otherWatcher.setIsWatching(false);
                updateCurrencyAmount(source, target, isInverseExchangeRate);
                otherWatcher.setIsWatching(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private void setupEditText() {
        CurrencyTextWatcher watcher1 = new CurrencyTextWatcher(currency1Amt, currency2Amt, false);
        CurrencyTextWatcher watcher2 = new CurrencyTextWatcher(currency2Amt, currency1Amt, true);
        watcher1.setOtherWatcher(watcher2);
        watcher2.setOtherWatcher(watcher1);

        currency1Amt.addTextChangedListener(watcher1);
        currency2Amt.addTextChangedListener(watcher2);
    }

    private double roundOff(double num, int dp) {
        double exponent = Math.pow(10, dp);
        return ((double) Math.round(num * exponent)) / exponent;
    }

    private double getExchangeRate() {
        if (currency1Spinner.getSelectedItemPosition() >= 0
                && currency2Spinner.getSelectedItemPosition() >= 0) {
            int currency1 = currency1Spinner.getSelectedItemPosition();
            int currency2 = currency2Spinner.getSelectedItemPosition();

            exchangeRate = (double)currencyRateArray[currency2]/
                   (double)currencyRateArray[currency1];
        }
        else {
            exchangeRate = 0;
        }
        return exchangeRate;
    }

    private void updateCurrencyAmount(EditText source, EditText output, boolean inverseExchangeRate) {
        if (source.getText().length() > 0) {
            getExchangeRate();
            try {
                double exRate = inverseExchangeRate ? 1/exchangeRate: exchangeRate;
                Double outputValue = Double.parseDouble(source.getText().toString()) * exRate;
                output.setText(String.valueOf(roundOff(outputValue, 2)));
                if (source.getText().length() == 0)
                    output.getText().clear();
            } catch (Exception e) {
                source.getText().clear();
                output.getText().clear();
            }
        }
    }
}
