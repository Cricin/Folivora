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
* 自定义的Drawable **(新增)**

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview.gif" width="50%" height="50%"></img>

### 使用方法
 - **STEP1** :
添加Gradle依赖，在项目的build.gradle中加入
```groovy
  dependencies {
    implementation 'cn.cricin:folivora:0.0.6'
  }
```

 - **STEP2** :
在layout.xml中加入自定义的属性, 告诉Folivora如何创建drawable，Folivora提供的内置drawable属性前缀如下

* shape       -> shape
* selectror   -> selector
* layer-list  -> layer
* level-list  -> level
* clip        -> clip
* scale       -> scale
* inset       -> inset
* ripple      -> ripple
* animation   -> anim

例如所有的shape属性设置的前缀都是shape, 如`shapeSolidColor`, `shapeCornerRadius`等, 在设置了`drawableType`
之后，敲出指定的前缀，IDE会自动的给出所有该drawableType可用的属性

> shape

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_shape.png"></img>

我们来试着在xml中书写Folivora为我们提供的属性来实现上图中第一个的圆角shape效果

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:text="shape1"
  android:gravity="center"
  android:textColor="@android:color/white"
  app:drawableType="shape"
  app:shapeCornerRadius="6dp"
  app:shapeSolidColor="@color/blue_light"/>
```

我们在看一下其他几种drawable的用法：

> layerlist

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_layerlist.png"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:text="layerlist"
  android:gravity="center"
  android:textColor="@android:color/white"
  app:drawableType="layer_list"
  app:layerItem0Drawable="@color/blue_light"
  app:layerItem1Drawable="@color/blue_dark"
  app:layerItem1Insets="4dp"
  app:layerItem2Drawable="@color/blue_bright"
  app:layerItem2Insets="8dp"/>
```

> levellist

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_levellist.png"></img>

```xml
<!-- this level-list level is 95, levelItem1 matches -->
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="levellist"
  android:textColor="@android:color/white"
  app:drawableType="level_list"
  app:levelCurrentLevel="95"
  app:levelItem0Drawable="@color/green_dark"
  app:levelItem1Drawable="@color/blue_light"
  app:levelItem1MaxLevel="100"
  app:levelItem1MinLevel="90"/>
```

> selector

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_selector.gif"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:textColor="@android:color/white"
  android:gravity="center"
  android:text="selector"
  app:drawableType="selector"
  app:selectorStateNormal="@color/blue_light"
  app:selectorStatePressed="@color/blue_dark"/>
```

> ripple

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_ripple.gif"></img>

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:textColor="@android:color/white"
  android:gravity="center"
  android:text="ripple"
  app:drawableType="ripple"
  app:rippleColor="@android:color/white"
  app:rippleContent="@color/blue_light"/>
```

使用ripple的确是酷炫多了，但是ripple效果是5.0之后引入的，那5.0之前的设备怎么办呢，Folivora为你提供了`RippleFallback`接口，用来创建一个替换`RippleDrawable`的`Drawable`实例，让我们试着用一个selector来代替ripple:
```java
Folivora.setRippleFallback(new Folivora.RippleFallback()){
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
  app:clipDrawable="@color/blue_light"
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
  app:insetDrawable="@color/blue_light"/>
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
  app:scaleDrawable="@color/blue_light"
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

注: 如果你在layout文件中用Folivora为系统控件添加drawable，如`View`和`TextView`等，许多 IDE (Android Studio, IntelliJ) 会把这些Folivora提供的属性标注为错误，但是实际上是正确的。可以在这个View或者根ViewGroup上加上`tools:ignore="MissingPrefix"`来避免报错。为了使用 `ignore`属性，可以加上`xmlns:tools=" http://schemas.android.com/tools"`。关于这个问题，可以查看： https://code.google.com/p/android/issues/detail?id=65176.

### 使用嵌套的shape

Folivora现在支持在drawable中嵌套shape了，除了animation以外，所有的drawable的子drawable除了可以使用`@drawable/xxx`和颜色之外，新增了shape/shape1/shape2/shape3/shape4这5个值，参考定义shape的例子，替换相应的前缀即可, 我们来定义嵌套了shape的selector试一试

```xml
<TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="selector"
  android:textColor="@android:color/white"
  app:drawableType="selector"
  app:selectorStateNormal="shape"
  app:shapeSolidColor="@color/blue_light"
  app:shapeCornerRadius="10dp"
  app:selectorStatePressed="shape1"
  app:shape1SolidColor="@color/blue_dark"
  app:shape1CornerRadius="10dp"/>
