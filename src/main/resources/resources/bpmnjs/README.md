## 5.9 BPMN-JS 使用

- [BPMN 官方地址](https://bpmn.io/toolkit/bpmn-js/)
- [BPMN Gihub 地址](https://github.com/bpmn-io/bpmn-js-examples)

### 5.9.1 Activiti7与BPMN-JS整合

####  5.9.1.1 安装Node.js
* 下载地址https://nodejs.org/en/download/

####  5.9.1.2 BPMN-JS地址
* 下载源码https://github.com/bpmn-io/bpmn-js-examples/

####  5.9.1.3 在resources文件夹下再创建一个resources文件夹，
 ```text
实际路径为resources/resources/
 ```

![在这里插入图片描述](https://img-blog.csdnimg.cn/4a9933abf1f843cdbecd4df8075d3791.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)
####  5.9.1.4 从github克隆bpmn-js-examples
![在这里插入图片描述](https://img-blog.csdnimg.cn/9211749185cf4f9380cef6e97553e254.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)‪bpmn-js-examples 项目下 properties-panel 复制到你当前的Springboot工程里，把名称 properties-panel  修改为 bpmnjs

![在这里插入图片描述](https://img-blog.csdnimg.cn/445ec61b596446a5ac6995b914383e9d.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)
#### 5.9.1.5 解压 bpmnjs_init.zip 汉化包资料拖到bpmnjs

![在这里插入图片描述](https://img-blog.csdnimg.cn/ab16d9e97aad44698557100ee0929b8d.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)
#### 5.9.1.6 屏蔽部分引入
src/main/resources/resources/bpmnjs/app/index.js

```text
// import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/camunda';
// import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda.json';

// var bpmnModeler = new BpmnModeler({
//   container: canvas,
//   propertiesPanel: {
//     parent: '#js-properties-panel'
//   },
//   additionalModules: [
//     propertiesPanelModule,
//     propertiesProviderModule
//   ],
//   moddleExtensions: {
//     camunda: camundaModdleDescriptor
//   }
// });
```

#### 5.9.1.7 加入汉化包配置引用

```text
import propertiesProviderModule from '../resources/properties-panel/provider/activiti';
import activitiModdleDescriptor from '../resources/activiti.json';
import customTranslate from '../resources/customTranslate/customTranslate';
import customControlsModule from '../resources/customControls';

// 添加翻译组件
var customTranslateModule = {
  translate: ['value', customTranslate]
};


var bpmnModeler = new BpmnModeler({
  container: canvas,
  propertiesPanel: {
    parent: '#js-properties-panel'
  },
  additionalModules: [
    propertiesPanelModule,
    propertiesProviderModule,
    customControlsModule,
    customTranslateModule
  ],
  moddleExtensions: {
    activiti:activitiModdleDescriptor
  }
});
```

增加BPMNJS可执行选框默认勾选，打开resources/newDiagram.bpmn
```text
<bpmn2:process id="Process_1" isExecutable="true">
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/a085ba262280457e8e1a42cd0534a039.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)


#### 5.9.1.8 BPMN-JS 前端项目启动

cmd 或者 idea 工具都可以

切换地址：{PATH}\src\main\resources\res
ources\bpmnjs

安装需要依赖
```java
npm install
```


运行 bpmn-js 项目
```java
npm run dev
```

就会弹出：http://localhost:9013/

![在这里插入图片描述](https://img-blog.csdnimg.cn/8c1e586b4de941b384a4469f308f9359.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAZWRkaWVfazI=,size_20,color_FFFFFF,t_70,g_se,x_16)



###  5.9.2 整合时候出现问题

1. properties-panel 没有 app 文件夹
   答：版本问题，在 8.1.0 之后就没有了 app 文件夹

2. 打开 WEB 页面之后提示：“Import Error Details bo.get is not a function”
   答：版本过高，推荐版本是在 *Master v7.2.1 - v7.3.1 （汉化包兼容到这）*