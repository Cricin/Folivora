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

package cn.cricin.uipreview;

import android.content.Context;
import android.view.LayoutInflater;

import com.android.ide.common.rendering.api.Result;
import com.android.ide.common.rendering.api.SessionParams;
import com.android.layoutlib.bridge.android.BridgeContext;
import com.android.layoutlib.bridge.impl.RenderSessionImpl;

public final class MyRenderSessionImpl extends RenderSessionImpl {
  MyRenderSessionImpl(SessionParams params) {
    super(params);
  }

  @Override
  public Result init(long timeout) {
    Result init = super.init(timeout);
    BridgeContext context = getContext();
    if (context != null) {
      injectLayoutInflaterFactory(context);
    }
    return init;
  }

  private void injectLayoutInflaterFactory(Context context) {
    DebugLog.logLine("MyRenderSessionImpl: set ViewFactory");

    LayoutInflater i = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    if (i != null) {
      if (i.getFactory2() == null) {
        i.setFactory2(new ViewFactory(i, getParams().getProjectCallback()));
      }
    }
  }
}
