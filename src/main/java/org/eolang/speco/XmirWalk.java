package org.eolang.speco;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmirWalk implements Walk {

    private final Path input;
    private final Path output;

    private final Speco speco;

    public XmirWalk(
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
        for (final Path path : Files.newDirectoryStream(this.input)) {
            Files.write(
                this.output.resolve(path.getFileName()),
                this.speco.transform(this.toXML(path)).toString().getBytes()
            );
        }
    }

    XML toXML(final Path path) throws IOException {
        return new XMLDocument(Files.readString(path));
    }
}
