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

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintUtils;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.UastUtils;
import org.jetbrains.uast.visitor.AbstractUastVisitor;

import java.util.Collections;
import java.util.List;

public final class InstalledBeforeSuperDetector extends Detector implements SourceCodeScanner, Detector
  .UastScanner {
  static final Issue ISSUE = Issue.create(
    "FolivoraInstalledBeforeSuper",
    "Folivora installed before super.onCreate()",
    "You have installed Folivora before super.onCreate()," +
      " this will cause runtime failure if you are using AppCompatActivity",
    Category.CORRECTNESS,
    7,
    Severity.ERROR,
    new Implementation(InstalledBeforeSuperDetector.class, Scope.JAVA_FILE_SCOPE)
  );

  private static final String INSTALL_METHOD = "installViewFactory";
  private static final String TYPE_FOLIVORA = "cn.cricin.folivora.Folivora";
  private static final String LEGACY_COMPAT_ACTIVITY = "android.support.v7.app.AppCompatActivity";
  private static final String COMPAT_ACTIVITY = "androidx.appcompat.app.AppCompatActivity";

  @Nullable
  @Override
  public List<String> getApplicableMethodNames() {
    return Collections.singletonList(INSTALL_METHOD);
  }

  @Override
  public void visitMethod(@NonNull JavaContext context,
                          @NonNull UCallExpression call,
                          @NonNull PsiMethod method) {
    JavaEvaluator evaluator = context.getEvaluator();

    //check Folivora.installViewFactory() call
    String methodName = method.getName();
    if (!methodName.equals(INSTALL_METHOD) || !evaluator.isMemberInClass(method, TYPE_FOLIVORA))
      return;

    //check current class is decent of AppCompatActivity
    PsiClass legacyCompatActClass = evaluator.findClass(LEGACY_COMPAT_ACTIVITY);
    PsiClass compatActClass = evaluator.findClass(COMPAT_ACTIVITY);
    PsiClass c = UastUtils.getContainingClass(call);
    boolean isAppCompatActivity = false;
    if (c != null) {
      isAppCompatActivity = (legacyCompatActClass != null && c.isInheritor(legacyCompatActClass,
        true/*deep check*/))
        || compatActClass != null && c.isInheritor(compatActClass, true/*deep check*/);
    }
    if (!isAppCompatActivity) return;

    //check current method is onCreate
    @SuppressWarnings("unchecked")
    UMethod uMethod = UastUtils.getParentOfType(call, true, UMethod.class);
    if (uMethod == null || !"onCreate".equals(uMethod.getName())) return;

    SuperOnCreateFinder finder = new SuperOnCreateFinder(call);
    uMethod.accept(finder);
    if (!finder.isSuperOnCreateCalled()) {
      context.report(ISSUE, call, context.getLocation(call),
        "calling `Folivora.installViewFactory()` before super.onCreate can cause runtime failure");
    }
  }

  private static class SuperOnCreateFinder extends AbstractUastVisitor {
    /** The target installViewFactory call */
    private final UCallExpression target;
    /** Whether we've found the super.onCreate() before folivora installed */
    private boolean ok;
    /** Whether we've seen the target super.onCreate() node yet */
    private boolean onCreateFound;

    private SuperOnCreateFinder(UCallExpression target) {
      this.target = target;
    }

    @Override
    public boolean visitCallExpression(UCallExpression node) {
      if (node == target || node.getPsi() != null && node.getPsi() == target.getPsi()) {
        if (onCreateFound) {
          ok = true;
          return true;
        }
      } else {
        if ("onCreate".equals(LintUtils.getMethodName(node))
          && node.getReceiver() != null
          && "super".equals(node.getReceiver().toString())) {
          onCreateFound = true;
        }
      }
      return super.visitCallExpression(node);
    }

    boolean isSuperOnCreateCalled() {
      return ok;
    }
  }
}
