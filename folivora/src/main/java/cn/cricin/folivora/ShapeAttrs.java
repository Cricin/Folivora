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

/**
 * ShapeAttrs represents for a set of shape xml attribute indexes,
 * so we can refactor shape creating method to reduce boilerplate code
 */
final class ShapeAttrs {
  int[] mAttrStyleable;
  int mShapeType;
  int mShapeSolidSize;
  int mShapeSolidWidth;
  int mShapeSolidHeight;
  int mShapeGradientType;
  int mShapeGradientRadius;
  int mShapeGradientCenterX;
  int mShapeGradientCenterY;
  int mShapeGradientStartColor;
  int mShapeGradientCenterColor;
  int mShapeGradientEndColor;
  int mShapeGradientAngle;
  int mShapeSolidColor;
  int mShapeStrokeWidth;
  int mShapeStrokeColor;
  int mShapeStrokeDashGap;
  int mShapeStrokeDashWidth;
  int mShapeCornerRadius;
  int mShapeCornerRadiusTopLeft;
  int mShapeCornerRadiusTopRight;
  int mShapeCornerRadiusBottomLeft;
  int mShapeCornerRadiusBottomRight;

  private ShapeAttrs(
    int[] attrStyleable,
    int shapeType,
    int shapeSolidSize,
    int shapeSolidWidth,
    int shapeSolidHeight,
    int shapeGradientType,
    int shapeGradientRadius,
    int shapeGradientCenterX,
    int shapeGradientCenterY,
    int shapeGradientStartColor,
    int shapeGradientCenterColor,
    int shapeGradientEndColor,
    int shapeGradientAngle,
    int shapeSolidColor,
    int shapeStrokeWidth,
    int shapeStrokeColor,
    int shapeStrokeDashGap,
    int shapeStrokeDashWidth,
    int shapeCornerRadius,
    int shapeCornerRadiusTopLeft,
    int shapeCornerRadiusTopRight,
    int shapeCornerRadiusBottomLeft,
    int shapeCornerRadiusBottomRight
  ) {
    this.mAttrStyleable = attrStyleable;
    this.mShapeType = shapeType;
    this.mShapeSolidSize = shapeSolidSize;
    this.mShapeSolidWidth = shapeSolidWidth;
    this.mShapeSolidHeight = shapeSolidHeight;
    this.mShapeGradientType = shapeGradientType;
    this.mShapeGradientRadius = shapeGradientRadius;
    this.mShapeGradientCenterX = shapeGradientCenterX;
    this.mShapeGradientCenterY = shapeGradientCenterY;
    this.mShapeGradientStartColor = shapeGradientStartColor;
    this.mShapeGradientCenterColor = shapeGradientCenterColor;
    this.mShapeGradientEndColor = shapeGradientEndColor;
    this.mShapeGradientAngle = shapeGradientAngle;
    this.mShapeSolidColor = shapeSolidColor;
    this.mShapeStrokeWidth = shapeStrokeWidth;
    this.mShapeStrokeColor = shapeStrokeColor;
    this.mShapeStrokeDashGap = shapeStrokeDashGap;
    this.mShapeStrokeDashWidth = shapeStrokeDashWidth;
    this.mShapeCornerRadius = shapeCornerRadius;
    this.mShapeCornerRadiusTopLeft = shapeCornerRadiusTopLeft;
    this.mShapeCornerRadiusTopRight = shapeCornerRadiusTopRight;
    this.mShapeCornerRadiusBottomLeft = shapeCornerRadiusBottomLeft;
    this.mShapeCornerRadiusBottomRight = shapeCornerRadiusBottomRight;
  }

