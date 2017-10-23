package com.carlRodriguezThesis.googleMapsTotalCostCalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class SettingActivity extends AppCompatActivity {
    String units;
    String mpgStr;
    String weightStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Total Cost Calculator");

        setContentView(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.layout.activity_setting);

        Button btnCancel = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myItent = new Intent(SettingActivity.this,DataActivity.class);
                startActivity(myItent);
            }
        });

        Button btnSave = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.settingPref),0);
                SharedPreferences.Editor editor = prefs.edit();

                RadioButton radImp = (RadioButton) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.radioButtonImp);
                RadioButton radMet = (RadioButton) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.radioButtonMet);
                EditText weight = (EditText)findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.editTextWeight);
                EditText mpg = (EditText)findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.editTextMPG);

                if(radImp.isChecked()){
                    editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "imperial");
                }else{
                    editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "metric");
                }

                String weightString = weight.getText().toString();
                String mpgString = mpg.getText().toString();
                Log.d("asdf",weightString+" "+ mpgString);
                editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.weight), weightString);
                editor.putString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.mpg), mpgString);

                editor.commit();

                Intent myItent = new Intent(SettingActivity.this,DataActivity.class);
                startActivity(myItent);
            }
        });

    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences sharedPref = getSharedPreferences(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.settingPref),0);
        units = sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "imperial");
        weightStr = sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.weight), "180");
        mpgStr = sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.mpg),"26");

        if(units.equals("imperial")){
            RadioButton radImp = (RadioButton) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.radioButtonImp);
            radImp.setChecked(true);
        }else{
            RadioButton metImp = (RadioButton) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.radioButtonMet);
            metImp.setChecked(true);
        }
        EditText weight = (EditText)findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.editTextWeight);
        weight.setText(weightStr);
        EditText mpg = (EditText)findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.editTextMPG);
        mpg.setText(mpgStr);

    }

}
