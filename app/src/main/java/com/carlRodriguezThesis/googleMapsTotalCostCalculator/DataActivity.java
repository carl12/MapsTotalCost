package com.carlRodriguezThesis.googleMapsTotalCostCalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Distance;
import com.carlRodriguezThesis.googleMapsTotalCostCalculator.POJO.Duration;

public class DataActivity extends AppCompatActivity {

    DataHolder holder = DataHolder.getInstance();
    Distance[] dist;
    Duration[] time;

    int timeScale = 1;
    int[] distInt = new int[3];
    int[] durInt = new int[3];

    String units;
    double weight;
    double mpg;
    double poundCarbonPerday = 2.3;

    final double meterInMile = 1609;
    final double meterToFeet = 1/3.2804;
    final double secondMinute = 60;
    final double secondHour = secondMinute*60;
    final double secondDay = secondHour*24;
    final double poundToKilo = 0.453592;
    final double gallonsToLiters = 3.78541;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Total Cost Calculator");
        SharedPreferences sharedPref = getSharedPreferences(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.settingPref),0);

            units = sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "imperial");
            weight = Double.parseDouble(sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.weight), "180"));
            mpg = Double.parseDouble(sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.mpg),"26"));

        setContentView(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.layout.activity_data);
        Button btnClose = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myItent = new Intent(DataActivity.this,MapsActivity.class);
                startActivity(myItent);
            }
        });

        Button btnOnce = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnOnce);
        btnOnce.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                timeScale = 1;
                updateRows();
            }
        });

        Button btnWeek = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnWeek);
        btnWeek.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                timeScale = 7;
                updateRows();
            }
        });

        Button btnYear = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnAnnual);
        btnYear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                timeScale = 365;
                updateRows();
            }
        });

        Button btnSet = (Button) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.btnSettings);
        btnSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent myItent = new Intent(DataActivity.this,SettingActivity.class);
                startActivity(myItent);
            }
        });

    }

    @Override
    protected void onStart(){
        try{
        super.onStart();
        dist = holder.getDistance();
        time = holder.getTime();

        for(int i = 0; i < 3; i++) {
            distInt[i] = dist[i].getValue();
            durInt[i] = time[i].getValue();
        }
            updateRows();
        }
        catch (Exception e){
            Log.d("asdf",e.toString());
        }

    }

    private void updateRows(){
        SharedPreferences sharedPref = getSharedPreferences(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.settingPref),0);
        String units = sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.unitPref), "imperial");
        weight = Double.parseDouble(sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.weight), "180"));
        mpg = Double.parseDouble(sharedPref.getString(getString(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.string.mpg),"26"));

        setDistanceSquares();
        setDurationSquares();
        setCalorieRow();
        setCarbonRow();
        setGasRow();
    }

    private int getMin(int[] vals){
        int min = Integer.MAX_VALUE;
        int minIndex = 0;
        for(int i = 0; i < vals.length; i++){
            if(vals[i] < min){
                min = vals[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private void setDistanceSquares(){
        int minLoc = getMin(distInt);
        TextView[] txt = new TextView[3];

        txt[0] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView11);
        txt[1] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView12);
        txt[2] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView13);

        String[] msg = new String[3];
        for(int i = 0; i < distInt.length; i++){
            int curr = timeScale*distInt[i];
            String out;
            if(i == minLoc){
                txt[i].setTextColor(Color.GREEN);
                out = "";
            }
            else{
                txt[i].setTextColor(Color.RED);
                curr = curr - timeScale* distInt[minLoc];
                out = "+";
            }
            if(units.equals("imperial")){
                if(curr/meterInMile < 0.2){
                    out += Math.round(curr*meterToFeet)+" ft";
                }
                else{
                    out +=  Math.round(curr/meterInMile*10)/10.0 +" mi";
                }
            }else {
                if(curr < 300 ){
                    out += curr+" m";
                }else {
                    out+= Math.round(curr/1000.0*10.0)/10.0+" km";
                }
            }
            txt[i].setText(out);
        }
    }

    private void setDurationSquares(){
        int minLoc = getMin(durInt);
        TextView[] txt = new TextView[3];
        txt[0] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView21);
        txt[1] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView22);
        txt[2] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView23);

        String[] msg = new String[3];
        for(int i = 0; i < durInt.length; i++){
            int curr = timeScale*durInt[i];
            String out;
            if(i == minLoc){
                txt[i].setTextColor(Color.GREEN);
                out = "";
            }
            else{
                txt[i].setTextColor(Color.RED);
                curr = curr - timeScale*durInt[minLoc];
                out = "+";
            }

            if(curr < 1.5*secondMinute){
                out+= 1+" min";
            }
            else if(curr < secondHour){
                out+= Math.round(curr/secondMinute) +" min";
            }
            else if(curr < 5*secondHour){
                out+= (int)Math.floor(curr/secondHour) +
                        " hr " + Math.round(curr%secondHour/secondMinute) +" min";
            }
            else if (curr < secondDay){
                out+= (int)Math.round(curr/secondHour) +" hr";
            }
            else if(curr < 5*secondDay){
                out+= (int)Math.floor(curr/secondDay) +
                        " day " + Math.round(curr%secondDay/secondHour) +" hr";

            }else{
                out += (int)Math.round(curr/secondDay) +" day ";
            }

            txt[i].setText(out);

        }

    }

    private void setCalorieRow(){
        TextView[] txt = new TextView[3];
        txt[0] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView51);
        txt[1] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView52);
        txt[2] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView53);
        double walkCalsPerMile = (weight-100)*20/10.5+53;
        double bikeCalsPerMile = (weight-130)*17/60+36;
        int walkCalories = (int) Math.round(timeScale*distInt[2]/meterInMile*walkCalsPerMile);
        int bikeCalories = (int) Math.round(timeScale*distInt[1]/meterInMile*bikeCalsPerMile);


        txt[2].setText(walkCalories+"");
        txt[2].setTextColor(Color.GREEN);
        txt[1].setText(bikeCalories+"");
        txt[1].setTextColor(Color.GREEN);
        txt[0].setText("0");
        txt[0].setTextColor(Color.RED);

    }

    private void setCarbonRow(){

        TextView[] txt = new TextView[3];
        txt[0] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView31);
        txt[1] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView32);
        txt[2] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView33);


        double breathCarbonPerSec = poundCarbonPerday/secondDay;


        int carPoundCarbon = (int) Math.round(
                timeScale*20/mpg/meterInMile * distInt[0]+durInt[2]*breathCarbonPerSec);

        int bikePoundCarbon = (int) Math.round(timeScale*durInt[1]*breathCarbonPerSec);
        int walkPoundCarbon = (int) Math.round(timeScale*durInt[2]*breathCarbonPerSec);
        TextView carbon = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textViewCarbon) ;
        if(units.equals("imperial")){
            carbon.setText("Carbon(lbs)");
            txt[0].setText(carPoundCarbon+"");
            txt[1].setText(bikePoundCarbon+"");
            txt[2].setText(walkPoundCarbon+"");
        }
        else{
            carbon.setText("Carbon(kg)");
            double carKGCarbon = (Math.round(carPoundCarbon * poundToKilo * 10.0)/10.0);
            double bikeKGCarbon = (Math.round(bikePoundCarbon * poundToKilo * 10.0)/10.0);
            double walkKGCarbon = (Math.round(walkPoundCarbon * poundToKilo * 10.0)/10.0);

            txt[0].setText(carKGCarbon+"");
            txt[1].setText(bikeKGCarbon+"");
            txt[2].setText(walkKGCarbon+"");
        }

        txt[0].setTextColor(Color.RED);
        txt[1].setTextColor(Color.GREEN);
        txt[2].setTextColor(Color.GREEN);

    }

    private void setGasRow(){
        TextView[] txt = new TextView[3];
        txt[0] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView41);
        txt[1] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView42);
        txt[2] = (TextView) findViewById(com.carlRodriguezThesis.googleMapsTotalCostCalculator.R.id.textView43);

        double gallons = Math.round(100.0*(timeScale*distInt[2]*1.0)/meterInMile/mpg)/100.0;

        if(units.equals("imperial")){

            txt[0].setText(gallons+"");
        }else{
            double liters =  Math.round(gallons * gallonsToLiters*100.0)/100.0;
            txt[0].setText(liters+"");
        }

        txt[0].setTextColor(Color.RED);
        txt[1].setText("0");
        txt[1].setTextColor(Color.GREEN);
        txt[2].setText("0");
        txt[2].setTextColor(Color.GREEN);
    }
}
