package com.sealedstack.config

import org.jetbrains.exposed.v1.jdbc.Database

object AppConfiguration {
    const val APP_NAME = "Audio Architect"
    const val LIBRARY_ROOT_PATH = "/Users/ejreyme/Music/yME"
    const val LIBRARY_PLAYLIST_EXPORT_PATH = "/Users/ejreyme/Music/yME/export"
}

object DbSettings {
    val h2Db by lazy {
        Database.connect("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }
}