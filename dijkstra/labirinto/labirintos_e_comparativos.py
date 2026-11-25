# Mapas, algoritmos e dados do vídeo A* vs Dijkstra
# Rodar: python labirintos_e_comparativos.py
import heapq, random, itertools, sys
sys.setrecursionlimit(10000)

# ---------- vizinhos e heurística
def vizinhos(c, grid):
    x, y = c; H = len(grid); W = len(grid[0])
    for dx, dy in ((1, 0), (0, 1), (-1, 0), (0, -1)):   # 4 direções, sem diagonal
        nx, ny = x+dx, y+dy
        if 0 <= nx < W and 0 <= ny < H and not grid[ny][nx]: yield (nx, ny)
def manhattan(a, b): return abs(a[0]-b[0]) + abs(a[1]-b[1])   # h(n): ignora paredes

# ---------- busca (Dijkstra = A* com h = 0)
def buscar(grid, ini, fim, astar):
    cnt = itertools.count(); g = {ini: 0}; pai = {ini: None}
    h0 = manhattan(ini, fim) if astar else 0
    fila = [(g[ini]+h0, h0, next(cnt), ini)]      # (f, h, desempate, nó)
    ordem = []; fechados = set()
    while fila:
        f, h, _, c = heapq.heappop(fila)           # abre o menor f
        if c in fechados: continue
        fechados.add(c); ordem.append(c)           # ordem = nós explorados (é o que anima)
        if c == fim: break
        for n in vizinhos(c, grid):
            ng = g[c]+1
            if n not in g or ng < g[n]:
                g[n] = ng; pai[n] = c
                hn = manhattan(n, fim) if astar else 0
                heapq.heappush(fila, (ng+hn, hn, next(cnt), n))
    caminho = []; c = fim
    while c is not None: caminho.append(c); c = pai[c]
    caminho.reverse()
    return {'ordem': ordem, 'caminho': caminho, 'g': g}

# ---------- MAPA 1: espaço aberto com obstáculo em C (25 x 17)
def mapa_obstaculo(W=25, H=17):
    g = [[0]*W for _ in range(H)]
    for y in range(4, 13): g[y][14] = 1                       # barra vertical de frente para o início
    for x in range(14, 21): g[4][x] = 1; g[12][x] = 1         # braços abrindo para o leste
    return g
M1_INI, M1_FIM = (4, 8), (18, 8)

# ---------- MAPA 2: labirinto (21 x 13), gerado por semente
def gerar_labirinto(semente, W=21, H=13, atalhos=6):
    rnd = random.Random(semente); g = [[1]*W for _ in range(H)]
    def abrir(cx, cy):                                         # backtracker recursivo
        g[cy][cx] = 0
        dirs = [(2, 0), (-2, 0), (0, 2), (0, -2)]; rnd.shuffle(dirs)
        for dx, dy in dirs:
            nx, ny = cx+dx, cy+dy
            if 0 < nx < W-1 and 0 < ny < H-1 and g[ny][nx]:
                g[cy+dy//2][cx+dx//2] = 0; abrir(nx, ny)
    abrir(1, 1)
    # paredes quebradas ao acaso criam ciclos (vários caminhos possíveis)
    cand = [(x, y) for y in range(1, H-1) for x in range(1, W-1) if g[y][x] and ((x % 2 == 0) != (y % 2 == 0))]
    rnd.shuffle(cand)
    for (x, y) in cand[:atalhos]: g[y][x] = 0
    return g
M2_INI, M2_FIM = (1, 1), (19, 11)
SEMENTE = 52   # escolhida testando as sementes 0 a 199: A*/Dijkstra entre 0,80 e 0,95 e caminho >= 40

# ---------- MAPAS DAS CENAS DO A* (explicação de f = g + h)
def mapa_aberto():                                             # 13 x 9, sem paredes
    return [[0]*13 for _ in range(9)]
def mapa_armadilha():                                          # 13 x 9, parede em x = 6 (linhas 0 a 6)
    g = [[0]*13 for _ in range(9)]
    for y in range(0, 7): g[y][6] = 1
    return g
ARM_INI, ARM_FIM, ARM_X = (1, 4), (11, 4), (5, 4)             # X: ponto "enganoso" colado na parede

def desenhar(grid, ini, fim):
    for y, linha in enumerate(grid):
        print("  " + "".join("S" if (x, y) == ini else "G" if (x, y) == fim else "#" if v else "." for x, v in enumerate(linha)))

def comparar(nome, grid, ini, fim):
    d = buscar(grid, ini, fim, False); a = buscar(grid, ini, fim, True)
    print(f"\n{nome}: {len(grid[0])}x{len(grid)}  início={ini}  destino={fim}")
    desenhar(grid, ini, fim)
    print("  nós explorados  Dijkstra:", len(d['ordem']), " A*:", len(a['ordem']))
    print("  caminho (passos) Dijkstra:", len(d['caminho'])-1, " A*:", len(a['caminho'])-1)
    print("  matriz (1 = parede):", grid)
    return d, a

if __name__ == '__main__':
    comparar("MAPA 1  espaço aberto com obstáculo", mapa_obstaculo(), M1_INI, M1_FIM)
    comparar(f"MAPA 2  labirinto (semente {SEMENTE})", gerar_labirinto(SEMENTE), M2_INI, M2_FIM)

    # cena do palpite: h é estimativa, não é distância real
    arm = mapa_armadilha(); real = buscar(arm, ARM_X, ARM_FIM, False)
    g_x = manhattan(ARM_INI, ARM_X); h_x = manhattan(ARM_X, ARM_FIM)
    print(f"\nARMADILHA: no ponto X={ARM_X}  g={g_x}  h={h_x}  f={g_x+h_x}")
    print(f"  h (estimativa, ignora a parede) = {h_x}   distância real até o destino = {len(real['caminho'])-1}")
    desenhar(arm, ARM_INI, ARM_FIM)

    # como a animação foi montada: a cada quadro revela os primeiros k nós de 'ordem'
    # (k = (tempo - início) * ritmo), igual nos dois painéis, e no fim desenha o 'caminho'
    d = buscar(mapa_obstaculo(), M1_INI, M1_FIM, False)
    print("\nPrimeiros 8 nós explorados (Dijkstra, mapa 1):", d['ordem'][:8])
