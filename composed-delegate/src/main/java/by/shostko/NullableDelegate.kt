@file:Suppress("unused")

package by.shostko

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

abstract class BaseNullableDelegate<T, B : ViewBinding>(
    diffItemCallback: DiffUtil.ItemCallback<T>,
    private val clickListener: ((B, T?, Int) -> Unit)?,
    private val itemIdProvider: ((T?, Int) -> Long)?,
    onViewInflated: ((Int, B) -> Unit)?,
    private val binder: (B, T?, Int, MutableList<Any>?) -> Unit
) : CoreDelegate<T, B>(diffItemCallback, onViewInflated) {

    final override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int) {
        binder(holder.binding, item, position, null)
        super.onBindViewHolder(holder, item, position)
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int, payloads: MutableList<Any>) {
        binder(holder.binding, item, position, payloads)
        super.onBindViewHolder(holder, item, position, payloads)
    }

    final override fun getItemId(item: T?, position: Int): Long? = itemIdProvider?.invoke(item, position)

    final override fun injectClickListener(holder: BindingViewHolder<B>, item: T?, position: Int) {
        clickListener?.let {
            holder.itemView.setOnClickListener { it(holder.binding, item, position) }
        }
    }
}

open class SingleTypeNullableDelegate<T, B : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
    clickListener: ((B, T?, Int) -> Unit)?,
    itemIdProvider: ((T?, Int) -> Long)?,
    onViewInflated: ((Int, B) -> Unit)?,
    binder: (B, T?, Int, MutableList<Any>?) -> Unit
) : BaseNullableDelegate<T, B>(diffCallback, clickListener, itemIdProvider, onViewInflated, binder) {

    final override fun onCreateViewHolderInternal(parent: ViewGroup, viewType: Int): BindingViewHolder<B> = BindingViewHolder(parent, inflater)

    final override fun getItemViewType(item: T?, position: Int): Int = 0
}

open class MultiTypeNullableDelegate<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
    private val viewTypeProvider: (T?, Int) -> Int,
    private val inflaterProvider: (Int) -> (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
    clickListener: ((ViewBinding, T?, Int) -> Unit)?,
    itemIdProvider: ((T?, Int) -> Long)? = null,
    onViewInflated: ((Int, ViewBinding) -> Unit)? = null,
    binder: (ViewBinding, T?, Int, MutableList<Any>?) -> Unit
) : BaseNullableDelegate<T, ViewBinding>(diffCallback, clickListener, itemIdProvider, onViewInflated, binder) {

    final override fun onCreateViewHolderInternal(parent: ViewGroup, viewType: Int): BindingViewHolder<ViewBinding> = BindingViewHolder(parent, inflaterProvider(viewType))

    final override fun getItemViewType(item: T?, position: Int): Int = viewTypeProvider(item, position)
}

fun <T : Any, B : ViewBinding> nullableBinder(
    emptyBinder: (B, Int, MutableList<Any>?) -> Unit,
    binder: (B, T, Int, MutableList<Any>?) -> Unit
): (B, T?, Int, MutableList<Any>?) -> Unit = { binding, item, position, payloads ->
    if (item == null) {
        emptyBinder(binding, position, payloads)
    } else {
        binder(binding, item, position, payloads)
    }
}

fun <T : Any, B : ViewBinding> nullableClickListener(
    emptyClickListener: ((B, Int) -> Unit)?,
    clickListener: ((B, T, Int) -> Unit)?
): ((B, T?, Int) -> Unit)? =
    if (emptyClickListener == null && clickListener == null) {
        null
    } else {
        { binding, item, position ->
            if (item == null) {
                emptyClickListener?.invoke(binding, position)
            } else {
                clickListener?.invoke(binding, item, position)
            }
        }
    }