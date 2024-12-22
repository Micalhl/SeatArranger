import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

//lateinit var content: String
//    private set






// 主题色
private val ThemeColor = Color(0xFF007AFF)  // iOS 蓝色

// 界面颜色
private val LightBg = Color(0xFFEEEEEE)      // 浅灰背景色
private val LightSurface = Color(0xFFF8F9FA)  // 带灰的卡片背景
private val Border = Color(0xFFDFE1E5)        // 边框色

val contentString: String? = null

@Composable
fun App() {

    var content by remember { mutableStateOf(contentString ?: "") }
    var selectedFile: File? = null
    var selectedFileName by remember { mutableStateOf(selectedFile?.name ?: "未选择文件") }
    var isProcessing by mutableStateOf(false)

    MaterialTheme(
        colors = lightColors(
            primary = ThemeColor,
            background = LightBg,
            surface = LightSurface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.width(400.dp).padding(16.dp),
                elevation = 1.dp,
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 文件选择区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedFileName,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { selectedFileName = openFileChooser()?.name ?: "未选择文件" },
                            modifier = Modifier.width(90.dp)
                        ) {
                            Text("选择文件", style = MaterialTheme.typography.body2)
                        }
                    }

                    // 说明文字
                    Text(
                        text = "请在下方输入框中输入座位不向前移动的学生的名字或名字开头，多个同学名称中间用空格分隔。\n例如：汤姆 汤",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                    )

                    // 内容编辑区域
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.body2,
                        placeholder = {
                            Text(
                                "学生名称",
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    )

                    // 按钮区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        OutlinedButton(
                            onClick = { /* 保存函数 */ },
                            modifier = Modifier.width(90.dp)
                        ) {
                            Text("保存", style = MaterialTheme.typography.body2)
                        }

                        Button(
                            onClick = {
                                isProcessing = true
                                /* 处理函数 */
                            },
                            enabled = !isProcessing,
                            modifier = Modifier.width(90.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.6f)
                            )
                        ) {
                            Text(
                                if (isProcessing) "处理中" else "开始处理",
                                style = MaterialTheme.typography.body2,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "自动换座工具",
        state = rememberWindowState(
            size = DpSize(400.dp, 350.dp)  // 设置窗口大小
        )
    ) {
        App()
    }
}

fun openFileChooser(): File? {
    val dialog = FileDialog(Frame(), "选择文件", FileDialog.LOAD).apply {
        // 设置文件过滤器，只允许选择 Excel 文件
        setFilenameFilter { _, name ->
            name.lowercase().run {
                endsWith(".xlsx") || endsWith(".xls")
            }
        }
        // 默认显示 xlsx 文件
        file = "*.xlsx"
    }
    dialog.isVisible = true
    val selectedFile = dialog.files.firstOrNull()

    return selectedFile
}

//fun loadConfig() {
//    val file = File(System.getProperty("user.home"), "seat-arranger.txt")
//    if (!file.exists()) {
//        file.createNewFile()
//    }
//    content = file.readLines(StandardCharsets.UTF_8).firstOrNull() ?: ""
//}
