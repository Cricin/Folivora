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

import android.util.AttributeSet;
import android.view.View;

import com.android.ide.common.rendering.api.LayoutlibCallback;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class is an mediator between layoutlib inside IDE and
 * Folivora inside current project's dependencies, this class
 * will try to find Folivora class and try to invoke it's
 * applyDrawableToView() method.
 */
final class FolivoraAccess {
  private static Method sMethod;

  static void applyDrawableToView(View view, AttributeSet attrs) {
    if (sMethod == null) {
      return;
    }
    try {
      sMethod.invoke(null, view, attrs);
    } catch (Exception ex) {
      FolivoraPreview.logOnlyOnce("FolivoraAccess",
        "Folivora.applyDrawableToView() invoke failed", ex);
    }
  }

  static void initIfNeeded(LayoutlibCallback callback) {
    if (sMethod != null) return;
    try {
      Class<?> c = callback.findClass("cn.cricin.folivora.Folivora");
      sMethod = c.getDeclaredMethod("applyDrawableToView", View.class, AttributeSet.class);
      sMethod.setAccessible(true);
      Field field = c.getDeclaredField("sDrawableCacheEnabled");
      field.setAccessible(true);
      field.set(null, false);
      FolivoraPreview.log("FolivoraAccess", "Folivora class loaded", null);
    } catch (Throwable ex) {
      FolivoraPreview.logOnlyOnce("FolivoraAccess", "Folivora class load failed", ex);
    }
  }
}
