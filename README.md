Sistema de Transação Distribuída com Two-Phase Commit

Projeto acadêmico desenvolvido em Java para demonstrar conceitos de sistemas distribuídos, com foco em transações distribuídas e no protocolo Two-Phase Commit (2PC).

Descrição

Este projeto simula uma transação distribuída coordenada por um Coordenador e executada por dois participantes (Servidor A e Servidor B).

A comunicação entre os processos é realizada por meio de Sockets TCP, permitindo demonstrar as principais etapas de uma transação distribuída:

PREPARE

COMMIT

ROLLBACK

Comunicação entre processos

Detecção de falha parcial

Timeout e servidor indisponível

O projeto foi desenvolvido como atividade acadêmica para aplicação prática dos conceitos de sistemas distribuídos.

Arquitetura

O sistema é composto por quatro classes principais:

src/
├── Transacao.java
├── Coordenador.java
├── ServidorA.java
└── ServidorB.java

Componentes

Classe

Função

Transacao.java

Representa a transação e seus estados

Coordenador.java

Coordena o protocolo 2PC

ServidorA.java

Primeiro participante da transação

ServidorB.java

Segundo participante da transação

Funcionamento

O protocolo segue duas fases principais.

1. Fase PREPARE

O Coordenador envia uma solicitação PREPARE para os participantes.

                 Coordenador
                     |
              PREPARE
                /     \
               /       \
              v         v
       Servidor A   Servidor B
            |             |
           OK            OK

Se os dois participantes responderem OK, o Coordenador pode confirmar a transação.

2. Fase COMMIT

Quando todos os participantes estão preparados, o Coordenador envia COMMIT.

Servidor A → COMMIT
Servidor B → COMMIT

Transação confirmada.

3. ROLLBACK

Se algum participante estiver indisponível, responder negativamente ou ocorrer um timeout, o Coordenador envia ROLLBACK.

Servidor A → OK
Servidor B → TIMEOUT

        ↓

     ROLLBACK

Dessa forma, a transação não é confirmada parcialmente.

Comunicação

A comunicação entre o Coordenador e os participantes utiliza:

ServerSocket para receber conexões;

Socket para comunicação TCP;

BufferedReader para receber mensagens;

PrintWriter para enviar mensagens;

timeout para detectar participantes indisponíveis.

As portas utilizadas são:

Servidor A → 5001
Servidor B → 5002

Tecnologias utilizadas

Java

Java Sockets

TCP/IP

Two-Phase Commit (2PC)

Programação Orientada a Objetos

Pré-requisitos

Para executar o projeto, é necessário ter o Java JDK instalado.

Verifique a instalação com:

java -version
javac -version

Como executar

1. Clonar o repositório

git clone URL_DO_SEU_REPOSITORIO
cd NOME_DO_REPOSITORIO

2. Compilar

Na pasta onde estão os arquivos .java:

javac *.java

3. Iniciar o Servidor A

Abra um terminal e execute:

java ServidorA

O servidor ficará aguardando conexões na porta 5001.

4. Iniciar o Servidor B

Abra outro terminal e execute:

java ServidorB

O servidor ficará aguardando conexões na porta 5002.

5. Iniciar o Coordenador

Abra um terceiro terminal e execute:

java Coordenador

O Coordenador iniciará a transação e executará o protocolo 2PC.

Exemplo de execução com sucesso

Quando os dois participantes estiverem disponíveis:

[COORDENADOR] Enviando PREPARE para porta 5001
[COORDENADOR] Participante 5001 respondeu OK.

[COORDENADOR] Enviando PREPARE para porta 5002
[COORDENADOR] Participante 5002 respondeu OK.

Todos os participantes responderam OK.
Enviando COMMIT...

[COORDENADOR] COMMIT enviado para 5001
[COORDENADOR] COMMIT enviado para 5002

[TRANSACAO] Transacao confirmada.

Estado final: COMMIT

Simulação de falha parcial

Para demonstrar o conceito de falha parcial, execute somente o ServidorA e o Coordenador.

Não inicie o ServidorB.

Nesse cenário, o Coordenador conseguirá se comunicar com o participante A, mas não encontrará o participante B.

Exemplo:

[COORDENADOR] Enviando PREPARE para porta 5001
[COORDENADOR] Participante 5001 respondeu OK.

[COORDENADOR] Enviando PREPARE para porta 5002
[COORDENADOR] Participante 5002 indisponivel.

Falha detectada.
Enviando ROLLBACK...

[TRANSACAO] Transacao desfeita.

Estado final: ROLLBACK

Esse teste demonstra como o sistema reage quando um participante da transação distribuída fica indisponível.

Fluxo do projeto

                    ┌─────────────────┐
                    │   Coordenador   │
                    └────────┬────────┘
                             │
                         PREPARE
                       ┌─────┴─────┐
                       ↓           ↓
                ┌───────────┐ ┌───────────┐
                │ Servidor A│ │ Servidor B│
                │  :5001    │ │  :5002    │
                └─────┬─────┘ └─────┬─────┘
                      │              │
                     OK             OK
                       └──────┬───────┘
                              ↓
                            COMMIT
                              │
                              ↓
                       Transação concluída

Em caso de falha:

                    Coordenador
                         │
                     PREPARE
                    /       \
                   ↓         ↓
             Servidor A   Servidor B
                  │           X
                 OK         TIMEOUT
                   \         /
                    \       /
                     ↓     ↓
                     ROLLBACK

Objetivos acadêmicos

O projeto tem como objetivo demonstrar, na prática:

O conceito de transação distribuída;

O papel de um coordenador;

A comunicação entre processos por sockets;

A fase PREPARE;

A decisão COMMIT;

A decisão ROLLBACK;

O tratamento de falhas parciais;

O uso de timeout para detectar indisponibilidade.

Observação

Este projeto possui finalidade didática e acadêmica. A implementação é uma simplificação do protocolo Two-Phase Commit e não pretende reproduzir todas as garantias, mecanismos de recuperação e características de uma implementação de produção.

