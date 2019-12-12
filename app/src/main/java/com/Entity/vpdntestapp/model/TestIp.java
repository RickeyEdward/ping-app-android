package com.Entity.vpdntestapp.model;

/**
 * @author panqi
 */
public class TestIp {

  public static final String TABLE = "TestIp";

  public static final String KEY_ID = "id";

  public static final String KEY_IPADDRESS = "ip_address";

  private int id;

  private String ipAddress;

  public TestIp() {
  }

  public TestIp(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }
}