  private static final ShapeAttrs[] SHAPE_ATTRS_ARRAY = {
    new ShapeAttrs(
      R.styleable.Folivora_Shape,
      R.styleable.Folivora_Shape_shapeType,
      R.styleable.Folivora_Shape_shapeSolidSize,
      R.styleable.Folivora_Shape_shapeSolidWidth,
      R.styleable.Folivora_Shape_shapeSolidHeight,
      R.styleable.Folivora_Shape_shapeGradientType,
      R.styleable.Folivora_Shape_shapeGradientRadius,
      R.styleable.Folivora_Shape_shapeGradientCenterX,
      R.styleable.Folivora_Shape_shapeGradientCenterY,
      R.styleable.Folivora_Shape_shapeGradientStartColor,
      R.styleable.Folivora_Shape_shapeGradientEndColor,
      R.styleable.Folivora_Shape_shapeGradientCenterColor,
      R.styleable.Folivora_Shape_shapeGradientAngle,
      R.styleable.Folivora_Shape_shapeSolidColor,
      R.styleable.Folivora_Shape_shapeStrokeWidth,
      R.styleable.Folivora_Shape_shapeStrokeColor,
      R.styleable.Folivora_Shape_shapeStokeDashGap,
      R.styleable.Folivora_Shape_shapeStokeDashWidth,
      R.styleable.Folivora_Shape_shapeCornerRadius,
      R.styleable.Folivora_Shape_shapeCornerRadiusTopLeft,
      R.styleable.Folivora_Shape_shapeCornerRadiusTopRight,
      R.styleable.Folivora_Shape_shapeCornerRadiusBottomRight,
      R.styleable.Folivora_Shape_shapeCornerRadiusBottomLeft
    ),
    new ShapeAttrs(
      R.styleable.Folivora_Shape1,
      R.styleable.Folivora_Shape1_shape1Type,
      R.styleable.Folivora_Shape1_shape1SolidSize,
      R.styleable.Folivora_Shape1_shape1SolidWidth,
      R.styleable.Folivora_Shape1_shape1SolidHeight,
      R.styleable.Folivora_Shape1_shape1GradientType,
      R.styleable.Folivora_Shape1_shape1GradientRadius,
      R.styleable.Folivora_Shape1_shape1GradientCenterX,
      R.styleable.Folivora_Shape1_shape1GradientCenterY,
      R.styleable.Folivora_Shape1_shape1GradientStartColor,
      R.styleable.Folivora_Shape1_shape1GradientEndColor,
      R.styleable.Folivora_Shape1_shape1GradientCenterColor,
      R.styleable.Folivora_Shape1_shape1GradientAngle,
      R.styleable.Folivora_Shape1_shape1SolidColor,
      R.styleable.Folivora_Shape1_shape1StrokeWidth,
      R.styleable.Folivora_Shape1_shape1StrokeColor,
      R.styleable.Folivora_Shape1_shape1StokeDashGap,
      R.styleable.Folivora_Shape1_shape1StokeDashWidth,
      R.styleable.Folivora_Shape1_shape1CornerRadius,
      R.styleable.Folivora_Shape1_shape1CornerRadiusTopLeft,
      R.styleable.Folivora_Shape1_shape1CornerRadiusTopRight,
      R.styleable.Folivora_Shape1_shape1CornerRadiusBottomRight,
      R.styleable.Folivora_Shape1_shape1CornerRadiusBottomLeft
    ),
    new ShapeAttrs(
      R.styleable.Folivora_Shape2,
      R.styleable.Folivora_Shape2_shape2Type,
      R.styleable.Folivora_Shape2_shape2SolidSize,
      R.styleable.Folivora_Shape2_shape2SolidWidth,
      R.styleable.Folivora_Shape2_shape2SolidHeight,
      R.styleable.Folivora_Shape2_shape2GradientType,
      R.styleable.Folivora_Shape2_shape2GradientRadius,
      R.styleable.Folivora_Shape2_shape2GradientCenterX,
      R.styleable.Folivora_Shape2_shape2GradientCenterY,
      R.styleable.Folivora_Shape2_shape2GradientStartColor,
      R.styleable.Folivora_Shape2_shape2GradientEndColor,
      R.styleable.Folivora_Shape2_shape2GradientCenterColor,
      R.styleable.Folivora_Shape2_shape2GradientAngle,
      R.styleable.Folivora_Shape2_shape2SolidColor,
      R.styleable.Folivora_Shape2_shape2StrokeWidth,
      R.styleable.Folivora_Shape2_shape2StrokeColor,
      R.styleable.Folivora_Shape2_shape2StokeDashGap,
      R.styleable.Folivora_Shape2_shape2StokeDashWidth,
      R.styleable.Folivora_Shape2_shape2CornerRadius,
      R.styleable.Folivora_Shape2_shape2CornerRadiusTopLeft,
      R.styleable.Folivora_Shape2_shape2CornerRadiusTopRight,
      R.styleable.Folivora_Shape2_shape2CornerRadiusBottomRight,
      R.styleable.Folivora_Shape2_shape2CornerRadiusBottomLeft
    ),
    new ShapeAttrs(
      R.styleable.Folivora_Shape3,
      R.styleable.Folivora_Shape3_shape3Type,
      R.styleable.Folivora_Shape3_shape3SolidSize,
      R.styleable.Folivora_Shape3_shape3SolidWidth,
      R.styleable.Folivora_Shape3_shape3SolidHeight,
      R.styleable.Folivora_Shape3_shape3GradientType,
      R.styleable.Folivora_Shape3_shape3GradientRadius,
      R.styleable.Folivora_Shape3_shape3GradientCenterX,
      R.styleable.Folivora_Shape3_shape3GradientCenterY,
      R.styleable.Folivora_Shape3_shape3GradientStartColor,
      R.styleable.Folivora_Shape3_shape3GradientEndColor,
      R.styleable.Folivora_Shape3_shape3GradientCenterColor,
      R.styleable.Folivora_Shape3_shape3GradientAngle,
      R.styleable.Folivora_Shape3_shape3SolidColor,
      R.styleable.Folivora_Shape3_shape3StrokeWidth,
      R.styleable.Folivora_Shape3_shape3StrokeColor,
      R.styleable.Folivora_Shape3_shape3StokeDashGap,
      R.styleable.Folivora_Shape3_shape3StokeDashWidth,
      R.styleable.Folivora_Shape3_shape3CornerRadius,
      R.styleable.Folivora_Shape3_shape3CornerRadiusTopLeft,
      R.styleable.Folivora_Shape3_shape3CornerRadiusTopRight,
      R.styleable.Folivora_Shape3_shape3CornerRadiusBottomRight,
      R.styleable.Folivora_Shape3_shape3CornerRadiusBottomLeft
    ),
    new ShapeAttrs(
      R.styleable.Folivora_Shape4,
      R.styleable.Folivora_Shape4_shape4Type,
      R.styleable.Folivora_Shape4_shape4SolidSize,
      R.styleable.Folivora_Shape4_shape4SolidWidth,
      R.styleable.Folivora_Shape4_shape4SolidHeight,
      R.styleable.Folivora_Shape4_shape4GradientType,
      R.styleable.Folivora_Shape4_shape4GradientRadius,
      R.styleable.Folivora_Shape4_shape4GradientCenterX,
      R.styleable.Folivora_Shape4_shape4GradientCenterY,
      R.styleable.Folivora_Shape4_shape4GradientStartColor,
      R.styleable.Folivora_Shape4_shape4GradientEndColor,
      R.styleable.Folivora_Shape4_shape4GradientCenterColor,
      R.styleable.Folivora_Shape4_shape4GradientAngle,
      R.styleable.Folivora_Shape4_shape4SolidColor,
      R.styleable.Folivora_Shape4_shape4StrokeWidth,
      R.styleable.Folivora_Shape4_shape4StrokeColor,
      R.styleable.Folivora_Shape4_shape4StokeDashGap,
      R.styleable.Folivora_Shape4_shape4StokeDashWidth,
      R.styleable.Folivora_Shape4_shape4CornerRadius,
      R.styleable.Folivora_Shape4_shape4CornerRadiusTopLeft,
      R.styleable.Folivora_Shape4_shape4CornerRadiusTopRight,
      R.styleable.Folivora_Shape4_shape4CornerRadiusBottomRight,
      R.styleable.Folivora_Shape4_shape4CornerRadiusBottomLeft
    )
  };

  static ShapeAttrs forIndex(int index) {
    if (index < 0 || index > 4) {
      return null;
    } else {
      return SHAPE_ATTRS_ARRAY[index];
    }
  }
}
