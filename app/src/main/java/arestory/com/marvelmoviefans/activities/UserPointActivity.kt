package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.DividerItemDecoration.VERTICAL
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.adapter.RankAdapter
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.uiview.CustomLoadMoreView
import arestory.com.marvelmoviefans.util.ToastUtil
import com.ares.datacontentlayout.DataContentLayout
import com.ares.movie.entity.UserPoint
import com.chad.library.adapter.base.BaseQuickAdapter

class UserPointActivity:BaseDataBindingActivity<arestory.com.marvelmoviefans.databinding.ActivityRankBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_rank

    override fun doMain() {

        initToolbarSetting(dataBinding.toolbar)


        getUserPointList(currentPage)


    }
    private var adapter :RankAdapter?=null
    private var currentPage:Int = 1

    private var footView:View?=null
    private var isLoadingMore = false

    private fun getUserPointList(page:Int){

        currentPage = page
        if(page==1){

            dataBinding.dataContentLayout.showLoading()
        }
        UserDataSource.getPointRank(page,object :DataCallback<List<UserPoint>>{
            override fun onSuccess(data: List<UserPoint>) {

                if(page==1){
                    dataBinding.dataContentLayout.showContent()
                    if(data.isEmpty()){

                        dataBinding.dataContentLayout.showEmptyContent()

                    }else{

                        adapter = RankAdapter(data)
                        dataBinding.adapter =adapter

                        footView = LayoutInflater.from(this@UserPointActivity).inflate(R.layout.view_footer,null)
                        adapter?.addFooterView(footView)
                        adapter?.bindToRecyclerView(dataBinding.rvUserPoint)
                        dataBinding.rvUserPoint.addItemDecoration(DividerItemDecoration(this@UserPointActivity,VERTICAL))
                        dataBinding.rvUserPoint.layoutManager = LinearLayoutManager(this@UserPointActivity)
                        dataBinding.rvUserPoint.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                super.onScrolled(recyclerView, dx, dy)
                            }

                            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                                super.onScrollStateChanged(recyclerView, newState)
                                if(newState == RecyclerView.SCROLL_STATE_IDLE){

                                    val pos =   (dataBinding.rvUserPoint.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                                    println("pos = $pos")
                                    if(pos>=adapter?.itemCount?.minus(1)!!&&!isLoadingMore){
                                        println("到达底部啦！！")
                                        isLoadingMore=true
                                        getUserPointList(currentPage+1)
                                    }
                                }
                            }
                        })
                    }
                }else{

                    adapter?.removeFooterView(footView)
                    if(data.isNotEmpty()){
                        adapter?.addData(data)
//                        adapter?.loadMoreComplete()

                        adapter?.addFooterView(footView)

                    }else{
                        ToastUtil.showShortToast(this@UserPointActivity,"已经没有更多数据了")
//                        adapter?.loadMoreEnd()

                    }

                    isLoadingMore =false


                }


            }

            override fun onFail(msg: String?) {
                if(page==1){
                    dataBinding.dataContentLayout.showError(object :DataContentLayout.ErrorListener{
                        override fun showError(view: View) {
                             getUserPointList(currentPage)
                        }
                    })
                }else{
                    isLoadingMore=false
                    adapter?.removeFooterView(footView)
                    ToastUtil.showShortToast(this@UserPointActivity,"加载更多数据失败")
                }

             }


        })

    }

    companion object {


        fun start(context:Context){

            context.startActivity(Intent(context,UserPointActivity::class.java))
        }
    }
}