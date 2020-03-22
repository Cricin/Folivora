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

/**
 * A ParseRequest holding all information of drawable parsing task
 * each field may be used by different DrawableParsers.
 */
public final class ParseRequest {
  private final Context mContext;
  private final AttributeSet mAttrs;
  private final RippleFallback mRippleFallback;
  private final ShapeAttrs mShapeAttrs;
  private final String mDrawableClassName;

  /*package*/ParseRequest(Context context,
               AttributeSet attrs,
               RippleFallback fallback,
               ShapeAttrs shapeAttrs,
               String drawableClassName) {
    this.mContext = context;
    this.mAttrs = attrs;
    this.mRippleFallback = fallback;
    this.mShapeAttrs = shapeAttrs;
    this.mDrawableClassName = drawableClassName;
  }

  /**
   * @return current inflation context
   */
  public Context context() {
    return mContext;
  }

  /**
   * @return attribute retrieved from view tag
   */
  public AttributeSet attrs() {
    return mAttrs;
  }

  /**
   * @return a fallback for RippleDrawable
   */
  public RippleFallback rippleFallback() {
    return mRippleFallback;
  }

  /**
   * @return attribute information of a shape at different indexes, used by GradientDrawableParser
   */
  public ShapeAttrs shapeAttrs() {
    return mShapeAttrs;
  }

  /**
   * @return full qualified class name of drawable to be inflated
   */
  public String drawableClassName() {
    return mDrawableClassName;
  }
}