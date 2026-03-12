# HevyTwo — Gerenciador de Treinos

Aplicativo Android para criação, organização e execução de treinos personalizados. O usuário se autentica, monta suas rotinas escolhendo exercícios de um banco de dados externo, configura séries e cargas, e então executa o treino em tempo real com acompanhamento de progresso.

> **Status:** O core do aplicativo está funcional — autenticação, gestão de rotinas e execução de treinos operam conforme proposto. O projeto segue em desenvolvimento ativo, com melhorias planejadas como criação de exercícios customizados pelo usuário, notificações de tempo de descanso entre séries e expansão das estatísticas de progresso.

Obs: consulta a Api externa limitada

---

## Demonstração

> https://youtube.com/shorts/JAzCuxpWMTo

## Fluxo da Aplicação

```
Abertura do app
      │
      ▼
[SessionManager verifica JWT salvo]
      │
      ├── JWT válido ──────────────────────────► Dashboard
      │                                               │
      └── Sem JWT ──► Login / Cadastro                │
                            │                         │
                            │ (salva JWT)             │
                            └────────────────────────►│
                                                       │
                                          ┌────────────┼────────────┐
                                          ▼            ▼            ▼
                                   Explorar       Minhas       Treino Diário
                                  Exercícios      Rotinas     (Start Routine)
                                          │            │
                                          │     ┌──────┴──────┐
                                          │     ▼             ▼
                                          │  Criar         Editar
                                          │  Rotina        Rotina
                                          │  (adiciona     (configura
                                          │  exercícios)   séries/cargas)
                                          │
                                          └──────────────────────────────►
                                                              Treino Ativo
                                                         (executa séries,
                                                          marca conclusão,
                                                          finaliza sessão)
```

---

## Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)** com separação clara em três camadas:

```
┌─────────────────────────────────────┐
│             UI Layer                │
│   Jetpack Compose Screens           │
│  (Login, Dashboard, Rotinas, etc.)  │
└──────────────┬──────────────────────┘
               │ observa estado
               ▼
┌─────────────────────────────────────┐
│          ViewModel Layer            │
│   AndroidViewModel + Coroutines     │
│  (LoginViewModel, DashboardVM,      │
│   RoutineBuilderVM, ActiveWorkoutVM)│
└──────────────┬──────────────────────┘
               │ chama
               ▼
┌─────────────────────────────────────┐
│            Data Layer               │
│  Repositories → ApiService          │
│  (Retrofit + OkHttp + JWT intercept)│
│  SessionManager (DataStore)         │
└─────────────────────────────────────┘
```

### Detalhes por camada

| Camada             | Responsabilidade                                                                     |
| ------------------ | ------------------------------------------------------------------------------------ |
| **UI**             | Renderiza o estado exposto pelo ViewModel. Não contém lógica de negócio.             |
| **ViewModel**      | Gerencia o estado da tela, dispara corrotinas, processa resultados dos repositórios. |
| **Repository**     | Abstrai as chamadas à API. Retorna `Result<T>` para facilitar tratamento de erro.    |
| **ApiService**     | Interface Retrofit com todos os endpoints REST do backend.                           |
| **RetrofitClient** | Singleton que configura o OkHttpClient com interceptor de autenticação JWT.          |
| **SessionManager** | Persiste e lê o token JWT e o nome do usuário usando DataStore.                      |

---

## Navegação

Gerenciada pelo **Navigation Compose** via `NavGraph.kt`. As rotas disponíveis são:

| Rota                         | Tela                                  |
| ---------------------------- | ------------------------------------- |
| `login`                      | Login e cadastro                      |
| `dashboard`                  | Painel inicial com estatísticas       |
| `menu_workout`               | Menu de opções de treino              |
| `explore_exercises`          | Explorar exercícios do banco de dados |
| `my_routines`                | Listar rotinas criadas                |
| `create_routine`             | Criar nova rotina                     |
| `edit_routine/{id}/{name}`   | Editar rotina existente               |
| `start_routine`              | Selecionar rotina para executar       |
| `active_workout/{id}/{name}` | Executar treino em tempo real         |

A rota inicial é determinada dinamicamente: se há JWT salvo → `dashboard`; caso contrário → `login`.

---

## Tecnologias Utilizadas

| Tecnologia                                           | Uso                                                                                                                                   |
| ---------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| **Kotlin**                                           | Linguagem principal                                                                                                                   |
| **Android SDK** (min 30 / target 36)                 | Plataforma                                                                                                                            |
| **Jetpack Compose**                                  | Construção de toda a interface declarativa                                                                                            |
| **Material Design 3**                                | Sistema de design e componentes visuais                                                                                               |
| **Navigation Compose**                               | Navegação entre telas                                                                                                                 |
| **ViewModel + AndroidViewModel**                     | Gerenciamento de estado e ciclo de vida                                                                                               |
| **Kotlin Coroutines**                                | Operações assíncronas (chamadas de rede, I/O)                                                                                         |
| **Retrofit 2 + OkHttp**                              | Cliente HTTP para comunicação com a API REST                                                                                          |
| **Gson**                                             | Serialização/desserialização de JSON                                                                                                  |
| **DataStore Preferences**                            | Persistência local do token JWT e sessão                                                                                              |
| **Coil + Coil-GIF**                                  | Carregamento de imagens e GIFs                                                                                                        |
| **[Stitch (Google)](https://stitch.withgoogle.com)** | Prototipagem e geração assistida de interfaces                                                                                        |
| **GitHub Copilot**                                   | Assistente de desenvolvimento — apoio na escrita de código, sugestões de implementação e revisão de lógica ao longo de todo o projeto |

---

## Estrutura de Pacotes

```
com.example.sort/
├── data/
│   ├── ApiService.kt          # Interface Retrofit (endpoints)
│   ├── RetrofitClient.kt      # Configuração do cliente HTTP + JWT
│   ├── SessionManager.kt      # Persistência de sessão (DataStore)
│   ├── AuthRepository.kt      # Login e cadastro
│   ├── RoutineRepository.kt   # CRUD de rotinas e séries
│   ├── ExerciseRepository.kt  # Busca de exercícios
│   └── [DTOs e modelos de dados]
├── viewmodel/
│   ├── LoginViewModel.kt
│   ├── DashboardViewModel.kt
│   ├── ExploreExerciseViewModel.kt
│   ├── RoutineBuilderViewModel.kt
│   ├── MyRoutinesViewModel.kt
│   ├── EditRoutineViewModel.kt
│   └── ActiveWorkoutViewModel.kt
├── navigation/
│   └── NavGraph.kt            # Definição

    rotas e navegação

├── ui/theme/                  # Tema, cores e

    tipografia

└── [Screens].kt               # Uma arquivo por tela
```

---

## Pré-requisitos para Rodar

- Android Studio Hedgehog ou superior
- Backend rodando localmente (configure o IP em `RetrofitClient.kt` → `BASE_URL`)
- Dispositivo/emulador com Android 11 (API 30) ou superior
