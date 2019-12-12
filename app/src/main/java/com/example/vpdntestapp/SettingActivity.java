package com.example.vpdntestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.Adapter.vpdntestapp.MulAdapter;
import com.Dialog.vpdntestapp.IpDialog;
import com.Dialog.vpdntestapp.IpDialog.IpDialogListener;
import com.Entity.vpdntestapp.model.TestIp;
import com.Entity.vpdntestapp.model.TestPara;
import com.Repo.vpdntestapp.TestIpRepo;
import com.Repo.vpdntestapp.TestParaRepo;
import com.Utils.vpdntestapp.IpUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author panqi
 */
public class SettingActivity extends Activity implements View.OnClickListener {

  Button btnSave;
  Button btnDelete;
  Button btnAdd;

  EditText packetCountText;
  EditText intervalText;
  EditText testTimeText;
  EditText waitTimeText;
  TextView localIpText;
  ListView listView;

  IpDialog ipDialog;

  private TestParaRepo testParaRepo;
  private TestIpRepo testIpRepo;

  private static MulAdapter adapter;

  private static int TEST_PARA_ID = 1;

  private List<HashMap<String, String>> ips;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test_info_setting);

    btnSave = findViewById(R.id.save);
    btnAdd = findViewById(R.id.add);
    btnDelete = findViewById(R.id.delete);

    btnSave.setOnClickListener(this);
    btnAdd.setOnClickListener(this);
    btnDelete.setOnClickListener(this);

    packetCountText = findViewById(R.id.packetCount);
    intervalText = findViewById(R.id.interval);
    testTimeText = findViewById(R.id.testTime);
    waitTimeText = findViewById(R.id.waitTime);
    localIpText = findViewById(R.id.localIp);
    listView = findViewById(R.id.listView);

    //读取测试参数
    testParaRepo = new TestParaRepo(this);
    TestPara testPara = testParaRepo.getParaById(TEST_PARA_ID);

    packetCountText.setText(String.valueOf(testPara.getPacketCount()));
    intervalText.setText(String.valueOf(testPara.getInterval()));
    testTimeText.setText(String.valueOf(testPara.getTestTime()));
    waitTimeText.setText(String.valueOf(testPara.getWaitTime()));
    localIpText.setText(IpUtils.getIp());

    //读取测试ip地址
    testIpRepo = new TestIpRepo(this);
    ips = testIpRepo.getIps();
    AdapterView.OnItemClickListener listItemListener = new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        MulAdapter.ViewHolder viewHolder = (MulAdapter.ViewHolder) view.getTag();
        viewHolder.checkBox.toggle();
        MulAdapter.getIsSelected().put(position, viewHolder.checkBox.isChecked());
      }
    };
    adapter = new MulAdapter(this, ips);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(listItemListener);
  }

  @Override
  public void onClick(View view) {
    testParaRepo = new TestParaRepo(this);
    testIpRepo = new TestIpRepo(this);

    switch (view.getId()) {
      case R.id.save:
        if (testParaRepo.getParaById(1).getId() != 1) {
          TestPara testPara = getTestPara();
          testParaRepo.insert(testPara);
          sendData();
//          finish();
        } else {
          TestPara testPara = getTestPara();
          testParaRepo.update(testPara);
          sendData();
//          finish();
        }
        break;
      case R.id.add:
        ipDialog = new IpDialog(this, R.style.IpDialog, new IpDialogListener() {
          @Override
          public void onClick(View view) {
            switch (view.getId()) {
              case R.id.addIp:
                TestIp testIp = new TestIp();
                testIp.setIpAddress(String.valueOf(ipDialog.ipText.getText()));
                testIpRepo.insert(testIp);
                ipDialog.dismiss();
                onResume();
              default:
                break;
            }
          }
        });
        ipDialog.show();
        break;
      case R.id.delete:
        HashMap<Integer, Boolean> isSelect = MulAdapter.getIsSelected();
        for (int i = 0; i < isSelect.size(); i++) {
          if (isSelect.get(i)) {
            int id = (int) adapter.getItemId(i);
            testIpRepo.deleteById(id);
          }
        }
        onResume();
        break;
      default:
        break;

    }


  }

  private TestPara getTestPara() {
    int packetCount = Integer.parseInt(String.valueOf(packetCountText.getText()));
    double interval = Double.parseDouble(String.valueOf(intervalText.getText()));
    int testTime = Integer.parseInt(String.valueOf(testTimeText.getText()));
    int waitTime = Integer.parseInt(String.valueOf(waitTimeText.getText()));

    TestPara testPara = new TestPara();
    testPara.setPacketCount(packetCount);
    testPara.setWaitTime(waitTime);
    testPara.setTestTime(testTime);
    testPara.setInterval(interval);
    testPara.setId(TEST_PARA_ID);

    return testPara;
  }

  private void sendData() {
    HashMap<Integer, Boolean> isSelected = adapter.getIsSelected();
    ArrayList<String> ipList = new ArrayList<>();
    for (int i = 0; i < isSelected.size(); i++) {
      if (isSelected.get(i)) {
        String ip = String.valueOf(ips.get(i).get("ipAddress"));
        ipList.add(ip);
      }
    }
    Intent intent = new Intent();
    intent.setClass(SettingActivity.this, MainActivity.class);
    intent.putExtra("ips", ipList);
    intent.putExtra("myIp", String.valueOf(localIpText.getText()));
    startActivity(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    onCreate(null);
  }

}
