package ru.shlomeno4ek.familybudget;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private Spinner _spinner;
    private Button _btnAccept;
    private RadioButton _radio_inner;
    private RadioButton _radio_external;
    private RadioButton _radio_translation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("position");

        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));

        _spinner = (Spinner) findViewById(R.id.spinner);

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
                case R.id.radio_inner: _spinner.setEnabled(false);
                    break;
                case R.id.radio_external: _spinner.setEnabled(false);
                    break;
                case R.id.radio_translation: _spinner.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };
}
