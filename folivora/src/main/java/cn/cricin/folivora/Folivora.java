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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * @see #getDrawable(Context, TypedArray, AttributeSet, int)
 * @see #addOnViewCreatedListener(OnViewCreatedListener)
 */
public final class Folivora {
  static final String TAG = "Folivora";

  private static final int DRAWABLE_TYPE_SHAPE = 0;
  private static final int DRAWABLE_TYPE_SELECTOR = 1;
  private static final int DRAWABLE_TYPE_LAYER = 2;
  private static final int DRAWABLE_TYPE_RIPPLE = 3;
  private static final int DRAWABLE_TYPE_LEVEL = 4;
  private static final int DRAWABLE_TYPE_CLIP = 5;
  private static final int DRAWABLE_TYPE_INSET = 6;
  private static final int DRAWABLE_TYPE_SCALE = 7;
  private static final int DRAWABLE_TYPE_ANIMATION = 8;

  private static final int SET_AS_BACKGROUND = 0;
  private static final int SET_AS_SRC = 1;
  private static final int SET_AS_FOREGROUND = 2;

  private static final int SHAPE_INDEX_0 = 0;
  private static final int SHAPE_INDEX_1 = 1;
  private static final int SHAPE_INDEX_2 = 2;
  private static final int SHAPE_INDEX_3 = 3;
  private static final int SHAPE_INDEX_4 = 4;

  private static final int[] STATE_FIRST = {android.R.attr.state_first};
  private static final int[] STATE_MIDDLE = {android.R.attr.state_middle};
  private static final int[] STATE_LAST = {android.R.attr.state_last};
  private static final int[] STATE_ACTIVE = {android.R.attr.state_active};
  private static final int[] STATE_ACTIVATED = {android.R.attr.state_activated};
  private static final int[] STATE_ACCELERATE = {android.R.attr.state_accelerated};
  private static final int[] STATE_CHECKED = {android.R.attr.state_checked};
  private static final int[] STATE_CHECKABLE = {android.R.attr.state_checkable};
  private static final int[] STATE_ENABLED = {android.R.attr.state_enabled};
  private static final int[] STATE_FOCUSED = {android.R.attr.state_focused};
  private static final int[] STATE_PRESSED = {android.R.attr.state_pressed};
  private static final int[] STATE_NORMAL = {};

  private static final int FIRST = 1;
  private static final int FIRST_NOT = 1 << 1;
  private static final int MIDDLE = 1 << 2;
  private static final int MIDDLE_NOT = 1 << 3;
  private static final int LAST = 1 << 4;
  private static final int LAST_NOT = 1 << 5;
  private static final int ACTIVE = 1 << 6;
  private static final int ACTIVE_NOT = 1 << 7;
  private static final int ACTIVATED = 1 << 8;
  private static final int ACTIVATED_NOT = 1 << 9;
  private static final int ACCELERATE = 1 << 10;
  private static final int ACCELERATE_NOT = 1 << 11;
  private static final int CHECKED = 1 << 12;
  private static final int CHECKED_NOT = 1 << 13;
  private static final int CHECKABLE = 1 << 14;
  private static final int CHECKABLE_NOT = 1 << 15;
  private static final int ENABLED = 1 << 16;
  private static final int ENABLED_NOT = 1 << 17;
  private static final int FOCUSED = 1 << 18;
  private static final int FOCUSED_NOT = 1 << 19;
  private static final int PRESSED = 1 << 20;
  private static final int PRESSED_NOT = 1 << 21;
  private static final int[] TEMP_STATE_SET = new int[11];

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
   * @see Folivora#addDrawableFactory(DrawableFactory)
   * @see Folivora#newDrawableFromFactory(String, Context, AttributeSet)
   */
  public interface DrawableFactory {
    /**
     * Create a new drawable instance from the given attrs, if the
     * specific drawable contains other drawables, note that you need
     * to use {@link Folivora#getDrawable(Context, TypedArray, AttributeSet, int)}
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

  private static RippleFallback sRippleFallback;
  private static List<DrawableFactory> sDrawableFactories;
  private static List<OnViewCreatedListener> sOnViewCreatedListeners;

  private static Class[] sConstructorSignature = {Context.class, AttributeSet.class};
  private static Object[] sConstructorArgs = new Object[2];
  private static Map<String, Constructor<? extends Drawable>> sConstructorCache = new HashMap<>();
  private static Set<String> sFailedNames = new HashSet<>();

  /**
   * Create a new GradientDrawable(shape).
   * attrs:
   * <p>
   * app:shapeType                    enum
   * app:shapeSolidSize               dimension
   * app:shapeSolidWidth              dimension
   * app:shapeSolidHeight             dimension
   * app:shapeSolidColor              color
   * <p>
   * app:shapeStrokeWidth             dimension
   * app:shapeStrokeColor             color
   * app:shapeStrokeDashGap           dimension
   * app:shapeStrokeDashWidth         dimension
   * <p>
   * app:shapeGradientType            enum
   * app:shapeGradientRadius          dimension
   * app:shapeGradientCenterX         dimension
   * app:shapeGradientCenterY         dimension
   * app:shapeGradientStartColor      color
   * app:shapeGradientCenterColor     color
   * app:shapeGradientEndColor        color
   * app:shapeGradientAngle           enum
   * <p>
   * app:shapeCornerRadius            dimension
   * app:shapeCornerRadiusTopLeft     dimension
   * app:shapeCornerRadiusTopRight    dimension
   * app:shapeCornerRadiusBottomLeft  dimension
   * app:shapeCornerRadiusBottomRight dimension
   */
  private static GradientDrawable newShape(Context ctx, AttributeSet attrs) {
    GradientDrawable gd = new GradientDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Shape);
    gd.setShape(a.getInt(R.styleable.Folivora_Shape_shapeType, GradientDrawable.RECTANGLE));

