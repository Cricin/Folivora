package com.android.ide.common.rendering.api;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;

public abstract class Bridge {

  public int getApiLevel() {throw new RuntimeException("Stub!");}

  public int getRevision() {throw new RuntimeException("Stub!");}

  public EnumSet<Capability> getCapabilities() {throw new RuntimeException("Stub!");}

  public boolean supports(int feature) {throw new RuntimeException("Stub!");}

  public boolean init(Map<String, String> platformProperties,
                      File fontLocation,
                      Map<String, Map<String, Integer>> enumValueMap,
                      LayoutLog log) {throw new RuntimeException("Stub!");}

  public boolean dispose() {throw new RuntimeException("Stub!");}

  public RenderSession createSession(SessionParams params) {throw new RuntimeException("Stub!");}

  public Result renderDrawable(DrawableParams params) {throw new RuntimeException("Stub!");}

  public void clearCaches(Object projectKey) {throw new RuntimeException("Stub!");}

  public Result getViewParent(Object viewObject) {throw new RuntimeException("Stub!");}

  public Result getViewIndex(Object viewObject) {throw new RuntimeException("Stub!");}

  public boolean isRtl(String locale) {throw new RuntimeException("Stub!");}

  public Result getViewBaseline(Object viewObject) {throw new RuntimeException("Stub!");}
}
