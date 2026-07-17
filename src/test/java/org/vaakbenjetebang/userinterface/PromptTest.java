package org.vaakbenjetebang.userinterface;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import org.junit.Before;
import org.junit.Test;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.search.WhiskySuffixTrie;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PromptTest {

    private WhiskySuffixTrie trie;
    private ScrapingState state;
    private Screen screen;
    private TextGraphics graphics;

    private Prompt prompt;

    @Before
    public void setUp() {
        trie = mock(WhiskySuffixTrie.class);
        state = mock(ScrapingState.class);
        screen = mock(Screen.class);
        graphics = mock(TextGraphics.class);

        when(screen.newTextGraphics()).thenReturn(graphics);

        prompt = new Prompt(trie, state, screen);
    }

    @Test
    public void constructor_initializesMainMenuState() throws Exception {
        AtomicReference<?> menuState = getMenuState(prompt);
        assertNotNull("menuState should be initialized", menuState.get());
        assertEquals("Prompt should start in MAIN_MENU", MenuState.MAIN_MENU, menuState.get());
    }

    @Test
    public void handleMainMenuInput_enterWithQ_stopsRunningAndStopsScreen() throws Exception {
        // NOTE: Prompt trims input; include whitespace to verify trim behavior remains effective.
        StringBuilder buffer = new StringBuilder(" q ");
        AtomicBoolean running = new AtomicBoolean(true);

        KeyStroke enter = mock(KeyStroke.class);
        when(enter.getKeyType()).thenReturn(KeyType.Enter);

        invokeHandleMainMenuInput(prompt, enter, buffer, running);

        assertFalse("enter + 'q' should stop loop", running.get());
        verify(screen).stopScreen();
        assertEquals("should remain in MAIN_MENU when quitting", MenuState.MAIN_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleMainMenuInput_enterWithNonQ_switchesToSearchMenuAndDoesNotStopScreen() throws Exception {
        StringBuilder buffer = new StringBuilder("something");
        AtomicBoolean running = new AtomicBoolean(true);

        KeyStroke enter = mock(KeyStroke.class);
        when(enter.getKeyType()).thenReturn(KeyType.Enter);

        invokeHandleMainMenuInput(prompt, enter, buffer, running);

        assertTrue("enter + non-q should not stop loop", running.get());
        verify(screen, never()).stopScreen();
        assertEquals("should switch to SEARCH_MENU on enter for non-q", MenuState.SEARCH_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleMainMenuInput_characterAppendsToBuffer() throws Exception {
        StringBuilder buffer = new StringBuilder();
        AtomicBoolean running = new AtomicBoolean(true);

        KeyStroke ch = mock(KeyStroke.class);
        when(ch.getKeyType()).thenReturn(KeyType.Character);
        when(ch.getCharacter()).thenReturn('a');

        invokeHandleMainMenuInput(prompt, ch, buffer, running);

        assertEquals("a", buffer.toString());
        assertTrue(running.get());
    }

    @Test
    public void handleMainMenuInput_backspaceRemovesLastCharWhenNotEmpty() throws Exception {
        StringBuilder buffer = new StringBuilder("ab");
        AtomicBoolean running = new AtomicBoolean(true);

        KeyStroke backspace = mock(KeyStroke.class);
        when(backspace.getKeyType()).thenReturn(KeyType.Backspace);

        invokeHandleMainMenuInput(prompt, backspace, buffer, running);

        // Kills "subtraction replaced by addition" mutation by ensuring last character is removed.
        assertEquals("backspace should delete last character", "a", buffer.toString());
    }

    @Test
    public void handleMainMenuInput_backspaceOnEmptyDoesNothing() throws Exception {
        StringBuilder buffer = new StringBuilder();
        AtomicBoolean running = new AtomicBoolean(true);

        KeyStroke backspace = mock(KeyStroke.class);
        when(backspace.getKeyType()).thenReturn(KeyType.Backspace);

        invokeHandleMainMenuInput(prompt, backspace, buffer, running);

        assertEquals("", buffer.toString());
    }

    @Test
    public void handleSearchMenuInput_characterQ_clearsBufferAndReturnsToMainMenu() throws Exception {
        // Put prompt in SEARCH_MENU
        getMenuState(prompt).set(MenuState.SEARCH_MENU);

        StringBuilder buffer = new StringBuilder("some query");

        KeyStroke q = mock(KeyStroke.class);
        when(q.getKeyType()).thenReturn(KeyType.Character);
        when(q.getCharacter()).thenReturn('q');

        invokeHandleSearchMenuInput(prompt, q, buffer);

        assertEquals("pressing q in search menu should clear buffer", "", buffer.toString());
        assertEquals("pressing q in search menu should return to MAIN_MENU", MenuState.MAIN_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleSearchMenuInput_nonQOrNonCharacter_doesNothing() throws Exception {
        getMenuState(prompt).set(MenuState.SEARCH_MENU);

        StringBuilder buffer = new StringBuilder("keep");
        KeyStroke notQ = mock(KeyStroke.class);
        when(notQ.getKeyType()).thenReturn(KeyType.Character);
        when(notQ.getCharacter()).thenReturn('x');

        invokeHandleSearchMenuInput(prompt, notQ, buffer);

        assertEquals("keep", buffer.toString());
        assertEquals("menu should remain SEARCH_MENU", MenuState.SEARCH_MENU, getMenuState(prompt).get());

        KeyStroke enter = mock(KeyStroke.class);
        when(enter.getKeyType()).thenReturn(KeyType.Enter);

        invokeHandleSearchMenuInput(prompt, enter, buffer);

        assertEquals("keep", buffer.toString());
        assertEquals("menu should remain SEARCH_MENU", MenuState.SEARCH_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleInput_pollInputNull_doesNothing() throws Exception {
        when(screen.pollInput()).thenReturn(null);

        StringBuilder buffer = new StringBuilder("x");
        AtomicBoolean running = new AtomicBoolean(true);

        invokeHandleInput(prompt, buffer, running);

        // no exception and no interactions beyond pollInput
        verify(screen).pollInput();
        verifyNoMoreInteractions(screen);
        assertEquals(MenuState.MAIN_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleInput_inMainMenu_dispatchesToMainMenuHandler() throws Exception {
        // Provide a character stroke and ensure buffer changes => proves handler invoked (kills void-call mutation)
        KeyStroke ch = mock(KeyStroke.class);
        when(ch.getKeyType()).thenReturn(KeyType.Character);
        when(ch.getCharacter()).thenReturn('z');
        when(screen.pollInput()).thenReturn(ch);

        StringBuilder buffer = new StringBuilder();
        AtomicBoolean running = new AtomicBoolean(true);

        invokeHandleInput(prompt, buffer, running);

        assertEquals("buffer should be appended by main menu input handler", "z", buffer.toString());
        assertEquals(MenuState.MAIN_MENU, getMenuState(prompt).get());
    }

    @Test
    public void handleInput_inSearchMenu_dispatchesToSearchMenuHandler() throws Exception {
        getMenuState(prompt).set(MenuState.SEARCH_MENU);

        KeyStroke q = mock(KeyStroke.class);
        when(q.getKeyType()).thenReturn(KeyType.Character);
        when(q.getCharacter()).thenReturn('q');
        when(screen.pollInput()).thenReturn(q);

        StringBuilder buffer = new StringBuilder("to be cleared");
        AtomicBoolean running = new AtomicBoolean(true);

        invokeHandleInput(prompt, buffer, running);

        assertEquals("buffer should be cleared by search menu handler", "", buffer.toString());
        assertEquals("q should return to main menu", MenuState.MAIN_MENU, getMenuState(prompt).get());
    }

    @Test
    public void paintSearchMenu_rendersHeaderAndEachProductOnSeparateLine() throws Exception {
        WhiskyProduct p1 = productWithIdAndName(UUID.randomUUID(), "A");
        WhiskyProduct p2 = productWithIdAndName(UUID.randomUUID(), "B");
        List<WhiskyProduct> products = List.of(p1, p2);

        when(trie.search("abc")).thenReturn(products);

        invokePaintSearchMenu(prompt, "abc");

        verify(trie).search("abc");
        // header
        verify(graphics).putString(eq(2), eq(4), contains("Search results for abc. Found 2 so far."));
        // each product line uses BODY_START_POSITION + 2 + i => 6 and 7 for i=0,1
        verify(graphics).putString(eq(2), eq(6), eq(String.valueOf(p1)));
        verify(graphics).putString(eq(2), eq(7), eq(String.valueOf(p2)));

        // Kills conditional-boundary mutation by ensuring i < size prints exactly size lines, not size+1 or size-1.
        verify(graphics, times(3)).putString(eq(2), anyInt(), anyString());
    }

    @Test
    public void startPrompt_processesInputAndUpdatesScreenAtLeastOnce_thenStopsOnQEnter() throws Exception {
        when(state.getTrieSize()).thenReturn(0L);
        when(state.isDone()).thenReturn(false);

        // Build a deterministic input sequence:
        // 1) type 'q' (character) -> buffer "q"
        // 2) press Enter -> should set running=false and call stopScreen
        KeyStroke qChar = mock(KeyStroke.class);
        when(qChar.getKeyType()).thenReturn(KeyType.Character);
        when(qChar.getCharacter()).thenReturn('q');

        KeyStroke enter = mock(KeyStroke.class);
        when(enter.getKeyType()).thenReturn(KeyType.Enter);

        // After that, return nulls to allow poll loop to continue briefly if needed.
        when(screen.pollInput()).thenReturn(qChar, enter, null, null, null);

        doNothing().when(screen).startScreen();
        doNothing().when(screen).setCursorPosition(isNull());
        doNothing().when(screen).clear();
        doNothing().when(screen).refresh();
        doNothing().when(screen).stopScreen();

        doReturn(new TerminalPosition(0, 0)).when(screen).getCursorPosition();

        prompt.startPrompt();

        verify(screen).startScreen();
        verify(screen).setCursorPosition(null);

        // must have painted at least once
        verify(screen, atLeastOnce()).clear();
        verify(screen, atLeastOnce()).refresh();

        // quitting must stop the screen
        verify(screen, atLeastOnce()).stopScreen();

        // and we expect the header "Input:" to have been drawn at least once in main menu
        verify(graphics, atLeastOnce()).putString(eq(2), eq(2), contains("Whiskys scraped: 0. Status - running."));
        verify(graphics, atLeastOnce()).putString(eq(2), eq(4), contains("Input:"));
    }

    private static WhiskyProduct productWithIdAndName(UUID id, String name) {
        WhiskyProduct p = new WhiskyProduct();
        try {
            Field idField = WhiskyProduct.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(p, id);
        } catch (ReflectiveOperationException ignored) {
            // If id is set via constructor or defaults, it's fine; only used for toString stability.
        }
        try {
            Method setName = WhiskyProduct.class.getMethod("setName", String.class);
            setName.invoke(p, name);
        } catch (ReflectiveOperationException ignored) {
            // fallback: leave as-is
        }
        return p;
    }

    @SuppressWarnings("unchecked")
    private static AtomicReference<MenuState> getMenuState(Prompt prompt) throws Exception {
        Field f = Prompt.class.getDeclaredField("menuState");
        f.setAccessible(true);
        return (AtomicReference<MenuState>) f.get(prompt);
    }

    private static void invokeHandleInput(Prompt prompt, StringBuilder buffer, AtomicBoolean running) throws Exception {
        Method m = Prompt.class.getDeclaredMethod("handleInput", StringBuilder.class, AtomicBoolean.class);
        m.setAccessible(true);
        try {
            m.invoke(prompt, buffer, running);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
            throw e;
        }
    }

    private static void invokeHandleMainMenuInput(Prompt prompt, KeyStroke ks, StringBuilder buffer, AtomicBoolean running) throws Exception {
        Method m = Prompt.class.getDeclaredMethod("handleMainMenuInput", KeyStroke.class, StringBuilder.class, AtomicBoolean.class);
        m.setAccessible(true);
        try {
            m.invoke(prompt, ks, buffer, running);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof IOException) throw (IOException) e.getCause();
            throw e;
        }
    }

    private static void invokeHandleSearchMenuInput(Prompt prompt, KeyStroke ks, StringBuilder buffer) throws Exception {
        Method m = Prompt.class.getDeclaredMethod("handleSearchMenuInput", KeyStroke.class, StringBuilder.class);
        m.setAccessible(true);
        m.invoke(prompt, ks, buffer);
    }

    private static void invokePaintSearchMenu(Prompt prompt, String input) throws Exception {
        Method m = Prompt.class.getDeclaredMethod("paintSearchMenu", String.class);
        m.setAccessible(true);
        m.invoke(prompt, input);
    }
}