package ru.shlomeno4ek.familybudget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.shlomeno4ek.familybudget.data.BudgetDbHelper;
import ru.shlomeno4ek.familybudget.data.FamilyBudget;

public class ViewPurseActivity extends AppCompatActivity {

    private TextView tvNamePurse;
    private TextView tvBalansPurse;
    private TextView tvReservPurse;
    private ListView _lvOperationsPurse;
//    private String pursesInfo[];
    private String idPurse;
    private BudgetDbHelper _mDbHelper;
    private ImageButton _imageBtnAddOperation;

    AlertDialog.Builder ad;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_purse);

        //Получаем елементы Activity
        tvNamePurse = (TextView) findViewById(R.id.tvNamePurse);
        tvBalansPurse = (TextView) findViewById(R.id.tvBalansPurse);
        tvReservPurse = (TextView) findViewById(R.id.tvReservPurse);
        _lvOperationsPurse =(ListView) findViewById(R.id.lvOperationsPurse);

        //Получаем параменты, переданные в Intent
        Intent intent = getIntent();
        idPurse = intent.getStringExtra("id");

        // создаем объект для создания и управления версиями БД
        _mDbHelper = new BudgetDbHelper(this);

//        //Получаем кнопку добавления операции и устанавливаем на нее действие
//        _imageBtnAddOperation = (ImageButton) findViewById(R.id.imageBtnAddOperation);
//        _imageBtnAddOperation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ViewPurseActivity.this, OperationActivity.class);
//                //Передаем в интент id  и namePurse для OperationActivity
//                intent.putExtra("id",idPurse);
//                intent.putExtra("namePurse",tvNamePurse.getText().toString().split("\n")[0]);
//                startActivity(intent);
//            }
//        });

        //Подтверждение удаления кошелька
        context = ViewPurseActivity.this;
        String title = "Удаление кошелька";
        String message = "Вы действительно хотите удалить кошелек и все операции в нем?";
        String button1String = "Да";
        String button2String = "Нет";

        //Создание диалога для подтверждения
        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение

        //Действия при нажатии подтверждения диалога
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Кошелек удален",
                        Toast.LENGTH_LONG).show();

                // Создадим и откроем для чтения базу данных
                SQLiteDatabase db = _mDbHelper.getReadableDatabase();

                //Удаляем кошелек и все его операции
                db.delete(FamilyBudget.PurseEntry.TABLE_NAME, FamilyBudget.PurseEntry._ID + "=" + idPurse, null);
                db.delete(FamilyBudget.BudgetEntry.TABLE_NAME, FamilyBudget.BudgetEntry.COLUMN_IDPURSE + "=" + idPurse, null);

                //Закрываем подключение к базе
                db.close();
                finish();
            }
        });

        //Действия при нажатии отмены диалога
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Кошелек не удален", Toast.LENGTH_LONG).show();
            }
        });

        //Действия при нажатии назад
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Кошелек не удален", Toast.LENGTH_LONG).show();
            }
        });
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
            //Выбран пункт меню "Удаление кошелька"
            case R.id.action_del_purse:
                ad.show();
                return true;

            //Выбран пункт меню "Редактирование кошелька"
            case R.id.action_edit_purse:
                Intent intent = new Intent(ViewPurseActivity.this, EditPurseActivity.class);
                intent.putExtra("id",idPurse);
                intent.putExtra("namePurse",tvNamePurse.getText().toString().split("\n")[0]);
                startActivity(intent);
                return true;

            //Выбран пункт меню "Добавление операции"
            case R.id.action_add_operations:
                Intent intent1 = new Intent(ViewPurseActivity.this, OperationActivity.class);
                //Передаем в интент id  и namePurse для OperationActivity
                intent1.putExtra("id",idPurse);
                intent1.putExtra("namePurse",tvNamePurse.getText().toString().split("\n")[0]);
                startActivity(intent1);
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
            int balansColumnIndexPurse = cursorPurse.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_BALANS);
            int reservColumnIndexPurse = cursorPurse.getColumnIndex(FamilyBudget.PurseEntry.COLUMN_RESERVE);

            // Узнаем индекс каждого столбца
            int idColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry._ID);
            int idPurseColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_IDPURSE);
            int typeColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_TYPE);
            int summColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_SUMM);
            int nameColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_NAME);
            int dateColumnIndexBudget = cursorBudget.getColumnIndex(FamilyBudget.BudgetEntry.COLUMN_DATE);

            // Проходим через все ряды таблицы purse
            while (cursorPurse != null && cursorPurse.moveToNext()) {
                // Используем индекс для получения строки или числа
//                int currentID = cursorPurse.getInt(idColumnIndexPurse);
                String currentName = cursorPurse.getString(nameColumnIndexPurse);
//                String currentOwner = cursorPurse.getString(ownerColumnIndexPurse);
                Double currentBalans = cursorPurse.getDouble(balansColumnIndexPurse);
                Double currentReserv = cursorPurse.getDouble(reservColumnIndexPurse);

                tvNamePurse.setText(currentName);
                tvBalansPurse.setText("Баланс: " + currentBalans);
                tvReservPurse.setText("Из них в резерве: " + currentReserv);
            }

            // Проходим через все ряды таблицы budget
            while (cursorPurse != null && cursorBudget.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursorBudget.getInt(idColumnIndexBudget);
                int currentIdPurse = cursorBudget.getInt(idPurseColumnIndexBudget);
                int currentTYPE = cursorBudget.getInt(typeColumnIndexBudget);
                double currentSUMM = cursorBudget.getDouble(summColumnIndexBudget);
                String currentName = cursorBudget.getString(nameColumnIndexBudget);
                String currentDATE = cursorBudget.getString(dateColumnIndexBudget);
                // Выводим значения каждого столбца
                allPurses.add(currentSUMM + " " +currentName+" " + currentDATE);
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
            tvReservPurse.append("\n\nНи каких движений пока не проводилось, добавте операции для данного кошелька через кнопку");
            String pursesInfo[] = null;
            return pursesInfo;
        }

    }
}
