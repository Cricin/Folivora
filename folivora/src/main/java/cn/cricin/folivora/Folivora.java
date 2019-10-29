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
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

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
 * @see #addDrawableFactory(DrawableFactory)
 * @see #getDrawable(Context, AttributeSet, TypedArray, int)
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

  /**
   * This class is designed for device platform lowers than
   * {@link Build.VERSION_CODES#LOLLIPOP} lollipop, which RippleDrawable
   * is unavailable, you can create substitution drawable here
   */
  public interface RippleFallback {
    /**
     * Called when the view want a RippleDrawable, but it is unavailable in
     * current device
     *
     * @param color   ripple color, nonnull
     * @param content content of ripple, nullable
     * @param mask    ripple mask, nullable
     * @param ctx     current context
     * @return a substitute drawable, or null
     */
    Drawable onFallback(ColorStateList color, Drawable content,
                        Drawable mask, Context ctx);
  }

  /**
   * A drawable factory takes responsibility of drawable creation
   * when the drawable name retrieved from view tag matches this
   * factory's {@link #drawableClass()} ()} drawableClass()
   *
   * note: this interface is deprecated and will be removed,
   * use DrawableParser instead
   *
   * @see Folivora#addDrawableFactory(DrawableFactory)
   */
  public interface DrawableFactory {
    /**
     * Create a new drawable instance from the given attrs, if the
     * specific drawable contains other drawables, note that you need
     * to use {@link Folivora#getDrawable(Context, AttributeSet, TypedArray, int)}
     * to get a drawable, which takes care of nested shape creation
     *
     * @param ctx   current context
     * @param attrs attrs in view tag
     * @return a newly created drawable, or null
     */
    Drawable newDrawable(Context ctx, AttributeSet attrs);

    /**
     * Returns drawable class this factory can handles
     *
     * @return a drawable class
     */
    Class<? extends Drawable> drawableClass();
  }

  /**
   * A listener notified when a view is been created. you can
   * do some customization on the view based on the attrs. eg,
   * change text or typeface of TextView or change src of ImageView
   */
  public interface OnViewCreatedListener {
    void onViewCreated(View view, AttributeSet attrs);
  }

  // Exposed apis
  private static RippleFallback sRippleFallback;
  private static List<DrawableFactory> sDrawableFactories;
  private static List<OnViewCreatedListener> sOnViewCreatedListeners;

  // Cached drawables with it's ids
  private static LruCache<String, Drawable> sDrawableCache = new LruCache<>(128);
  // Cache is enabled at runtime, but at design time, this should be disabled for work properly
  @SuppressWarnings("FieldCanBeLocal") // This is accessed by preview
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
  public static Drawable getDrawable(Context ctx, AttributeSet attrs, TypedArray a, int attrIndex) {
    if (!a.hasValue(attrIndex)) return null;
    final int shapeIndex = a.getInt(attrIndex, -1);
    DrawableParser parser = getDrawableParser(-1,
      shapeIndex, "", null, null);
    Drawable result = parser.parse(ctx, attrs);
    if(result == null) result = a.getDrawable(attrIndex);
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
      DrawableParser parser = getDrawableParser(drawableType, -1,
        drawableName, sDrawableFactories, sRippleFallback);
      d = parser.parse(ctx, attrs);
      if (d != null && d.getConstantState() != null && drawableId != null) {
        sDrawableCache.put(drawableId, d);
      }
    }
    d = d == null ? cached : d;
    if (d == null) return;
    // Step4 set drawable to view
    if(setAs == SET_AS_BACKGROUND){
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

  /**
   * Select suitable DrawableParser to parse a Drawable.
   * if no DrawableParser matched, a dumb instance is returned.
   */
  private static DrawableParser getDrawableParser(int drawableType,
                                                  int shapeIndex,
                                                  String drawableName,
                                                  List<Folivora.DrawableFactory> drawableFactories,
                                                  Folivora.RippleFallback rippleFallback) {
    ShapeAttrs shapeAttrs = ShapeAttrs.forIndex(shapeIndex);
    if (shapeAttrs != null) {
      return new DrawableParser.GradientDrawableParser(shapeAttrs);
    }
    if (drawableType >= 0) {
      switch (drawableType) {
        case DRAWABLE_TYPE_SHAPE:
          return new DrawableParser.GradientDrawableParser(ShapeAttrs.forIndex(0));
        case DRAWABLE_TYPE_SELECTOR:
          return new DrawableParser.StateListDrawableParser();
        case DRAWABLE_TYPE_LAYER:
          return new DrawableParser.LayerDrawableParser();
        case DRAWABLE_TYPE_RIPPLE:
          return new DrawableParser.RippleDrawableParser(rippleFallback);
        case DRAWABLE_TYPE_LEVEL:
          return new DrawableParser.LevelListDrawableParser();
        case DRAWABLE_TYPE_CLIP:
          return new DrawableParser.ClipDrawableParser();
        case DRAWABLE_TYPE_INSET:
          return new DrawableParser.InsetDrawableParser();
        case DRAWABLE_TYPE_SCALE:
          return new DrawableParser.ScaleDrawableParser();
        case DRAWABLE_TYPE_ANIMATION:
          return new DrawableParser.AnimationDrawableParser();
      }
    }
    if (drawableName != null && drawableName.length() > 0) {
      Folivora.DrawableFactory candidate = null;
      for (Folivora.DrawableFactory drawableFactory : drawableFactories) {
        if (drawableName.equals(drawableFactory.drawableClass().getCanonicalName())) {
          candidate = drawableFactory;
          break;
        }
      }
      if (candidate != null) {
        return new DrawableParser.DelegateDrawableParser(candidate);
      } else {
        return new DrawableParser.ReflectiveDrawableParser(drawableName);
      }
    }
    return new DrawableParser.NoopDrawableParser();
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
  @SuppressWarnings("WeakerAccess")
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
   * Set a fallback to create substitute drawable when the
   * {@link android.graphics.drawable.RippleDrawable} RippleDrawable
   * is not available in current device.
   * @param fallback a fallback to create drawable
   */
  public static void setRippleFallback(RippleFallback fallback) {
    sRippleFallback = fallback;
  }

  /**
   * Add a {@link DrawableFactory} factory to folivora, folivora
   * will create drawables from this factory if the drawableName
   * specified in view attrs equals with {@link DrawableFactory#drawableClass()}
   *
   * @param factory factory for create drawable
   */
  public static void addDrawableFactory(DrawableFactory factory) {
    if (sDrawableFactories == null) {
      sDrawableFactories = new ArrayList<>();
    }
    sDrawableFactories.add(factory);
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
