### Folivora

* 还在只为给一个view添加一个圆角背景而创建一个drawable/round_rect.xml而纠结？
* drawable目录下的文件越来越多？好多文件都只是被引用了一次？

现在可以使用Folivora，在layout文件中直接定义drawable。

一个简单的例子

```
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text="round_rect"
  app:drawableType="shape"
  app:setAs="background"
  app:shapeCornerRadius="6dp"
  app:shapeSolidColor="@color/red"/>
```

注: 许多 IDE (Android Studio, IntelliJ) 会把这些属性标注为错误，但是实际上是正确的。可以在这个View或者根ViewGroup上加上`tools:ignore="MissingPrefix"`来避免报错。为了使用 `ignore`属性，可以加上`xmlns:tools=" http://schemas.android.com/tools"`。关于这个问题，可以查看： https://code.google.com/p/android/issues/detail?id=65176.

### Android Studio预览支持
在Android Studio中提供了实时预览编辑layout文件时，但是IDE不识别自定义的属性，预览窗口渲染不出自定义的View背景

为了解决这个问题，Folivora提供了支持工具，按下面的方式使用：

1. 下载preview_support/release/android_folivora_preview.jar。
2. 拷贝下载的文件到Android Studio安装目录下的plugins/android/lib/下
3. 重启IDE，如果你的项目依赖中有Folivora，打开layout文件即可实时预览

注: 支持工具依赖java的classloader加载类的顺序(替换LayoutLibraryLoader)，所以下载的jar包请不要重命名，直接拷贝即可

Copyright 2019 Cricin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
