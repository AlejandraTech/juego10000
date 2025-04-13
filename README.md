# 🎲 Juego 10000: Dados Estratégicos

Aplicación Android que implementa el clásico juego de dados “10000”, combinando **estrategia**, **suerte** y una experiencia moderna para dispositivos móviles.

## 📱 Descripción

**Juego 10000** es una app para Android que simula el popular juego de dados con el objetivo de alcanzar exactamente **10,000 puntos** antes que tus oponentes. Permite jugar en solitario contra bots o en modo multijugador local, todo con una interfaz intuitiva y funcionalidades completas.

## 🎮 Características Principales

| Funcionalidad                | Descripción                                                             |
| ---------------------------- | ----------------------------------------------------------------------- |
| 🎮 **Modos de juego**        | Multijugador local y juego individual contra bots de distintos niveles. |
| 👤 **Gestión de jugadores**  | Crea, edita y elimina perfiles con estadísticas asociadas.              |
| 📊 **Estadísticas**          | Seguimiento de partidas, victorias, puntuaciones máximas, etc.          |
| 🎨 **Interfaz moderna**      | Basado en Material Design 3 y Jetpack Compose.                          |
| 💾 **Guardado de partidas**  | Guarda y continúa tus partidas en cualquier momento.                    |
| 🎧 **Efectos audiovisuales** | Animaciones y sonidos inmersivos.                                       |

## 📋 Reglas del Juego

### 🎯 Objetivo

Ser el primer jugador en alcanzar **exactamente 10,000 puntos**.

### 🟢 Entrada en Juego

Para comenzar a acumular puntos, el jugador debe obtener **al menos 500 puntos** en su primer turno.

### 🔄 Mecánica de Turnos

```
1. Lanzar 6 dados.
2. Separar al menos un dado puntuable.
3. Relanzar los dados restantes (opcional).
4. Si no se obtiene puntuación, se pierde lo acumulado ese turno.
5. El jugador puede “plantarse” y sumar los puntos.
6. Si todos los dados fueron puntuables, se lanza de nuevo con los 6.
```

## 🧮 Sistema de Puntuación

### Puntuación Individual

| Dado | Puntos |
| ---- | ------ |
| 1    | 100    |
| 5    | 50     |

### Combinaciones

| Combinación          | Puntos |
| -------------------- | ------ |
| Tres 1's             | 1000   |
| Tres 2's             | 200    |
| Tres 3's             | 300    |
| Tres 4's             | 400    |
| Tres 5's             | 500    |
| Tres 6's             | 600    |
| Escalera (1–6)       | 1500   |
| Tres pares distintos | 1500   |

### Multiplicadores

| Cantidad de dados iguales | Multiplicador sobre “Tres iguales” |
| ------------------------- | ---------------------------------- |
| Cuatro                    | x2                                 |
| Cinco                     | x3                                 |
| Seis                      | x4                                 |

## 🤖 Niveles de Dificultad (Bots)

| Nivel               | Descripción                                                             |
| ------------------- | ----------------------------------------------------------------------- |
| 🟢 **Principiante** | Estrategia simple y conservadora. Se planta a partir de 300 puntos.     |
| 🟡 **Intermedio**   | Equilibrio entre riesgo y seguridad. Se planta sobre 450 puntos.        |
| 🔴 **Experto**      | Estrategia optimizada. Busca combinaciones más rentables (600+ puntos). |

## 💡 Estrategias Recomendadas

- ⚖️ **Equilibrar riesgo y recompensa**: Saber cuándo relanzar o plantarse es clave.
- 🎯 **Priorizar dados clave**: Los 1 y 5 otorgan puntos sin necesidad de combinaciones.
- 🔢 **Buscar combinaciones**: Multiplica tus puntos rápidamente.
- 👀 **Observar a tus rivales**: Ajusta tu estrategia según la situación del juego.

## 🛠️ Tecnologías Utilizadas

| Categoría                     | Tecnología                                                                                                                             |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------------------------------- |
| **Lenguaje**                  | ![Kotlin](https://img.shields.io/badge/Kotlin-%237F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)                             |
| **UI Framework**              | ![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-%234285F4.svg?style=flat-square&logo=jetpackcompose&logoColor=white) |
| **Diseño**                    | ![Material Design](https://img.shields.io/badge/Material%20Design-%23757575.svg?style=flat-square&logo=materialdesign&logoColor=white) |
| **Base de Datos**             | ![Room](https://img.shields.io/badge/Room%20Database-%23FF5722.svg?style=flat-square&logo=android&logoColor=white)                     |
| **Inyección de Dependencias** | ![Hilt](https://img.shields.io/badge/Hilt-%23121212.svg?style=flat-square&logo=android&logoColor=white)                                |
| **Arquitectura**              | ![MVVM](https://img.shields.io/badge/MVVM%20+%20Clean-%23121212.svg?style=flat-square&logo=android&logoColor=white)                    |
| **Asincronía**                | ![Coroutines](https://img.shields.io/badge/Coroutines%20&%20Flow-%237F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)          |

## 📱 Requisitos del Sistema

| Requisito                     | Detalle               |
| ----------------------------- | --------------------- |
| **Versión mínima de Android** | 6.0 (Marshmallow)     |
| **Espacio requerido**         | 50 MB aproximadamente |

## 👩‍💻 Autor

Este proyecto fue creado por [**@AlejandraTech**](https://github.com/AlejandraTech).
