<p align="center">
  <img src="assets/capa.png" alt="Dijkstra vs A*: qual algoritmo acha o menor caminho?" width="100%">
</p>

# Dijkstra vs A*: qual algoritmo acha o menor caminho?

Dois exploradores, um labirinto e uma pergunta: como chegar ao tesouro pelo menor caminho?

Neste trabalho a gente explica, de forma visual e passo a passo, como funcionam os algoritmos Dijkstra e A* (A estrela), mostra o código em Java cortado em trechos e compara os dois no mesmo labirinto.

* O **Dijkstra** é o explorador sem bússola: ele guarda quanto já andou desde a entrada (o custo `g`) e se espalha para todos os lados, como uma onda.
* O **A\*** é o explorador com bússola: além do custo `g`, ele usa uma estimativa do que ainda falta (a heurística `h`) e escolhe sempre o quadrado de menor `f = g + h`.

Vídeo no YouTube: (adicionar o link depois da publicação)

## Equipe

| Discente | Parte do trabalho |
| --- | --- |
| Robson Douglas Fernandes | Explica o Dijkstra |
| Victor Paiva Passos | Explica o A* |
| Marya Eduarda Alexandre | Faz a comparação entre os dois |

**Docente:** Robinson Alves

**Disciplina:** Estrutura de Dados Não-Lineares

## Estrutura do repositório

```
.
├── README.md
├── assets/
│   └── capa.png                 capa do vídeo
├── dijkstra/
│   ├── Dijkstra.java            Dijkstra sobre matriz de custo (código do vídeo do Robson)
│   └── labirinto/
│       ├── Labirinto.java       Dijkstra e A* rodando no mesmo labirinto, com a comparação
│       ├── mapa1.dat            mapa aberto, 25 x 17
│       └── mapa2.dat            labirinto com paredes, 21 x 13 (o do vídeo)
└── astar/
    └── AEstrela.java            A* em grade (código do vídeo do Victor)
```

## Composição do trabalho

O vídeo é dividido em quatro partes, e cada parte tem o seu código nesta organização de pastas.

| Parte | Quem apresenta | O que mostra | Onde está o código |
| --- | --- | --- | --- |
| Abertura | Equipe | Apresentação dos três integrantes e do que cada um vai explicar | |
| Dijkstra | Robson | O explorador sem bússola, o custo acumulado, a expansão em onda e o código em 4 trechos | `dijkstra/Dijkstra.java` |
| A* | Victor | O explorador com bússola, a fórmula `f = g + h`, a distância de Manhattan e o código em trechos | `astar/AEstrela.java` |
| Comparação | Marya | Os dois algoritmos no mesmo labirinto, lado a lado, com nós explorados e caminho encontrado | `dijkstra/labirinto/Labirinto.java` |

## Dijkstra (`dijkstra/Dijkstra.java`)

Versão clássica do algoritmo sobre uma **matriz de custo** (`custo[i][j]` guarda o custo da aresta de `i` para `j`, e `INF` quando não existe aresta). Foi o código usado na explicação do Robson.

Estruturas usadas:

* `distancia[v]`: a menor distância conhecida da origem até `v`.
* `antecessor[v]`: de qual vértice se chegou em `v`, para montar o caminho no fim.
* `emS[v]`: indica se o vértice já foi fechado (já pertence ao conjunto S).

O código tem dois métodos:

* `buscar(custo, origem, antecessor)` roda o algoritmo e devolve o vetor de distâncias. Ele tem três etapas:
  1. **Prepara os vetores:** a distância de cada vértice começa com o custo direto a partir da origem; a origem fica com distância 0 e entra em S.
  2. **Escolhe o vértice de menor distância** entre os que estão fora de S. Se essa distância é `INF`, não dá mais para chegar em ninguém e o laço para. Senão, o vértice entra em S.
  3. **Atualiza as distâncias:** para cada vértice fora de S, testa se passar pelo vértice escolhido é mais curto. Se for, atualiza `distancia` e `antecessor`. Não existe nenhuma heurística aqui.
* `caminho(antecessor, destino)` volta de antecessor em antecessor até a origem e monta o caminho.

Essa versão calcula a distância da origem para **todos** os vértices, sem parar ao chegar no destino.

### O labirinto (`dijkstra/labirinto/`)

Esta pasta guarda o que foi usado para montar o labirinto e comparar os dois algoritmos.

