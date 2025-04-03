package com.awesomereactnativeintroduction.network

import com.facebook.react.bridge.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class NetworkModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

    override fun getName(): String {
        return "NetworkModule"
    }

    @ReactMethod
    fun getRequest(url: String, promise: Promise) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                promise.reject("REQUEST_ERROR", e.message, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        promise.reject("REQUEST_ERROR", "Unexpected response: ${response.code}")
                        return
                    }

                    try {
                        val responseBody = response.body?.string() ?: ""
                        val result = Arguments.createMap()
                        result.putString("data", responseBody)
                        result.putInt("statusCode", response.code)
                        promise.resolve(result)
                    } catch (e: Exception) {
                        promise.reject("REQUEST_ERROR", e.message, e)
                    }
                }
            }
        })
    }

    @ReactMethod
    fun getRequestWithParams(url: String, params: ReadableMap, promise: Promise) {
        val urlBuilder = HttpUrl.parse(url)?.newBuilder()
            ?: run {
                promise.reject("REQUEST_ERROR", "Invalid URL: $url")
                return
            }

        // 添加参数到URL
        val keys = params.keySetIterator()
        while (keys.hasNextKey()) {
            val key = keys.nextKey()
            val value = params.getString(key) ?: continue
            urlBuilder.addQueryParameter(key, value)
        }

        val request = Request.Builder()
            .url(urlBuilder.build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                promise.reject("REQUEST_ERROR", e.message, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        promise.reject("REQUEST_ERROR", "Unexpected response: ${response.code}")
                        return
                    }

                    try {
                        val responseBody = response.body?.string() ?: ""
                        val result = Arguments.createMap()
                        result.putString("data", responseBody)
                        result.putInt("statusCode", response.code)
                        promise.resolve(result)
                    } catch (e: Exception) {
                        promise.reject("REQUEST_ERROR", e.message, e)
                    }
                }
            }
        })
    }

    @ReactMethod
    fun postRequest(url: String, promise: Promise) {
        val requestBody = "".toRequestBody(JSON)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                promise.reject("REQUEST_ERROR", e.message, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        promise.reject("REQUEST_ERROR", "Unexpected response: ${response.code}")
                        return
                    }

                    try {
                        val responseBody = response.body?.string() ?: ""
                        val result = Arguments.createMap()
                        result.putString("data", responseBody)
                        result.putInt("statusCode", response.code)
                        promise.resolve(result)
                    } catch (e: Exception) {
                        promise.reject("REQUEST_ERROR", e.message, e)
                    }
                }
            }
        })
    }

    @ReactMethod
    fun postRequestWithParams(url: String, params: ReadableMap, promise: Promise) {
        try {
            val jsonObject = JSONObject()
            val keys = params.keySetIterator()
            while (keys.hasNextKey()) {
                val key = keys.nextKey()
                when {
                    params.getType(key) == ReadableType.String -> jsonObject.put(key, params.getString(key))
                    params.getType(key) == ReadableType.Number -> jsonObject.put(key, params.getDouble(key))
                    params.getType(key) == ReadableType.Boolean -> jsonObject.put(key, params.getBoolean(key))
                    params.getType(key) == ReadableType.Null -> jsonObject.put(key, JSONObject.NULL)
                    // 嵌套对象需要更复杂的处理，这里简化处理
                }
            }

            val requestBody = jsonObject.toString().toRequestBody(JSON)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    promise.reject("REQUEST_ERROR", e.message, e)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            promise.reject("REQUEST_ERROR", "Unexpected response: ${response.code}")
                            return
                        }

                        try {
                            val responseBody = response.body?.string() ?: ""
                            val result = Arguments.createMap()
                            result.putString("data", responseBody)
                            result.putInt("statusCode", response.code)
                            promise.resolve(result)
                        } catch (e: Exception) {
                            promise.reject("REQUEST_ERROR", e.message, e)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            promise.reject("REQUEST_ERROR", e.message, e)
        }
    }
} 