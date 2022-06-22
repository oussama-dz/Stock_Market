package com.learning.stockmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.learning.stockmarket.presentation.company_info.CompanyInfoScreen
import com.learning.stockmarket.presentation.company_info.TestComp
import com.learning.stockmarket.presentation.company_listings.CompanyListingsScreen
import com.learning.stockmarket.ui.theme.StockMarketTheme
import com.learning.stockmarket.util.COMPANY_INFO_SCREEN
import com.learning.stockmarket.util.COMPANY_LISTING_SCREEN
import com.learning.stockmarket.util.TEST_SCREEN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockMarketTheme {
                //DestinationsNavHost(navGraph = NavGraphs.root)
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = COMPANY_LISTING_SCREEN){
                    composable(COMPANY_LISTING_SCREEN){
                        CompanyListingsScreen(navController = navController)
                    }

                    composable(
                        route = COMPANY_INFO_SCREEN + "/{symbol}",
                        arguments = listOf(
                            navArgument("symbol"){
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ){ entry ->
                        CompanyInfoScreen(navController = navController, symbol = entry.arguments?.getString("symbol"))
                    }
                }
            }
        }
    }
}
