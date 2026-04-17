package psicologia.clinica.infrastructure.config.env;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class DotEnvReader {

    private static final Path DOT_ENV_PATH = Path.of(".env");

    private DotEnvReader() {
    }

    public static Optional<String> read(String key) {
        if (Files.notExists(DOT_ENV_PATH)) {
            return Optional.empty();
        }

        try {
            return Files.readAllLines(DOT_ENV_PATH, StandardCharsets.UTF_8)
                    .stream()
                    .map(String::trim)
                    .filter(line -> line.startsWith(key + "="))
                    .map(line -> line.substring((key + "=").length()).trim())
                    .filter(value -> !value.isBlank())
                    .findFirst();
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível ler o arquivo .env local.", exception);
        }
    }
}
