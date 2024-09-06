package com.example.appupdate

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appupdate.R
import org.json.JSONException
import org.json.JSONObject

class AppUpdateActivity : AppCompatActivity() {

    private var activityClose = false

//    @SuppressLint("NewApi")
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_update)

        try {
            val ai: ApplicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle1: Bundle? = ai.metaData
            var icon = 0
            var name = ""

            if (bundle1 != null) {
                icon = bundle1.getInt("com.appsonair.icon")
                name = bundle1.getString("com.appsonair.name") ?: "Your"
            } else {
                if (name.isEmpty()) {
                    name = "Your"
                    icon = resources.getIdentifier("maintenance_icon", "drawable", packageName)
                }
            }

            val bundle = intent.extras
            val data = bundle?.getString("res") ?: return
            val jsonObject = JSONObject(data)

            if (!jsonObject.isNull("updateData")) {
                val updateData = jsonObject.getJSONObject("updateData")
                val isAndroidForcedUpdate = updateData.getBoolean("isAndroidForcedUpdate")
                val isAndroidUpdate = updateData.getBoolean("isAndroidUpdate")
                val androidBuildNumber = updateData.getString("androidBuildNumber")
                val playStoreURL = updateData.getString("androidUpdateLink")
                val info: PackageInfo = packageManager.getPackageInfo(packageName, 0)
                @Suppress("DEPRECATION") val versionCode = info.versionCode
                val buildNum = androidBuildNumber.toIntOrNull() ?: 0

                val isUpdate = versionCode < buildNum

                val imgIcon: ImageView = findViewById(R.id.img_icon)
                val txtTitle: TextView = findViewById(R.id.txt_title)
                val txtDes: TextView = findViewById(R.id.txt_des)
                val txtNoThanks: TextView = findViewById(R.id.txt_no_thanks)
                val btnUpdate: TextView = findViewById(R.id.btn_update)

                if ((isAndroidForcedUpdate || isAndroidUpdate) && isUpdate) {
                    if (icon != 0) {
                        imgIcon.setImageResource(icon)
                    }
                    txtTitle.text = "$name ${getString(R.string.update_title)}"
                    Log.d("App Update Activity", "onCreate: " + txtTitle.text)
                    if (isAndroidForcedUpdate) {
                        txtNoThanks.visibility = View.GONE
                        txtDes.text = getString(R.string.update_force_dsc)
                    } else {
                        txtNoThanks.visibility = View.VISIBLE
                        txtDes.text = getString(R.string.update_dsc)
                        txtNoThanks.setOnClickListener {
                            activityClose = true
                            @Suppress("DEPRECATION")
                            onBackPressed()
                        }
                    }
                    btnUpdate.setOnClickListener {
                        try {
                            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreURL))
                            startActivity(marketIntent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (activityClose) {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
}
