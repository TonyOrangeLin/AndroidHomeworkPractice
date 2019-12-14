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

    private lateinit var loginViewModel: LoginViewModel

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

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

//        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
//            val loginResult = it ?: return@Observer
//
//            loading.visibility = View.GONE
//            if (loginResult.error != null) {
//                showLoginFailed(loginResult.error)
//            }
//            if (loginResult.success != null) {
//                updateUiWithUser(loginResult.success)
//            }
//            setResult(Activity.RESULT_OK)
//
//            //Complete and destroy login activity once successful
//            finish()
//        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                beginLogin(username.text.toString(),password.text.toString())
                //loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

//    private fun updateUiWithUser(model: LoggedInUserView) {
//        val welcome = getString(R.string.welcome)
//        val displayName = model.displayName
//        // TODO : initiate successful logged in experience
//        Toast.makeText(
//            applicationContext,
//            "$welcome $displayName",
//            Toast.LENGTH_LONG
//        ).show()
//
//        //val editText = findViewById<EditText>(R.id.editText)
//        //val message = editText.text.toString()
//        val intent = Intent(this, UpdateZoneActivity::class.java).apply {
//            putExtra(EXTRA_MESSAGE, "1234")
//        }
//        startActivity(intent)
//    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
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

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
