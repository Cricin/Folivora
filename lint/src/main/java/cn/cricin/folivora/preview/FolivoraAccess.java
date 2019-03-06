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

import java.lang.reflect.Method;

import static cn.cricin.folivora.preview.FolivoraPreview.sDebug;
import static cn.cricin.folivora.preview.FolivoraPreview.sLogger;

/**
 * This class is an mediator between layoutlib inside IDE and
 * Folivora inside current project's dependencies, this class
 * will try to find Folivora class and try to invoke it's
 * applyDrawableToView() method.
 */
final class FolivoraAccess {
  private static Method sMethod;
  private static Class<?> sReplacedBySuperClass;

  static void applyDrawableToView(View view, AttributeSet attrs) {
    if (sMethod == null) {
      return;
    }
    try {
      sMethod.invoke(null, view, attrs);
    } catch (Exception ex) {
      if (sDebug) sLogger.warn("FolivoraAccess: Folivora.applyDrawableToView() invoke failed", ex);
    }
  }

  /** if developer used preview stub views, we should skip it */
  static boolean shouldSkipApplyDrawable(String name, View view) {
    return name.startsWith("cn.cricin.folivora.view.")
      || (sReplacedBySuperClass != null && sReplacedBySuperClass.isInstance(view));
  }

  static void initIfNeeded(LayoutlibCallback callback) {
    if (sMethod != null) return;
    try {
      Class<?> c = callback.findClass("cn.cricin.folivora.Folivora");
      sMethod = c.getDeclaredMethod("applyDrawableToView", View.class, AttributeSet.class);
      sMethod.setAccessible(true);
      sReplacedBySuperClass = callback.findClass("cn.cricin.folivora.ReplacedBySuper");
      if (sDebug) sLogger.info("FolivoraAccess: Folivora class loaded");
    } catch (Exception ex) {
      if (sDebug) sLogger.warn("FolivoraAccess: Folivora class load failed", ex);
    }
  }
}
