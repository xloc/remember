package com.app.oliver.remember;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ShowMapActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private ListView mListView;
    private WordCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        initDatabase();

        mListView = (ListView) findViewById(R.id.lvShow);

        initContent();
    }

    private void initContent() {
        RememberDatabase db = new RememberDatabase(mDatabase);
        Cursor c = db.selectByHierarchy("words","Test Wordlist");
        mCursorAdapter = new WordCursorAdapter(this,c,false);
        Log.d("main", "Value of mCursorAdapter" + mCursorAdapter);
        mListView.setAdapter(mCursorAdapter);
    }

    private void initDatabase() {
        mDatabase = openOrCreateDatabase(RememberDatabase.DATABASE_NAME, MODE_PRIVATE,null);
    }

    class WordCursorAdapter extends CursorAdapter {
        public WordCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return View.inflate(context,R.layout.map_item,null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvKey = (TextView)view.findViewById(R.id.tvKey);
            tvKey.setText(cursor.getString(cursor.getColumnIndex(RememberDatabase.KEY)));
            TextView tvValue = (TextView) view.findViewById(R.id.tvValue);
            tvValue.setText(cursor.getString(cursor.getColumnIndex(RememberDatabase.VALUE)));
        }
    }
}