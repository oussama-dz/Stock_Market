package com.learning.stockmarket.di

import com.learning.stockmarket.data.csv.CSVParser
import com.learning.stockmarket.data.csv.CompanyListingsParser
import com.learning.stockmarket.data.csv.IntradayInfoParser
import com.learning.stockmarket.data.repository.StockRepositoryImpl
import com.learning.stockmarket.domain.model.CompanyListing
import com.learning.stockmarket.domain.model.IntradayInfo
import com.learning.stockmarket.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCompanyListingsParser(
        companyListingsParser: CompanyListingsParser
    ): CSVParser<CompanyListing>

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindIntradayInfoParser(
        intradayInfoParser: IntradayInfoParser
    ): CSVParser<IntradayInfo>
}