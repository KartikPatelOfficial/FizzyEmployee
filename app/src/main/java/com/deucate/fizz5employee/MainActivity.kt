package com.deucate.fizz5employee

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blikoon.qrcodescanner.QrCodeActivity
import com.deucate.fizz5employee.current_order.CurrentOrderActivity
import com.deucate.fizz5employee.model.Order
import com.deucate.fizz5employee.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MainAdapter
    private val orders = ArrayList<Order>()

    private val requestCode = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = MainAdapter(orders, object : MainAdapter.OnClick {
            override fun onClickCard(order: Order) {
                val intent = Intent(this@MainActivity, CurrentOrderActivity::class.java)
                intent.putExtra("Product", order.products)
                intent.putExtra("OrderID", order.id)
                startActivity(intent)
            }
        })

        mainQRCodeScanner.setOnClickListener {
            startActivityForResult(Intent(this, QrCodeActivity::class.java), requestCode)
        }

        val recyclerView = mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        FirebaseFirestore.getInstance().collection(getString(R.string.order))
            .whereEqualTo("martID", "0eqYpRrSwk2sKIig7aOE").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val result = it.result!!
                    for (order in result) {
                        val status = order.getLong("status")!!.toInt()
                        if (status != 0) {
                            continue
                        }
                        orders.add(
                            Order(
                                id = order.id,
                                paymentMethod = order.getLong("paymentMethod")!!.toInt(),
                                address = order.getString("address")!!,
                                products = order.get("products")!! as ArrayList<Product>,
                                martName = order.getString("martName")!!,
                                martID = order.getString("martID")!!,
                                status = status,
                                time = order.getTimestamp("time")!!,
                                userName = order.getString("userName")!!,
                                userID = order.getString("userID")!!,
                                totalAmount = order.getLong("totalAmount")!!
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    AlertDialog.Builder(this).setTitle("Error")
                        .setMessage(it.exception!!.localizedMessage).show()
                }
            }
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
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            }
            return

        }
        if (requestCode == this.requestCode) {
            if (data == null)
                return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")

            val map = HashMap<String, Any>()
            map["status"] = 4
            FirebaseFirestore.getInstance().collection(getString(R.string.order)).document(result)
                .update(map).addOnCompleteListener {
                    Toast.makeText(this@MainActivity, "Thank ou for lul", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
