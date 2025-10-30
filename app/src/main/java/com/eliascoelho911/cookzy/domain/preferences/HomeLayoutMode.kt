package com.eliascoelho911.cookzy.domain.preferences

enum class HomeLayoutMode(val storageValue: String) {
    List("list"),
    Grid("grid");

    companion object {
        fun fromStorage(value: String?): HomeLayoutMode =
            values().firstOrNull { it.storageValue == value } ?: List
    }
}
