package com.android.sdklib;

public interface IAndroidTarget {
  int FONTS = 12;

  /**
   * Returns true if the target is a standard Android platform.
   */
  boolean isPlatform();

  /**
   * Returns the target location.
   */
  String getLocation();

  /**
   * Returns the parent target. This is likely to only be non <code>null</code> if
   * {@link #isPlatform()} returns <code>false</code>
   */
  IAndroidTarget getParent();

  /**
   * Returns the version of the target. This is guaranteed to be non-null.
   */
  AndroidVersion getVersion();

  String getPath(int type);

}
