# SwitchButton
### 仿微信的滑动按钮，如有问题欢迎[issue](https://github.com/rain9155/SwitchButton/issues)

## Pre

虽然Android原生自带一个Switch按钮，但是我觉得还是自己动手做的更好用一些，于是就仿照微信设置的滑动按钮自己实现了一下，除了颜色，看起来和微信的差不多，而且实现的原理还挺简单。

## Preview

![sb1](/screenshots/sb1.gif)

## How to install?
在app目录的build.gradle中添加，如下：
```
dependencies{
    implementation 'com.jianyu:switchbutton:1.0.0'
}
```

## How to use?

直接在xml布局文件中引用，如下：

```xml
<com.example.library.SwitchButton
	android:id="@+id/sb_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

当然，上面都是使用默认的属性，你也可以自定义属性，如下：

```xml
<com.example.library.SwitchButton
 	android:id="@+id/sb_button"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:sb_interpolator="Accelerate"
    app:sb_status="open"
    app:sb_circleRadius="10dp"
    app:sb_closeBackground="@android:color/black"
    app:sb_openBackground="@android:color/holo_red_light"
    app:sb_circleColor="@android:color/white" />
```

当然，你也可以用代码配置SwitchButton属性，如下：

```java
switchButton.setOnStatusListener(new SwitchButton.OnStatusListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_SHORT).show();
            }
        });//SwitchButton.OnStatusListener()是按钮打开和关闭的状态回调
switchButton.setCircleColor(Color.WHITE);
switchButton.setCloseBackground(Color.GRAY);
switchButton.setOpenBackground(Color.MAGENTA);
switchButton.setInterpolator(SwitchButton.OVER_SHOOT);
switchButton.open();//同理，有一个close()函数
```

circleRadius只能在xml中配置。

## Attrs

|        名字        |                    描述                    |
| :----------------: | :----------------------------------------: |
| sb_openBackground  |           按钮打开状态的轨道颜色           |
| sb_closeBackground |           按钮关闭状态的轨道颜色           |
|  sb_circleRadius   |              按钮的小圆的半径              |
|   sb_circleColor   |              按钮的小圆的颜色              |
|     sb_status      |       按钮的初始状态：open 或 close        |
|  sb_interpolator   | 按钮滑动时的速度，默认是Linear，即线性速度 |

