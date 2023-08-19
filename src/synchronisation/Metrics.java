package synchronisation;

import java.util.Random;

public class Metrics {

    private static class MetricHolder {
        private long count = 0;
        private double average = 0;

        /*
        Without a synchronised keyword this operation will not be atomic thus giving
        incorrect results
         */
        public void addSample(long sample) {
            double currentSum = average * count;
            count++;
            average = (currentSum + sample) / count;
        }

        public double getAverage(){
            return average;
        }
    }

    private static class BusinessLogic extends Thread {
        private MetricHolder metrics;
        private Random random = new Random();

        public BusinessLogic(MetricHolder metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while (true) {
                long current = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                long end = System.currentTimeMillis();
                metrics.addSample(end - current);
            }
        }
    }

    private static class MetricsPrinter extends Thread {
        private MetricHolder metricHolder;

        public MetricsPrinter(MetricHolder metricHolder) {
            this.metricHolder = metricHolder;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("The current average is "+metricHolder.getAverage());
            }
        }
    }

    public static void main(String[] args) {
        MetricHolder metricHolder = new MetricHolder();
        BusinessLogic businessLogic = new BusinessLogic(metricHolder);
        BusinessLogic logic = new BusinessLogic(metricHolder);
        MetricsPrinter printer = new MetricsPrinter(metricHolder);

        businessLogic.start();
        logic.start();
        printer.start();
    }

}
