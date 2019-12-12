package com.Repo.vpdntestapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.Entity.vpdntestapp.model.TestIp;
import com.SQLite.vpdntestapp.DBHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author panqi
 */
public class TestIpRepo {

  private DBHelper dbHelper;

  public TestIpRepo(Context context) {
    dbHelper = new DBHelper(context);
  }

  public int insert(TestIp testIp) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(TestIp.KEY_IPADDRESS, testIp.getIpAddress());

    long ipId = db.insert(TestIp.TABLE, null, values);
    db.close();
    return (int) ipId;
  }

  public void deleteById(int id) {
    TestIp testIp = new TestIp(id);
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.delete(TestIp.TABLE, TestIp.KEY_ID + "=?", new String[]{String.valueOf(testIp.getId())});
    db.close();
  }

  public List<HashMap<String, String>> getIps() {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    String selectQuery = "SELECT " +
      TestIp.KEY_ID + "," +
      TestIp.KEY_IPADDRESS + " FROM " + TestIp.TABLE;
    List<HashMap<String, String>> ips = new ArrayList<HashMap<String, String>>();

    Cursor cursor = db.rawQuery(selectQuery, null);

    if (cursor.moveToFirst()) {
      do {
        HashMap<String, String> testIp = new HashMap<String, String>();
        testIp.put("id", cursor.getString(cursor.getColumnIndex(TestIp.KEY_ID)));
        testIp.put("ipAddress", cursor.getString(cursor.getColumnIndex(TestIp.KEY_IPADDRESS)));
        ips.add(testIp);
      } while (cursor.moveToNext());
    }
    cursor.close();
    db.close();
    return ips;

  }

}
