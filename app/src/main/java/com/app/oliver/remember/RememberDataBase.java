package com.app.oliver.remember;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RememberDatabase {

    public static final String DATABASE_NAME = "remember.db";

    public static final String ID = "_id";

    public static final String TABLE_MAPS = "maps";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DETAIL = "detail";

    public static final String ID_LIST = "list_id";
    public static final String ID_BOOK = "book_id";

    public static final String TABLE_LISTS = "lists";
    public static final String TABLE_BOOKS = "books";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    private SQLiteDatabase mDatabase;

    public RememberDatabase(SQLiteDatabase db) {
        mDatabase = db;
    }

    public void createTablesIfNotExist() {
        RememberDatabase.createTablesIfNotExist(mDatabase);
    }

    public Cursor selectAllIn(String tableName){
        return RememberDatabase.selectAllIn(tableName, mDatabase);
    }

    public Cursor selectTables(){
        return RememberDatabase.selectTables(mDatabase);
    }

    public Cursor selectByHierarchy(String book, String list){
        int bookId = getBookId(book);

        String sql = "select * from maps where list_id = " +
                "(select _id from lists where " +
                "lists.name = '" + list +"' and " +
                "lists.book_id = "+bookId+") ";

        Log.d("main","In Method selectByHierarchy sql = "+sql);
        return mDatabase.rawQuery(sql, null);
    }

    public int getBookId(String bookName){
        return RememberDatabase.getBookId(mDatabase, bookName);
    }

    public static int importDatabaseFromSD(String databaseName){
        File source = new File(Environment.getExternalStorageDirectory(),databaseName);
        if(!source.exists()) return -1;

        String targetPath = "/data/data/" + RememberDatabase.class.getPackage().getName() + "/databases/";
        File target = new File(targetPath,DATABASE_NAME);

        //noinspection ResultOfMethodCallIgnored
        target.delete();

        try {
            if(!target.createNewFile()) return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileInputStream fin;
        FileOutputStream fout;
        try {
            fin = new FileInputStream(source);
            fout = new FileOutputStream(target, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        }


        byte[] buffer = new byte[400000];
        int count;
        try {
            while((count = fin.read(buffer)) > 0){
                fout.write(buffer, 0, count);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fin.close();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void copy2Database(SQLiteDatabase original, SQLiteDatabase copy, String bookName){
//        TODO Use some method to ensure copy the latest version

        createTablesIfNotExist(copy);
        // FIXME: 2015/10/13 Can't use ? to replace table name words
        int bookId = getBookId(original, bookName);

        Log.d("main","book id is"+bookId );

        Cursor c = original.rawQuery("select * from books where name = '" + bookName + "'", null);
        c.moveToNext();
        String[] book_row= {
                c.getString(c.getColumnIndex(NAME)),
                c.getString(c.getColumnIndex(DESCRIPTION))
        };
        copy.execSQL("insert into books values(NULL,?,?)",book_row);

        Cursor list = original.rawQuery("select * from lists where book_id=" + bookId,null);
        while(list.moveToNext()){
            String[] values = {
                    list.getString(list.getColumnIndex(NAME)),
                    list.getString(list.getColumnIndex(DESCRIPTION)),
                    list.getString(list.getColumnIndex(ID_BOOK))
            };

            copy.execSQL("insert into lists values(NULL,?,?,?)",values);
        }

        Cursor map = original.rawQuery("select * from maps where list_id=" +
                        "(select _id from lists where book_id='"+ bookId +"')",
                null);
        while(map.moveToNext()){
            String[] values = {
                    map.getString(map.getColumnIndex(KEY)),
                    map.getString(map.getColumnIndex(VALUE)),
                    map.getString(map.getColumnIndex(DETAIL)),
                    map.getString(map.getColumnIndex(ID_LIST))
            };

            copy.execSQL("insert into maps values(NULL,?,?,?,?)",values);
        }

        // TODO: 2015/10/12 Add distingct map item to table Maps
    }

    private static int getBookId(SQLiteDatabase original, String bookName) {
        Cursor book = original.rawQuery("select _id from books where name = '" + bookName +"'",null);

//        Cursor book = original.rawQuery("select ? from ? where ? = ?;",
//                new String[]{ID, TABLE_BOOKS, NAME, bookName});
        book.moveToFirst();
        int bookId = book.getInt(book.getColumnIndex(ID));
        book.close();
        return bookId;
    }

    public static void createTablesIfNotExist(SQLiteDatabase db){
        db.execSQL(
                "create table if not exists lists (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(50) NOT NULL," +
                "description TEXT," +
                "book_id INTEGER" +
                ");");
        db.execSQL(
                "create table if not exists books (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(50) NOT NULL," +
                "description TEXT" +
                ");" );
        db.execSQL(
                "create table if not exists maps (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "key TEXT NOT NULL," +
                "value TEXT NOT NULL," +
                "detail TEXT," +
                "list_id INTEGER NOT NULL" +
                ");");
    }

    public static Cursor selectAllIn(String tableName, SQLiteDatabase db) {
        String sql = "select * from" + tableName;

        return  db.rawQuery(sql,null);
    }

    public static Cursor selectTables(SQLiteDatabase db){
        String sql = "select name from sqlite_master where type='table' order by name";

        return db.rawQuery(sql, null);
    }


}
