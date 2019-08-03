# SwitchButton
### 仿微信的滑动按钮，如有问题欢迎issue

## Pre

虽然Android原生自带一个Switch按钮，但是我觉得还是自己动手做的更好用一些，于是就仿照微信设置的滑动按钮自己实现了一下，除了颜色，看起来和微信的差不多，而且实现的原理还挺简单。

## Preview

{% asset_img sb1.gif sb1 %}

## How to install?

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
        });
switchButton.setCircleColor(Color.WHITE);
switchButton.setCloseBackground(Color.GRAY);
switchButton.setOpenBackground(Color.MAGENTA);
switchButton.setInterpolator(SwitchButton.OVER_SHOOT);
switchButton.open();
```

circleRadius只能在xml中配置。

## Attrs

| 名字 | 格式 | 描述 |
| :--: | :--: | :--: |
|      |      |      |

