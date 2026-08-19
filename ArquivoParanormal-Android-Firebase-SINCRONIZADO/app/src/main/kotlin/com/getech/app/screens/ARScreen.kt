package com.getech.app.screens

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.getech.app.ui.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@Composable
fun ARScreen(onBack:()->Unit){
    val context=LocalContext.current;val lifecycle=LocalLifecycleOwner.current
    var granted by remember{mutableStateOf(androidx.core.content.ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==android.content.pm.PackageManager.PERMISSION_GRANTED)}
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){granted=it}
    LaunchedEffect(Unit){if(!granted)launcher.launch(Manifest.permission.CAMERA)}
    TechScaffold("Realidade Aumentada",onBack=onBack){pad->
        Column(Modifier.padding(pad).padding(14.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
            TechCard{Text("AR Industrial",style=MaterialTheme.typography.headlineMedium,color=Cyan);Text("Câmera nativa com HUD de inspeção. A projeção espacial 3D depende de ARCore/modelos compatíveis.",color=TextSecondary)}
            Box(Modifier.fillMaxWidth().height(430.dp)){
                if(granted){
                    AndroidView(factory={ctx->
                        PreviewView(ctx).also{view->
                            val future=ProcessCameraProvider.getInstance(ctx)
                            future.addListener({
                                val provider=future.get()
                                val preview=androidx.camera.core.Preview.Builder().build().also{it.surfaceProvider=view.surfaceProvider}
                                provider.unbindAll()
                                provider.bindToLifecycle(lifecycle,CameraSelector.DEFAULT_BACK_CAMERA,preview)
                            },ContextCompat.getMainExecutor(ctx))
                        }
                    },modifier=Modifier.fillMaxSize())
                    Surface(color=Color.Transparent,modifier=Modifier.fillMaxSize()){Box(Modifier.fillMaxSize()){Text("◎",color=Cyan,fontSize=74.sp,modifier=Modifier.align(androidx.compose.ui.Alignment.Center));Text("AR • GE TECH",color=Cyan,modifier=Modifier.padding(14.dp))}}
                }else{
                    TechCard{Text("Permissão de câmera necessária.",color=TextPrimary);PrimaryButton("Permitir câmera"){launcher.launch(Manifest.permission.CAMERA)}}
                }
            }
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){SecondaryButton("Torno CNC",Modifier.weight(1f)){};SecondaryButton("Robô",Modifier.weight(1f)){}}
        }
    }
}
