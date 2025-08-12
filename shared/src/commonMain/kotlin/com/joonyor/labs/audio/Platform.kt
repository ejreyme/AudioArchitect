package com.joonyor.labs.audio

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform