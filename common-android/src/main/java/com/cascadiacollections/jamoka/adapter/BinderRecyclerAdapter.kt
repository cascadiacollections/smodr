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
 * @param VH The ViewHolder type.
 * @param viewHolderBinder The ViewHolderBinder responsible for binding data and creating view holders.
 * @param config Optional configuration object to customize adapter behavior.
 */
class BinderRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
    private val viewHolderBinder: ViewHolderBinder<T, VH>,
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
         */
        fun bind(model: T, viewHolder: VH)

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
         */
        fun onItemBound(model: T, viewHolder: VH) {}

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
     * Adds a single item to the list and notifies the adapter.
     *
     * @param item The item to add.
     */
    fun addItem(item: T) {
        val updatedItems = items + item
        updateItems(updatedItems)
    }

    /**
     * Adds multiple items to the list and notifies the adapter.
     *
     * @param newItems The new items to add.
     */
    fun addItems(newItems: List<T>) {
        val updatedItems = items + newItems
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
        if (position in items.indices) {
            val updatedItems = items.toMutableList().also { it.removeAt(position) }
            updateItems(updatedItems)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = viewHolderBinder.createViewHolder(parent, viewType)
        config.adapterCallback?.onViewHolderCreated(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val item = items[position]
        viewHolderBinder.bind(item, viewHolder)
        config.adapterCallback?.onItemBound(item, viewHolder)
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
    private val onBindViewHolder: (T, VH) -> Unit,
    private val viewHolderCreator: (View) -> VH
) : BinderRecyclerAdapter.ViewHolderBinder<T, VH> {

    override fun bind(model: T, viewHolder: VH) {
        onBindViewHolder(model, viewHolder)
    }

    override fun createViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return viewHolderCreator(view)
    }
}