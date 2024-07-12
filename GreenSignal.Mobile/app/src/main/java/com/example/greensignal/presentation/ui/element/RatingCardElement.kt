package com.example.greensignal.presentation.ui.element

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greensignal.domain.model.response.Inspector
import com.example.greensignal.domain.model.response.Rating
import com.example.greensignal.presentation.ui.theme.GreenSignalTheme

@Composable
fun RatingCardElement(topThreeList: MutableList<Rating>, currentInspector: Rating) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
    ) {
        LazyColumn(modifier = Modifier
            .padding(20.dp)
        ) {
            items(topThreeList) { model ->
                RatingRow(model, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 20.dp,
            color = MaterialTheme.colorScheme.secondary
        )

        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
        {
            RatingRow(currentInspector, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun RatingRow(rating: Rating, fontWeight: FontWeight) {
    Row(modifier = Modifier
        .fillMaxWidth()) {

        Text(text = rating.place.toString() + ".",
            textAlign = TextAlign.Start,
            fontWeight = fontWeight,
            modifier = Modifier.width(30.dp))

        Spacer(modifier = Modifier.width(10.dp))

        rating.inspector?.fio?.let {
            Text(text = it,
                fontWeight = fontWeight,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start) }

        Text(text = if(rating.totalScore < 0) "0" else rating.totalScore.toString(),
            modifier = Modifier.width(50.dp),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
@Preview(name = "Rating Card",
    showBackground = false)
fun RatingCardPreview() {
    GreenSignalTheme {
        RatingCardElement(mutableListOf(Rating(place = 1, inspector = Inspector(fio = "Шибанова Валентина Сергеевна"), totalScore = 1000),
                                                Rating(place = 2, inspector = Inspector(fio = "Иванов Иван Иванович"), totalScore = 300),
                                                Rating(place = 3, inspector = Inspector(fio = "Петров Петр Петрович"), totalScore = 250)),
                            Rating(place = 24, inspector = Inspector(fio = "Константинов Константин Константинович"), totalScore = 5))
    }
}