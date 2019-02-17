### 为什么需要Folivora
对于android开发者来说，在layout文件中引用drawable来设置`View`的背景或者`ImageView`的`src`是很常见的事情，需要我们在drawable文件夹下写好xml文件就可以应用了，但是有许多drawable文件可能只被使用了一次，也有可能我们只是为了实现一个简单的圆角背景的功能。越来越多的drawable文件导致开发和维护成本的增加，有没有什么方法可以直接在layout文件中去创建drawable呢，Folivora为你提供了这样的功能。

### Folivora能做什么
Folivora可以为你的View设置一个背景或者ImageView的src,当前支持的drawable类型有

* shape (GradientDrawable)
* selector (StateListDrawable)
* ripple (RippleDrawable)
* layerlist (LayerListDrawable)
* levellist (LevelListDrawable)
* inset (InsetDrawable)
* clip (ClipDrawable)
* scale (ScaleDrawable)
* animation (AnimationDrawable)

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview.gif"></img>

### 使用方法
 - **STEP1** :
添加Gradle依赖，在项目的build.gradle中加入
```groovy
  dependencies {
    implementation 'cn.cricin:folivora:0.0.2'
  }
```

 - **STEP2** :
在layout.xml中加入自定义的属性, 告诉Folivora如何创建drawable

> shape

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_shape.png"></img>

```xml
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:text="round rect"
  app:drawableType="shape1"
  app:shapeCornerRadius="6dp"
  app:shapeSolidColor="@color/colorAccent"/>
```

> layerlist

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_layerlist.png"></img>

```xml
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
  app:layerItem2Insets="8dp"/>
```

> levellist

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_levellist.png"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:layout_marginLeft="10dp"
  android:clickable="true"
  android:focusable="true"
  android:gravity="center"
  android:text="levellist"
  android:textColor="@android:color/white"
  app:drawableType="level_list"
  app:levelCurrentLevel="95"
  app:levelItem0Drawable="@color/colorPrimary"
  app:levelItem1Drawable="@color/colorAccent"
  app:levelItem1MaxLevel="100"
  app:levelItem1MinLevel="90"/>
```

> selector

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_selector.gif"></img>

```xml
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:layout_marginTop="10dp"
  android:padding="12dp"
  android:text="selector"
  app:drawableType="selector"
  app:selectorStateNormal="@color/colorAccent"
  app:selectorStatePressed="@color/colorPrimary"/>
```

> ripple

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_ripple.gif"></img>

```xml
<TextView
  android:layout_width="wrap_content"
  android:layout_height="wrap_content"
  android:layout_marginTop="10dp"
  android:padding="20dp"
  android:text="ripple"
  app:drawableType="ripple"
  app:rippleColor="@android:color/white"
  app:rippleContent="@color/colorAccent"/>
```

使用ripple的确是酷炫多了，但是ripple效果是5.0之后引入的，那5.0之前的设备怎么办呢，Folivora为你提供了`RippleFallback`接口，用来创建一个替换`RippleDrawable`的`Drawable`实例，让我们试着用一个selector来代替ripple:
```java
Folivora.setRippleFallback(new RippleFallback()){
  @Override
  public Drawable onFallback(ColorStateList ripple, Drawable content, Drawable mask, Context ctx){
    StateListDrawable sld = new StateListDrawable();
    sld.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(ripple.getDefaultColor()));
    sld.addState(new int[0], content);
    return sld;
  }
}
```

> clip

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_clip.png"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="clip"
  android:textColor="@android:color/white"
  app:clipDrawable="@color/colorAccent"
  app:clipLevel="6000"
  app:drawableType="clip"/>
```

> inset

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_inset.png"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="inset"
  android:textColor="@android:color/white"
  app:drawableType="inset"
  app:insetAll="4dp"
  app:insetDrawable="@color/colorAccent"/>
```

> scale

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_scale.png"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="scale"
  android:textColor="@android:color/white"
  app:drawableType="scale"
  app:scaleDrawable="@color/colorAccent"
  app:scaleGravity="center"
  app:scaleHeight="0.3"
  app:scaleWidth="0.3"/>
```

> animation

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_animation.gif"></img>

```xml
<TextView
  android:id="@+id/animation"
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:layout_marginLeft="10dp"
  android:gravity="center"
  android:text="animation"
  android:textColor="@android:color/white"
  app:animAutoPlay="true"
  app:animDuration="300"
  app:animFrame0="@drawable/animation0"
  app:animFrame1="@drawable/animation1"
  app:animFrame2="@drawable/animation2"
  app:animFrame3="@drawable/animation3"
  app:animFrame4="@drawable/animation4"
  app:animFrame5="@drawable/animation5"
  app:animFrame6="@drawable/animation6"
  app:animFrame7="@drawable/animation7"
  app:animFrame8="@drawable/animation8"
  app:animFrame9="@drawable/animation9"
  app:drawableType="animation"/>
```

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


### Folivora支持的属性列表

##### 通用属性

属性 | 取值| 描述
 ---|--- | --- |
app:setAs|background(default) &#124; src| 设置view背景或者ImageView的src
app:drawableType|shape &#124; layer_list &#124; selector &#124; ripple|drawable类型(必须设置)

##### shape属性

属性 | 取值| 描述
 ---|--- | --- |
