package ru.shlomeno4ek.familybudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OperationActivity extends AppCompatActivity {

    private String idPurse;
    private TextView _tvNamePurse;
    private EditText _etName;
    private EditText _etDate;
    private EditText _etSumm;
    private Spinner _spinnerNamePurse;
    private Spinner _spinnerProcentReserve;

    private Button _btnAccept;
    private RadioButton _radio_inner;
    private RadioButton _radio_external;
    private RadioButton _radio_translation;
    private CheckBox _checkBoxPutinReserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("position");

        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));

        _spinnerNamePurse = (Spinner) findViewById(R.id.spinnerNamePurse);

        _radio_inner = (RadioButton) findViewById(R.id.radio_inner);
        _radio_inner.setOnClickListener(radioButtonClickListener);

        _radio_external = (RadioButton) findViewById(R.id.radio_external);
        _radio_external.setOnClickListener(radioButtonClickListener);

        _radio_translation = (RadioButton) findViewById(R.id.radio_translation);
        _radio_translation.setOnClickListener(radioButtonClickListener);

        _checkBoxPutinReserve = (CheckBox) findViewById(R.id.checkBoxPutinReserve);
        _checkBoxPutinReserve.setEnabled(false);
        _checkBoxPutinReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _spinnerProcentReserve.setEnabled(true);
            }
        });

        _spinnerProcentReserve = (Spinner) findViewById(R.id.spinnerProcentReserve);
        _spinnerProcentReserve.setEnabled(false);

    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radio_inner:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutinReserve.setEnabled(false);
                    break;
                case R.id.radio_external:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutinReserve.setEnabled(true);
                    break;
                case R.id.radio_translation:
                    _spinnerNamePurse.setEnabled(true);
                    _checkBoxPutinReserve.setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };
}
