package wait_notify_eg;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class MainApplication {

    private static final String INPUT_FILE = "./out/matrices";
    private static final String OUTPUT_FILE = "./out/matrices_results";
    private static final Integer N = 10;

    private static class MatrixMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter writer;

        public MatrixMultiplierConsumer(ThreadSafeQueue queue, FileWriter writer) {
            this.queue = queue;
            this.writer = writer;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    MatrixPair matrixPair = queue.remove();

                    if (matrixPair == null) {
                        System.out.println("No more thing to remove, consumer is terminating");
                        break;
                    }

                    float[][] result = multiplyMatrices(matrixPair.matrix1, matrixPair.matrix2);
                    saveFileToMatrix(result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try{
                writer.flush();
                writer.close();
            }catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void saveFileToMatrix(float[][] matrix) throws IOException {
            for (int r = 0; r<N; r++) {
                StringBuilder builder = new StringBuilder();
                for (int c=0; c<N; c++) {
                    builder.append(matrix[r][c]).append(", ");
                }
                writer.write(builder.toString());
                writer.write('\n');
            }
            writer.write('\n');
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2) {
            float [][] result = new float[N][N];
            for (int r = 0; r<N; r++) {
                for (int c = 0; c < N; c++) {
                    for (int k=0; k<N; k++) {
                        result[r][c] += m1[r][k] * m1[k][c];
                    }
                }
            }
            return result;
        }
    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();
                if (matrix1 == null || matrix2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer thread is terminating");
                    return;
                }
                MatrixPair matrixPair = new MatrixPair();
                matrixPair.matrix1 = matrix1;
                matrixPair.matrix2 = matrix2;

                queue.add(matrixPair);
            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r<N; r++) {
                if (!scanner.hasNextLine()) {
                    return null;
                }
                String [] nextLine = scanner.nextLine().split(",");
                for (int c=0; c<nextLine.length; c++) {
                    matrix[r][c] = Float.parseFloat(nextLine[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    private static class ThreadSafeQueue {
        private final Queue<MatrixPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean terminate = false;

        /*The below methods are synchronised that lets us have atomic operations on queue
        * and also allows us to use wait and notify methods*/
        /**
         * This will be called by producer to add the pair of matrices to queue
         * @param matrixPair
         */
        public synchronized void add(MatrixPair matrixPair) {
            queue.add(matrixPair);
            isEmpty = false;
            notify();//If the consumer is waiting for work we will notify it
        }

        /**
         * This called by consumer to read and remove matrix from the queue
         * @return
         */
        public synchronized MatrixPair remove() {
            //check if queue is empty and we are not instructed to terminate by the producer
            while (isEmpty && !terminate) {
                try {
                    wait();
                    //consumer has nothing to consume from the queue, so it will go to sleep releasing the lock
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (queue.size() == 1) {
                isEmpty = true;
            }
            if (queue.isEmpty() && terminate) return null;
            System.out.println("Queue size "+queue.size());
            return queue.remove();
        }

        /**
         * Called by producer to let the consumer know that queue is empty and the consumer
         * must terminate its thread
         */
        public synchronized void terminate() {
            terminate = true;
            notifyAll();//to wake up all the potetially waiting consumer threads
        }
    }

    private static class MatrixPair {
        public float[][] matrix1;
        public float[][] matrix2;
    }

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer producer = new MatricesReaderProducer(
                new FileReader(inputFile), threadSafeQueue);
        MatrixMultiplierConsumer consumer = new MatrixMultiplierConsumer(
                threadSafeQueue, new FileWriter(outputFile)
        );

        consumer.start();
        producer.start();
    }

}
