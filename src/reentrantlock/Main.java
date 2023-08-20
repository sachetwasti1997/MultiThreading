package reentrantlock;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static class PriceContainer {
        private Lock lock = new ReentrantLock();

        private double bitCoinPrice;
        private double etherPrice;
        private double liteCoinPrice;
        private double bitCoinCashPrice;
        private double ripplePrice;

        public Lock getLock() {
            return lock;
        }

        public void setLock(Lock lock) {
            this.lock = lock;
        }

        public double getBitCoinPrice() {
            return bitCoinPrice;
        }

        public void setBitCoinPrice(double bitCoinPrice) {
            this.bitCoinPrice = bitCoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLiteCoinPrice() {
            return liteCoinPrice;
        }

        public void setLiteCoinPrice(double liteCoinPrice) {
            this.liteCoinPrice = liteCoinPrice;
        }

        public double getBitCoinCashPrice() {
            return bitCoinCashPrice;
        }

        public void setBitCoinCashPrice(double bitCoinCashPrice) {
            this.bitCoinCashPrice = bitCoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }

    public static class PricesUpdater extends  Thread {
        private final PriceContainer priceContainer;
        private final Random random = new Random();

        public PricesUpdater(PriceContainer priceContainer) {
            this.priceContainer = priceContainer;
        }

        @Override
        public void run() {
            priceContainer.getLock().lock();
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                priceContainer.setEtherPrice(random.nextInt(2000));
                priceContainer.setRipplePrice(random.nextDouble());
                priceContainer.setBitCoinPrice(random.nextInt(2000));
                priceContainer.setLiteCoinPrice(random.nextInt(2000));
                priceContainer.setBitCoinCashPrice(random.nextInt(2000));
            }
            finally {
                priceContainer.getLock().unlock();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
