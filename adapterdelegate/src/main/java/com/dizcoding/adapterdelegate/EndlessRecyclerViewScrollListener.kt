package com.dizcoding.adapterdelegate

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class EndlessRecyclerViewScrollListener(layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private val visibleThreshold = 5
    private var previousTotalItemCount = 0
    private var loading = true
    var mLayoutManager: RecyclerView.LayoutManager = layoutManager
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount
        lastVisibleItemPosition =
            (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (totalItemCount < previousTotalItemCount) {
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            onLoadMore()
            loading = true
        }
    }

    fun resetState() {
        previousTotalItemCount = 0
        loading = true
    }

    abstract fun onLoadMore()

}