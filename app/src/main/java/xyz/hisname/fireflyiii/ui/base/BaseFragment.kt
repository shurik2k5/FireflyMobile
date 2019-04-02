package xyz.hisname.fireflyiii.ui.base

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import xyz.hisname.fireflyiii.R
import xyz.hisname.fireflyiii.data.local.pref.AppPref
import xyz.hisname.fireflyiii.repository.GlobalViewModel
import xyz.hisname.fireflyiii.repository.MapsViewModel
import xyz.hisname.fireflyiii.repository.account.AccountsViewModel
import xyz.hisname.fireflyiii.repository.bills.BillsViewModel
import xyz.hisname.fireflyiii.repository.budget.BudgetViewModel
import xyz.hisname.fireflyiii.repository.category.CategoryViewModel
import xyz.hisname.fireflyiii.repository.currency.CurrencyViewModel
import xyz.hisname.fireflyiii.repository.piggybank.PiggyViewModel
import xyz.hisname.fireflyiii.repository.tags.TagsViewModel
import xyz.hisname.fireflyiii.repository.transaction.TransactionsViewModel
import xyz.hisname.fireflyiii.repository.userinfo.UserInfoViewModel
import xyz.hisname.fireflyiii.util.animation.CircularReveal
import xyz.hisname.fireflyiii.util.extension.bindView
import xyz.hisname.fireflyiii.util.extension.getViewModel
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment: Fragment() {

    protected val progressLayout by bindView<View>(R.id.progress_overlay)
    protected val fab by bindView<FloatingActionButton>(R.id.globalFAB)
    protected val fragmentContainer by bindView<FrameLayout>(R.id.fragment_container)
    protected val revealX by lazy { arguments?.getInt("revealX") ?: 0 }
    protected val revealY by lazy { arguments?.getInt("revealY") ?: 0 }
    protected val billViewModel: BillsViewModel by lazy { getViewModel(BillsViewModel::class.java) }
    protected val currencyViewModel by lazy { getViewModel(CurrencyViewModel::class.java) }
    protected val categoryViewModel by lazy { getViewModel(CategoryViewModel::class.java) }
    protected val accountViewModel by lazy { getViewModel(AccountsViewModel::class.java) }
    protected val piggyViewModel by lazy { getViewModel(PiggyViewModel::class.java) }
    protected val budgetViewModel by lazy { getViewModel(BudgetViewModel::class.java) }
    protected val tagsViewModel by lazy { getViewModel(TagsViewModel::class.java) }
    protected val transactionViewModel by lazy { getViewModel(TransactionsViewModel::class.java) }
    protected val mapsViewModel by lazy { getViewModel(MapsViewModel::class.java) }
    protected val userApiVersion by lazy { getViewModel(UserInfoViewModel::class.java).userApiVersion() }
    private val globalViewModel by lazy { getViewModel(GlobalViewModel::class.java) }
    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().findViewById<AppBarLayout>(R.id.activity_appbar)?.setExpanded(true,true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleBackPress()
    }

    // Taken from: https://proandroiddev.com/enter-animation-using-recyclerview-and-layoutanimation-part-1-list-75a874a5d213
    fun runLayoutAnimation(recyclerView: RecyclerView){
        val controller = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            layoutAnimation = controller
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    protected fun showReveal(rootLayout: View) = CircularReveal(rootLayout).showReveal(revealX, revealY)

    protected fun isDarkMode(): Boolean{
        return AppPref(PreferenceManager.getDefaultSharedPreferences(requireContext())).nightModeEnabled
    }

    private fun handleBackPress() {
        globalViewModel.backPress.observe(this, Observer { backPressValue ->
            if(backPressValue == true) {
                scope.launch(Dispatchers.Main) {
                    handleBack()
                }.invokeOnCompletion {
                    globalViewModel.backPress.value = false
                }
            }
        })
    }
    abstract fun handleBack()
}