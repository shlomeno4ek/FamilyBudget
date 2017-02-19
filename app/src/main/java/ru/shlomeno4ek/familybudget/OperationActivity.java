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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
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
    private RadioButton _radio_inInner;
    private RadioButton _radio_outInner;
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

        //Получаем параменты, переданные в Intent
        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");

        //Получаем елементы Activity
        _etSumm = (EditText) findViewById(R.id.etSumm);
        _etName = (EditText) findViewById(R.id.etName);
        _tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _tvNamePurse.setText(intent.getStringExtra("namePurse"));

        //Делаем TextView кликабельной и задаем действие на нее
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

        //Заполняем spinnerNamePurse Названиями кошельков
        getNamePurses();

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

        _radio_inInner = (RadioButton) findViewById(R.id.radio_inInner);
        _radio_inInner.setOnClickListener(radioButtonClickListener);

        _radio_outInner = (RadioButton) findViewById(R.id.radio_outInner);
        _radio_outInner.setOnClickListener(radioButtonClickListener);

        _radio_external = (RadioButton) findViewById(R.id.radio_external);
        _radio_external.setOnClickListener(radioButtonClickListener);

        _radio_translation = (RadioButton) findViewById(R.id.radio_translation);
        _radio_translation.setOnClickListener(radioButtonClickListener);

        _rbIncome = (RadioButton) findViewById(R.id.rbIncome);
        _rbExpenses = (RadioButton) findViewById(R.id.rbExpenses);

        context = OperationActivity.this;

        //Получаем кнопку Сохранить и обрабатываем нажатие
        _btnAccept = (Button) findViewById(R.id.btnAccept);
        _btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             try {

                if (_etSumm.getText().toString()==null || _etName.getText().toString().equals("") || !(_radio_external.isChecked() || _radio_inInner.isChecked() || _radio_translation.isChecked()) ||!(_rbIncome.isChecked() || _rbExpenses.isChecked())) {
                    Toast.makeText(context, "Не все поля заполнены", Toast.LENGTH_LONG).show();
                } else {
                    //Получаем значения из полей Activity
                    double summ = Double.parseDouble(_etSumm.getText().toString());
                    if (summ<0) summ*=-1;
                    String date = _tvInputDate.getText().toString();
                    String name = _etName.getText().toString();

                    //Определяем тип операции + или -
                    int type = 1;
                    if (_rbExpenses.isChecked()) type*=-1;

                    //Получаем id переключателя и проверяем какой выбран и производим соответственное действие
                    int viewOperations = _radio_group_view.getCheckedRadioButtonId();
                    switch (viewOperations) {
                        //Внешняя операция
                        case R.id.radio_external:
                            double summReserve = 0;
                            double balans = 0;
                            double reserv = 0;

                            //Если CHeckBox включен - получаем значение SpinnerProcent
                            if (_checkBoxPutInReserve.isChecked()) {
                                summReserve += summ/100*Integer.parseInt(_spinnerProcentReserve.getSelectedItem().toString());
                            }

                            //Записываем в базу Budget внешнюю операцию
                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_EXTERNAL,summ*type, name, date);

                            //Если был включен резерв, то нужно его тоже внести в операции
                            if (summReserve>0) {
                                putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_INNER,summReserve, "В резерв из: " + name, date);
                            }

                            //Получаем баланс и резерв кошелька
                            String query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_BALANS + ", "
                                    + FamilyBudget.PurseEntry.COLUMN_RESERVE
                                    + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                                    + " WHERE _id = " + idPurse;

                            // подключаемся к БД
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            //Получаем курсор по кошельку где ID = idPurse
                            Cursor cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                balans = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS));
                                reserv = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE));
                                Log.d(LOG_TAG, "--- Query in purse balans and reserv: ---");
                            }

                            cursor2.close();
                            balans += summ*type;
                            reserv += summReserve;

                            //Контейнер для обновленных значений
                            ContentValues values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_BALANS, balans);
                            values.put(FamilyBudget.PurseEntry.COLUMN_RESERVE, reserv);
                            // обновляем запись и получаем ее ID
                            long rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{idPurse});
                            Log.d(LOG_TAG, "--- Update in purse balans and reserv: ID = " + rowID);

                            db.close();
                            finish();
                            break;

                        //Операция перевода в резерв
                        case R.id.radio_inInner:

                            summReserve = 0;
                            reserv = 0;

                            summReserve += summ;

                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_INNER,summReserve, "В резерв " + _tvNamePurse.getText().toString() + ". " + name, date);

                            //Получаем баланс и резерв кошелька
                            query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_RESERVE
                                    + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                                    + " WHERE _id = " + idPurse;

                            // подключаемся к БД
                            db = dbHelper.getWritableDatabase();
                            //Получаем курсор по кошельку где ID = idPurse
                            cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                reserv = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE));
                                Log.d(LOG_TAG, "--- Query in purse reserv: ---");
                            }

                            cursor2.close();

                            reserv += summReserve;

                            //Контейнер для обновленных значений
                            values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_RESERVE, reserv);
                            // обновляем запись и получаем ее ID
                            rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{idPurse});
                            Log.d(LOG_TAG, "--- Update in purse reserv: ID = " + rowID);

                            db.close();
                            finish();
                            break;

                        //Операция перевода из резерва
                        case R.id.radio_outInner:

                            summReserve = 0;
                            reserv = 0;

                            summReserve += summ;

                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_INNER,summReserve, "Из резерва " + _tvNamePurse.getText().toString() + ". " + name, date);

                            //Получаем баланс и резерв кошелька
                            query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_RESERVE
                                    + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                                    + " WHERE _id = " + idPurse;

                            // подключаемся к БД
                            db = dbHelper.getWritableDatabase();
                            //Получаем курсор по кошельку где ID = idPurse
                            cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                reserv = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE));
                                Log.d(LOG_TAG, "--- Query in purse reserv: ---");
                            }

                            cursor2.close();

                            reserv -= summReserve;

                            //Контейнер для обновленных значений
                            values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_RESERVE, reserv);
                            // обновляем запись и получаем ее ID
                            rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{idPurse});
                            Log.d(LOG_TAG, "--- Update in purse reserv: ID = " + rowID);

                            db.close();
                            finish();
                            break;

                        //Операция перевода в другой кошелек
                        case R.id.radio_translation:
                            String namePurseForTranslation = _spinnerNamePurse.getSelectedItem().toString();
                            summReserve = 0;
                            balans = 0;
                            reserv = 0;

                            //Записываем в базу Budget операцию по переводу на другой кошелек
                            //вычитаем из текущего кошелька
                            putInBdTableBudget(FamilyBudget.BudgetEntry.TYPE_TRANSLATION,summ*type, "Перевод в кошелк " + namePurseForTranslation + ". " + name, date);

                            //Получаем баланс кошелька
                            query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_BALANS
                                    + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                                    + " WHERE _id = " + idPurse;

                            // подключаемся к БД
                            db = dbHelper.getWritableDatabase();
                            //Получаем курсор по кошельку где ID = idPurse
                            cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                balans = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS));
                                Log.d(LOG_TAG, "--- Query in purse balans: ---");
                            }

                            cursor2.close();
                            balans -= summ;

                            //Контейнер для обновленных значений
                            values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_BALANS, balans);
                            // обновляем запись и получаем ее ID
                            rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{idPurse});
                            Log.d(LOG_TAG, "--- Update in purse balans and reserv: ID = " + rowID);

                            //Получаем баланс и ID кошелька в который будет перевод
                            query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_BALANS + ", "
                                    + FamilyBudget.PurseEntry._ID
                                    + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                                    + " WHERE name = " + namePurseForTranslation;

                            int idPurseForTranslate=-1;
                            //Получаем курсор по кошельку где name = namePurseForTranslation
                            cursor2 = db.rawQuery(query, null);
                            while (cursor2.moveToNext()) {
                                balans = cursor2.getDouble(cursor2
                                        .getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS));
                                idPurseForTranslate = cursor2.getInt(cursor2.
                                        getColumnIndex(FamilyBudget.PurseEntry._ID));
                                Log.d(LOG_TAG, "--- Query in purse balans: ---");
                            }
                            cursor2.close();
                            balans+=summ;

                            //Контейнер для обновленных значений
                            values = new ContentValues();
                            values.put(FamilyBudget.PurseEntry.COLUMN_BALANS, balans);
                            // обновляем запись и получаем ее ID
                            if (idPurseForTranslate>=0) {
                                rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, values, FamilyBudget.PurseEntry._ID + "= ?", new String[]{""+idPurseForTranslate});
                                Log.d(LOG_TAG, "--- Update in purse balans: ID = " + rowID);
                            }
                            values = new ContentValues();
                            values.put("idPurse", idPurseForTranslate);
                            values.put("type", FamilyBudget.BudgetEntry.TYPE_TRANSLATION);
                            values.put("summ", summ);
                            values.put("name", "Перевод из кошелька " + _tvNamePurse.getText().toString() + ". " + name);
                            values.put("date", date);

                            Log.d(LOG_TAG, "--- Insert in table budget: ---");
                            // вставляем запись и получаем ее ID
                            rowID = db.insert("budget", null, values);
                            Log.d(LOG_TAG, "row inserted, ID = " + rowID);

                            db.close();
                            finish();

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

                //Перевод в резерв
                case R.id.radio_inInner:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutInReserve.setEnabled(false);
                    _checkBoxPutInReserve.setChecked(false);
                    _spinnerProcentReserve.setEnabled(false);
