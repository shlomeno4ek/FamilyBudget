package ru.shlomeno4ek.familybudget;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

public class ViewPurseActivity extends AppCompatActivity {

    private TextView tvNamePurse;
    private ListView _lvOperationsPurse;
    private String pursesInfo[];
    private String idPurse;
    private BudgetDbHelper _mDbHelper;
    private ImageButton _imageBtnAddOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_purse);

        _mDbHelper = new BudgetDbHelper(this);
        _imageBtnAddOperation = (ImageButton) findViewById(R.id.imageBtnAddOperation);
        _imageBtnAddOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPurseActivity.this, OperationActivity.class);
                intent.putExtra("id",idPurse);
                intent.putExtra("namePurse",tvNamePurse.getText().toString().split("\n")[0]);
                startActivity(intent);
            }
        });

        tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        _lvOperationsPurse =(ListView) findViewById(R.id.lvOperationsPurse);

        Intent intent = getIntent();
        idPurse = intent.getStringExtra("position");
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] temp = displayDatabaseInfo();
        if (temp != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temp);
            _lvOperationsPurse.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_purse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_del_purse:
                // Создадим и откроем для чтения базу данных
                SQLiteDatabase db = _mDbHelper.getReadableDatabase();
                db.delete(FamilyBudget.PurseEntry.TABLE_NAME, FamilyBudget.PurseEntry._ID + "=" + idPurse, null);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String[] displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = _mDbHelper.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projectionOnPurse = {
                FamilyBudget.PurseEntry._ID,
                FamilyBudget.PurseEntry.COLUMN_NAME,
                FamilyBudget.PurseEntry.COLUMN_OWNER};

        String[] projectionOnBudget = {
                FamilyBudget.BudgetEntry._ID,
                FamilyBudget.BudgetEntry.COLUMN_IDPURSE,
                FamilyBudget.BudgetEntry.COLUMN_TYPE,
                FamilyBudget.BudgetEntry.COLUMN_SUMM,
                FamilyBudget.BudgetEntry.COLUMN_NAME,
                FamilyBudget.BudgetEntry.COLUMN_DATE};

        String selectionPurse = FamilyBudget.PurseEntry._ID + "=?";
        String[] selectionArgsPurse = {idPurse};
        String selectionBudget = FamilyBudget.BudgetEntry.COLUMN_IDPURSE + "=?";
        String[] selectionArgsBudget = {idPurse};

        // Делаем запрос кошелька
        Cursor cursorPurse = db.query(
                FamilyBudget.PurseEntry.TABLE_NAME,     // таблица
                projectionOnPurse,                      // столбцы
                selectionPurse,                                   // столбцы для условия WHERE
                selectionArgsPurse,                                   // значения для условия WHERE
                null,                                   // Don't group the rows
                null,                                   // Don't filter by row groups
                null);                                  // порядок сортировки

        // Делаем запрос баланса
        Cursor cursorBudget = db.query(
                FamilyBudget.BudgetEntry.TABLE_NAME,     // таблица
                projectionOnBudget,                      // столбцы
                selectionBudget,                              // столбцы для условия WHERE
                selectionArgsBudget,                          // значения для условия WHERE
                null,                                   // Don't group the rows
                null,                                   // Don't filter by row groups
                null);                                  // порядок сортировки

        ArrayList<String> allPurses = new ArrayList<>();

        try {
            // Узнаем индекс каждого столбца
            int idColumnIndexPurse = cursorPurse.getColumnIndex(FamilyBudget.PurseEntry._ID);
            int nameColumnIndexPurse = cursorPurse.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_NAME);
            int ownerColumnIndexPurse = cursorPurse.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_OWNER);

            // Узнаем индекс каждого столбца
            int idColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry._ID);
            int idPurseColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_IDPURSE);
            int typeColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_TYPE);
            int summColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_SUMM);
            int nameColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_NAME);
            int dateColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_DATE);

            // Проходим через все ряды таблицы Purse
            while (cursorPurse.moveToNext()) {
                // Используем индекс для получения строки или числа
//                int currentID = cursorPurse.getInt(idColumnIndexPurse);
                String currentName = cursorPurse.getString(nameColumnIndexPurse);
//                String currentOwner = cursorPurse.getString(ownerColumnIndexPurse);

                tvNamePurse.setText(currentName);
            }

            // Проходим через все ряды таблицы Budget
            while (cursorBudget.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursorBudget.getInt(idColumnIndexBudget);
                int currentIdPurse = cursorPurse.getInt(idPurseColumnIndexBudget);
                int currentTYPE = cursorPurse.getInt(typeColumnIndexBudget);
                double currentSUMM = cursorPurse.getDouble(summColumnIndexBudget);
                String currentName = cursorPurse.getString(nameColumnIndexBudget);
                String currentDATE = cursorPurse.getString(dateColumnIndexBudget);
                // Выводим значения каждого столбца
                allPurses.add(currentTYPE == 1 ? "-"+currentSUMM : currentSUMM + " currentName " + currentDATE);
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursorPurse.close();
            cursorBudget.close();
        }




        if (allPurses.size()>0) {
            String pursesInfo[] = new String[allPurses.size()];
            pursesInfo = allPurses.toArray(pursesInfo);
            return pursesInfo;
        } else {
            tvNamePurse.append("\nНи каких движений пока не проводилось, добавте операции для данного кошелька через кнопку");
            String pursesInfo[] = null;
            return pursesInfo;
        }

    }
}
