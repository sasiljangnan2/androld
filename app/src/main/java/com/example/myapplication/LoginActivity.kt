package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
@OptIn(ExperimentalComposeUiApi::class)
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // MainActivity 에서 보낸 인텐트에 포함된 Extra 데이터 받기
        val initialMsg: String = intent?.getStringExtra("UserDefinedExtra") ?: ""

        setContent {
            MyApplicationTheme(){
                var text by remember { mutableStateOf(initialMsg) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .semantics { testTagsAsResourceId = true },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier.fillMaxWidth().testTag("userField")
                    )
                    Button(
                        onClick = {
                            val resultIntent = Intent().apply {
                                putExtra("ResultString", text)
                            }
                            setResult(android.app.Activity.RESULT_OK, resultIntent)
                            finish()
                        },
                        modifier = Modifier.testTag("login")
                    ) {
                        Text("login")
                    }
                }

                BackHandler {
                    val resultIntent = Intent().apply {
                        putExtra("ResultString", text)
                    }
                    // 백 버튼을 누른 경우 RESULT_CANCELED
                    setResult(android.app.Activity.RESULT_CANCELED, resultIntent)
                    finish()
                }
            }
        }
    }
}