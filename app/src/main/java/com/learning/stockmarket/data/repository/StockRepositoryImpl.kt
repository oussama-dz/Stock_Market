package com.learning.stockmarket.data.repository

import com.learning.stockmarket.data.csv.CSVParser
import com.learning.stockmarket.data.local.StockDatabase
import com.learning.stockmarket.data.mapper.toCompanyInfo
import com.learning.stockmarket.data.mapper.toCompanyListing
import com.learning.stockmarket.data.mapper.toCompanyListingEntity
import com.learning.stockmarket.data.remote.StockApi
import com.learning.stockmarket.domain.model.CompanyInfo
import com.learning.stockmarket.domain.model.CompanyListing
import com.learning.stockmarket.domain.model.IntradayInfo
import com.learning.stockmarket.domain.repository.StockRepository
import com.learning.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingsParser: CSVParser<CompanyListing>,
    private val intrdayInfoParser: CSVParser<IntradayInfo>

): StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListings(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if(shouldJustLoadFromCache){
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListing = try {

                val response = api.getListings()
                companyListingsParser.parse(response.byteStream())

            } catch (e: IOException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            } catch (e: HttpException){
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
                null
            }

            remoteListing?.let { listings->
                dao.clearCompanyListings()
                dao.insertCompanyListings(listings.map { it.toCompanyListingEntity() })
                emit(Resource.Success(
                    data = dao.
                    searchCompanyListings("")
                        .map { it.toCompanyListing() }
                ))
                emit(Resource.Loading<List<CompanyListing>>(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {

        return try {

            val response = api.getIntradayInfo(symbol)
            val result = intrdayInfoParser.parse(response.byteStream())
            Resource.Success(result)
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")

        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load intraday info")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {

            val result = api.getCompanyInfo(symbol)
            Resource.Success(result.toCompanyInfo())
        }catch (e: IOException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")

        }catch (e: HttpException){
            e.printStackTrace()
            Resource.Error("Couldn't load company info")
        }
    }


}