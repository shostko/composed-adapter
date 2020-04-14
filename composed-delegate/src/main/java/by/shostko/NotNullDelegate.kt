@file:Suppress("unused")

package by.shostko

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseNotNullDelegate<T : Any, B : ViewBinding>(
    diffItemCallback: DiffUtil.ItemCallback<T>,
    clickListener: ClickListener<T>?,
    private val itemIdProvider: ((T, Int) -> Long)?,
    onViewInflated: ((Int, B) -> Unit)?,
    private val binder: (B, T, Int, MutableList<Any>?) -> Unit
) : CoreDelegate<T, B>(diffItemCallback, clickListener, onViewInflated) {

    companion object {
        internal const val ERROR = "This adapter can't handle null item"
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int) {
        requireNotNull(item) { ERROR }
        binder(holder.binding, item, position, null)
        super.onBindViewHolder(holder, item, position)
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int, payloads: MutableList<Any>) {
        requireNotNull(item) { ERROR }
        binder(holder.binding, item, position, payloads)
        super.onBindViewHolder(holder, item, position, payloads)
    }

    final override fun getItemId(item: T?, position: Int): Long? {
        requireNotNull(item) { ERROR }
        return itemIdProvider?.invoke(item, position)
    }

    private fun <T> ClickListener<T>.inject(holder: RecyclerView.ViewHolder, item: T?) {
        if (item == null) {
            holder.itemView.setOnClickListener(null)
        } else {
            holder.itemView.setOnClickListener { this(item) }
        }
    }
}

open class SingleTypeNotNullDelegate<T : Any, B : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
    clickListener: ClickListener<T>?,
    itemIdProvider: ((T, Int) -> Long)?,
    onViewInflated: ((Int, B) -> Unit)?,
    binder: (B, T, Int, MutableList<Any>?) -> Unit
) : BaseNotNullDelegate<T, B>(diffCallback, clickListener, itemIdProvider, onViewInflated, binder) {

    final override fun onCreateViewHolderInternal(parent: ViewGroup, viewType: Int): BindingViewHolder<B> = BindingViewHolder(parent, inflater)

    final override fun getItemViewType(item: T?, position: Int): Int = 0
}

open class MultiTypeNotNullDelegate<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val viewTypeProvider: (T, Int) -> Int,
    private val inflaterProvider: (Int) -> (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
    clickListener: ClickListener<T>? = null,
    itemIdProvider: ((T, Int) -> Long)? = null,
    onViewInflated: ((Int, ViewBinding) -> Unit)? = null,
    binder: (ViewBinding, T, Int, MutableList<Any>?) -> Unit
) : BaseNotNullDelegate<T, ViewBinding>(diffCallback, clickListener, itemIdProvider, onViewInflated, binder) {

    final override fun onCreateViewHolderInternal(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> = BindingViewHolder(parent, inflaterProvider(viewType))

    final override fun getItemViewType(item: T?, position: Int): Int {
        requireNotNull(item) { ERROR }
        return viewTypeProvider(item, position)
    }
}