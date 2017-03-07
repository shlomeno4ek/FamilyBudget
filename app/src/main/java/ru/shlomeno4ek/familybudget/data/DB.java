package ru.shlomeno4ek.familybudget.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

    final static String LOG_TAG = "myLogs";

    /**
     * Имя файла базы данных
     */
    private static final String DATABASE_NAME = "budget.db";

    /**
     * Версия базы данных. При изменении схемы увеличить на единицу
     */
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    //Запрос данных из базы
    public Cursor getDB(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        Log.d(LOG_TAG, "Читаем из таблицы - " + tableName + ". Колонки: " + columns);
        return mDB.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //добавление строки в базу
    public long addRec(String tableName, String nullColumnHack, ContentValues values) {
        Log.d(LOG_TAG, "Добавляем в таблицу - " + tableName + ". Значения: " + values.toString());
        return mDB.insert(tableName, nullColumnHack, values);
    }

    //обновление строки в базе
    public long updateRec(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        Log.d(LOG_TAG, "Обновляем таблицу - " + tableName + ". Значения: " + values.toString());
        return mDB.update(tableName, values, whereClause, whereArgs);
    }

    //Получение курсора по запросу в базу
    public Cursor getRawQuery(String sql, String[] selectionArgs) {
        Log.d(LOG_TAG, "Производим запрос в базу - " + sql);
        return mDB.rawQuery(sql, selectionArgs);
    }

    //удаление строки в базе
    public void deleteRec(String tableName, String whereClause, String[] whereArgs) {
        Log.d(LOG_TAG, "Удаляем из таблицы - " + tableName + ". Значения: " + whereClause);
        mDB.delete(tableName, whereClause, whereArgs);
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        /**
         * Конструктор {@link DB}.
         *
         * @param context Контекст приложения
         */
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        /**
         * Вызывается при создании базы данных
         */
        @Override
        public void onCreate(SQLiteDatabase db) {

            // Строка для создания таблицы Purse
            String SQL_CREATE_PURSE_TABLE = "CREATE TABLE " + FamilyBudget.PurseEntry.TABLE_NAME + " ("
                    + FamilyBudget.PurseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FamilyBudget.PurseEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + FamilyBudget.PurseEntry.COLUMN_OWNER + " TEXT NOT NULL, "
                    + FamilyBudget.PurseEntry.COLUMN_BALANS + " REAL NOT NULL DEFAULT 0, "
                    + FamilyBudget.PurseEntry.COLUMN_RESERVE + " REAL NOT NULL DEFAULT 0);";

            // Строка для создания таблицы Budget
            String SQL_CREATE_BUDGET_TABLE = "CREATE TABLE " + FamilyBudget.BudgetEntry.TABLE_NAME + " ("
                    + FamilyBudget.BudgetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FamilyBudget.BudgetEntry.COLUMN_IDPURSE + " INTEGER NOT NULL, "
                    + FamilyBudget.BudgetEntry.COLUMN_TYPE + " INTEGER NOT NULL, "
                    + FamilyBudget.BudgetEntry.COLUMN_SUMM + " REAL NOT NULL, "
                    + FamilyBudget.BudgetEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + FamilyBudget.BudgetEntry.COLUMN_DATE + " TEXT NOT NULL);";

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
}
