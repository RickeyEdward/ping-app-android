package com.Entity.vpdntestapp.model;

/**
 * @author panqi
 */
public class TestPara {

  public static final String TABLE = "TestPara";

  public static final String KEY_ID = "id";

  public static final String KEY_PACKETCOUNT = "packet_count";

  public static final String KEY_INTERVAL = "interval";

  public static final String KEY_TESTTIME = "test_time";

  public static final String KEY_WAITTIME = "wait_time";

  private int id;

  private int packetCount;

  private double interval;

  private int testTime;

  private int waitTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPacketCount() {
    return packetCount;
  }

  public void setPacketCount(int packetCount) {
    this.packetCount = packetCount;
  }

  public double getInterval() {
    return interval;
  }

  public void setInterval(double interval) {
    this.interval = interval;
  }

  public int getTestTime() {
    return testTime;
  }

  public void setTestTime(int testTime) {
    this.testTime = testTime;
  }

  public int getWaitTime() {
    return waitTime;
  }

  public void setWaitTime(int waitTime) {
    this.waitTime = waitTime;
  }
}
