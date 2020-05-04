@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package by.shostko

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class SingleItemAdapter<T, B : ViewBinding> private constructor(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
    private val binder: ((B, T?) -> Unit)? = null,
    initialValue: T? = null,
    initiallyVisible: Boolean = true
) : RecyclerView.Adapter<BindingViewHolder<B>>() {

    var value: T? = initialValue
        set(value) {
            if (field != value) {
                field = value
                if (isVisible) {
                    notifyItemChanged(0)
                }
            }
        }

    var isVisible: Boolean = initiallyVisible
        set(value) {
            if (field != value) {
                field = value
                if (value) notifyItemInserted(0) else notifyItemRemoved(0)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingViewHolder(parent, inflater)

    override fun getItemCount(): Int = if (isVisible) 1 else 0

    override fun onBindViewHolder(holder: BindingViewHolder<B>, position: Int) {
        binder?.invoke(holder.binding, value)
    }

    companion object {

        fun <B : ViewBinding> static(
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            initiallyVisible: Boolean = true
        ) = SingleItemAdapter<Unit, B>(inflater, null, null, initiallyVisible)

        fun <T : Any, B : ViewBinding> nullableOf(
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            initialValue: T? = null,
            initiallyVisible: Boolean = true,
            binder: (B, T?) -> Unit
        ) = SingleItemAdapter<T, B>(inflater, binder, initialValue, initiallyVisible)

        fun <T : Any, B : ViewBinding> of(
            inflater: (LayoutInflater, ViewGroup, Boolean) -> B,
            initialValue: T? = null,
            initiallyVisible: Boolean = true,
            emptyBinder: (B) -> Unit,
            binder: (B, T) -> Unit
        ) = SingleItemAdapter<T, B>(inflater, nullableBinder(emptyBinder, binder), initialValue, initiallyVisible)
    }
}

private fun <T : Any, B : ViewBinding> nullableBinder(
    emptyBinder: (B) -> Unit,
    binder: (B, T) -> Unit
): (B, T?) -> Unit = { binding, item ->
    if (item == null) {
        emptyBinder(binding)
    } else {
        binder(binding, item)
    }
}