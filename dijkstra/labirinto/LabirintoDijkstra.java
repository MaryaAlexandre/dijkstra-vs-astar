// Roda o Dijkstra.java (matriz de custo) no labirinto do vídeo
// Compilar: javac -d out ../Dijkstra.java LabirintoDijkstra.java
// Rodar:    java -cp out LabirintoDijkstra
import java.util.ArrayList;
import java.util.Arrays;

public class LabirintoDijkstra {

    // labirinto gerado por labirintos_e_comparativos.py (semente 52)
    // # parede, . livre, S entrada, E tesouro
    static final String[] MAPA = {
        "#####################",
        "#S....#.............#",
        "#####.###.#.#######.#",
        "#...#...#.#.....#.#.#",
        "#.#####.#######.#.#.#",
        "#.#.....#.......#...#",
        "#.#.#####.#.#.###.#.#",
        "#.#...#.........#...#",
        "#.#.#.###.#.#.#.###.#",
        "#.....#...#.....#...#",
        "#.#####.#########.###",
        "#.......#..........E#",
        "#####################"
    };

    public static void main(String[] args) {
        int linhas = MAPA.length, colunas = MAPA[0].length();
        int n = linhas * colunas;                    // cada quadrado é um vértice
        int origem = -1, destino = -1;

        // matriz de custo: 1 entre vizinhos livres, INF no resto
        int[][] custo = new int[n][n];
        for (int[] linha : custo) Arrays.fill(linha, Dijkstra.INF);

        int[] dl = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                char ch = MAPA[l].charAt(c);
                if (ch == '#') continue;
                int v = l * colunas + c;
                if (ch == 'S') origem = v;
                if (ch == 'E') destino = v;
                for (int i = 0; i < 4; i++) {
                    int a = l + dl[i], b = c + dc[i];
                    if (a < 0 || a >= linhas || b < 0 || b >= colunas) continue;
                    if (MAPA[a].charAt(b) != '#') custo[v][a * colunas + b] = 1;
                }
            }
        }

        // busca
        int[] antecessor = new int[n];
        int[] distancia = Dijkstra.buscar(custo, origem, antecessor);
        ArrayList<Integer> caminho = Dijkstra.caminho(antecessor, destino);

        // desenha o caminho
        boolean[] noCaminho = new boolean[n];
        for (int v : caminho) noCaminho[v] = true;
        for (int l = 0; l < linhas; l++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < colunas; c++) {
                char ch = MAPA[l].charAt(c);
                if (ch == '.' && noCaminho[l * colunas + c]) ch = '*';
                sb.append(ch).append(' ');
            }
            System.out.println(sb);
        }
        System.out.println("Passos do caminho: " + distancia[destino]);
        System.out.println("Quadrados no caminho: " + caminho.size());
    }
}
