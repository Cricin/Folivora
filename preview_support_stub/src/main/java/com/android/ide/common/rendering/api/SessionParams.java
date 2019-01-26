package com.android.ide.common.rendering.api;

public class SessionParams extends RenderParams {

  public interface Key<T> {
  }

  public IProjectCallback getProjectCallback() {
    throw new RuntimeException("Stub!");
  }

  public long getTimeout() {
    throw new RuntimeException("Stub!");
  }

  public <T> T getFlag(Key<T> key) {
    throw new RuntimeException("Stub!");
  }
}