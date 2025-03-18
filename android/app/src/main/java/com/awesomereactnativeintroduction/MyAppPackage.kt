package com.awesomereactnativeintroduction

import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager

/**
 * MyAppPackage - 自定义React Native包
 * 
 * 用于向React Native注册Native Modules
 */
class MyAppPackage : ReactPackage {
    // 创建并返回原生模块列表
    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return listOf(CalendarModule(reactContext))
    }

    // 创建并返回ViewManager列表，用于自定义UI组件
    // 在这个示例中，我们不需要任何ViewManager，所以返回空列表
    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<View, ReactShadowNode<*>>> {
        return emptyList()
    }
} 