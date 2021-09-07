@file:Suppress("LeakingThis")

package com.example.mvr.core.container.widget.refresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by sai
 *
 *
 * on 2019-07-18
 *
 *
 * desc:下拉刷新上拉加载控件，目前适用于RecyclerView
 */
open class RecyclerRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs),
    SwipeRefreshLayout.OnRefreshListener {

    private var mRecycleView: RecyclerView? = null
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var listener: SuperRefreshLayoutListener? = null
    var isLoading = false
        private set
    private var mCanLoadMore = true
    private var mHasMore = true
    private var mYDown: Int = 0
    private var mLastY: Int = 0
    private var mBottomCount = 1

    /**
     * 是否是上拉操作
     *
     * @return isPullUp
     */
    private val isPullUp: Boolean
        get() = mYDown - mLastY >= mTouchSlop

    /**
     * 判断是否到了最底部
     */
    private val isScrollBottom: Boolean
        get() = mRecycleView != null && mRecycleView!!.adapter != null && lastVisiblePosition == mRecycleView!!.adapter!!.itemCount - mBottomCount

    /**
     * 判断是否到了最底部
     */
    private val isNextScrollBottom: Boolean
        get() = mRecycleView != null && mRecycleView!!.adapter != null && lastVisiblePosition == mRecycleView!!.adapter!!.itemCount - 1

    /**
     * 获取RecyclerView可见的最后一项
     *
     * @return 可见的最后一项position
     */
    private val lastVisiblePosition: Int
        get() {
            val position: Int
            position = when (mRecycleView!!.layoutManager) {
                is LinearLayoutManager -> (mRecycleView!!.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                is GridLayoutManager -> (mRecycleView!!.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                is StaggeredGridLayoutManager -> {
                    val layoutManager = mRecycleView!!.layoutManager as StaggeredGridLayoutManager?
                    val lastPositions =
                        layoutManager!!.findLastVisibleItemPositions(IntArray(layoutManager.spanCount))
                    getMaxPosition(lastPositions)
                }
                else -> mRecycleView!!.layoutManager!!.itemCount - 1
            }
            return position
        }

    init {
        setColorSchemeColors(
            Color.parseColor("#ffa74c"),
            Color.parseColor("#f5bf6a"),
            Color.parseColor("#A47FE6"),
            Color.parseColor("#99A7FF")
        )
        setProgressBackgroundColorSchemeColor(Color.parseColor("#ffffff"))
        setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (listener != null && !isLoading) {
            listener!!.onRefreshing()
        } else
            isRefreshing = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 初始化ListView对象
        if (mRecycleView == null) {
            getRecycleView()
        }
    }

    /**
     * 获取RecyclerView，后续支持AbsListView
     */
    private fun getRecycleView() {
        if (childCount > 0) {
            mRecycleView = findRecyclerView(this)
            mRecycleView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (canLoad() && mCanLoadMore) {
                        loadData()
                    } else if (isNextScrollBottom && listener != null && mCanLoadMore && !isLoading) {
                        listener!!.onScrollToBottom()
                    }
                }
            })
        }
    }

    private fun findRecyclerView(view: ViewGroup): RecyclerView? {
        for (index in 0..view.childCount) {
            val child = view.getChildAt(index)
            return if (child is RecyclerView) {
                child
            } else {
                if (child is ViewGroup) {
                    findRecyclerView(child)
                } else {
                    null
                }
            }
        }
        return null
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mYDown = event.rawY.toInt()
            MotionEvent.ACTION_MOVE -> mLastY = event.rawY.toInt()
            else -> {
            }
        }
        return super.dispatchTouchEvent(event)
    }

    /**
     * 是否可以加载更多, 条件是到了最底部
     *
     * @return isCanLoad
     */
    private fun canLoad(): Boolean {
        return isScrollBottom && !isLoading && isPullUp && mHasMore
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private fun loadData() {
        if (listener != null) {
            setOnLoading(true)
            listener!!.onLoadMore()
        }
    }

    /**
     * 设置正在加载
     *
     * @param loading 设置正在加载
     */
    fun setOnLoading(loading: Boolean) {
        isLoading = loading
        if (!isLoading) {
            mYDown = 0
            mLastY = 0
        }
    }

    fun setBottomCount(mBottomCount: Int) {
        this.mBottomCount = mBottomCount
    }

    /**
     * 加载结束记得调用
     */
    fun onComplete() {
        setOnLoading(false)
        isRefreshing = false
        mHasMore = true
    }

    /**
     * 是否可加载更多
     *
     * @param mCanLoadMore 是否可加载更多
     */
    fun setCanLoadMore(mCanLoadMore: Boolean) {
        this.mCanLoadMore = mCanLoadMore
    }

    /**
     * 获得最大的位置
     *
     * @param positions 获得最大的位置
     * @return 获得最大的位置
     */
    private fun getMaxPosition(positions: IntArray): Int {
        var maxPosition = Integer.MIN_VALUE
        for (position in positions) {
            maxPosition = Math.max(maxPosition, position)
        }
        return maxPosition
    }

    /**
     * 添加加载和刷新
     *
     * @param listener add the listener for SuperRefreshLayout
     */
    fun setSuperRefreshLayoutListener(listener: SuperRefreshLayoutListener) {
        this.listener = listener
    }

    interface SuperRefreshLayoutListener {
        fun onRefreshing()
        fun onLoadMore() {}
        fun onScrollToBottom() {}
    }

    companion object {
        private val TAG = RecyclerView::class.java.simpleName
    }
}
