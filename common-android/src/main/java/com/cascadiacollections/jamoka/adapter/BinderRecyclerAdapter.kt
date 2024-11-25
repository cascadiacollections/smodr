package com.cascadiacollections.jamoka.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BinderRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
    private val binder: Binder<T, VH>
) : RecyclerView.Adapter<VH>() {

    interface Binder<T, VH : RecyclerView.ViewHolder> {
        fun bind(model: T, viewHolder: VH)
        fun createViewHolder(parent: ViewGroup): VH
    }

    var items: List<T> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return binder.createViewHolder(parent)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        binder.bind(items[position], viewHolder)
    }

    override fun getItemCount(): Int = items.size

    /**
     * Adds items to the current list and refreshes the adapter.
     */
    fun addItems(newItems: List<T>) {
        items = items + newItems
    }

    /**
     * Clears all items from the adapter.
     */
    fun clearItems() {
        items = emptyList()
    }
}