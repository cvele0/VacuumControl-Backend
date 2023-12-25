package rs.raf.demo.model;

public class UserPermission {
  public static int CAN_CREATE_USERS = 1;
  public static int CAN_READ_USERS = 2;
  public static int CAN_UPDATE_USERS = 4;
  public static int CAN_DELETE_USERS = 8;

  public static int CAN_SEARCH_VACUUM = 16;
  public static int CAN_START_VACUUM = 32;
  public static int CAN_STOP_VACUUM = 64;
  public static int CAN_DISCHARGE_VACUUM = 128;
  public static int CAN_ADD_VACUUM = 256;
  public static int CAN_REMOVE_VACUUMS = 512;
}
