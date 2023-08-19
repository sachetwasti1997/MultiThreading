package thread_termination;

import java.math.BigInteger;
import java.util.Scanner;

public class Thread_Interruption2 {

    private static class LongComputation implements Runnable {

        private final BigInteger base;
        private final BigInteger power;

        public LongComputation(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base+"^"+power+" = "+power());
        }

        private BigInteger power() {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i=BigInteger.ONE; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Running really long running task," +
                            " result calculated up until this point");
                    return result;
                }
                result = result.multiply(base);
            }
            return result;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var comp = new Thread(new LongComputation(new BigInteger("24345212"), new BigInteger("10000000")));
        System.out.println("---THREAD WAITING HERE FOR INPUT-------");
        System.out.println("How long do you want to wait for? ");
        int wait = new Scanner(System.in).nextInt();
        comp.start();

//        comp.interrupt();
        //just calling this will not work here, as we dont have any logic to handle it
        Thread.sleep(wait * 1000L);
        comp.interrupt();
    }

}
