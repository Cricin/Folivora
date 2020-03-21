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
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import cn.cricin.folivora.sample.R;

@SuppressWarnings("WeakerAccess, unused")
public class AlphaBackgroundDrawable extends Drawable {
  private static final int DEFAULT_COLOR_0 = Color.WHITE;
  private static final int DEFAULT_COLOR_1 = Color.BLACK;
  private static final int DEFAULT_SQUARE_SIZE = 8;//dp

  private Paint mPaint;

  public AlphaBackgroundDrawable(Context ctx) {
    initPaint(DEFAULT_COLOR_0,
      DEFAULT_COLOR_1,
      (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_SQUARE_SIZE,
        ctx.getResources().getDisplayMetrics()));
  }

  public AlphaBackgroundDrawable(Context ctx, AttributeSet attrs) {
    TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.AlphaBackgroundDrawable);
    final int color0 = a.getColor(R.styleable.AlphaBackgroundDrawable_abdSquareColor0, DEFAULT_COLOR_0);
    final int color1 = a.getColor(R.styleable.AlphaBackgroundDrawable_abdSquareColor1, DEFAULT_COLOR_1);
    final int squareSize = a.getDimensionPixelSize(R.styleable.AlphaBackgroundDrawable_abdSquareSize,
      DEFAULT_SQUARE_SIZE);
    a.recycle();

    initPaint(color0, color1, squareSize);
  }

  @SuppressWarnings("SuspiciousNameCombination")
  void initPaint(int color0, int color1, int squareSize) {
    final int squareSizeX2 = squareSize * 2;

    Bitmap temp = Bitmap.createBitmap(squareSizeX2, squareSizeX2, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(temp);
    Paint paint = new Paint();
    paint.setColor(color0);
    canvas.drawRect(0, 0, squareSize, squareSize, paint);
    canvas.drawRect(squareSize, squareSize, squareSizeX2, squareSizeX2, paint);

    paint.setColor(color1);
    canvas.drawRect(squareSize, 0, squareSizeX2, squareSize, paint);
    canvas.drawRect(0, squareSize, squareSize, squareSizeX2, paint);
    Bitmap bg = Bitmap.createBitmap(temp);
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    BitmapShader bs = new BitmapShader(bg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    mPaint.setShader(bs);
  }

  @Override
  public void draw(@NonNull Canvas canvas) {
    canvas.save();
    canvas.clipRect(getBounds());
    canvas.drawRect(getBounds(), mPaint);
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
    int alpha = mPaint.getAlpha();
    if (alpha == 255) {
      return PixelFormat.OPAQUE;
    } else if (alpha == 0) {
      return PixelFormat.TRANSPARENT;
    } else {
      return PixelFormat.TRANSLUCENT;
    }
  }
}
