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

import com.android.layoutlib.bridge.android.BridgeContext;
import com.intellij.openapi.diagnostic.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Preview support entry point for Folivora.
 */
@SuppressWarnings("unchecked")
public final class FolivoraPreview {
  private static final Logger sLogger = Logger.getInstance(FolivoraPreview.class);
  private static boolean sLogged = false;

  private static Field sContextField;
  private static WeakHashMap<Resources, BridgeContext> sContextMap;

  public static void install() {
    try {
      Field field = Resources_Delegate.class.getDeclaredField("sContexts");
      field.setAccessible(true);
      sContextMap = (WeakHashMap<Resources, BridgeContext>) field.get(null);
    } catch (Throwable t) {
      log("FolivoraPreview", "Unable to find static field sContexts in" +
        " Resource_Delegate, current AS version may lower than 3.0", t);
    }
    if (sContextMap == null) {
      try {
        sContextField = Resources.class.getDeclaredField("mContext");
        sContextField.setAccessible(true);
      } catch (Throwable t) {
        log("FolivoraPreview", "Unable to find static field mContext in" +
          " Resource_Delegate, current AS version may higher than 3.0", t);
      }
    }
    if (sContextField == null && sContextMap == null) {
      log("FolivoraPreview", "FolivoraPreview not installed, AS version not supported", null);
      return;
    }
    try {
      Field field = LayoutInflater.class.getDeclaredField("sConstructorMap");
      field.setAccessible(true);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      modifiers.setAccessible(true);
      modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
      Map<String, Constructor<?>> origin = (Map<String, Constructor<?>>) field.get(null);
      Map<String, Constructor<?>> map = new HashMap<String, Constructor<?>>(origin) {
        @Override
        public Constructor<?> get(Object o) {
          installViewFactoryIfNeeded();
          return super.get(o);
        }
      };
      field.set(null, map);
    } catch (Exception ex) {
      log("FolivoraPreview", "FolivoraPreview not installed", ex);
    }
  }

  private static void installViewFactoryIfNeeded() {
    Collection<BridgeContext> contexts = peekContexts();
    for (BridgeContext context : contexts) {
      LayoutInflater inflater = LayoutInflater.from(context);
      if (inflater.getFactory2() == null) {
        FolivoraAccess.initIfNeeded(context.getLayoutlibCallback());
        inflater.setFactory2(new ViewFactory(inflater, context.getLayoutlibCallback()));
      }
    }
  }

  private static Collection<BridgeContext> peekContexts() {
    if (sContextMap != null) {
      return sContextMap.values();
    } else if (sContextField != null) {
      try {
        return Collections.singletonList((BridgeContext) sContextField.get(Resources.getSystem()));
      } catch (Exception ignore) {}
    }
    return Collections.emptyList();
  }

  static void logOnlyOnce(String tag, String msg, Throwable t){
    // if error occurred in layout inflation, we should only log
    // once, so IDE log file will not be massed up.
    if(sLogged) return;
    sLogger.info("[" + tag + "] ->" + msg , t);
    sLogged = true;
  }

  static void log(String tag, String msg, Throwable t) {
    sLogger.info("[" + tag + "] ->" + msg , t);
  }
}
