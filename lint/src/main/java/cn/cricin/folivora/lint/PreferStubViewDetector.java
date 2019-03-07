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

package cn.cricin.folivora.lint;

import com.android.resources.ResourceFolderType;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;
import com.android.tools.lint.detector.api.XmlScanner;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Deprecated
public final class PreferStubViewDetector extends Detector implements Detector.XmlScanner, XmlScanner {
  static final Issue ISSUE = Issue.create(
    "PreferUseStubView",
    "Using stub view folivora provided instead",
    "To work with design time preview, use stub view provided by folivora instead of system views",
    Category.CORRECTNESS,
    7,
    Severity.ERROR,
    new Implementation(PreferStubViewDetector.class, Scope.RESOURCE_FILE_SCOPE)
  );


  private static Set<String> sSystemViewNames = new HashSet<>(Arrays.asList(
    "Button", "CheckBox", "EditText", "FrameLayout", "GridView", "HorizontalScrollView",
    "ImageView", "LinearLayout", "ListView", "ProgressBar", "RelativeLayout", "ScrollView",
    "SeekBar", "Spinner", "TextView", "View"));


  @Override
  public Collection<String> getApplicableAttributes() {
    return Arrays.asList("drawableType", "drawableName");
  }

  @Override
  public boolean appliesTo(@NotNull ResourceFolderType folderType) {
    return folderType == ResourceFolderType.LAYOUT;
  }

  @Override
  public void visitAttribute(@NotNull XmlContext context, @NotNull Attr attribute) {
    Element tag = attribute.getOwnerElement();
    String tagName = tag.getTagName();
    if (sSystemViewNames.contains(tagName)) {
      LintFix fix = LintFix.create().replace().range(context.getLocation(tag))
        .text(tagName).with("cn.cricin.folivora.view." + tagName).build();
      context.report(ISSUE, context.getLocation(tag),
        "Using cn.cricin.folivora.view." + tagName + " instead to support design time preview", fix);
    }
  }
}
