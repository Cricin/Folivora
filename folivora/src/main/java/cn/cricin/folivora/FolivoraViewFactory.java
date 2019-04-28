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

package cn.cricin.folivora;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A view factory takes responsibility of view creation, if the view
 * is created, folivora will create a drawable for it if available.
 */
final class FolivoraViewFactory implements LayoutInflater.Factory2 {
  private static final String[] sClassPrefixList = {
    "android.widget.",
    "android.webkit.",
    "android.app.",
    "android.view."
  };

  private static final Class<?>[] sConstructorSignature = new Class[]{
    Context.class, AttributeSet.class};
  private static Object[] sConstructorArgs = new Object[2];
  private static Map<String, Constructor<? extends View>> sConstructorMap = new HashMap<>();

  LayoutInflater.Factory2 mFactory2;
  LayoutInflater.Factory mFactory;

  @Override
  public View onCreateView(String name, Context context, AttributeSet attrs) {
    return onCreateView(null, name, context, attrs);
  }

  @Override
  public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    View view = null;
    if (mFactory2 != null) {
      view = mFactory2.onCreateView(parent, name, context, attrs);
    }
    if (view == null && mFactory != null) {
      view = mFactory.onCreateView(name, context, attrs);
    }
    if (view == null && name.endsWith("ViewStub")) return null;//fix NPE when creating ViewStub

    if (view == null && name.indexOf('.') != -1) {
      view = createView(name, context, attrs);
    }

    if (view == null) {
      for (String prefix : sClassPrefixList) {
        view = createView(prefix + name, context, attrs);
        if (view != null) break;
      }
    }
    if (view != null) {
      Folivora.applyDrawableToView(view, attrs);
      Folivora.dispatchViewCreated(view, attrs);
    }
    return view;
  }

  private static View createView(String name, Context ctx, AttributeSet attrs) {
    Constructor<? extends View> constructor = sConstructorMap.get(name);
    try {
      if (constructor == null) {
        // Class not found in the cache, see if it's real, and try to add it
        Class<? extends View> clazz = ctx.getClassLoader().loadClass(name).asSubclass(View.class);

        constructor = clazz.getConstructor(sConstructorSignature);
        sConstructorMap.put(name, constructor);
      }
      constructor.setAccessible(true);
      sConstructorArgs[0] = ctx;
      sConstructorArgs[1] = attrs;
      return constructor.newInstance(sConstructorArgs);
    } catch (Exception e) {
      // We do not want to catch these, lets return null and let the actual LayoutInflater
      // try
      return null;
    } finally {
      sConstructorArgs[0] = null;
      sConstructorArgs[1] = null;
    }
  }

  private static LayoutInflater getLayoutInflater(Context context) {
    return LayoutInflater.from(context);
  }

  private static boolean sCheckedField;
  private static Field sLayoutInflaterFactory2Field;

  static void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
    if (!sCheckedField) {
      try {
        //noinspection JavaReflectionMemberAccess
        sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
        sLayoutInflaterFactory2Field.setAccessible(true);
      } catch (NoSuchFieldException e) {
        Log.e(Folivora.TAG, "forceSetFactory2 Could not find field 'mFactory2' on class "
          + inflater.getClass().getName() + "; Folivora will not available.", e);
      }
      sCheckedField = true;
    }
    if (sLayoutInflaterFactory2Field != null) {
      try {
        sLayoutInflaterFactory2Field.set(inflater, factory);
      } catch (Exception e) {
        Log.e(Folivora.TAG, "forceSetFactory2 could not set the Factory2 on LayoutInflater "
          + inflater + "; Folivora will not available.", e);
      }
    }
  }

}
