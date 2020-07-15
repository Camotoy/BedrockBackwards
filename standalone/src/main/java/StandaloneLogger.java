import com.github.doctormacc.common.Logger;
import lombok.extern.log4j.Log4j2;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

@Log4j2
public class StandaloneLogger extends SimpleTerminalConsole implements Logger {

    public StandaloneLogger() {
        Configurator.setLevel(log.getName(), Level.INFO);
    }

    public void info(String message) {
        log.info(message);
    }

    protected boolean isRunning() {
        return true;
    }

    protected void runCommand(String s) {

    }

    protected void shutdown() {
        System.exit(0);
    }
}
