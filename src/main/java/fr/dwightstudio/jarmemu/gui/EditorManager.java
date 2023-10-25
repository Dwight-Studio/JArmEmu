package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.util.RegisterName;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditorManager {
    private static final String[] INSTRUCTIONS = getFromEnum(Instruction.values(), false);
    private static final String[] KEYWORDS = getFromEnum(Keyword.values(), false);
    private static final String[] REGISTERS = getFromEnum(RegisterName.values(), false);
    private static final String[] CONDITIONS = getFromEnum(Condition.values(), true);
    private static final String[] DATA_MODES = getFromEnum(DataMode.values(), true);
    private static final String[] UPDATE_MODES = getFromEnum(UpdateMode.values(), true);
    private static final String[] UPDATE_FLAG = new String[]{"S", ""};

    private static final String INSTRUCTION_PATTERN = "\\b(?i)(" + String.join("|", INSTRUCTIONS) + ")(" + String.join("|", CONDITIONS) + ")((" + String.join("|", DATA_MODES) + ")|(" + String.join("|", UPDATE_FLAG) + ")|(" + String.join("|", UPDATE_MODES) + "))\\b";
    private static final String KEYWORD_PATTERN = "\\.\\b(?i)(" + String.join("|", KEYWORDS) + ")(?-i)\\b";
    private static final String REGISTER_PATTERN = "\\b(?i)(" + String.join("|", REGISTERS) + ")(?-i)\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String LABEL_PATTERN = "[A-Za-z_0-9]+:";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "@[^\n]*";
    private static final String IMM_PATTERN = "=[^\n]*|#[^\n]*";
    private static final Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")" + "|(?<REGISTER>" + REGISTER_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<LABEL>" + LABEL_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")" + "|(?<IMM>" + IMM_PATTERN + ")");
    private static final String sampleCode = String.join("\n", new String[]{".text", ".global _start", "_start:", "\t"});

    private final Logger logger = Logger.getLogger(getClass().getName());

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private CodeArea codeArea;
    private JArmEmuApplication application;
    private int[] lineIndex;

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass = matcher.group("INSTRUCTION") != null ? "instruction" : matcher.group("KEYWORD") != null ? "keyword" : matcher.group("REGISTER") != null ? "register" : matcher.group("BRACE") != null ? "brace" : matcher.group("BRACKET") != null ? "bracket" : matcher.group("LABEL") != null ? "label" : matcher.group("STRING") != null ? "string" : matcher.group("COMMENT") != null ? "comment" :  matcher.group("IMM") != null ? "imm" : null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private static <T extends Enum<T>> String[] getFromEnum(T[] list, boolean addEmpty) {
        ArrayList<String> rtn = new ArrayList<>();

        for (T elmt : list) {
            rtn.add(elmt.toString().toUpperCase());
        }

        if (addEmpty) rtn.add("");

        return rtn.toArray(new String[0]);
    }

    public void init(JArmEmuApplication application) {
        this.application = application;

        this.codeArea = application.controller.codeArea;
        this.executor = Executors.newSingleThreadExecutor();

        codeArea.setParagraphGraphicFactory(new JARMEmuLineFactory());
        codeArea.setContextMenu(new DefaultContextMenu(codeArea));

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
                //.successionEnds(Duration.ofMillis(500))
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
        codeArea.getStylesheets().add(EditorManager.class.getResource("editor-style.css").toExternalForm());
    }

    public void newFile() {
        codeArea.clear();
        codeArea.replaceText(0, 0, sampleCode);
    }

    public void registerLines() {
        String[] lines = codeArea.getText().split("\n");
        lineIndex = new int[lines.length];
        int p = 0;
        for (int i = 0; i < lines.length; i++) {
            p += lines[i].length();
            lineIndex[i] = p;
        }
    }

    public void markLineAsExecuted(int line) {
        if (line >= lineIndex.length) return;
        codeArea.selectRange(lineIndex[line] - 2, lineIndex[line] - 1);
        codeArea.selectLine();
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

    private static class DefaultContextMenu extends ContextMenu {
        MenuItem cut;
        MenuItem copy;
        MenuItem paste;

        public DefaultContextMenu(CodeArea codeArea) {
            cut = new MenuItem("Cut");
            copy = new MenuItem("Copy");
            paste = new MenuItem("Paste");

            cut.setOnAction((actionEvent -> {
                ClipboardContent content = new ClipboardContent();
                content.putString(codeArea.getSelectedText());
                Clipboard.getSystemClipboard().setContent(content);
                codeArea.replaceSelection("");
            }));

            copy.setOnAction((actionEvent -> {
                ClipboardContent content = new ClipboardContent();
                content.putString(codeArea.getSelectedText());
                Clipboard.getSystemClipboard().setContent(content);
            }));

            paste.setOnAction((actionEvent -> {
                codeArea.replaceSelection(Clipboard.getSystemClipboard().getString());
            }));

            getItems().add(cut);
            getItems().add(copy);
            getItems().add(paste);
        }
    }
}
