package ru.shlomeno4ek.familybudget;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;

import static ru.shlomeno4ek.familybudget.MainActivity.LOG_TAG;

public class AddPurseActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnOkAddNewPurse;
    private EditText etAddNewPurse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purse);

        //Получаем елементы Activity и устанавливаем кнопке слушателя
        btnOkAddNewPurse = (Button) findViewById(R.id.btnOkAddNewPurse);
        btnOkAddNewPurse.setOnClickListener(this);

        etAddNewPurse = (EditText) findViewById(R.id.etAddNewPurse);

    }


    @Override
    public void onClick(View v) {
        // создаем объект для создания и управления версиями БД
        BudgetDbHelper dbHelper = new BudgetDbHelper(this);

        // создаем объект для данных
        ContentValues cv = new ContentValues();

        // получаем данные из полей ввода
        String name = etAddNewPurse.getText().toString();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();

       // подготовим данные для вставки в виде пар: наименование столбца - значение
        if(!name.equals("")){

        cv.put("name", name);
        cv.put("owner", "Owner");
        cv.put("balans", 0);
        cv.put("reserve", 0);

        Log.d(LOG_TAG, "--- Insert in purse: ---");
        // вставляем запись и получаем ее ID
        long rowID = db.insert("purse", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);
        }

        // закрываем подключение к БД
        dbHelper.close();
        //закрываем Activity
        finish();
    }
}
