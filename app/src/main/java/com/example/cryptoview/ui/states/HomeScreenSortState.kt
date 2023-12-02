package com.example.cryptoview.ui.states

data class HomeScreenSortState(
    val isSortByName: SortState = SortState.NONE,
    val isSortByPrice: SortState = SortState.NONE
){
    companion object {
        inline fun sortByState(
            vararg sortState: SortState,
            crossinline actionNone: () -> Unit,
            crossinline actionUp: () -> Unit,
            crossinline actionDown: () -> Unit
        ) {
            sortState.forEach { action ->
                when (action) {
                    SortState.NONE -> actionNone()
                    SortState.UP -> actionUp()
                    SortState.DOWN -> actionDown()
                }
            }
        }
    }

    enum class SortState {
        NONE,
        UP,
        DOWN
    }

    enum class SortBy(var sortState: SortState) {
        NAME(SortState.NONE),
        PRICE(SortState.NONE)
    }
}


