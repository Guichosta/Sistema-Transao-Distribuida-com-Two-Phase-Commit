import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Participante B da transação distribuída.
 *
 * O Servidor B recebe comandos do coordenador através
 * de Socket e participa das etapas da transação.
 */
public class ServidorB {

    // Porta utilizada pelo Servidor B
    private static final int PORTA = 5002;

    public static void main(String[] args) {

        // Cria uma transação com saldo inicial de R$ 1000
        Transacao transacao = new Transacao(1000);

        System.out.println("=================================");
        System.out.println("        SERVIDOR B");
        System.out.println("=================================");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando conexão do coordenador...");

        try (ServerSocket servidor = new ServerSocket(PORTA)) {

            /*
             * Mantém o servidor funcionando para que ele
             * possa receber novas solicitações.
             */
            while (true) {

                // Aguarda uma conexão do coordenador
                Socket socket = servidor.accept();

                System.out.println("\nCoordenador conectado.");

                // Entrada de dados recebidos pelo Socket
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                // Saída de dados enviada pelo Socket
                PrintWriter saida = new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

                // Lê o comando enviado pelo coordenador
                String comando = entrada.readLine();

                System.out.println("Comando recebido: " + comando);

                /*
                 * Verifica se o comando recebido é PREPARE.
                 *
                 * Exemplo:
                 * PREPARE 100
                 */
                if (comando.startsWith("PREPARE")) {

                    // Separa o comando e o valor
                    String[] partes = comando.split(" ");

                    // Converte o valor recebido
                    double valor = Double.parseDouble(partes[1]);

                    // Tenta preparar a transação
                    boolean preparado = transacao.preparar(valor);

                    if (preparado) {

                        // Participante está preparado
                        saida.println("READY");

                    } else {

                        // Participante não pode realizar a operação
                        saida.println("ABORT");
                    }

                } else if (comando.equals("COMMIT")) {

                    // Confirma definitivamente a transação
                    transacao.commit();

                    saida.println("COMMIT_OK");

                } else if (comando.equals("ROLLBACK")) {

                    // Desfaz a operação
                    transacao.rollback();

                    saida.println("ROLLBACK_OK");

                } else {

                    // Comando não reconhecido
                    System.out.println("Comando desconhecido.");
                    saida.println("ERRO");
                }

                // Encerra a conexão atual
                socket.close();
            }

        } catch (IOException e) {

            System.out.println("Erro no Servidor B.");
            System.out.println("Mensagem: " + e.getMessage());
        }
    }
}