package com.example.logan.promdate

class BadTokenException : Exception("Failed to authenticate token")

class MissingSpException : Exception("SharedPreferences not found")