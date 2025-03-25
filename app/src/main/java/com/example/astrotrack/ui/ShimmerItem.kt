package com.example.astrotrack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.ShimmerBounds
import androidx.compose.ui.draw.clip


@Composable
fun ShimmerCardItem() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shimmer(shimmer)
    ) {
        // Fake image box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Fake title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Fake date
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.LightGray)
        )
    }
}
