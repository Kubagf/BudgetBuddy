package pl.edu.pwr.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.edu.pwr.budgetbuddy.ui.BudgetViewModel
import pl.edu.pwr.budgetbuddy.ui.NavBar
import pl.edu.pwr.budgetbuddy.ui.home.HomeScreen
import pl.edu.pwr.budgetbuddy.ui.stats.StatsScreen
import pl.edu.pwr.budgetbuddy.ui.theme.BudgetBuddyTheme
import pl.edu.pwr.budgetbuddy.ui.transaction.EditTransactionScreen
import pl.edu.pwr.budgetbuddy.ui.transaction.NewTransactionScreen
import pl.edu.pwr.budgetbuddy.ui.transaction.TransactionListScreen

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: BudgetViewModel by viewModels()

        setContent {
            val navController = rememberNavController()
            val pagerState = rememberPagerState(pageCount = { 3 })

            BudgetBuddyTheme {
                Box(Modifier.background(MaterialTheme.colorScheme.background)) {
                    NavHost(
                        navController = navController,
                        startDestination = "pager",
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up, tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down, tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up, tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down, tween(300)
                            )
                        }) {
                        composable("pager") {
                            Pager(pagerState, navController, viewModel)
                        }
                        composable("add") {
                            NewTransactionScreen(navController, viewModel)
                        }
                        composable(
                            route = "edit/{transactionId}",
                            arguments = listOf(navArgument("transactionId") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("transactionId") ?: -1
                            EditTransactionScreen(navController, viewModel, id)
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun Pager(pagerState: PagerState, navController: NavController, viewModel: BudgetViewModel) {
    Scaffold(
        floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("add") }) {
            Icon(Icons.Filled.Add, "Add new transaction")
        }
    },
        contentWindowInsets = WindowInsets(4.dp),
        modifier = Modifier.fillMaxSize(),
        bottomBar = { NavBar(pagerState) }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> HomeScreen(viewModel, navController)
                    1 -> StatsScreen()
                    2 -> TransactionListScreen(viewModel, navController)
                }
            }
        }
    }
}