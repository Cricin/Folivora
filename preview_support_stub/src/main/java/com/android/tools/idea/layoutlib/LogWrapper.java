package com.android.tools.idea.layoutlib;

import com.android.utils.ILogger;
import com.intellij.openapi.diagnostic.Logger;

public class LogWrapper implements ILogger {

  public LogWrapper(Logger logger) {
    throw new RuntimeException("Stub!");
  }

  @Override
  public void error(Throwable t, String msgFormat, Object... args) {
    throw new RuntimeException("Stub!");
  }

  @Override
  public void warning(String msgFormat, Object... args) {
    throw new RuntimeException("Stub!");
  }

  @Override
  public void info(String msgFormat, Object... args) {
    throw new RuntimeException("Stub!");
  }

  @Override
  public void verbose(String msgFormat, Object... args) {
    throw new RuntimeException("Stub!");
  }
}
