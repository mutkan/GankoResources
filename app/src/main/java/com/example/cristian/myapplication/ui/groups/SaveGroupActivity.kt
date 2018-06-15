package com.example.cristian.myapplication.ui.groups

import android.app.Activity
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Group
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.*
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_save_group.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import javax.inject.Inject


class SaveGroupActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: GroupViewModel by lazy { buildViewModel<GroupViewModel>(factory) }

    var isAdd = false
    var group: Group? = null
    var bovines: List<String> = emptyList()
    val dis: LifeDisposable = LifeDisposable(this)
    var color: Int = 0
    val colorDialog: AlertDialog by lazy {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Selecciona el color")
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(12)
                .setOnColorSelectedListener {
                    color = it
                    colorPicker.setCardBackgroundColor(color)
                    colorDialog.dismiss()
                }
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_group)
        fixColor(12)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        group = intent.extras.getParcelable(EXTRAS_GROUP)
        isAdd = group == null
        bovines = if (isAdd) intent.extras.getStringArray(EXTRAS_BOVINES).toList()
        else group?.bovines!!

        title = getString(if(isAdd) R.string.group_add else R.string.group_update)


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        number.text = "${bovines.size}"
        color = if (isAdd) ContextCompat.getColor(this, R.color.colorPicker) else  group?.color!!

        name.setText(group?.nombre ?: "")

        dis add selection.clicks()
                .subscribe { startActivityForResult<BovineSelectedActivity>(101, BovineSelectedActivity.EXTRA_SELECTED to bovines) }

        dis add btnCancel.clicks()
                .subscribe { finish() }

        dis add colorPicker.clicks()
                .subscribe { colorDialog.show() }

        dis add btnSave.clicks()
                .flatMap { validateForm(R.string.group_form_error, name.text(), "$color") }
                .flatMapSingle { if (isAdd) addGroup(it[0]) else updateGroup(it[0]) }
                .subscribe {
                    setResult(Activity.RESULT_OK)
                    finish()
                }




    }

    fun addGroup(name: String): Single<Unit> = Single.just(Group(null, null, null, viewModel.farmId(), name, color, bovines))
            .flatMap { viewModel.add(it) }
            .doOnSuccess { toast(R.string.group_added) }

    fun updateGroup(name: String): Single<Unit> {
        group?.nombre = name
        group?.color = color
        group?.bovines = bovines
        return Single.just(group!!)
                .flatMap { viewModel.update(it._id!!, it) }
                .doOnSuccess { toast(R.string.group_updated) }
    }


    companion object {
        const val EXTRAS_BOVINES = "dataBovines"
        const val EXTRAS_GROUP = "dataGroup"
    }
}
