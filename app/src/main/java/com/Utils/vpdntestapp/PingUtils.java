package com.Utils.vpdntestapp;

import com.Entity.vpdntestapp.model.TestPara;
import com.Entity.vpdntestapp.vo.TestResultVO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author panqi
 */
public class PingUtils {

  /**
   * net包提供的方法，不靠谱 有时候会出现误判
   *
   * @param ipAddress hostname
   * @return isReachable
   */
  public static boolean isReachable(String ipAddress) throws Exception {
    int timeOut = 3000;  //超时应该在3秒以上
    boolean status = InetAddress.getByName(ipAddress)
      .isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
    return status;
  }

  /**
   * 曲线救国的方法，直接ping
   *
   * @param hostName hostName
   * @return isHostReachable
   */
  public static boolean isHostReachable(String hostName) {
    try {
      //waitFor方法返回0为ping的通,返回2则失败
      return 0 == Runtime.getRuntime().exec("ping -c 1 " + hostName).waitFor();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * 根据参数ping某个ip地址
   *
   * @param ipAddress hostName
   * @param testPara 测试参数
   * @param pingMsg ping信息
   * @param presentCount 当前测试次数
   * @param testResultList 测试结果列表
   * @return boolean
   */
  public static boolean ping(String ipAddress, TestPara testPara, StringBuffer pingMsg,
    int presentCount, List<TestResultVO> testResultList) {
    //连接不到节点直接return
    if (!isHostReachable(ipAddress)) {
      pingMsg.append(ipAddress + "无法连接\n");
      return false;
    }
    int pingTimes = testPara.getPacketCount();
    double timeOut = testPara.getInterval();
    BufferedReader reader = null;
    List<String> result = new ArrayList<>();
    Runtime runtime = Runtime.getRuntime();
    // 将要执行的ping命令,此命令是linux格式的命令
    String pingCommand = "ping" + " -c " + pingTimes + " -i " + timeOut + " " + ipAddress;
    try {
      System.out.println(pingCommand);
      Process p = runtime.exec(pingCommand);
      if (p == null) {
        return false;
      }
      reader = new BufferedReader(
        new InputStreamReader(p.getInputStream()));
      String line;
      Boolean isResult = false;
      while ((line = reader.readLine()) != null) {
        if (isResult) {
          //把测试结果拿出来
          result.add(line);
        }
        if (line.startsWith("---")) {
          isResult = true;
        }
        pingMsg.append(line + "\n");
        System.out.println(line);
      }
      //测试结果
      String testProgress = presentCount + "/" + testPara.getTestTime();
      testResultList.add(exportTestResult(result, ipAddress, testProgress));
      return true;
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void stopPing() {
    Runtime runtime = Runtime.getRuntime();
    String command = "0x03";
    try {
      runtime.exec(command);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * 取出ping测试的测试结果
   *
   * @param result 测试结果信息
   * @param ipAddress 目标地址
   * @param testProgress 测试进度
   * @return 测试结果
   */
  private static TestResultVO exportTestResult(List<String> result, String ipAddress,
    String testProgress) {
    TestResultVO testResultVO = new TestResultVO();
    //获取丢包率
    String packetMsg = result.get(0);
    List<String> packetMsgList = Arrays.asList(packetMsg.split(","));
    String temp = packetMsgList.get(2).trim(); //截取丢包率的信息 一定在index=2这里
    int rateIndex = temp.indexOf("%");
    String packetLossRate = temp.substring(0, rateIndex + 1);
    testResultVO.setPacketLossRate(packetLossRate);
    //获取 max avg
    String timeMsg = result.get(1);
    int equalSingIndex = timeMsg.indexOf("=");
    String timeData = timeMsg.substring(equalSingIndex + 1);
    List<String> timeMsgList = Arrays.asList(timeData.trim().split("/")); //min在index=0,avg在1,max在2
    testResultVO.setMin(timeMsgList.get(0));
    testResultVO.setAvg(timeMsgList.get(1));
    testResultVO.setMax(timeMsgList.get(2));
    testResultVO.setIpAddress(ipAddress);
    testResultVO.setTestTime(TimeUtils.getTime());
    testResultVO.setTestProgress(testProgress);
    testResultVO.setMyAddress(IpUtils.getIp());
    return testResultVO;

  }

//  public static void main(String[] args) {
//
//  }
}
