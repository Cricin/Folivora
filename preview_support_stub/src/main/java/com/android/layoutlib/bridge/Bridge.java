package com.android.layoutlib.bridge;

public class Bridge extends com.android.ide.common.rendering.api.Bridge {

  public static void cleanupThread() {
    throw new RuntimeException("Stub!");
  }

  public static void prepareThread() {
    throw new RuntimeException("Stub!");
  }

  @Override
  public int getApiLevel() {
    throw new RuntimeException("Stub");
  }
}
