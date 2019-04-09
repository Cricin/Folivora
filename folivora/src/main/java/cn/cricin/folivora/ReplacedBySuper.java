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
 * A marker interface donates that a design time preview stub view will
 * be replaced by it's super class at runtime, this interface helps you
 * do not need do declare replacedBy attribute in your layout.xml, for
 * example, RecyclerView is not stubbed in folivora to reduce dependencies,
 * so preview for RecyclerView is not supported by default. you can doing
 * this to make preview available
 * <pre>
 *   public class StubRecyclerView extends RecyclerView implements ReplacedBySuper {
 *     public StubRecyclerView(Context ctx, AttributeSet attrs) {
 *       super(ctx, attrs);
 *       if (!isInEditMode()) {
 *         throw new IllegalStateException("this view only available at design time");
 *       }
 *       Folivora.applyDrawableToView(this, attrs);
 *     }
 *   }
 * </pre>
 *
 * then in your layout.xml, there is no need to set replacedBy attribute,
 * StubRecyclerView will be replaced by RecyclerView automatically in layout
 * inflation
 * <pre>
 *   <your.package.name.StubRecyclerView
 *     android:layout_width="120dp"
 *     android:layout_height="120dp"
 *     app:drawableType="shape"
 *     app:shapeSolidColor="@color/black"
 *     app:shapeCornerRadius="10dp"/>
 * </pre>
 *
 * @see cn.cricin.folivora.FolivoraViewFactory#replaceViewNameIfNeeded(String, Context, AttributeSet)
 */
@Deprecated // Preview is automatically supported
public interface ReplacedBySuper {
}
