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

import sun.misc.Unsafe;

/**
 * Preview support entry point for Folivora.
 */
@SuppressWarnings("unchecked")
public final class FolivoraPreview {
  static final Logger sLogger = Logger.getInstance(FolivoraPreview.class);

  private static Field sContextField;
  private static WeakHashMap<Resources, BridgeContext> sContextMap;

  public static void install() {
    try {
      Field field = Resources_Delegate.class.getDeclaredField("sContexts");
      field.setAccessible(true);
      sContextMap = (WeakHashMap<Resources, BridgeContext>) field.get(null);
    } catch (Throwable t) {
      sLogger.info("Unable to find static field sContexts in" +
        " Resource_Delegate, current AS version may lower than 3.0", t);
    }
    if (sContextMap == null) {
      try {
        sContextField = Resources.class.getDeclaredField("mContext");
        sContextField.setAccessible(true);
      } catch (Throwable t) {
        sLogger.info("Unable to find static field mContext in" +
          " Resource_Delegate, current AS version may higher than 3.0", t);
      }
    }
    if (sContextField == null && sContextMap == null) {
      sLogger.info("Preview install failed, AS version not supported");
      return;
    }
    tryHookConstructorMap();
  }

  private static void tryHookConstructorMap() {
    Field field = null;
    HashMap<String, Constructor<?>> origin = null;
    boolean needHookWithUnsafe = false;
    try {
      field = LayoutInflater.class.getDeclaredField("sConstructorMap");
      field.setAccessible(true);
      Field modifiers = Field.class.getDeclaredField("modifiers");
      modifiers.setAccessible(true);
      modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
      origin = (HashMap<String, Constructor<?>>) field.get(null);
      if (origin.getClass().getCanonicalName().endsWith("MyHashMap")) return;// already hooked
      field.set(null, new MyHashMap<>(origin));
    } catch (Exception ex) {
      needHookWithUnsafe = true;
    }
    if(field == null || origin == null) {
      sLogger.info("Preview install failed, Unable to find field LayoutInflater.sConstructorMap");
      return;
    }
    // if failed, hook using unsafe
    if (needHookWithUnsafe) {
      Unsafe unsafe = getUnsafe();
      if(unsafe == null) return;
      Object fieldBase = unsafe.staticFieldBase(field);
      long offset = unsafe.staticFieldOffset(field);
      unsafe.putObjectVolatile(fieldBase, offset, new MyHashMap<>(origin));
    }
    try {
      Object o = field.get(null);
      if (o != null && o.getClass().getCanonicalName().endsWith("MyHashMap")) {
        sLogger.info("Preview installed successfully");
      } else {
        sLogger.info("Preview install failed");
      }
    } catch (Exception e) {
      sLogger.info("Preview install failed");
    }
  }

  private static Unsafe getUnsafe(){
    Unsafe unsafe = null;
    try {
      unsafe = Unsafe.getUnsafe();
    } catch (SecurityException e){
      try {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        unsafe = (Unsafe) field.get(null);
      } catch (Exception ignore) {}
    }
    return unsafe;
  }

  private static void installViewFactoryIfNeeded() {
    Collection<BridgeContext> contexts = peekContexts();
    for (BridgeContext context : contexts) {
      LayoutInflater inflater = LayoutInflater.from(context);
      if (inflater.getFactory2() == null) {
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

  static class MyHashMap<K,V> extends HashMap<K,V>{
    MyHashMap(Map<? extends K, ? extends V> map) {
      super(map);
    }

    @Override
    public V get(Object o) {
      installViewFactoryIfNeeded();
      return super.get(o);
    }
  }

}
