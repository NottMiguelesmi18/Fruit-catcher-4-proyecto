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

    private static final int MAX_OBJETOS = 6;
    private int[]     objetoX      = new int[MAX_OBJETOS];
    private int[]     objetoY      = new int[MAX_OBJETOS];
    private int[]     objetoTipo   = new int[MAX_OBJETOS];
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
        imagenJugador = new ImageIcon("src/imagenes/jugador.png").getImage();
        imagenManzana = new ImageIcon("src/imagenes/manzana.png").getImage();
        imagenPinya   = new ImageIcon("src/imagenes/pinya.png").getImage();
        imagenRoca    = new ImageIcon("src/imagenes/roca.png").getImage();
        imagenCorazon = new ImageIcon("src/imagenes/corazon.png").getImage();
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


}
