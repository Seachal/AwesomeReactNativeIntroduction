package com.awesomereactnativeintroduction

import android.util.Log
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * CalendarModule - 日历原生模块
 * 
 * 提供从JavaScript调用Android日历功能的能力
 */
class CalendarModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    
    companion object {
        const val NAME = "CalendarModule"
        const val DEFAULT_EVENT_NAME = "默认活动"
    }
    
    // 返回模块名称，JavaScript端将通过此名称访问
    override fun getName(): String {
        return NAME
    }
    
    // 导出常量，JavaScript可以直接访问
    override fun getConstants(): Map<String, Any> {
        val constants = HashMap<String, Any>()
        constants["DEFAULT_EVENT_NAME"] = DEFAULT_EVENT_NAME
        return constants
    }
    
    // 导出方法，可以从JavaScript调用
    // 创建日历事件的同步方法
    @ReactMethod
    fun createCalendarEvent(name: String, location: String) {
        Log.d("CalendarModule", "创建日历事件：$name 在 $location")
    }
    
    // 使用回调的方法
    @ReactMethod
    fun createCalendarEventWithCallback(name: String, location: String, callback: Callback) {
        try {
            // 模拟创建事件
            Log.d("CalendarModule", "创建日历事件（回调）：$name 在 $location")
            
            // 返回事件ID给JavaScript
            val eventId = "EVENT_" + (10000..99999).random()
            callback.invoke(null, eventId)
        } catch (e: Exception) {
            callback.invoke(e.message, null)
        }
    }
    
    // 使用Promise的方法
    @ReactMethod
    fun createCalendarEventWithPromise(name: String, location: String, promise: Promise) {
        try {
            // 模拟创建事件
            Log.d("CalendarModule", "创建日历事件（Promise）：$name 在 $location")
            
            // 返回事件ID给JavaScript
            val eventId = "EVENT_" + (10000..99999).random()
            promise.resolve(eventId)
        } catch (e: Exception) {
            promise.reject("E_EVENT_CREATION_FAILED", e.message, e)
        }
    }
    
    // 向JavaScript发送事件的方法
    private fun sendEvent(eventName: String, params: WritableMap?) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }
    
    // 添加一个方法来测试事件发送
    @ReactMethod
    fun triggerCalendarEvent() {
        val params = Arguments.createMap().apply {
            putString("eventProperty", "某个值")
        }
        sendEvent("EventReminder", params)
    }
} 