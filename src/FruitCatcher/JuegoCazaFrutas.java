import FruitCatcher.GestorBaseDatos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class JuegoCazaFrutas extends JPanel implements ActionListener, KeyListener {

    public static final int ANCHO = 600;
    public static final int ALTO  = 650;

    private int jugadorX = ANCHO / 2 - 32;
    private final int JUGADOR_Y   = ALTO - 90;
    private final int JUGADOR_ANCHO = 64;
    private final int JUGADOR_ALT = 64;
    private final int VELOCIDAD   = 7;
    enum TipoObjeto { MANZANA, PINYA, ROCA }
    private static final int MAX_OBJETOS = 6;
    private int[]     objetoX      = new int[MAX_OBJETOS];
    private int[]     objetoY      = new int[MAX_OBJETOS];
    private TipoObjeto[] objetoTipo = new TipoObjeto[MAX_OBJETOS];
    private boolean[] objetoActivo = new boolean[MAX_OBJETOS];
    private int[]     objetoVelocidad    = new int[MAX_OBJETOS];

    private int puntuacion = 0;
    private int vidas      = 3;

    private long tiempoInicio;
    private long tiempoTotal = 0;

    private boolean enJuego         = false;
    private boolean partidaTerminada = false;
    private boolean mostrarFormulario = false;

    private String nombreJugador = "";

    private boolean moverIzquierda = false;
    private boolean moverDerecha   = false;

    private Random aleatorio = new Random();

    private Image imagenJugador;
    private Image imagenManzana;
    private Image imagenPinya;
    private Image imagenRoca;
    private Image imagenCorazon;

    private Timer temporizador;

    private int velocidadBase = 3;


    public JuegoCazaFrutas() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(new Color(30, 100, 30));
        setFocusable(true);
        addKeyListener(this);

        cargarImagenes();

        temporizador = new Timer(16, this);
        temporizador.start();
    }

    private void cargarImagenes() {
        try {
            imagenJugador = cargarRecurso("/imagenes/jugador.png");
            imagenManzana = cargarRecurso("/imagenes/manzana.png");
            imagenPinya   = cargarRecurso("/imagenes/pinya.png");
            imagenRoca    = cargarRecurso("/imagenes/roca.png");
            imagenCorazon = cargarRecurso("/imagenes/corazon.png");
        } catch (Exception e) {
            System.err.println("Error crítico: No se pudieron cargar las imágenes.");
        }
    }
    private Image cargarRecurso(String ruta) {
        java.net.URL url = getClass().getResource(ruta);
        if (url == null) {
            System.err.println("No se encontró el archivo en: " + ruta);
            return null;
        }
        return new ImageIcon(url).getImage();
    }


    private void iniciarPartida() {
        puntuacion        = 0;
        vidas             = 3;
        velocidadBase     = 3;
        jugadorX          = ANCHO / 2 - 32;
        partidaTerminada  = false;
        mostrarFormulario = false;
        enJuego           = true;
        tiempoInicio      = System.currentTimeMillis();

        for (int i = 0; i < MAX_OBJETOS; i++) {
            objetoActivo[i] = false;
        }

        for (int i = 0; i < 3; i++) {
            lanzarObjeto(i);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        if (!enJuego) return;

        moverJugador();
        moverObjetos();
        comprobarColisiones();
        ajustarDificultad();
        repaint();
    }


    private void moverJugador() {
        if (moverIzquierda) jugadorX -= VELOCIDAD;
        if (moverDerecha)   jugadorX += VELOCIDAD;


        if (jugadorX < 0)  jugadorX = 0;
        if (jugadorX > ANCHO - JUGADOR_ANCHO) jugadorX = ANCHO - JUGADOR_ANCHO;
    }


    private void moverObjetos() {
        for (int i = 0; i < MAX_OBJETOS; i++) {
            if (!objetoActivo[i]) continue;

            objetoY[i] += objetoVelocidad[i];


            if (objetoY[i] > ALTO) {
                objetoActivo[i] = false;
                lanzarObjeto(i);
            }
        }
    }

    private void comprobarColisiones() {

        Rectangle rectanguloJugador = new Rectangle(jugadorX, JUGADOR_Y, JUGADOR_ANCHO, JUGADOR_ALT);

        for (int i = 0; i < MAX_OBJETOS; i++) {
            if (!objetoActivo[i]) continue;

            Rectangle rectanguloObjeto = new Rectangle(objetoX[i], objetoY[i], 50, 50);


            if (rectanguloJugador.intersects(rectanguloObjeto)) {
                objetoActivo[i] = false;
                if (objetoTipo[i] == TipoObjeto.MANZANA) puntuacion += 10;
                if (objetoTipo[i] == TipoObjeto.PINYA)   puntuacion += 50;
                if (objetoTipo[i] == TipoObjeto.ROCA) {
                    vidas--;
                    if (vidas <= 0) {
                        terminarPartida();
                        return;
                    }
                }
                lanzarObjeto(i);
            }
        }
    }

    private void ajustarDificultad() {
        velocidadBase = 3 + (puntuacion / 100);
        if (velocidadBase > 9) velocidadBase = 9;
    }


    private void lanzarObjeto(int i) {
        objetoX[i]   = aleatorio.nextInt(ANCHO - 50);
        objetoY[i]   = -50 - aleatorio.nextInt(200);
        objetoVelocidad[i] = velocidadBase + aleatorio.nextInt(3);


        int numero = aleatorio.nextInt(100);
        if      (numero < 30) objetoTipo[i] =TipoObjeto.MANZANA;
        else if (numero < 45) objetoTipo[i] = TipoObjeto.PINYA;
        else                  objetoTipo[i] = TipoObjeto.ROCA;

        objetoActivo[i] = true;
    }


    private void terminarPartida() {
        enJuego           = false;
        partidaTerminada  = true;
        mostrarFormulario = true;
        tiempoTotal       = (System.currentTimeMillis() - tiempoInicio) / 1000;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!enJuego && !partidaTerminada) {
            dibujarPantallaInicio(g2);
        } else if (enJuego) {
            dibujarPartida(g2);
        } else if (partidaTerminada) {
            dibujarPantallaFinal(g2);
        }
    }


    private void dibujarPantallaInicio(Graphics2D g) {

        GradientPaint fondoVerde = new GradientPaint(0, 0, new Color(20, 90, 20), 0, ALTO, new Color(5, 40, 5));
        g.setPaint(fondoVerde);
        g.fillRect(0, 0, ANCHO, ALTO);

        g.setColor(new Color(255, 220, 50));
        g.setFont(new Font("Arial Black", Font.BOLD, 46));
        centrarTexto(g, "CAZA FRUTAS", ALTO / 2 - 90);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        centrarTexto(g, "Recoge frutas y esquiva las rocas!", ALTO / 2 - 10);
        centrarTexto(g, "Usa  <--  -->  para moverte", ALTO / 2 + 25);

        g.setColor(new Color(255, 220, 50));
        g.setFont(new Font("Arial Black", Font.BOLD, 22));
        centrarTexto(g, "Pulsa ESPACIO para empezar", ALTO / 2 + 90);

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(150, ALTO / 2 + 120, 300, 110, 15, 15);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 17));

        if (imagenManzana != null) g.drawImage(imagenManzana, 165, ALTO / 2 + 128, 32, 32, null);
        g.drawString("Manzana  =  +10 puntos", 205, ALTO / 2 + 150);

        if (imagenPinya != null) g.drawImage(imagenPinya, 165, ALTO / 2 + 163, 32, 32, null);
        g.drawString("Pina     =  +50 puntos", 205, ALTO / 2 + 185);

        if (imagenRoca != null) g.drawImage(imagenRoca, 165, ALTO / 2 + 196, 32, 32, null);
        g.drawString("Roca     =  -1 vida",    205, ALTO / 2 + 218);
    }


    private void dibujarPartida(Graphics2D g) {
        GradientPaint fondoVerde = new GradientPaint(0, 0, new Color(20, 90, 20), 0, ALTO, new Color(5, 40, 5));
        g.setPaint(fondoVerde);
        g.fillRect(0, 0, ANCHO, ALTO);

        dibujarImagen(g, imagenJugador, jugadorX, JUGADOR_Y, JUGADOR_ANCHO, JUGADOR_ALT, new Color(100, 160, 255));

        for (int i = 0; i < MAX_OBJETOS; i++) {
            if (!objetoActivo[i]) continue;
            if (objetoTipo[i] == TipoObjeto.MANZANA) dibujarImagen(g, imagenManzana, objetoX[i], objetoY[i], 50, 50, new Color(220, 50, 50));
            if (objetoTipo[i] == TipoObjeto.PINYA) dibujarImagen(g, imagenPinya,   objetoX[i], objetoY[i], 50, 50, new Color(230, 180, 20));
            if (objetoTipo[i] == TipoObjeto.ROCA) dibujarImagen(g, imagenRoca,    objetoX[i], objetoY[i], 50, 50, new Color(120, 110, 100));
        }

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(10, 10, ANCHO - 20, 52, 15, 15);

        g.setColor(new Color(255, 220, 50));
        g.setFont(new Font("Arial Black", Font.BOLD, 20));
        g.drawString("Puntos: " + puntuacion, 25, 44);

        for (int v = 0; v < vidas; v++) {
            dibujarImagen(g, imagenCorazon, ANCHO - 115 + v * 35, 14, 28, 28, Color.RED);
        }

        long segundos = (System.currentTimeMillis() - tiempoInicio) / 1000;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        centrarTexto(g, "Tiempo: " + segundos + "s", 44);
    }

    private void dibujarPantallaFinal(Graphics2D g) {
        GradientPaint fondoVerde = new GradientPaint(0, 0, new Color(20, 90, 20), 0, ALTO, new Color(5, 40, 5));
        g.setPaint(fondoVerde);
        g.fillRect(0, 0, ANCHO, ALTO);

        g.setColor(new Color(0, 0, 0, 190));
        g.fillRoundRect(70, 140, ANCHO - 140, 370, 25, 25);

        g.setColor(new Color(255, 70, 70));
        g.setFont(new Font("Arial Black", Font.BOLD, 50));
        centrarTexto(g, "FIN DE PARTIDA", 220);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        centrarTexto(g, "Puntuacion: " + puntuacion + " puntos", 275);
        centrarTexto(g, "Tiempo: " + tiempoTotal + " segundos", 312);

        g.setColor(new Color(255, 220, 50));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        centrarTexto(g, "Introduce tu nombre:", 365);

        g.setColor(Color.WHITE);
        g.fillRoundRect(145, 378, 310, 40, 10, 10);
        g.setColor(new Color(80, 80, 200));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(145, 378, 310, 40, 10, 10);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(nombreJugador + "|", 160, 404);

        g.setColor(new Color(150, 220, 150));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        centrarTexto(g, "INTRO para guardar  |  R para reiniciar", 445);
    }

    private void dibujarImagen(Graphics2D g, Image imagen, int x, int y, int ancho, int alto, Color colorReserva) {
        if (imagen != null) {
            g.drawImage(imagen, x, y, ancho, alto, null);
        } else {
            g.setColor(colorReserva);
            g.fillRoundRect(x, y, ancho, alto, 10, 10);
        }
    }

    private void centrarTexto(Graphics2D g, String texto, int y) {
        FontMetrics medidas = g.getFontMetrics();
        int x = (ANCHO - medidas.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }

    @Override
    public void keyPressed(KeyEvent evento) {
        int tecla = evento.getKeyCode();

        if (!enJuego && !partidaTerminada && tecla == KeyEvent.VK_SPACE) {
            iniciarPartida();
            return;
        }

        if (enJuego) {
            if (tecla == KeyEvent.VK_LEFT)  moverIzquierda = true;
            if (tecla == KeyEvent.VK_RIGHT) moverDerecha   = true;
        }

        if (partidaTerminada && mostrarFormulario) {
            if (tecla == KeyEvent.VK_ENTER) {
                guardarPuntuacion();
            } else if (tecla == KeyEvent.VK_BACK_SPACE && nombreJugador.length() > 0) {
                nombreJugador = nombreJugador.substring(0, nombreJugador.length() - 1);
                repaint();
            } else if (tecla == KeyEvent.VK_R) {
                nombreJugador = "";
                iniciarPartida();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent evento) {
        int tecla = evento.getKeyCode();
        if (tecla == KeyEvent.VK_LEFT)  moverIzquierda = false;
        if (tecla == KeyEvent.VK_RIGHT) moverDerecha   = false;
    }

    @Override
    public void keyTyped(KeyEvent evento) {
        if (partidaTerminada && mostrarFormulario) {
            char caracter = evento.getKeyChar();
            if (Character.isLetterOrDigit(caracter) || caracter == ' ') {
                if (nombreJugador.length() < 15) {
                    nombreJugador += caracter;
                    repaint();
                }
            }
        }
    }


    private void guardarPuntuacion() {
        if (nombreJugador.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Escribe tu nombre antes de guardar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean guardado = GestorBaseDatos.guardarPartida(
                nombreJugador.trim(), puntuacion, (int) tiempoTotal
        );

        if (guardado) {
            JOptionPane.showMessageDialog(this,
                    "Partida guardada correctamente!\n"
                            + "Nombre: " + nombreJugador + "\n"
                            + "Puntos: " + puntuacion,
                    "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la partida.\n"
                            + "Comprueba la conexion con la base de datos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        nombreJugador     = "";
        enJuego           = false;
        partidaTerminada  = false;
        repaint();
    }


    public static void main(String[] argumentos) {
        JFrame ventana = new JFrame("Caza Frutas");
        JuegoCazaFrutas juego = new JuegoCazaFrutas();
        ventana.add(juego);
        ventana.pack();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.setResizable(false);
        ventana.setVisible(true);
        juego.requestFocusInWindow();
    }
}
