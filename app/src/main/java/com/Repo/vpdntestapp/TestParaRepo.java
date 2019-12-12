package com.Repo.vpdntestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Entity.vpdntestapp.model.TestPara;
import com.SQLite.vpdntestapp.DBHelper;

/**
 * @author panqi
 */
public class TestParaRepo {

  private DBHelper dbHelper;

  public TestParaRepo(Context context) {
    dbHelper = new DBHelper(context);
  }

  public int insert(TestPara testPara) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(TestPara.KEY_INTERVAL, testPara.getInterval());
    values.put(TestPara.KEY_PACKETCOUNT, testPara.getPacketCount());
    values.put(TestPara.KEY_TESTTIME, testPara.getTestTime());
    values.put(TestPara.KEY_WAITTIME, testPara.getWaitTime());

    long paraId = db.insert(TestPara.TABLE, null, values);
    db.close();
    return (int) paraId;
  }

  public void update(TestPara testPara) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(TestPara.KEY_INTERVAL, testPara.getInterval());
    values.put(TestPara.KEY_PACKETCOUNT, testPara.getPacketCount());
    values.put(TestPara.KEY_TESTTIME, testPara.getTestTime());
    values.put(TestPara.KEY_WAITTIME, testPara.getWaitTime());

    db.update(TestPara.TABLE, values, TestPara.KEY_ID + "=?",
      new String[]{String.valueOf(testPara.getId())});
    db.close();

  }

  public TestPara getParaById(int id) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    String selectQuery = "SELECT " +
      TestPara.KEY_ID + "," +
      TestPara.KEY_PACKETCOUNT + "," +
      TestPara.KEY_INTERVAL + "," +
      TestPara.KEY_WAITTIME + "," +
      TestPara.KEY_TESTTIME + " FROM " + TestPara.TABLE
      + " WHERE " + TestPara.KEY_ID + "=?";
    TestPara testPara = new TestPara();
    Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});

    if (cursor.moveToNext()) {
      do {
        testPara.setId(cursor.getInt(cursor.getColumnIndex(TestPara.KEY_ID)));
        testPara.setInterval(cursor.getDouble(cursor.getColumnIndex(TestPara.KEY_INTERVAL)));
        testPara.setPacketCount(cursor.getInt(cursor.getColumnIndex(TestPara.KEY_PACKETCOUNT)));
        testPara.setTestTime(cursor.getInt(cursor.getColumnIndex(TestPara.KEY_TESTTIME)));
        testPara.setWaitTime(cursor.getInt(cursor.getColumnIndex(TestPara.KEY_WAITTIME)));

      } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();
    return testPara;
  }
}
