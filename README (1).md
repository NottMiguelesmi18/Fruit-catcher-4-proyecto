# 🍎 Fruit Catcher

Juego de acción en Java donde el jugador recoge frutas que caen desde arriba y esquiva las rocas. Las puntuaciones se guardan en una base de datos MySQL.

---

## 🎮 Cómo se juega

| Tecla | Acción |
|-------|--------|
| `←` `→` | Mover el jugador |
| `ESPACIO` | Iniciar partida |
| `R` | Reiniciar |

| Objeto | Puntos |
|--------|--------|
| 🍎 Manzana | +10 puntos |
| 🍍 Piña | +50 puntos |
| 🪨 Roca | -1 vida |

---

## 🛠️ Tecnologías

- **Java** con Swing
- **MySQL**
- **IntelliJ IDEA**
- **Git + GitHub**

---

## ▶️ Cómo ejecutar

1. Clona el repositorio:
```bash
git clone https://github.com/TU_USUARIO/fruit-catcher.git
```

2. Crea la base de datos en MySQL Workbench ejecutando el script SQL del proyecto

3. Abre el proyecto en IntelliJ y añade el driver MySQL:
**File → Project Structure → Libraries → +** → selecciona `mysql-connector-j.jar`

4. Ajusta tu contraseña en `GestorBaseDatos.java`

5. Ejecuta `JuegoCazaFrutas.java`
