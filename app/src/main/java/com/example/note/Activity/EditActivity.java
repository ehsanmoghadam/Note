package com.example.note.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.note.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class EditActivity extends AppCompatActivity {
    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String DATE = "date";
    EditText title_editText;
    EditText description_editText;
    Button save_btn;
    Button edit_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title_editText = findViewById(R.id.et_editactivity_title);
        description_editText = findViewById(R.id.et_editactivity_description);
        save_btn = findViewById(R.id.bt_editActivity_save);
        edit_button = findViewById(R.id.bt_editActivity_cancel);


        Intent data_intent = getIntent();
        title_editText.setText(data_intent.getStringExtra(TITLE));
        description_editText.setText(data_intent.getStringExtra(DESCRIPTION));

        final int title_len = data_intent.getStringExtra(TITLE).length();
        int description_len = data_intent.getStringExtra(DESCRIPTION).length();




        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = title_editText.getText().toString();
                String description = description_editText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(TITLE, title);
                intent.putExtra(DESCRIPTION, description);
                intent.putExtra(DATE, generate_date());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }

    private String generate_date() {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(d);
    }

}
