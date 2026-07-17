package org.vaakbenjetebang.scraper;

import org.junit.Test;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertSame;

public class ProcessorTest {

    private static class TestProcessor extends Processor<String> {
        private final List<String> processedItems = new ArrayList<>();
        private final List<Optional<WhiskyProduct>> responses;
        private final AtomicInteger invocationCount = new AtomicInteger();

        private TestProcessor(Optional<WhiskyProduct>... responses) {
            this.responses = new ArrayList<>();
            for (Optional<WhiskyProduct> response : responses) {
                this.responses.add(response);
            }
        }

        @Override
        public Optional<WhiskyProduct> processItem(String item) {
            processedItems.add(item);
            int index = invocationCount.getAndIncrement();
            if (index < responses.size()) {
                return responses.get(index);
            }
            return Optional.empty();
        }
    }

    @Test
    public void process_shouldProcessItemsUntilSentinel_andPublishResultsAndSentinel() throws Exception {
        TestProcessor processor = new TestProcessor(
                Optional.of(new WhiskyProduct()),
                Optional.empty(),
                Optional.of(new WhiskyProduct())
        );

        BlockingQueue<QueueItem<String>> inputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<QueueItem<WhiskyProduct>> outputQueue = new LinkedBlockingQueue<>();

        inputQueue.add(QueueItem.of("first"));
        inputQueue.add(QueueItem.of("second"));
        inputQueue.add(QueueItem.of("third"));
        inputQueue.add(QueueItem.sentinel());

        processor.process(inputQueue, outputQueue);

        assertThat(processor.processedItems).containsExactly("first", "second", "third");

        QueueItem<WhiskyProduct> first = outputQueue.poll(1, TimeUnit.SECONDS);
        QueueItem<WhiskyProduct> second = outputQueue.poll(1, TimeUnit.SECONDS);
        QueueItem<WhiskyProduct> third = outputQueue.poll(1, TimeUnit.SECONDS);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(third).isNotNull();

        assertThat(first.isSentinel()).isFalse();
        assertThat(second.isSentinel()).isFalse();
        assertThat(third.isSentinel()).isTrue();

        assertThat(outputQueue.poll()).isNull();
    }

    @Test
    public void process_shouldInterruptCurrentThreadAndStillAddSentinelWhenInputIsInterrupted() {
        TestProcessor processor = new TestProcessor();

        BlockingQueue<QueueItem<String>> inputQueue = new InterruptingBlockingQueue<>();
        BlockingQueue<QueueItem<WhiskyProduct>> outputQueue = new LinkedBlockingQueue<>();

        Thread.interrupted();
        processor.process(inputQueue, outputQueue);

        assertThat(Thread.currentThread().isInterrupted()).isTrue();

        QueueItem<WhiskyProduct> sentinel = outputQueue.poll();
        assertThat(sentinel).isNotNull();
        assertThat(sentinel.isSentinel()).isTrue();
    }

    @Test
    public void process_shouldRetryAddingResultUntilOutputQueueAcceptsIt() throws Exception {
        TestProcessor processor = new TestProcessor(Optional.of(new WhiskyProduct()));

        BlockingQueue<QueueItem<String>> inputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<QueueItem<WhiskyProduct>> outputQueue = new OfferRejectingBlockingQueue<>(1);

        inputQueue.add(QueueItem.of("only"));
        inputQueue.add(QueueItem.sentinel());

        processor.process(inputQueue, outputQueue);

        assertThat(processor.processedItems).containsExactly("only");
        assertThat(outputQueue).hasSize(2);

        QueueItem<WhiskyProduct> first = outputQueue.poll(1, TimeUnit.SECONDS);
        QueueItem<WhiskyProduct> second = outputQueue.poll(1, TimeUnit.SECONDS);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(first.isSentinel()).isFalse();
        assertThat(second.isSentinel()).isTrue();
    }

    @Test
    public void process_shouldRetryAddingSentinelUntilOutputQueueAcceptsIt() throws Exception {
        TestProcessor processor = new TestProcessor(Optional.empty());

        BlockingQueue<QueueItem<String>> inputQueue = new LinkedBlockingQueue<>();
        BlockingQueue<QueueItem<WhiskyProduct>> outputQueue = new OfferRejectingBlockingQueue<>(0);

        inputQueue.add(QueueItem.sentinel());

        processor.process(inputQueue, outputQueue);

        QueueItem<WhiskyProduct> sentinel = outputQueue.poll(1, TimeUnit.SECONDS);
        assertThat(sentinel).isNotNull();
        assertThat(sentinel.isSentinel()).isTrue();
    }

    private static class InterruptingBlockingQueue<E> extends LinkedBlockingQueue<E> {
        @Override
        public E take() throws InterruptedException {
            throw new InterruptedException("forced interruption");
        }
    }

    private static class OfferRejectingBlockingQueue<E> extends LinkedBlockingQueue<E> {
        private final AtomicInteger remainingFailures;

        private OfferRejectingBlockingQueue(int failuresBeforeAccepting) {
            this.remainingFailures = new AtomicInteger(failuresBeforeAccepting);
        }

        @Override
        public boolean offer(E e) {
            if (remainingFailures.getAndDecrement() > 0) {
                return false;
            }
            return super.offer(e);
        }
    }
}