package com.deucate.fizz5employee

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.fizz5employee.model.Order
import kotlinx.android.synthetic.main.card_order.view.*

class MainAdapter(private val orders: ArrayList<Order>, private val listener: OnClick) :
    RecyclerView.Adapter<MainViewHolder>() {

    companion object {
        fun getStatus(status: Int): String {
            return when (status) {
                0 -> "Placed"
                1 -> "Waiting"
                2 -> "Dispatching"
                3 -> "Ready"
                4 -> "Done"
                else -> "Unknown"
            }
        }
    }

    interface OnClick {
        fun onClickCard(order: Order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_order,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val order = orders[position]

        holder.nameTV.text = order.userName
        holder.statusTV.text = getStatus(order.status)
        holder.products.text = "${order.products!!.size} products"

        holder.cardView.setOnClickListener {
            listener.onClickCard(order)
        }
    }


}

class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardView = view.orderCardView!!
    val nameTV = view.cardOrderName!!
    val statusTV = view.cardOrderStatus!!
    val products = view.cardOrderProducts!!
}