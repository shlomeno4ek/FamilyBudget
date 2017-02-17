package ru.shlomeno4ek.familybudget;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

import static ru.shlomeno4ek.familybudget.MainActivity.LOG_TAG;

public class EditPurseActivity extends AppCompatActivity {

    private String idPurse;
    private String namePurse;
    private EditText etNamePurse;
    private Button btnSave;
    private BudgetDbHelper _mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_purse);

        // создаем объект для создания и управления версиями БД
        _mDbHelper = new BudgetDbHelper(this);

        //Получаем параменты, переданные в Intent
        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");
        namePurse = intent.getStringExtra("namePurse");

        //Получаем елементы Activity
        btnSave = (Button) findViewById(R.id.btnSave);
        etNamePurse = (EditText) findViewById(R.id.etNamePurse);
        etNamePurse.setText(namePurse);

        //Добавляем действие при нажатиии на кнопку "Сохраниеть"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Если имя изменили то обновляем запись в базе
                if (!namePurse.equals(etNamePurse.getText())) {

                    // Создадим и откроем для чтения базу данных
                    SQLiteDatabase db = _mDbHelper.getReadableDatabase();

                    // Создайте новую строку со значениями для вставки.
                    ContentValues newValues = new ContentValues();

                    // Задайте значения для каждой строки.
                    newValues.put(FamilyBudget.PurseEntry.COLUMN_NAME, etNamePurse.getText().toString());
                    String where = FamilyBudget.PurseEntry._ID + "=" + idPurse;

                    Log.d(LOG_TAG, "--- Update in purse: ---");
                    // Обновите строку с указанным индексом, используя новые значения и получаем ее ID
                    long rowID = db.update(FamilyBudget.PurseEntry.TABLE_NAME, newValues, where, null);
                    Log.d(LOG_TAG, "row updatinf, ID = " + rowID);
                    finish();
                }
            }
        });

    }
}
