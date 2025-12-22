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
    private val config: BinderRecyclerAdapterConfig<T, VH> = BinderRecyclerAdapterConfig.Builder<T, VH>()
        .build()
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
     *
     * This interface provides hooks for monitoring adapter lifecycle events.
     * Both methods have default implementations, making the interface optional to implement.
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
     * Returns a read-only view of the current items in the adapter.
     *
     * @return An immutable list of items.
     */
    val currentItems: List<T>
        get() = items

    /**
     * Updates the adapter's data set and uses DiffUtil for efficient rendering if enabled.
     *
     * @param newItems The new list of items.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<T>) {
        when {
            config.enableDiffUtil -> {
                val diffCallback = config.diffUtilCallback ?: DefaultDiffUtilCallback(items, newItems)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                items = newItems
                diffResult.dispatchUpdatesTo(this)
            }
            else -> {
                items = newItems
                notifyDataSetChanged()
            }
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
        require(index in 0..items.size) { "Invalid index: $index, size: ${items.size}" }
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
     * @throws IllegalArgumentException if the index is out of range.
     */
    fun addItems(newItems: List<T>, index: Int) {
        require(index in 0..items.size) { "Invalid index: $index, size: ${items.size}" }
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
     * @throws IllegalArgumentException if the position is out of range.
     */
    fun removeItemAt(position: Int) {
        require(position in items.indices) { "Invalid position: $position, size: ${items.size}" }
        val updatedItems = items.toMutableList().apply { removeAt(position) }
        updateItems(updatedItems)
    }

    /**
     * Removes a given item from the adapter.
     *
     * @param item The item to remove
     */
    fun removeItem(item: T) {
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
     * Uses Kotlin's null-safe operators for concise equality checks.
     */
    private class DefaultDiffUtilCallback<T>(
        private val oldList: List<T>,
        private val newList: List<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList.getOrNull(oldItemPosition) == newList.getOrNull(newItemPosition)

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList.getOrNull(oldItemPosition) == newList.getOrNull(newItemPosition)
    }
}

/**
 * Configuration class for BinderRecyclerAdapter to customize behavior.
 * Uses modern Kotlin data class pattern with default parameters for better ergonomics.
 *
 * @param T The type of the data model.
 * @param VH The ViewHolder type, must extend RecyclerView.ViewHolder.
 * @property adapterCallback Optional callback for lifecycle events.
 * @property viewTypeResolver Optional lambda to resolve item view types.
 * @property enableDiffUtil Whether to use DiffUtil for efficient list updates. Defaults to true.
 * @property diffUtilCallback Optional custom DiffUtil.Callback for item comparison.
 */
data class BinderRecyclerAdapterConfig<T, VH : RecyclerView.ViewHolder>(
    val adapterCallback: BinderRecyclerAdapter.AdapterCallback<T, VH>? = null,
    val viewTypeResolver: ((T) -> Int)? = null,
    val enableDiffUtil: Boolean = true,
    val diffUtilCallback: DiffUtil.Callback? = null
) {

    /**
     * Builder class for creating BinderRecyclerAdapterConfig instances.
     * Retained for backward compatibility with existing code.
     */
    class Builder<T, VH : RecyclerView.ViewHolder>(
        private var adapterCallback: BinderRecyclerAdapter.AdapterCallback<T, VH>? = null,
        private var viewTypeResolver: ((T) -> Int)? = null,
        private var enableDiffUtil: Boolean = true,
        private var diffUtilCallback: DiffUtil.Callback? = null
    ) {
        /**
         * Sets an optional AdapterCallback for lifecycle events.
         *
         * @param callback The AdapterCallback.
         * @return This builder for chaining.
         */
        fun adapterCallback(callback: BinderRecyclerAdapter.AdapterCallback<T, VH>?) =
            apply { this.adapterCallback = callback }

        /**
         * Sets a lambda function to resolve item view types based on the data model.
         *
         * @param resolver The view type resolver lambda.
         * @return This builder for chaining.
         */
        fun viewTypeResolver(resolver: ((T) -> Int)?) = apply { this.viewTypeResolver = resolver }

        /**
         * Enables or disables DiffUtil for efficient list updates.
         *
         * @param enable Whether to enable DiffUtil.
         * @return This builder for chaining.
         */
        fun enableDiffUtil(enable: Boolean) = apply { this.enableDiffUtil = enable }

        /**
         * Sets a custom DiffUtil.Callback for item comparison.
         *
         * @param callback The custom DiffUtil.Callback.
         * @return This builder for chaining.
         */
        fun diffUtilCallback(callback: DiffUtil.Callback?) =
            apply { this.diffUtilCallback = callback }

        /**
         * Builds the BinderRecyclerAdapterConfig instance.
         *
         * @return The created BinderRecyclerAdapterConfig.
         */
        fun build(): BinderRecyclerAdapterConfig<T, VH> =
            BinderRecyclerAdapterConfig(
                adapterCallback = adapterCallback,
                viewTypeResolver = viewTypeResolver,
                enableDiffUtil = enableDiffUtil,
                diffUtilCallback = diffUtilCallback
            )
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
