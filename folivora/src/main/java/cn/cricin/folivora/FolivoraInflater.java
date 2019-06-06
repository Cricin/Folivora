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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

/**
 * A LayoutInflater implementation which delegate all view creation
 * to {@link FolivoraViewFactory}.
 */
final class FolivoraInflater extends LayoutInflater {
  private static final String[] sClassPrefixList = {
    "android.widget.",
    "android.webkit.",
    "android.app."
  };

  FolivoraInflater(Context newContext, LayoutInflater original) {
    super(original, newContext);
  }

  @Override
  public LayoutInflater cloneInContext(Context newContext) {
    return new FolivoraInflater(newContext, this);
  }

  @Override
  public void setFactory(Factory factory) {
    FolivoraViewFactory f = (FolivoraViewFactory) getFactory2();
    if (f == null) {
      super.setFactory2(f = new FolivoraViewFactory());
    }
    f.mFactory = factory;
  }

  @Override
  public void setFactory2(Factory2 factory2) {
    FolivoraViewFactory f = (FolivoraViewFactory) getFactory2();
    if (f == null) {
      super.setFactory2(f = new FolivoraViewFactory());
    }
    f.mFactory2 = factory2;
  }

  @Override
  public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
    Factory2 factory2 = getFactory2();
    if (factory2 == null) {
      super.setFactory2(new FolivoraViewFactory());
    }
    return super.inflate(parser, root, attachToRoot);
  }

  /** fallback if FolivoraViewFactory could not create views properly */
  @Override
  protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
    for (String prefix : sClassPrefixList) {
      try {
        View view = createView(name, prefix, attrs);
        if (view != null) {
          return view;
        }
      } catch (ClassNotFoundException e) {
        // In this case we want to let the base class take a crack
        // at it.
      }
    }
    return super.onCreateView(name, attrs);
  }
}
