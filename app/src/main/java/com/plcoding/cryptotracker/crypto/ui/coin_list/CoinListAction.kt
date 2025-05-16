package com.plcoding.cryptotracker.crypto.ui.coin_list

import com.plcoding.cryptotracker.crypto.ui.models.CoinUi

sealed interface CoinListAction {
    data class OnCoinClick(val coinUi: CoinUi) : CoinListAction
}