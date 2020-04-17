@file:Suppress("unused")

package by.shostko

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class ComposedPagedAdapter<T, H : RecyclerView.ViewHolder>(
    private val delegate: AdapterDelegate<T, H>
) : PagedListAdapter<T, H>(delegate.getDiffItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H = delegate.onCreateViewHolder(parent, viewType)
    override fun onBindViewHolder(holder: H, position: Int) = delegate.onBindViewHolder(holder, getItem(position), position)
    override fun onBindViewHolder(holder: H, position: Int, payloads: MutableList<Any>) = delegate.onBindViewHolder(holder, getItem(position), position, payloads)
    override fun getItemViewType(position: Int): Int = delegate.getItemViewType(getItem(position), position)
    override fun getItemId(position: Int): Long = delegate.getItemId(getItem(position), position) ?: super.getItemId(position)

    companion object {

        fun <T : Any, B : ViewBinding> nullableOf(
            diffCallback: DiffUtil.ItemCallback<T>,
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            clickListener: ((B, T?, Int) -> Unit)? = null,
            itemIdProvider: ((T?, Int) -> Long)? = null,
            onViewInflated: ((Int, B) -> Unit)? = null,
            binder: (B, T?, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(SingleTypeNullableDelegate(
            diffCallback,
            inflater,
            clickListener,
            itemIdProvider,
            onViewInflated,
            binder
        ))

        fun <T : Any> nullableOf(
            diffCallback: DiffUtil.ItemCallback<T>,
            viewTypeProvider: (T?, Int) -> Int,
            inflaterProvider: (Int) -> (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
            clickListener: ((ViewBinding, T?, Int) -> Unit)? = null,
            itemIdProvider: ((T?, Int) -> Long)? = null,
            onViewInflated: ((Int, ViewBinding) -> Unit)? = null,
            binder: (ViewBinding, T?, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(MultiTypeNullableDelegate(
            diffCallback,
            viewTypeProvider,
            inflaterProvider,
            clickListener,
            itemIdProvider,
            onViewInflated,
            binder
        ))

        fun <T : Any, B : ViewBinding> of(
            diffCallback: DiffUtil.ItemCallback<T>,
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            emptyClickListener: ((ViewBinding, Int) -> Unit)? = null,
            clickListener: ((ViewBinding, T, Int) -> Unit)? = null,
            itemIdProvider: ((T?, Int) -> Long)? = null,
            onViewInflated: ((Int, B) -> Unit)? = null,
            emptyBinder: (B, Int, MutableList<Any>?) -> Unit,
            binder: (B, T, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(SingleTypeNullableDelegate(
            diffCallback,
            inflater,
            nullableClickListener(emptyClickListener, clickListener),
            itemIdProvider,
            onViewInflated,
            nullableBinder(emptyBinder, binder)
        ))

        fun <T : Any> of(
            diffCallback: DiffUtil.ItemCallback<T>,
            viewTypeProvider: (T?, Int) -> Int,
            inflaterProvider: (Int) -> (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
            emptyClickListener: ((ViewBinding, Int) -> Unit)? = null,
            clickListener: ((ViewBinding, T, Int) -> Unit)? = null,
            itemIdProvider: ((T?, Int) -> Long)? = null,
            onViewInflated: ((Int, ViewBinding) -> Unit)? = null,
            emptyBinder: (ViewBinding, Int, MutableList<Any>?) -> Unit,
            binder: (ViewBinding, T, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(MultiTypeNullableDelegate(
            diffCallback,
            viewTypeProvider,
            inflaterProvider,
            nullableClickListener(emptyClickListener, clickListener),
            itemIdProvider,
            onViewInflated,
            nullableBinder(emptyBinder, binder)
        ))

        fun <T : Any, B : ViewBinding> of(
            diffCallback: DiffUtil.ItemCallback<T>,
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            clickListener: ((B, T, Int) -> Unit)? = null,
            itemIdProvider: ((T, Int) -> Long)? = null,
            onViewInflated: ((Int, B) -> Unit)? = null,
            binder: (B, T, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(SingleTypeNotNullDelegate(
            diffCallback,
            inflater,
            clickListener,
            itemIdProvider,
            onViewInflated,
            binder
        ))

        fun <T : Any> of(
            diffCallback: DiffUtil.ItemCallback<T>,
            viewTypeProvider: (T, Int) -> Int,
            inflaterProvider: (Int) -> (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
            clickListener: ((ViewBinding, T, Int) -> Unit)? = null,
            itemIdProvider: ((T, Int) -> Long)? = null,
            onViewInflated: ((Int, ViewBinding) -> Unit)? = null,
            binder: (ViewBinding, T, Int, MutableList<Any>?) -> Unit
        ): PagedListAdapter<T, out RecyclerView.ViewHolder> = ComposedPagedAdapter(MultiTypeNotNullDelegate(
            diffCallback,
            viewTypeProvider,
            inflaterProvider,
            clickListener,
            itemIdProvider,
            onViewInflated,
            binder
        ))
    }
}