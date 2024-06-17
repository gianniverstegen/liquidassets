package org.vaakbenjetebang.infra;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.io.IOException;

@Module
public class UserInterfaceModule {

    @Provides
    @Singleton
    Screen getScreen() {
        try {
            Terminal terminal = new DefaultTerminalFactory().createSwingTerminal();
            return new TerminalScreen(terminal);
        } catch (IOException e) {
            throw new RuntimeException("Failed to instantiate terminal");
        }
    }
}
