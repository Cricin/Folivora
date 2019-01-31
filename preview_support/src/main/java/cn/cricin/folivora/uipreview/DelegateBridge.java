/*
 * Copyright (C) 2019 Cricin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.cricin.folivora.uipreview;

import android.view.LayoutInflater;

import com.android.ide.common.rendering.api.Bridge;
import com.android.ide.common.rendering.api.Capability;
import com.android.ide.common.rendering.api.DrawableParams;
import com.android.ide.common.rendering.api.LayoutLog;
import com.android.ide.common.rendering.api.RenderSession;
import com.android.ide.common.rendering.api.Result;
import com.android.ide.common.rendering.api.SessionParams;
import com.android.layoutlib.bridge.RenderSessionAccess;
import com.android.layoutlib.bridge.android.RenderParamsFlags;
import com.android.layoutlib.bridge.impl.RenderSessionImpl;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;

import static com.android.layoutlib.bridge.Bridge.cleanupThread;
import static com.android.layoutlib.bridge.Bridge.prepareThread;

public final class DelegateBridge extends Bridge {
  private Bridge mBridge;

  public DelegateBridge(Bridge bridge) {
    this.mBridge = bridge;
  }

  @Override
  public int getRevision() {
    return mBridge.getRevision();
  }

  @Override
  public EnumSet<Capability> getCapabilities() {
    return mBridge.getCapabilities();
  }

  @Override
  public boolean supports(int feature) {
    return mBridge.supports(feature);
  }

  @Override
  public boolean init(Map<String, String> platformProperties, File fontLocation, Map<String,
    Map<String, Integer>> enumValueMap, LayoutLog log) {
    return mBridge.init(platformProperties, fontLocation, enumValueMap, log);
  }

  @Override
  public boolean dispose() {
    return mBridge.dispose();
  }

  @Override
  public Result renderDrawable(DrawableParams params) {
    return mBridge.renderDrawable(params);
  }

  @Override
  public void clearCaches(Object projectKey) {
    mBridge.clearCaches(projectKey);
  }

  @Override
  public Result getViewParent(Object viewObject) {
    return mBridge.getViewParent(viewObject);
  }

  @Override
  public Result getViewIndex(Object viewObject) {
    return mBridge.getViewIndex(viewObject);
  }

  @Override
  public boolean isRtl(String locale) {
    return mBridge.isRtl(locale);
  }

  @Override
  public Result getViewBaseline(Object viewObject) {
    return mBridge.getViewBaseline(viewObject);
  }

  @Override
  public int getApiLevel() {
    return 5;
  }

  @Override
  public RenderSession createSession(SessionParams params) {
    FolivoraAccess.initIfNeeded(params);
    try {
      MyRenderSessionImpl scene = new MyRenderSessionImpl(params);
      Result lastResult;
      try {
        prepareThread();
        lastResult = scene.init(params.getTimeout());
        if (lastResult.isSuccess()) {
          lastResult = scene.inflate();
          boolean doNotRenderOnCreate = Boolean.TRUE.equals(params.getFlag(RenderParamsFlags.FLAG_DO_NOT_RENDER_ON_CREATE));
          if (lastResult.isSuccess() && !doNotRenderOnCreate) {
            lastResult = scene.render(true);
          }
        }
      } finally {
        scene.release();
        cleanupThread();
      }
      return RenderSessionAccess.newRenderSession(scene, lastResult);
    } catch (Throwable ex) {
      DebugLog.error("DelegateBridge: error occurred, delegate to system bridge", ex);
      return mBridge.createSession(params);
    }
  }

  static class MyRenderSessionImpl extends RenderSessionImpl {
    MyRenderSessionImpl(SessionParams params) {
      super(params);
    }

    @Override
    public Result init(long timeout) {
      Result result = super.init(timeout);
      LayoutInflater i = (LayoutInflater.from(getContext()));
      if (i.getFactory2() == null) {
        i.setFactory2(new ViewFactory(i, getParams().getLayoutlibCallback()));
      } else {
        DebugLog.info("DelegateBridge: A Factory2 has already been set to LayoutInflater");
      }
      return result;
    }
  }

}
