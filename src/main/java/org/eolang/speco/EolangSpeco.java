package org.eolang.speco;

import com.jcabi.xml.XML;

import java.io.IOException;
import java.nio.file.Path;

public class EolangSpeco implements Speco {
    private final Speco speco;
    EolangSpeco(final Speco speco) {
        this.speco = speco;
    }
    @Override
    public void exec() throws IOException {

    }

    @Override
    public XML applyTrain(XML xml) {
        return null;
    }
}
