package com.example.vpdntestapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.Entity.vpdntestapp.model.TestPara;
import com.Entity.vpdntestapp.vo.TestResultVO;
import com.Repo.vpdntestapp.TestParaRepo;
import com.Utils.vpdntestapp.EmailUtils;
import com.Utils.vpdntestapp.POIUtils;
import com.Utils.vpdntestapp.PingUtils;
import com.Utils.vpdntestapp.TimeUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author panqi
 */
public class MainActivity extends Activity implements View.OnClickListener {

  private Button setting;
  private Button sendEmail;
  private Button chooseFile;
  private Button startPing;
  private Button saveTxt;
  private Button saveExcel;

  private EditText senderText;
  private EditText addresseeText;
  private EditText passwordText;

  private TextView filePathText;
  private TextView pingMsgText;

  private TableLayout resultTable;

  private StringBuffer pingMsg = new StringBuffer();
  private String path;
  private List<TestResultVO> resultList = new ArrayList<>();

  private TestParaRepo testParaRepo;

  private static final String ROOT_PATH = "/storage/emulated/0/DCIM/";
  private static final String BLANK = "  ";


  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static String[] PERMISSIONS_STORAGE = {
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE};

  private void init() {
    setting = findViewById(R.id.setting);
    sendEmail = findViewById(R.id.sendEmail);
    senderText = findViewById(R.id.sender);
    addresseeText = findViewById(R.id.addressee);
    passwordText = findViewById(R.id.password);
    filePathText = findViewById(R.id.filePath);
    pingMsgText = findViewById(R.id.pingMsg);
    chooseFile = findViewById(R.id.chooseFile);
    startPing = findViewById(R.id.start);
    saveTxt = findViewById(R.id.saveTxt);
    saveExcel = findViewById(R.id.saveExcel);
    resultTable = findViewById(R.id.resultTable);

    pingMsgText.setMovementMethod(new ScrollingMovementMethod());

    setting.setOnClickListener(this);
    sendEmail.setOnClickListener(this);
    chooseFile.setOnClickListener(this);
    startPing.setOnClickListener(this);
    saveTxt.setOnClickListener(this);
    saveExcel.setOnClickListener(this);

    verifyStoragePermissions(this);
  }


  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();


  }

  @Override
  public void onClick(View view) {
    testParaRepo = new TestParaRepo(this);
    switch (view.getId()) {
      case R.id.setting:
        Intent toSetting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(toSetting);
        break;
      case R.id.sendEmail:
        if (!isInputValid(this)) {
          break;
        }
        new Thread(sendEmailTask).start();
        break;
      case R.id.chooseFile:
        Intent toFileManager = new Intent(Intent.ACTION_GET_CONTENT);
        toFileManager.setType("*/*");
        toFileManager.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(toFileManager, 1);
        break;
      case R.id.start:
        List<String> ipList = getIntent().getStringArrayListExtra("ips");
        TestPara testPara = testParaRepo.getParaById(1);
        if (ipList == null || ipList.isEmpty()) {
          makeToast(this, "请选择目标地址");
          break;
        }
        for (String hostName : ipList) {
          for (int i = 0; i < testPara.getTestTime(); i++) {
            PingUtils.ping(hostName, testPara, pingMsg, i + 1, resultList);
            pingMsg.append("\n");//每次ping信息之间隔一行
          }
        }
        pingMsgText.setText(pingMsg.toString());
        outPutResult(resultList);
        break;
      case R.id.saveTxt:
        if (pingMsg.length() == 0) {
          makeToast(this, "导出内容为空");
          break;
        }
        try {
          String fileName = "pingInfo" + File.separator + TimeUtils.getDate() + "ping信息.txt";
          File file = createFile(fileName);
          FileWriter fileWriter = new FileWriter(file);
          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
          bufferedWriter.write(String.valueOf(pingMsg));
          bufferedWriter.flush();
          bufferedWriter.close();
          pingMsg.setLength(0);
          makeToast(this, "导出成功");
        } catch (IOException e) {
          e.printStackTrace();
          makeToast(this, "导出失败，请重试");
        }
        break;
      case R.id.saveExcel:
        if (resultList.isEmpty()) {
          makeToast(this, "导出内容为空");
          break;
        }
        try {
          exportResultExcel(resultList);
          resultList.clear();
          makeToast(this, "导出成功");
        } catch (Exception e) {
          e.printStackTrace();
          makeToast(this, "导出失败，请重试");
        }
        break;
      default:
        break;
    }

  }

  Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      Bundle data = msg.getData();
      String val = data.getString("value");
      Log.i("mainLog", "请求结果为-->" + val);
      makeToast(MainActivity.this, val);
      clearEmailTxt();
      // TODO
      // UI界面的更新等相关操作
    }

  };
  /**
   * 发送邮件子线程
   */
  Runnable sendEmailTask = new Runnable() {
    @Override
    public void run() {
      String mailResult = "";
      verifyStoragePermissions(MainActivity.this);
      Message msg = new Message();
      Bundle data = new Bundle();

      String userName = String.valueOf(senderText.getText());
      String password = String.valueOf(passwordText.getText());
      String addressee = String.valueOf(addresseeText.getText());
      String filePath = String.valueOf(filePathText.getText());
      File file = new File(filePath);

      try {
        EmailUtils.sendEmailWithFile(userName, password, addressee, userName, file);
        mailResult = "发送成功";
        data.putString("value", mailResult);
        msg.setData(data);
        handler.sendMessage(msg);
      } catch (Exception e) {
        mailResult = "发送失败,请重启app";
        e.printStackTrace();
        data.putString("value", mailResult);
        msg.setData(data);
        handler.sendMessage(msg);
      }


    }
  };

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
      Uri uri = data.getData();
      if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
        path = uri.getPath();
        filePathText.setText(path);
        Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
        return;
      }
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
        path = getPath(this, uri);
        filePathText.setText(path);
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
      } else {//4.4以下下系统调用方法
        path = getRealPathFromURI(uri);
        filePathText.setText(path);
        Toast.makeText(MainActivity.this, path + "222222", Toast.LENGTH_SHORT).show();
      }
    }
  }


  /**
   * 弹出消息提示框
   *
   * @param context 上下文
   * @param message 提示消息
   */
  private void makeToast(Context context, String message) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }

  private void clearEmailTxt() {
    filePathText.setText("");
    senderText.setText("");
    addresseeText.setText("");
    passwordText.setText("");
  }

  private boolean isInputValid(Context context) {
    String userName = String.valueOf(senderText.getText());
    if (isBlank(userName)) {
      makeToast(context, "用户名不能为空！");
      return false;
    }
    String password = String.valueOf(passwordText.getText());
    if (isBlank(password)) {
      makeToast(context, "密码不能为空！");
      return false;
    }
    String addressee = String.valueOf(addresseeText.getText());
    if (isBlank(addressee)) {
      makeToast(context, "收件人不能为空！");
      return false;
    }
    String filePath = String.valueOf(filePathText.getText());
    if (isBlank(filePath)) {
      makeToast(context, "请选择文件");
      return false;
    }
    return true;
  }

  private File createFile(String fileName) throws IOException {
    File file = new File(ROOT_PATH + fileName);
    if (file.exists()) {
      return null;
    }
    if (!file.getParentFile().exists()) {
      if (!file.getParentFile().mkdirs()) {
        return null;
      }
    }
    if (!file.createNewFile()) {
      return null;
    }
    file.setReadable(true);
    file.setWritable(true);
    file.setExecutable(true);
    return file;
  }

  private void exportResultExcel(List<TestResultVO> resultList) throws Exception {
    SXSSFWorkbook xb = new SXSSFWorkbook();
    Sheet sheet = xb.createSheet();

    String resultHead[] = new String[]{"测试时间", "发起端", "响应端", "时延-min", "时延-avg", "时延-max", "丢包率",
      "测试次数"};
    POIUtils.createExcelRow(sheet, resultHead, 0);

    int rowIndex = 1;
    for (TestResultVO vo : resultList) {
      String resultData[] = new String[]{vo.getTestTime(), vo.getMyAddress(), vo.getIpAddress(),
        vo.getMin(), vo.getAvg(), vo.getMax(), vo.getPacketLossRate(), vo.getTestProgress()};
      POIUtils.createExcelRow(sheet, resultData, rowIndex);
      rowIndex++;
    }
//    for (int i = 0; i < resultHead.length; i++) {
//      sheet.autoSizeColumn(i, true);
//    }
    String fileName = "resultInfo" + File.separator + TimeUtils.getDate() + "ping结果.xls";
    createFile(fileName);
    FileOutputStream outputStream = new FileOutputStream(ROOT_PATH + fileName);
    xb.write(outputStream);

    outputStream.close();
  }

  /**
   * 输出测试结果
   */
  private void outPutResult(List<TestResultVO> list) {
    for (TestResultVO vo : list) {
      TableRow tableRow = new TableRow(this);
      TextView testTime = new TextView(this);
      TextView ipAddress = new TextView(this);
      TextView min = new TextView(this);
      TextView max = new TextView(this);
      TextView avg = new TextView(this);
      TextView lossRate = new TextView(this);
      TextView testProgress = new TextView(this);

      testTime.setText(vo.getTestTime() + BLANK);
      ipAddress.setText(vo.getIpAddress() + BLANK);
      min.setText(vo.getMin() + BLANK);
      max.setText(vo.getMax() + BLANK);
      avg.setText(vo.getAvg() + BLANK);
      lossRate.setText(vo.getPacketLossRate() + BLANK);
      testProgress.setText(vo.getTestProgress() + BLANK);

      tableRow.addView(testTime);
      tableRow.addView(ipAddress);
      tableRow.addView(min);
      tableRow.addView(max);
      tableRow.addView(avg);
      tableRow.addView(lossRate);
      tableRow.addView(testProgress);

      resultTable
        .addView(tableRow, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

  }


  public String getRealPathFromURI(Uri contentUri) {
    String res = null;
    String[] proj = {MediaStore.Images.Media.DATA};
    Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
    if (null != cursor && cursor.moveToFirst()) {
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      res = cursor.getString(column_index);
      cursor.close();
    }
    return res;
  }

  /**
   * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
   */
  @SuppressLint("NewApi")
  public String getPath(final Context context, final Uri uri) {

    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          return Environment.getExternalStorageDirectory() + "/" + split[1];
        }
      }
      // DownloadsProvider
      else if (isDownloadsDocument(uri)) {

        final String id = DocumentsContract.getDocumentId(uri);
        final Uri contentUri = ContentUris.withAppendedId(
          Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        return getDataColumn(context, contentUri, null, null);
      }
      // MediaProvider
      else if (isMediaDocument(uri)) {
        final String docId = DocumentsContract.getDocumentId(uri);
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id=?";
        final String[] selectionArgs = new String[]{split[1]};

        return getDataColumn(context, contentUri, selection, selectionArgs);
      }
    }
    // MediaStore (and general)
    else if ("content".equalsIgnoreCase(uri.getScheme())) {
      return getDataColumn(context, uri, null, null);
    }
    // File
    else if ("file".equalsIgnoreCase(uri.getScheme())) {
      return uri.getPath();
    }
    return null;
  }

  /**
   * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
   * file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   * @param selection (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  public String getDataColumn(Context context, Uri uri, String selection,
    String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {column};

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
        null);
      if (cursor != null && cursor.moveToFirst()) {
        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return null;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  public boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * 动态申请权限
   */
  private void verifyStoragePermissions(Activity activity) {
    // Check if we have write permission
    int permission = ActivityCompat.checkSelfPermission(activity,
      Manifest.permission.WRITE_EXTERNAL_STORAGE);

    if (permission != PackageManager.PERMISSION_GRANTED) {
      // We don't have permission so prompt the user
      ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
        REQUEST_EXTERNAL_STORAGE);
    }
  }

  private boolean isBlank(String str) {
    if (str == null || "".equals(str.trim())) {
      return true;
    }
    return false;

  }

}
