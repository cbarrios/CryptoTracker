package com.plcoding.cryptotracker.crypto.ui.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.ui.models.toCoinUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoinListState())
    val uiState = _uiState
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CoinListState()
        )

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClick -> {
                // TODO
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    val uiCoins = withContext(Dispatchers.Default) {
                        coins.map { it.toCoinUi() }
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            coins = uiCoins
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }
}