package ru.shlomeno4ek.familybudget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class OperationActivity extends AppCompatActivity {

    private String idPurse;
    private TextView _tvNamePurse;
    private TextView _tvInputDate;
    private EditText _etName;
    private EditText _etDate;
    private EditText _etSumm;
    private Spinner _spinnerNamePurse;
    private Spinner _spinnerProcentReserve;
    private Button _btnAccept;
    private RadioButton _radio_inner;
    private RadioButton _radio_external;
    private RadioButton _radio_translation;
    private CheckBox _checkBoxPutInReserve;

    final Calendar c = Calendar.getInstance();
    int DIALOG_DATE = 1;
    int myYear = c.get(Calendar.YEAR);
    int myMonth = c.get(Calendar.MONTH);
    int myDay = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");

        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));

        _tvInputDate = (TextView) findViewById(R.id.tvInputDate);
        _tvInputDate.setClickable(true);
        _tvInputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickDate();
            }
        });

        _spinnerNamePurse = (Spinner) findViewById(R.id.spinnerNamePurse);
        _spinnerNamePurse.setEnabled(false);

        _spinnerProcentReserve = (Spinner) findViewById(R.id.spinnerProcentReserve);
        _spinnerProcentReserve.setEnabled(false);

        _checkBoxPutInReserve = (CheckBox) findViewById(R.id.checkBoxPutInReserve);
        _checkBoxPutInReserve.setEnabled(false);
        _checkBoxPutInReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_checkBoxPutInReserve.isChecked()) {_spinnerProcentReserve.setEnabled(true);} else {_spinnerProcentReserve.setEnabled(false);}
            }
        });


        _radio_inner = (RadioButton) findViewById(R.id.radio_inner);
        _radio_inner.setOnClickListener(radioButtonClickListener);

        _radio_external = (RadioButton) findViewById(R.id.radio_external);
        _radio_external.setOnClickListener(radioButtonClickListener);

        _radio_translation = (RadioButton) findViewById(R.id.radio_translation);
        _radio_translation.setOnClickListener(radioButtonClickListener);

    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radio_inner:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutInReserve.setEnabled(false);
                    _checkBoxPutInReserve.setChecked(false);
                    _spinnerProcentReserve.setEnabled(false);

                    break;
                case R.id.radio_external:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutInReserve.setEnabled(true);
                    _spinnerProcentReserve.setEnabled(false);
                    break;
                case R.id.radio_translation:
                    _spinnerNamePurse.setEnabled(true);
                    _checkBoxPutInReserve.setEnabled(false);
                    _checkBoxPutInReserve.setChecked(false);
                    _spinnerProcentReserve.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };

    public void onclickDate() {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;
            _tvInputDate.setText(myDay + "." + ++myMonth + "." + myYear);
        }
    };
}
