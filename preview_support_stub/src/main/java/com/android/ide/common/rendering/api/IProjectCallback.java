package com.android.ide.common.rendering.api;

public interface IProjectCallback {

  Object loadView(String name, Class<?>[] paramClasses, Object[] paramsArr);

}