```

效果是这样的

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_shape_nested.gif"></img>

### 使用自定义Drawable

从0.0.4版本开始，Folivora除了支持自带的drawable以外，还支持使用自定的drawable类型了，让你使用自定义drawable就和使用自定义view一样轻松。这里我们以自定义一个绘制纸风车的`WindmillDrawable`为例，来让Folivora为我们提供支持：

1. 首先我们和自定义`View`一样，为`WindmillDrawable`提供自定义的属性：
```xml
<!-- 和自定义view相同，这里declare-styleable的name最好和自定义drawable的名字一样 -->
<declare-styleable name="WindmillDrawable">
    <attr name="wdSize" format="dimension"/> <!-- 纸风车的默认大小 -->
    <attr name="wdColor0" format="color"/> <!-- 纸风车第一个叶子的颜色 -->
    <attr name="wdColor1" format="color"/> <!-- 纸风车第二个叶子的颜色 -->
    <attr name="wdColor2" format="color"/> <!-- 纸风车第三个叶子的颜色 -->
    <attr name="wdColor3" format="color"/> <!-- 纸风车第四个叶子的颜色 -->
    <attr name="wdCenterDotRadius" format="dimension"/> <!-- 中心圆的半径 -->
    <attr name="wdCenterDotColor" format="color"/> <!-- 中心圆的填充色 -->
    <attr name="wdRotateDegrees" format="integer"/> <!-- 纸风车旋转角度 -->
  </declare-styleable>
```
可以看到，自定义属性这部分和普通的`View`自定义属性是一样的。name和自定义drawable的类名相同就行了，Folivora就可以在layout文件中为这些drawable的自定义属性提供属性的自动提示了

2. 创建自定义的`WindmillDrawable`，继承自`Drawable`, 提供一个`public WindmillDrawable(Context ctx, AttributeSet attrs)`的构造方法，在这个构造方法里就可以获取自定义的属性, 代码如下：
```java
public WindmillDrawable(Context ctx, AttributeSet attrs) {
  TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.WindmillDrawable);
  int count = a.getIndexCount();
  for (int i = 0; i < count; i++) {
    int index = a.getIndex(i);
    switch (index) {
      case R.styleable.WindmillDrawable_wdSize:
        mSize = a.getDimensionPixelSize(index, mSize);
        break;
      case R.styleable.WindmillDrawable_wdColor0:
        mColors[0] = a.getColor(index, mColors[0]);
        break;
      case R.styleable.WindmillDrawable_wdColor1:
        mColors[1] = a.getColor(index, mColors[1]);
        break;
      ...
      default://no-op unexpected attr index
        break;
    }
  }
  a.recycle();
}
```
这部分代码其实和自定义`View`的属性获取没有什么区别，主要就是给drawable添加一个构造方法，具体绘制代码就不贴了，如果想要查看具体细节，可以点击[这里](https://github.com/Cricin/Folivora/blob/master/sample/src/main/java/cn/cricin/folivora/sample/drawable/WindmillDrawable.java)查看源码

3. 在layout文件中使用自定义drawable，Folivora提供了`drawableName`属性，使用该属性指定需要使用的drawable：
```xml
<View
  andorid:layout_width="120dp"
  android:layout_height="120dp"
  app:drawableName="cn.cricin.folivora.sample.drawable.WindmillDrawable"
  app:wdColor0="@color/blue_light"
  app:wdColor1="@color/green_dark"
  app:wdColor2="@color/green_light"
  app:wdColor3="@color/purple"
  app:wdRotateDegrees="45"/>
