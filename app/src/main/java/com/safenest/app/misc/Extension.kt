package com.safenest.app.misc

class Extension {

    companion object{
        fun removeDecimalFromString(input: String): String {
            return input.substringBefore('.')
        }

        fun trimString(str: String): String {
            return str.trim()
        }

        fun isStringEmpty(str: String):Boolean{
            return str.isEmpty()
        }
        fun isStringNotEmpty(str: String):Boolean{
            return str.isNotEmpty()
        }

        fun compareTwoString(str1: String,  str2: String): Boolean{
            return str1 == str2
        }

        fun isEmailValid(email: String): Boolean{
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isMobileValid(mobileNumber: String): Boolean {
            val isMobile =  android.util.Patterns.PHONE.matcher(mobileNumber).matches()
            return mobileNumber.length == 10 && isMobile
        }

        fun isPasswordValid(password: String): Boolean{
            val pattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()-_=+{};:,<.>]).{8,}$")
            return password.length >= 8 && pattern.matches(password)
        }

    }
}