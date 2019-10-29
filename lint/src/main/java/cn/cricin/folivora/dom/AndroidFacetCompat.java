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

package cn.cricin.folivora.dom;

import com.android.tools.idea.model.AndroidModel;

import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.resourceManagers.LocalResourceManager;
import org.jetbrains.android.resourceManagers.ModuleResourceManagers;
import org.jetbrains.android.resourceManagers.ResourceManager;

import java.lang.reflect.Method;

/**
 * Since the API of AndroidFacet class changed so frequently
 * in different version of Android Studio, a compat utility
 * is needed for those API changes.
 */
final class AndroidFacetCompat {
  private static boolean sModuleResourceManagerExists = true;
  static ResourceManager getAppResourceManager(AndroidFacet facet) {
    ResourceManager manager = null;
    if (sModuleResourceManagerExists) {
      try {
        manager = ModuleResourceManagers.getInstance(facet).getResourceManager(null);
      } catch (NoClassDefFoundError ignore) {
        sModuleResourceManagerExists = false;
      }
    }
    if (!sModuleResourceManagerExists) {
      manager = LocalResourceManager.getInstance(facet.getModule());
    }
    return manager;
  }

  /**
   * Added for android studio 3.0
   */
  private static Method sIsAppProjectMethod;
  static boolean isAppProject(AndroidFacet facet) {
    if (sIsAppProjectMethod != null) {
      try {
        return (boolean) sIsAppProjectMethod.invoke(facet.getConfiguration(), (Object[]) null);
      } catch (Exception ignore) {
      }
    }
    try {
      return facet.getConfiguration().isAppProject();
    } catch (NoSuchMethodError nsme) {
      try {
        sIsAppProjectMethod = facet.getClass().getDeclaredMethod("isAppProject", (Class<?>[]) null);
        return (boolean) sIsAppProjectMethod.invoke(facet, (Object[]) null);
      } catch (Exception ignore) {
      }
    }
    return false;
  }

  /**
   * Added for android studio 4.0 canary release
   */
  private static Method mIsRequired;
  static boolean isAndroidModelRequired(AndroidFacet facet) {
    if (mIsRequired != null) {
      try {
        return (boolean) mIsRequired.invoke(null, facet);
      } catch (Throwable ignore) {
      }
    }
    try {
      return facet.requiresAndroidModel();
    } catch (Throwable t) {
      try {
        mIsRequired = AndroidModel.class.getDeclaredMethod("isRequired", AndroidFacet.class);
        return (boolean) mIsRequired.invoke(null, facet);
      } catch (Throwable ignore) {
      }
    }
    return false;
  }

  /**
   * Added for android studio 4.0 canary release
   */
  private static Method sGetMainManifest;
  static Manifest getManifest(AndroidFacet facet) {
    if (sGetMainManifest != null) {
      try {
        return (Manifest) sGetMainManifest.invoke(null, facet);
      } catch (Throwable ignore) {
      }
    }
    try {
      return facet.getManifest();
    } catch (Throwable t) {
      try {
        sGetMainManifest = Manifest.class.getDeclaredMethod("getMainManifest", AndroidFacet.class);
        return (Manifest) sGetMainManifest.invoke(null, facet);
      } catch (Throwable ignore) {
      }
    }
    return null;
  }

  private AndroidFacetCompat() {}
}
