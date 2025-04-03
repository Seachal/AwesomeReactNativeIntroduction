# React Native与原生Android网络请求集成

本文档描述了如何在React Native项目中使用原生Android网络请求模块。

## 架构概述

该方案使用原生Android代码处理网络请求，React Native负责UI展示和数据处理。架构如下：

1. 原生Android网络模块（使用OkHttp实现）
2. React Native桥接模块
3. React Native UI组件

## 目录结构

```
.
├── android/app/src/main/java/com/awesomereactnativeintroduction/
│   ├── network/
│   │   ├── NetworkModule.kt  // 原生网络请求实现
│   │   └── NetworkPackage.kt  // 原生模块注册
│   ├── MainActivity.kt
│   └── MainApplication.kt  // 注册网络模块包
├── src/
│   ├── services/
│   │   └── NetworkService.js  // JS层网络请求服务
│   └── screens/
│       └── NetworkDemo.js  // 演示界面
├── App.tsx  // 主应用入口
└── ...
```

## 功能支持

该网络模块支持以下功能：

1. GET请求（无参数）
2. GET请求（带参数）
3. POST请求（无参数）
4. POST请求（带参数）

所有请求均返回包含`data`（响应体）和`statusCode`（状态码）的对象。

## 如何使用

### 1. 导入网络服务

```javascript
import NetworkService from '../services/NetworkService';
```

### 2. GET请求（无参数）

```javascript
try {
  const response = await NetworkService.get('https://api.example.com/data');
  console.log(response.data); // 响应数据
  console.log(response.statusCode); // HTTP状态码
} catch (error) {
  console.error(error);
}
```

### 3. GET请求（带参数）

```javascript
try {
  const params = {
    userId: 1,
    type: 'active'
  };
  const response = await NetworkService.getWithParams('https://api.example.com/users', params);
  console.log(response.data);
  console.log(response.statusCode);
} catch (error) {
  console.error(error);
}
```

### 4. POST请求（无参数）

```javascript
try {
  const response = await NetworkService.post('https://api.example.com/data');
  console.log(response.data);
  console.log(response.statusCode);
} catch (error) {
  console.error(error);
}
```

### 5. POST请求（带参数）

```javascript
try {
  const params = {
    name: 'John Doe',
    email: 'john@example.com'
  };
  const response = await NetworkService.postWithParams('https://api.example.com/users', params);
  console.log(response.data);
  console.log(response.statusCode);
} catch (error) {
  console.error(error);
}
```

## 错误处理

所有网络请求方法都会在请求失败时抛出异常。您应该使用try-catch块捕获这些异常：

```javascript
try {
  const response = await NetworkService.get('https://api.example.com/data');
  // 处理响应
} catch (error) {
  // 处理错误
  console.error('网络请求失败:', error.message);
}
```

## 自定义扩展

如果需要添加更多功能，您可以修改以下文件：

1. `NetworkModule.kt` - 添加新的原生网络请求方法
2. `NetworkService.js` - 添加对应的JS方法
3. 更新`NetworkDemo.js`以演示新功能

## 测试

使用`NetworkDemo`组件可以测试网络请求功能。该组件允许您：

1. 输入请求URL
2. 输入JSON格式的请求参数
3. 选择请求类型（GET/POST，带参数/无参数）
4. 查看响应结果

## 注意事项

1. 确保在AndroidManifest.xml中添加了网络权限：
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

2. 所有网络请求都在后台线程中执行，不会阻塞UI线程

3. 所有网络请求都返回Promise，应使用async/await或.then().catch()处理 