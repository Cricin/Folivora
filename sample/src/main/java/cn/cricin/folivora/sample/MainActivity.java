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

package cn.cricin.folivora.sample;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import cn.cricin.folivora.DrawableParser;
import cn.cricin.folivora.Folivora;
import cn.cricin.folivora.OnViewCreatedListener;
import cn.cricin.folivora.ParseRequest;
import cn.cricin.folivora.RippleFallback;
import cn.cricin.folivora.sample.drawable.UmbrellaDrawable;

public class MainActivity extends Activity {

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Folivora.wrap(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Folivora.installViewFactory(this); //if you do not want folivora wraps the base context, using this

    //create a selector if ripple drawable is unavailable
    Folivora.setRippleFallback(new RippleFallback() {
      @Override
      public Drawable onFallback(ColorStateList color, Drawable content, Drawable mask, Context ctx) {
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(color.getDefaultColor()));
        sld.addState(new int[0], content);
        return sld;
      }
    });

    //UmbrellaDrawable does not have a UmbrellaDrawable(Context ctx, AttributeSet attrs)
    //constructor, so we take over creation here
    Folivora.registerDrawableParser(UmbrellaDrawable.class, new DrawableParser() {
      @Override
      public Drawable parse(ParseRequest request) {
        TypedArray a = request.context().obtainStyledAttributes(request.attrs(), R.styleable.UmbrellaDrawable);
        UmbrellaDrawable d = new UmbrellaDrawable();
        d.setBackgroundColor(a.getColor(R.styleable.UmbrellaDrawable_udBackgroundColor, d.getBackgroundColor()));
        d.setColor1(a.getColor(R.styleable.UmbrellaDrawable_udColor1, d.getColor1()));
        d.setColor2(a.getColor(R.styleable.UmbrellaDrawable_udColor2, d.getColor2()));
        a.recycle();
        return d;
      }
    });

    // If you want to do some further customization, this will be helpful
    Folivora.addOnViewCreatedListener(new OnViewCreatedListener() {
      @Override
      public void onViewCreated(View view, AttributeSet attrs) {
        //we change text color to green here
        if (view instanceof TextView) {
          ((TextView) view).setTextColor(Color.GREEN);
        }
      }
    });

    setContentView(R.layout.activity_main);
  }
}
