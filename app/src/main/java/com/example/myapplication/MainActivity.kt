package com.example.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.material3.NavigationBar
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold( modifier = Modifier.fillMaxSize(),
                    bottomBar = {BottomBar()}) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Box{
            Image(
                painter = painterResource(R.drawable.android),
                contentDescription = "My Image")
            Text("Hansung", modifier= Modifier.align(Alignment.BottomCenter))
        }
        Column{
            Text(
                text = "Hello $name!"
                )
            Text("University", fontSize =  20.sp, fontWeight =  FontWeight.Bold)


        }
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = "jack!"
    )
    }
}

@Composable
fun BottomBar() {
    NavigationBar() {
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Home")
            Spacer(modifier = Modifier.width(16.dp))
            Text("Profile")
            Spacer(modifier = Modifier.width(16.dp))
            Text("Settings")
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}