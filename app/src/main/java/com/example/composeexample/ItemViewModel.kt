package com.example.composeexample

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.Serializable

data class ProgrLang(val name: String, val year: Int, var picture: Int = R.drawable.no_picture): Serializable

class ItemViewModel : ViewModel() {

    private var langList = mutableStateListOf(
        ProgrLang("Basic", 1964, R.drawable.basic),
        ProgrLang("Pascal", 1975, R.drawable.pascal),
        ProgrLang("C", 1972, R.drawable.c),
        ProgrLang("C++", 1983, R.drawable.cpp),
        ProgrLang("C#", 2000, R.drawable.c_sharp),
        ProgrLang("Java", 1995, R.drawable.java),
        ProgrLang("Python", 1991, R.drawable.python),
        ProgrLang("JavaScript", 1995),
        ProgrLang("Kotlin", 2011)
    )


    private val _langListFlow = MutableStateFlow(langList)

    val langListFlow: StateFlow<List<ProgrLang>> get() = _langListFlow

    fun clearList() {
        langList.clear()
    }

    fun addLangToHead(lang: ProgrLang) {
        langList.add(0, lang)
    }

    fun addLangToEnd(lang: ProgrLang) {
        langList.add(lang)
    }

    fun removeItem(item: ProgrLang) {
        val index = langList.indexOf(item)
        langList.remove(langList[index])
    }
}