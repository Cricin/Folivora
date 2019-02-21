package cn.cricin.folivora;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * A drawable factory takes responsibility of drawable creation
 * when the drawable type retrieved from view tag matches this
 * factory's {@link #drawableType()} drawableType()
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
   * Returns drawableType this creator can handles
   *
   * @return drawable drawableType
   */
  String drawableType();
}
