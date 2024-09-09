package com.example.appupdate

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.util.Log
import org.json.JSONObject
import java.io.IOException


import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl
import com.example.coreservice.CoreService
import com.example.coreservice.NetworkService
import com.example.coreservice.UpdateNetwork

class AppUpdateService {

    companion object {

        private var appId: String = ""
        private var showNativeUI: Boolean = false
        private const val TAG = "AppUpdateService"
        private var isResponseReceived : Boolean = false


        private fun getResponse(response: Response, context: Context, callBack: UpdateCallBack, isFromCDN: Boolean) {
            try {
                if (response.code() == 200) {
                    val myResponse = response.body()?.string() ?: ""
                    val jsonObject = JSONObject(myResponse)
                    val updateData = jsonObject.getJSONObject("updateData")
                    val isAndroidUpdate = updateData.getBoolean("isAndroidUpdate")
                    val isMaintenance = jsonObject.getBoolean("isMaintenance")

                    if (isAndroidUpdate) {
                        val isAndroidForcedUpdate = updateData.getBoolean("isAndroidForcedUpdate")
                        val androidBuildNumber = updateData.getString("androidBuildNumber")
                        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                        @Suppress("DEPRECATION") val versionCode = info.versionCode
                        val buildNum = androidBuildNumber.toIntOrNull() ?: 0

                        if (showNativeUI && versionCode < buildNum && (isAndroidForcedUpdate || isAndroidUpdate)) {
                            val intent = Intent(context, AppUpdateActivity::class.java).apply {
                                putExtra("res", myResponse)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        }
                    } else if (isMaintenance && showNativeUI) {
                        val intent = Intent(context, MaintenanceActivity::class.java).apply {
                            putExtra("res", myResponse)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                    callBack.onSuccess(myResponse)
                } else if (isFromCDN) {
                    callServiceApi(context, callBack)
                }
            } catch (e: Exception) {
                callBack.onFailure(e.message ?: "Unknown error")
                Log.d(TAG, "getResponse: ${e.message}")
            }
        }

        private fun callCDNServiceApi(context: Context, callBack: UpdateCallBack) {
            val baseUrl = BuildConfig.CDN_BASE_URL
            val pathSegment = "${appId ?: ""}.json"

            val urlBuilder = HttpUrl.parse(baseUrl)?.newBuilder() ?: return
            val unixTime = System.currentTimeMillis() / 1000L
            urlBuilder.addPathSegment(pathSegment)
            urlBuilder.addQueryParameter("now", unixTime.toString())
            val url = urlBuilder.build().toString()
            Log.d(TAG, "URL: AppsOnAirCDNApi$url")

            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: AppsOnAirCDNApi${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "CDN Service response: $response")
                    getResponse(response, context, callBack, true)
                }
            })
        }

        private fun callServiceApi(context: Context, callBack: UpdateCallBack) {
            val url = "${BuildConfig.BASE_URL}${appId ?: ""}"
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "onFailure: AppsOnAirServiceApi${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    getResponse(response, context, callBack, false)
                }
            })
        }

        fun checkForAppUpdate(context: Context, callBack: UpdateCallBack, options: Map<String, Any> = emptyMap() ) {
            val appId: String = CoreService.getAppId(context)
            AppUpdateService.appId = appId
             if(options.isNotEmpty()) {
                if(options.containsKey(key = "showNativeUI")){
                    if(options["showNativeUI"] is Boolean){
                        showNativeUI = options["showNativeUI"] as Boolean
                    }else{
                        showNativeUI =  true
                    }
                }
            } else{
                 showNativeUI =  true
            }

            val updateNetworkState = UpdateNetwork { isConnected ->
                if (isConnected) {
                    // Network is available
                    println("Network is available")
                    if(!isResponseReceived){
                        callCDNServiceApi(context, callBack)
                        isResponseReceived = true
                    }

                } else {
                    // Network is lost
                    println("Network is lost")
                }
            }
            NetworkService.checkConnectivity(context, updateNetworkState)
        }
    }
}
