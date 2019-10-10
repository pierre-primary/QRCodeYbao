package com.ybao.qrcode.demo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker


object PermisionUtils {
    fun check(context: Context, permission: String, grantedAction: (() -> Unit)? = null): Boolean {
        val hasPermission = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> true
            context.applicationInfo.targetSdkVersion < Build.VERSION_CODES.M -> PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            else -> ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        if (hasPermission) grantedAction?.let { grantedAction() }
        return hasPermission
    }

    fun checkMultiple(context: Context, permissions: Array<String>, grantedAction: (() -> Unit)? = null): Array<String> {
        val needRequestPermissions = permissions.filter { p -> !check(context, p) }.toTypedArray()
        if (needRequestPermissions.isEmpty()) grantedAction?.let { grantedAction() }
        return needRequestPermissions
    }

    fun request(activity: Activity, permission: String, requestCode: Int) {
        requestMultiple(activity, arrayOf(permission), requestCode)
    }

    fun requestMultiple(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun verify(activity: Activity, permission: String, requestCode: Int, grantedAction: (() -> Unit)? = null): Boolean {
        val granted = check(activity, permission)
        if (granted) grantedAction?.let { grantedAction() } else request(activity, permission, requestCode)
        return granted
    }

    fun verifyMultiple(activity: Activity, permissions: Array<String>, requestCode: Int, grantedAction: (() -> Unit)? = null): Array<String> {
        val needRequestPermissions = checkMultiple(activity, permissions)
        if (needRequestPermissions.isEmpty()) grantedAction?.let { grantedAction() } else requestMultiple(activity, permissions, requestCode)
        return needRequestPermissions
    }

    fun verifyStorage(activity: Activity, requestCode: Int, grantedAction: (() -> Unit)? = null): Boolean {
        val granted = verifyMultiple(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requestCode).isEmpty()
        if (granted) grantedAction?.let { grantedAction() }
        return granted
    }

    fun checkRequestResult(context: Context, permissions: Array<out String>, grantResults: IntArray, action: ((granted: Boolean, failPermission: Array<String>?) -> Unit)? = null): Boolean {

        return if (grantResults.isEmpty() || permissions.isEmpty() || permissions.size != grantResults.size) {
            action?.let { action(false, null) }
            false
        } else {
            val pss = permissions.filterIndexed { index, _ ->
                grantResults[index] != PackageManager.PERMISSION_GRANTED
            }
            if (pss.isNotEmpty()) {
                action?.let { action(false, pss.toTypedArray()) }
                false
            } else {
                action?.let { action(true, null) }
                true
            }
        }
    }

    fun checkRequestGrant(context: Context, permissions: Array<out String>, grantResults: IntArray, grantedAction: (() -> Unit)) {
        if (checkRequestResult(context, permissions, grantResults)) {
            grantedAction()
        }
    }

    fun gotoApplicationDetails(context: Context) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }

    fun isDisableVerify(activity: Activity, permission: String): Boolean {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun isDisableVerifyMultiple(activity: Activity, permissions: Array<String>): Boolean {
        return permissions.any { ps -> isDisableVerify(activity, ps) }
    }
}