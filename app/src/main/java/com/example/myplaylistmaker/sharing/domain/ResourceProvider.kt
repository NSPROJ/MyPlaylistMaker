package com.example.myplaylistmaker.sharing.domain

interface ResourceProvider {
    fun getString(resId: Int): String
}