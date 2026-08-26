import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Participante A da transação distribuída.
 *
 * Este servidor recebe comandos do coordenador através
 * de uma conexão Socket.
 *
 * Os principais comandos utilizados são:
 *
 * PREPARE
 * COMMIT
 * ROLLBACK
 */
public class ServidorA {

    // Porta utilizada pelo Servidor A
    private static final int PORTA = 5001;

    public static void main(String[] args) {

        // Cria uma transação com saldo inicial de R$ 1000
        Transacao transacao = new Transacao(1000);

        System.out.println("=================================");
        System.out.println("        SERVIDOR A");
        System.out.println("=================================");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando conexão do coordenador...");

        /*
         * O ServerSocket fica aguardando conexões.
         *
         * O try-with-resources garante que o servidor
         * seja fechado corretamente caso aconteça algum erro.
         */
        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            /*
             * O servidor continua funcionando para receber
             * novas transações.
             */
            while (true) {

                // Aguarda o coordenador estabelecer uma conexão
                Socket socket = servidor.accept();

                System.out.println("\nCoordenador conectado.");

                /*
                 * Cria os objetos utilizados para receber
                 * e enviar mensagens pelo Socket.
                 */
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

                // Recebe o comando enviado pelo coordenador
                String comando = entrada.readLine();

                System.out.println("Comando recebido: " + comando);

                /*
                 * Verifica se o comando começa com PREPARE.
                 *
                 * Exemplo:
                 * PREPARE 100
                 */
                if (comando.startsWith("PREPARE")) {

                    // Divide o comando em partes
                    String[] partes = comando.split(" ");

                    // Converte o valor recebido para double
                    double valor = Double.parseDouble(partes[1]);

                    // Tenta preparar a transação
                    boolean preparado = transacao.preparar(valor);

                    if (preparado) {

                        // Informa ao coordenador que está pronto
                        saida.println("READY");

                    } else {

                        // Informa que não pode realizar a transação
                        saida.println("ABORT");
                    }

                } else if (comando.equals("COMMIT")) {

                    // Confirma a transação
                    transacao.commit();

                    saida.println("COMMIT_OK");

                } else if (comando.equals("ROLLBACK")) {

                    // Desfaz a transação
                    transacao.rollback();

                    saida.println("ROLLBACK_OK");

                } else {

                    // Caso seja recebido um comando desconhecido
                    System.out.println("Comando desconhecido.");
                    saida.println("ERRO");
                }

                // Fecha a conexão com o coordenador
                socket.close();
            }

        } catch (IOException e) {

            System.out.println("Erro no Servidor A.");
            System.out.println("Mensagem: " + e.getMessage());
        }
    }
}