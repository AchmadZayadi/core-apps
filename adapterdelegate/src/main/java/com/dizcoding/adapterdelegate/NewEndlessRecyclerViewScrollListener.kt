package com.dizcoding.adapterdelegate

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


abstract class NewEndlessRecyclerViewScrollListener(layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 5
    var firstVisibleItem = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    var mLayoutManager: RecyclerView.LayoutManager = layoutManager
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        visibleItemCount = view.childCount
        totalItemCount = mLayoutManager.itemCount
        firstVisibleItem = (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
            <= (firstVisibleItem + visibleThreshold)
        ) {
            onLoadMore()
            loading = true
        }
    }


    abstract fun onLoadMore()

}