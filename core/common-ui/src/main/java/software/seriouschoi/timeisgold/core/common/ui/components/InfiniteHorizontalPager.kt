package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun InfiniteHorizontalPager(
    pageList: List<Any>,
    initialPageIndex: Int = 0,
    onSelectPage: (Int) -> Unit = {},
    contentPage: @Composable (Int) -> Unit,
) {
    if (pageList.isEmpty()) return

    val pageCount = Int.MAX_VALUE
    val initialPage = remember(pageList) {
        val size = pageList.size
        if (size == 0) 0
        else pageCount / 2 - (pageCount / 2) % size + initialPageIndex
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pageCount }, //무한.
        initialPageOffsetFraction = 0f
    )

    LaunchedEffect(pagerState.currentPage) {
        onSelectPage(pagerState.currentPage % pageList.size)
    }
    HorizontalPager(
        state = pagerState
    ) { page: Int ->
        val pageIndex = page % pageList.size
        contentPage(pageIndex)

    }
}