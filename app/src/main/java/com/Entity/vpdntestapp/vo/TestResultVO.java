package com.Entity.vpdntestapp.vo;

public class TestResultVO {

  /**
   * 系统时间
   */
  private String testTime;
  /**
   * 响应端
   */
  private String ipAddress;
  /**
   * 发起端
   */
  private String myAddress;
  /**
   * 最大延时
   */
  private String max;
  /**
   * 最小延时
   */
  private String min;
  /**
   * 平均延时
   */
  private String avg;
  /**
   * 丢包率
   */
  private String packetLossRate;
  /**
   * 测试进度
   */
  private String testProgress;

  public String getTestTime() {
    return testTime;
  }

  public void setTestTime(String testTime) {
    this.testTime = testTime;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public String getAvg() {
    return avg;
  }

  public void setAvg(String avg) {
    this.avg = avg;
  }

  public String getPacketLossRate() {
    return packetLossRate;
  }

  public void setPacketLossRate(String packetLossRate) {
    this.packetLossRate = packetLossRate;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public String getTestProgress() {
    return testProgress;
  }

  public void setTestProgress(String testProgress) {
    this.testProgress = testProgress;
  }

  public String getMyAddress() {
    return myAddress;
  }

  public void setMyAddress(String myAddress) {
    this.myAddress = myAddress;
  }
}
