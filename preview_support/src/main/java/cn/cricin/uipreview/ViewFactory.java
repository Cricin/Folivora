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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.android.ide.common.rendering.api.IProjectCallback;
import com.android.layoutlib.bridge.android.BridgeContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ViewFactory implements LayoutInflater.Factory2 {
  private static final String[] sClassPrefixList = {
    "android.widget.",
    "android.webkit.",
    "android.app.",
    "android.view."
  };
  private static final Set<String> APPCOMPAT_VIEWS = new HashSet<>(Arrays.asList(
    "TextView", "ImageView", "Button", "EditText", "Spinner", "ImageButton",
    "CheckBox", "RadioButton", "CheckedTextView", "AutoCompleteTextView",
    "MultiAutoCompleteTextView", "RatingBar", "SeekBar"));
  private Set<String> mFailedAppCompatViews = new HashSet<>();
  private static final Class<?>[] sConstructorSignature = {Context.class, AttributeSet.class};
  private static final Object[] sConstructorArgs = new Object[2];

  private LayoutInflater mDelegate;
  private boolean mLoadAppCompatViews;
  private IProjectCallback mProjectCallback;

  ViewFactory(LayoutInflater inflater, IProjectCallback projectCallback) {
    this.mDelegate = inflater;
    this.mProjectCallback = projectCallback;
    this.mLoadAppCompatViews = getContext().isAppCompatTheme();
  }

  private BridgeContext getContext() {
    return (BridgeContext) mDelegate.getContext();
  }

  @Override
  public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    View result = null;
    if (this.mLoadAppCompatViews && APPCOMPAT_VIEWS.contains(name) && !this.mFailedAppCompatViews.contains(name)) {
      result = this.loadCustomView("android.support.v7.widget.AppCompat" + name, attrs);
      if (result == null) {
        this.mFailedAppCompatViews.add(name);
      }
    }

    if (result == null) {
      for (String prefix : sClassPrefixList) {
        try {
          result = mDelegate.createView(name, prefix, attrs);
          if (result != null) {
            break;
          }
        } catch (ClassNotFoundException e) {
          // In this case we want to let the base class take a crack
          // at it.
        }
      }
    }

    //if is a custom view
    if (result == null) {
      result = loadCustomView(name, attrs);
    }

    if (result == null) {
      DebugLog.logLine("ViewFactory: failed to create view: " + name);
    } else {
      onViewCreated(result, attrs);
    }

    return result;
  }

  private View loadCustomView(String name, AttributeSet attrs) {
    if (this.mProjectCallback == null) return null;
    if (name.equals("view")) {
      name = attrs.getAttributeValue(null, "class");
      if (name == null) {
        return null;
      }
    }
    sConstructorArgs[0] = getContext();
    sConstructorArgs[1] = attrs;
    Object customView = null;

    try {
      customView = this.mProjectCallback.loadView(name, sConstructorSignature, sConstructorArgs);
    } catch (Exception e) {
      //empty
    } finally {
      sConstructorArgs[0] = null;
      sConstructorArgs[1] = null;
    }
    if (customView instanceof View) {
      return (View) customView;
    }
    return null;
  }

  @Override
  public View onCreateView(String name, Context context, AttributeSet attrs) {
    return onCreateView(null, name, context, attrs);
  }

  private void onViewCreated(View view/*Nonnull*/, AttributeSet attrs/*Nonnull*/) {
    FolivoraAccess.applyDrawableToView(view, attrs);
  }
}
