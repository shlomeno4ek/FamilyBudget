package ru.shlomeno4ek.familybudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OperationActivity extends AppCompatActivity {

    private String idPurse;
    private TextView _tvNamePurse;
    private EditText _etName;
    private EditText _etDate;
    private EditText _etSumm;
    private Spinner _spinner;
    private Button _btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("position");

        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));


    }
}
