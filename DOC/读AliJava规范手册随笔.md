# **(一)命名 命名风格 风格**
1. 【强制】类名使用 UpperCamelCase 风格，但以下情形例外： DO / BO / DTO / VO / AO /
PO 等。
<br/>正例： MarcoPolo / UserDO / XmlService / TcpUdpDeal / TaPromotion
<br/>反例： macroPolo / UserDo / XMLService / TCPUDPDeal / TAPromotion

2. 【强制】方法名、参数名、成员变量、局部变量都统一使用 lowerCamelCase 风格，必须遵从
驼峰形式。
<br/>正例： localValue / getHttpMessage() / inputUserId

3. 【强制】常量命名全部大写，单词间用下划线隔开，力求语义表达完整清楚，不要嫌名字长。
<br/>正例： MAX _ STOCK _ COUNT
<br/>反例： MAX _ COUNT

4. 【强制】抽象类命名使用 Abstract 或 Base 开头 ； 异常类命名使用 Exception 结尾 ； 测试类
命名以它要测试的类名开始，以 Test 结尾。

5. 【强制】 POJO 类中布尔类型的变量，都不要加 is 前缀 ，否则部分框架解析会引起序列化错误。
反例：定义为基本数据类型 Boolean isDeleted； 的属性，它的方法也是 isDeleted() ， RPC
框架在反向解析的时候，“误以为”对应的属性名称是 deleted ，导致属性获取不到，进而抛
出异常。

6. 【推荐】如果模块、接口、类、方法使用了设计模式，在命名时体现出具体模式。
说明：将设计模式体现在名字中，有利于阅读者快速理解架构设计理念。
<br/>正例： 
<br/>public class OrderFactory;
<br/>public class LoginProxy;
<br/>public class ResourceObserver;
