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
import com.intellij.openapi.util.Pair;

import org.jetbrains.android.dom.manifest.Manifest;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.resourceManagers.LocalResourceManager;
import org.jetbrains.android.resourceManagers.ModuleResourceManagers;
import org.jetbrains.android.resourceManagers.ResourceManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

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
      } catch (Throwable ignore) {
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
  static boolean isAppProject(AndroidFacet facet) {
    Boolean value = invoke(AndroidFacet.class, "isAppProject", facet, null, null, new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return facet.getConfiguration().isAppProject();
      }
    });
    return value != null && value;
  }

  /**
   * Added for android studio 4.0 canary release
   */
  static boolean requiresAndroidModel(AndroidFacet facet) {
    Boolean value = invoke(AndroidModel.class, "isRequired", null, new Class[]{AndroidFacet.class}, new Object[]{facet}, new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return facet.requiresAndroidModel();
      }
    });
    return value != null && value;
  }

  /**
   * Added for android studio 4.0 canary release
   */
  static Manifest getManifest(AndroidFacet facet) {
    return invoke(AndroidFacet.class, "getManifest", facet, null, null, new Callable<Manifest>() {
      @Override
      public Manifest call() throws Exception {
        return Manifest.getMainManifest(facet);
      }
    });
  }

  private static Map<Pair<Class<?>, String>, Method> sInvokableMethods = new HashMap<>();

  @SuppressWarnings("unchecked")
  private static <R> R invoke(Class<?> clazz, String methodName, Object receiver, Class<?>[] argClasses, Object[] args, Callable<R> c) {
    final Pair<Class<?>, String> pair = Pair.create(clazz, methodName);
    Method method = sInvokableMethods.get(pair);
    if (method != null) {
      try {
        return (R) method.invoke(receiver, args);
      } catch (Throwable ignore) {
      }
    }
    try {
      return c.call();
    } catch (Throwable ignore) {
    }
    try {
      method = clazz.getDeclaredMethod(methodName, argClasses);
      Object result = method.invoke(receiver, args);
      sInvokableMethods.put(pair, method);
      return (R) result;
    } catch (Throwable ignore) {
    }
    return null;
  }

  private AndroidFacetCompat() {
  }
}