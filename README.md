# Sistema de Controle de Estoque com Reposição Automática

Projeto da Unidade 2 — Estrutura de Dados (EDA/LEDA 2026.1).

Sistema em Java que integra **quatro estruturas de dados implementadas
manualmente** (sem uso de `java.util.LinkedList`, `java.util.Stack`,
`java.util.PriorityQueue` ou `java.util.HashMap` nas estruturas do
projeto) para controlar um estoque de produtos com reposição
automática quando o nível cai abaixo do mínimo definido.

## 1. Domínio escolhido

**Sistema de Controle de Estoque com Reposição Automática**

| Estrutura | Papel no sistema |
|---|---|
| **Fila** (implementada com **duas Pilhas**) | Pedidos de compra/reposição a processar em ordem de chegada (FIFO). |
| **Heap Binária (min-heap)** | Produtos com estoque mais baixo sobem na prioridade de reposição (topo da heap = mais urgente). |
| **Árvore AVL** | Catálogo de produtos indexado por **código** — busca, inserção e remoção em O(log n) mesmo no pior caso. |
| **Tabela Hash (chaining)** | Busca rápida de produto por **nome**, com colisões tratadas por encadeamento usando a Lista Simplesmente Encadeada do grupo. |

## 2. Arquitetura / pacotes

```
com.estoque
├── estruturas
│   ├── lista   -> No<T>, ListaSimplesmenteEncadeada<T>          (base de tudo)
│   ├── pilha   -> Pilha<T>                (implementada com ListaSimplesmenteEncadeada)
│   ├── fila    -> Fila<T>                 (implementada com DUAS Pilha<T>)
│   ├── avl     -> NoAVL<K,V>, ArvoreAVL<K,V>
│   ├── heap    -> MinHeap<T>
│   └── hash    -> TabelaHash<K,V>         (baldes = ListaSimplesmenteEncadeada)
├── modelo      -> Produto, PedidoCompra, StatusPedido
├── servico     -> SistemaEstoque           (orquestra as 4 estruturas)
├── util        -> Log                      (logging das operações internas)
└── app         -> Main                     (menu interativo de console)
```

### Dependência entre estruturas (conforme exigido pelo enunciado)

```
ListaSimplesmenteEncadeada
        │
        ├──> Pilha (empilhar = inserirInicio, desempilhar = removerInicio)
        │        │
        │        └──> Fila (usa EXCLUSIVAMENTE duas Pilhas: pilhaEntrada / pilhaSaida)
        │
        └──> TabelaHash (cada balde/bucket é uma ListaSimplesmenteEncadeada<Entrada<K,V>>)
```

A `ArvoreAVL` e a `MinHeap` são estruturas independentes (árvore ligada
por nós e heap baseada em array, respectivamente), como é usual para
essas estruturas.

## 3. Logs das operações internas

