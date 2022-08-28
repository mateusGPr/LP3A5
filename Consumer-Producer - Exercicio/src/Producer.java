import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Producer implements Runnable {
    private static final String[] products = { "Alfa", "Beta", "Delta", "Echo", "Foxtrot", "Golf", "Hotel" };
    private final ReentrantLock reentLock;
    private final List<String> buffer;
    private final AtomicBoolean stop;
    private final int maxProducerCount;

    Producer(ReentrantLock reentLock, List<String> buffer, AtomicBoolean stop, int maxProducerCount) {
        this.reentLock = reentLock;
        this.buffer = buffer;
        this.stop = stop;
        this.maxProducerCount = maxProducerCount;
    }

    @Override
    public void run() {
        while (!stop.get()) {
            // final boolean locked = reentLock.tryLock();
            reentLock.lock();

            // if (locked) {
            try {
                if (buffer.size() >= maxProducerCount) {
                    System.out.println(Thread.currentThread().getName() + " atingiu o limite de produção...");
                    continue;
                }
                Random random = new Random();
                String addString = products[random.nextInt(products.length)] + " " + (random.nextInt(10) * 1000);
                buffer.add(addString);
                System.out.println(Thread.currentThread().getName() + " adicionou: " + addString + "...");
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
