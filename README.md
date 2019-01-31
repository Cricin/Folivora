### Folivora

* 还在只为给一个view添加一个圆角背景而创建一个drawable/round_rect.xml而纠结？
* drawable目录下的文件越来越多？好多文件都只是被引用了一次？

现在可以使用Folivora，在layout文件中直接定义drawable。

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview.gif"></img>

### 使用方法
 - **STEP1** :
添加Gradle依赖，在项目的build.gradle中加入
```groovy
  dependencies {
    implementation 'cn.cricin:folivora:0.0.1'
  }
```

 - **STEP2** :
在layout.xml中加入自定义的属性, 告诉Folivora如何创建drawable

> 圆角的shape
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_shape.png"></img>

```xml
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text="round rect"
  app:drawableType="shape"
  app:setAs="background"
  app:shapeCornerRadius="6dp"
  app:shapeSolidColor="@color/red"/>
```

> 圆形的shape
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_shape_circle.png"></img>
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:gravity="center"
    android:text="circle shape"
    app:drawableType="shape"
    app:setAs="background"
    app:shapeCornerRadius="40dp"
    app:shapeSolidColor="@color/colorAccent"
    app:shapeSolidSize="80dp"
    app:shapeType="rectangle"/>
```

> layerlist
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_layerlist.png"></img>
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:padding="12dp"
    android:text="layerlist"
    app:drawableType="layer_list"
    app:layerItem0Drawable="#ff00ddff"
    app:layerItem1Drawable="@color/colorPrimary"
    app:layerItem1Insets="4dp"
    app:layerItem2Drawable="@color/colorAccent"
    app:layerItem2Insets="8dp"
    app:setAs="background"/>

> selector
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_selector.png"></img>
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:padding="12dp"
    android:text="selector"
    app:drawableType="selector"
    app:selectorStateNormal="@color/colorAccent"
    app:selectorStatePressed="@color/colorPrimary"
    app:setAs="background"/>

> ripple
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_ripple.png"></img>
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="10dp"
    android:padding="20dp"
    android:text="ripple"
    app:drawableType="ripple"
    app:rippleColor="@android:color/white"
    app:rippleContent="@color/colorAccent"
    app:setAs="background"/>

注: 许多 IDE (Android Studio, IntelliJ) 会把这些属性标注为错误，但是实际上是正确的。可以在这个View或者根ViewGroup上加上`tools:ignore="MissingPrefix"`来避免报错。为了使用 `ignore`属性，可以加上`xmlns:tools=" http://schemas.android.com/tools"`。关于这个问题，可以查看： https://code.google.com/p/android/issues/detail?id=65176.

 - **STEP3** :
在Activity中注入Folivora, Folivora可以通过两种方法注入：
```java
public class MainActivity extends AppCompatActivity {
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Folivora.wrap(newBase));
  }
}
```
或者
```java
public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Folivra.installViewFactory(this);
    setContentView(R.layout.your_layout_xml_name);
  }
}

```

### Android Studio预览支持
在Android Studio中提供了实时预览编辑layout文件时，但是IDE不识别自定义的属性，预览窗口渲染不出自定义的View背景

为了解决这个问题，Folivora提供了支持工具，按下面的方式使用：

1. 下载jar包 [点击下载](https://raw.githubusercontent.com/Cricin/Folivora/master/preview_support/release/folivora_preview_support.jar)。
2. 拷贝下载的文件到Android Studio安装目录下的plugins/android/lib/下
3. 重启IDE，如果你的项目依赖中有Folivora，打开layout文件即可实时预览

注: 支持工具依赖java的classloader加载类的顺序(替换LayoutLibraryLoader)，所以下载的jar包请不要重命名，直接拷贝即可

> 预览效果
<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/studio_preview.png" width="30%"></img>

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
