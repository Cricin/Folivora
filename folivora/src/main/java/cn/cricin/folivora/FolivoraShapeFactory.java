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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

/**
 * Factory class for create shape(GradientDrawable)s using
 * the alternative attrs(shape1, shape2, shape3, shape4)
 *
 * @see Folivora#getDrawable(Context, TypedArray, AttributeSet, int)
 * @see Folivora#newShape(Context, AttributeSet)
 */
final class FolivoraShapeFactory {

  /**
   * Create a new GradientDrawable(shape) using attrs from shape1
   *
   * @see Folivora#newShape(Context, AttributeSet)
   */
  static GradientDrawable newShape1(Context ctx, AttributeSet attrs) {
    GradientDrawable gd = new GradientDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Shape1);
    gd.setShape(a.getInt(R.styleable.Folivora_Shape1_shape1Type, GradientDrawable.RECTANGLE));

    final int size = a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1SolidSize, -1);
    gd.setSize(a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1SolidWidth, size),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1SolidHeight, size));

    gd.setGradientType(a.getInt(R.styleable.Folivora_Shape1_shape1GradientType, 0));
    gd.setGradientRadius(a.getDimension(R.styleable.Folivora_Shape1_shape1GradientRadius, 0));
    gd.setGradientCenter(a.getDimension(R.styleable.Folivora_Shape1_shape1GradientCenterX, 0),
      a.getDimension(R.styleable.Folivora_Shape1_shape1GradientCenterY, 0));
    gd.setColors(new int[]{
      a.getColor(R.styleable.Folivora_Shape1_shape1GradientStartColor, 0),
      a.getColor(R.styleable.Folivora_Shape1_shape1GradientCenterColor, 0),
      a.getColor(R.styleable.Folivora_Shape1_shape1GradientEndColor, 0)
    });
    final int orientationIndex = a.getInt(R.styleable.Folivora_Shape1_shape1GradientAngle, 0);
    gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

    if (a.hasValue(R.styleable.Folivora_Shape1_shape1SolidColor)) {
      gd.setColor(a.getColor(R.styleable.Folivora_Shape1_shape1SolidColor, Color.WHITE));
    }

    gd.setStroke(
      a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1StrokeWidth, -1),
      a.getColor(R.styleable.Folivora_Shape1_shape1StrokeColor, Color.WHITE),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1StokeDashGap, 0),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape1_shape1StokeDashWidth, 0)
    );
    final float radius = a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadius, 0);
    gd.setCornerRadii(new float[]{
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusBottomLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape1_shape1CornerRadiusBottomLeft, radius),
    });
    a.recycle();
    return gd;
  }

  /**
   * Create a new GradientDrawable(shape) using attrs from shape2
   *
   * @see Folivora#newShape(Context, AttributeSet)
   */
  static GradientDrawable newShape2(Context ctx, AttributeSet attrs) {
    GradientDrawable gd = new GradientDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Shape2);
    gd.setShape(a.getInt(R.styleable.Folivora_Shape2_shape2Type, GradientDrawable.RECTANGLE));

    final int size = a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2SolidSize, -1);
    gd.setSize(a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2SolidWidth, size),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2SolidHeight, size));

    gd.setGradientType(a.getInt(R.styleable.Folivora_Shape2_shape2GradientType, 0));
    gd.setGradientRadius(a.getDimension(R.styleable.Folivora_Shape2_shape2GradientRadius, 0));
    gd.setGradientCenter(a.getDimension(R.styleable.Folivora_Shape2_shape2GradientCenterX, 0),
      a.getDimension(R.styleable.Folivora_Shape2_shape2GradientCenterY, 0));
    gd.setColors(new int[]{
      a.getColor(R.styleable.Folivora_Shape2_shape2GradientStartColor, 0),
      a.getColor(R.styleable.Folivora_Shape2_shape2GradientCenterColor, 0),
      a.getColor(R.styleable.Folivora_Shape2_shape2GradientEndColor, 0)
    });
    final int orientationIndex = a.getInt(R.styleable.Folivora_Shape2_shape2GradientAngle, 0);
    gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

    if (a.hasValue(R.styleable.Folivora_Shape2_shape2SolidColor)) {
      gd.setColor(a.getColor(R.styleable.Folivora_Shape2_shape2SolidColor, Color.WHITE));
    }

    gd.setStroke(
      a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2StrokeWidth, -1),
      a.getColor(R.styleable.Folivora_Shape2_shape2StrokeColor, Color.WHITE),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2StokeDashGap, 0),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape2_shape2StokeDashWidth, 0)
    );
    final float radius = a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadius, 0);
    gd.setCornerRadii(new float[]{
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusBottomLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape2_shape2CornerRadiusBottomLeft, radius),
    });
    a.recycle();
    return gd;
  }

  /**
   * Create a new GradientDrawable(shape) using attrs from shape3
   *
   * @see Folivora#newShape(Context, AttributeSet)
   */
  static GradientDrawable newShape3(Context ctx, AttributeSet attrs) {
    GradientDrawable gd = new GradientDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Shape3);
    gd.setShape(a.getInt(R.styleable.Folivora_Shape3_shape3Type, GradientDrawable.RECTANGLE));

    final int size = a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3SolidSize, -1);
    gd.setSize(a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3SolidWidth, size),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3SolidHeight, size));

    gd.setGradientType(a.getInt(R.styleable.Folivora_Shape3_shape3GradientType, 0));
    gd.setGradientRadius(a.getDimension(R.styleable.Folivora_Shape3_shape3GradientRadius, 0));
    gd.setGradientCenter(a.getDimension(R.styleable.Folivora_Shape3_shape3GradientCenterX, 0),
      a.getDimension(R.styleable.Folivora_Shape3_shape3GradientCenterY, 0));
    gd.setColors(new int[]{
      a.getColor(R.styleable.Folivora_Shape3_shape3GradientStartColor, 0),
      a.getColor(R.styleable.Folivora_Shape3_shape3GradientCenterColor, 0),
      a.getColor(R.styleable.Folivora_Shape3_shape3GradientEndColor, 0)
    });
    final int orientationIndex = a.getInt(R.styleable.Folivora_Shape3_shape3GradientAngle, 0);
    gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

    if (a.hasValue(R.styleable.Folivora_Shape3_shape3SolidColor)) {
      gd.setColor(a.getColor(R.styleable.Folivora_Shape3_shape3SolidColor, Color.WHITE));
    }

    gd.setStroke(
      a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3StrokeWidth, -1),
      a.getColor(R.styleable.Folivora_Shape3_shape3StrokeColor, Color.WHITE),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3StokeDashGap, 0),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape3_shape3StokeDashWidth, 0)
    );
    final float radius = a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadius, 0);
    gd.setCornerRadii(new float[]{
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusBottomLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape3_shape3CornerRadiusBottomLeft, radius),
    });
    a.recycle();
    return gd;
  }

  /**
   * Create a new GradientDrawable(shape) using attrs from shape4
   *
   * @see Folivora#newShape(Context, AttributeSet)
   */
  static GradientDrawable newShape4(Context ctx, AttributeSet attrs) {
    GradientDrawable gd = new GradientDrawable();
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.Folivora_Shape4);
    gd.setShape(a.getInt(R.styleable.Folivora_Shape4_shape4Type, GradientDrawable.RECTANGLE));

    final int size = a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4SolidSize, -1);
    gd.setSize(a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4SolidWidth, size),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4SolidHeight, size));

    gd.setGradientType(a.getInt(R.styleable.Folivora_Shape4_shape4GradientType, 0));
    gd.setGradientRadius(a.getDimension(R.styleable.Folivora_Shape4_shape4GradientRadius, 0));
    gd.setGradientCenter(a.getDimension(R.styleable.Folivora_Shape4_shape4GradientCenterX, 0),
      a.getDimension(R.styleable.Folivora_Shape4_shape4GradientCenterY, 0));
    gd.setColors(new int[]{
      a.getColor(R.styleable.Folivora_Shape4_shape4GradientStartColor, 0),
      a.getColor(R.styleable.Folivora_Shape4_shape4GradientCenterColor, 0),
      a.getColor(R.styleable.Folivora_Shape4_shape4GradientEndColor, 0)
    });
    final int orientationIndex = a.getInt(R.styleable.Folivora_Shape4_shape4GradientAngle, 0);
    gd.setOrientation(GradientDrawable.Orientation.values()[orientationIndex]);

    if (a.hasValue(R.styleable.Folivora_Shape4_shape4SolidColor)) {
      gd.setColor(a.getColor(R.styleable.Folivora_Shape4_shape4SolidColor, Color.WHITE));
    }

    gd.setStroke(
      a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4StrokeWidth, -1),
      a.getColor(R.styleable.Folivora_Shape4_shape4StrokeColor, Color.WHITE),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4StokeDashGap, 0),
      a.getDimensionPixelSize(R.styleable.Folivora_Shape4_shape4StokeDashWidth, 0)
    );
    final float radius = a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadius, 0);
    gd.setCornerRadii(new float[]{
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusTopLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusTopRight, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusBottomRight, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusBottomLeft, radius),
      a.getDimension(R.styleable.Folivora_Shape4_shape4CornerRadiusBottomLeft, radius),
    });
    a.recycle();
    return gd;
  }

  private FolivoraShapeFactory() {}
}
