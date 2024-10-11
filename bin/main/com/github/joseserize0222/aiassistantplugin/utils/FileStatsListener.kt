package com.github.joseserize0222.aiassistantplugin.utils

interface FileStatsListener {
    fun callback(allStats: KotlinFileStats) {}
}