package com.example.healthcard_demo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MedicalHistory extends AppCompatActivity {
Spinner sp1;
TestAdapter adapter;
    ArrayAdapter<String> ad;
    List<String> subject;
    String    usermedicalid;
    TableLayout layout;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        sp1=(Spinner) findViewById(R.id.spin_history);
         layout=(TableLayout) findViewById(R.id.table_tblecurrentdisease);

        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();
            Bundle bundle = getIntent().getExtras();

            usermedicalid = bundle.getString("MedicalID");
           // id=bundle.getString("Key");


            List<String>list;
            list=new ArrayList<String>();
            list.add("Disesase History");
            list.add("Old History");


            ArrayAdapter<String> datAdapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
            datAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(datAdapter1);

            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(i==0){

                        adddiseaseshistory();

                    }

                    if(i==1){
                        Intent ii=new Intent(MedicalHistory.this,OldMedicalHistory.class);
                        startActivity(ii);
                    }
                    if(i==2){
                        Intent ii=new Intent(MedicalHistory.this,NewMedicalHistory.class);
                        startActivity(ii);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        }catch (Exception e){}
    }

    private void adddiseaseshistory() {
        layout.setStretchAllColumns(true);
        TableRow row1 = new TableRow(this);
        TableLayout.LayoutParams lp2 = new TableLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        lp2.setMargins(0, 30, 0, 0);
        TextView tv2 = new TextView(this);
        tv2.setText("Recover.");
        tv2.setTextSize(20);
        row1.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText("Diseases.");
        tv3.setTextSize(20);
        row1.addView(tv3);
        layout.addView(row1);


        Cursor cursor = adapter.selectDisesehistory(usermedicalid);
        int l=0;
        while (cursor.moveToNext()) {
            TableRow row2 = new TableRow(this);
            row2.setMinimumHeight(50);
            if (l % 2 == 0)
                row2.setBackgroundColor(getResources().getColor(R.color.purple_100));
            else
                row2.setBackgroundColor(getResources().getColor(R.color.purple_200));
            row2.getBackground().setAlpha(40);

            final TextView oid = new TextView(this);
            oid.setText(cursor.getString(1));
            //mediid = (cursor.getString(0));
            oid.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            oid.setTextColor(Color.BLACK);
            oid.setPadding(0, 0, 0, 0);


            final TextView name = new TextView(this);
            name.setText(cursor.getString(2));
            // udname = (cursor.getString(2));
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            name.setTextColor(Color.BLACK);

            name.setPadding(0, 0, 0, 0);
            row2.addView(oid);
            row2.addView(name);
            row2.setLayoutParams(lp2);
            layout.addView(row2);
            l++;


        }





    }
}