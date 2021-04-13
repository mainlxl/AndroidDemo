![](image/mainli.svg)
# **[Mainli](https://wiki.96kg.cn) - 学习以及Demo编写**

|序号|名称|Demo相关Activity|备注|
|:-:|:-:|:-:|:-:|
|0|Test|[TestActivity][TestActivity]|预留测试|
|1|骰子DrawableDice|[DiceActivity]|微信骰子效果|
|2|onPostCreate & onPostResume|[TestPostCallMethodActivity][TestPostCallMethodActivity]|生命周期方法调用测试|
|3|帧动画优化内存|[AnimationsContainerTestActivity][AnimationsContainerTestActivity]|阿里文档中看到的[原作者][animations]|
|4|APT-Demo|[TestAPTActivity][TestAPTActivity]|使用编译时注解自动生成编译时输出的Log信息|
|5|验证码控件|[NumberCaptchaActivity][NumberCaptchaActivity]|自定义EditText实现验证码-扩展![](https://i.imgur.com/YOOqJPS.png)|
|6|Emoji输入键盘实现|[EmojiActivity][EmojiActivity]|简单实现聊天输入emoji表情![](https://i.imgur.com/LErYvTj.png)|
|7|富文本编辑|[RichMediaActivity][RichMediaActivity]<br/><br/>[实现过程分享][RichMedia]|链接输入以及转换为markdown上报服务端,以及markdown转换显示并支持点击![](https://i.imgur.com/ihaMNzQ.gif)|
|8|`ViewGroup`在指定位置添加子View(不包含`RecyclerView`等依赖`ViewHolder`的`ViewGroup`),已封装成工具类<br/>[AttachPosition.java][AttachPosition.java]|[TestLayoutChangeListenerActivity][TestLayoutChangeListenerActivity]|实现LinearLayout不同位置添加加View![](https://i.loli.net/2019/04/24/5cc021a837a15.png)|
|9|统一处理的子View圆角|[RoundActivity][RoundActivity]|动态改变View圆角,SDK>=21采用`android.view.ViewOutlineProvider`实现,小于21采用PorterDuffXfermode实现。![](https://i.loli.net/2019/04/24/5cc022efc865b.gif)<br/>真机比较流畅|
|10|自定义ViewPager练习,多点触控冲突解决|[TestViewPagerActivity][TestViewPagerActivity]|![](https://i.loli.net/2019/04/24/5cc024c4c1dab.gif)|

---------------------------------------------
## :blush:**分享**
1. [hash值生成有趣的头像](/DOC/avater.md)![][avater1]![][avater2]![][avater3]![][avater4]![][avater5]
2. [GFM语法(GitHub Flavored Markdown)](https://github.com/guodongxiaren/README)
3. [RxJava之map转换源码解析](rxlib/DOC/map.md)

## :book:**资料整理**
1. [Kotlin学习资料](https://github.com/enbandari/Kotlin-Tutorials)
2. [贝塞尔曲线学习应用](https://github.com/Android-Mainli/bezier)
3. [Android-AES Native实现](https://github.com/Android-Mainli/Native-Encrypt)


## :book:**阿里文档**
1. [阿里巴巴Java规范手册](PDF/阿里巴巴Java规范手册.pdf)    |  [读Ali-java规范手册随笔](DOC/读AliJava规范手册随笔.md)<br/>
2. [阿里巴巴Android开发手册](PDF/阿里巴巴Android开发手册.pdf)   |   [读Ali-Android开发手册随笔](DOC/读AliAndroid开发手册随笔.md)










[TestActivity]:app/src/main/java/com/mainli/TestActivity.java
[DiceActivity]:app/src/main/java/com/mainli/activity/DiceActivity.kt
[TestPostCallMethodActivity]:app/src/main/java/com/mainli/activity/TestPostCallMethodActivity.kt
[AnimationsContainerTestActivity]:app/src/main/java/com/mainli/activity/AnimationsContainerTestActivity.java
[TestAPTActivity]:app/src/main/java/com/mainli/activity/TestAPTActivity.java
[NumberCaptchaActivity]:app/src/main/java/com/mainli/activity/NumberCaptchaActivity.kt
[EmojiActivity]:app/src/main/java/com/mainli/activity/EmojiActivity.java
[RichMediaActivity]:app/src/main/java/com/mainli/activity/RichMediaActivity.java
[TestLayoutChangeListenerActivity]:app/src/main/java/com/mainli/activity/TestLayoutChangeListenerActivity.java
[RoundActivity]:app/src/main/java/com/mainli/activity/RoundActivity.kt
[TestViewPagerActivity]:app/src/main/java/com/mainli/activity/TestViewPagerActivity.kt




[AttachPosition.java]:app\src\main\java\com\mainli\utils\AttachPosition.java





[avater1]:http://www.gravatar.com/avatar/88593401?s=30&d=identicon
[avater2]:http://www.gravatar.com/avatar/88593401?s=30&d=monsterid
[avater3]:http://www.gravatar.com/avatar/88593401?s=30&d=wavatar
[avater4]:http://www.gravatar.com/avatar/88593401?s=30&d=retro
[avater5]:http://www.gravatar.com/avatar/88593401?s=30&d=robohash
[animations]:https://github.com/VDshixiaoming/AnimationTest
[RichMedia]:https://wiki.96kg.cn/2019/03/%E8%87%AA%E5%AE%9A%E4%B9%89View-%E9%92%88%E5%AF%B9EditText%E7%9A%84Markdown%E9%93%BE%E6%8E%A5%E5%BC%8F%E6%96%87%E6%9C%AC%E7%BC%96%E8%BE%91.html
