package cn.cricin.folivora.lint;

import cn.cricin.folivora.dom.FolivoraDomExtender;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.ApiKt;
import com.android.tools.lint.detector.api.Issue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public final class FolivoraIssueRegistry extends IssueRegistry {

  static {
    initDomExtender();
  }

  private static void initDomExtender() {
    boolean lintInsideStudio;
    try {
      Class.forName("org.jetbrains.android.dom.AndroidDomElement");
      lintInsideStudio = true;
    } catch (ClassNotFoundException e) {
      lintInsideStudio = false;
    }
    if (lintInsideStudio) {
      try {
        FolivoraDomExtender.install();
      } catch (Exception ignore) {}
    }
  }

  @NotNull
  @Override
  public List<Issue> getIssues() {
    return Collections.singletonList(InstalledBeforeSuperDetector.ISSUE);
  }

  @Override
  public int getApi() {
    return ApiKt.CURRENT_API;
  }
}
