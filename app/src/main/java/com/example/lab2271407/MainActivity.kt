package com.example.lab2271407

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lab2271407.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CallLogApp()
                }
            }
        }
    }
}

data class CallLogEntry(
    val name: String,
    val number: String,
    val typeText: String,
    val date: Long
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CallLogApp() {
    val context = LocalContext.current
    var hasPermissions by remember { mutableStateOf(checkPermissions(context)) }
    var loading by remember { mutableStateOf(false) }
    var callLogs by remember { mutableStateOf<List<CallLogEntry>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.all { it.value == true }
    }

    // 앱 시작 시 권한 요청
    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS
            ))
        }
    }

    // 권한이 허용되면 통화기록 자동 로드
    LaunchedEffect(hasPermissions) {
        if (hasPermissions) {
            loading = true
            val list = loadRecentCallLogs(context, limit = 3)
            callLogs = list
            loading = false
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)) {

            if (!hasPermissions) {
                Text("권한이 필요합니다.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    permissionLauncher.launch(arrayOf(
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS
                    ))
                }) { Text("권한 요청") }
                return@Column
            }

            if (loading) {
                Text("불러오는 중...")
            }

            if (callLogs.isEmpty() && !loading) {
                Text("표시할 통화기록이 없습니다.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(callLogs) { entry ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)) {
                            // 요구된 ID들 (Compose에서는 testTag로 대체)
                            Text(
                                text = entry.name,
                                modifier = Modifier
                                    .semantics { testTagsAsResourceId = true }
                                    .testTag("TextName")
                            )
                            Text(
                                text = entry.number,
                                modifier = Modifier
                                    .semantics { testTagsAsResourceId = true }
                                    .testTag("TextNum")
                            )
                            Text(
                                text = entry.typeText,
                                modifier = Modifier
                                    .semantics { testTagsAsResourceId = true }
                                    .testTag("TextType")
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

private fun checkPermissions(context: Context): Boolean {
    val p1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
    val p2 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
    return p1 == PackageManager.PERMISSION_GRANTED && p2 == PackageManager.PERMISSION_GRANTED
}

private fun callTypeToString(type: Int): String {
    return when (type) {
        CallLog.Calls.INCOMING_TYPE -> "INCOMING"
        CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
        CallLog.Calls.MISSED_TYPE -> "MISSED"
        else -> "UNKNOWN"
    }
}

private fun lookupContactName(context: Context, phoneNumber: String): String? {
    if (phoneNumber.isEmpty()) return null
    val resolver = context.contentResolver
    val uri: Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber))
    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
    val cursor = resolver.query(uri, projection, null, null, null)
    cursor?.use { cur ->
        if (cur.moveToFirst()) {
            return cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
        }
    }
    return null
}

suspend fun loadRecentCallLogs(context: Context, limit: Int = 3): List<CallLogEntry> {
    return withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val projection = arrayOf(
            CallLog.Calls._ID,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE
        )
        val sortOrder = "${CallLog.Calls.DATE} DESC"
        val list = mutableListOf<CallLogEntry>()
        val cursor = resolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, sortOrder)
        cursor?.use { cur ->
            var count = 0
            while (cur.moveToNext() && count < limit) {
                val number = cur.getString(cur.getColumnIndexOrThrow(CallLog.Calls.NUMBER)) ?: ""
                val typeInt = cur.getInt(cur.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                val date = cur.getLong(cur.getColumnIndexOrThrow(CallLog.Calls.DATE))
                val typeText = callTypeToString(typeInt)
                val name = lookupContactName(context, number) ?: "Unknown"
                list.add(CallLogEntry(name = name, number = number, typeText = typeText, date = date))
                count++
            }
        }
        list
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCallLogApp() {
    MyApplicationTheme {
        CallLogApp()
    }
}