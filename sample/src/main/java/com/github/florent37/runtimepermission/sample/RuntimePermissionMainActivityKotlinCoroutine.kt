package com.github.florent37.runtimepermission.sample

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.runtimepermission.kotlin.PermissionException
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import com.github.florent37.runtimepermission.sample.AppendText.appendText
import kotlinx.android.synthetic.main.runtime_permissions_activity_request.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Sample of a very basic activity asking for permission.
 * It shows a button to trigger the permission dialog if permission is needed,
 * and hide it when it doesn't
 */
class RuntimePermissionMainActivityKotlinCoroutine : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.runtime_permissions_activity_request)

        requestView.setOnClickListener {
            myMethod()
        }
    }

    fun myMethod() = GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
        try {
            val result = askPermission(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION)
            //all permissions already granted or just granted
            //your action
            resultView.setText("Accepted :${result.accepted.toString()}")

        } catch (e: PermissionException) {
            if (e.hasDenied()) {
                appendText(resultView, "Denied :")
                //the list of denied permissions
                e.denied.forEach { permission ->
                    appendText(resultView, permission)
                }
                //but you can ask them again, eg:

                AlertDialog.Builder(this@RuntimePermissionMainActivityKotlinCoroutine)
                        .setMessage("Please accept our permissions")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain()
                        }
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show();
            }

            if (e.hasForeverDenied()) {
                appendText(resultView, "ForeverDenied")
                //the list of forever denied permissions, user has check 'never ask again'
                e.foreverDenied.forEach { permission ->
                    appendText(resultView, permission)
                }
                //you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
    }

}
