package com.example.forceupdate

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.appupdate.AppUpdateService
import com.example.appupdate.UpdateCallBack
import com.example.forceupdate.ui.theme.ForceUpdateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForceUpdateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val updateCallback = object : UpdateCallBack {
            override fun onSuccess(response: String?) {
                Log.e("mye", response!!)
            }

            override fun onFailure(message: String?) {
                Log.e("mye", "onFailure: $message")
            }
        }
        //Get your appId from https://appsonair.com/
        AppUpdateService.checkForAppUpdate(true,this,  updateCallback)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ForceUpdateTheme {
        Greeting("Android")
    }
}