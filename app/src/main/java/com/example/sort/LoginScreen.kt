package com.example.sort

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sort.viewmodel.LoginViewModel

@Composable
fun LoginScree(viewModel: LoginViewModel = viewModel (), onLoginSuccess: () -> Unit) {
    // ESTADO: Criamos uma variável que lembra qual aba está selecionada
    var selectedTab by remember { mutableStateOf(0) }

    androidx.compose.runtime.LaunchedEffect(viewModel.isLoginSuccessful) {
        if (viewModel.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF020024), Color(0xFF090979), Color(0xFF8A00FF))
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()).animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBarRow()
            Spacer(modifier = Modifier.height(40.dp))
            LogoComponent()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Bem-vindo",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            // Passamos o estado e a função de mudar o estado para o Switcher
            TabSwitcher(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            AnimatedVisibility(
                visible = selectedTab == 1,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                CustomTextField(
                    label = "Nome de usuário",
                    placeholder = "Ex: joaosilva",
                    icon = Icons.Default.Person,
                    value = viewModel.username,      // LIGA AO VIEWMODEL
                    onValueChange = { viewModel.username = it}
                )
            }

            // Estes sempre aparecem
            CustomTextField(
                label = "E-mail",
                placeholder = "exemplo@email.com",
                icon = Icons.Default.Email,
                value = viewModel.email,             // LIGA AO VIEWMODEL
                onValueChange = { viewModel.email = it }
            )
            CustomTextField(
                label = "Senha",
                placeholder = "Sua senha secreta",
                icon = Icons.Default.Lock,
                value = viewModel.password,          // LIGA AO VIEWMODEL
                onValueChange = { viewModel.password = it },
                isPassword = true
            )

            MainActionButton(
                text = if (selectedTab == 0) "Entrar" else "Cadastrar",
                onClick = { if (selectedTab == 0) viewModel.onLoginClick() else viewModel.onRegisterClick() } // CHAMA A FUNÇÃO DO VIEWMODEL
            )

            viewModel.loginError?.let { erro ->
                Text(text = erro, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Text(
                text = "Igual ao Hevy, porém Melhor!!",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.W100
            )
        }

    }
}

@Composable
fun TopAppBarRow() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .height(32.dp), // Definimos uma altura fixa para a barra
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Hevy Clone",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LogoComponent() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(
                brush = Brush.linearGradient(listOf(Color(0xFF7311D4), Color(0xFF3B82F6))),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.White)
    }
}

@Composable
fun TabSwitcher(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth()
            .height(50.dp)
            // EDIÇÃO: RoundedCornerShape(50.dp) deixa as bordas totalmente circulares
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
            .padding(4.dp)
    ) {
        // Botão Entrar
        Button(
            onClick = { onTabSelected(0) },
            modifier = Modifier.weight(1f).fillMaxHeight(),
            // Se estiver selecionado, fica roxo, se não, fica transparente
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == 0) Color(0xFF7311D4) else Color.Transparent
            ),
            shape = RoundedCornerShape(50.dp)
        ) { Text("Entrar", color = Color.White) }

        // Botão Criar Conta
        Button(
            onClick = { onTabSelected(1) },
            modifier = Modifier.weight(1f).fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedTab == 1) Color(0xFF7311D4) else Color.Transparent
            ),
            shape = RoundedCornerShape(50.dp)
        ) { Text("Criar conta", color = if (selectedTab == 1) Color.White else Color.LightGray) }
    }
}

@Composable
fun CustomTextField(
    label: String,
    placeholder: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color(0xFF3B82F6),
                cursorColor = Color.White
            )
        )
    }
}

@Composable
fun MainActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(

            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Color(0xFF7311D4), Color(0xFF3B82F6)))),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.Login, contentDescription = null)
            }
        }
    }
}