    final int size = a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeSolidSize, -1);
    gd.setSize(a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeSolidWidth, size),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeSolidHeight, size));

    gd.setGradientType(a.getInt(R.styleable.Folivora_Shape_shapeGradientType, 0));
    gd.setGradientRadius(a.getDimension(R.styleable.Folivora_Shape_shapeGradientRadius, 0));
    gd.setGradientCenter(a.getDimension(R.styleable.Folivora_Shape_shapeGradientCenterX, 0),
      a.getDimension(R.styleable.Folivora_Shape_shapeGradientCenterY, 0));
    gd.setColors(new int[]{
      a.getColor(R.styleable.Folivora_Shape_shapeGradientStartColor, 0),
      a.getColor(R.styleable.Folivora_Shape_shapeGradientCenterColor, 0),
      a.getColor(R.styleable.Folivora_Shape_shapeGradientEndColor, 0)
    });
    final int orientationIndex = a.getInt(R.styleable.Folivora_Shape_shapeGradientAngle, 0);
    gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

    if (a.hasValue(R.styleable.Folivora_Shape_shapeSolidColor)) {
      gd.setColor(a.getColor(R.styleable.Folivora_Shape_shapeSolidColor, Color.WHITE));
    }

    gd.setStroke(
      a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeStrokeWidth, -1),
      a.getColor(R.styleable.Folivora_Shape_shapeStrokeColor, Color.WHITE),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeStokeDashGap, 0),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape_shapeStokeDashWidth, 0)
    );
    final float radius = a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadius, 0);
    gd.setCornerRadii(new float[]{
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusBottomLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape_shapeCornerRadiusBottomLeft, radius),
    });
    a.recycle();
    return gd;
  }

  /**
   * Create a new StateListDrawable, only support single state match.
   * attrs:
   * <p>
   * app:selectorStateFirst       drawable
   * app:selectorStateMiddle      drawable
   * app:selectorStateLast        drawable
   * app:selectorStateActive      drawable
   * app:selectorStateActivate    drawable
   * app:selectorStateAccelerate  drawable
   * app:selectorStateChecked     drawable
   * app:selectorStateCheckable   drawable
   * app:selectorStateEnabled     drawable
   * app:selectorStateFocused     drawable
   * app:selectorStatePressed     drawable
   * app:selectorStateNormal      drawable
   *
   * note that if you want use more complicated state combination, use
   * <p>
   * selectorItemXXX instead.
   * app:selectorItem0States      flags
   * app:selectorItem0Drawable    drawable
   * ...
   */
  private static StateListDrawable newSelector(Context ctx, AttributeSet attrs) {
    StateListDrawable d = new StateListDrawable();
    Drawable temp;
    int[] states;
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Selector);
    states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem0States, 0));
    if (states != null && (temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorItem0Drawable)) != null) {
      d.addState(states, temp);
    }
    states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem1States, 0));
    if (states != null && (temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorItem1Drawable)) != null) {
      d.addState(states, temp);
    }
    states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem2States, 0));
    if (states != null && (temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorItem2Drawable)) != null) {
      d.addState(states, temp);
    }
    states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem3States, 0));
    if (states != null && (temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorItem3Drawable)) != null) {
      d.addState(states, temp);
    }
    states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem4States, 0));
    if (states != null && (temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorItem4Drawable)) != null) {
      d.addState(states, temp);
    }
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateFirst);
    if (temp != null) d.addState(STATE_FIRST, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateMiddle);
    if (temp != null) d.addState(STATE_MIDDLE, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateLast);
    if (temp != null) d.addState(STATE_LAST, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateActive);
    if (temp != null) d.addState(STATE_ACTIVE, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateActivated);
    if (temp != null) d.addState(STATE_ACTIVATED, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateAccelerate);
    if (temp != null) d.addState(STATE_ACCELERATE, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateChecked);
    if (temp != null) d.addState(STATE_CHECKED, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateCheckable);
    if (temp != null) d.addState(STATE_CHECKABLE, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateEnabled);
    if (temp != null) d.addState(STATE_ENABLED, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateFocused);
    if (temp != null) d.addState(STATE_FOCUSED, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStatePressed);
    if (temp != null) d.addState(STATE_PRESSED, temp);
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Selector_selectorStateNormal);
    if (temp != null) d.addState(STATE_NORMAL, temp);
    a.recycle();
    return d;
  }

  /**
   * Parse state sets from the stateFlags
   *
   * @param stateFlags the flags which states combined
   * @return state set or null if absent
   */
  private static int[] parseStateSet(int stateFlags) {
    if (stateFlags == 0) return null;
    int index = 0;
    if ((stateFlags & FIRST) == FIRST) {
      TEMP_STATE_SET[index++] = android.R.attr.state_first;
    }
    if ((stateFlags & FIRST_NOT) == FIRST_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_first;
    }
    if ((stateFlags & MIDDLE) == MIDDLE) {
      TEMP_STATE_SET[index++] = android.R.attr.state_middle;
    }
    if ((stateFlags & MIDDLE_NOT) == MIDDLE_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_middle;
    }
    if ((stateFlags & LAST) == LAST) {
      TEMP_STATE_SET[index++] = android.R.attr.state_last;
    }
    if ((stateFlags & LAST_NOT) == LAST_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_last;
    }
    if ((stateFlags & ACTIVE) == ACTIVE) {
      TEMP_STATE_SET[index++] = android.R.attr.state_active;
    }
    if ((stateFlags & ACTIVE_NOT) == ACTIVE_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_active;
    }
    if ((stateFlags & ACTIVATED) == ACTIVATED) {
      TEMP_STATE_SET[index++] = android.R.attr.state_activated;
    }
    if ((stateFlags & ACTIVATED_NOT) == ACTIVATED_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_activated;
    }
    if ((stateFlags & ACCELERATE) == ACCELERATE) {
      TEMP_STATE_SET[index++] = android.R.attr.state_accelerated;
    }
    if ((stateFlags & ACCELERATE_NOT) == ACCELERATE_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_accelerated;
    }
    if ((stateFlags & CHECKED) == CHECKED) {
      TEMP_STATE_SET[index++] = android.R.attr.state_checked;
    }
    if ((stateFlags & CHECKED_NOT) == CHECKED_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_checked;
    }
    if ((stateFlags & CHECKABLE) == CHECKABLE) {
      TEMP_STATE_SET[index++] = android.R.attr.state_checkable;
    }
    if ((stateFlags & CHECKABLE_NOT) == CHECKABLE_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_checkable;
    }
    if ((stateFlags & ENABLED) == ENABLED) {
      TEMP_STATE_SET[index++] = android.R.attr.state_enabled;
    }
    if ((stateFlags & ENABLED_NOT) == ENABLED_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_enabled;
    }
    if ((stateFlags & FOCUSED) == FOCUSED) {
      TEMP_STATE_SET[index++] = android.R.attr.state_focused;
    }
    if ((stateFlags & FOCUSED_NOT) == FOCUSED_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_focused;
    }
    if ((stateFlags & PRESSED) == PRESSED) {
      TEMP_STATE_SET[index++] = android.R.attr.state_pressed;
    }
    if ((stateFlags & PRESSED_NOT) == PRESSED_NOT) {
      TEMP_STATE_SET[index++] = -android.R.attr.state_pressed;
    }
    if (index == 0) return null;
    return StateSet.trimStateSet(TEMP_STATE_SET, index);
  }

  /**
   * Create a new LayerDrawable, currently we only support 5 children drawables.
   * attrs:
   * <p>
   * app:layerItem0Drawable       drawable
   * app:layerItem0Insets         dimension
   * app:layerItem0Left           dimension
   * app:layerItem0Top            dimension
   * app:layerItem0Right          dimension
   * app:layerItem0Bottom         dimension
   * <p>
   * app:layerItem1Drawable       drawable
   * app:layerItem1Insets         dimension
   * app:layerItem1Left           dimension
   * app:layerItem1Top            dimension
   * app:layerItem1Right          dimension
   * app:layerItem1Bottom         dimension
   * <p>
   * app:layerItem2Drawable       drawable
   * app:layerItem2Insets         dimension
   * app:layerItem2Left           dimension
   * app:layerItem2Top            dimension
   * app:layerItem2Right          dimension
   * app:layerItem2Bottom         dimension
   * <p>
   * app:layerItem3Drawable       drawable
   * app:layerItem3Insets         dimension
   * app:layerItem3Left           dimension
   * app:layerItem3Top            dimension
   * app:layerItem3Right          dimension
   * app:layerItem3Bottom         dimension
   * <p>
   * app:layerItem4Drawable       drawable
   * app:layerItem4Insets         dimension
   * app:layerItem4Left           dimension
   * app:layerItem4Top            dimension
   * app:layerItem4Right          dimension
   * app:layerItem4Bottom         dimension
   */
  private static LayerDrawable newLayer(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Layer);
    List<Drawable> childDrawables = new ArrayList<>(5);
    List<Rect> childInsets = new ArrayList<>(5);

    Drawable temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Layer_layerItem0Drawable);
    if (temp != null) {
      childDrawables.add(temp);
      int insets = a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem0Insets, 0);
      childInsets.add(new Rect(
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem0Left, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem0Top, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem0Right, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem0Bottom, insets)
      ));
    }
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Layer_layerItem1Drawable);
    if (temp != null) {
      childDrawables.add(temp);
      int insets = a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem1Insets, 0);
      childInsets.add(new Rect(
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem1Left, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem1Top, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem1Right, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem1Bottom, insets)
      ));
    }
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Layer_layerItem2Drawable);
    if (temp != null) {
      childDrawables.add(temp);
      int insets = a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem2Insets, 0);
      childInsets.add(new Rect(
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem2Left, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem2Top, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem2Right, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem2Bottom, insets)
      ));
    }
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Layer_layerItem3Drawable);
    if (temp != null) {
      childDrawables.add(temp);
      int insets = a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem3Insets, 0);
      childInsets.add(new Rect(
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem3Left, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem3Top, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem3Right, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem3Bottom, insets)
      ));
    }
    temp = getDrawable(ctx, a, attrs, R.styleable.Folivora_Layer_layerItem4Drawable);
    if (temp != null) {
      childDrawables.add(temp);
      int insets = a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem4Insets, 0);
      childInsets.add(new Rect(
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem4Left, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem4Top, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem4Right, insets),
        a.getDimensionPixelSize(R.styleable.Folivora_Layer_layerItem4Bottom, insets)
      ));
    }
    a.recycle();
    LayerDrawable d = new LayerDrawable(childDrawables.toArray(new Drawable[0]));
    for (int i = 0; i < childInsets.size(); i++) {
      Rect inset = childInsets.get(i);
      d.setLayerInset(i, inset.left, inset.top, inset.right, inset.bottom);
    }
    return d;
  }

  /**
   * Create a new RippleDrawable, if current platform does not support,
   * we will try to create a substitute from {@link RippleFallback} rippleFallback.
   * attrs:
   * <p>
   * app:rippleColor              color
   * app:rippleContent            drawable
   * app:rippleMask               drawable
   */
  private static Drawable newRipple(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Ripple);
    ColorStateList color;
    Drawable content;
    Drawable mask;
    try {
      color = a.getColorStateList(R.styleable.Folivora_Ripple_rippleColor);
      if (color == null) {
        throw new IllegalStateException("rippleColor not set");
      }
      content = getDrawable(ctx, a, attrs, R.styleable.Folivora_Ripple_rippleContent);
      mask = getDrawable(ctx, a, attrs, R.styleable.Folivora_Ripple_rippleMask);
    } finally {
      a.recycle();
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      return new RippleDrawable(color, content, mask);
    } else if (sRippleFallback != null) {
      return sRippleFallback.onFallback(color, content, mask, ctx);
    } else {
      Log.w(TAG, "RippleDrawable is not available in current platform");
      return null;
    }
  }

  /**
   * Create a new LevelListDrawable, currently we only support 5 children drawables.
   * attrs:
   * <p>
   * app:levelCurrentLevel        int
   * app:levelItem0Drawable       drawable
   * app:levelItem0MinLevel       int
   * app:levelItem0MaxLevel       int
   * app:levelItem1Drawable       drawable
   * app:levelItem1MinLevel       int
   * app:levelItem1MaxLevel       int
   * app:levelItem2Drawable       drawable
   * app:levelItem2MinLevel       int
   * app:levelItem2MaxLevel       int
   * app:levelItem3Drawable       drawable
   * app:levelItem3MinLevel       int
   * app:levelItem3MaxLevel       int
   * app:levelItem4Drawable       drawable
   * app:levelItem4MinLevel       int
   * app:levelItem4MaxLevel       int
   */
  private static LevelListDrawable newLevel(Context ctx, AttributeSet attrs) {
    LevelListDrawable lld = new LevelListDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Level);
    if (a.hasValue(R.styleable.Folivora_Level_levelItem0Drawable)) {
      lld.addLevel(
        a.getInt(R.styleable.Folivora_Level_levelItem0MinLevel, 0),
        a.getInt(R.styleable.Folivora_Level_levelItem0MaxLevel, 0),
        getDrawable(ctx, a, attrs, R.styleable.Folivora_Level_levelItem0Drawable)
      );
    }
    if (a.hasValue(R.styleable.Folivora_Level_levelItem1Drawable)) {
      lld.addLevel(
        a.getInt(R.styleable.Folivora_Level_levelItem1MinLevel, 0),
        a.getInt(R.styleable.Folivora_Level_levelItem1MaxLevel, 0),
        getDrawable(ctx, a, attrs, R.styleable.Folivora_Level_levelItem1Drawable)
      );
    }
    if (a.hasValue(R.styleable.Folivora_Level_levelItem2Drawable)) {
      lld.addLevel(
        a.getInt(R.styleable.Folivora_Level_levelItem2MinLevel, 0),
        a.getInt(R.styleable.Folivora_Level_levelItem2MaxLevel, 0),
        getDrawable(ctx, a, attrs, R.styleable.Folivora_Level_levelItem2Drawable)
      );
    }
    if (a.hasValue(R.styleable.Folivora_Level_levelItem3Drawable)) {
      lld.addLevel(
        a.getInt(R.styleable.Folivora_Level_levelItem3MinLevel, 0),
        a.getInt(R.styleable.Folivora_Level_levelItem3MaxLevel, 0),
        getDrawable(ctx, a, attrs, R.styleable.Folivora_Level_levelItem3Drawable)
      );
    }
    if (a.hasValue(R.styleable.Folivora_Level_levelItem4Drawable)) {
      lld.addLevel(
        a.getInt(R.styleable.Folivora_Level_levelItem4MinLevel, 0),
        a.getInt(R.styleable.Folivora_Level_levelItem4MaxLevel, 0),
        getDrawable(ctx, a, attrs, R.styleable.Folivora_Level_levelItem4Drawable)
      );
    }
    lld.setLevel(a.getInt(R.styleable.Folivora_Level_levelCurrentLevel, 0));
    a.recycle();
    return lld;
  }

  /**
   * Create a new ClipDrawable.
   * attrs:
   * <p>
   * app:clipDrawable             drawable
   * app:clipGravity              enum
   * app:clipOrientation          enum
   * app:clipLevel                int(10000 means no clip)
   */
  private static ClipDrawable newClip(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Clip);
    final Drawable child = getDrawable(ctx, a, attrs, R.styleable.Folivora_Clip_clipDrawable);
    final int clipGravity = a.getInt(R.styleable.Folivora_Clip_clipGravity, Gravity.START);
    final int clipOrientation = a.getInt(R.styleable.Folivora_Clip_clipOrientation, ClipDrawable.HORIZONTAL);

    ClipDrawable cd = new ClipDrawable(child, clipGravity, clipOrientation);
    cd.setLevel(a.getInt(R.styleable.Folivora_Clip_clipLevel, 10000/*no clip*/));
    a.recycle();
    return cd;
  }

  /**
   * Create a new InsetDrawable, inset by fraction is not support, since it is an new api.
   * attrs:
   * <p>
   * app:insetDrawable            drawable
   * app:insetAll                 dimension
   * app:insetLeft                dimension
   * app:insetTop                 dimension
   * app:insetRight               dimension
   * app:insetBottom              dimension
   */
  private static InsetDrawable newInset(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Inset);
    int insetAll = a.getDimensionPixelSize(R.styleable.Folivora_Inset_insetAll, 0);
    int[] ints = {
      R.styleable.Folivora_Inset_insetLeft,
      R.styleable.Folivora_Inset_insetTop,
      R.styleable.Folivora_Inset_insetRight,
      R.styleable.Folivora_Inset_insetBottom
    };
    for (int i = 0; i < ints.length; i++) {
      ints[i] = a.getDimensionPixelSize(ints[i], insetAll);
    }
    final Drawable child = getDrawable(ctx, a, attrs, R.styleable.Folivora_Inset_insetDrawable);
    a.recycle();
    return new InsetDrawable(child, ints[0], ints[1], ints[2], ints[3]);
  }

  /**
   * Create a new ScaleDrawable.
   * attrs:
   * <p>
   * app:scaleDrawable            drawable
   * app:scaleGravity             enum
   * app:scaleWidth               float
   * app:scaleHeight              float
   * app:scaleLevel               int
   */
  private static ScaleDrawable newScale(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Scale);
    ScaleDrawable sd = new ScaleDrawable(
      getDrawable(ctx, a, attrs, R.styleable.Folivora_Scale_scaleDrawable),
      a.getInt(R.styleable.Folivora_Scale_scaleGravity, Gravity.START),
      a.getFloat(R.styleable.Folivora_Scale_scaleWidth, -1F),
      a.getFloat(R.styleable.Folivora_Scale_scaleHeight, -1F)
    );
    sd.setLevel(a.getInt(R.styleable.Folivora_Scale_scaleLevel, 1));
    a.recycle();
    return sd;
  }

  /**
   * Create a new AnimationDrawable, currently we only support 10 frames.
   * attrs:
   * <p>
   * app:animAutoPlay             boolean
   * app:animDuration             int(millisecond)
   * app:animOneShot              boolean
   * <p>
   * app:animFrame0               drawable
   * app:animDuration0            int(millisecond)
   * app:animFrame1               drawable
   * app:animDuration1            int(millisecond)
   * app:animFrame2               drawable
   * app:animDuration2            int(millisecond)
   * app:animFrame3               drawable
   * app:animDuration3            int(millisecond)
   * app:animFrame4               drawable
   * app:animDuration4            int(millisecond)
   * app:animFrame5               drawable
   * app:animDuration5            int(millisecond)
   * app:animFrame6               drawable
   * app:animDuration6            int(millisecond)
   * app:animFrame7               drawable
   * app:animDuration7            int(millisecond)
   * app:animFrame8               drawable
   * app:animDuration8            int(millisecond)
   * app:animFrame9               drawable
   * app:animDuration9            int(millisecond)
   */
  @SuppressWarnings("ConstantConditions")
  private static AnimationDrawable newAnimation(Context ctx, AttributeSet attrs) {
    AnimationDrawable ad = new AnimationDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Animation);
    final boolean autoPlay = a.getBoolean(R.styleable.Folivora_Animation_animAutoPlay, false);
    final int frameDuration = a.getInt(R.styleable.Folivora_Animation_animDuration, -1);
    ad.setOneShot(a.getBoolean(R.styleable.Folivora_Animation_animOneShot, false));

    if (a.hasValue(R.styleable.Folivora_Animation_animFrame0)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame0),
        a.getInt(R.styleable.Folivora_Animation_animDuration0, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame1)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame1),
        a.getInt(R.styleable.Folivora_Animation_animDuration1, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame2)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame2),
        a.getInt(R.styleable.Folivora_Animation_animDuration2, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame3)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame3),
        a.getInt(R.styleable.Folivora_Animation_animDuration3, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame4)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame4),
        a.getInt(R.styleable.Folivora_Animation_animDuration4, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame5)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame5),
        a.getInt(R.styleable.Folivora_Animation_animDuration5, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame6)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame6),
        a.getInt(R.styleable.Folivora_Animation_animDuration6, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame7)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame7),
        a.getInt(R.styleable.Folivora_Animation_animDuration7, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame8)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame8),
        a.getInt(R.styleable.Folivora_Animation_animDuration8, frameDuration));
    }
    if (a.hasValue(R.styleable.Folivora_Animation_animFrame9)) {
      ad.addFrame(a.getDrawable(R.styleable.Folivora_Animation_animFrame9),
        a.getInt(R.styleable.Folivora_Animation_animDuration9, frameDuration));
    }
    a.recycle();
    if (autoPlay) {
      if (ad.isOneShot()) {
        Log.i(TAG, "Auto play and oneshot both enabled, you could not see the animation");
      }
      ad.start();
    }
    return ad;
  }

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
    final int shapeIndex = a.getInt(attrIndex, -1);
    switch (shapeIndex) {
      case -1://not used
        break;
      case SHAPE_INDEX_0:
        result = newShape(ctx, attrs);
        break;
      case SHAPE_INDEX_1:
        result = FolivoraShapeFactory.newShape1(ctx, attrs);
        break;
      case SHAPE_INDEX_2:
        result = FolivoraShapeFactory.newShape2(ctx, attrs);
        break;
      case SHAPE_INDEX_3:
        result = FolivoraShapeFactory.newShape3(ctx, attrs);
        break;
      case SHAPE_INDEX_4:
        result = FolivoraShapeFactory.newShape4(ctx, attrs);
      default:
        Log.w(TAG, "Unexpected shape index" + shapeIndex);
        break;
    }
    if (result == null) {
      result = a.getDrawable(attrIndex);
    }
    return result;
  }

  /**
   * Create a new drawable by the {@code drawableType} and using the {@code attrs} customize it.
   *
   * @param drawableType the type of drawable to create
   * @param ctx          current context
   * @param attrs        attributes from view tag
   * @return a newly created drawable, or null
   */
  private static Drawable newDrawable(int drawableType, Context ctx, AttributeSet attrs) {
    Drawable result = null;
    switch (drawableType) {
      case -1://not used
        break;
      case DRAWABLE_TYPE_SHAPE:
        result = newShape(ctx, attrs);
        break;
      case DRAWABLE_TYPE_SELECTOR:
        result = newSelector(ctx, attrs);
        break;
      case DRAWABLE_TYPE_LAYER:
        result = newLayer(ctx, attrs);
        break;
      case DRAWABLE_TYPE_RIPPLE:
        result = newRipple(ctx, attrs);
        break;
      case DRAWABLE_TYPE_LEVEL:
        result = newLevel(ctx, attrs);
        break;
      case DRAWABLE_TYPE_CLIP:
        result = newClip(ctx, attrs);
        break;
      case DRAWABLE_TYPE_INSET:
        result = newInset(ctx, attrs);
        break;
      case DRAWABLE_TYPE_SCALE:
        result = newScale(ctx, attrs);
        break;
      case DRAWABLE_TYPE_ANIMATION:
        result = newAnimation(ctx, attrs);
        break;
      default:
        Log.w(TAG, "Unexpected drawableType: " + drawableType);
        break;
    }
    return result;
  }

  /**
   * Try to create a custom drawable from the given class name, note
   * that the custom drawable class must have a public constructor
   * that takes a {@link Context} context and a {@link AttributeSet}
   * attrs as parameters, if it is difficult to make this constructor,
   * you can supply a {@link DrawableFactory} drawable factory to
   * folivora instead, which can receive attributes to create and
   * configure the drawable manually.
   *
   * @param drawableName full qualified drawable name
   * @param ctx          current context
   * @param attrs        attributes from view tag
   * @return a newly created drawable, or null
   */
  private static Drawable newCustomDrawable(String drawableName, Context ctx, AttributeSet attrs) {
    if (sFailedNames.contains(drawableName)) return null;
    if (drawableName.indexOf('.') == -1) return null;
    Constructor<? extends Drawable> constructor = sConstructorCache.get(drawableName);

    try {
      if (constructor == null) {
        Class<? extends Drawable> clazz = ctx.getClassLoader().loadClass(drawableName)
          .asSubclass(Drawable.class);
        constructor = clazz.getConstructor(sConstructorSignature);
        constructor.setAccessible(true);
        sConstructorCache.put(drawableName, constructor);
      }
      sConstructorArgs[0] = ctx;
      sConstructorArgs[1] = attrs;
      return constructor.newInstance(sConstructorArgs);
    } catch (ClassNotFoundException cnfe) {
      sFailedNames.add(drawableName);
      Log.w(TAG, "drawable class [" + drawableName
        + "] not found, Folivora will never try to load it any more");
    } catch (NoSuchMethodException nsme) {
      sFailedNames.add(drawableName);
      final String classSimpleName = drawableName.substring(drawableName.lastIndexOf('.') + 1);
      final String msg = "constructor " + classSimpleName + "(Context context, AttributeSet attrs)"
        + " does not exists in drawable class [" + drawableName + "], Folivora will never try to" +
        " load it any more";
      Log.w(TAG, msg);
    } catch (IllegalAccessException iae) {
      throw new AssertionError(iae);//never happen
    } catch (Exception e) {
      Log.w(TAG, "exception occurred instantiating drawable [" + drawableName + "]", e);
    } finally {
      sConstructorArgs[0] = null;
      sConstructorArgs[1] = null;
    }

    return null;
  }

  /**
   * Create a new drawable by the {@link DrawableFactory} with the specific
   * drawableType and using the {@code attrs} customize it.
   *
   * @param drawableName a drawableName used to find suitable DrawableFactory
   * @param ctx          current context
   * @param attrs        attributes from view tag
   * @return a newly created drawable, or null
   */
  private static Drawable newDrawableFromFactory(String drawableName, Context ctx, AttributeSet attrs) {
    if (sDrawableFactories == null) return null;
    for (DrawableFactory creator : sDrawableFactories) {
      if (drawableName.equals(creator.drawableClass().getCanonicalName())) {
        return creator.newDrawable(ctx, attrs);
      }
    }
    return null;
  }

  /**
   * Create a drawable to the specific view with attrs, this method is
   * used by folivora internally, but in order to support preview for
   * the views folivora not stubbed, this method becomes publicly
   *
   * @param view  view of drawable attached
   * @param attrs attributes from view tag
   */
  public static void applyDrawableToView(View view, AttributeSet attrs) {
    final Context ctx = view.getContext();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora);

    int drawableType = a.getInt(R.styleable.Folivora_drawableType, -1);
    String drawableName = a.getString(R.styleable.Folivora_drawableName);
    int setAs = a.getInt(R.styleable.Folivora_setAs, SET_AS_BACKGROUND);
    a.recycle();
    if ((drawableType < 0 && drawableName == null)) return;

    Drawable d = null;
    if (drawableType >= 0) {
      d = newDrawable(drawableType, ctx, attrs);
    }
    if (d == null && drawableName != null) {
      d = newDrawableFromFactory(drawableName, ctx, attrs);
      if (d == null) {
        d = newCustomDrawable(drawableName, ctx, attrs);
      }
    }
    if (d == null) return;
    if (setAs == SET_AS_SRC && view instanceof ImageView) {
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
    } else {
      view.setBackground(d);
    }
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
