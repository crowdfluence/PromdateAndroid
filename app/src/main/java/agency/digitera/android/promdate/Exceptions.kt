package agency.digitera.android.promdate

class BadTokenException : Exception("Failed to authenticate token")

class MissingSpException : Exception("SharedPreferences not found")

class CancelRequestException : Exception("The user cancelled the request")