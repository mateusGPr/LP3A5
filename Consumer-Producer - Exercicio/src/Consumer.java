import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer implements Runnable {
    private final ReentrantLock reentLock;
    private final List<String> buffer;
    private final AtomicBoolean stop;

    Consumer(ReentrantLock reentLock, List<String> buffer, AtomicBoolean stop) {
        this.reentLock = reentLock;
        this.buffer = buffer;
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop.get()) {
            // final boolean locked = reentLock.tryLock();
            reentLock.lock();

            // if (locked) {
            try {
                if (buffer.isEmpty()) {
                    System.out.println(Thread.currentThread().getName() + " não há nenhum produto...");
                    continue;
                }
                System.out.println(Thread.currentThread().getName() + " removeu: " + buffer.remove(0) + "...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " interrompido...");
                }
            } catch (Exception e) {
                System.err.println(Thread.currentThread().getName() + " lançou uma exepção:");
                System.err.println(e.getLocalizedMessage());
            } finally {
                reentLock.unlock();
            }
            // }
        }
        System.out.println(Thread.currentThread().getName() + " terminou...");
    }
}
