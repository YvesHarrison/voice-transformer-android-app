package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager
{
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> Constructor");
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add notes
     *
     * @note notes
     */
    public void add(List<note> notes)
    {

        db.beginTransaction(); // 开始事务
        try
        {
            for (note note : notes)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(null, ?, ?, ?)", new Object[] { note.text,
                        note.date, note.pid });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    public void addone(note note)
    {
        if(note.text=="")
            return;
        db.beginTransaction(); // 开始事务
        try
        {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME
                        + " VALUES(null, ?, ?, ?)", new Object[] { note.text,
                        note.date, note.pid });
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    public void addpassage(passage passage)
    {

        db.beginTransaction(); // 开始事务
        try
        {
            db.execSQL("INSERT INTO " + DatabaseHelper.Table_passage
                    + " VALUES(null, ?, ?)", new Object[] {
                    passage.name, passage.date });
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * update person's age
     *
     * @param person
     */
    public void updateAge(Person person)
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> updateAge");
        ContentValues cv = new ContentValues();
        cv.put("age", person.age);
        db.update(DatabaseHelper.TABLE_NAME, cv, "name = ?",
                new String[] { person.name });
    }

    /**
     * delete old person
     *
     * @param tid
     */
    public void deletenote(int tid)
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> deleteOldPerson");
        db.delete(DatabaseHelper.TABLE_NAME, "_id = ?",
                new String[] { String.valueOf(tid) });
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public List<note> query()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<note> notes = new ArrayList<note>();
        Cursor c = queryTheCursor();
        while (c.moveToNext())
        {
            note note = new note();
            note.tid = c.getInt(c.getColumnIndex("_id"));
            note.text = c.getString(c.getColumnIndex("text"));
            note.date = c.getString(c.getColumnIndex("date"));
            note.pid = c.getInt(c.getColumnIndex("pid"));
            notes.add(note);
        }
        c.close();
        return notes;
    }

    /**
     * query all persons, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME,
                null);
        return c;
    }
    private Cursor querycursor()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> queryTheCursor");
        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseHelper.Table_passage,
                null);
        return c;
    }
    public List<passage> querypassage()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> query");
        ArrayList<passage> passages = new ArrayList<passage>();
        Cursor c = querycursor();
        while (c.moveToNext())
        {
            passage passage = new passage();
            passage.pid = c.getInt(c.getColumnIndex("pid"));
            passage.name = c.getString(c.getColumnIndex("name"));
            passage.date = c.getString(c.getColumnIndex("date"));
            passages.add(passage);
        }
        c.close();
        return passages;
    }

    /**
     * close database
     */
    public void closeDB()
    {
//        Log.d(AppConstants.LOG_TAG, "DBManager --> closeDB");
        // 释放数据库资源
        db.close();
    }

}