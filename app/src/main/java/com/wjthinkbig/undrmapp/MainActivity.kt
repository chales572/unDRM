package com.wjthinkbig.undrmapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.wjthinkbig.bookclubdrm.DrmDecode
import com.wjthinkbig.bookclubdrm.common.LOCAL_PATH
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "UnDRM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val targetPath = intent.getStringExtra("target_path")
        if (!TextUtils.isEmpty(targetPath)) {
            Log.d(TAG, "Received target path: $targetPath")
            val file = File(targetPath)
            if (file.exists()) {
                val mediaWrapper = MediaWrapper(Uri.fromFile(file))
                // Try to set other required fields if possible, or rely on defaults in DrmDecodeTask
                DrmDecodeTask().execute(mediaWrapper)
            } else {
                Log.e(TAG, "File does not exist: $targetPath")
                finish()
            }
        } else {
             // Default behavior or UI for manual selection logic (omitted for now)
             Log.d(TAG, "No target_path provided, waiting for user interaction or manual run.")
        }
    }

    // Need to expose this for the inner class or pass it in.
    // However, DrmDecodeTask is static-like (inner class) but used instance methods.
    // Let's make it an inner class that can access 'this' if needed, or pass Context.
    private inner class DrmDecodeTask : AsyncTask<MediaWrapper?, Void?, MediaWrapper?>() {
        override fun doInBackground(vararg params: MediaWrapper): MediaWrapper? {
            val media = params[0]
            var devKey: String = DeviceInfo.getDeviceIDForDRM(applicationContext) // Pass context if needed
            val pref: SharedPreferences = getSharedPreferences("video", Activity.MODE_PRIVATE)
            
            // Simplified logic for devKey
            if (TextUtils.isEmpty(devKey)) {
                 devKey = pref.getString("devkey", "dev_key12345") ?: "dev_key12345"
            } else {
                 pref.edit().putString("devkey", devKey).apply()
            }

            var orderID = media.orderID
            if (TextUtils.isEmpty(orderID)) orderID = "woongjin!drm@anypass#"
            
            // Ensure local path directories exist
            LOCAL_PATH.makeSureFileExistsAndDrmKind(LOCAL_PATH.DRM_KIND.KIND_VID)

            // DrmDecode instantiation
            // Note: The original code accessed mConInfo.MEMBER_CODE from MainActivity, which wasn't defined.
            // We'll use a dummy or "anypass" member code as per user context or defaults.
            val memberCode = "dummy_member" 
            
            val mExecDrm = DrmDecode(applicationContext, devKey, memberCode, orderID)
            val drmResponse: DrmDecode.DrmResponse? = mExecDrm.decodeFile(media.uri.path)

            if (drmResponse != null) {
                Log.d(TAG, "DrmDecodeTask drmResponse.errorCode : " + drmResponse.errorCode)
                 if (drmResponse.errorCode == "00000000") {
                    // Success
                    Log.d(TAG, "Decryption Successful: " + drmResponse.sFullPath)
                    media.uriDRM = Uri.parse(drmResponse.sFullPath)
                    media.isDRMDone = true
                } else {
                    Log.e(TAG, "Decryption Failed with error code: ${drmResponse.errorCode}")
                    media.isDRMDone = false
                }
            } else {
                Log.e(TAG, "DrmDecodeTask drmResponse == null")
                return null
            }
            
            // Cleanup DRM manager if needed
            mExecDrm.closeDrmManager()
            
            return media
        }

        override fun onPostExecute(mw: MediaWrapper?) {
            super.onPostExecute(mw)
            if (mw != null && mw.isDRMDone) {
                Log.d(TAG, "DONE: File decrypted successfully.")
                // Create a signal file for the PC script
                try {
                     File("/sdcard/undrm_done.txt").createNewFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.e(TAG, "DONE: Decryption failed.")
                try {
                     File("/sdcard/undrm_fail.txt").createNewFile()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // Close the app independently of success/fail to return control
            finish()
        }
    }
}


