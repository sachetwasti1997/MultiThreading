package IoBoundApplication;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IoBoundApplication {

    private static final Integer NUMBER_OF_TASKS = 1000;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Press start to executing tasks");
        scan.nextLine();
        System.out.printf("Running %d tasks\n", NUMBER_OF_TASKS);

        long start = System.currentTimeMillis();
        performTasks();
        System.out.printf("The tasks took %dms to complete", System.currentTimeMillis() - start);
    }

    private static void performTasks() {
        ExecutorService service = Executors.newCachedThreadPool();
        try {
            for (int i=0; i<NUMBER_OF_TASKS; i++) {
                service.submit(new Runnable() {
                    @Override
                    public void run() {
                        blockingIoOperation();
                    }
                });
            }
        }finally {
            service.shutdown();
        }

    }

    //Simulates long blocking IO
    private static void blockingIoOperation() {
        System.out.println("Executing a blocking task from thread: "+Thread.currentThread().getName());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
