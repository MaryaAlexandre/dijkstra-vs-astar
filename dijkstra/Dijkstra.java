import java.util.ArrayList;

public class Dijkstra {
    public static final int INF = Integer.MAX_VALUE;

    public static int[] buscar(int[][] custo, int origem, int[] antecessor) {
        int n = custo.length;
        int[] distancia = new int[n];
        boolean[] emS = new boolean[n];

        for (int v = 0; v < n; v++) {
            distancia[v] = custo[origem][v];
            antecessor[v] = (custo[origem][v] != INF) ? origem : -1;
        }
        distancia[origem] = 0;
        antecessor[origem] = -1;
        emS[origem] = true;

        for (int i = 1; i < n; i++) {
            int w = -1;
            for (int v = 0; v < n; v++)
                if (!emS[v] && (w == -1 || distancia[v] < distancia[w]))
                    w = v;

            if (distancia[w] == INF)
                break;
            emS[w] = true;

            for (int v = 0; v < n; v++) {
                if (!emS[v] && custo[w][v] != INF && distancia[w] + custo[w][v] < distancia[v]) {
                    distancia[v] = distancia[w] + custo[w][v];
                    antecessor[v] = w;
                }
            }
        }
        return distancia;
    }

    public static ArrayList<Integer> caminho(int[] antecessor, int destino) {
        ArrayList<Integer> caminho = new ArrayList<>();
        for (int v = destino; v != -1; v = antecessor[v])
            caminho.add(0, v);
        return caminho;
    }
}
