package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.login.OBJECT_ID
import com.example.myapplication.ui.login.TOKEN
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdateZoneActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
    private var editTextHello: EditText? = null
    private var objectId: String = ""
    private var sessionToken: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_zone)
        editTextHello = findViewById(R.id.timeZoneEditText)

        objectId = intent.getStringExtra(OBJECT_ID)
        sessionToken = intent.getStringExtra(TOKEN)
    }

    private val sqareApiServe by lazy {
        SquareService.create()
    }

    fun ClickUpdatebutton(view: View) {
        beginUpdate(editTextHello?.text.toString(), sessionToken, objectId)
    }

    private fun beginUpdate(updateTime: String, sessionToken: String, objectId: String) {
        disposable = sqareApiServe.updatedate(objectId,updateTime, sessionToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result -> Log.e("Tony", result.updatedAt)
                    Toast.makeText(this, "update successfully", Toast.LENGTH_SHORT).show()
                },
                { error -> Log.e("Tony",  error.message)
                    Toast.makeText(this, "update fail", Toast.LENGTH_SHORT).show()
                }
            )
    }
}
