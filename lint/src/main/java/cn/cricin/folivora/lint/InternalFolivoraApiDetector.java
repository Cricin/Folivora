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

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.MethodSignature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastUtils;

import java.util.Collections;
import java.util.List;

@Deprecated
public final class InternalFolivoraApiDetector extends Detector
  implements SourceCodeScanner, Detector.UastScanner {

  static final Issue ISSUE = Issue.create(
    "InternalFolivoraCall",
    "You are using Folivora internal API",
    "Using Folivora.applyDrawableToView() outside view's (Context context," +
      " AttributeSet attrs) constructor is not recommended, you should avoid doing this",
    Category.CORRECTNESS,
    7,
    Severity.ERROR,
    new Implementation(InternalFolivoraApiDetector.class, Scope.JAVA_FILE_SCOPE)
  );

  private static final String APPLY_METHOD = "applyDrawableToView";
  private static final String FOLIVORA_CLASS = "cn.cricin.folivora.Folivora";
  private static final String VIEW_CLASS = "android.view.View";
  private static final String CONTEXT_CLASS = "android.content.Context";
  private static final String ATTRS_CLASS = "android.util.AttributeSet";

  @Nullable
  @Override
  public List<String> getApplicableMethodNames() {
    return Collections.singletonList(APPLY_METHOD);
  }

  @Override
  public void visitMethod(@NotNull JavaContext context,
                          @NotNull UCallExpression call,
                          @NotNull PsiMethod method) {
    JavaEvaluator evaluator = context.getEvaluator();

    //check Folivora.applyDrawableToView() call
    String methodName = method.getName();
    if (!methodName.equals(APPLY_METHOD) || !evaluator.isMemberInClass(method, FOLIVORA_CLASS)) return;

    PsiClass viewClass = evaluator.findClass(VIEW_CLASS);
    PsiClass currentClass = UastUtils.getContainingClass(call);
    if(currentClass == null || viewClass == null) return;
    if (!currentClass.isInheritor(viewClass, true/*deep check*/)) {
      report(context, call);
      return;
    }

    UMethod uMethod = UastUtils.getParentOfType(call, UMethod.class, false);
    if(uMethod == null) return;

    //check it is a view's constructor
    if (!uMethod.isConstructor()) {
      report(context, call);
      return;
    }

    MethodSignature signature = uMethod.getSignature(PsiSubstitutor.EMPTY);
    PsiType[] types = signature.getParameterTypes();
    if (types.length != 2) {
      report(context, call);
      return;
    }

    if (!types[0].equalsToText(CONTEXT_CLASS)
      || !types[1].equalsToText(ATTRS_CLASS)) {
      report(context, call);
    }
  }

  private void report(JavaContext context, UCallExpression call) {
    context.report(ISSUE, call, context.getLocation(call),
      "`Folivora.applyDrawableToView()` should only be called in preview stub view's (Context context, AttributeSet attrs) constructor");
  }

}
