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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.cricin.folivora.Folivora.TAG;
import static cn.cricin.folivora.Folivora.getDrawable;

/**
 * A DrawableParser take responsibility of drawable parsing, Folivora
 * will find best match DrawableParser to parse a drawable instance
 * for view. each subclass handles one or more drawable parsing when
 * a view want a drawable to decorate self.
 */
public interface DrawableParser {

  /**
   * Entry point for subclass to accomplish drawable parsing task
   * @param ctx current parsing context
   * @param attrs attribute retrieved from xml tag
   * @return a parsed drawable instance, or null
   */
  Drawable parse(Context ctx, AttributeSet attrs);

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
  class GradientDrawableParser implements DrawableParser {
    private final ShapeAttrs mShapeAttrs;

    GradientDrawableParser(ShapeAttrs shapeAttrs) {
      this.mShapeAttrs = shapeAttrs;
    }

    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      GradientDrawable gd = new GradientDrawable();
      TypedArray a = ctx.obtainStyledAttributes(attrs, mShapeAttrs.mAttrStyleable);
      gd.setShape(a.getInt(mShapeAttrs.mShapeType, GradientDrawable.RECTANGLE));

      final int size = a.getDimensionPixelSize(mShapeAttrs.mShapeSolidSize, -1);
      gd.setSize(a.getDimensionPixelSize(mShapeAttrs.mShapeSolidWidth, size),
        a.getDimensionPixelSize(mShapeAttrs.mShapeSolidHeight, size));

      gd.setGradientType(a.getInt(mShapeAttrs.mShapeGradientType, GradientDrawable.LINEAR_GRADIENT));
      gd.setGradientRadius(a.getDimension(mShapeAttrs.mShapeGradientRadius, 0));
      gd.setGradientCenter(a.getFloat(mShapeAttrs.mShapeGradientCenterX, 0.5F),
        a.getFloat(mShapeAttrs.mShapeGradientCenterY, 0.5F));
      int[] gradientColors;
      final int gradientStartColor = a.getColor(mShapeAttrs.mShapeGradientStartColor, 0);
      final int gradientEndColor = a.getColor(mShapeAttrs.mShapeGradientEndColor, 0);
      final int gradientCenterColor = a.getColor(mShapeAttrs.mShapeGradientCenterColor, 0);
      if (a.hasValue(mShapeAttrs.mShapeGradientCenterColor)) {
        gradientColors = new int[]{gradientStartColor, gradientCenterColor, gradientEndColor};
      } else {
        gradientColors = new int[]{gradientStartColor, gradientEndColor};
      }
      gd.setColors(gradientColors);
      final int orientationIndex = a.getInt(mShapeAttrs.mShapeGradientAngle, 0);
      gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

      if (a.hasValue(mShapeAttrs.mShapeSolidColor)) {
        gd.setColor(a.getColor(mShapeAttrs.mShapeSolidColor, Color.WHITE));
      }

