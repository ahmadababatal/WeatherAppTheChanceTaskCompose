package com.example.weatherappthechancetask

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherappthechancetask.model.Weather
import com.example.weatherappthechancetask.ui.theme.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val client = OkHttpClient()
    private val searchTextState: MutableState<String> = mutableStateOf("syria")
    var weatherState by mutableStateOf(Weather())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=syria&appid=8597c19a2987ddeedaef98f1a4238194&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG:(", e.message.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    val res = Gson().fromJson(jsonString, Weather::class.java)
                    weatherState = Weather(
                        coord = res.coord,
                        weather = res.weather,
                        main = res.main,
                        visibility = res.visibility,
                        wind = res.wind,
                        clouds = res.clouds
                    )

                    Log.d("GGRRR", weatherState.toString())

                    runOnUiThread {

                        setContent {
                            WeatherAppTheChanceTaskTheme {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(ActivityBackground)
                                            .padding(13.dp)
                                    ) {

                                        SearchLogic(
                                            text = searchTextState.value,
                                            onTextChange = { newText ->
                                                searchTextState.value = newText
                                            },
                                            onSearchClicked = {
                                                searchTextState.value = ""
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(15.dp))

                                        WeatherCard(weatherState, "Syria")

                                        Spacer(modifier = Modifier.height(15.dp))

                                        DetailsWeather(weatherState)

                                        Spacer(modifier = Modifier.height(15.dp))

                                        WindAndPressureCard(weatherState)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}

@Composable
fun WeatherCard(
    weatherState: Weather,
    countyName: String
) {
    weatherState.let { weather ->
        Card(
            backgroundColor = CardBackground,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .height(360.dp),
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = countyName,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = TextColors,
                    fontSize = 24.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.suny_image),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(140.dp),
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = weather.main!!.temp,
                        color = TextColors,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "C",
                        color = UnitColors,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = weather.main!!.temp,
                    modifier = Modifier.align(Alignment.Start),
                    color = TextColors,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sun_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = weather.main.temp,
                        color = TextColors,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Image(
                        painter = painterResource(id = R.drawable.moon_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = weather.main.temp,
                        color = TextColors,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLogic(
    text: String,
    onTextChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit
) {
    Card(
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_place_holder),
                    color = TextColors
                )
            },
            textStyle = TextStyle(
                color = TextColors,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier.alpha(ContentAlpha.disabled),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_icon),
                        tint = UnitColors
                    )
                }
            }, keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = TextColors,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun DetailsWeather(
    weatherState: Weather
) {
    weatherState.let { weather ->
        Card(
            backgroundColor = CardBackground,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .height(110.dp),
            elevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Visible",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(
                        text = "Humidity",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Text(
                        text = "Clouds",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.visible_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = (weather.visibility?.toInt()!! / 100).toString(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Image(
                        painter = painterResource(id = R.drawable.water_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = weather.main?.humidity+" %",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Image(
                        painter = painterResource(id = R.drawable.cloud_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = weather.clouds?.all+" %",
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        color = TextColors,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WindAndPressureCard(
    weatherState: Weather
) {
    weatherState.let { weather ->
        Card(
            backgroundColor = CardBackground,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .height(110.dp),
            elevation = 0.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(65.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.wind_t_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(65.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Wind",
                        color = TextColors,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row{
                        Text(
                            text = weather.wind?.speed.toString(),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            color = TextColors,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "km/h",
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            color = UnitColors,
                            fontSize = 14.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Pressure",
                            color = TextColors,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            modifier = Modifier
                                .size(26.dp),
                            painter = painterResource(id = R.drawable.pressure_icon),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(35.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = weather.main?.pressure.toString(),
                            color = TextColors,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "mbar",
                            color = UnitColors,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//
////    WeatherCard("Syria")
////    DetailsWeather()
//
//    WindAndPressureCard()
//
//}


