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

package cn.cricin.folivora.preview;

import android.content.res.Resources;
import android.content.res.Resources_Delegate;
import android.view.LayoutInflater;

import com.android.ide.common.rendering.api.LayoutlibCallback;
import com.android.layoutlib.bridge.android.BridgeContext;
import com.android.layoutlib.bridge.impl.Stack;
import com.intellij.openapi.diagnostic.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Preview support entry point for Folivora.
 */
@SuppressWarnings("unchecked")
public final class FolivoraPreview {
  static final Logger sLogger = Logger.getInstance(FolivoraPreview.class);
  static final boolean sDebug = true;

  public static void install() {
    try {
      Field field = Resources_Delegate.class.getDeclaredField("sContexts");
      field.setAccessible(true);
      WeakHashMap originMap = (WeakHashMap) field.get(null);
      MyWeakHashMap map = new MyWeakHashMap();
      map.putAll(originMap);
      field.set(null, map);
    } catch (Exception ex) {
      if(sDebug) sLogger.error(ex);
    }
  }

  private static Field sParserStackField;

  private static void setUpAfterContextCreated(Resources res, BridgeContext ctx) {
    try {
      if (sParserStackField == null) {
        sParserStackField = ctx.getClass().getDeclaredField("mParserStack");
        sParserStackField.setAccessible(true);
      }
      Stack originStack = (Stack) sParserStackField.get(ctx);
      MyStack myStack = new MyStack(res);
      myStack.addAll(originStack);
      sParserStackField.set(ctx, myStack);
    } catch (Exception ex) {
      if(sDebug) sLogger.error(ex);
    }
  }

  private static void setUpPreInflate(Resources resources) {
    BridgeContext context = Resources_Delegate.getContext(resources);
    if (context == null) return;
    LayoutInflater inflater = LayoutInflater.from(context);
    if (inflater == null) return;
    if (inflater.getFactory2() != null) return;
    LayoutlibCallback callback = Resources_Delegate.getLayoutlibCallback(resources);
    inflater.setFactory2(new ViewFactory(inflater, callback));
    FolivoraAccess.initIfNeeded(callback);
  }

  private static class MyStack extends Stack {
    private WeakReference<Resources> mResRef;

    MyStack(Resources res) {
      this.mResRef = new WeakReference<>(res);
    }

    @Override
    public void push(Object object) {
      setUpPreInflate(mResRef.get());
      super.push(object);
    }

    @Override
    public boolean addAll(Collection collection) {
      setUpPreInflate(mResRef.get());
      return super.addAll(collection);
    }
  }

  private static class MyWeakHashMap extends WeakHashMap<Resources, BridgeContext> {
    @Override
    public BridgeContext put(Resources res, BridgeContext ctx) {
      setUpAfterContextCreated(res, ctx);
      return super.put(res, ctx);
    }

    @Override
    public void putAll(Map<? extends Resources, ? extends BridgeContext> map) {
      for (Map.Entry<? extends Resources, ? extends BridgeContext> entry : map.entrySet()) {
        setUpAfterContextCreated(entry.getKey(), entry.getValue());
      }
      super.putAll(map);
    }
  }

}
