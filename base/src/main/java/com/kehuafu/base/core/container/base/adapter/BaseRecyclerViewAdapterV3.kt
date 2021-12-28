package com.kehuafu.base.core.container.base.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import com.kehuafu.base.core.viewbinding.IViewBinding

/**
 * Created by light
 *
 * on 2021/09/06
 *
 * desc:
 *
 */
abstract class BaseRecyclerViewAdapterV3<VB : ViewBinding, Item, VH : BaseRecyclerViewAdapterV3.BaseViewHolder<Item>> :
    RecyclerView.Adapter<VH>() {

    companion object {
        private val TAG = BaseRecyclerViewAdapterV3::class.java.simpleName

        const val EMPTY_TYPE = -0x01
    }

    var mItems: MutableList<Item> = mutableListOf()

    protected var mOnItemClickListener: OnItemClickListener<Item>? = null

    private var mOnCreateEmptyViewHolderCallback: OnCreateEmptyViewHolderCallback? = null

    protected var mShowEmptyView = true

    fun setOnCreateEmptyViewHolderCallback(onCreateEmptyViewHolderCallback: OnCreateEmptyViewHolderCallback) {
        this.mOnCreateEmptyViewHolderCallback = onCreateEmptyViewHolderCallback
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Item>) {
        this.mOnItemClickListener = onItemClickListener
    }

    fun setShowEmptyView(showEmptyView: Boolean) {
        this.mShowEmptyView = showEmptyView
    }

    override fun getItemCount(): Int {
        return if (mShowEmptyView && mItems.isEmpty()) 0 else mItems.size
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.d(TAG, "onCreateViewHolder: ")
        return if (viewType == EMPTY_TYPE) {
            mOnCreateEmptyViewHolderCallback?.onCreateEmptyViewHolder(parent) as? VH
                ?: throw IllegalArgumentException(" Unrealized Empty ViewHolder")
        } else {
            onCreateVH(parent, viewType)
        }
    }

    abstract fun onCreateVH(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (mItems.isEmpty()) return
        val item = mItems[position]
        holder.setState(item)
    }

    override fun getItemViewType(position: Int): Int {
//        return if (getRealItemCount() == 0) {
//            EMPTY_TYPE
//        } else {
//            super.getItemViewType(position)
//        }
        return super.getItemViewType(position)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        when (val manager = recyclerView.layoutManager) {
            is GridLayoutManager -> {
                val gridManager = manager
                gridManager.spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (getItemViewType(position) == EMPTY_TYPE) gridManager.spanCount else 1
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                //val p = manager.lay as StaggeredGridLayoutManager.LayoutParams
            }
        }
    }

    fun getRealItemCount(): Int {
        return mItems.size
    }

    private fun diffAllItem(newItems: MutableList<Item>) {
        mItems = mutableListOf()
        val diffResult = DiffUtil.calculateDiff(RvDiffItemCallback(mItems, newItems), false)
        mItems = newItems
        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                notifyItemRangeChanged(position, count, payload)
                LogUtils.a("diffAllItem", "notifyItemRangeChanged--->", position)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
                LogUtils.a("diffAllItem", "onMoved--->${fromPosition}", toPosition)
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
                LogUtils.a("diffAllItem", "onInserted--->", position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
                LogUtils.a("diffAllItem", "onRemoved--->", position)
            }

        })
    }

    fun removeItem(position: Int) {
        if (position == -1 || position > mItems.size) return
        mItems.removeAt(position)
        notifyItemRemoved(position)
    }

    fun itemChanged(position: Int, item: Item) {
        mItems[position] = item
        notifyItemChanged(position)
    }

    fun resetItems(newItems: MutableList<Item>?) {
        if (newItems.isNullOrEmpty()) return
        diffAllItem(newItems)
    }

    interface OnItemClickListener<Item> {
        fun onItemClick(itemView: View, item: Item, position: Int? = 0)
    }

    interface OnItemLongClickListener<Item> : OnItemClickListener<Item> {
        fun onItemLongClick(view: View, position: Int, item: Item)
    }

    interface OnCreateEmptyViewHolderCallback {
        fun onCreateEmptyViewHolder(parent: ViewGroup): BaseViewHolder<*>
        fun onBindEmptyViewHolder(holder: BaseViewHolder<*>, position: Int)
    }

    class RvDiffItemCallback<Item>(val old: MutableList<Item>, val new: MutableList<Item>) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        //判断id是否相同，由于是 string，没有id，所以就直接比较了
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition]?.equals(new[newItemPosition])!!
        }

        //判断内容是否相同
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition]?.equals(new[newItemPosition])!!
        }
    }

    abstract class BaseViewHolder<Item>(override val viewBinding: ViewBinding) :
        RecyclerView.ViewHolder(viewBinding.root), IViewBinding {

        protected var mOnItemClickListener: OnItemClickListener<Item>? = null
        protected var mItem: Item? = null

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Item>?) {
            this.mOnItemClickListener = onItemClickListener
        }

        open fun setState(item: Item) {
            this.mItem = item
            itemView.setOnClickListener { itemView ->
                mOnItemClickListener?.onItemClick(itemView, item = item, position = adapterPosition)
            }
            if (mOnItemClickListener is OnItemLongClickListener) {
                itemView.setOnLongClickListener {
                    (mOnItemClickListener as? OnItemLongClickListener)?.onItemLongClick(
                        itemView,
                        item = item,
                        position = adapterPosition
                    )
                    return@setOnLongClickListener true
                }
            }
        }
    }
}


