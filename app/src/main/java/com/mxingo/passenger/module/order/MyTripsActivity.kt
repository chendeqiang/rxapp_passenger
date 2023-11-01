package com.mxingo.passenger.module.order

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
//import androidx.core.widget.SwipeRefreshLayout
import androidx.appcompat.widget.Toolbar
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mxingo.driver.module.BaseActivity
import com.mxingo.driver.utils.Constants
import com.mxingo.passenger.R
import com.mxingo.passenger.model.ListOrderEntity
import com.mxingo.passenger.model.OrderEntity
import com.mxingo.passenger.module.base.http.ComponentHolder
import com.mxingo.passenger.module.base.http.MyPresenter
import com.mxingo.passenger.module.base.log.LogUtils
import com.mxingo.passenger.widget.MyFooterView
import com.mxingo.passenger.widget.MyProgress
import com.mxingo.passenger.widget.ShowToast
import com.squareup.otto.Subscribe
import javax.inject.Inject

class MyTripsActivity : BaseActivity(), TabLayout.OnTabSelectedListener {

    private lateinit var lvTrip: ListView
    private lateinit var llEmpty: LinearLayout
    private lateinit var adapter: OrderAdapter
    private lateinit var srlRefresh: SwipeRefreshLayout
    private lateinit var footView: MyFooterView
    private lateinit var tabLookOrder: TabLayout

    @Inject
    lateinit var presenter: MyPresenter
    private lateinit var progress: MyProgress
    private var userId = 0
    private val pageCount = 20
    private var pageIndex = 0
    private var tabAction: Int = 1

    companion object {
        @JvmStatic
        fun startMyTripsActivity(context: Context, userId: Int) {
            context.startActivity(Intent(context, MyTripsActivity::class.java).putExtra(Constants.USER_ID, userId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_trips)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        userId = intent.getIntExtra(Constants.USER_ID, 0)
        progress = MyProgress(this)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setToolbar(toolbar)
        val tvToolbar = findViewById(R.id.tv_toolbar_title) as TextView
        tvToolbar.text = "我的行程"

        srlRefresh = findViewById(R.id.srl_refresh) as SwipeRefreshLayout
        lvTrip = findViewById(R.id.lv_trip) as ListView
        llEmpty = findViewById(R.id.ll_empty) as LinearLayout
        tabLookOrder = findViewById(R.id.tab_look_order) as TabLayout

        adapter = OrderAdapter(this)
        footView = MyFooterView(this)
        lvTrip.emptyView = llEmpty
        lvTrip.adapter = adapter
//        lvTrip.addFooterView(footView)
        lvTrip.addFooterView(footView, null, false)

        tabLookOrder.addOnTabSelectedListener(this)
        srlRefresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorButtonBg))
        srlRefresh.setOnRefreshListener {
            pageIndex = 0
            adapter.clear()
            presenter.listOrder(userId, pageIndex, pageCount)
        }

        lvTrip.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount == 0)
                    return
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    val lastItemView = lvTrip.getChildAt(lvTrip.childCount - 1)
                    if (lvTrip.bottom == lastItemView.bottom) {
                        if (footView.refresh) {
                            pageIndex += pageCount
                            presenter.listOrder(userId, pageIndex, pageCount)
                        }
                    }
                }
            }

            override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
            }

        })

        lvTrip.setOnItemClickListener { _, view, i, _ ->
            if (view != footView) {
                val item = adapter.getItem(i) as OrderEntity
                if (item.orderStatus in OrderStatus.PUBORDER_TYPE..OrderStatus.REFUND_TYPE) {
                    OrderInfoActivity.startOrderInfoActivity(this, item.orderNo)
                } else {
                    PayOrderActivity.startPayOrderActivity(this, item.orderNo)
                }
            }
        }

        progress.show()
        presenter.listOrder(userId, pageIndex, pageCount)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == Constants.REQUEST_ORDER_INFO_ACTIVITY || requestCode == Constants.REQUEST_PAY_ORDER_ACTIVITY) && resultCode == Activity.RESULT_OK) {
            progress.show()
            adapter.clear()
            pageIndex = 0
            presenter.listOrder(userId, pageIndex, pageCount)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }

    @Subscribe
    fun loadData(listOrder: ListOrderEntity) {
        progress.dismiss()
        srlRefresh.isRefreshing = false
        if (listOrder.rspCode.equals("00")) {
            var waitTakeOrderList: MutableList<OrderEntity> = ArrayList()
            listOrder.orders.forEach { value -> if (value.orderStatus == OrderStatus.TAKEORDER_TYPE || value.orderStatus == OrderStatus.PAY_SUCC_TYPE) waitTakeOrderList.add(value) }
            if (tabAction == 3) {
                adapter.addAll(listOrder.waitPayOrders)
                LogUtils.d("待支付---------",listOrder.waitPayOrders.toString())
                footView.refresh = listOrder.waitPayOrders.size >= pageCount
            }
            if (tabAction == 2) {
                adapter.addAll(waitTakeOrderList)
                LogUtils.d("未出行---------",waitTakeOrderList.toString())
                footView.refresh = waitTakeOrderList.size >= pageCount
            }
            if (tabAction == 1) {
                adapter.addAll(listOrder.orders)
                footView.refresh = listOrder.orders.size >= pageCount
            }
        } else {
            ShowToast.showCenter(this, listOrder.rspDesc)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        tabAction = tab!!.position + 1
        progress.show()
        adapter.clear()
        pageIndex = 0
        presenter.listOrder(userId, pageIndex, pageCount)
    }
}
