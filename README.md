# uexCallKit

# 1、简介
 APP在启动过程中插件检测手机来电时，如果APP通讯录中有相关匹配的电话，显示此电话来电人的姓名与部门

## 1.2、UI展示
无
## 1.3、 开源源码
无
# 2、API概览
## 2.1、 方法

> ### openCallKit 初始化插件接口
`uexCallKit.initPhoneData(JSON.stringify(param));`
**说明:**
初始化uexCallKit插件
**参数:**
| 参数名称 | 参数类型 | 是否必须 | 说明 |
| ------ | ----- | ----- | ------|
| param | String | 是| 该字符串为JSON 格式，参见下方param 列表|

**param列表**


|参数|是否必选|类型|说明|
| ----- | ------- | ------ | ------ |
|num|是|String|手机号|
|name|是|String|人员姓名|
|positioninfo|是|String|全部公司人员信息|(IOS此参数无效)
|messageinfo|是|String|全部公司人员信息|
|companyinfo|是|String|全部公司人员信息|(IOS此参数无效)
|officePhone|是|String|办公室号码|(IOS此参数无效)
|field3|是|String|短号|(IOS此参数无效)

**平台支持:**
Android

**版本支持:**
4.0.0+

**示例:**

```
	function test(){
	        var param = {
	         "links": [
	        {
                "num": "15321375192",
                "name": "张三",
                "positioninfo": "航天局局长",
                "messageinfo": "公司本部—信息中心-系统运营",
                "companyinfo": "北京重工业集团有限公司",
				"officePhone":"123456",
				"field3":"1234"
	        },
	        {
                "num": "185114606332",
                "name": "李四",
                "positioninfo": "航天局局长",
                "messageinfo": "公司本部—信息中心-系统运营",
                "companyinfo": "北京重工业集团有限公司",
				"officePhone":"123456",
				"field3":"1234"
	        },
	        {
                "num": "185114606331",
                "name": "王五",
                "positioninfo": "航天局局长",
                "messageinfo": "公司本部—信息中心-系统运营",
                "companyinfo": "北京重工业集团有限公司",
				"officePhone":"123456",
				"field3":"1234"
	        }
	    ]
	}

```




> ### checkDataCache 检查是否有缓存数据和悬浮框权限
`uexCallKit.checkDataCache(FunctionId);`
**说明:**
检查是否有缓存数据和悬浮框权限
**参数:**
| 参数名称 | 参数类型 | 是否必须 | 说明 |
| ------ | ----- | ----- | ------|
| FunctionId | function  | 是| 传入的js回调方法|

**function方法参数**


|参数|是否必选|类型|说明|
| ----- | ------- | ------ | ------ |
|isData|是|boolean|是否有缓存数据|
|isPermission|是|boolean|是否开启悬浮框权限|
**平台支持:**
Android

**版本支持:**
4.0.0+

**示例:**

```
	function Jump() {
				uexCallKit.checkDataCache(function(isData, isPermission) {
				//判断是否有悬浮框权限，如果有就开启悬浮框权限
					if(!isPermission) {
						uexCallKit.openPermissionDialog();

					}
				});
			}

```

> ### openPermissionDialog 打开系统的悬浮框权限
`uexCallKit.openPermissionDialog();`
**说明:**
打开系统悬浮框设置界面去开启悬浮框权限
**参数:**
无
**平台支持:**
Android

**版本支持:**
4.0.0+

**示例:**

```
	 uexCallKit.openPermissionDialog();

```


# 3、更新历史

API 版本：
uexCallKit-4.0.1（iOS）
 最近更新时间：2017-10-18



|  历史发布版本 | iOS更新  |
| ------------ | ------------ | ------------ |
| 1.0.0 | uexCallKit 新增插件 | |

uexCallKit-4.0.9（Android）
 最近更新时间：2018-12-3

|  历史发布版本 | Android更新  |
| ------------ | ------------ |
| 4.0.0 | uexCallKit 新增插件 |
| 4.0.9 | uexCallKit 新增去电显示监听和增加办公室和短号监听匹配规则，以及优化相关的代码 |
