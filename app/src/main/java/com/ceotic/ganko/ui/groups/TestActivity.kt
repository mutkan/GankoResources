package com.ceotic.ganko.ui.groups

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.util.LifeDisposable
import org.jetbrains.anko.startActivityForResult

class TestActivity : AppCompatActivity() {

    var groupFragment: GroupFragment? = null
    var group: Group? = null
    var bovines: List<String>? = null
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        startActivityForResult<SelectActivity>(SelectActivity.REQUEST_SELECT,
                SelectActivity.EXTRA_COLOR to 12)

    }

    override fun onResume() {
        super.onResume()
        setupGroupFragment()
    }

    fun setupGroupFragment(){
        if ((group != null || bovines != null) && groupFragment == null) {
            groupFragment = if (group != null) GroupFragment.instance(12, group!!)
            else GroupFragment.instance(12, bovines!!)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, groupFragment)
                    .commit()

            dis add groupFragment!!.ids
                    .filter { group == null }
                    .subscribe { bovines = it }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SelectActivity.REQUEST_SELECT) {
            if(resultCode == Activity.RESULT_OK){
                group = data?.extras?.getParcelable(SelectActivity.DATA_GROUP)
                bovines = data?.extras?.getStringArray(SelectActivity.DATA_BOVINES)?.toList()
            }else{
                finish()
            }
        }
    }


}
