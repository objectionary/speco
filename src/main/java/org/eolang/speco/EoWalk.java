package org.eolang.speco;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.cactoos.io.InputOf;
import org.cactoos.io.OutputTo;
import org.eolang.parser.Syntax;
import org.eolang.parser.XMIR;
import org.objectionary.aoi.launch.LauncherKt;

public class EoWalk implements Walk {

    private final Path input;
    private final Path output;

    private final Speco speco;

    public EoWalk(
        final Path input,
        final Path output,
        final Speco speco
    ) {
        this.input = input;
        this.output = output;
        this.speco = speco;
    }

    @Override
    public void exec() throws IOException {
        Files.createDirectories(this.output);
        for (final Path path : Files.newDirectoryStream(EoWalk.parse(this.input))) {
            Files.write(
                this.output.resolve(path.getFileName()),
                new XMIR(this.speco.transform(this.toXML(path))).toEO().getBytes()
            );
        }
    }

    /**
     * Takes source codes on EO, converts to xmir and applies the AOI tool.
     *
     * @param input Path of the source directory
     * @return Path to the directory with the parsed files
     * @throws IOException When Parsing EO fails
     */
    private static Path parse(final Path input) throws IOException {
        final StringBuilder name = new StringBuilder(input.toString());
        final Path source = Path.of(name.append("_prs").toString());
        FileUtils.copyDirectory(input.toFile(), source.toFile());
        for (final Path path : Files.newDirectoryStream(source)) {
            new Syntax(
                "scenario",
                new InputOf(String.format("%s%n", Files.readString(path))),
                new OutputTo(new FileOutputStream(path.toFile()))
            ).parse();
        }
        LauncherKt.launch(source.toString());
        return Path.of(name.append("_aoi").toString());
    }

    XML toXML(final Path path) throws IOException {
        return new XMLDocument(Files.readString(path));
    }
}
