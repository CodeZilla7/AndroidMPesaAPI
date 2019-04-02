package com.androidstudy.mpesa.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast
import com.androidstudy.mpesa.R
import com.androidstudy.mpesa.common.BaseActivity
import com.androidstudy.mpesa.common.Status
import com.androidstudy.mpesa.utils.AppUtils
import com.androidstudy.mpesa.viewmodel.PaymentViewModel

import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.content_payment.*
import java.util.*

class PaymentActivity : BaseActivity() {

    lateinit var viewModel: PaymentViewModel
    val TAG = this::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setSupportActionBar(toolbar)

        title = "Payment"

        viewModel = getViewModel(PaymentViewModel::class.java)

        accessToken()
        bPay.setOnClickListener{pay()}

    }

    private fun pay() {
        val phoneNumber= etPhoneNumber.text.toString()
        val amountString = etAmount.text.toString()

        if (phoneNumber.isEmpty() && amountString.isEmpty()){
            toast("You have left some fields blank")
            return
        }

        val amount =amountString.toInt()
        sendPaymentRequest(phoneNumber, amount)
    }

    private fun sendPaymentRequest(phoneNumber: String, amount: Int) {
        val token = AppUtils.getAccessToken(baseContext)
        viewModel.pay(token, phoneNumber, amount, "Payment").observe(this, android.arch.lifecycle.Observer { response ->
            when (response!!.status()) {
                Status.LOADING -> {
                    toast("Loading")
                }

                Status.SUCCESS -> {
                    val lnm = response.data()!!
                    toast("Success : " + lnm.ResponseDescription)
                }

                Status.ERROR -> {
                    toast("error" + response.error()!!.message)
                }
            }

        })
    }

    private fun toast(text: String) {
        Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
    }

    private fun accessToken() {
        viewModel.accessToken().observe(this, android.arch.lifecycle.Observer { response ->
            when (response!!.status()) {
                Status.LOADING -> {
                }

                Status.SUCCESS -> {
                    val token = response.data()!!
                   AppUtils.saveAccessToken(baseContext, token.access_token)
                }

                Status.ERROR -> {
                    toast("error" + response.error()!!.message)
                }
            }

        })
    }


}
