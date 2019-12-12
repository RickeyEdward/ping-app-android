package com.SQLite.vpdntestapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.Entity.vpdntestapp.model.TestIp;
import com.Entity.vpdntestapp.model.TestPara;

/**
 * @author panqi
 */
public class DBHelper extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 3;
  private static final String DATABASE_NAME = "VPDNTest.db";

  public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
    int version) {
    super(context, name, factory, version);
  }

  public DBHelper(Context context) {
    this(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  /**
   * 创建表
   */
  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    String CREATE_TABLE_TESTPARA = "CREATE TABLE " + TestPara.TABLE + "("
      + TestPara.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
      + TestPara.KEY_INTERVAL + " DOUBLE, "
      + TestPara.KEY_PACKETCOUNT + " INTEGER, "
      + TestPara.KEY_TESTTIME + " INTEGER,"
      + TestPara.KEY_WAITTIME + " INTEGER)";

    sqLiteDatabase.execSQL(CREATE_TABLE_TESTPARA);

    String CREATE_TABLE_TESTIP = "CREATE TABLE " + TestIp.TABLE + "("
      + TestIp.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
      + TestIp.KEY_IPADDRESS + " TEXT)";

    sqLiteDatabase.execSQL(CREATE_TABLE_TESTIP);
  }

  /**
   * 数据库升级更新时调用
   */
  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TestPara.TABLE);

    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TestIp.TABLE);

    onCreate(sqLiteDatabase);

  }
}
