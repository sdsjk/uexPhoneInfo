# uexCallKit

# 1、简介
 APP在启动过程中插件检测手机来电时，如果APP通讯录中有相关匹配的电话，显示此电话来电人的姓名与部门

## 1.2、UI展示
无
## 1.3、 开源源码
无
# 2、API概览
## 2.1、 方法

> ### initPhoneData 初始化插件接口
`uexCallKit.initPhoneData(param);`
**说明:**
初始化uexCloudAdapte插件
**参数:**
| 参数名称 | 参数类型 | 是否必须 | 说明 |
| ------ | ----- | ----- | ------|
| param | String | 是| 该字符串为JSON 格式，参见下方param 列表|

**param列表**

|参数|是否必选|类型|说明|
| ----- | ------- | ------ |
|param|是|String|全部公司人员信息|

**平台支持:**
Android

**版本支持:**
4.0.0+

**示例:**

```
	function initSDK(){
	        var param = {
	         "links": [
	        {
	            "num": "18511460633",
	            "name": "张三",
	            "positioninfo": "航天局局长",
	            "messageinfo": "公司本部—信息中心-系统运营",
	            "companyinfo": "北京重工业集团有限公司"
	        },
	        {
	            "num": "185114606332",
	            "name": "李四",
	            "positioninfo": "航天局局长",
	            "messageinfo": "公司本部—信息中心-系统运营",
	            "companyinfo": "北京重工业集团有限公司"
	        },
	        {
	            "num": "185114606331",
	            "name": "王五",
	            "positioninfo": "航天局局长",
	            "messageinfo": "公司本部—信息中心-系统运营",
	            "companyinfo": "北京重工业集团有限公司"
	        }
	    ]
	}
	 uexCallKit.initPhoneData(JSON.stringify(param));
	}

```

# 3、更新历史

API 版本： uexCallKit-4.0.1（android）
 最近更新时间：2017-10-18
 
|  历史发布版本 | 安卓更新  |
| ------------ | ------------ | ------------ |
| 1.0.0 | uexCallKit 新增插件 | |