package com.cascadiacollections.jamoka.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * A generic RecyclerView Adapter that provides extensibility, performance optimizations,
 * and flexibility for binding data to views.
 *
 * @param T The type of the data model.
 * @param VH The ViewHolder type, must extend RecyclerView.ViewHolder.
 * @param binder The ViewHolderBinder responsible for binding data and creating view holders.
 * @param config Optional configuration object to customize adapter behavior.
 */
class BinderRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
    private val binder: ViewHolderBinder<T, VH>,
    private val config: BinderRecyclerAdapterConfig<T> = BinderRecyclerAdapterConfig.Builder<T>().build()
) : RecyclerView.Adapter<VH>() {

    /**
     * Interface for binding items and creating ViewHolders.
     */
    interface ViewHolderBinder<T, VH : RecyclerView.ViewHolder> {
        /**
         * Binds the data model to the ViewHolder.
         *
         * @param model The data model item.
         * @param viewHolder The ViewHolder to bind the data to.
         * @param position The position of the item in the list.
         */
        fun bind(model: T, viewHolder: VH, position: Int)

        /**
         * Creates a new ViewHolder instance.
         *
         * @param parent The parent ViewGroup.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder instance.
         */
        fun createViewHolder(parent: ViewGroup, viewType: Int): VH
    }

    /**
     * Optional callbacks for lifecycle events.
     */
    interface AdapterCallback<T, VH : RecyclerView.ViewHolder> {
        /**
         * Called when an item has been bound to a ViewHolder.
         *
         * @param model The data model item.
         * @param viewHolder The ViewHolder that was bound.
         * @param position The position of the item in the adapter.
         */
        fun onItemBound(model: T, viewHolder: VH, position: Int) {}

        /**
         * Called when a new ViewHolder has been created.
         *
         * @param viewHolder The newly created ViewHolder.
         */
        fun onViewHolderCreated(viewHolder: VH) {}
    }

    private var items: List<T> = emptyList()

    /**
     * Updates the adapter's data set and uses DiffUtil for efficient rendering if enabled.
     *
     * @param newItems The new list of items.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<T>) {
        if (config.enableDiffUtil) {
            val diffCallback = config.diffUtilCallback ?: DefaultDiffUtilCallback(items, newItems)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            items = newItems
            diffResult.dispatchUpdatesTo(this)
        } else {
            items = newItems
            notifyDataSetChanged()
        }
    }

    /**
     * Adds a single item to the end of the list and notifies the adapter.
     * @param item The item to add.
     */
    fun addItem(item: T) {
        val updatedItems = items + item
        updateItems(updatedItems)
    }

    /**
     * Inserts a single item to a specific index of the list and notifies the adapter.
     * @param item The item to add.
     * @param index The index to add the item
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    fun addItem(item: T, index: Int) {
        if (index < 0 || index > items.size) {
            throw IndexOutOfBoundsException("Invalid index: $index, size: ${items.size}")
        }
        val updatedItems = items.toMutableList().apply { add(index, item) }
        updateItems(updatedItems)
    }

    /**
     * Adds multiple items to the end of the list and notifies the adapter.
     * @param newItems The new items to add.
     */
    fun addItems(newItems: List<T>) {
        val updatedItems = items + newItems
        updateItems(updatedItems)
    }

    /**
     * Inserts multiple items at a specific index in the list and notifies the adapter.
     * @param newItems The new items to add.
     * @param index The index to add the item
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    fun addItems(newItems: List<T>, index: Int) {
        if (index < 0 || index > items.size) {
            throw IndexOutOfBoundsException("Invalid index: $index, size: ${items.size}")
        }
        val updatedItems = items.toMutableList().apply { addAll(index, newItems) }
        updateItems(updatedItems)
    }

    /**
     * Clears all items from the adapter.
     */
    fun clearItems() {
        updateItems(emptyList())
    }

    /**
     * Removes an item at the specified position.
     *
     * @param position The position of the item to remove.
     * @throws IndexOutOfBoundsException if the position is out of range.
     */
    fun removeItemAt(position: Int) {
        if (position !in items.indices) {
            throw IndexOutOfBoundsException("Invalid position: $position, size: ${items.size}")
        }
        val updatedItems = items.toMutableList().apply { removeAt(position) }
        updateItems(updatedItems)
    }

    /**
     * Removes a given item from the adapter.
     *
     * @param item The item to remove
     */
    fun removeItem(item: T){
        val updatedItems = items.toMutableList().apply { remove(item) }
        updateItems(updatedItems)
    }

    /**
     * Gets the item at the specified position.
     *
     * @param position The position of the item.
     * @return The item at the specified position, or null if the position is out of bounds.
     */
    fun getItem(position: Int): T? {
        return items.getOrNull(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = binder.createViewHolder(parent, viewType)
        config.adapterCallback?.onViewHolderCreated(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val item = items[position]
        binder.bind(item, viewHolder, position)
        config.adapterCallback?.onItemBound(item, viewHolder, position)
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return config.viewTypeResolver?.invoke(items[position]) ?: 0
    }

    /**
     * Default implementation of DiffUtil.Callback for basic comparisons.
     */
    private class DefaultDiffUtilCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList.getOrNull(oldItemPosition)
            val newItem = newList.getOrNull(newItemPosition)
            return if (oldItem != null && newItem != null) {
                oldItem == newItem
            } else {
                oldItem == null && newItem == null
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList.getOrNull(oldItemPosition)
            val newItem = newList.getOrNull(newItemPosition)
            return if (oldItem != null && newItem != null) {
                oldItem == newItem // Rely on equals() for content comparison
            } else {
                oldItem == null && newItem == null
            }
        }
    }
}

/**
 * Configuration class for BinderRecyclerAdapter to customize behavior.
 * Uses the Builder pattern for better readability with multiple options.
 *
 * @param T The type of the data model.
 */
class BinderRecyclerAdapterConfig<T> private constructor(
    val enableDiffUtil: Boolean,
    val diffUtilCallback: DiffUtil.Callback?,
    val adapterCallback: BinderRecyclerAdapter.AdapterCallback<T, RecyclerView.ViewHolder>?,
    val viewTypeResolver: ((T) -> Int)?
) {

    /**
     * Builder class for creating BinderRecyclerAdapterConfig instances.
     */
    class Builder<T>(
        private var enableDiffUtil: Boolean = true,
        private var diffUtilCallback: DiffUtil.Callback? = null,
        private var adapterCallback: BinderRecyclerAdapter.AdapterCallback<T, RecyclerView.ViewHolder>? = null,
        private var viewTypeResolver: ((T) -> Int)? = null
    ) {
        /**
         * Enables or disables the use of DiffUtil for item updates.
         *
         * @param enable True to enable DiffUtil, false otherwise.
         */
        fun enableDiffUtil(enable: Boolean) = apply { this.enableDiffUtil = enable }

        /**
         * Sets a custom DiffUtil.Callback implementation.
         *
         * @param callback The custom DiffUtil.Callback.
         */
        fun diffUtilCallback(callback: DiffUtil.Callback?) = apply { this.diffUtilCallback = callback }

        /**
         * Sets an optional AdapterCallback for lifecycle events.
         *
         * @param callback The AdapterCallback.
         */
        fun adapterCallback(callback: BinderRecyclerAdapter.AdapterCallback<T, RecyclerView.ViewHolder>?) =
            apply { this.adapterCallback = callback }

        /**
         * Sets a lambda function to resolve item view types based on the data model.
         *
         * @param resolver The view type resolver lambda.
         */
        fun viewTypeResolver(resolver: ((T) -> Int)?) = apply { this.viewTypeResolver = resolver }

        /**
         * Builds the BinderRecyclerAdapterConfig instance.
         *
         * @return The created BinderRecyclerAdapterConfig.
         */
        fun build(): BinderRecyclerAdapterConfig<T> =
            BinderRecyclerAdapterConfig(enableDiffUtil, diffUtilCallback, adapterCallback, viewTypeResolver)
    }
}

/**
 * Default implementation of ViewHolderBinder for simple data-binding use cases.
 *
 * @param T The type of the data model.
 * @param VH The type of the ViewHolder.
 * @param layoutResId The resource ID of the layout to inflate for each item.
 * @param onBindViewHolder Function to bind data to the ViewHolder's views.
 * @param viewHolderCreator Lambda function to create a ViewHolder instance from a View.
 */
class DefaultBinder<T, VH : RecyclerView.ViewHolder>(
    @LayoutRes private val layoutResId: Int,
    private val onBindViewHolder: (T, VH, Int) -> Unit,
    private val viewHolderCreator: (View) -> VH
) : BinderRecyclerAdapter.ViewHolderBinder<T, VH> {

    override fun bind(model: T, viewHolder: VH, position: Int) {
        onBindViewHolder(model, viewHolder, position)
    }

    override fun createViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return viewHolderCreator(view)
    }
}