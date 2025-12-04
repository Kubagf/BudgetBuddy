package pl.edu.pwr.budgetbuddy

import NavBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.edu.pwr.budgetbuddy.ui.home.HomeScreen
import pl.edu.pwr.budgetbuddy.ui.receipt.NewReceiptScreen
import pl.edu.pwr.budgetbuddy.ui.receipt.ReceiptListScreen
import pl.edu.pwr.budgetbuddy.ui.stats.StatsScreen
import pl.edu.pwr.budgetbuddy.ui.theme.BudgetBuddyTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val pagerState = rememberPagerState(pageCount = { 3 })
            BudgetBuddyTheme {
                NavHost(navController = navController, startDestination = "pager") {
                    composable("pager") {
                        Pager(pagerState, navController)
                    }
                    composable("add") {
                        NewReceiptScreen(navController)
                    }
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun Pager(pagerState: PagerState, navController: NavController) {
    Scaffold(
        floatingActionButton = {
        FloatingActionButton(onClick = { navController.navigate("add") }) {
            Icon(Icons.Filled.Add, "Localized description")
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
                    0 -> HomeScreen()
                    1 -> StatsScreen()
//                                2 -> NewReceiptScreen()
                    2 -> ReceiptListScreen()
//                                4 -> SettingsScreen()
                }
            }
        }
    }
}