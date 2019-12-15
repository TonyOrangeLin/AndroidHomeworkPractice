package com.example.myapplication.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.myapplication.R
import com.example.myapplication.SquareService
import com.example.myapplication.UpdateZoneActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

const val OBJECT_ID = "com.example.myapplication.OBJECT_ID"
const val TOKEN = "com.example.myapplication.TOKEN"
class LoginActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    private lateinit var loading: ProgressBar

    private val sqareApiServe by lazy {
        SquareService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        loading = findViewById<ProgressBar>(R.id.loading)

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                beginLogin(username.text.toString(),password.text.toString())
            }

    }

    private fun beginLogin(userString: String, password: String) {
        disposable = sqareApiServe.loginCheck(userString, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {  result -> Log.e("Tony", result.sessionToken)
                    Toast.makeText(this, "Welcome" + userString.toString(), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, UpdateZoneActivity::class.java).apply {
                        putExtra(OBJECT_ID, result.objectId)
                        putExtra(TOKEN, result.sessionToken)
                    }
                    startActivity(intent)
                    loading.visibility = View.INVISIBLE
                },
                { error -> Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show()
                    Log.e("Tony", error.message)
                    loading.visibility = View.INVISIBLE
                }
            )
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}

