/**
 * Representa uma transação simples.
 *
 * A classe possui as operações utilizadas no exemplo:
 * PREPARE, COMMIT e ROLLBACK.
 *
 * Projeto desenvolvido para demonstrar conceitos de
 * transações distribuídas utilizando Java.
 */
public class Transacao {

    // Saldo atual do participante
    private double saldo;

    // Guarda o saldo antes da transação.
    // É utilizado caso seja necessário realizar um rollback.
    private double saldoAnterior;

    /**
     * Construtor da transação.
     *
     * @param saldoInicial saldo inicial do participante
     */
    public Transacao(double saldoInicial) {
        this.saldo = saldoInicial;
        this.saldoAnterior = saldoInicial;
    }

    /**
     * Primeira etapa da transação.
     *
     * Nesta etapa verificamos se o valor é válido e
     * se existe saldo suficiente para realizar a operação.
     *
     * @param valor valor que será retirado
     * @return true caso a transação esteja pronta,
     *         false caso não possa ser realizada
     */
    public boolean preparar(double valor) {

        System.out.println("PREPARE: verificando a transação...");

        // Verifica se o valor informado é válido
        if (valor <= 0) {
            System.out.println("PREPARE: valor inválido.");
            return false;
        }

        // Verifica se existe saldo suficiente
        if (valor > saldo) {
            System.out.println("PREPARE: saldo insuficiente.");
            return false;
        }

        // Guarda o saldo atual para possibilitar o rollback
        saldoAnterior = saldo;

        // Reserva o valor para a transação
        saldo = saldo - valor;

        System.out.println("PREPARE: transação preparada.");
        System.out.println("Saldo reservado: R$ " + valor);

        return true;
    }

    /**
     * Segunda etapa quando todos os participantes
     * estão preparados.
     *
     * O COMMIT confirma definitivamente a operação.
     */
    public void commit() {

        System.out.println("COMMIT: transação confirmada.");
        System.out.println("Saldo atual: R$ " + saldo);
    }

    /**
     * Cancela a operação realizada durante o PREPARE.
     *
     * O saldo anterior é restaurado.
     */
    public void rollback() {

        saldo = saldoAnterior;

        System.out.println("ROLLBACK: transação cancelada.");
        System.out.println("Saldo restaurado: R$ " + saldo);
    }

    /**
     * Retorna o saldo atual.
     *
     * @return saldo atual
     */
    public double getSaldo() {
        return saldo;
    }
}