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

package cn.cricin.folivora.uipreview;

import com.intellij.openapi.diagnostic.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class DebugLog {
  private static final Logger LOG = Logger.getInstance(DebugLog.class);
  private static final boolean ENABLED = false;

  public static void debug(String message) {
    if (ENABLED) {
      LOG.debug(message);
    }
  }

  public static void info(String message) {
    if (ENABLED) {
      LOG.info(message);
    }
  }

  public static void warn(String message) {
    if (ENABLED) {
      LOG.warn(message);
    }
  }

  public static void warn(String message, Throwable err) {
    if (ENABLED) {
      LOG.warn(message, err);
    }
  }

  public static void error(String message) {
    if (ENABLED) {
      LOG.error(message);
    }
  }

  public static void error(String message, Throwable err) {
    if (ENABLED) {
      LOG.error(message, err);
    }
  }
}
