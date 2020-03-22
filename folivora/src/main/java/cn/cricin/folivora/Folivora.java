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
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Folivora support sets drawable directly in your layout.xml files, no need
 * to create XXX.xml in drawable directory. just write down attributes we
 * provided in your layout.xml, folivora will take care of with this attrs
 * and create suitable drawable's for view.
 * <p>
 * Folivora is light weight, you would use {@link #wrap(Context)} wrap()
 * or {@link #installViewFactory(Context)} installViewFactory() to enable
 * folivora functions
 *
 * @see #wrap(Context)
 * @see #installViewFactory(Context)
 * @see #setRippleFallback(RippleFallback)
 * @see #getDrawable(Context, TypedArray, AttributeSet, int)
 * @see #addOnViewCreatedListener(OnViewCreatedListener)
 */
public final class Folivora {
  static final String TAG = "Folivora";

  // Drawable type enums, keep sync with app:drawableType
  private static final int DRAWABLE_TYPE_SHAPE = 0;
  private static final int DRAWABLE_TYPE_SELECTOR = 1;
  private static final int DRAWABLE_TYPE_LAYER = 2;
  private static final int DRAWABLE_TYPE_RIPPLE = 3;
  private static final int DRAWABLE_TYPE_LEVEL = 4;
  private static final int DRAWABLE_TYPE_CLIP = 5;
  private static final int DRAWABLE_TYPE_INSET = 6;
  private static final int DRAWABLE_TYPE_SCALE = 7;
  private static final int DRAWABLE_TYPE_ANIMATION = 8;

  // Set as enums, keep sync with app:setAs
  private static final int SET_AS_BACKGROUND = 0;
  private static final int SET_AS_SRC = 1;
  private static final int SET_AS_FOREGROUND = 2;

  // Exposed apis
  private static RippleFallback sRippleFallback;
  private static List<OnViewCreatedListener> sOnViewCreatedListeners;
  private static Map<String, DrawableParser> sDrawableParsers = new HashMap<>();
  private static DrawableParser sReflectiveDrawableParser = new DrawableParser.ReflectiveDrawableParser();
  private static SparseArray<String> sSystemDrawableNames = new SparseArray<>();

  static {
    sDrawableParsers.put("android.graphics.drawable.GradientDrawable", new DrawableParser.GradientDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.StateListDrawable", new DrawableParser.StateListDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.LayerDrawable", new DrawableParser.LayerDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.RippleDrawable", new DrawableParser.RippleDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.LevelListDrawable", new DrawableParser.LevelListDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.ClipDrawable", new DrawableParser.ClipDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.InsetDrawable", new DrawableParser.InsetDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.ScaleDrawable", new DrawableParser.ScaleDrawableParser());
    sDrawableParsers.put("android.graphics.drawable.AnimationDrawable", new DrawableParser.AnimationDrawableParser());

    sSystemDrawableNames.put(DRAWABLE_TYPE_SHAPE, "android.graphics.drawable.GradientDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_SELECTOR, "android.graphics.drawable.StateListDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_LAYER, "android.graphics.drawable.LayerDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_RIPPLE, "android.graphics.drawable.RippleDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_LEVEL, "android.graphics.drawable.LevelListDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_CLIP, "android.graphics.drawable.ClipDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_INSET, "android.graphics.drawable.InsetDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_SCALE, "android.graphics.drawable.ScaleDrawable");
    sSystemDrawableNames.put(DRAWABLE_TYPE_ANIMATION, "android.graphics.drawable.AnimationDrawable");

  }

  // Cached drawables with it's ids
  private static LruCache<String, Drawable> sDrawableCache = new LruCache<>(128);
  // Cache is enabled at runtime, but at design time, this should be disabled for work properly
  @SuppressWarnings("FieldCanBeLocal") // This is accessed by layout editor
  private static boolean sDrawableCacheEnabled = true;

  /**
   * Try to get a child drawable, if the attrIndex pointing to a specific drawable,
   * then load it in normal way, if the attrIndex pointing a enum(shape index), try
   * to create it from the given attrs
   *
   * @param ctx       current context
   * @param a         caller's typed array
   * @param attrs     attributes from view tag
   * @param attrIndex attribute index in the typed array
   * @return a drawable, or a newly created GradientDrawable from attrs, or null
   */
  @SuppressWarnings("WeakerAccess")
  public static Drawable getDrawable(Context ctx, TypedArray a, AttributeSet attrs, int attrIndex) {
    if (!a.hasValue(attrIndex)) return null;
    Drawable result = null;
    ShapeAttrs shapeAttrs = ShapeAttrs.forIndex(a.getInt(attrIndex, -1));
    if (shapeAttrs != null) {
      final String className = "android.graphics.drawable.GradientDrawable";
      DrawableParser parser = sDrawableParsers.get(className);
      ParseRequest request = new ParseRequest(ctx, attrs, sRippleFallback, shapeAttrs, className);
      if (parser != null) {
        result = parser.parse(request);
      }
    }

    if (result == null) {
      result = a.getDrawable(attrIndex);
    }
    return result;
  }

  /**
   * Create a drawable to the specific view with attrs, this method is
   * used by folivora internally, but in order to support preview for
   * the views folivora not stubbed, this method becomes publicly
   *
   * @param view  view of drawable attached
   * @param attrs attributes from view tag
   */
  static void applyDrawableToView(View view, AttributeSet attrs) {
    final Context ctx = view.getContext();
    // Step1 extract attrs
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora);
    int drawableType = a.getInt(R.styleable.Folivora_drawableType, -1);
    String drawableId = a.getString(R.styleable.Folivora_drawableId);
    String drawableName = a.getString(R.styleable.Folivora_drawableName);
    int setAs = a.getInt(R.styleable.Folivora_setAs, SET_AS_BACKGROUND);
    a.recycle();
    if (drawableType < 0 && drawableId == null && drawableName == null) return;
    // Step2 lookup cached if available
    Drawable cached = null;
    Drawable d = null;
    if (drawableId != null) {
      cached = sDrawableCache.get(drawableId);
      if (cached != null && cached.getConstantState() != null) {
        cached = cached.getConstantState().newDrawable();
      }
    }
    // Step3 try to create a new drawable and cached it
    if (!sDrawableCacheEnabled || cached == null) {
      d = createDrawable(ctx, attrs, drawableType, drawableName);
      if (d != null && d.getConstantState() != null && drawableId != null) {
        sDrawableCache.put(drawableId, d);
      }
    }
    d = d == null ? cached : d;
    if (d == null) return;
    // Step4 set drawable to view
    if (setAs == SET_AS_BACKGROUND) {
      view.setBackground(d);
    } else if (setAs == SET_AS_SRC && view instanceof ImageView) {
      ((ImageView) view).setImageDrawable(d);
    } else if (setAs == SET_AS_FOREGROUND) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        view.setForeground(d);
      } else if (view instanceof FrameLayout) {
        //noinspection RedundantCast
        ((FrameLayout) view).setForeground(d);
      } else {
        Log.w(TAG, "Folivora can not set foreground to [" + view.getClass()
          + "], Current device platform is lower than MarshMallow");
      }
    }
  }

  private static Drawable createDrawable(Context ctx, AttributeSet attrs, int drawableType, String drawableName) {
    String realDrawableName = null;
    if (drawableType >= 0) {
      realDrawableName = sSystemDrawableNames.get(drawableType);
    }
    if (realDrawableName == null) {
      realDrawableName = drawableName;
    }
    if (realDrawableName == null) return null;
    DrawableParser parser = sDrawableParsers.get(realDrawableName);
    if (parser == null && !realDrawableName.startsWith("android.graphics.drawable")) {
      parser = sReflectiveDrawableParser;
    }
    if (parser == null) return null;
    ParseRequest request = new ParseRequest(ctx, attrs, sRippleFallback, ShapeAttrs.forIndex(0), realDrawableName);
    return parser.parse(request);
  }

  /**
   * Install Folivora's ViewFactory to current context. note that if
   * you are using AppCompatActivity, this method should called after
   * your activity's super.onCreate() method, since AppCompatDelegate
   * will install a {@link LayoutInflater.Factory2} factory2 to this
   * context.
   *
   * @param ctx context to enable folivora support
   */
  public static void installViewFactory(Context ctx) {
    LayoutInflater inflater = LayoutInflater.from(ctx);
    LayoutInflater.Factory2 factory2 = inflater.getFactory2();
    if (factory2 instanceof FolivoraViewFactory) return;
    FolivoraViewFactory viewFactory = new FolivoraViewFactory();
    viewFactory.mFactory2 = factory2;
    if (factory2 != null) {
      FolivoraViewFactory.forceSetFactory2(inflater, viewFactory);
    } else {
      inflater.setFactory2(viewFactory);
    }
  }

  /**
   * Wraps the given context, replace the {@link LayoutInflater} inflater
   * to folivora's implementation, this method does nothing if the given
   * context is already been wrapped.
   *
   * @param newBase new base context
   * @return a wrapped context
   */
  public static Context wrap(final Context newBase) {
    final LayoutInflater inflater = LayoutInflater.from(newBase);
    if (inflater instanceof FolivoraInflater) return newBase;
    return new ContextWrapper(newBase) {
      private FolivoraInflater mInflater;

      @Override
      public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
          if (mInflater == null) {
            mInflater = new FolivoraInflater(newBase, inflater);
          }
          return mInflater;
        }
        return super.getSystemService(name);
      }
    };
  }

  /**
   * Set a fallback to create substitute drawable when the {@link RippleDrawable}
   * RippleDrawable is not available in current device.
   *
   * @param fallback a fallback to create drawable
   */
  public static void setRippleFallback(RippleFallback fallback) {
    sRippleFallback = fallback;
  }

  public static void registerDrawableParser(Class<? extends Drawable> drawableClass, DrawableParser parser) {
    final String className = drawableClass.getCanonicalName();
    sDrawableParsers.put(className, parser);
  }

  /**
   * Add a {@link OnViewCreatedListener} listener to folivora, folivora
   * will notify these listeners when a view is created. this listener
   * allows you do some extra customization about the inflated views.
   *
   * @param l listener to register
   */
  public static void addOnViewCreatedListener(OnViewCreatedListener l) {
    if (sOnViewCreatedListeners == null) {
      sOnViewCreatedListeners = new ArrayList<>();
    }
    sOnViewCreatedListeners.add(l);
  }

  static void dispatchViewCreated(View view, AttributeSet attrs) {
    if (sOnViewCreatedListeners != null) {
      for (OnViewCreatedListener l : sOnViewCreatedListeners) {
        l.onViewCreated(view, attrs);
      }
    }
  }

  private Folivora() {}
}
