package com.kunaldhongadi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class AddPersonalData extends AppCompatActivity {

    TextView pTitle,pPageCounter;
    ImageView pCancelBtn;

    private TextInputLayout pFullName,pNationality,pBirthDate,pBirthCountry;
    TextInputEditText pBirthDateInput;
    Button pSubmitBtn;


    DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_data);

        pCancelBtn = findViewById(R.id.p_cancel_btn);
        pTitle = findViewById(R.id.p_title_text);
        pPageCounter = findViewById(R.id.p_title_text_2);
        pFullName = findViewById(R.id.p_full_name);
        pNationality = findViewById(R.id.p_nationality);
        pBirthCountry = findViewById(R.id.p_birth_country);
        pBirthDate = findViewById(R.id.p_birth_date);
        pBirthDateInput = findViewById(R.id.p_birth_date_input);
        pSubmitBtn = findViewById(R.id.p_submit_btn);

        pBirthDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AddPersonalData.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                pBirthDateInput.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });


        //If the user has entered data set title to update it.
        if(MainActivity.getHasData()){
            pTitle.setText("Update Personal Data");
        }

        pCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(AddPersonalData.this,MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            }
        });

        pSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddPersonalData.this, AddContactData.class);
                Pair[] pairs = new Pair[4];
                pairs[0] = new Pair<View, String>(pCancelBtn, "transition_cancel_btn");
                pairs[1] = new Pair<View, String>(pTitle, "transition_titleView");
                pairs[2] = new Pair<View, String>(pPageCounter,"transition_page_counter");
                pairs[3] = new Pair<View, String>(pSubmitBtn, "transition_add_data_btn");

                String full_name = pFullName.getEditText().getText().toString().trim();
                String nationality = pNationality.getEditText().getText().toString().trim();
                String birth_country = pBirthCountry.getEditText().getText().toString().trim();
                String birth_date = pBirthDate.getEditText().getText().toString().trim();


//                String re = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/(19|20)[0-9]{2}$";

                //Removing all errors before submitting again
                removeError(pFullName);
                removeError(pNationality);
                removeError(pBirthCountry);
                removeError(pBirthDate);

                //Name validation
                if (TextUtils.isEmpty(full_name)) {
                    pFullName.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(pFullName);
                }

                //Nationality validation
                if (TextUtils.isEmpty(nationality)) {
                    pNationality.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(pNationality);
                }

                //Birth country validation
                if (TextUtils.isEmpty(birth_country)) {
                    pBirthCountry.setError("This field cannot be empty");
                    return;
                } else {
                    removeError(pBirthCountry);
                }

//                //Date validation
                if(TextUtils.isEmpty(birth_date)){
                    pBirthDate.setError("This field cannot be empty");
                    return;
                }else{
                    removeError(pBirthDate);
                }


//                Passing data to the next activity
                intent.putExtra("p_Full_Name",full_name);
                intent.putExtra("p_Nationality",nationality);
                intent.putExtra("p_birth_country",birth_country);
                intent.putExtra("p_birth_date",birth_date);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(AddPersonalData.this, pairs);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });

    }


    //Function to remove the previous errors
    public void removeError(TextInputLayout data){
        data.setError(null);
        data.setErrorEnabled(false);
    }

}