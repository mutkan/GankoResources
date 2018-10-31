package com.ceotic.ganko.ui.menu

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.couchbase.lite.Expression
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.FragmentFilterBinding
import com.ceotic.ganko.util.*
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_filter.*
import java.util.*

class FilterFragment : Fragment() {

    lateinit var binding: FragmentFilterBinding
    val dis: LifeDisposable = LifeDisposable(this)
    val filterState: Filter = Filter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onResume() {
        super.onResume()

        dis add btnClear.clicks()
                .subscribe {
                    checkMilk.isChecked = false
                    checkMeat.isChecked = false
                    checkBoth.isChecked = false
                    checkStateZeal.isChecked = false
                    checkService.isChecked = false
                    checkDiagnosis.isChecked = false
                    checkWeaning.isChecked = false
                    checkNoWeaning.isChecked = false
                    checkRetired.isChecked = false
                    filter.onNext(filterState.clear())
                }

        val milk = checkMilk.clicks()
                .doOnNext { filterState.milk = checkMilk.isChecked }

        val meat = checkMeat.clicks()
                .doOnNext { filterState.meat = checkMeat.isChecked }

        val both = checkBoth.clicks()
                .doOnNext { filterState.both = checkBoth.isChecked }

        val zeal = checkStateZeal.clicks()
                .doOnNext { filterState.zeal = checkStateZeal.isChecked }

        val service = checkService.clicks()
                .doOnNext { filterState.service = checkService.isChecked }

        val diagnosis = checkDiagnosis.clicks()
                .doOnNext { filterState.diagnosis = checkDiagnosis.isChecked }

        val weaning = checkWeaning.clicks()
                .doOnNext {
                    filterState.destete = checkWeaning.isChecked
                    if (filterState.destete) checkNoWeaning.isChecked = false
                }

        val noWeaning = checkNoWeaning.clicks()
                .doOnNext {
                    filterState.noDestete = checkNoWeaning.isChecked
                    if (filterState.noDestete) checkWeaning.isChecked = false
                }

        val retired = checkRetired.clicks()
                .doOnNext {
                    filterState.retired = checkRetired.isChecked
                    disableChecks(!filterState.retired)
                }


        dis add Observable.mergeArray(milk, meat, both, zeal, service, diagnosis, weaning, noWeaning, retired)
                .subscribe {
                    filter.onNext(filterState)
                }


    }

    fun disableChecks(state: Boolean) {
        checkMilk.isEnabled = state
        checkMeat.isEnabled = state
        checkBoth.isEnabled = state
        checkStateZeal.isEnabled = state
        checkService.isEnabled = state
        checkDiagnosis.isEnabled = state
        checkWeaning.isEnabled = state
        checkNoWeaning.isEnabled = state
    }

    companion object {
        val filter: PublishSubject<Filter> = PublishSubject.create()
    }

}

class Filter {

    var milk: Boolean = false
    var meat: Boolean = false
    var both: Boolean = false
    var zeal: Boolean = false
    var service: Boolean = false
    var diagnosis: Boolean = false
    var destete: Boolean = false
    var noDestete: Boolean = false
    var retired: Boolean = false

    fun makeExp(origin:Expression):Expression {
        if(retired){
            return origin andEx ("retirado" equalEx true)
        }else{
            var exp:Expression = origin
            if(milk || meat || both){
                val purpose = mutableListOf<String>()
                if(milk) purpose.add("Lecheria")
                if(meat) purpose.add("Ceba")
                if(both) purpose.add("Ambos")
                exp = exp andEx ("proposito" inEx  purpose)
            }

            if(zeal){
                val time = Date().time - 10_800_000
                exp = exp andEx ("celo[0]" gte  Date(time))
            }
            if(destete) exp = exp andEx ("destete" equalEx true)
            if(noDestete) exp = exp andEx ("destete" equalEx  false)
            if(service) exp = exp andEx  ("servicios[0].finalizado" equalEx  false)
            if(diagnosis){
                if(!service) exp = exp andEx  ("servicios[0].finalizado" equalEx  false)
                exp = exp andEx  Expression.property("servicios[0].diagnostico").notNullOrMissing()
            }

            return exp
        }
    }

    fun clear(): Filter {
        milk = false
        meat = false
        both = false
        zeal = false
        service = false
        diagnosis = false
        destete = false
        noDestete = false
        retired = false
        return this
    }

}