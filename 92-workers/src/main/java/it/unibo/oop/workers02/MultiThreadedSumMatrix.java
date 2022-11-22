package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiThreadedSumMatrix.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int numThread;
    /**
     * 
     * @param numThread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int numThread) {
        super();
        this.numThread = numThread;
    }

    /**
     * 
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length / numThread + matrix.length % numThread;
        final List<Worker> workers = new ArrayList<>(numThread);
        double sum = 0;
        for (int i = 0; i < matrix.length; i += size) {
            workers.add(new Worker(matrix, i, size));
        }
        for (final Thread worker: workers) {
            worker.start();
        }
        for (final Thread worker: workers) {
            try {
                worker.join();
                sum += ((Worker) worker).getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
    private final class Worker extends Thread {

        private final double[][] matrix;
        private final int pos;
        private final int numElem;
        private double res;
        private Worker(final double[][] matrix, final int pos, final int numElem) {
            super();
            this.matrix = matrix;
            this.pos = pos;
            this.numElem = numElem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + pos // NOPMD: allowed as this is just an exercise
                + " to position " + (pos + numElem - 1)); 
            for (int i = pos; i < matrix.length && i < pos + numElem; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    this.res += this.matrix[i][j];
                }
            }
        }

        public double getResult() {
            return this.res;
        }
    }
}