      gd.setStroke(
        a.getDimensionPixelSize(mShapeAttrs.mShapeStrokeWidth, -1),
        a.getColor(mShapeAttrs.mShapeStrokeColor, Color.WHITE),
        a.getDimensionPixelSize(mShapeAttrs.mShapeStrokeDashGap, 0),
        a.getDimensionPixelSize(mShapeAttrs.mShapeStrokeDashWidth, 0)
      );
      final float radius = a.getDimension(mShapeAttrs.mShapeCornerRadius, 0);
      gd.setCornerRadii(new float[]{
        a.getDimension(mShapeAttrs.mShapeCornerRadiusTopLeft, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusTopLeft, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusTopRight, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusTopRight, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusBottomRight, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusBottomRight, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusBottomLeft, radius),
        a.getDimension(mShapeAttrs.mShapeCornerRadiusBottomLeft, radius),
      });
      a.recycle();
      return gd;
    }
  }

  /**
   * Create a new StateListDrawable, only support single state match.
   * attrs:
   * <p>
   * app:selectorStateFirst          drawable
   * app:selectorStateMiddle         drawable
   * app:selectorStateLast           drawable
   * app:selectorStateActive         drawable
   * app:selectorStateActivate       drawable
   * app:selectorStateAccelerate     drawable
   * app:selectorStateChecked        drawable
   * app:selectorStateCheckable      drawable
   * app:selectorStateEnabled        drawable
   * app:selectorStateFocused        drawable
   * app:selectorStatePressed        drawable
   * app:selectorStateNormal         drawable
   * app:selectorStateSelected       drawable
   * app:selectorStateSingle         drawable
   * app:selectorStateHovered        drawable
   * app:selectorStateWindowFocused  drawable
   *
   * note that if you want use more complicated state combination, use
   * <p>
   * selectorItemXXX instead.
   * app:selectorItemXStates         flags
   * app:selectorItemXDrawable       drawable
   * ...
   */
  class StateListDrawableParser implements DrawableParser {
    // Single state constants of view, keep sync with Folivora_Selector attrs
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
    private static final int[] STATE_SELECTED = {android.R.attr.state_selected};
    private static final int[] STATE_SINGLE = {android.R.attr.state_single};
    private static final int[] STATE_HOVERED = {android.R.attr.state_hovered};
    private static final int[] STATE_WINDOW_FOCUSED = {android.R.attr.state_window_focused};
    // Should we support these state?
    //private static final int[] STATE_DRAG_HOVERED = {android.R.attr.state_drag_hovered};
    //private static final int[] STATE_DRAG_CAN_ACCEPT = {android.R.attr.state_drag_can_accept};
    private static final int[] STATE_NORMAL = {};

    // State flags
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
    private static final int SELECTED = 1 << 22;
    private static final int SELECTED_NOT = 1 << 23;
    private static final int SINGLE = 1 << 24;
    private static final int SINGLE_NOT = 1 << 25;
    private static final int HOVERED = 1 << 26;
    private static final int HOVERED_NOT = 1 << 27;
    private static final int WINDOW_FOCUSED = 1 << 28;
    private static final int WINDOW_FOCUSED_NOT = 1 << 29;
    private static final int[] TEMP_STATE_SET = new int[15];

    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      StateListDrawable d = new StateListDrawable();
      Drawable temp;
      int[] states;
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Selector);
      states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem0States, 0));
      if (states != null && (temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorItem0Drawable)) != null) {
        d.addState(states, temp);
      }
      states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem1States, 0));
      if (states != null && (temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorItem1Drawable)) != null) {
        d.addState(states, temp);
      }
      states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem2States, 0));
      if (states != null && (temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorItem2Drawable)) != null) {
        d.addState(states, temp);
      }
      states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem3States, 0));
      if (states != null && (temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorItem3Drawable)) != null) {
        d.addState(states, temp);
      }
      states = parseStateSet(a.getInt(R.styleable.Folivora_Selector_selectorItem4States, 0));
      if (states != null && (temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorItem4Drawable)) != null) {
        d.addState(states, temp);
      }
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateFirst);
      if (temp != null) d.addState(STATE_FIRST, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateMiddle);
      if (temp != null) d.addState(STATE_MIDDLE, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateLast);
      if (temp != null) d.addState(STATE_LAST, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateActive);
      if (temp != null) d.addState(STATE_ACTIVE, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateActivated);
      if (temp != null) d.addState(STATE_ACTIVATED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateAccelerate);
      if (temp != null) d.addState(STATE_ACCELERATE, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateChecked);
      if (temp != null) d.addState(STATE_CHECKED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateCheckable);
      if (temp != null) d.addState(STATE_CHECKABLE, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateEnabled);
      if (temp != null) d.addState(STATE_ENABLED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateFocused);
      if (temp != null) d.addState(STATE_FOCUSED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStatePressed);
      if (temp != null) d.addState(STATE_PRESSED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateSelected);
      if (temp != null) d.addState(STATE_SELECTED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateSingle);
      if (temp != null) d.addState(STATE_SINGLE, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateHovered);
      if (temp != null) d.addState(STATE_HOVERED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateWindowFocused);
      if (temp != null) d.addState(STATE_WINDOW_FOCUSED, temp);
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Selector_selectorStateNormal);
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
      if ((stateFlags & SELECTED) == SELECTED) {
        TEMP_STATE_SET[index++] = android.R.attr.state_selected;
      }
      if ((stateFlags & SELECTED_NOT) == SELECTED_NOT) {
        TEMP_STATE_SET[index++] = -android.R.attr.state_selected;
      }
      if ((stateFlags & SINGLE) == SINGLE) {
        TEMP_STATE_SET[index++] = android.R.attr.state_single;
      }
      if ((stateFlags & SINGLE_NOT) == SINGLE_NOT) {
        TEMP_STATE_SET[index++] = -android.R.attr.state_single;
      }
      if ((stateFlags & HOVERED) == HOVERED) {
        TEMP_STATE_SET[index++] = android.R.attr.state_hovered;
      }
      if ((stateFlags & HOVERED_NOT) == HOVERED_NOT) {
        TEMP_STATE_SET[index++] = -android.R.attr.state_hovered;
      }
      if ((stateFlags & WINDOW_FOCUSED) == WINDOW_FOCUSED) {
        TEMP_STATE_SET[index++] = android.R.attr.state_window_focused;
      }
      if ((stateFlags & WINDOW_FOCUSED_NOT) == WINDOW_FOCUSED_NOT) {
        TEMP_STATE_SET[index++] = -android.R.attr.state_window_focused;
      }
      if (index == 0) return null;
      return StateSet.trimStateSet(TEMP_STATE_SET, index);
    }
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
  class LayerDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Layer);
      List<Drawable> childDrawables = new ArrayList<>(5);
      List<Rect> childInsets = new ArrayList<>(5);

      Drawable temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Layer_layerItem0Drawable);
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
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Layer_layerItem1Drawable);
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
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Layer_layerItem2Drawable);
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
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Layer_layerItem3Drawable);
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
      temp = getDrawable(ctx, attrs, a, R.styleable.Folivora_Layer_layerItem4Drawable);
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
  }

  /**
   * Create a new RippleDrawable, if current platform does not support,
   * we will try to create a substitute from {@link Folivora.RippleFallback} rippleFallback.
   * attrs:
   * <p>
   * app:rippleColor              color
   * app:rippleContent            drawable
   * app:rippleMask               drawable
   */
  class RippleDrawableParser implements DrawableParser {
    private Folivora.RippleFallback mRippleFallback;

    RippleDrawableParser(Folivora.RippleFallback rippleFallback) {
      this.mRippleFallback = rippleFallback;
    }

    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Ripple);
      ColorStateList color;
      Drawable content;
      Drawable mask;
      try {
        color = a.getColorStateList(R.styleable.Folivora_Ripple_rippleColor);
        if (color == null) {
          throw new IllegalStateException("rippleColor not set");
        }
        content = getDrawable(ctx, attrs, a, R.styleable.Folivora_Ripple_rippleContent);
        mask = getDrawable(ctx, attrs, a, R.styleable.Folivora_Ripple_rippleMask);
      } finally {
        a.recycle();
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        return new RippleDrawable(color, content, mask);
      } else if (mRippleFallback != null) {
        return mRippleFallback.onFallback(color, content, mask, ctx);
      } else {
        Log.w(TAG, "RippleDrawable is not available in current platform");
        return null;
      }
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
  class LevelListDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      LevelListDrawable lld = new LevelListDrawable();
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Level);
      if (a.hasValue(R.styleable.Folivora_Level_levelItem0Drawable)) {
        lld.addLevel(
          a.getInt(R.styleable.Folivora_Level_levelItem0MinLevel, 0),
          a.getInt(R.styleable.Folivora_Level_levelItem0MaxLevel, 0),
          getDrawable(ctx, attrs, a, R.styleable.Folivora_Level_levelItem0Drawable)
        );
      }
      if (a.hasValue(R.styleable.Folivora_Level_levelItem1Drawable)) {
        lld.addLevel(
          a.getInt(R.styleable.Folivora_Level_levelItem1MinLevel, 0),
          a.getInt(R.styleable.Folivora_Level_levelItem1MaxLevel, 0),
          getDrawable(ctx, attrs, a, R.styleable.Folivora_Level_levelItem1Drawable)
        );
      }
      if (a.hasValue(R.styleable.Folivora_Level_levelItem2Drawable)) {
        lld.addLevel(
          a.getInt(R.styleable.Folivora_Level_levelItem2MinLevel, 0),
          a.getInt(R.styleable.Folivora_Level_levelItem2MaxLevel, 0),
          getDrawable(ctx, attrs, a, R.styleable.Folivora_Level_levelItem2Drawable)
        );
      }
      if (a.hasValue(R.styleable.Folivora_Level_levelItem3Drawable)) {
        lld.addLevel(
          a.getInt(R.styleable.Folivora_Level_levelItem3MinLevel, 0),
          a.getInt(R.styleable.Folivora_Level_levelItem3MaxLevel, 0),
          getDrawable(ctx, attrs, a, R.styleable.Folivora_Level_levelItem3Drawable)
        );
      }
      if (a.hasValue(R.styleable.Folivora_Level_levelItem4Drawable)) {
        lld.addLevel(
          a.getInt(R.styleable.Folivora_Level_levelItem4MinLevel, 0),
          a.getInt(R.styleable.Folivora_Level_levelItem4MaxLevel, 0),
          getDrawable(ctx, attrs, a, R.styleable.Folivora_Level_levelItem4Drawable)
        );
      }
      lld.setLevel(a.getInt(R.styleable.Folivora_Level_levelCurrentLevel, 0));
      a.recycle();
      return lld;
    }
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
  class ClipDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Clip);
      final Drawable child = getDrawable(ctx, attrs, a, R.styleable.Folivora_Clip_clipDrawable);
      final int clipGravity = a.getInt(R.styleable.Folivora_Clip_clipGravity, Gravity.START);
      final int clipOrientation = a.getInt(R.styleable.Folivora_Clip_clipOrientation, ClipDrawable.HORIZONTAL);

      ClipDrawable cd = new ClipDrawable(child, clipGravity, clipOrientation);
      cd.setLevel(a.getInt(R.styleable.Folivora_Clip_clipLevel, 10000/*no clip*/));
      a.recycle();
      return cd;
    }
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
  class InsetDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
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
      final Drawable child = getDrawable(ctx, attrs, a, R.styleable.Folivora_Inset_insetDrawable);
      a.recycle();
      return new InsetDrawable(child, ints[0], ints[1], ints[2], ints[3]);
    }
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
  class ScaleDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Scale);
      ScaleDrawable sd = new ScaleDrawable(
        getDrawable(ctx, attrs, a, R.styleable.Folivora_Scale_scaleDrawable),
        a.getInt(R.styleable.Folivora_Scale_scaleGravity, Gravity.START),
        a.getFloat(R.styleable.Folivora_Scale_scaleWidth, -1F),
        a.getFloat(R.styleable.Folivora_Scale_scaleHeight, -1F)
      );
      sd.setLevel(a.getInt(R.styleable.Folivora_Scale_scaleLevel, 1));
      a.recycle();
      return sd;
    }
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
  class AnimationDrawableParser implements DrawableParser {
    @SuppressWarnings("ConstantConditions")
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      AnimationDrawable ad;
      TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Animation);
      final boolean autoPlay = a.getBoolean(R.styleable.Folivora_Animation_animAutoPlay, false);
      if (autoPlay) {
        // Workaround for android bug, animation drawable does not played
        // normally when calling start() on activity's onCreate() method.
        ad = new AnimationDrawable() {
          boolean autoPlayed;

          @Override
          protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            if (!autoPlayed) {
              if (isOneShot()) {
                Log.i(TAG, "Auto play and oneshot both enabled, you could not see the animation");
              }
              this.start();
              autoPlayed = true;
            }
          }
        };
      } else {
        ad = new AnimationDrawable();
      }
      ad.setOneShot(a.getBoolean(R.styleable.Folivora_Animation_animOneShot, false));
      final int frameDuration = a.getInt(R.styleable.Folivora_Animation_animDuration, -1);

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
      return ad;
    }
  }

  /**
   * Try to create a custom drawable from the given class name, note
   * that the custom drawable class must have a public constructor
   * that takes a {@link Context} context and a {@link AttributeSet}
   * attrs as parameters
   */
  class ReflectiveDrawableParser implements DrawableParser {
    // Stuffs for create custom drawable reflectively
    private static Set<String> sFailedNames = new HashSet<>();
    private static Class[] sConstructorSignature = {Context.class, AttributeSet.class};
    private static Object[] sConstructorArgs = new Object[2];
    private static Map<String, Constructor<? extends Drawable>> sConstructorCache = new HashMap<>();

    private final String mDrawableName;

    ReflectiveDrawableParser(String drawableName) {
      this.mDrawableName = drawableName;
    }

    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      final String drawableName = mDrawableName;
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
  }

  /**
   * Create a new drawable by the {@link Folivora.DrawableFactory} with the specific
   * drawableName and using the {@code attrs} customize it.
   */
  class DelegateDrawableParser implements DrawableParser {
    private final Folivora.DrawableFactory mDelegate;

    DelegateDrawableParser(Folivora.DrawableFactory mDelegate) {
      this.mDelegate = mDelegate;
    }

    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      return mDelegate.newDrawable(ctx, attrs);
    }
  }

  /**
   * Dumb implementation of DrawableParser, which parse nothing
   */
  class NoopDrawableParser implements DrawableParser {
    @Override
    public Drawable parse(Context ctx, AttributeSet attrs) {
      return null;
    }
  }

}