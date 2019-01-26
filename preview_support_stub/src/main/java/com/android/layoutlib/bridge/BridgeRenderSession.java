package com.android.layoutlib.bridge;

import com.android.ide.common.rendering.api.RenderSession;
import com.android.ide.common.rendering.api.Result;
import com.android.layoutlib.bridge.impl.RenderSessionImpl;

public class BridgeRenderSession extends RenderSession {
  public BridgeRenderSession(RenderSessionImpl scene, Result lastResult) {
    throw new RuntimeException("Stub!");
  }
}
