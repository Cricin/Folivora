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

package cn.cricin.folivora.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.cricin.folivora.Folivora;

/**
 * Stub HorizontalScrollView to support previewing, will be replaced by
 * android.widget.HorizontalScrollView at runtime
 */
@Deprecated // Preview is automatically supported
public final class HorizontalScrollView extends android.widget.HorizontalScrollView {
  public HorizontalScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
    Folivora.applyDrawableToView(this, attrs);
  }
}
