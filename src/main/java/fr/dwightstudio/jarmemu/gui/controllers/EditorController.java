package fr.dwightstudio.jarmemu.gui.controllers;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import fr.dwightstudio.jarmemu.asm.*;
import fr.dwightstudio.jarmemu.gui.EditorContextMenu;
import fr.dwightstudio.jarmemu.gui.JArmEmuApplication;
import fr.dwightstudio.jarmemu.gui.JArmEmuLineFactory;
import fr.dwightstudio.jarmemu.gui.LineStatus;
import fr.dwightstudio.jarmemu.sim.exceptions.SyntaxASMException;
import fr.dwightstudio.jarmemu.util.RegisterUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.reactfx.Subscription;

import java.net.URL;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.dwightstudio.jarmemu.util.EnumUtils.getFromEnum;

public class EditorController extends AbstractJArmEmuModule {
    private static final String[] INSTRUCTIONS = getFromEnum(Instruction.values(), false);
    private static final String[] KEYWORDS = ArrayUtils.addAll(getFromEnum(Directive.values(), false), getFromEnum(Section.values(), false));
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
    private static final String STRING_PATTERN = "\"([^\"\\\\@]|\\\\.)*\"|\'([^\'\\\\@]|\\\\.)*\'";
    private static final String COMMENT_PATTERN = "@[^\n]*";
    private static final String IMM_PATTERN = "=[^\n@]*|#[^\n\\]@]*";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<NEWLINE>\n)"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<SHIFT>" + SHIFT_PATTERN + ")"
            + "|(?<LABEL>" + LABEL_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")"
            + "|(?<REGISTER>" + REGISTER_PATTERN + ")"
            + "|(?<IMM>" + IMM_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
    );
    private static final String SAMPLE_CODE = String.join("\n", new String[]{".global _start", ".text", "_start:", "\t@ Beginning of the program"});

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final ExecutorService executor;
    private final JArmEmuApplication application;
    private final JArmEmuLineFactory lineFactory;
    private Subscription hightlightUpdateSubscription;

    public EditorController(JArmEmuApplication application) {
        super(application);
        this.application = application;
        this.executor = Executors.newSingleThreadExecutor();
        this.lineFactory = new JArmEmuLineFactory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getController().codeArea = application.getController().codeArea;
        getController().codeArea.setParagraphGraphicFactory(lineFactory);
        getController().codeArea.setContextMenu(new EditorContextMenu(getController().codeArea));

        // auto-indent: insert previous line's indents on enter
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        getController().codeArea.addEventHandler( KeyEvent.KEY_PRESSED, KE ->
        {
            if ( KE.getCode() == KeyCode.ENTER ) {
                int caretPosition = getController().codeArea.getCaretPosition();
                int currentParagraph = getController().codeArea.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher( getController().codeArea.getParagraph( currentParagraph-1 ).getSegments().get( 0 ) );
                if ( m0.find() ) Platform.runLater( () -> getController().codeArea.insertText( caretPosition, m0.group() ) );
            }
        });

        // Auto-add closing char
        getController().codeArea.addEventHandler( KeyEvent.KEY_TYPED, KE ->
        {
            int caretPosition = getController().codeArea.getCaretPosition();
            Platform.runLater( () -> {
                switch (KE.getCharacter()) {
                    case "[" -> {
                        getController().codeArea.insertText( caretPosition,"]" );
                        getController().codeArea.moveTo(caretPosition);
                    }

                    case "{" -> {
                        getController().codeArea.insertText( caretPosition,"}" );
                        getController().codeArea.moveTo(caretPosition);
                    }

                    case "\"" -> {
                        getController().codeArea.insertText( caretPosition,"\"" );
                        getController().codeArea.moveTo(caretPosition);
                    }
                }
            } );
        });

        hightlightUpdateSubscription = getController().codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(50))
                .retainLatestUntilLater(executor)
                .supplyTask(this::autoComputeHighlightingAsync)
                .awaitLatest(getController().codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        logger.log(Level.WARNING, ExceptionUtils.getStackTrace(t.getFailure()));
                        return Optional.empty();
                    }
                }).subscribe((highlighting) -> getController().codeArea.setStyleSpans(0, highlighting));

        newFile();
        getController().codeArea.getStylesheets().add(EditorController.class.getResource("editor-style.css").toExternalForm());
    }

    /**
     * Ajoute une notification sur l'éditeur (5 maximums).
     *
     * @param titleString le titre (en gras)
     * @param contentString le corps du message
     * @param classString la classe à utiliser (Classes de BootstrapFX)
     */
    public void addNotif(String titleString, String contentString, String classString) {

        if (getController().notifications.getChildren().size() > 5) return;

        Message message;

        switch (classString) {
            case Styles.ACCENT -> message = new Message(titleString, contentString, new FontIcon(Material2OutlinedAL.INFO));
            case Styles.SUCCESS -> message = new Message(titleString, contentString, new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE));
            case Styles.WARNING -> message = new Message(titleString, contentString, new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG));
            case Styles.DANGER -> message = new Message(titleString, contentString, new FontIcon(Material2OutlinedAL.ERROR_OUTLINE));

            default -> message = new Message(titleString, contentString);
        }

        message.getStyleClass().add(classString);

        getController().notifications.getChildren().add(message);
    }

    /**
     * Affiche une notification relative à une AssemblyError.
     *
     * @param exception l'erreur en question
     */
    protected void addError(SyntaxASMException exception) {
        if (exception.getObject() != null) {
            logger.info("Error parsing " + exception.getObject().toString() + " at line " + exception.getLine());
        } else {
            logger.info("Error parsing code at line " + exception.getLine());
        }
        logger.log(Level.INFO, ExceptionUtils.getStackTrace(exception));
        if (exception.isLineSpecified()) {
            addNotif(exception.getTitle(), exception.getMessage() + " at line " + exception.getLine(), Styles.DANGER);
        } else {
            addNotif(exception.getTitle(), exception.getMessage(), Styles.DANGER);
        }
    }

    /**
     * Supprime les notifications
     */
    protected void clearNotifs() {
        getController().notifications.getChildren().clear();
    }

    public void newFile() {
        getController().codeArea.clear();
        getController().codeArea.replaceText(0, 0, SAMPLE_CODE);
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
            if (status == LineStatus.SCHEDULED) {
                getController().codeArea.moveTo(line, 0);
                getController().codeArea.requestFollowCaret();
            }

            this.lineFactory.markLine(line, status == null ? LineStatus.NONE : status);
        }
    }

    public void clearLineMarking() {
        for (int i = 0 ; i < getController().codeArea.getParagraphs().size() ; i++) {
            this.lineFactory.markLine(i, LineStatus.NONE);
        }
    }

    /**
     * Méthode utilisée pour automatiquement mettre à jour la colorimétrie
     *
     * @return la tache associée
     */
    private ComputeHightlightsTask autoComputeHighlightingAsync() {
        String text = getController().codeArea.getText();

        ComputeHightlightsTask task = new ComputeHightlightsTask(text, false);

        executor.execute(task);
        return task;
    }

    /**
     * Appel une mise à jour de la colorimétrie "manuellement"
     */
    private void computeHighlightingAsync() {
        String text = getController().codeArea.getText();

        ComputeHightlightsTask task = new ComputeHightlightsTask(text, true);

        executor.execute(task);
    }

    /**
     * Nettoie l'espace de travail dans le cas ou on voudrait le réinitialiser
     */
    public void clean() {
        hightlightUpdateSubscription.unsubscribe();
        executor.shutdown();
    }

    /**
     * @return le texte de l'éditeur
     */
    public String getText() {
        return getController().codeArea.getText();
    }

    public void prepareSimulation() {
        int lineNum = getController().codeArea.getParagraphs().size();
        logger.info("Pre-generate " + lineNum + " lines");
        lineFactory.pregenAll(getController().codeArea.getParagraphs().size());
        Platform.runLater(this::clearLineMarking);
    }

    private class ComputeHightlightsTask extends Task<StyleSpans<Collection<String>>> {
        private final String text;
        private final boolean update;

        public ComputeHightlightsTask(String text, boolean update) {
            this.text = text;
            this.update = update;
        }

        @Override
        protected StyleSpans<Collection<String>> call() {
            Matcher matcher = PATTERN.matcher(text);
            int lastKwEnd = 0;
            int line = 0;
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
            while (matcher.find()) {
                if (matcher.group("NEWLINE") != null) {
                    line++;
                    continue;
                }

                String styleClass = matcher.group("COMMENT") != null ? "comment"
                        : matcher.group("STRING") != null ? "string"
                        : matcher.group("SHIFT") != null ? "shift"
                        : matcher.group("LABEL") != null ? "label"
                        : matcher.group("KEYWORD") != null ? "keyword"
                        : matcher.group("INSTRUCTION") != null ? "instruction"
                        : matcher.group("REGISTER") != null ? "register"
                        : matcher.group("BRACE") != null ? "brace"
                        : matcher.group("BRACKET") != null ? "bracket"
                        : matcher.group("IMM") != null ? "imm" : null;

                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }

            application.updateSaveState();
            StyleSpans<Collection<String>> styleSpans = spansBuilder.create();

            if (this.update) {
                Platform.runLater(() -> getController().codeArea.setStyleSpans(0, styleSpans));
            }
            return styleSpans;
        }
    }
}
