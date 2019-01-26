package com.android.utils;

import java.util.Formatter;

public interface ILogger {

  /**
   * Prints an error message.
   *
   * @param t         is an optional {@link Throwable} or {@link Exception}. If non-null, its
   *                  message will be printed out.
   * @param msgFormat is an optional error format. If non-null, it will be printed
   *                  using a {@link Formatter} with the provided arguments.
   * @param args      provides the arguments for errorFormat.
   */
  void error(Throwable t, String msgFormat, Object... args);

  /**
   * Prints a warning message.
   *
   * @param msgFormat is a string format to be used with a {@link Formatter}. Cannot be null.
   * @param args      provides the arguments for warningFormat.
   */
  void warning(String msgFormat, Object... args);

  /**
   * Prints an information message.
   *
   * @param msgFormat is a string format to be used with a {@link Formatter}. Cannot be null.
   * @param args      provides the arguments for msgFormat.
   */
  void info(String msgFormat, Object... args);

  /**
   * Prints a verbose message.
   *
   * @param msgFormat is a string format to be used with a {@link Formatter}. Cannot be null.
   * @param args      provides the arguments for msgFormat.
   */
  void verbose(String msgFormat, Object... args);

}
