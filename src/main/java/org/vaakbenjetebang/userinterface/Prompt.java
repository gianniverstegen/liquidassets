package org.vaakbenjetebang.userinterface;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.search.WhiskySuffixTrie;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Prompt {

    private final static long MS_TO_WAIT = 50L;
    private final static int START_STRING_COLUMN_POSITION = 2;
    private final static int COUNTER_ROW_POSITION = 2;
    private final static int BODY_START_POSITION = 4;
    private final AtomicReference<MenuState> menuState;
    private final static Logger log = LogManager.getLogger(Prompt.class);
    private final WhiskySuffixTrie trie;
    private final ScrapingState state;
    private final Screen screen;

    @Inject
    public Prompt(WhiskySuffixTrie trie, ScrapingState state, Screen screen) {
        this.trie = trie;
        this.state = state;
        this.screen = screen;
        this.menuState = new AtomicReference<>(MenuState.MAIN_MENU);
    }

    public void startPrompt() {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()){
            screen.startScreen();
            screen.setCursorPosition(null); // We don't need a cursor

            AtomicBoolean running = new AtomicBoolean(true);
            StringBuilder inputBuffer = new StringBuilder();


            Callable<Void> counterTask = () -> {
                try {
                    while (running.get()) {
                        screen.clear();
                        if (menuState.get() == MenuState.MAIN_MENU) {
                            screen.newTextGraphics().putString(START_STRING_COLUMN_POSITION, COUNTER_ROW_POSITION, "Whiskys scraped: " + state.getTrieSize() + ". Status - " + (state.isDone() ? "done" : "running") + ".");
                            screen.newTextGraphics().putString(START_STRING_COLUMN_POSITION, BODY_START_POSITION, "Input: " + inputBuffer);
                        } else {
                            screen.newTextGraphics().putString(START_STRING_COLUMN_POSITION, COUNTER_ROW_POSITION, "Whiskys scraped: " + state.getTrieSize() + ". Status - " + (state.isDone() ? "done" : "running") + ".");
                            paintSearchMenu(inputBuffer.toString().trim());
                        }
                        screen.refresh();
                        Thread.sleep(MS_TO_WAIT);

                    }
                } catch (InterruptedException | IOException e) {
                    log.error(e);
                }
                return null;
            };

            Callable<Void> inputTask = () -> {
                try {
                    while (running.get()) {
                        handleInput(inputBuffer, running);
                    }
                } catch (IOException e) {
                    log.error(e);
                }
                return null;
            };

            Future<?> counterFuture = executorService.submit(counterTask);
            Future<?> inputFuture = executorService.submit(inputTask);

            inputFuture.get();
            executorService.shutdownNow();
            counterFuture.get();
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error(e);
        }
    }

    private void handleInput(StringBuilder inputBuffer, AtomicBoolean running) throws IOException {
        KeyStroke keyStroke = screen.pollInput();
        if (keyStroke != null) {
            if (menuState.get() == MenuState.MAIN_MENU) {
                handleMainMenuInput(keyStroke, inputBuffer, running);
            } else {
                handleSearchMenuInput(keyStroke, inputBuffer);
            }
        }
    }

    private void handleMainMenuInput(KeyStroke keyStroke, StringBuilder inputBuffer, AtomicBoolean running) throws IOException {
        if (keyStroke.getKeyType() == KeyType.Enter) {
            String input = inputBuffer.toString().trim();

            if (input.equals("q")) {
                running.set(false);
                screen.stopScreen();
            }
            else {
                menuState.set(MenuState.SEARCH_MENU);
            }
            return;
        }
        if (keyStroke.getKeyType() == KeyType.Character) {
            inputBuffer.append(keyStroke.getCharacter());
        }
        if (keyStroke.getKeyType() == KeyType.Backspace && !inputBuffer.isEmpty()) {
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);
        }
    }

    private void handleSearchMenuInput(KeyStroke keyStroke, StringBuilder inputBuffer) {
        if (keyStroke != null && keyStroke.getKeyType() == KeyType.Character &&
                keyStroke.getCharacter() == 'q') {
            inputBuffer.setLength(0);
            menuState.set(MenuState.MAIN_MENU);
        }
    }

    private void paintSearchMenu(String input) {
        List<WhiskyProduct> products = trie.search(input);

        screen.newTextGraphics().putString(START_STRING_COLUMN_POSITION, BODY_START_POSITION,"Search results for " + input + ". Found " + products.size() + " so far. ");
        for (int i = 0; i < products.size(); i++) {
            screen.newTextGraphics().putString(START_STRING_COLUMN_POSITION, BODY_START_POSITION + 2 + i, String.valueOf(products.get(i)));
        }
    }
}
