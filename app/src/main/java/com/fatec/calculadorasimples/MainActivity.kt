package com.fatec.calculadorasimples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fatec.calculadorasimples.ui.MainScreen
import com.fatec.calculadorasimples.ui.theme.CalculadorafatecTheme
import com.fatec.calculadorasimples.viewmodel.CalculatorViewModel

/**
 * Activity principal — ponto de entrada do aplicativo.
 *
 * Responsabilidades mínimas (seguindo boas práticas):
 * - Habilitar edge-to-edge (UI imersiva, sem barras do sistema visíveis)
 * - Aplicar o tema customizado [CalculadorafatecTheme]
 * - Delegar toda a UI à [MainScreen] (Compose)
 *
 * O ViewModel é criado aqui e passado para a Composable, garantindo
 * que seu ciclo de vida seja gerenciado pela Activity e não recriado
 * em recomposições.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita layout que se estende atrás das barras do sistema
        enableEdgeToEdge()

        setContent {
            CalculadorafatecTheme {
                // ViewModel sobrevive a rotação de tela e recomposições
                val viewModel: CalculatorViewModel = viewModel()
                MainScreen(viewModel = viewModel)
            }
        }
    }
}