//                    _rbIncome.setChecked(false);
                    _rbExpenses.setChecked(true);
                    _rbIncome.setEnabled(false);
                    _rbExpenses.setEnabled(false);
                    break;

                //Перевод из резерва
                case R.id.radio_outInner:
                    _spinnerNamePurse.setEnabled(false);
                    _checkBoxPutInReserve.setEnabled(false);
                    _checkBoxPutInReserve.setChecked(false);
                    _spinnerProcentReserve.setEnabled(false);
                    _rbIncome.setChecked(true);
//                    _rbExpenses.setChecked(false);
                    _rbIncome.setEnabled(false);
                    _rbExpenses.setEnabled(false);
                    break;

                //Внешняя оперция
                case R.id.radio_external:
                    _spinnerNamePurse.setEnabled(false);
                    if (_rbExpenses.isChecked())_checkBoxPutInReserve.setEnabled(false); else _checkBoxPutInReserve.setEnabled(true);
                    _spinnerProcentReserve.setEnabled(false);
                    _rbIncome.setEnabled(true);
                    _rbExpenses.setEnabled(true);
//                    _radio_group_type.clearCheck();
                    break;

                //Перевод в другой кошелек
                case R.id.radio_translation:
                    _spinnerNamePurse.setEnabled(true);
                    _checkBoxPutInReserve.setEnabled(false);
                    _checkBoxPutInReserve.setChecked(false);
                    _spinnerProcentReserve.setEnabled(false);
//                    _rbIncome.setChecked(false);
                    _rbExpenses.setChecked(true);
                    _rbIncome.setEnabled(false);
                    _rbExpenses.setEnabled(false);
                    break;

                default:
                    break;
            }
        }
    };

    //Диалог для выбора даты
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

    //Метод записывает в базу Budget новые операции
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
    private void getNamePurses() {
        ArrayList<String> namePursesList = new ArrayList<>();

        // создаем объект для создания и управления версиями БД
        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Получаем баланс и резерв кошелька
        String query = "SELECT " + FamilyBudget.PurseEntry.COLUMN_NAME
                + " FROM " + FamilyBudget.PurseEntry.TABLE_NAME
                + " WHERE _id <> " + idPurse;

        //Получаем курсор по кошельку где ID <> idPurse
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            namePursesList.add(cursor2.getString(cursor2.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_NAME)));
            Log.d(LOG_TAG, "--- Query in purse namePurse: ---");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, namePursesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerNamePurse.setAdapter(adapter);

        db.close();

    }
}
