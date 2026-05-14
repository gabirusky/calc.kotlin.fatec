## 1. ANÁLISE DE REQUISITOS

**Objetivo do aplicativo:**  
Demonstrar o uso do framework Kotlin no desenvolvimento mobile, implementando uma calculadora com as quatro operações básicas.

**Requisitos funcionais (o que o app deve fazer):**
- Exibir um visor com os números e o resultado.
- Botões para dígitos (0–9).
- Botões para as operações: `+` (adição), `−` (subtração), `×` (multiplicação), `÷` (divisão).
- Botão `=` para calcular o resultado.
- Botão `C` para limpar o visor e reiniciar o cálculo.
- Tratamento básico de erro: divisão por zero deve exibir “Erro” ou mensagem similar.
- Permitir números decimais (opcional, mas recomendado para demonstrar tratamento).

**Requisitos não funcionais:**
- Interface simples e responsiva (ideal para demonstração em sala).
- Código organizado e comentado, facilitando a explicação no seminário.
- App sem travamentos, executado diretamente no emulador ou dispositivo Android.

---

## 2. ARQUITETURA DO PROJETO

Para um app tão pequeno, uma arquitetura completa (MVVM, Clean) pode ser um exagero, mas como o foco é *demonstrar boas práticas do framework*, sugiro um **MVVM simplificado** com **Jetpack Compose** (moderno e conciso) ou, se preferirem, com **XML + ViewBinding**.

### Estrutura sugerida (usando MVVM + Compose):

```
CalculadoraKotlin/
├── app/src/main/java/.../calculadora/
│   ├── ui/
│   │   ├── theme/            # Cores, fontes, tema
│   │   └── MainScreen.kt     # Tela principal em Compose
│   ├── viewmodel/
│   │   └── CalculatorViewModel.kt
│   └── model/
│       └── Calculator.kt     # Lógica pura (operações)
│   └── MainActivity.kt       # Activity única
├── app/src/main/res/         # resources (se usar XML)
└── ... (outros arquivos do projeto)
```

**Fluxo de dados (MVVM):**
1. **Model**: classe `Calculator` com métodos `add(a,b)`, `subtract(a,b)`, etc. Contém a lógica pura, sem dependências Android.
2. **ViewModel**: `CalculatorViewModel` mantém o estado da UI (expressão atual, resultado) e chama o Model.
3. **View (Compose)**: observa o estado do ViewModel e renderiza os botões e o visor. As ações do usuário disparam eventos para o ViewModel.

**Vantagem para apresentação:** O MVVM separa responsabilidades e facilita a explicação do “mecanismo de funcionamento” do framework.

---

## 3. PASSO A PASSO PARA INICIAR O PROJETO

### 3.1. Configuração do ambiente
- Instale o **Android Studio** (versão mais recente, Flamingo ou superior).
- Dentro do Android Studio, verifique se os SDKs e o emulador estão configurados.
- Se for usar Jetpack Compose, tenha certeza de que o Kotlin está atualizado (1.9.x) e o Compose Compiler compatível.

### 3.2. Criar um novo projeto
1. Abra o Android Studio → **New Project**.
2. Escolha o template **“Empty Activity”** **com Compose** (ou “Empty Views Activity” se for usar XML).
3. Em **Language**, selecione **Kotlin**.
4. Defina o **Minimum SDK** como API 21 (prático para maioria dos dispositivos) ou API 24.
5. Finalize a criação.

### 3.3. Organização inicial dos pacotes
Dentro de `app/src/main/java/[seu.package]/`, crie os seguintes pacotes:
- `model`
- `viewmodel`
- `ui` (e dentro de `ui`, `theme`)

Mova a `MainActivity.kt` para a raiz do pacote principal (ou deixe onde está) e crie os arquivos necessários.

### 3.1. Configuração do ambiente
- Instale o **Android Studio** (versão mais recente, Flamingo ou superior).
- Dentro do Android Studio, verifique se os SDKs e o emulador estão configurados.
- Se for usar Jetpack Compose, tenha certeza de que o Kotlin está atualizado (1.9.x) e o Compose Compiler compatível.

### 3.2. Criar um novo projeto
1. Abra o Android Studio → **New Project**.
2. Escolha o template **“Empty Activity”** **com Compose** (ou “Empty Views Activity” se for usar XML).
3. Em **Language**, selecione **Kotlin**.
4. Defina o **Minimum SDK** como API 21 (prático para maioria dos dispositivos) ou API 24.
5. Finalize a criação.

### 3.3. Organização inicial dos pacotes
Dentro de `app/src/main/java/[seu.package]/`, crie os seguintes pacotes:
- `model`
- `viewmodel`
- `ui` (e dentro de `ui`, `theme`)

Mova a `MainActivity.kt` para a raiz do pacote principal (ou deixe onde está) e crie os arquivos necessários.

