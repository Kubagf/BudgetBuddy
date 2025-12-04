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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.pwr.budgetbuddy.ui.home.HomeScreen
import pl.edu.pwr.budgetbuddy.ui.theme.BudgetBuddyTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val pagerState = rememberPagerState(pageCount = { 5 })
            BudgetBuddyTheme {
                Scaffold(
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
//                                1 -> StatsScreen()
//                                2 -> ReceiptScreen()
//                                3 -> ListScreen()
//                                4 -> SettingsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}