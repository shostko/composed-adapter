package by.shostko

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

interface AdapterDelegate<T, H : RecyclerView.ViewHolder> {
    fun getDiffItemCallback(): DiffUtil.ItemCallback<T>
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H
    fun onBindViewHolder(holder: H, item: T?, position: Int)
    fun onBindViewHolder(holder: H, item: T?, position: Int, payloads: MutableList<Any>)
    fun getItemViewType(item: T?, position: Int): Int
    fun getItemId(item: T?, position: Int): Long?
}

typealias ClickListener<T> = (T) -> Unit

class BindingViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root) {
    constructor(parent: ViewGroup, inflater: (LayoutInflater, ViewGroup, Boolean) -> B) : this(inflater(LayoutInflater.from(parent.context), parent, false))
}

abstract class CoreDelegate<T, B : ViewBinding>(
    private val diffItemCallback: DiffUtil.ItemCallback<T>,
    private val clickListener: ClickListener<T>?,
    private val onViewInflated: ((Int, B) -> Unit)?
) : AdapterDelegate<T, BindingViewHolder<B>> {

    final override fun getDiffItemCallback(): DiffUtil.ItemCallback<T> = diffItemCallback

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> {
        val holder = onCreateViewHolderInternal(parent, viewType)
        onViewInflated?.invoke(viewType, holder.binding)
        return holder
    }

    abstract fun onCreateViewHolderInternal(parent: ViewGroup, viewType: Int): BindingViewHolder<B>

    @CallSuper
    override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int) {
        clickListener?.inject(holder, item)
    }

    @CallSuper
    override fun onBindViewHolder(holder: BindingViewHolder<B>, item: T?, position: Int, payloads: MutableList<Any>) {
        clickListener?.inject(holder, item)
    }

    private fun <T> ClickListener<T>.inject(holder: RecyclerView.ViewHolder, item: T?) {
        if (item == null) {
            holder.itemView.setOnClickListener(null)
        } else {
            holder.itemView.setOnClickListener { this(item) }
        }
    }
}