package com.wjthinkbig.undrmapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.wjthinkbig.bookclubdrm.DrmDecode
import com.wjthinkbig.bookclubdrm.common.LOCAL_PATH

class MainActivity : AppCompatActivity() {


    init {
        val mExecDrm: DrmDecode? = null
        val mContext:Context? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this.applicationContext
        setContentView(R.layout.activity_main)
    }

    private fun requestDrmDecode() {
        val mw: MediaWrapper = getCurrentMediaWrapper()
        com.wjthinkbig.mvideo2.VideoPlayerActivity.DrmDecodeTask().execute(mw)
    }
    private class DrmDecodeTask : AsyncTask<MediaWrapper?, Void?, MediaWrapper?>() {
        protected override fun doInBackground(vararg params: MediaWrapper): MediaWrapper? {
            val media = params[0]
            var devKey: String = DeviceInfo.getDeviceIDForDRM()
            val pref: SharedPreferences = getSharedPreferences("video", Activity.MODE_PRIVATE)
            if (devKey != null) {
                val editor = pref.edit()
                editor.putString("devkey", devKey)
                editor.commit()
            } else {
                devKey = pref.getString("devkey", "dev_key12345")
            }
            //String orderID = "5951c35abb901dc254c437b4fdd238ed";
            var orderID = media.orderID
            if (TextUtils.isEmpty(orderID)) orderID = "woongjin!drm@anypass#"
            //LogWrapper.d(TAG, "orderID=" + orderID);
            LOCAL_PATH.makeSureFileExistsAndDrmKind(LOCAL_PATH.DRM_KIND.KIND_VID)
            //drmDestroy()
            mExecDrm
            var mExecDrm = DrmDecode(devKey, MainActivity.mConInfo.MEMBER_CODE, orderID)
            val drmResponse: DrmDecode.DrmResponse = mExecDrm.decodeFile(media.uri.toString().trim())
            //LogWrapper.d(TAG, "DrmDecodeTask 2. drm devKey : " + devKey + "  mConInfo.MEMBER_CODE : " + mConInfo.MEMBER_CODE  + " orderID : " + orderID);
            if (drmResponse != null) {
                LogWrapper.d(com.wjthinkbig.mvideo2.VideoPlayerActivity.TAG, "DrmDecodeTask drmResponse.errorCode : " + drmResponse.errorCode)
            } else {
                LogWrapper.d(com.wjthinkbig.mvideo2.VideoPlayerActivity.TAG, "DrmDecodeTask drmResponse == null")
                return null
            }
            if (drmResponse.errorCode.equals("00000000")) {
                var uriDRM = Uri.parse(drmResponse.sFullPath)
                if (media.uri.toString().startsWith(Environment.getExternalStorageDirectory().absolutePath)
                        || (SDcardUtils.getRemovableStorageDirectory(this@VideoPlayerActivity) != null
                                && media.uri.toString().startsWith(SDcardUtils.getRemovableStorageDirectory(this@VideoPlayerActivity).getAbsolutePath()))) { //media.getUri() = Uri.parse("file://" + media.getUri().toString());
                    LogWrapper.d(com.wjthinkbig.mvideo2.VideoPlayerActivity.TAG, "should be... -> media.getUri()=" + "file://" + media.uri.toString())
                    uriDRM = Uri.parse("file://$uriDRM")
                }
                media.uriDRM = uriDRM
                media.isDRMDone = true
            } else {
                media.isDRMDone = false
            }
            return media
        }

        override fun onPostExecute(mw: MediaWrapper?) {
            super.onPostExecute(mw)
            mIsDrmDecoding = false
            drmDecodeComplete(mw)
        }
    }

    private fun drmDestroy() {
        LogWrapper.d(
            com.wjthinkbig.mvideo2.VideoPlayerActivity.TAG,
            "VideoPlayerActivity drmDestroy mExecDrm=$mExecDrm"
        )
        if (mExecDrm != null) {
            mExecDrm.closeDrmManager()

            mExecDrm = null
        }
    }

}


