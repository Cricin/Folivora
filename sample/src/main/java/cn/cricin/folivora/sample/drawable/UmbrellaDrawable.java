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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;


@SuppressWarnings("WeakerAccess, unused")
public class UmbrellaDrawable extends Drawable {
  private static final float TAN20 = (float) Math.tan(Math.toRadians(20));

  private Shader mShader1;
  private Shader mShader2;

  private int mColor1;
  private int mColor2;
  private int mBackgroundColor;

  private Path mPath;
  private Paint mPaint;

  public UmbrellaDrawable() {
    mColor1 = Color.RED;
    mColor2 = Color.WHITE;
    mBackgroundColor = Color.BLACK;
    mPath = new Path();
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    Rect bounds = getBounds();
    canvas.save();
    canvas.clipRect(bounds);
    canvas.drawColor(mBackgroundColor);
    canvas.translate(bounds.centerX(), bounds.centerY());
    List<Shader> shaderList = Arrays.asList(mShader1, mShader2);
    for (int i = 0; i < 8; i++) {
      mPaint.setShader(shaderList.get(i % 2));
      canvas.drawPath(mPath, mPaint);
      canvas.rotate(45);
    }
    canvas.restore();
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    final int radius = Math.max(bounds.width(), bounds.height()) / 2;
    mPath.reset();
    mPath.moveTo(radius * 0.07F, 0);
    mPath.lineTo(radius, -radius * TAN20);
    mPath.quadTo(0.7F * radius, 0, radius, radius * TAN20);
    mPath.close();

    createShader();
  }

  private void createShader() {
    Rect bounds = getBounds();
    if (bounds.isEmpty()) return;
    final int radius = Math.max(bounds.width(), bounds.height()) / 2;
    mShader1 = new LinearGradient(0, 0, radius, 0, mColor1, mBackgroundColor, Shader.TileMode.CLAMP);
    mShader2 = new LinearGradient(0, 0, radius, 0, mColor2, mBackgroundColor, Shader.TileMode.CLAMP);
  }

  public void setColor1(int color) {
    if (mColor1 != color) {
      mColor1 = color;
      createShader();
      invalidateSelf();
    }
  }

  public void setColor2(int color) {
    if (mColor2 != color) {
      mColor2 = color;
      createShader();
      invalidateSelf();
    }
  }

  public void setBackgroundColor(int color) {
    if (mBackgroundColor != color) {
      mBackgroundColor = color;
      invalidateSelf();
    }
  }

  public int getColor1() {
    return mColor1;
  }

  public int getColor2() {
    return mColor2;
  }

  public int getBackgroundColor() {
    return mBackgroundColor;
  }


  @Override
  public void setAlpha(int alpha) {
    //not supported
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    //not supported
  }

  @Override
  public int getOpacity() {
    return PixelFormat.OPAQUE;
  }
}