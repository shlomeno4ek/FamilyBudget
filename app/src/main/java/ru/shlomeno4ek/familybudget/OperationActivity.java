package ru.shlomeno4ek.familybudget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

import static ru.shlomeno4ek.familybudget.MainActivity.LOG_TAG;

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
    private RadioGroup _radio_group_view;
    private RadioGroup _radio_group_type;
    private RadioButton _radio_inner;
    private RadioButton _radio_external;
    private RadioButton _radio_translation;
    private RadioButton _rbIncome;
    private RadioButton _rbExpenses;
    private CheckBox _checkBoxPutInReserve;

    final Calendar c = Calendar.getInstance();
    int DIALOG_DATE = 1;
    int myYear = c.get(Calendar.YEAR);
    int myMonth = c.get(Calendar.MONTH);
    int myDay = c.get(Calendar.DAY_OF_MONTH);


    BudgetDbHelper dbHelper;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);

        // создаем объект для создания и управления версиями БД
        dbHelper = new BudgetDbHelper(this);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");

        _etSumm = (EditText) findViewById(R.id.etSumm);
        _etName = (EditText) findViewById(R.id.etName);

        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));

        _tvInputDate = (TextView) findViewById(R.id.tvInputDate);
        _tvInputDate.setClickable(true);
        _tvInputDate.setText(myDay + "." + ++myMonth + "." + myYear);
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


        _radio_group_view = (RadioGroup) findViewById(R.id.radio_group_view);
        _radio_group_type = (RadioGroup) findViewById(R.id.radio_group_type);

        _radio_inner = (RadioButton) findViewById(R.id.radio_inInner);
        _radio_inner.setOnClickListener(radioButtonClickListener);

        _radio_external = (RadioButton) findViewById(R.id.radio_external);
        _radio_external.setOnClickListener(radioButtonClickListener);

        _radio_translation = (RadioButton) findViewById(R.id.radio_translation);
        _radio_translation.setOnClickListener(radioButtonClickListener);

        _rbIncome = (RadioButton) findViewById(R.id.rbIncome);
        _rbExpenses = (RadioButton) findViewById(R.id.rbExpenses);

        context = OperationActivity.this;

        _btnAccept = (Button) findViewById(R.id.btnAccept);
        _btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {

                if (_etSumm.getText().toString()==null || _etName.getText().toString().equals("") || !(_radio_external.isChecked() || _radio_inner.isChecked() || _radio_translation.isChecked()) ||!(_rbIncome.isChecked() || _rbExpenses.isChecked())) {
                    Toast.makeText(context, "Не все поля заполнены", Toast.LENGTH_LONG).show();
                } else {
                    double summ = Double.parseDouble(_etSumm.getText().toString());
                    String date = _tvInputDate.getText().toString();
                    String name = _etName.getText().toString();
                    int type = 1;
                    if (_rbExpenses.isChecked()) type*=-1;

                    //Получаем id переключателя
                    int viewOperations = _radio_group_view.getCheckedRadioButtonId();

                    switch (viewOperations) {
                        case R.id.radio_external:
                            int summReserve = 0;
                            CheckBox _checkBoxPutInReserve = (CheckBox) findViewById(R.id.checkBoxPutInReserve);

                            if (_checkBoxPutInReserve.isChecked()) {
                                summReserve += summ/100*Integer.parseInt(_spinnerProcentReserve.getSelectedItem().toString());
                            }

                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_EXTERNAL,summ*type, name, date);
                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_EXTERNAL,summ*type, name+7, date);

                            if (summReserve>0) {
                                putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_INNER,summReserve, "В резерв из: " + name, date);
                            }

                            //Получаем баланс и резерв кошелька
                            String query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_BALANS + ", "
                                    + FamilyBudget.PurseEntry.COLUMN_RESERVE + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME + " WHERE _id = " + idPurse;

                            // подключаемся к БД
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            double balans = 0;
                            double reserv = 0;
                            Cursor cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                balans = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS));
                                reserv = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE));
                                Log.i("LOG_TAG", "ROW " + idPurse + " HAS BALANS " + balans + " AND RESERV " + reserv);
                            }
                            cursor2.close();
                            balans += summ*type;
                            reserv += summReserve;

                            ContentValues values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_BALANS, balans);
                            values.put(FamilyBudget.PurseEntry.COLUMN_RESERVE, reserv);
                            db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{idPurse});

                            db.close();
                            finish();
                            break;
                        case R.id.radio_inInner:

                            break;
                        case R.id.radio_outInner:

                            break;
                        case R.id.radio_translation:
                            String NamePurseForTranslation = _spinnerNamePurse.getSelectedItem().toString();

                            break;
                    }
                }
             } catch (Exception e) {e.printStackTrace();}
            }
        });

    }

    View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RadioButton rb = (RadioButton)v;
            switch (rb.getId()) {
                case R.id.radio_inInner:
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

    private void putInBdTableBudget(int type, double summ, String name, String date) {
//        // создаем объект для создания и управления версиями БД
//        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // создаем объект для данных
        ContentValues cv = new ContentValues();

//        // получаем данные из полей ввода
//        String name = etAddNewPurse.getText().toString();

        // подготовим данные для вставки в виде пар: наименование столбца - значение

        cv.put("idPurse", Integer.parseInt(idPurse));
        cv.put("type", type);
        cv.put("summ", summ);
        cv.put("name", name);
        cv.put("date", date);

        Log.d(LOG_TAG, "--- Insert in table budget: ---");
        // вставляем запись и получаем ее ID
        long rowID = db.insert("budget", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        // закрываем подключение к БД
        dbHelper.close();
    }
    private void putInBdTablePurse(double balans, double reserv) {
        // создаем объект для создания и управления версиями БД
        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // подготовим данные для вставки в виде пар: наименование столбца - значение
        cv.put("idPurse", idPurse);
        cv.put("balans", balans);
        cv.put("reserv", reserv);

        Log.d(LOG_TAG, "--- Insert in mytable: ---");
        // вставляем запись и получаем ее ID
//        long rowID = db.insert("purse", null, cv);
//        Log.d(LOG_TAG, "row inserted, ID = " + rowID);

        // закрываем подключение к БД
        dbHelper.close();
    }
}
