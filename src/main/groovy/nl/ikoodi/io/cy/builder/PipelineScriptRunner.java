package nl.ikoodi.io.cy.builder;

import groovy.lang.GroovyShell;
import nl.ikoodi.io.cy.builder.script.PipelineScript;
import nl.ikoodi.io.cy.model.DefaultPipeline;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.PrintStream;

public class PipelineScriptRunner {

    private final PipelineScript script;
    private final PrintStream redirectedOutput;
    private PrintStream originalStdOut;
    private PrintStream originalStdErr;

    public PipelineScriptRunner(final PrintStream output, final String scriptText) {
        redirectedOutput = output;
        final CompilerConfiguration config = new CompilerConfiguration();
        config.setScriptBaseClass(PipelineScript.class.getName());

        final GroovyShell shell = new GroovyShell(config);
        script = (PipelineScript)shell.parse(scriptText);

        final DefaultPipeline pipeline = new DefaultPipeline();
        script.init(pipeline);
    }

    public void run() {
        try {
            startCapturingOutput();
            script.run();
        } finally {
            stopCapturingOutput();
        }
    }

    private void startCapturingOutput() {
        originalStdOut = System.out;
        System.setOut(redirectedOutput);
        originalStdErr = System.err;
        System.setErr(redirectedOutput);
    }

    private void stopCapturingOutput() {
        try {
            System.setOut(originalStdOut);
            System.setErr(originalStdErr);
            redirectedOutput.flush();
        } finally {
            originalStdOut = null;
            originalStdErr = null;
        }
    }
}
