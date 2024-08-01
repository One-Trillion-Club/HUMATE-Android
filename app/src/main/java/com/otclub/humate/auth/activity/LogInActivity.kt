package com.otclub.humate.auth.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.otclub.humate.R
import com.otclub.humate.auth.api.AuthService
import com.otclub.humate.auth.data.LogInRequestDTO
import com.otclub.humate.auth.data.LogInResponseDTO
import com.otclub.humate.auth.viewmodel.AuthViewModel
import com.otclub.humate.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback

class LogInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: AuthViewModel

    private lateinit var idInputEditText: EditText
    private lateinit var passwordInputEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        idInputEditText = findViewById(R.id.inputLoginId)
        passwordInputEditText = findViewById(R.id.inputPassword)
        signInButton = findViewById(R.id.signInButton)
        signUpText = findViewById(R.id.signUpText)

        signInButton.setOnClickListener(this)
        signUpText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.signInButton -> {
                val loginId = idInputEditText.text.toString()
                val password = passwordInputEditText.text.toString()
                Log.i("sign in btn click", "loginId:${loginId}, password:${password}")
                viewModel.fetchLogIn(
                    LogInRequestDTO(loginId, password),
                    onSuccess = {response ->
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    },
                    onError = {error ->
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                    })

            }
            R.id.signUpText -> { Log.i("test", "sign up text clicked") }
//            else -> { Log.i("test", "다른데 클릭") }
        }

    }

    private fun logIn(loginId: String, password: String) {
        val retrofitAPI = RetrofitConnection.getInstance().create(AuthService::class.java)

        retrofitAPI.logIn(LogInRequestDTO(loginId, password))
            .enqueue(object: Callback<LogInResponseDTO> {
                override fun onResponse(call: Call<LogInResponseDTO>, response: retrofit2.Response<LogInResponseDTO>) {
                    val logInResponse = response.body()
                    if (response.isSuccessful) {
                        // 서버 응답 성공
                        val logInResponse = response.body()
                        Log.i("sign in btn click success", logInResponse.toString())
                    } else {
                        // 서버 응답 오류 처리
                        Log.i("login", "아이디나 비번 틀림")
                    }
                }
                override fun onFailure(call: Call<LogInResponseDTO>, t: Throwable) {
                    // 네트워크 오류 등 실패 처리
                    Log.i("(onfailure) sign in btn click error", t.toString())
                }
            })

    }
}