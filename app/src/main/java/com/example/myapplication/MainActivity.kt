package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.compose.material3.TextField
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold( modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class UserProfileUiState(
    val name: String = "", val isLoading: Boolean = false)

class UserViewModel : ViewModel() {
    // 내부에서만 변경 가능한 MutableStateFlow (캡슐화)
    private val _uiState = MutableStateFlow(UserProfileUiState())
    // 외부(UI)에서는 읽기만 가능한 StateFlow로 노출
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()
    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) } // // 상태 업데이트 -> UI에 자동 전파
    }
}

@Composable
fun UserProfileScreen(viewModel: UserViewModel = viewModel()) {
// StateFlow를 Compose가 이해하는 State로 변환
    // collectAsStateWithLifecycle은 생명주기에 안전하게 상태를 수집, 화면이 안보일 때 는 리컴포지션 안 함
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = uiState.name,
            onValueChange = { viewModel.updateName(it) },
            label = { Text("이름을 입력하세요") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "입력된 이름: ${uiState.name}")
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, viewModel: UserViewModel = viewModel()) {
    val inputValue = remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { testTagsAsResourceId = true },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            if (uiState.name.isEmpty()) stringResource(R.string.hello_android) else uiState.name,
            modifier = Modifier.testTag("text")
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = inputValue.value,
            onValueChange = { inputValue.value = it },
            modifier = Modifier
                .testTag("textfield")
                .fillMaxWidth(0.7f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { viewModel.updateName(inputValue.value) },
            modifier = Modifier.testTag("button")
        ) {
            Text("Update")
        }
    }

}

@Preview(showBackground = true, locale =
"ko")
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}