package synchronisation;

public class LockSynchronize {
    private static class InventoryCounter {
        private int items = 0;
        private final Object lock = new Object();

        public synchronized void increment() {
            synchronized (this.lock) {
                items++;
            }
        }

        public synchronized void decrement() {
            synchronized (this.lock) {
                items--;
            }
        }

        public int getItems() {
            synchronized (this.lock) {
                return items;
            }
        }
    }

    private static class IncrementThread extends Thread {

        private final InventoryCounter inventoryCounter;
        public IncrementThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i=0; i<10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    private static class DecrementThread extends Thread {

        private final InventoryCounter inventoryCounter;
        public DecrementThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i=0; i<10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        DecrementThread decrementThread = new DecrementThread(inventoryCounter);
        IncrementThread incrementThread = new IncrementThread(inventoryCounter);

        incrementThread.start();
        decrementThread.start();

        incrementThread.join();
        decrementThread.join();

        System.out.println(inventoryCounter.getItems());
    }
}