* **`Labirinto.java`** lê um arquivo `.dat`, roda o Dijkstra e o A* no mesmo mapa e imprime passos, quadrados visitados e tempo de cada um, além de desenhar o caminho.
* **`mapa1.dat`** e **`mapa2.dat`** são os mapas.

Formato do `.dat`: cada linha é uma sequência de dígitos, e cada dígito é um quadrado.

| Dígito | Significado |
| --- | --- |
| `0` | vazio |
| `1` | parede |
| `2` | partida |
| `3` | saída |

Os mapas:

* `mapa1.dat`: 25 colunas e 17 linhas, um campo aberto com um recinto de paredes no meio. Serve para mostrar a diferença de esforço quando há muito espaço livre.
* `mapa2.dat`: 21 colunas e 13 linhas, um labirinto com corredores e paredes. É o mapa usado nas animações do vídeo.

Como o `Labirinto.java` funciona:

* Movimentos em 4 direções (cima, baixo, esquerda, direita), cada passo com custo 1.
* Usa uma fila de prioridade (`PriorityQueue`) como lista aberta. No Dijkstra a fila ordena por `g`. No A* ordena por `f = g + h`, e desempata por `h` e depois pela ordem de entrada.
* A heurística `h` é a menor distância de Manhattan até alguma das saídas.
* Conta quantos quadrados foram visitados e mede o tempo de cada algoritmo. Antes da medição, roda os dois 200 vezes para a JVM "esquentar" e o tempo ficar justo.

## A* (`astar/AEstrela.java`)

A* em grade (0 = livre, 1 = parede). Foi o código usado na explicação do Victor.

* A classe `No` guarda `linha`, `coluna`, `g`, `h`, `f` e o `pai`.
* O custo de um passo reto é `RETO = 10` e o de uma diagonal é `DIAGONAL = 14`.
* A heurística `h` é a distância de Manhattan até o destino, multiplicada por `RETO`.
* Funcionamento, em três partes principais:
  1. **Escolhe** na lista aberta o quadrado de menor `f`.
  2. **Olha os 8 vizinhos**, ignorando o que está fora do mapa, as paredes e os quadrados já fechados, e calcula o `novoG`.
  3. **Atualiza:** se o vizinho é novo, entra na lista aberta com `g`, `h` e `f`. Se já estava, só é atualizado quando o novo caminho é melhor (troca o pai e atualiza `g` e `f`).
* Ao chegar no destino, segue os pais de trás para frente para montar o caminho.

## Resultados

Rodando o `Labirinto.java` (4 direções, custo 1 por passo):

| Mapa | Algoritmo | Passos | Quadrados visitados |
| --- | --- | --- | --- |
| `mapa1.dat` | Dijkstra | 30 | 392 |
| `mapa1.dat` | A* | 30 | 235 |
| `mapa2.dat` | Dijkstra | 72 | 118 |
| `mapa2.dat` | A* | 72 | 107 |

Nos dois mapas os algoritmos encontram caminhos com o mesmo número de passos, o menor possível. A diferença está no esforço: o A* visita menos quadrados, porque a heurística o puxa para perto da saída em vez de se espalhar para todos os lados. A diferença é maior no mapa aberto e menor no labirinto, onde as paredes limitam o espaço de busca.

O tempo também é impresso pelo programa, mas varia de máquina para máquina e entre execuções, então vale rodar e conferir no seu computador.

## Como compilar e executar

Requisito: JDK instalado.

```bash
# Dijkstra e A* no labirinto, com a comparação
cd dijkstra/labirinto
javac Labirinto.java
java Labirinto mapa2.dat      # ou mapa1.dat

# A* em grade (exemplo próprio dentro do arquivo)
cd ../../astar
javac AEstrela.java
java AEstrela

# Dijkstra sobre matriz de custo (classe com os métodos buscar e caminho)
cd ../dijkstra
javac Dijkstra.java
```

Se nenhum arquivo for informado, o `Labirinto` procura por `labirinto.dat`.

## Conceitos usados

* **`g(n)`**: custo acumulado desde a entrada até o quadrado `n`.
* **`h(n)`**: estimativa do que ainda falta até a saída (aqui, a distância de Manhattan).
* **`f(n) = g(n) + h(n)`**: o valor que o A* usa para escolher o próximo quadrado. O Dijkstra usa apenas `g`.
* **Lista aberta**: quadrados descobertos que ainda podem ser explorados.
* **Lista fechada**: quadrados já explorados.
* **Pai ou antecessor**: de onde se veio, usado para montar o caminho de trás para frente.
