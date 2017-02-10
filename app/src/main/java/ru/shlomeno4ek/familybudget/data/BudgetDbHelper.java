package ru.shlomeno4ek.familybudget.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by шлома-сн on 08.02.2017.
 */

public class BudgetDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BudgetDbHelper.class.getSimpleName();

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "budget.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Конструктор {@link BudgetDbHelper}.
     *
     * @param context Контекст приложения
     */
    public BudgetDbHelper(Context context) {
        // конструктор суперкласса
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Строка для создания таблицы
        String SQL_CREATE_PURSE_TABLE = "CREATE TABLE " + FamilyBudget.PurseEntry.TABLE_NAME + " ("
                + FamilyBudget.PurseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FamilyBudget.PurseEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + FamilyBudget.PurseEntry.COLUMN_OWNER  + " TEXT NOT NULL);";

        String SQL_CREATE_BUDGET_TABLE = "CREATE TABLE " + FamilyBudget.BudgetEntry.TABLE_NAME + " ("
                + FamilyBudget.BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FamilyBudget.BudgetEntry.COLUMN_IDPURSE + " INTEGER NOT NULL, "
                + FamilyBudget.BudgetEntry.COLUMN_TYPE + " INTEGER NOT NULL, "
                + FamilyBudget.BudgetEntry.COLUMN_SUMM + " REAL NOT NULL, "
                + FamilyBudget.BudgetEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + FamilyBudget.BudgetEntry.COLUMN_DATE  + " TEXT NOT NULL);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_PURSE_TABLE);
        db.execSQL(SQL_CREATE_BUDGET_TABLE);
    }

    /**
     * Вызывается при обновлении схемы базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + FamilyBudget.PurseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF IT EXISTS " + FamilyBudget.BudgetEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);
    }
}
