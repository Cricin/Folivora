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

package com.android.tools.idea.layoutlib;

import cn.cricin.uipreview.DelegateBridge;
import cn.cricin.uipreview.DebugLog;
import com.android.ide.common.rendering.api.LayoutLog;
import com.android.io.FileWrapper;
import com.android.layoutlib.bridge.Bridge;
import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkVersionInfo;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.utils.ILogger;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class LayoutLibraryLoader {

  private static final Logger LOG = Logger.getInstance("#org.jetbrains.android.uipreview.LayoutLibraryLoader");

  @SuppressWarnings("UnresolvedPropertyKey")
  public static LayoutLibrary load(IAndroidTarget target, Map<String, Map<String, Integer>>
    enumMap) throws RenderingException, IOException {
    DebugLog.logLine("LayoutLibLoader: loading layoutlib");

    String fontFolderPath = FileUtil.toSystemIndependentName(target.getPath(IAndroidTarget.FONTS));
    VirtualFile fontFolder = LocalFileSystem.getInstance().findFileByPath(fontFolderPath);
    if (fontFolder != null && fontFolder.isDirectory()) {
      String platformFolderPath = target.isPlatform() ? target.getLocation() : target.getParent()
        .getLocation();

      File platformFolder = new File(platformFolderPath);
      if (!platformFolder.isDirectory()) {
        throw new RenderingException(LayoutlibBundle.message("android.directory.cannot.be.found" +
          ".error", new Object[]{FileUtil.toSystemDependentName(platformFolderPath)}), new
          Throwable[0]);
      } else {
        File buildProp = new File(platformFolder, "build.prop");
        if (!buildProp.isFile()) {
          throw new RenderingException(LayoutlibBundle.message("android.file.not.exist.error",
            new Object[]{FileUtil.toSystemDependentName(buildProp.getPath())}), new Throwable[0]);
        } else if (!SystemInfo.isJavaVersionAtLeast("1.8") && target.getVersion()
          .getFeatureLevel() >= 24) {
          throw new UnsupportedJavaRuntimeException(LayoutlibBundle.message("android.layout" +
            ".preview.unsupported.jdk", new Object[]{SdkVersionInfo.getCodeName(target.getVersion
            ().getFeatureLevel())}));
        } else {
          ILogger logger = new LogWrapper(LOG);
          LayoutLibrary library = LayoutLibrary.load(new DelegateBridge(new Bridge()), new
            LayoutlibClassLoader(LayoutLibraryLoader.class.getClassLoader()));
          Map<String, String> buildPropMap = ProjectProperties.parsePropertyFile(new FileWrapper
            (buildProp), logger);
          LayoutLog layoutLog = new LayoutLogWrapper(LOG);
          if(library.init(buildPropMap, new File(fontFolder.getPath()), enumMap, layoutLog)){
            DebugLog.logLine("LayoutLibLoader: layoutlib loaded");
            return library;
          }else{
            DebugLog.logLine("LayoutLibLoader: layoutLib load failed");
            return null;
          }
        }
      }
    } else {
      throw new RenderingException(LayoutlibBundle.message("android.directory.cannot.be.found" +
        ".error", new Object[]{FileUtil.toSystemDependentName(fontFolderPath)}), new Throwable[0]);
    }
  }

  private LayoutLibraryLoader() {}
}
