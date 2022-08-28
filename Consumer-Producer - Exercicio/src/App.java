import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

public class App {
    static final int MAX_THREADS = 14;

    public static void main(final String[] args) throws Exception {
        final AtomicBoolean stop = new AtomicBoolean(false);
        final ReentrantLock reentLock = new ReentrantLock(true);
        final List<String> buffer = new ArrayList<String>();
        ExecutorService pool = Executors.newFixedThreadPool(MAX_THREADS);

        for (int i = 0; i < MAX_THREADS / 2; i++) {
            pool.execute(new Consumer(reentLock, buffer, stop));
            pool.execute(new Producer(reentLock, buffer, stop, 10));

            /*
             * final Thread consumerThread = new Thread(new Consumer(reentLock, buffer,
             * stop));
             * final Thread producerThread = new Thread(new Producer(reentLock, buffer,
             * stop, 5));
             * 
             * consumerThread.start();
             * producerThread.start();
             */
        }
        JOptionPane.showMessageDialog(null, "Clique em 'Ok' para encerrar...", "Encerrar", JOptionPane.INFORMATION_MESSAGE);

        try {
            System.out.println("---------- Tentando terminar o executor...");
            stop.set(true);
            pool.shutdown();
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("---------- Tarefas interrompidas...");
        } finally {
            if (!pool.isTerminated()) {
                System.err.println("---------- Cancelando tarefas não finalizadas...");
            pool.shutdownNow();
            }
            System.out.println("---------- Tarefas finalizadas...");
        }
        System.out.println("---------- Conteúdo da lista: " + buffer);
    }
}
