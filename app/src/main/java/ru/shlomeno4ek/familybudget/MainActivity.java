package ru.shlomeno4ek.familybudget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import ru.shlomeno4ek.familybudget.data.DB;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final static String LOG_TAG = "myLogs";

    private DB _mDbHelper;
    private ListView _lvMain;
    private TextView _tvMainActBalansAll;
    private TextView _tvMainActReserveAll;
    private ArrayList<Integer> _idAndNamePurses;
    private HashMap<Integer, Integer> idAndNamePurse;
    private double balansAll;
    private double reserveAll;
    private SimpleCursorAdapter scAdapter;


    @Override
    protected void onResume() {
        super.onResume();

        //Получаем массив из Нзавания кошелька и его баланса
//        String[] temp = displayDatabaseInfo();
//        //Если массив не NULL то создаем и подключаем адптер для ListView, иначе очищаем ListView
//        if (temp != null) {
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayDatabaseInfo());
//            _lvMain.setAdapter(adapter);
//        } else _lvMain.setAdapter(null);

        // получаем новый курсор с данными
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        _mDbHelper.close();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // создаем объект для создания и управления версиями БД
        _mDbHelper = new DB(this);
        _mDbHelper.open();

        //Получаем елементы Activity
        _tvMainActBalansAll = (TextView) findViewById(R.id.tvMainActBalansAll);
        _tvMainActReserveAll = (TextView) findViewById(R.id.tvMainActReserveAll);
//        _lvMain = (ListView) findViewById(R.id.lvMain);

        //Добавляем действие на щелчок по элементу ListView
//        _lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, ViewPurseActivity.class);
//                Integer a = idAndNamePurse.get(position);
//                //Передаем в интент id для ViewPurseActivity
//                intent.putExtra("id",""+a);
////                intent.putExtra("id",""+_idAndNamePurses.get(position));
//                startActivity(intent);
//            }
//        });

        //SimpleCursoreAdapter
        // формируем столбцы сопоставления
        String[] from = new String[] {  FamilyBudget.PurseEntry.COLUMN_NAME,
                FamilyBudget.PurseEntry.COLUMN_BALANS,
                FamilyBudget.PurseEntry.COLUMN_RESERVE };
        int[] to = new int[] { R.id.tvNamePurseForLv, R.id.tvBalansPurseForLv,R.id.tvReservPurseForLv};

        // создаем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        _lvMain = (ListView) findViewById(R.id.lvMain);
        _lvMain.setAdapter(scAdapter);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);

    }

    //Создание меню для Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Действия для каждого из пункта меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();

        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.action_add_purse:
                //Если выбрано добавление кошелька
                Intent intent = new Intent(MainActivity.this, AddPurseActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Получаем информацию о всех кошельках и возвращаем в виде String[]
    private String[] displayDatabaseInfo() {
        balansAll = 0;
        reserveAll = 0;
        idAndNamePurse = new HashMap<Integer, Integer>();
        int i = 0;
//        // Создадим и откроем для чтения базу данных
//        SQLiteDatabase db = _mDbHelper.getReadableDatabase();
        //Массив с названиями для ListView
        String pursesInfo[];

        // Зададим условие для выборки - список столбцов
        String[] projectionOnPurse = {
                FamilyBudget.PurseEntry._ID,
                FamilyBudget.PurseEntry.COLUMN_NAME,
                FamilyBudget.PurseEntry.COLUMN_OWNER,
                FamilyBudget.PurseEntry.COLUMN_BALANS,
                FamilyBudget.PurseEntry.COLUMN_RESERVE};

//        String[] projectionOnBudget = {
//                FamilyBudget.BudgetEntry._ID,
//                FamilyBudget.BudgetEntry.COLUMN_IDPURSE,
//                FamilyBudget.BudgetEntry.COLUMN_TYPE,
//                FamilyBudget.BudgetEntry.COLUMN_SUMM,
//                FamilyBudget.BudgetEntry.COLUMN_NAME,
//                FamilyBudget.BudgetEntry.COLUMN_DATE};

        // Делаем запрос
        Cursor cursor = _mDbHelper.getDB(
                FamilyBudget.PurseEntry.TABLE_NAME,     // таблица
                projectionOnPurse,                      // столбцы
                null,                                   // столбцы для условия WHERE
                null,                                   // значения для условия WHERE
                null,                                   // Don't group the rows
                null,                                   // Don't filter by row groups
                null);                                  // порядок сортировки

        ArrayList<String> allPurses = new ArrayList<>();

//        _idAndNamePurses = new ArrayList<Integer>();

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
                allPurses.add(currentName + "\nБаланс: " + currentBalans + "\nНа трату:  " + (currentBalans-currentReserve) + "\nРезерв: " + currentReserve);

                balansAll+=currentBalans;
                reserveAll+=currentReserve;

                //Добавляем в map пару i - ID
//                _idAndNamePurses.add(currentID);
                idAndNamePurse.put(i,currentID);
                Log.d(LOG_TAG, "MainActivity читаем из базы id = " + currentID + " имя: " + currentName + " и вставляем в HashMap с ключом: " + i);
                i++;
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
        //Проверка на количество кошельков, если их не то возвращаем NULL
        if (allPurses.size()>0) {
            pursesInfo = new String[allPurses.size()];
            pursesInfo = allPurses.toArray(pursesInfo);
            _tvMainActBalansAll.setText("Общий баланс: "+balansAll+" руб.");
            _tvMainActReserveAll.setText("В резерве: "+reserveAll+" руб.\nНа трату: " + (balansAll-reserveAll) + " руб.");
        } else {
            _tvMainActBalansAll.setText("У вас пока не создано ни одного кошелька, дабавьте его через пункт меню");
            _tvMainActReserveAll.setText("");
            pursesInfo = null;
        }

        return pursesInfo;
    }
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        _mDbHelper.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, _mDbHelper);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData(FamilyBudget.PurseEntry.TABLE_NAME);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cursor;
        }

    }
}
