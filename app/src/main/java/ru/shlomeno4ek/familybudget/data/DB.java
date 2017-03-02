package ru.shlomeno4ek.familybudget.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by шлома-сн on 08.02.2017.
 */

public class DB {

    public static final String LOG_TAG = DB.class.getSimpleName();

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

//    // получить все данные из таблицы DB_TABLE
//    public Cursor getAllData() {
//        return mDB.query(DATABASE_NAME, null, null, null, null, null, null);
//    }

    public Cursor getDB(String tableName, String[] projectionOnPurse, String o, String[] o1, String o2, String o3, String o4) {
        return mDB.query(tableName, projectionOnPurse, o, o1, o2, o3, o4);
    }

    public long addRec(String purse, String o, ContentValues cv) {
        return mDB.insert(purse, o, cv);
    }

    public long updateRec(String tableName, ContentValues newValues, String where, String[] o) {
        return mDB.update(tableName,newValues,where,o);
    }

    public Cursor getRawQuery(String query, String[] o) {
        return mDB.rawQuery(query,o);
    }

    public void deleteRec(String tableName, String s, String[] o) {
        mDB.delete(tableName,s,o);
    }

    public Cursor getAllData(String tableName) {
        return mDB.query(tableName, null, null, null, null, null, null);
    }

    //    // добавить запись в DB_TABLE
//    public void addRec(String txt, int img) {
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN_TXT, txt);
//        cv.put(COLUMN_IMG, img);
//        mDB.insert(DB_TABLE, null, cv);
//    }
//
//    // удалить запись из DB_TABLE
//    public void delRec(long id) {
//        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
//    }
//
//
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
