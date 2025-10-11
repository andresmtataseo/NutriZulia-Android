package com.nutrizulia.data.remote.api

import java.io.IOException

class ApiHttpException(val statusCode: Int, message: String) : IOException(message)