### 3.4. Implementar o Model (lógica da calculadora)
Crie `model/Calculator.kt`:
```kotlin
object Calculator {
### 3.4. Implementar o Model (lógica da calculadora)
Crie `model/Calculator.kt`:
```kotlin
object Calculator {
    fun add(a: Double, b: Double): Double = a + b
    fun subtract(a: Double, b: Double): Double = a - b
    fun multiply(a: Double, b: Double): Double = a * b
    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw ArithmeticException("Divisão por zero")
        return a / b
    }
}
```
(Opcional: fazer uma classe que guarda o estado de uma expressão, mas para simplicidade, esses métodos bastam.)

### 3.5. Criar o ViewModel
Crie `viewmodel/CalculatorViewModel.kt`:
```kotlin
class CalculatorViewModel : ViewModel() {
    // Estado da UI exposto como StateFlow
    private val _display = MutableStateFlow("0")
    val display: StateFlow<String> = _display

    private var currentOperand = ""
    private var lastOperator = ""
    private var firstOperand = 0.0

    fun onDigit(digit: String) {
        if (_display.value == "0" || _display.value == "Erro") {
            _display.value = digit
        } else {
            _display.value += digit
        }
    }

    fun onOperator(op: String) {
        firstOperand = _display.value.toDoubleOrNull() ?: 0.0
        lastOperator = op
        _display.value = "0"
    }

    fun onClear() {
        _display.value = "0"
        firstOperand = 0.0
        lastOperator = ""
    }

    fun onEquals() {
        val second = _display.value.toDoubleOrNull() ?: return
        try {
            val result = when (lastOperator) {
                "+" -> Calculator.add(firstOperand, second)
                "-" -> Calculator.subtract(firstOperand, second)
                "*" -> Calculator.multiply(firstOperand, second)
                "/" -> Calculator.divide(firstOperand, second)
                else -> second
            }
            _display.value = result.format()
        } catch (e: ArithmeticException) {
            _display.value = "Erro"
        }
    }

    private fun Double.format(): String {
        return if (this == this.toLong().toDouble())
            this.toLong().toString() else this.toString()
    }
}
```
**Explicação:** O ViewModel mantém o que aparece no visor e a lógica de acumular dígitos e operações. Injeção de dependência simples (sem Hilt, para não complicar).

### 3.6. Criar a UI com Jetpack Compose
No arquivo `ui/MainScreen.kt`:
```kotlin
@Composable
fun MainScreen(viewModel: CalculatorViewModel = viewModel()) {
    val display by viewModel.display.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        // Visor
        Text(
            text = display,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.headlineLarge
        )

        // Linhas de botões
        val buttons = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "*"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+"),
            listOf("C")
        )

        for (row in buttons) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (label in row) {
                    CalculatorButton(label) { viewModel.onButtonClick(label) }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.size(72.dp)) {
        Text(text = label)
    }
}
```

No `MainActivity.kt`, chame a tela:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraKotlinTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}
```

(A lógica do clique dos botões deve ser integrada: você pode criar um método `onButtonClick(label: String)` no ViewModel que decide se chama `onDigit`, `onOperator`, `onEquals` ou `onClear`.)

### 3.7. Configurar a ação dos botões no ViewModel
Adicione ao `CalculatorViewModel`:
```kotlin
fun onButtonClick(label: String) {
    when (label) {
        "C" -> onClear()
        "=" -> onEquals()
        "+", "-", "*", "/" -> onOperator(label)
        else -> onDigit(label) // dígitos e ponto
    }
}
```

Agora a UI chama `viewModel.onButtonClick(label)` e tudo se conecta.

### 3.8. Testar no emulador / dispositivo
- Conecte um dispositivo ou crie um AVD (Android Virtual Device).
- Execute o app (Shift + F10).
- Verifique se as operações básicas funcionam e se a divisão por zero mostra “Erro”.

---

## 4. O QUE INCLUIR NA APRESENTAÇÃO DO SEMINÁRIO

- **Propósito do Kotlin/Android**: como framework mobile moderno, conciso, interoperável com Java, adotado oficialmente pelo Google.
- **Mecanismo de funcionamento**: linguagem compilada para JVM/ART, uso de Corrotinas, ViewModel, Compose…
- **Vantagens / Desvantagens**: (ex: menos verboso, null safety, corrotinas, mas curva de aprendizado com Compose…).
- **Exemplos reais**: apps como Trello, Evernote, Netflix usam Kotlin.
- **Demonstração prática**: mostrar o app rodando e explicar rapidamente o código (Model → ViewModel → UI).

---

## 5. PRÓXIMOS PASSOS (ENTREGA DO PROJETO)
- Subir o código no **GitHub** e gerar um `.zip` para anexo.
- Gravar um vídeo curto do app em funcionamento (se não for possível espelhar na TV).
- Preparar slides com os tópicos do seminário.

Esse roteiro garante que vocês cubram a análise de requisitos, a arquitetura e ainda tenham um app funcional para demonstrar o uso do Kotlin. Se precisarem de ajuda com algum trecho específico do código, é só falar!