package ru.teamdroid.colibripost.data.account.auth

interface AuthRemote {
    suspend fun insertCode(code: String): Boolean
    suspend fun insertPhoneNumber(phone: String): String
}