package com.deucate.fizz5employee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import android.app.Activity
import com.blikoon.qrcodescanner.QrCodeActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private var merchantID: String? = null
    private var employeeID: String? = null

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val REQUEST_CODE_QR_SCAN = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            startMainActivity()
        }
        setContentView(R.layout.activity_login)


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 69)
        }

        loginNextButton.setOnClickListener {
            val employeeID = loginEmployeeID.text.toString()

            if (employeeID.isEmpty()) {
                loginEmployeeID.error = "Please enter our employee id."
                return@setOnClickListener
            }

            this.employeeID = employeeID

            if (merchantID == null) {
                getMerchantID()
            } else {
                isUserValid()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getMerchantID() {
        startActivityForResult(Intent(this, QrCodeActivity::class.java), REQUEST_CODE_QR_SCAN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != Activity.RESULT_OK) {
            if (data == null)
                return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image")
            if (result != null) {
                val alertDialog = AlertDialog.Builder(this).create()
                alertDialog.setTitle("Scan Error")
                alertDialog.setMessage("QR Code could not be scanned")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
            return

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            merchantID = result
            isUserValid()
        }
    }

    private fun isUserValid() {
        db.collection(getString(R.string.vendors)).document(merchantID!!)
            .collection(getString(R.string.employees)).document(employeeID!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result!!
                    if (!result.exists()) {
                        AlertDialog.Builder(this).setTitle("Error")
                            .setMessage(it.exception!!.localizedMessage).show()
                    } else {
                        val email = "123@bigbaazar.com"
                        val password = "holyshit"

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { resu ->
                                if (resu.isSuccessful) {
                                    startMainActivity()
                                } else {
                                    AlertDialog.Builder(this).setTitle("Error")
                                        .setMessage(resu.exception!!.localizedMessage).show()
                                }
                            }
                    }
                } else {
                    AlertDialog.Builder(this).setTitle("Error")
                        .setMessage(it.exception!!.localizedMessage).show()
                }
            }
    }
}

