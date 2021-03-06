package xyz.hisname.fireflyiii.repository

import android.accounts.AccountManager
import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Retrofit
import xyz.hisname.fireflyiii.data.local.account.AuthenticatorManager
import xyz.hisname.fireflyiii.data.local.pref.AppPref
import xyz.hisname.fireflyiii.data.remote.RetrofitBuilder
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(application: Application) : AndroidViewModel(application){

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    val scope = CoroutineScope(coroutineContext)
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val apiResponse: MutableLiveData<String> = MutableLiveData()
    protected val accManager by lazy { AuthenticatorManager(AccountManager.get(getApplication()))  }
    protected val sharedPref by lazy { PreferenceManager.getDefaultSharedPreferences(getApplication()) }

    protected fun genericService(): Retrofit? {
        var cert = ""
        if(AppPref(sharedPref).enableCertPinning){
            cert = AppPref(sharedPref).certValue
        }
        return RetrofitBuilder.getClient(AppPref(sharedPref).baseUrl,
                accManager.accessToken, cert)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}