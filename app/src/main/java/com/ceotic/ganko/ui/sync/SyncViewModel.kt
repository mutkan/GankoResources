package com.ceotic.ganko.ui.sync

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.applySchedulers
import com.couchbase.lite.*
import io.reactivex.Observable
import java.net.URI
import javax.inject.Inject

class SyncViewModel @Inject constructor(private val db:Database,
                                        private val session:UserSession) : ViewModel() {

    private var replicator: Replicator? = null

    fun validatePlan():Boolean = session.validatePlanDate()
            .first

    fun replicate(url:String): Observable<Int> = Observable.create<Int> { emitter->
        val config = ReplicatorConfiguration(db, URLEndpoint(URI(url)))
        config.replicatorType = ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL

        val channels = mutableListOf("account_${session.userId}", session.userId!!)
        config.channels = channels
        replicator = Replicator(config)
        replicator?.start()

        var token: ListenerToken? = null

        fun stopReplicatior() {
            if (token != null) replicator?.removeChangeListener(token)
            replicator?.stop()
            replicator = null
        }

        token = replicator?.addChangeListener {
            val error = it.status.error
            val status = it.status.activityLevel

            when {
                error != null -> {
                    emitter.onNext(ERROR)
                    stopReplicatior()
                    emitter.onComplete()
                }
                status == Replicator.ActivityLevel.BUSY -> emitter.onNext(BUSY)
                status == Replicator.ActivityLevel.OFFLINE -> {
                    emitter.onNext(OFFLINE)
                    stopReplicatior()
                    emitter.onComplete()
                }
                status == Replicator.ActivityLevel.STOPPED -> {
                    emitter.onNext(STOPPED)
                    emitter.onComplete()
                }
            }
        }

        emitter.setCancellable {
            stopReplicatior()
        }
    }.applySchedulers()

    companion object {
        const val ERROR = 0
        const val BUSY = 1
        const val OFFLINE = 2
        const val STOPPED = 3
    }

}