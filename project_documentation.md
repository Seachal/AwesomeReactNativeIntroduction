# React Native 与 Android 原生模块集成文档

## 项目概述

本项目实现了 React Native 应用与 Android 原生代码的交互功能，遵循 React Native 官方文档中的 Native Modules 实现方式。主要功能是在 JavaScript 代码中调用 Android 原生的日历功能，包括创建日历事件和响应原生事件。

## 文件结构

### Android 原生代码

1. **CalendarModule.kt**
   - 位置：`android/app/src/main/java/com/awesomereactnativeintroduction/CalendarModule.kt`
   - 功能：实现原生的日历事件功能，包括：
     - 基本方法：`createCalendarEvent()`
     - 回调方法：`createCalendarEventWithCallback()`
     - Promise 方法：`createCalendarEventWithPromise()`
     - 事件发送：`triggerCalendarEvent()`
   - 通过 `ReactMethod` 注解将方法暴露给 JavaScript

2. **MyAppPackage.kt**
   - 位置：`android/app/src/main/java/com/awesomereactnativeintroduction/MyAppPackage.kt`
   - 功能：创建一个 ReactPackage 以向 React Native 注册我们的原生模块
   - 主要方法：
     - `createNativeModules()`：返回原生模块列表
     - `createViewManagers()`：返回视图管理器列表（本例中为空）

3. **MainApplication.kt** (修改)
   - 位置：`android/app/src/main/java/com/awesomereactnativeintroduction/MainApplication.kt`
   - 修改内容：在 `getPackages()` 方法中添加 `MyAppPackage()`

### JavaScript 代码

1. **CalendarModule.js**
   - 位置：`NativeModules/CalendarModule.js`
   - 功能：为原生模块提供 JavaScript 接口
   - 主要功能：
     - 导出常量
     - 提供同步、回调和 Promise 方法
     - 提供事件监听和移除功能

2. **App.tsx** (修改)
   - 位置：项目根目录
   - 修改内容：添加使用 CalendarModule 的示例代码
   - 主要功能：
     - 创建日历事件（同步、回调、Promise）
     - 监听原生事件
     - 显示各种方法的返回结果

## 实现细节

### 1. 创建原生模块

原生模块类必须：
- 继承 `ReactContextBaseJavaModule`
- 实现 `getName()` 方法，返回在 JavaScript 中访问的模块名称
- 使用 `@ReactMethod` 注解标记要暴露给 JavaScript 的方法

示例：
```kotlin
@ReactMethod
fun createCalendarEvent(name: String, location: String) {
    Log.d("CalendarModule", "创建日历事件：$name 在 $location")
}
```

### 2. 注册原生模块

需要创建一个 ReactPackage 类，并在 `createNativeModules()` 方法中返回原生模块实例：

```kotlin
override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(CalendarModule(reactContext))
}
```

### 3. 在应用中注册 Package

在 MainApplication 的 `getPackages()` 方法中添加 Package 实例：

```kotlin
override fun getPackages(): List<ReactPackage> =
    PackageList(this).packages.apply {
        // Packages that cannot be autolinked yet can be added manually here
        add(MyAppPackage())
    }
```

### 4. 创建 JavaScript 接口

在 JavaScript 中通过 NativeModules 获取原生模块：

```javascript
import { NativeModules } from 'react-native';
const { CalendarModule } = NativeModules;
```

### 5. 通信方式

本项目演示了三种原生通信方式：

1. **同步方法**：直接调用，没有返回值
   ```javascript
   CalendarModule.createCalendarEvent('测试活动', '我的位置');
   ```

2. **回调函数**：通过回调获取结果
   ```javascript
   CalendarModule.createCalendarEventWithCallback(
     '带回调的活动',
     '回调位置',
     (error, eventId) => {
       if (error) {
         console.error(error);
       } else {
         console.log('成功创建事件，ID:', eventId);
       }
     }
   );
   ```

3. **Promise**：使用 Promise 处理结果
   ```javascript
   try {
     const eventId = await CalendarModule.createCalendarEventWithPromise(
       '带Promise的活动',
       'Promise位置'
     );
     console.log('成功创建事件，ID:', eventId);
   } catch (e) {
     console.error(e);
   }
   ```

4. **事件发送**：从原生代码发送事件到 JavaScript
   ```javascript
   // 在原生代码中
   private fun sendEvent(eventName: String, params: WritableMap?) {
     reactApplicationContext
       .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
       .emit(eventName, params)
   }
   
   // 在JavaScript中
   const eventListener = CalendarModule.addListener(
     'EventReminder',
     (event) => {
       console.log('收到事件:', event);
     }
   );
   ```

## 使用指南

1. 在 Android 设备或模拟器上运行应用
2. 应用主屏幕上会显示以下功能按钮：
   - **创建日历事件（同步）**：调用同步方法
   - **创建日历事件（回调）**：使用回调函数获取结果
   - **创建日历事件（Promise）**：使用 Promise 获取结果
   - **触发原生事件**：触发从原生代码到 JavaScript 的事件

## 注意事项

1. 此功能仅适用于 Android 平台
2. 原生模块中导出的方法都应用 `@ReactMethod` 标记
3. 原生模块的方法必须返回 `void`
4. 回调函数必须在主线程调用
5. 如果需要在 iOS 平台实现相同功能，需要单独创建 iOS 原生模块 