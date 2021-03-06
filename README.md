# Fast Request

[![Jetbrains Plugins][plugin-img]][plugin]
![Version](http://phpstorm.espend.de/badge/16988/version)
![Downloads](http://phpstorm.espend.de/badge/16988/downloads)
![Downloads Last Month](http://phpstorm.espend.de/badge/16988/last-month)

**Fast Request** is a plugin based on springmvc that help you quickly generate **url** and **params**.It is also a http client tool.

Just press the shortcut key, Plugin will give you the url and params, Then click Send Request button to complete the http request

The plugin UI references [Paw](https://paw.cloud/)

If you think this plugin help you, please **πStar** project, and also welcome to provide excellent pull request

![example](./screenshot/example.gif)

- document
    * [δΈ­ζζζ‘£](README.zh_CN.md)
    * [English Document](README.md)

QQ group:754131222  
  
## 1.Install
**using IDE plugin system**
- <kbd>Preferences(Settings)</kbd> > <kbd>Plugins</kbd> > <kbd>Browse repositories...</kbd> > <kbd>find"Fast Request"</kbd> > <kbd>Install Plugin</kbd>

**Manual:**
- download[`lastest plugin zip`][latest-release] -> <kbd>Preferences(Settings)</kbd> > <kbd>Plugins</kbd> > <kbd>Install plugin from disk...</kbd>


## 2.Default shortcut key
recommend way:just click FastRequest icon
![](./screenshot/methodIcon.png)


|key|scope|description|
| --- | --- | --- |
| <kbd> ctrl \ </kbd> | method(just put the cursor on method) | Generate the url and request parameters of the current method |

if it doesn't work,you can search <kbd>Generate URL and Param</kbd> in key map and change the key

modify key:<kbd>Preferences(Settings)</kbd> > <kbd>Keymap</kbd>

other way:<kbd>Code(Toolbar)</kbd> > <kbd>Generate</kbd> > <kbd>Generate URL and Param</kbd>




## 3.config and usage
### 3.1 common config
|config name|description|
| --- | --- |
|ProjectName|project name,Example:**user**γ**order**,Please make sure there must be a project before adding env|
|Env|environment Name,Example:**local**γ**develop**γ**test**γ**produce**|
|Domain|add the domain url in table|

![](./screenshot/commonConfig.png)

### 3.2 Data Mapping
|config name|description|
| --- | --- |
|Random String Length|Random string length,default 5|
|Custom Data Mapping|If you want a class to parse only the fields you want,then you can add custom mapping configuration|
|Default Data Mapping|Default type of relational mapping,That is, the type is converted to the corresponding value|

![](./screenshot/dataMapping.png)

#### 3.2.1 Custom Data Mapping
**Java Type**is the corresponding object type,must contain package name and class name

Example:`com.baomidou.mybatisplus.extension.plugins.pagination.Page`

**Default value**must be in json format,

Example:
```
{"size":10,"current":1}
```

## 4.Tips
1. The parameter Table supports value modification,  the URL will change when Path Param Table params has been modified.
2. Url ParamγJSONγForm URL-Encoded Table support parameter modification,after modify,text tab parameters will also change accordingly,**right click in textarea to copy parameters**
## 5.Type Icon Mapping
icon and type mapping

|Icon|Type|
| --- | --- | 
|![](./screenshot/icon/array.svg)  |Array  |
|![](./screenshot/icon/object.svg) |Object |
|![](./screenshot/icon/number.svg) |Number |
|![](./screenshot/icon/string.svg) |String |
|![](./screenshot/icon/boolean.svg)|Boolean|

## 6.Updates
- v1.1.2(2021.07.01)
  * add line icon for method
  * limit send button click frequency
  * optimize response show large text

- v1.1.1(2021.06.28)
  * params can delete batch
  * fix some bugs
- v1.1.0(2021.06.25)
  * support http request
  * support modify and add params
  * beautify ui
  * fix some bugs
- v1.0.0(2021.06.09)
  * support generate url and param
  * support custom domain
  * support custom params

## 7.Donate
If you think the plug-in is great and saves you a lot of time, then invite the author to have a cup of coffee~βββ,thank you very much!

| ![εΎ?δΏ‘](./screenshot/pay/wechat.jpg) | ![ζ―δ»ε?](./screenshot/pay/alipay.png) |
| --- | --- |

## 8.Plan
I very hope you can help build follow-up functions.Provide good idea and provide good PR!

* support send request(complete in v1.1.0)


[latest-release]: https://github.com/kings1990/fast-request/releases/latest
[plugin]: https://plugins.jetbrains.com/plugin/16988
[plugin-img]: https://img.shields.io/badge/plugin-FastRequest-x.svg