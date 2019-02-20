package com.deucate.fizz5employee.current_order

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.blikoon.qrcodescanner.QrCodeActivity
import com.deucate.fizz5employee.R
import com.deucate.fizz5employee.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_current_order.*

class CurrentOrderActivity : AppCompatActivity() {

    private val products = ArrayList<Product>()
    private lateinit var adapter: CurrentOrderAdapter

    private val requestCode = 69

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_order)

        val temp = intent.getSerializableExtra("Product") as ArrayList<HashMap<String, Any>>
        val orderID = intent.getStringExtra("OrderID")

        for (product in temp) {
            products.add(
                Product(
                    id = product["id"] as String,
                    name = product["name"] as String,
                    rating = product["rating"] as Long,
                    hasDiscount = product["hasDiscount"] as Boolean,
                    price = product["price"] as Long,
                    isStock = product["isStock"] as Boolean?,
                    discountedPrice = product["discountedPrice"] as Long,
                    description = product["description"] as String,
                    image = product["image"] as String,
                    quantity = product["quantity"].toString().toInt(),
                    status = product["status"] as Boolean?
                )
            )
        }

        adapter = CurrentOrderAdapter(products, object : CurrentOrderAdapter.OnClick {
            override fun onClickCard(product: Product) {
                startActivityForResult(
                    Intent(
                        this@CurrentOrderActivity,
                        QrCodeActivity::class.java
                    ), requestCode
                )
            }
        })

        val recyclerView = currentOrderRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        currentOrderDoneButton.setOnClickListener {

            //checking all status
            for (product in products) {
                if (!product.status!!) {
                    Toast.makeText(this, "Please add all products", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val data = HashMap<String, Any>()
            data["status"] = 3

            //change the status
            FirebaseFirestore.getInstance().collection(getString(R.string.order)).document(orderID)
                .update(data).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        AlertDialog.Builder(this).setTitle("Error")
                            .setMessage(it.exception!!.localizedMessage).show()
                    }
                    finish()
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

            for ((i, product) in products.withIndex()) {
                if (product.image == result) {
                    this.products[i].status = true
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}