app:shapeType|rectangle(default)&#124;oval&#124;line&#124;ring|形状
app:shapeSolidSize|dimension|宽高
app:shapeSolidWidth|dimension|宽
app:shapeSolidHeight|dimension|高
app:shapeSolidColor|color|填充色
app:shapeSolidColor|color|边框填充色
app:shapeStokeWidth|dimension|边框厚度
app:shapeStokeDashWidth|dimension|边框线宽
app:shapeStokeDashGap|dimension|边框线间距
app:shapeCornerRadius|dimension|角半径
app:shapeCornerRadiusTopLeft|dimension|左上角半径
app:shapeCornerRadiusTopRight|dimension|右上角半径
app:shapeCornerRadiusBottomLeft|dimension|坐下角半径
app:shapeCornerRadiusBottomRight|dimension|右下角半径
app:shapeGradientType|linear &#124; radial &#124; sweep|渐变类型
app:shapeGradientAngle|tb &#124; tr_bl &#124; rl &#124; br_tl &#124; bt &#124; bl_tr &#124; lr &#124; tl_br|渐变角度
app:shapeGradientStartColor|color|渐变起始颜色
app:shapeGradientCenterColor|color|渐变中间颜色
app:shapeGradientEndColor|color|渐变结束颜色
app:shapeGradientRadius|dimension|渐变半径
app:shapeGradientCenterX|dimension|渐变中点x轴位置
app:shapeGradientCenterY|dimension|渐变中点y轴位置

##### selector属性

属性 | 取值| 描述
 ---|--- | --- |
app:selectorStateFirst|reference &#124; color|selector状态：第一个
app:selectorStateMiddle|reference &#124; color|selector状态：中间
app:selectorStateLast|reference &#124; color|selector状态：最后一个
app:selectorStateActive|reference &#124; color|selector状态：活动
app:selectorStateActivated|reference &#124; color|selector状态：激活的
app:selectorStateAccelerate|reference &#124; color|selector状态：加速的
app:selectorStateChecked|reference &#124; color|selector状态：勾选的
app:selectorStateCheckable|reference &#124; color|selector状态：可勾选的
app:selectorStateEnabled|reference &#124; color|selector状态：启用的
app:selectorStateFocused|reference &#124; color|selector状态：获得焦点
app:selectorStatePressed|reference &#124; color|selector状态：点击
app:selectorStateNormal|reference &#124; color|selector状态：正常状态

##### layerlist属性

属性 | 取值| 描述
 ---|--- | --- |
app:layerItem0Drawable| reference &#124; color| 最底层的drawable
app:layerItem0Insets|dimension|该drawable的margin
app:layerItem0Left|dimension| 该drawable的左margin
app:layerItem0Right|dimension|该drawable的右margin
app:layerItem0Top|dimension|该drawable的上margin
app:layerItem0Bottom|dimension|该drawable的下margin

...

layerlist支持最多5个drawable，替换相应的数字即可

##### ripple属性

属性 | 取值| 描述
 ---|--- | --- |
app:rippleColor|color|ripple点击时的涟漪色
app:rippleMask|reference &#124; color|ripple涟漪色的遮罩
app:rippleContent|reference &#124; color|ripple的内容背景

如果设备不支持Ripple效果(<Api21)，可以给Folivora设置一个`RippleFallback`, 用来创建替代RippleDrawable的Drawable

##### levellist属性

属性 | 取值| 描述
 ---|--- | --- |
app:levelCurrentLevel|integer|当前的level
app:levelItem0Drawable|reference &#124; color|第一个item的drawable
app:levelItem1MinLevel|integer|该drawable的最小level
app:levelItem1MaxLevel|integer|该drawable的最大level

...

levellist支持最多5个drawable，替换相应的数字即可

##### clip属性

属性 | 取值| 描述
 ---|--- | --- |
app:clipDrawable|reference &#124; color|需要裁剪的drawable
app:clipGravity|同View的layout_gravity|裁剪位置
app:clipOrientation|vertical &#124; horizontal|裁剪的方向
app:clipLevel|integer|当前level

##### scale属性

属性 | 取值| 描述
 ---|--- | --- |
app:scaleDrawable|reference &#124; color|需要缩放的drawable
app:scaleGravity|同View的layout_gravity|缩放位置
app:scaleWidth|float[0,1] or -1()|宽度缩放比例
app:scaleHeight|float[0,1] or -1()|高度缩放比例
app:scaleLevel|integer[0,10000]|当前的level

##### inset属性

属性 | 取值| 描述
 ---|--- | --- |
app:insetDrawable|reference &#124; color|需要插入边距的drawable
app:insetAll|dimension|所有方向的边距
app:insetLeft|dimension|左边距
app:insetTop|dimension|上边距
app:insetRight|dimension|右边距
app:insetBottom|dimension|下边距

##### animation属性

属性 | 取值| 描述
 ---|--- | --- |
app:animAutoPlay|boolean|是否自动开始动画
app:animDuration|int(millisecond)|每一帧的持续时间
app:animOneShot|boolean|是否只播放一次
app:animFrame0|reference &#124; color|第0帧
app:animDuration0|int(millisecond)|第0帧持续时间

animation支持最多10帧，替换相应的数字即可

### 下载示例APK
[点击下载](https://raw.githubusercontent.com/Cricin/Folivora/master/sample.apk)

### Android Studio预览支持
在Android Studio中提供了实时预览编辑layout文件，但是IDE不识别自定义的属性，预览窗口渲染不出自定义的View背景，也无法使用属性提示

为了解决这个问题，Folivora提供了支持工具，按下面的方式使用：

1. 下载jar包 [点击下载](https://raw.githubusercontent.com/Cricin/Folivora/master/android-folivora-support.jar)。
2. 拷贝下载的文件到Android Studio安装目录下的plugins/android/lib/下
3. 重启IDE，如果你的项目依赖中有Folivora，打开layout文件即可实时预览

注: 支持工具依赖java的classloader加载类的顺序，所以下载的jar包请不要重命名，直接拷贝即可

> 预览效果

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/studio_preview.gif"></img>

## License

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
