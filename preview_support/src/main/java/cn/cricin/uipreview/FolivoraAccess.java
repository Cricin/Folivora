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

import android.util.AttributeSet;
import android.view.View;

import com.android.ide.common.rendering.api.IProjectCallback;
import com.android.ide.common.rendering.api.SessionParams;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class FolivoraAccess {
  private static Method sMethod;
  private static Method sFindClassMethod;

  static void applyDrawableToView(View view, AttributeSet attrs) {
    if (sMethod == null) {
      return;
    }
    try {
      sMethod.invoke(null, view, attrs);
    } catch (Exception e) {
      DebugLog.logLine("FolivoraAccess: Folivora.applyDrawableToView() invoke failed");
      DebugLog.logLine(e);
    }
  }

  static void initIfNeeded(SessionParams params) {
    if (sMethod != null) return;
    IProjectCallback callback = params.getProjectCallback();
    if (callback == null) return;
    initFindClassMethod(callback);

    if (sFindClassMethod != null) {
      try {
        Class<?> c = (Class) sFindClassMethod.invoke(callback, "cn.cricin.folivora.Folivora");
        sMethod = c.getDeclaredMethod("applyDrawableToView", View.class, AttributeSet.class);
        sMethod.setAccessible(true);
        Field sOut = c.getDeclaredField("sOut");
        sOut.setAccessible(true);
        sOut.set(null, new Appendable() {
          @Override
          public Appendable append(CharSequence charSequence) {
            DebugLog.log(charSequence.toString());
            return this;
          }

          @Override
          public Appendable append(CharSequence charSequence, int offset, int count) {
            DebugLog.log(charSequence.subSequence(offset, count).toString());
            return this;
          }

          @Override
          public Appendable append(char c) {
            return append(String.valueOf(c));
          }
        });
        DebugLog.logLine("FolivoraAccess: Folivora class loaded");
      } catch (Exception e) {
        DebugLog.logLine("FolivoraAccess: Folivora class load failed");
        DebugLog.logLine(e);
      }
    }
  }

  private static void initFindClassMethod(Object o) {
    if (sFindClassMethod == null) {
      try {
        sFindClassMethod = o.getClass().getDeclaredMethod("findClass", String.class);
        sFindClassMethod.setAccessible(true);
      } catch (NoSuchMethodException e) {
        DebugLog.logLine("FolivoraAccess: can not find findClass(String) method");
      }
    }
  }
}
