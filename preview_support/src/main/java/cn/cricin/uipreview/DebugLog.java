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

package cn.cricin.uipreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Queue;

public final class DebugLog {
  private static final boolean DEBUG = true;
  private static boolean sThreadStarted = false;
  private static FileOutputStream sOut;
  private static Queue<String> sLogQueue = new LinkedList<String>() {
    @Override
    public synchronized String poll() {
      return super.poll();
    }

    @Override
    public synchronized boolean add(String s) {
      return super.add(s);
    }
  };

  public static void logLine(String text) {
    if (DEBUG) {
      startLogThread();
      sLogQueue.add(text + "\n");
    }
  }

  static void log(String text) {
    if (DEBUG) {
      startLogThread();
      sLogQueue.add(text);
    }
  }

  static void logLine(Throwable e) {
    if (DEBUG) {
      startLogThread();
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      e.printStackTrace(printWriter);
      printWriter.close();
      sLogQueue.add(stringWriter.toString());
    }
  }

  private static void logInner(String text) {
    if (!DEBUG) return;
    try {
      sOut.write(text.getBytes());
      sOut.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void startLogThread() {
    if (sThreadStarted) return;
    LogThread t = new LogThread();
    t.start();
    sThreadStarted = true;
  }

  static class LogThread extends Thread {
    LogThread() {
      setDaemon(true);
    }

    @SuppressWarnings("all")
    @Override
    public void run() {
      File logFile = new File(new File(System.getProperty("user.home")), "folivora_debug.txt");
      if (logFile.exists()) logFile.delete();
      try {
        sOut = new FileOutputStream(logFile);
      } catch (FileNotFoundException ignore) {
      }
      while (true) {
        String poll = sLogQueue.poll();
        if (poll != null) {
          logInner(poll);
        } else {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }


}