Cada estrutura registra no console (prefixo `[LOG-<MODULO>]`) suas
operações principais: `push`/`pop` da pilha, inserção/remoção na fila,
inserção/remoção/**colisão** na tabela hash, inserção/remoção/**rotações**
na AVL, e inserção/extração/**sift-up/sift-down** na heap. Isso pode ser
ligado/desligado em tempo de execução pelo menu do `Main` (opção 12) ou
programaticamente via `Log.ativar()` / `Log.desativar()` (usado nos
testes para não poluir a saída).

## 4. Como compilar e executar

Pré-requisitos: **JDK 17+** e **Maven 3.8+**.

```bash
# Compilar
mvn compile

# Rodar os testes JUnit 5
mvn test

# Executar o sistema (menu interativo de console)
mvn compile exec:java -Dexec.mainClass="com.estoque.app.Main"

# Ou gerar o JAR executável e rodar
mvn package
java -jar target/estoque-projeto.jar
```

> Caso o plugin `exec-maven-plugin` não esteja configurado no seu
> ambiente, basta rodar a classe compilada diretamente:
> `java -cp target/classes com.estoque.app.Main`

## 5. Justificativa das estruturas escolhidas

- **Fila com duas Pilhas** para os pedidos de compra: o domínio exige
  processamento **estritamente em ordem de chegada** (o primeiro pedido
  registrado deve ser o primeiro atendido). Implementar a fila em cima
  de duas pilhas (que por sua vez usam a lista encadeada) cumpre o
  requisito do enunciado e mantém custo amortizado O(1) por operação.

- **Min-Heap** para priorização de reposição: entre Heap e AVL, a Heap
  foi escolhida para essa funcionalidade porque o requisito é apenas
  "obter rapidamente o produto/produtos mais críticos" (o de menor
  estoque), não uma ordenação total nem busca por chave arbitrária — a
  Heap resolve isso em O(1) para consultar o mínimo e O(log n) para
  inserir/atualizar, sem o overhead de manter toda a árvore balanceada
  por uma chave que muda constantemente (estoque). Usar a AVL para
  isso exigiria reindexação (remover/reinserir) a cada venda, o que é
  mais custoso quando o único interesse é "o mínimo atual".

- **Árvore AVL** para o catálogo por código: o código do produto é uma
  chave estável (não muda) e o sistema precisa de busca, inserção e
  remoção eficientes por essa chave, além de conseguir listar o
  catálogo inteiro **ordenado**. A AVL garante O(log n) garantido
  (pior caso) para essas três operações e ainda fornece o percurso em
  ordem "de graça".

- **Tabela Hash (chaining)** para busca por nome: nomes não têm uma
  ordem "natural" relevante para o sistema (não precisamos listar por
  nome ordenado com frequência) e o padrão de acesso é "eu sei o nome,
  quero o produto rápido" — a Tabela Hash entrega O(1) médio, mais
  rápido que os O(log n) que uma segunda AVL indexada por nome
  ofereceria. Poderíamos ter usado uma segunda AVL indexada por nome
  em vez da Tabela Hash, mas isso não atenderia ao requisito explícito
  do projeto de ter uma Tabela Hash com chaining, e teria custo maior
  para o caso de uso (busca simples por chave exata).

## 6. Análise de complexidade (custos assintóticos)

| Estrutura | Operação | Complexidade | Observação |
|---|---|---|---|
| Lista Simplesmente Encadeada | inserirInicio / removerInicio | O(1) | mantém ponteiro de início |
| | inserirFim | O(1) | mantém ponteiro de fim |
| | remover(valor) / contém | O(n) | busca linear |
| Pilha (sobre lista) | empilhar / desempilhar / topo | O(1) | sempre opera no início da lista |
| Fila (duas pilhas) | enfileirar | O(1) | push na pilha de entrada |
| | desenfileirar / espiar | O(1) amortizado | cada elemento migra de uma pilha para outra no máximo uma vez |
| Árvore AVL | buscar / inserir / remover | O(log n) | garantido mesmo no pior caso, graças às rotações de balanceamento |
| | emOrdem (listar catálogo) | O(n) | percurso completo |
| | rotação (simples ou dupla) | O(1) | rearranjo local de ponteiros |
| Heap Binária (min-heap) | inserir | O(log n) | sift-up |
| | extrairMinimo | O(log n) | sift-down |
| | espiarMinimo | O(1) | acesso direto à raiz (índice 0) |
| | atualizar(item) | O(log n) | sift-up ou sift-down a partir da posição conhecida (mapa auxiliar) |
| | obterMenores(k) / heapsort | O(n log n) | usa cópia; não altera a heap original |
| Tabela Hash (chaining) | inserir / buscar / remover | O(1) médio, O(n) pior caso | pior caso só ocorre com muitas colisões; fator de carga controlado (≤ 0.75) com redimensionamento automático |
| | redimensionar (rehash) | O(n) | ocorre O(log n) vezes no total → custo amortizado O(1) por inserção |

### Por que a AVL garante O(log n) e uma BST comum não

Uma BST sem balanceamento pode degenerar em uma lista (ex.: inserção
de códigos em ordem crescente), levando busca/inserção/remoção a
O(n). A AVL evita isso mantendo, para todo nó, o **fator de
balanceamento** (diferença de altura entre subárvore esquerda e
direita) sempre em `{-1, 0, 1}`, restaurado via rotações simples ou
duplas após cada inserção/remoção — garantindo altura O(log n) e,
consequentemente, todas as operações principais em O(log n) no pior
caso. O teste `insercaoSequencialCrescenteDeveManterArvoreBalanceada`
verifica esse comportamento na prática.

## 7. Testes

Testes JUnit 5 cobrindo casos normais e de borda (estrutura vazia,
elemento inexistente, colisões, rotações AVL, FIFO da fila, ordem da
heap, geração/duplicação de pedidos automáticos, etc.) para cada
estrutura e para o `SistemaEstoque` (testes de integração):

```
src/test/java/com/estoque/
├── estruturas/lista/ListaSimplesmenteEncadeadaTest.java
├── estruturas/pilha/PilhaTest.java
├── estruturas/fila/FilaTest.java
├── estruturas/avl/ArvoreAVLTest.java
├── estruturas/heap/MinHeapTest.java
├── estruturas/hash/TabelaHashTest.java
└── servico/SistemaEstoqueTest.java
```