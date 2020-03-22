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
import android.graphics.drawable.Drawable;
import android.os.Build;

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