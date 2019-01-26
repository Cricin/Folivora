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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

final class FolivoraInflater extends LayoutInflater {
  private LayoutInflater mInflater;

  FolivoraInflater(Context context, LayoutInflater delegate) {
    super(context);
    if (delegate instanceof FolivoraInflater) {
      this.mInflater = ((FolivoraInflater) delegate).mInflater.cloneInContext(context);
    } else {
      this.mInflater = delegate;
    }
    Factory2 factory2 = mInflater.getFactory2();
    if (factory2 == null) {
      mInflater.setFactory2(new FolivoraViewFactory());
    } else {
      if (!(factory2 instanceof FolivoraViewFactory))
        Log.i("Folivora", "The Activity's LayoutInflater already has a Factory installed"
          + " so we can not install Folivora's");
    }
  }

  @Override
  public LayoutInflater cloneInContext(Context newContext) {
    return new FolivoraInflater(newContext, this);
  }

  @Override
  public void setFactory(Factory factory) {
    FolivoraViewFactory f = (FolivoraViewFactory) mInflater.getFactory2();
    f.mFactory = factory;
  }

  @Override
  public void setFactory2(Factory2 factory2) {
    FolivoraViewFactory f = (FolivoraViewFactory) mInflater.getFactory2();
    f.mFactory2 = factory2;
  }

  @Override
  public Filter getFilter() {
    return mInflater.getFilter();
  }

  @Override
  public void setFilter(Filter filter) {
    mInflater.setFilter(filter);
  }

  @Override
  public View inflate(int resource, ViewGroup root) {
    return mInflater.inflate(resource, root);
  }

  @Override
  public View inflate(XmlPullParser parser, ViewGroup root) {
    return mInflater.inflate(parser, root);
  }

  @Override
  public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
    return mInflater.inflate(resource, root, attachToRoot);
  }

  @Override
  public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
    return mInflater.inflate(parser, root, attachToRoot);
  }

  LayoutInflater getBaseInflater() {
    return mInflater;
  }
}
