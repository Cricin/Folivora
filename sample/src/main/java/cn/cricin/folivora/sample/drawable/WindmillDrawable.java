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

package cn.cricin.folivora.sample.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import cn.cricin.folivora.sample.R;

@SuppressWarnings("WeakerAccess, unused")
public class WindmillDrawable extends Drawable {
  private static final int DEFAULT_SIZE = 100;//dp
  private static final int DEFAULT_CENTER_DOT_RADIUS = 4;//dp

  private Paint mPaint;
  private int mSize;
  private int[] mColors;
  private RectF mRectF = new RectF();
  private int mCenterDotColor;
  private int mCenterDotRadius;
  private int mRotateDegrees;

  public WindmillDrawable(Context ctx) {
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mColors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.BLACK};
    mSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
      DEFAULT_SIZE, ctx.getResources().getDisplayMetrics());
    mCenterDotRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
      DEFAULT_CENTER_DOT_RADIUS, ctx.getResources().getDisplayMetrics());
    mCenterDotColor = Color.WHITE;
  }

  public WindmillDrawable(Context ctx, AttributeSet attrs) {
    this(ctx);
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.WindmillDrawable);
    int count = a.getIndexCount();
    for (int i = 0; i < count; i++) {
      int index = a.getIndex(i);
      switch (index) {
        case R.styleable.WindmillDrawable_wdSize:
          mSize = a.getDimensionPixelSize(index, mSize);
          break;
        case R.styleable.WindmillDrawable_wdColor0:
          mColors[0] = a.getColor(index, mColors[0]);
          break;
        case R.styleable.WindmillDrawable_wdColor1:
          mColors[1] = a.getColor(index, mColors[1]);
          break;
        case R.styleable.WindmillDrawable_wdColor2:
          mColors[2] = a.getColor(index, mColors[2]);
          break;
        case R.styleable.WindmillDrawable_wdColor3:
          mColors[3] = a.getColor(index, mColors[3]);
          break;
        case R.styleable.WindmillDrawable_wdCenterDotRadius:
          mCenterDotRadius = a.getDimensionPixelSize(index, mCenterDotRadius);
          break;
        case R.styleable.WindmillDrawable_wdCenterDotColor:
          mCenterDotColor = a.getColor(index, mCenterDotColor);
          break;
        case R.styleable.WindmillDrawable_wdRotateDegrees:
          mRotateDegrees = a.getInt(index, 0);
          break;
        default://no-op
          break;
      }
    }
    a.recycle();
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    final Rect bounds = getBounds();
    canvas.save();
    canvas.clipRect(bounds);
    canvas.translate(bounds.centerX(), bounds.centerY());
    canvas.rotate(mRotateDegrees);
    float radius = Math.min(bounds.width(), bounds.height()) / (float) 2;
    mRectF.set(0, -radius / 2, radius, radius / 2);
    for (int i = 0; i < mColors.length; i++) {
      mPaint.setColor(mColors[i]);
      canvas.rotate(90 * i);
      canvas.drawArc(mRectF, 0, 180, true, mPaint);
    }
    mPaint.setColor(mCenterDotColor);
    canvas.drawCircle(0, 0, mCenterDotRadius, mPaint);
    canvas.restore();
  }

  @Override
  public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    mPaint.setColorFilter(colorFilter);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public int getIntrinsicWidth() {
    return mSize;
  }

  @Override
  public int getIntrinsicHeight() {
    return mSize;
  }
}
