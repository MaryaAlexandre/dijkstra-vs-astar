import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

// Labirinto com Dijkstra e A*
// 0 = vazio, 1 = parede, 2 = partida, 3 = saida
public class Labirinto {

    static class No {
        int linha, coluna;
        int g, h, f;
        long ordem; // desempate: quem entrou primeiro na fila
        No pai;

        No(int linha, int coluna) {
            this.linha = linha;
            this.coluna = coluna;
        }
    }

    static class Resultado {
        List<No> caminho;
        int visitados;
        long tempoNs;
    }

    static int[][] mapa;
    static int linhas, colunas;
    static long contador = 0;
    static int[] partida;
    static List<int[]> saidas = new ArrayList<>();

    // movimentos: cima, baixo, esquerda, direita (custo 1 cada)
    static final int[] DL = {0, 1, 0, -1};
    static final int[] DC = {1, 0, -1, 0};

    // le o arquivo .dat (cada linha e uma sequencia de digitos)
    static void lerArquivo(String nome) throws IOException {
        List<String> texto = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nome))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    texto.add(linha);
                }
            }
        }

        linhas = texto.size();
        colunas = texto.get(0).length();
        mapa = new int[linhas][colunas];

        for (int l = 0; l < linhas; l++) {
            if (texto.get(l).length() != colunas) {
                throw new IOException("Linha " + (l + 1) + " com tamanho diferente");
            }
            for (int c = 0; c < colunas; c++) {
                int valor = texto.get(l).charAt(c) - '0';
                mapa[l][c] = valor;
                if (valor == 2) {
                    partida = new int[]{l, c};
                } else if (valor == 3) {
                    saidas.add(new int[]{l, c});
                }
            }
        }

        if (partida == null) {
            throw new IOException("Nao tem ponto de partida (2)");
        }
        if (saidas.isEmpty()) {
            throw new IOException("Nao tem saida (3)");
        }
    }

    static boolean ehSaida(int l, int c) {
        return mapa[l][c] == 3;
    }

    // H: menor distancia de Manhattan ate alguma das saidas
    static int heuristica(int l, int c) {
        int menor = Integer.MAX_VALUE;
        for (int[] s : saidas) {
            int d = Math.abs(l - s[0]) + Math.abs(c - s[1]);
            if (d < menor) {
                menor = d;
            }
        }
        return menor;
    }

    static List<No> montarCaminho(No fim) {
        List<No> caminho = new ArrayList<>();
        No n = fim;
        while (n != null) {
            caminho.add(n);
            n = n.pai;
        }
        Collections.reverse(caminho);
        return caminho;
    }

    // Dijkstra: escolhe sempre o menor G
    static Resultado dijkstra() {
        Resultado r = new Resultado();
        long inicio = System.nanoTime();

        No[][] nos = new No[linhas][colunas];
        boolean[][] fechado = new boolean[linhas][colunas];
        PriorityQueue<No> aberta = new PriorityQueue<>((a, b) -> a.g != b.g ? a.g - b.g : Long.compare(a.ordem, b.ordem));

        No origem = new No(partida[0], partida[1]);
        origem.ordem = contador++;
        nos[origem.linha][origem.coluna] = origem;
        aberta.add(origem);

        while (!aberta.isEmpty()) {
            No atual = aberta.poll();
            if (fechado[atual.linha][atual.coluna]) {
                continue;
            }
            fechado[atual.linha][atual.coluna] = true;
            r.visitados++;

            if (ehSaida(atual.linha, atual.coluna)) {
                r.caminho = montarCaminho(atual);
                break;
            }

            for (int i = 0; i < 4; i++) {
                int l = atual.linha + DL[i];
                int c = atual.coluna + DC[i];
                if (l < 0 || l >= linhas || c < 0 || c >= colunas) {
                    continue;
                }
                if (mapa[l][c] == 1 || fechado[l][c]) {
                    continue;
                }

                int novoG = atual.g + 1;
                No viz = nos[l][c];
                if (viz == null || novoG < viz.g) {
                    viz = new No(l, c);
                    viz.g = novoG;
                    viz.ordem = contador++;
                    viz.pai = atual;
                    nos[l][c] = viz;
                    aberta.add(viz);
                }
            }
        }

        r.tempoNs = System.nanoTime() - inicio;
        return r;
    }

    // A*: escolhe sempre o menor F = G + H
    static Resultado aEstrela() {
        Resultado r = new Resultado();
        long inicio = System.nanoTime();

        No[][] nos = new No[linhas][colunas];
        boolean[][] fechado = new boolean[linhas][colunas];
        PriorityQueue<No> aberta = new PriorityQueue<>((a, b) -> a.f != b.f ? a.f - b.f : a.h != b.h ? a.h - b.h : Long.compare(a.ordem, b.ordem));

        No origem = new No(partida[0], partida[1]);
        origem.h = heuristica(origem.linha, origem.coluna);
        origem.f = origem.h;
        origem.ordem = contador++;
        nos[origem.linha][origem.coluna] = origem;
        aberta.add(origem);

        while (!aberta.isEmpty()) {
            No atual = aberta.poll();
            if (fechado[atual.linha][atual.coluna]) {
                continue;
            }
            fechado[atual.linha][atual.coluna] = true;
            r.visitados++;

            if (ehSaida(atual.linha, atual.coluna)) {
                r.caminho = montarCaminho(atual);
                break;
            }

            for (int i = 0; i < 4; i++) {
                int l = atual.linha + DL[i];
                int c = atual.coluna + DC[i];
                if (l < 0 || l >= linhas || c < 0 || c >= colunas) {
                    continue;
                }
                if (mapa[l][c] == 1 || fechado[l][c]) {
                    continue;
                }

                int novoG = atual.g + 1;
                No viz = nos[l][c];
                if (viz == null || novoG < viz.g) {
                    viz = new No(l, c);
                    viz.g = novoG;
                    viz.h = heuristica(l, c);
                    viz.f = novoG + viz.h;
                    viz.ordem = contador++;
                    viz.pai = atual;
                    nos[l][c] = viz;
                    aberta.add(viz);
                }
            }
        }

        r.tempoNs = System.nanoTime() - inicio;
        return r;
    }

    static void mostrar(Resultado r) {
        boolean[][] noCaminho = new boolean[linhas][colunas];
        if (r.caminho != null) {
            for (No n : r.caminho) {
                noCaminho[n.linha][n.coluna] = true;
            }
        }
        for (int l = 0; l < linhas; l++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < colunas; c++) {
                char ch;
                if (mapa[l][c] == 1) {
                    ch = '#';
                } else if (mapa[l][c] == 2) {
                    ch = 'S';
                } else if (mapa[l][c] == 3) {
                    ch = 'E';
                } else if (noCaminho[l][c]) {
                    ch = '*';
                } else {
                    ch = '.';
                }
                sb.append(ch).append(' ');
            }
            System.out.println(sb);
        }
    }

    static void imprimir(String nome, Resultado r) {
        System.out.println("=== " + nome + " ===");
        if (r.caminho == null) {
            System.out.println("Nao existe caminho");
        } else {
            System.out.println("Passos: " + (r.caminho.size() - 1));
            System.out.println("Quadrados visitados: " + r.visitados);
            System.out.println("Tempo: " + r.tempoNs + " ns (" + String.format("%.3f", r.tempoNs / 1000000.0) + " ms)");
            System.out.println();
            mostrar(r);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "labirinto.dat";

        try {
            lerArquivo(arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return;
        }

        // roda varias vezes antes pra JVM "esquentar" e o tempo ficar justo
        for (int i = 0; i < 200; i++) {
            dijkstra();
            aEstrela();
        }

        Resultado d = dijkstra();
        Resultado a = aEstrela();

        imprimir("Dijkstra", d);
        imprimir("A*", a);

        System.out.println("=== Comparacao ===");
        System.out.println("Visitados: Dijkstra " + d.visitados + " x A* " + a.visitados);
        System.out.println("Tempo (ns): Dijkstra " + d.tempoNs + " x A* " + a.tempoNs);
    }
}
