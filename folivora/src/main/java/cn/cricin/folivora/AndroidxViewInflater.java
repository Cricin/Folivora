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
import android.view.View;

import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * This ViewInflater bridges folivora with Androidx appcompat libraries
 * method.
 *
 * how to:
 * in your theme declarations, add item belows
 *
 * <pre>
 *   &lt;style name="App.Theme" parent="..."&gt;
 *     your theme attr defs ...
 *     &lt;item name="viewInflaterClass"&gt;cn.cricin.folivora.AndroidxViewInflater&lt;/item&gt;
 *   &lt;style&gt;
 * </pre>
 *
 * folivora will be enabled with no configuration needed to your source code,
 * calling Folivora#installViewFactory(ctx) is unnessesary
 *
 * bonus:
 *   if your are using Android Studio which version is higher than <b>3.2.0</b>, design time
 *   preview is available on any views, no need to use stub views
 */
@SuppressWarnings({"unused", "NullableProblems"})
public class AndroidxViewInflater extends AppCompatViewInflater {

  @Override
  protected AppCompatTextView createTextView(Context context, AttributeSet attrs) {
    return apply(super.createTextView(context, attrs), attrs);
  }

  @Override
  protected AppCompatImageView createImageView(Context context, AttributeSet attrs) {
    return apply(super.createImageView(context, attrs), attrs);
  }

  @Override
  protected AppCompatButton createButton(Context context, AttributeSet attrs) {
    return apply(super.createButton(context, attrs), attrs);
  }

  @Override
  protected AppCompatEditText createEditText(Context context, AttributeSet attrs) {
    return apply(super.createEditText(context, attrs), attrs);
  }

  @Override
  protected AppCompatSpinner createSpinner(Context context, AttributeSet attrs) {
    return apply(super.createSpinner(context, attrs), attrs);
  }

  @Override
  protected AppCompatImageButton createImageButton(Context context, AttributeSet attrs) {
    return apply(super.createImageButton(context, attrs), attrs);
  }

  @Override
  protected AppCompatCheckBox createCheckBox(Context context, AttributeSet attrs) {
    return apply(super.createCheckBox(context, attrs), attrs);
  }

  @Override
  protected AppCompatRadioButton createRadioButton(Context context, AttributeSet attrs) {
    return apply(super.createRadioButton(context, attrs), attrs);
  }

  @Override
  protected AppCompatCheckedTextView createCheckedTextView(Context context, AttributeSet attrs) {
    return apply(super.createCheckedTextView(context, attrs), attrs);
  }

  @Override
  protected AppCompatAutoCompleteTextView createAutoCompleteTextView(Context context, AttributeSet attrs) {
    return apply(super.createAutoCompleteTextView(context, attrs), attrs);
  }

  @Override
  protected AppCompatMultiAutoCompleteTextView createMultiAutoCompleteTextView(Context context, AttributeSet attrs) {
    return apply(super.createMultiAutoCompleteTextView(context, attrs), attrs);
  }

  @Override
  protected AppCompatRatingBar createRatingBar(Context context, AttributeSet attrs) {
    return apply(super.createRatingBar(context, attrs), attrs);
  }

  @Override
  protected AppCompatSeekBar createSeekBar(Context context, AttributeSet attrs) {
    return apply(super.createSeekBar(context, attrs), attrs);
  }

  @Override
  protected View createView(Context context, String name, AttributeSet attrs) {
    View view = super.createView(context, name, attrs);
    if (view == null) {
      view = FolivoraViewFactory.onCreateView(null, name, context, attrs,
        null, null);
    }
    return apply(view, attrs);
  }

  @SuppressWarnings("unchecked")
  private static <V extends View> V apply(View view, AttributeSet attrs) {
    if (view != null) {
      Folivora.applyDrawableToView(view, attrs);
    }
    return (V) view;
  }
}
