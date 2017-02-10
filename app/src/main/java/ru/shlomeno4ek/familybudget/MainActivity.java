package ru.shlomeno4ek.familybudget;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

public class MainActivity extends AppCompatActivity {

    final static String LOG_TAG = "myLogs";

    private BudgetDbHelper _mDbHelper;
    private ListView _lvMain;
    private TextView _tvMainActBalansAll;
    private ArrayList<Integer> _idAndNamePurses;


    @Override
    protected void onResume() {
        super.onResume();

        String[] temp = displayDatabaseInfo();
        if (temp != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayDatabaseInfo());
            _lvMain.setAdapter(adapter);
        } else _lvMain.setAdapter(null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _mDbHelper = new BudgetDbHelper(this);

        _tvMainActBalansAll = (TextView) findViewById(R.id.tvMainActBalansAll);
        _lvMain = (ListView) findViewById(R.id.lvMain);

        _lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ViewPurseActivity.class);

                intent.putExtra("position",""+_idAndNamePurses.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_add_purse:
                Intent intent = new Intent(MainActivity.this, AddPurseActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String[] displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = _mDbHelper.getReadableDatabase();
        //Массив с названиями для ListView
        String pursesInfo[];

        // Зададим условие для выборки - список столбцов
        String[] projectionOnPurse = {
                FamilyBudget.PurseEntry._ID,
                FamilyBudget.PurseEntry.COLUMN_NAME,
                FamilyBudget.PurseEntry.COLUMN_OWNER,
                FamilyBudget.PurseEntry.COLUMN_BALANS,
                FamilyBudget.PurseEntry.COLUMN_RESERVE};

        String[] projectionOnBudget = {
                FamilyBudget.BudgetEntry._ID,
                FamilyBudget.BudgetEntry.COLUMN_IDPURSE,
                FamilyBudget.BudgetEntry.COLUMN_TYPE,
                FamilyBudget.BudgetEntry.COLUMN_SUMM,
                FamilyBudget.BudgetEntry.COLUMN_NAME,
                FamilyBudget.BudgetEntry.COLUMN_DATE};

        // Делаем запрос
        Cursor cursor = db.query(
                FamilyBudget.PurseEntry.TABLE_NAME,     // таблица
                projectionOnPurse,                      // столбцы
                null,                                   // столбцы для условия WHERE
                null,                                   // значения для условия WHERE
                null,                                   // Don't group the rows
                null,                                   // Don't filter by row groups
                null);                                  // порядок сортировки

        ArrayList<String> allPurses = new ArrayList<>();

        _idAndNamePurses = new ArrayList<Integer>();

        try {
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(FamilyBudget.PurseEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_NAME);
            int ownerColumnIndex = cursor.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_OWNER);
            int balansColumnIndex = cursor.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS);
            int reserveColumnIndex = cursor.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentOwner = cursor.getString(ownerColumnIndex);
                Double currentBalans = cursor.getDouble(balansColumnIndex);
                Double currentReserve = cursor.getDouble(reserveColumnIndex);

                // Выводим значения каждого столбца
                allPurses.add(currentName + "\nБаланс: " + currentBalans + ", Резерв: " + currentReserve);

                //Добавляем в map пару ID - NAME
                _idAndNamePurses.add(currentID);

            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }

        if (allPurses.size()>0) {
            pursesInfo = new String[allPurses.size()];
            pursesInfo = allPurses.toArray(pursesInfo);
            _tvMainActBalansAll.setText("");
        } else {
            _tvMainActBalansAll.setText("У вас пока не создано ни одного кошелька, дабавьте его через пункт меню");
            pursesInfo = null;
        }

        return pursesInfo;
    }
}
