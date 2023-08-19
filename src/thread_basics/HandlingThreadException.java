package thread_basics;

public class HandlingThreadException {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Internal Exception");
            }
        });

        thread.setName("ThreadException");

        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("A critical error happened in the thread "+t.getName()
                +" the error is "+e.getMessage());
            }
        });
        thread.start();
    }

}
