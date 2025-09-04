package software.seriouschoi.timeisgold.core.common.ui.components

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun InfiniteHorizontalPager(
    pageList: List<Any>,
    contentPage: @Composable (Int) -> Unit,
) {
    if (pageList.isEmpty()) return

    val pageCount = Int.MAX_VALUE
    val startPage = remember(pageList) {
        val size = pageList.size
        if (size == 0) 0
        else pageCount / 2 - (pageCount / 2) % size
    }

    val pagerState = rememberPagerState(
        initialPage = startPage,
        pageCount = { pageCount }, //무한.
        initialPageOffsetFraction = 0f
    )
    HorizontalPager(
        state = pagerState
    ) { page: Int ->
        val pageIndex = page % pageList.size
        contentPage(pageIndex)

    }
}