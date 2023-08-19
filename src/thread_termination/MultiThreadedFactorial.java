package thread_termination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiThreadedFactorial {

    private static class FactorialThread extends Thread {
        private boolean isFinished;
        private BigInteger result;
        private long inputNumber;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
            this.result = BigInteger.ZERO;
            this.isFinished = false;
        }

        @Override
        public void run() {
            this.result = calculateFactorial(inputNumber);
            this.isFinished = true;
        }

        private BigInteger calculateFactorial(long n) {
            BigInteger result = BigInteger.ONE;
            for (long i=n; i>0; i--) {
                result = result.multiply(new BigInteger(Long.toString(i)));
            }
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Long> numbers = Arrays.asList(1000000000L, 3452L, 5562L, 1234L, 23L);
        List<FactorialThread> threadList = new ArrayList<>();
        for (long i: numbers) {
            threadList.add(new FactorialThread(i));
        }
        for (Thread th: threadList) {
            th.start();
        }
        /*
        This is: Solution@
         */
        for (Thread thread: threadList) {
            thread.join(5000);
        }

        /*
        Output of the factorial calculation of 0 is 1
        The calculation for 3452 is still in progress
        The calculation for 5562 is still in progress
        The calculation for 1234 is still in progress
        Output of the factorial calculation of 23 is 25852016738884976640000
        The output is like this for above execution, because of race condition
        this can be eliminated by calling thread join for each threads, Solution@
         */
        for (int i=0; i<numbers.size(); i++) {
            FactorialThread thread = threadList.get(i);
            if (thread.isFinished) {
                System.out.println("Output of the factorial calculation of "
                        +numbers.get(i)+" is "+thread.getResult());
            } else {
                System.out.println("The calculation for "+numbers.get(i)
                        +" is still in progress");
            }
        }
    }

}
