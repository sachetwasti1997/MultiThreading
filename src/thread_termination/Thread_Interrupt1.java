package thread_termination;

public class Thread_Interrupt1 {

    private static class BlockingTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Exiting the blocking task caused by thread "
                        +Thread.currentThread().getName());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        System.out.println("Waiting for 5 seconds for the task, Thread: "
                +Thread.currentThread().getName());
        for (int i=0; i<5; i++) {
            System.out.println(i+1);
            Thread.sleep(1000);
        }
        System.out.println("Ending the task as not completed in 5 seconds, Thread: "
        +Thread.currentThread().getName());
        thread.interrupt();
    }

}
