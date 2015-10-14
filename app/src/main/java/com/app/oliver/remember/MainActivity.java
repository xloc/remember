package com.app.oliver.remember;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btClone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RememberDatabase.importDatabaseFromSD("remember.db");

            }
        });

        findViewById(R.id.btShow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, ShowMapActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        findViewById(R.id.btCreate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("main","Create table button pressed");
                SQLiteDatabase db = openOrCreateDatabase(RememberDatabase.DATABASE_NAME, MODE_PRIVATE, null);

                RememberDatabase.createTablesIfNotExist(db);
                TextView tvInf = (TextView) MainActivity.this.findViewById(R.id.tvInf);

                Cursor c = RememberDatabase.selectTables(db);

                StringBuffer inf = new StringBuffer();
                while(c.moveToNext()){
                     inf.append(c.getString(c.getColumnIndex("name"))+"\n" );
                }

                tvInf.setText(inf);
            }
        });
    }
}
