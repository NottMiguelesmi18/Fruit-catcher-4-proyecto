package FruitCatcher;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class GestorBaseDatos {

    private static final String SERVIDOR   = "localhost";
    private static final String PUERTO     = "3306";
    private static final String BASE_DATOS = "fruit_catcher_db";
    private static final String USUARIO    = "root";
    private static final String CONTRASENA = "mysql";

    private static final String URL_CONEXION =
            "jdbc:mysql://" + SERVIDOR + ":" + PUERTO + "/" + BASE_DATOS
                    + "?useSSL=false&serverTimezone=UTC";


    private static Connection abrirConexion() throws SQLException {
        return DriverManager.getConnection(URL_CONEXION, USUARIO, CONTRASENA);
    }

    public static boolean guardarPartida(String nombre, int puntuacion, int duracionSegundos) {
        try (Connection conexion = abrirConexion()) {

            int idJugador = buscarJugador(conexion, nombre);


            if (idJugador == -1) {
                idJugador = crearJugador(conexion, nombre);
            }

            if (idJugador == -1) return false;

            Timestamp fechaHora = Timestamp.valueOf(LocalDateTime.now());

            String consulta = "INSERT INTO Partidas (id_usuario, puntuacion, fecha, duracion) VALUES (?, ?, ?, ?)";
            PreparedStatement sentenciaPartida = conexion.prepareStatement(consulta);
            sentenciaPartida.setInt(1, idJugador);
            sentenciaPartida.setInt(2, puntuacion);
            sentenciaPartida.setTimestamp(3, fechaHora);
            sentenciaPartida.setInt(4, duracionSegundos);
            sentenciaPartida.executeUpdate();
            sentenciaPartida.close();

            System.out.println("Partida guardada: " + nombre + " | " + puntuacion + " puntos | " + duracionSegundos + " segundos");
            return true;

        } catch (SQLException error) {
            System.err.println("Error en la base de datos: " + error.getMessage());
            return false;
        }
    }

    private static int buscarJugador(Connection conexion, String nombre) throws SQLException {
        String consulta = "SELECT id_usuario FROM Usuarios WHERE nombre = ?";
        PreparedStatement sentencia = conexion.prepareStatement(consulta);
        sentencia.setString(1, nombre);
        ResultSet resultado = sentencia.executeQuery();

        int id = -1;
        if (resultado.next()) {
            id = resultado.getInt("id_usuario");
        }

        resultado.close();
        sentencia.close();
        return id;
    }

    private static int crearJugador(Connection conexion, String nombre) throws SQLException {
        String fechaHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String consulta = "INSERT INTO Usuarios (nombre, fecha_registro) VALUES (?, ?)";
        PreparedStatement sentencia = conexion.prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);
        sentencia.setString(1, nombre);
        sentencia.setString(2, fechaHora);
        sentencia.executeUpdate();

        ResultSet clavesGeneradas = sentencia.getGeneratedKeys();
        int id = -1;
        if (clavesGeneradas.next()) {
            id = clavesGeneradas.getInt(1);
        }

        clavesGeneradas.close();
        sentencia.close();
        return id;
    }

    public static void mostrarRanking() {
        String consulta =
                "SELECT u.nombre, p.puntuacion, p.duracion, p.fecha " +
                        "FROM Partidas p " +
                        "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                        "ORDER BY p.puntuacion DESC LIMIT 10";

        try (Connection conexion = abrirConexion();
             Statement sentencia = conexion.createStatement();
             ResultSet resultado  = sentencia.executeQuery(consulta)) {

            System.out.println("=== TOP 10 PUNTUACIONES ===");
            int posicionRanking = 1;
            while (resultado.next()) {
                System.out.printf("%2d. %-15s | %5d puntos | %3d seg | %s%n",
                        posicionRanking++,
                        resultado.getString("nombre"),
                        resultado.getInt("puntuacion"),
                        resultado.getInt("duracion"),
                        resultado.getString("fecha")
                );
            }

        } catch (SQLException error) {
            System.err.println("Error al leer el ranking: " + error.getMessage());
        }
    }
}