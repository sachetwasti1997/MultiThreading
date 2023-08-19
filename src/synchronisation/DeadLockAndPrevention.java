package synchronisation;

import java.util.Random;

public class DeadLockAndPrevention {

    private static class Intersection {
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is blocked by thread "+Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road B is blocked by thread "+Thread.currentThread().getName());
                synchronized (roadB) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random;

        public TrainA(Intersection intersection, Random random) {
            this.intersection = intersection;
            this.random = random;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadA();
            }
        }
    }

    private static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random;

        public TrainB(Intersection intersection, Random random) {
            this.intersection = intersection;
            this.random = random;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                intersection.takeRoadB();
            }
        }
    }

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread t1 = new Thread(new TrainA(intersection, new Random()));
        Thread t2 = new Thread(new TrainB(intersection, new Random()));
        t1.start();
        t2.start();
    }

}
