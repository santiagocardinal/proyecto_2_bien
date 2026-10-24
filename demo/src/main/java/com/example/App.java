package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.modelo.Posteo;
import com.example.modelo.Usuario;
import com.example.plataforma.PlataformaRedSocial;

public class App {

    public static void main(String[] args) {

        PlataformaRedSocial red = new PlataformaRedSocial();

        System.out.println("\n1. Registro de usuarios\n======================================================================\n");

        // Grupo 1
        Usuario ana = new Usuario("ana");
        Usuario bruno = new Usuario("bruno");
        Usuario carla = new Usuario("carla");
        Usuario diego = new Usuario("diego");

        // Grupo 2
        Usuario eva = new Usuario("eva");
        Usuario fede = new Usuario("fede");
        Usuario gabi = new Usuario("gabi");
        Usuario hugo = new Usuario("hugo");

        // Une a los dos grupos
        Usuario lucia = new Usuario("lucia");

        Usuario[] todos = { ana, bruno, carla, diego, eva, fede, gabi, hugo, lucia };
        for (Usuario u : todos) {
            red.registrarUsuario(u);
        }

        System.out.println("Usuarios registrados: " + red.obtenerUsuarios());

        System.out.println("\n2. Seguidores\n======================================================================\n");

        // Grupo 1: se siguen en ronda (ana -> bruno -> carla -> diego -> ana)
        red.seguirUsuario(ana, bruno);
        red.seguirUsuario(bruno, carla);
        red.seguirUsuario(carla, diego);
        red.seguirUsuario(diego, ana);
        red.seguirUsuario(bruno, ana);

        // Grupo 2: también en ronda (eva -> fede -> gabi -> hugo -> eva)
        red.seguirUsuario(eva, fede);
        red.seguirUsuario(fede, gabi);
        red.seguirUsuario(gabi, hugo);
        red.seguirUsuario(hugo, eva);

        // Lucia es el puente: carla -> lucia -> eva
        red.seguirUsuario(carla, lucia);
        red.seguirUsuario(lucia, eva);

        System.out.println("Seguidores de ana: " + red.obtenerSeguidores(ana)); // bruno y diego
        System.out.println("Seguidos de bruno: " + red.obtenerSeguidos(bruno)); // carla y ana

        System.out.println("\n3. Posteos, comentarios y menciones\n======================================================================\n");

        // Ana menciona a fede, que es del otro grupo y no la sigue
        Posteo p1 = red.publicar(ana, "Hola a todos! Les presento a @fede");
        System.out.println("Publicado: " + p1);

        // Carla comenta y menciona a hugo
        red.comentar(carla, p1, "Que bueno! @hugo mira esto");
        System.out.println("Carla comento el posteo y menciono a hugo");

        Posteo p2 = red.publicar(fede, "Partido de futbol el sabado");
        System.out.println("Publicado: " + p2);

        System.out.println("\n4. Compartir\n======================================================================\n");

        // A fede le llego p1 porque lo mencionaron, asi que puede compartirlo
        boolean fedeComparte = red.compartir(fede, p1);
        System.out.println("fede comparte el posteo de ana: " + fedeComparte); // true

        // A gabi no le llego p1, asi que no puede compartirlo
        boolean gabiComparte = red.compartir(gabi, p1);
        System.out.println("gabi comparte el posteo de ana: " + gabiComparte); // false

        System.out.println("\n5. Feed y alcance\n======================================================================\n");

        // Eva sigue a fede: ve el posteo de fede y el que fede compartio
        System.out.println("Feed de eva:");
        for (Posteo p : red.obtenerFeed(eva)) {
            System.out.println("  - " + p);
        }

        // p1 llega a: seguidores de ana (bruno, diego), mencionados (fede, hugo)
        // y seguidores de fede, que lo compartio (eva)
        System.out.println("\nAlcance del posteo de ana: " + red.calcularAlcance(p1));

        System.out.println("\n6. Sugerencias y grados de separacion\n======================================================================\n");

        // Ana sigue a bruno, y bruno sigue a carla -> le sugerimos carla
        System.out.println("Sugerencias para ana: " + red.sugerirUsuarios(ana));

        // ana -> bruno -> carla -> lucia -> eva = 4 pasos
        int anaAEva = red.gradosDeSeparacion(ana, eva);
        System.out.println("Pasos de ana hasta eva: " + anaAEva);

        // Nadie del grupo 2 sigue a alguien del grupo 1 -> no hay camino
        int evaAAna = red.gradosDeSeparacion(eva, ana);
        if (evaAAna == -1) {
            System.out.println("No hay camino que conecte a eva con ana");
        } else {
            System.out.println("Pasos de eva hasta ana: " + evaAAna);
        }

        System.out.println("\n7. Analisis de la red\n======================================================================\n");

        // --- Influencia: ranking de mayor a menor cantidad de seguidores ---
        System.out.println("Usuarios mas influyentes (por cantidad de seguidores):");

        Map<Usuario, Integer> influencia = red.calcularInfluencia();
        List<Map.Entry<Usuario, Integer>> ranking = new ArrayList<>(influencia.entrySet());
        ranking.sort((a, b) -> b.getValue() - a.getValue());

        int puesto = 1;
        for (Map.Entry<Usuario, Integer> entrada : ranking) {
            int cantidad = entrada.getValue();
            String palabra = (cantidad == 1) ? "seguidor" : "seguidores";
            System.out.println("  " + puesto + ". " + entrada.getKey() + " -> " + cantidad + " " + palabra);
            puesto++;
        }

        // --- Comunidades: una por línea ---
        System.out.println("\nComunidades detectadas:");

        int numero = 1;
        for (Set<Usuario> comunidad : red.detectarComunidades()) {
            System.out.println("  Comunidad " + numero + ": " + comunidad);
            numero++;
        }

        // --- Usuarios puente ---
        System.out.println("\nUsuarios puente (si se van, la red queda dividida):");

        for (Usuario puente : red.identificarUsuariosPuente()) {
            System.out.println("  - " + puente);
        }

        System.out.println("\n8. Consultas propias\n======================================================================");

        // --- Consulta 1 ---
        consulta(1, "Camino de difusion", "Por donde le llego el posteo de ana a cada usuario?");
        for (Usuario u : red.calcularAlcance(p1)) {
            List<Usuario> camino = red.caminoDeDifusion(p1, u);
            int pasos = camino.size() - 1;
            System.out.println("  " + u + " (" + pasos + " " + (pasos == 1 ? "paso" : "pasos") + "):  " + formatearCamino(camino));
        }
        if (red.caminoDeDifusion(p1, gabi).isEmpty()) {
            System.out.println("  " + gabi + ":  no le llego");
        }

        // --- Consulta 2 ---
        consulta(2, "Centro de cada comunidad", "Quien es el usuario mas cercano a todos en su grupo?");
        int numeroComunidad = 1;
        for (Map.Entry<Set<Usuario>, Usuario> entrada : red.centrosDeComunidades().entrySet()) {
            System.out.println("  Comunidad " + numeroComunidad + ": centro " + entrada.getValue() + "   (integrantes: " + entrada.getKey() + ")");
            numeroComunidad++;
        }

        // --- Consulta 3 ---
        consulta(3, "Circulo de interaccion", "Con quienes esta conectado cada usuario por interacciones?");
        mostrarCirculo(red, carla);
        mostrarCirculo(red, ana);
        mostrarCirculo(red, gabi);

        // --- Consulta 4 ---
        consulta(4, "Grupos de amigos mutuos", "Que usuarios se siguen mutuamente entre si?");
        List<Set<Usuario>> grupos = red.gruposDeAmigosMutuos();
        if (grupos.isEmpty()) {
            System.out.println("  No hay seguimientos mutuos");
        }
        int numeroGrupo = 1;
        for (Set<Usuario> grupo : grupos) {
            System.out.println("  Grupo " + numeroGrupo + ": " + grupo);
            numeroGrupo++;
        }

        // --- Consulta 5 ---
        consulta(5, "Orden de llegada", "En que orden le fue llegando el posteo de ana a cada usuario?");
        int orden = 1;
        for (Usuario u : red.ordenDeLlegada(p1)) {
            System.out.println("  " + orden + ". " + u);
            orden++;
        }
    }

    // Imprime el encabezado de una consulta: numero, nombre y la pregunta que responde
    private static void consulta(int numero, String nombre, String pregunta) {
        System.out.println();
        System.out.println("[Consulta " + numero + "] " + nombre);
        System.out.println("  " + pregunta);
        System.out.println("  ----------------------------------------------------------------");
    }

    // Convierte [ana, fede, eva] en "@ana -> @fede -> @eva"
    private static String formatearCamino(List<Usuario> camino) {
        String texto = "";
        for (int i = 0; i < camino.size(); i++) {
            texto += camino.get(i);
            // La flecha va entre usuarios, no despues del ultimo
            if (i < camino.size() - 1) {
                texto += " -> ";
            }
        }
        return texto;
    }

    // Muestra el circulo de interaccion de un usuario, o avisa si no interactuo con nadie
    private static void mostrarCirculo(PlataformaRedSocial red, Usuario usuario) {
        Set<Usuario> circulo = red.circuloDeInteraccion(usuario);
        if (circulo.isEmpty()) {
            System.out.println("  " + usuario + ": no interactuo con nadie");
        } else {
            System.out.println("  " + usuario + ": " + circulo);
        }
    }
}