package com.opentool

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform