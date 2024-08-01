package com.cevt.pmh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cevt.pmh.ui.theme.PropertyManagerHelperTestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val subscribedProperty = viewModel.subscribedProperty.collectAsState(-1)

            var stringPropertyId by remember { mutableStateOf("") }
            var integerPropertyId by remember { mutableStateOf("") }

            val ignition = viewModel.ignition.collectAsState()

            PropertyManagerHelperTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.DarkGray
                ) {
                    Row(modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.Center) {
                        Column(modifier = Modifier
                            .fillMaxWidth(0.4f),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { viewModel.getDeviceProjectCode() }) {
                                Text(text = "Get Device Project Code")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Device Project Code", color = Color.White)
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Text(viewModel.deviceProjectCode, modifier = Modifier.background(Color.Green))
                            }

                            Spacer(modifier = Modifier.height(36.dp))
                            Text(text = "Subscribed VIN value", color = Color.White)
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Text(subscribedProperty.value.toString(), color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(36.dp))
                            Text(text = "Ignition value", color = Color.White)
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Text(ignition.toString(), color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(36.dp))
                            OutlinedTextField(
                                value = stringPropertyId,
                                onValueChange = { stringPropertyId = it },
                                label = { Text("stringPropertyId") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                            Button(onClick = { viewModel.getStringProperty(stringPropertyId.toIntOrNull()) }) {
                                Text(text = "Get String Property")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "String Property Value", color = Color.White)
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Text(viewModel.stringProperty, modifier = Modifier.background(Color.Green))
                            }
                            Spacer(modifier = Modifier.height(36.dp))
                            OutlinedTextField(
                                value = integerPropertyId,
                                onValueChange = { integerPropertyId = it },
                                label = { Text("stringPropertyId") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                            Button(onClick = { viewModel.getIntegerProperty(integerPropertyId.toIntOrNull()) }) {
                                Text(text = "Get Integer Property")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Integer Property Value", color = Color.White)
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .border(2.dp, Color.Green), contentAlignment = Alignment.Center) {
                                Text(viewModel.integerProperty, modifier = Modifier.background(Color.Green))
                            }
                        }
                    }
                }
            }
        }
    }
}
