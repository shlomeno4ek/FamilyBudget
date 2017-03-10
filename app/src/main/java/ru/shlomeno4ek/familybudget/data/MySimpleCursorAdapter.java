package ru.shlomeno4ek.familybudget.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;


public class MySimpleCursorAdapter extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter adapter;
    private DB _mDbHelper;

    public SimpleCursorAdapter getSimpleCursorAdapter() {
        return adapter;
    }

    public MySimpleCursorAdapter() {
        _mDbHelper = new DB(this);
        _mDbHelper.open();
    }

    public void createSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {




        adapter = new SimpleCursorAdapter(context, layout, null, from, to, 0);

    }

        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, _mDbHelper);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
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
            Cursor cursor = db.getDB(FamilyBudget.PurseEntry.TABLE_NAME, null, null, null, null, null, null);
//            try {
//                TimeUnit.SECONDS.sleep(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return cursor;
        }

    }
}
