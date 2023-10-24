package fr.dwightstudio.jarmemu.gui;

import fr.dwightstudio.jarmemu.asm.Condition;
import fr.dwightstudio.jarmemu.asm.Instruction;
import fr.dwightstudio.jarmemu.asm.Keyword;
import fr.dwightstudio.jarmemu.util.RegisterName;
import javafx.concurrent.Task;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
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

    private static final String[] INSTRUCTIONS = getMultiCasesFromEnum(Instruction.values());
    private static final String[] KEYWORDS = getMultiCasesFromEnum(Keyword.values());
    private static final String[] REGISTERS = getMultiCasesFromEnum(RegisterName.values());
    private static final String[] CONDITIONS = getMultiCasesFromEnum(Condition.values());

    private static final String INSTRUCTION_PATTERN = "\\b(" + String.join("|", addCondition(INSTRUCTIONS)) + ")\\b"; // TODO: Ajouter tous les autre trucs de merde (Modes d'accès, etc)
    private static final String KEYWORD_PATTERN = "\\.\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String REGISTER_PATTERN = "\\b(" + String.join("|", REGISTERS) + ")\\b";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String LABEL_PATTERN = "[A-Za-z_0-9]+:";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "@[^\n]*";
    private static final String IMM_PATTERN = "=[^\n]*|#[^\n]*";
    private static final Pattern PATTERN = Pattern.compile("(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")" + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<REGISTER>" + REGISTER_PATTERN + ")" + "|(?<BRACE>" + BRACE_PATTERN + ")" + "|(?<BRACKET>" + BRACKET_PATTERN + ")" + "|(?<LABEL>" + LABEL_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")" + "|(?<IMM>" + IMM_PATTERN + ")");
    private static final String sampleCode = String.join("\n", new String[]{".text", ".global _start", "_start:", "\t"});

    private final Logger logger = Logger.getLogger(getClass().getName());

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private CodeArea codeArea;

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

    private static String[] addCondition(String[] strings) {
        String[] rtn = new String[strings.length * (CONDITIONS.length + 1)];
        for (int i = 0; i < strings.length; i++) {
            rtn[i * CONDITIONS.length] = strings[i];
            for (int j = 0; j < CONDITIONS.length; j++) {
                rtn[i * CONDITIONS.length + j + 1] = strings[i] + CONDITIONS[j];
            }
        }
        return rtn;
    }

    private static <T extends Enum<T>> String[] getMultiCasesFromEnum(T[] list) {
        ArrayList<String> rtn = new ArrayList<>();

        for (T elmt : list) {
            rtn.add(elmt.name().toUpperCase());
            rtn.add(elmt.name().toLowerCase());
            rtn.add(elmt.name().toLowerCase().toUpperCase().charAt(0) + elmt.name().toLowerCase().substring(1));
        }

        return rtn.toArray(new String[0]);
    }

    public void init(CodeArea codeArea) {

        this.codeArea = codeArea;
        this.executor = Executors.newSingleThreadExecutor();

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
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

        codeArea.replaceText(0, 0, sampleCode);
        codeArea.getStylesheets().add(EditorManager.class.getResource("editor-style.css").toExternalForm());
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
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
