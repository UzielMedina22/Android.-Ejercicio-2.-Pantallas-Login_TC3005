package medina.jonathan.login

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import medina.jonathan.login.ui.theme.LoginTheme
import java.util.Calendar

class PrincipalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var uid = Firebase.auth.currentUser?.uid ?: ""
        var myRef = Firebase.database.getReference("usuarios").child(uid)
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaPrincipal(
                        myRef,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal(myRef: DatabaseReference, modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var inputNombre by remember { mutableStateOf("") }
    var inputFecha by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf(0) }
    var puedeEditar by remember { mutableStateOf(false) }
    val context = LocalContext.current

    myRef.get().addOnSuccessListener { snapshot ->
        if (snapshot.exists()) {
            nombre = snapshot.child("name").value.toString()
            correo = snapshot.child("correo").value.toString()
            fechaNacimiento = snapshot.child("fechaNacimiento").value.toString()
            edad = obtenerEdad(fechaNacimiento)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(all = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡Bienvenido, $nombre!", fontSize = 40.sp)

        Spacer(modifier = modifier.height(16.dp))

        Text(
            text = "Correo: $correo",
            fontSize = 28.sp,
            lineHeight = 48.sp
        )

        Spacer(modifier = modifier.height(16.dp))

        Text(
            text = "Fecha de nacimiento: $fechaNacimiento ($edad años)",
            fontSize = 28.sp,
            lineHeight = 48.sp
        )

        Spacer(modifier = modifier.height(16.dp))

        OutlinedTextField(
            value = inputNombre,
            onValueChange = { inputNombre = it },
            label = {Text(text = "Nombre")},
            modifier = Modifier.fillMaxWidth(),
            enabled = puedeEditar
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputFecha,
            onValueChange = { inputFecha = it },
            label = {Text(text = "Fecha de nacimiento")},
            placeholder = {Text(text = "DD/MM/AAAA")},
            modifier = Modifier.fillMaxWidth(),
            enabled = puedeEditar
        )

        Spacer(modifier = modifier.height(16.dp))

        Button(
            onClick = {
                if (!puedeEditar) {
                    puedeEditar = true
                } else {
                    if (!inputNombre.isEmpty() && !inputFecha.isEmpty() &&
                        (inputNombre != nombre || inputFecha != fechaNacimiento)) {
                        myRef.child("name").setValue(inputNombre)
                        myRef.child("fechaNacimiento").setValue(inputFecha)
                        nombre = inputNombre
                        fechaNacimiento = inputFecha
                        Toast.makeText(
                            context,
                            "Se actualizaron los datos correctamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    puedeEditar = false
                }
            }) {
            Text(text = "Editar datos", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = {
                Firebase.auth.signOut()
                (context as? Activity)?.finish()
            }) {
            Text(text = "Cerrar sesión", fontSize = 24.sp)
        }
    }
}