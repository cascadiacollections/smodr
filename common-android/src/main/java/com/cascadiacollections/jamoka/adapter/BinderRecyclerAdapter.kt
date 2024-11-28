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
 * @param binder The ItemBinder responsible for binding data and creating view holders.
 * @param config Optional configuration object to customize adapter behavior.
 */
class BinderRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(
    private val binder: ItemBinder<T, VH>,
    private val config: BinderRecyclerAdapterConfig<T> = BinderRecyclerAdapterConfig()
) : RecyclerView.Adapter<VH>() {

    /**
     * Interface for binding items and creating ViewHolders.
     */
    interface ItemBinder<T, VH : RecyclerView.ViewHolder> {
        fun bind(model: T, viewHolder: VH)
        fun createViewHolder(parent: ViewGroup, viewType: Int): VH
    }

    /**
     * Optional callbacks for lifecycle events.
     */
    interface AdapterCallback<T, VH : RecyclerView.ViewHolder> {
        fun onItemBound(model: T, viewHolder: VH) {}
        fun onViewHolderCreated(viewHolder: VH) {}
    }

    private var items: List<T> = EMPTY_LIST as List<T>

    /**
     * Updates items using DiffUtil for efficient rendering if enabled in the config.
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
     */
    fun addItem(item: T) {
        items = items + item
        notifyItemInserted(items.size - 1)
    }

    /**
     * Adds multiple items to the list and notifies the adapter.
     */
    fun addItems(newItems: List<T>) {
        items = items + newItems
        notifyItemRangeInserted(items.size - newItems.size, newItems.size)
    }

    /**
     * Clears all items from the adapter.
     */
    fun clearItems() {
        items = EMPTY_LIST as List<T>
        notifyDataSetChanged()
    }

    /**
     * Removes an item at the specified position.
     */
    fun removeItemAt(position: Int) {
        if (position in items.indices) {
            items = items.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewHolder = binder.createViewHolder(parent, viewType)
        config.adapterCallback?.onViewHolderCreated(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val item = items[position]
        binder.bind(item, viewHolder)
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
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val EMPTY_LIST = listOf<Any>()
    }
}

/**
 * Configuration class for BinderRecyclerAdapter to customize behavior.
 *
 * @param enableDiffUtil Enables or disables the use of DiffUtil.
 * @param diffUtilCallback Custom implementation of DiffUtil.Callback.
 * @param adapterCallback Optional callback for lifecycle events.
 * @param viewTypeResolver Lambda for resolving item view types.
 */
class BinderRecyclerAdapterConfig<T>(
    var enableDiffUtil: Boolean = true,
    var diffUtilCallback: DiffUtil.Callback? = null,
    var adapterCallback: BinderRecyclerAdapter.AdapterCallback<T, RecyclerView.ViewHolder>? = null,
    var viewTypeResolver: ((T) -> Int)? = null
)

/**
 * Default implementation of ItemBinder for simple data-binding use cases.
 *
 * @param layoutResId Resource ID of the layout to inflate for each item.
 * @param bindFunction Function to bind data to the ViewHolder's views.
 */
class DefaultBinder<T>(
    @LayoutRes private val layoutResId: Int,
    private val bindFunction: (T, View) -> Unit
) : BinderRecyclerAdapter.ItemBinder<T, RecyclerView.ViewHolder> {

    override fun bind(model: T, viewHolder: RecyclerView.ViewHolder) {
        bindFunction(model, viewHolder.itemView)
    }

    override fun createViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }
}