import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Coordenador da transação distribuída.
 *
 * O coordenador é responsável por controlar os participantes
 * e decidir se a transação será confirmada ou cancelada.
 *
 * Neste projeto é utilizado o conceito de Two-Phase Commit:
 *
 * 1 - PREPARE
 * 2 - COMMIT ou ROLLBACK
 */
public class Coordenador {

    // Endereço dos servidores
    private static final String HOST = "localhost";

    // Portas dos participantes
    private static final int PORTA_A = 5001;
    private static final int PORTA_B = 5002;

    /*
     * Tempo máximo de espera pela resposta de um servidor.
     *
     * 3000 milissegundos = 3 segundos.
     *
     * Caso o servidor não responda nesse tempo,
     * consideramos que ocorreu uma falha parcial.
     */
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("         COORDENADOR");
        System.out.println("=================================");

        // Solicita o valor da transação
        System.out.print("Digite o valor da transação: ");

        double valor = scanner.nextDouble();

        System.out.println("\nIniciando transação...");
        System.out.println("Valor: R$ " + valor);

        /*
         * PRIMEIRA FASE - PREPARE
         *
         * O coordenador pergunta aos dois participantes
         * se eles conseguem realizar a transação.
         */
        System.out.println("\n----- FASE 1: PREPARE -----");

        boolean respostaA = enviarPrepare(PORTA_A, valor);
        boolean respostaB = enviarPrepare(PORTA_B, valor);

        System.out.println("\nResultado do PREPARE:");
        System.out.println("Servidor A: " + respostaA);
        System.out.println("Servidor B: " + respostaB);

        /*
         * SEGUNDA FASE
         *
         * O COMMIT só acontece se os dois participantes
         * responderem READY.
         */
        if (respostaA && respostaB) {

            System.out.println("\n----- FASE 2: COMMIT -----");

            System.out.println(
                    "Todos os participantes estão preparados."
            );

            System.out.println("Enviando COMMIT para os servidores...");

            enviarComando(PORTA_A, "COMMIT");
            enviarComando(PORTA_B, "COMMIT");

            System.out.println("\n=================================");
            System.out.println("      TRANSAÇÃO CONFIRMADA");
            System.out.println("=================================");

        } else {

            /*
             * Se pelo menos um participante falhou,
             * a transação deve ser cancelada.
             */
            System.out.println("\n----- FASE 2: ROLLBACK -----");

            System.out.println(
                    "Um ou mais participantes não estão disponíveis."
            );

            System.out.println("Enviando ROLLBACK...");

            enviarComando(PORTA_A, "ROLLBACK");
            enviarComando(PORTA_B, "ROLLBACK");

            System.out.println("\n=================================");
            System.out.println("       TRANSAÇÃO CANCELADA");
            System.out.println("=================================");
        }

        scanner.close();
    }

    /**
     * Envia o comando PREPARE para um participante.
     *
     * @param porta porta do servidor
     * @param valor valor da transação
     * @return true se o servidor responder READY
     */
    private static boolean enviarPrepare(int porta, double valor) {

        try {

            /*
             * Cria o Socket sem conectar imediatamente.
             * Isso permite definir um tempo limite para a conexão.
             */
            Socket socket = new Socket();

            socket.connect(
                    new InetSocketAddress(HOST, porta),
                    TIMEOUT
            );

            /*
             * Define o tempo máximo para esperar
             * uma resposta do servidor.
             */
            socket.setSoTimeout(TIMEOUT);

            // Cria o canal de entrada
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Cria o canal de saída
            PrintWriter saida = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            // Envia o comando PREPARE
            saida.println("PREPARE " + valor);

            // Aguarda a resposta
            String resposta = entrada.readLine();

            System.out.println(
                    "Servidor na porta " + porta +
                            " respondeu: " + resposta
            );

            // Fecha a conexão
            socket.close();

            /*
             * Somente READY significa que o participante
             * está preparado para realizar o COMMIT.
             */
            return "READY".equals(resposta);

        } catch (IOException e) {

            /*
             * Se ocorrer um erro de conexão ou timeout,
             * consideramos que o participante falhou.
             */
            System.out.println(
                    "Falha ao comunicar com o servidor na porta "
                            + porta
            );

            return false;
        }
    }

    /**
     * Envia COMMIT ou ROLLBACK para um participante.
     *
     * @param porta porta do servidor
     * @param comando comando que será enviado
     */
    private static void enviarComando(int porta, String comando) {

        try {

            Socket socket = new Socket();

            // Tenta estabelecer a conexão dentro do tempo limite
            socket.connect(
                    new InetSocketAddress(HOST, porta),
                    TIMEOUT
            );

            socket.setSoTimeout(TIMEOUT);

            // Entrada de dados
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            // Saída de dados
            PrintWriter saida = new PrintWriter(
                    socket.getOutputStream(),
                    true
            );

            // Envia o comando
            saida.println(comando);

            // Recebe a confirmação
            String resposta = entrada.readLine();

            System.out.println(
                    "Servidor na porta " + porta +
                            " respondeu: " + resposta
            );

            socket.close();

        } catch (IOException e) {

            /*
             * Caso o servidor esteja desligado ou não responda,
             * mostramos uma mensagem de falha.
             */
            System.out.println(
                    "Não foi possível enviar "
                            + comando
                            + " para o servidor na porta "
                            + porta
            );
        }
    }
}