```
运行之后的效果：

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/preview_custom_drawable.png"></img>


到这里，Folivora就会为该`View`设置我们指定的drawable了，有人可能就会问了，drawable名字这么长，写起来会不会太复杂了，不用担心，当你敲出drawableName的时候，Folivora会为你自动提示可用的drawable名字的，并且该drawable的自定义属性也会有自动提示。

> 如果我的自定义drawable没有上面指定的构造方法，并且我没办法直接修改该drawable的源码来添加这个构造方法该怎么办呢？

Folivora考虑到了这一点，有些drawable的源码我们没法修改，但是它总会有向外提供设置属性的方法吧？所以，我们提供了一个`DrawableFactory`接口，假设`WindmillDrawable`只有一个无参的构造方法，但是提供了设置各种属性的方法，我们需要让Folivora支持`WindmillDrawable`，可以这样做：
```java
Folivora.addDrawableFactory(new Folivora.DrawableFactory() {
  @Override
  public Drawable newDrawable(Context context, AttributeSet attrs) {
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WindmillDrawable);
    WindmillDrawable d = new WindmillDrawable();
    d.setColor0(a.getColor(R.styleable.WindmillDrawable_wdColor0, Color.BLACK));
    d.setRotateDegrees(a.getInt(R.styleable.WindmillDrawable_wdRotateDegrees, 0));
    ...
    a.recycle();
    return d;
  }

  @Override
  public Class<? extends Drawable> drawableClass() {
    return WindmillDrawable.class;
  }
});
```
> 自定义Drawable请注意，如果你的drawable需要获取其他drawable，建议使用`Folivora.getDrawable(Context ctx, TypedArray a, AttributeSet attrs, int attrIndex)`方法获取，这样可以支持获取内嵌的`shape`，当然如果你不需要支持内嵌的`shape`，可以不用这样做。

 - **STEP3** :
在Activity中启用Folivora, 有两种方法：
1.
```java
public class MainActivity extends Activity {
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Folivora.wrap(newBase));
  }
}
```
2.
```java
public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Folivora.installViewFactory(this);
    setContentView(R.layout.your_layout_xml_name);
  }
}

```

### 下载示例APK
[点击下载](https://raw.githubusercontent.com/Cricin/Folivora/master/sample.apk)

### 编辑layout文件时的预览

> 预览效果

<img src="https://raw.githubusercontent.com/Cricin/Folivora/master/pics/studio_preview.gif"></img>

依赖了Folivora之后，默认是可以直接预览的，如果没有效果，尝试build一下你的项目，如果还是没有效果，可以使用Folivora自带支持预览的插桩`View`，这些插桩`View`在运行时会被指定的View替换掉，不会对原来的view树结构产生任何影响，例如，如果你想要支持`TextView`的实时预览，你可以使用`cn.cricin.folivora.view.TextView`代替原来的`TextView`, 代码如下:
```xml
<!-- this becomes android.widget.TextView at runtime -->
<cn.cricin.folivora.view.TextView
  android:layout_width="100dp"
  android:layout_height="40dp"
  android:gravity="center"
  android:text="Stubbed TextView"
  android:textColor="@color/white"
  app:drawableType="shape"
  app:shapeCornerRadius="10dp"
  app:shapeSolidColor="@color/blue_light"/>
```
Folivora对系统常用的控件的预览提供了支持，如`Button`，`TextView`，`ImageView`等，使用这些控件即可实时预览。

> 对于你自己或者第三方的控件，如何提供预览支持呢?

Folivora也是支持的，例如RecyclerView在预览时是不支持Folivora的，让它支持预览可以这样做：
```java
public class StubRecyclerView extends RecyclerView {
  public StubRecyclerView(Context ctx, AttributeSet attrs){
    super(ctx, attrs);
    if (!isInEditMode()) {
      throw new IllegalStateException("this view only available at design time");
    }
    Folivora.applyDrawableToView(this, attrs);
  }
}
```
在xml代码中就可以使用了：
```xml
<your.package.name.StubRecyclerView
  android:layout_width="120dp"
  android:layout_height="120dp"
  app:replacedBy="android.support.v7.widget.RecyclerView"
  app:drawableType="shape"
  app:shapeSolidColor="@color/black"
  app:shapeCornerRadius="10dp"/>
```
可以看到，我们指定了`replacedBy`属性, 告诉Folivora需要把这个`StubRecyclerView`替换成`RecyclerView`，replacedBy也是支持自动提示的，注意如果没有该属性，在运行时`StubRecyclerView`不会被替换，导致直接抛出异常。如果不想每次都写`replacedBy`，可以使用`ReplacedBySuper`这个接口, Folivora会自动的用父类替换它. 让我们修改一下我们的StubRecyclerView：
```java
public class StubRecyclerView extends RecyclerView implements ReplacedBySuper {
...
```

### Folivora支持的属性列表

##### 通用属性

属性 | 取值| 描述
 ---|--- | --- |
app:setAs|background(default) &#124; src &#124; foreground| 设置view背景或者ImageView的src或者view前景
app:drawableType|shape &#124; layer_list &#124; selector &#124; ripple &#124; clip &#124; scale &#124; animation &#124; level_list|drawable类型
app:drawableName|string|自定义drawable的class全名
app:replacedBy|string|需要替换当前view的view class全名

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
