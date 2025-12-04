import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import pl.edu.pwr.budgetbuddy.R

@Composable
fun NavBar(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    val items = listOf("Home", "Stats", "Add receipt", "List", "Settings")
    val selectedIcons = listOf(
        R.drawable.home_filled,
        R.drawable.home_filled,
        R.drawable.home_filled,
        R.drawable.home_filled,
        R.drawable.home_filled
    )
    val unselectedIcons = listOf(
        R.drawable.home, R.drawable.home, R.drawable.home, R.drawable.home, R.drawable.home
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(icon = {
                if (pagerState.currentPage == index) {
                    Icon(
                        painter = painterResource(id = selectedIcons[index]),
                        contentDescription = item
                    )
                } else {
                    Icon(
                        painter = painterResource(id = unselectedIcons[index]),
                        contentDescription = item
                    )
                }
            }, label = { Text(item) }, selected = pagerState.currentPage == index, onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavBarPreview() {
    NavBar(pagerState = rememberPagerState(pageCount = { 5 }))
}
