package thread_basics;

public class ThreadStarting {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    System.out.println("Now in the thread "+Thread.currentThread().getName());
                    System.out.println("Priority "+Thread.currentThread().getPriority());
                    Thread.sleep(1000);
                    System.out.println("The completed thread "+Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread.setName("New Thread1");
        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("We are in the thread: "+Thread.currentThread().getName()
                +" before starting a new thread");
        thread.start();
        Thread.sleep(1000);
        System.out.println("We are in the thread: "+Thread.currentThread().getName()
                +" after starting a new thread");
    }

}
