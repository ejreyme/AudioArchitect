package com.joonyor.labs.audioarchitect

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform