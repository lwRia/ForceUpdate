package com.example.appupdate


interface UpdateCallBack {
    fun onSuccess(response: String?)
    fun onFailure(message: String?)
}