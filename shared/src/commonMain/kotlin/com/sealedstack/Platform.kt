package com.sealedstack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform