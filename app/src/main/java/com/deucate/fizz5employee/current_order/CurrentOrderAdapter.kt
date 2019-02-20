package com.deucate.fizz5employee.current_order

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.fizz5employee.R
import com.deucate.fizz5employee.model.Product
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_product.view.*

class CurrentOrderAdapter(
    private val products: ArrayList<Product>,
    private val listner: OnClick
) :
    RecyclerView.Adapter<CurrentOrderViewHolder>() {

    interface OnClick {
        fun onClickCard(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentOrderViewHolder {
        return CurrentOrderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_product,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: CurrentOrderViewHolder, position: Int) {
        val product = products[position]

        holder.title.text = product.name
        if (product.status == null) {
            product.status = false
        }
        val color = if (product.status!!) {
            Color.GREEN
        } else {
            Color.RED
        }

        holder.status.setBackgroundColor(color)

        Picasso.get().load(product.image).into(holder.imageView)

        holder.cardView.setOnClickListener {
            listner.onClickCard(product)
        }

    }

}

class CurrentOrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardView = view.cardCurrentProduct!!
    val imageView = view.productImageView!!
    val title = view.productCardTitle!!
    val status = view.productCurrentStatus!!

}