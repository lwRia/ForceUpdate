package com.example.appupdate

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MaintenanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maintenance)

        try {
            val bundle = intent.extras
            val data = bundle?.getString("res") ?: return // NullPointerException handling

            val jsonObject = JSONObject(data)
            val isMaintenance = jsonObject.getBoolean("isMaintenance")

            if (isMaintenance) {
                val maintenanceLayout = findViewById<LinearLayout>(R.id.ll_root)
                val maintenanceLayout1 = findViewById<LinearLayout>(R.id.lls_root)
                maintenanceLayout.visibility = View.GONE
                maintenanceLayout1.visibility = View.GONE

                val maintenanceData = jsonObject.getJSONObject("maintenanceData")
                if (maintenanceData.toString() != "{}") {
                    val title = maintenanceData.getString("title")
                    val description = maintenanceData.getString("description")
                    val image = maintenanceData.getString("image")
                    val textColorCode = maintenanceData.getString("textColorCode")
                    val backgroundColorCode = maintenanceData.getString("backgroundColorCode")

                    if (title.isNotEmpty() && description.isNotEmpty()) {
                        maintenanceLayout.visibility = View.VISIBLE

                        if (backgroundColorCode.isNotEmpty()) {
                            maintenanceLayout.setBackgroundColor(Color.parseColor(backgroundColorCode))
                        }

                        val imgIcon = findViewById<ImageView>(R.id.img_icon)
                        val txtTitleMaintain = findViewById<TextView>(R.id.txt_title_maintain)
                        val txtDesMaintain = findViewById<TextView>(R.id.txt_des_maintain)
                        val txtAppName = findViewById<TextView>(R.id.txt_app_name)

                        if (image.isNotEmpty() && image != "null") {
                            DownloadImageTask(imgIcon).execute(image)
                        } else {
                            imgIcon.setImageResource(R.drawable.maintenance_icon)
                        }

                        txtTitleMaintain.text = title
                        txtDesMaintain.text = description

                        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                        val bundle1 = ai.metaData
                        val appName = bundle1.getString("com.appsonair.name")
                        txtAppName.text = appName

                        if (textColorCode.isNotEmpty()) {
                            val textColor = Color.parseColor(textColorCode)
                            txtTitleMaintain.setTextColor(textColor)
                            txtDesMaintain.setTextColor(textColor)
                            txtAppName.setTextColor(textColor)
                        }
                    } else {
                        maintenanceLayout1.visibility = View.VISIBLE
                        val imgIcon2 = findViewById<ImageView>(R.id.img2_icon)
                        val txtTitle2Maintain = findViewById<TextView>(R.id.txt_title2_maintain)

                        val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                        val bundle1 = ai.metaData
                        val appName = bundle1.getString("com.appsonair.name")
                        txtTitle2Maintain.text = "$appName ${getString(R.string.maintenance)}"

                        if (maintenanceData.toString() != "{}") {
                            val image2 = maintenanceData.getString("image")
                            if (image2.isNotEmpty() && image2 != "null") {
                                DownloadImageTask(imgIcon2).execute(image2)
                            } else {
                                imgIcon2.setImageResource(R.drawable.maintenance_icon)
                            }
                        }
                    }
                } else {
                    maintenanceLayout1.visibility = View.VISIBLE
                    val imgIcon2 = findViewById<ImageView>(R.id.img2_icon)
                    val txtTitle2Maintain = findViewById<TextView>(R.id.txt_title2_maintain)

                    val ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    val bundle1 = ai.metaData
                    val appName = bundle1.getString("com.appsonair.name")
                    txtTitle2Maintain.text = "$appName ${getString(R.string.maintenance)}"

                    if (maintenanceData.toString() != "{}") {
                        val image2 = maintenanceData.getString("image")
                        if (image2.isNotEmpty() && image2 != "null") {
                            DownloadImageTask(imgIcon2).execute(image2)
                        } else {
                            imgIcon2.setImageResource(R.drawable.maintenance_icon)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        // Do nothing on back press
    }
}
