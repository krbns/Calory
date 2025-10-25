package com.kurban.calory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform