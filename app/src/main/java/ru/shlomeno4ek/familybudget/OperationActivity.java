package ru.shlomeno4ek.familybudget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private RadioGroup _radio_group;
    private RadioButton _radio_inner;
    private RadioButton _radio_external;
    private RadioButton _radio_translation;
    private CheckBox _checkBoxPutInReserve;

    final Calendar c = Calendar.getInstance();
    int DIALOG_DATE = 1;
    int myYear = c.get(Calendar.YEAR);
    int myMonth = c.get(Calendar.MONTH);
    int myDay = c.get(Calendar.DAY_OF_MONTH);

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");

        _etSumm = (EditText) findViewById(R.id.etSumm);

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


        _radio_group = (RadioGroup) findViewById(R.id.radio_group);

        _radio_inner = (RadioButton) findViewById(R.id.radio_inner);
        _radio_inner.setOnClickListener(radioButtonClickListener);

        _radio_external = (RadioButton) findViewById(R.id.radio_external);
        _radio_external.setOnClickListener(radioButtonClickListener);

        _radio_translation = (RadioButton) findViewById(R.id.radio_translation);
        _radio_translation.setOnClickListener(radioButtonClickListener);

        context = OperationActivity.this;

        _btnAccept = (Button) findViewById(R.id.btnAccept);
        _btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_etSumm.getText().toString().equals("") || _tvInputDate.getText().toString().equals(R.string.inputDateOperation) || _etName.getText().toString().equals("") || (_radio_external.isChecked() || _radio_inner.isChecked() || _radio_translation.isChecked())) {
                    Toast.makeText(context, "Не все поля заполнены", Toast.LENGTH_LONG).show();
                } else {
                    double summ = Double.parseDouble(_etSumm.getText().toString());
                    String date = _tvInputDate.getText().toString();
                    String name = _etName.getText().toString();
                    int summReserve = 0;
                    //Получаем id переключателя
                    int type = _radio_group.getCheckedRadioButtonId();
                    if (type==0) {
                        CheckBox _checkBoxPutInReserve = (CheckBox) findViewById(R.id.checkBoxPutInReserve);
                        if (_checkBoxPutInReserve.isChecked()) {
                            summReserve += summ/100*Integer.parseInt(_spinnerProcentReserve.getSelectedItem().toString());
                        }
                    }
                }
            }
        });

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
