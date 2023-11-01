package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.JArmEmuApplication;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class EditorManager {
    private static final String[] INSTRUCTIONS = getFromEnum(Instruction.values(), false);
    private static final String[] KEYWORDS = getFromEnum(PseudoInstruction.values(), false);
    private static final String[] REGISTERS = getFromEnum(RegisterUtils.values(), false);
    private static final String[] CONDITIONS = getFromEnum(Condition.values(), true);
    private static final String[] DATA_MODES = getFromEnum(DataMode.values(), true);
    private static final String[] UPDATE_MODES = getFromEnum(UpdateMode.values(), true);
    private static final String[] SHIFTS = new String[]{"LSL", "LSR", "ASR", "ROR", "RRX"};
    private static final String[] UPDATE_FLAG = new String[]{"S", ""};

    private static final String INSTRUCTION_PATTERN = "\\b(?i)(" + String.join("|", INSTRUCTIONS) + ")(" + String.join("|", CONDITIONS) + ")((" + String.join("|", DATA_MODES) + ")|(" + String.join("|", UPDATE_FLAG) + ")|(" + String.join("|", UPDATE_MODES) + "))\\b";
    private static final String KEYWORD_PATTERN = "\\.\\b(?i)(" + String.join("|", KEYWORDS) + ")(?-i)\\b";
    private static final String REGISTER_PATTERN = "\\b(?i)(" + String.join("|", REGISTERS) + ")(?-i)\\b";
    private static final String SHIFT_PATTERN = "\\b(?i)(" + String.join("|", SHIFTS) + ")(?-i)\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String LABEL_PATTERN = "[A-Za-z_0-9]+:";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "@[^\n]*";
    private static final String IMM_PATTERN = "=[^\n]*|#[^\n\\]]*";
    private static final Pattern PATTERN = Pattern.compile("(?<SHIFT>" + SHIFT_PATTERN + ")" + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")" + "|(?<REGISTER>" + REGISTER_PATTERN + ")" + "|(?<IMM>" + IMM_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<LABEL>" + LABEL_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");
    private static final String sampleCode = String.join("\n", new String[]{".text", "_start:", "\t@ Beginning of the program"});

    private final Logger logger = Logger.getLogger(getClass().getName());

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public CodeArea codeArea;
    private JArmEmuApplication application;
    private JARMEmuLineFactory lineFactory;
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("INSTRUCTION") != null ? "instruction" : matcher.group("KEYWORD") != null ? "keyword" : matcher.group("SHIFT") != null ? "shift" : matcher.group("REGISTER") != null ? "register" : matcher.group("BRACE") != null ? "brace" : matcher.group("BRACKET") != null ? "bracket" : matcher.group("LABEL") != null ? "label" : matcher.group("STRING") != null ? "string" : matcher.group("COMMENT") != null ? "comment" :  matcher.group("IMM") != null ? "imm" : null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**
     * Initialisation du module
     * @param application l'application à laquelle se rattacher
     */
    public void init(JArmEmuApplication application) {
        this.application = application;

        this.codeArea = application.controller.codeArea;
        this.executor = Executors.newSingleThreadExecutor();

        this.lineFactory = new JARMEmuLineFactory();

        codeArea.setParagraphGraphicFactory(lineFactory);
        codeArea.setContextMenu(new EditorContextMenu(codeArea));

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = codeArea.getCaretPosition();
                int currentParagraph = codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        // Auto-add closing char
        codeArea.addEventHandler( KeyEvent.KEY_TYPED, KE ->
        {
            int caretPosition = codeArea.getCaretPosition();
            Platform.runLater( () -> {
                switch (KE.getCharacter()) {
                    case "[" -> {
                        codeArea.insertText( caretPosition,"]" );
                        codeArea.moveTo(caretPosition);
                    }

                    case "{" -> {
                        codeArea.insertText( caretPosition,"}" );
                        codeArea.moveTo(caretPosition);
                    }

                    case "\"" -> {
                        codeArea.insertText( caretPosition,"\"" );
                        codeArea.moveTo(caretPosition);
                    }
                }
            } );
        });

        Subscription cleanupWhenFinished = codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(50))
                .retainLatestUntilLater(executor)
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        logger.log(Level.WARNING, ExceptionUtils.getStackTrace(t.getFailure()));
                        return Optional.empty();
                    }
                }).subscribe(this::applyHighlighting);

        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`

        newFile();
        codeArea.getStyleClass().add("editor");
        codeArea.getStylesheets().add(EditorManager.class.getResource("editor-style.css").toExternalForm());
    }

    public void newFile() {
        codeArea.clear();
        codeArea.replaceText(0, 0, sampleCode);
    }

    public boolean hasBreakPoint(int line) {
        AtomicBoolean flag = new AtomicBoolean(false);
        lineFactory.breakpoints.forEach(ln -> {
            if (ln == line) flag.set(true);
        });
        return flag.get();
    }

    public void markLine(int line, LineStatus status) {
        if (line >= 0) {
            codeArea.moveTo(line, 0);
            codeArea.requestFollowCaret();
            lineFactory.nums.get(line).accept(status);
        }
    }

    public void clearLineMarking() {
        lineFactory.nums.forEach((k, v) -> v.accept(LineStatus.NONE));
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                application.setUnsaved();
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    public void clean() {
        executor.shutdown();
    }
}
