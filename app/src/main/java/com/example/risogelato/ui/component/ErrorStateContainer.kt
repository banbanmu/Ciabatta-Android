package com.example.risogelato.ui.component

interface ErrorStateContainer {
    var isErrorState: Boolean
    fun onCreateDrawableState(extraSpace: Int): IntArray
}
