# 📱 Calculadora Simples com Android + Kotlin

> **Seminário de Avaliação: Mobile Frameworks**  
> Disciplina: Programação para Dispositivos Móveis  
> Professor: **Luís Felipe**  
> FATEC 2026

---

## 👥 Grupo

| Aluno |
|---|
| Douglas Lelis |
| Gabriel Pereira |
| João Gabriel Duran |
| João Gabriel Silva |
| Juan Bolderini |
| Rafael Felipe Cursino |
| Pablo Henrique |

---

## 📋 Sobre o Projeto

Aplicativo Android de **calculadora simples** desenvolvido em **Kotlin** com **Jetpack Compose**, demonstrando o uso do framework mobile moderno do Android com arquitetura **MVVM** e boas práticas de desenvolvimento.

O app implementa as quatro operações básicas (+, −, ×, ÷), tratamento de erro para divisão por zero e suporte a números decimais.

---

## 🧩 Tecnologia: Kotlin + Jetpack Compose

### Propósito

Kotlin é a linguagem oficial do Google para desenvolvimento Android desde 2017. É moderna, concisa e 100% interoperável com Java. O Jetpack Compose é o kit de ferramentas de UI declarativo do Android, que substitui os layouts XML tradicionais.

### Mecanismo de Funcionamento

- **Kotlin** compila para bytecode da JVM e roda na Android Runtime (ART).
- **Jetpack Compose** reconstrói a UI automaticamente quando o estado muda (*recomposição*), usando um paradigma funcional e declarativo.
- O **ViewModel** sobrevive a rotações de tela e mantém o estado separado da UI.
- O **StateFlow** propaga mudanças de estado de forma reativa para os Composables.

### Vantagens

- ✅ Null safety nativo  menos NullPointerException
- ✅ Sintaxe concisa (40% menos código vs. Java equivalente)
- ✅ Coroutines para programação assíncrona simples
- ✅ Compose elimina XMLs e findViewById
- ✅ Hot reload com Live Edit no Android Studio
- ✅ Suporte oficial e primeira classe do Google

### Desvantagens

- ❌ Curva de aprendizado com paradigma declarativo (Compose)
- ❌ Integração com código legado XML, requer esforço extra de migração
- ❌ Recomposições ineficientes podem impactar performance em runtime
- ❌ Web ainda em Beta; integrações profundas com APIs nativas de cada plataforma ainda exigem código específico

### Exemplos de Aplicativos Reais

| Aplicativo | Uso de Kotlin |
|---|---|
| **Google Play** | Totalmente reescrito em Kotlin |
| **Netflix** | Backend + Android client |
| **Trello** | App Android em Kotlin |
| **Evernote** | App Android em Kotlin |
| **Airbnb** | Kotlin Multiplatform |

---

## 🏗️ Arquitetura do Projeto (MVVM)

```
calculadorasimples/
├── model/
│   ├── Calculator.kt          # Lógica pura das operações (+, −, ×, ÷)
│   └── CalculatorState.kt     # Data class com o estado da UI
├── viewmodel/
│   └── CalculatorViewModel.kt # Gerencia estado via StateFlow
├── ui/
│   ├── MainScreen.kt          # Tela principal em Jetpack Compose
│   └── theme/
│       ├── Color.kt           # Paleta de cores dark mode
│       ├── Theme.kt           # Tema Material3 customizado
│       └── Type.kt            # Tipografia (visor 72sp)
└── MainActivity.kt            # Activity única  ponto de entrada
```

**Fluxo de dados:**

```
Usuário clica botão
      ↓
CalculatorButton (Compose)
      ↓
viewModel.onButtonClick(label)
      ↓
CalculatorViewModel atualiza StateFlow
      ↓
UI recompõe automaticamente
```

---

## ✨ Funcionalidades

- [x] Quatro operações básicas: `+` `−` `×` `÷`
- [x] Divisão por zero exibe **"Erro"** em vermelho
- [x] Números decimais com ponto (`.`)
- [x] Visor duplo: expressão acumulada + resultado
- [x] Encadeamento de operações (ex: `5 + 3 × 2`)
- [x] Botão `C` limpa tudo (reset completo)
- [x] Animação de escala ao pressionar botões
- [x] Design dark mode estilo iOS Calculator
- [x] Tela travada em modo retrato
- [x] 13 testes unitários JVM

---

## 🚀 Como Rodar no Android Studio

### Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Android Studio | Hedgehog 2023.1.1+ (ou superior) |
| JDK | 11+ (incluso no Android Studio) |
| Android SDK | API 21 (Android 5.0 Lollipop) |
| Kotlin | 2.0.21 (gerenciado pelo Gradle) |

---

### Passo 1  Clonar ou abrir o projeto

**Via Git:**
```bash
git clone https://github.com/seu-usuario/calculadorafatec.git
```

Ou baixe o `.zip` e extraia em qualquer pasta.

---

### Passo 2  Abrir no Android Studio

1. Abra o **Android Studio**
2. Clique em **"Open"** (ou `File → Open`)
3. Navegue até a pasta `calculadorafatec/` e clique **OK**
4. Aguarde o **Gradle sync** finalizar (barra de progresso na parte inferior)

> ⚠️ Se aparecer aviso de "SDK not found", vá em `File → Project Structure → SDK Location` e aponte para o seu Android SDK.

---

### Passo 3  Configurar o dispositivo de execução

#### Opção A  Emulador (AVD)

1. Clique no ícone **Device Manager** (🖥️) na barra lateral direita
2. Clique em **"Create Device"**
3. Escolha um hardware (ex: **Pixel 6**)
4. Selecione uma imagem de sistema (ex: **API 34  Android 14**)
5. Clique **Finish** e depois **▶ Play** para iniciar o emulador

#### Opção B  Dispositivo físico

1. No celular Android, vá em **Configurações → Sobre o telefone**
2. Toque **7 vezes** em "Número de compilação" para ativar Modo Desenvolvedor
3. Vá em **Configurações → Opções do desenvolvedor → Depuração USB** → Ativar
4. Conecte o celular ao computador via USB
5. Confirme a permissão de depuração no celular

---

### Passo 4  Executar o app

1. Selecione o dispositivo na barra superior (dropdown ao lado do ▶)
2. Pressione **`Shift + F10`** ou clique no botão **▶ Run**
3. O Android Studio vai compilar e instalar automaticamente
4. A calculadora abrirá no dispositivo/emulador 🎉

---

### Passo 5  Executar os testes unitários (opcional)

No terminal do Android Studio (`View → Terminal`):

```bash
./gradlew test
```

Ou clique com botão direito em `CalculatorTest.kt` → **"Run 'CalculatorTest'"**

Resultado esperado: **13 testes passando ✅**

---

## 📦 Estrutura de Dependências

```toml
# gradle/libs.versions.toml
[versions]
kotlin              = "2.0.21"
composeBom          = "2024.09.00"
lifecycleRuntimeKtx = "2.8.7"

[libraries]
androidx-compose-bom              # Bill of Materials do Compose
androidx-lifecycle-viewmodel-compose  # viewModel() em Composables
androidx-lifecycle-runtime-compose    # collectAsStateWithLifecycle()
androidx-compose-material3            # Material You (Material Design 3)
```

---

## 📄 Licença

Projeto desenvolvido para fins acadêmicos  FATEC 2026.
