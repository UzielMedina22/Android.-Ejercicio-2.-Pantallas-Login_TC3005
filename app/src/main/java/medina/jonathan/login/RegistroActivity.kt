package medina.jonathan.login

import android.content.Intent
import android.icu.text.DateFormat
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import medina.jonathan.login.ui.theme.LoginTheme
import java.util.Calendar
import java.util.Date

class RegistroActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        database = Firebase.database.reference
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(
                        auth,
                        database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


fun obtenerEdad(fechaNacimiento: String): Int{
    var anioActual: Int = Calendar.getInstance().get(Calendar.YEAR)
    var mesActual: Int = Calendar.getInstance().get(Calendar.MONTH)
    var diaActual: Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK_IN_MONTH)
    var anioNacimiento: Int = 0
    var mesNacimiento: Int = 0
    var diaNacimiento: Int = 0
    var edad: Int = 0

    if (fechaNacimiento.isNotEmpty()) {
        mesNacimiento = fechaNacimiento.subSequence(3, 5).toString().toInt()
        diaNacimiento = fechaNacimiento.subSequence(0, 2).toString().toInt()
        anioNacimiento = fechaNacimiento.subSequence(6, fechaNacimiento.length).toString().toInt()
        edad = anioActual - anioNacimiento

        if ((mesActual < mesNacimiento) ||
            ((mesActual < mesNacimiento) && (diaActual < diaNacimiento))) {
            edad--
        }
    }

    return edad
}

@Composable
fun PantallaRegistro(auth: FirebaseAuth, database: DatabaseReference, modifier: Modifier = Modifier) {
    var nombre by remember(){ mutableStateOf(value="") }
    var correo by remember(){ mutableStateOf(value="") }
    var password by remember(){ mutableStateOf(value="") }
    var verificarPassword by remember(){ mutableStateOf(value="") }
    var fechaNacimiento by remember(){ mutableStateOf(value="") }
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(all = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = {Text(text = "Nombre")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = {Text(text = "Correo electrónico")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = {Text(text = "Fecha de nacimiento")},
            placeholder = {Text(text = "DD/MM/AAAA")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {Text(text = "Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = verificarPassword,
            onValueChange = { verificarPassword = it },
            label = {Text(text = "Verificar contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(){
            TextButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = "Cancelar", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                var edad: Int = 0

                if (fechaNacimiento.isNotEmpty()) {
                    edad = obtenerEdad(fechaNacimiento)
                }

                if (nombre.isEmpty() || correo.isEmpty()|| fechaNacimiento.isEmpty() ||
                    password.isEmpty() || verificarPassword.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Error: Alguno(s) de los campos está(n) vacío(s).",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else if (!correo.isEmpty() && "@" !in correo) {
                    Toast.makeText(
                        context,
                        "Error: El formato del correo es incorrecto.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else if (!fechaNacimiento.isEmpty() &&
                    (fechaNacimiento[2] != '/' || fechaNacimiento[5] != '/')) {
                    Toast.makeText(
                        context,
                        "Error: El formato de la fecha de nacimiento es incorrecto.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else if (edad < 18) {
                    Toast.makeText(
                        context,
                        "Error: La cuenta no se puede crear porque el usuario es menor de 18 años.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else if ((!password.isEmpty() && !verificarPassword.isEmpty()) &&
                    (password != verificarPassword)){
                    Toast.makeText(
                        context,
                        "Error: Las contraseñas no coinciden.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else {
                    auth.createUserWithEmailAndPassword(correo, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Éxito al crear la cuenta.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                var usuario = Usuario(nombre, correo, fechaNacimiento)
                                var userID = auth.currentUser?.uid ?: "Anonimo"
                                database.child("usuarios").child(userID).setValue(usuario)
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                            else {
                                Toast.makeText(
                                    context,
                                    "Error al crear la cuenta.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }) {
                Text(text = "Crear cuenta", fontSize = 16.sp)
            }
        }
    }
}