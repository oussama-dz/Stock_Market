package com.learning.stockmarket.presentation.company_info

import com.learning.stockmarket.domain.model.CompanyInfo
import com.learning.stockmarket.domain.model.IntradayInfo

data class CompanyInfoState (
    val stockInfo: List<IntradayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
    )