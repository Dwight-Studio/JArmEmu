package fr.dwightstudio.jarmemu.util;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

public class RichTextUtils {

    public static Logger logger = Logger.getLogger(RichTextUtils.class.getName());
    public static PseudoClass EXECUTED_LIGHT = PseudoClass.getPseudoClass("executedLight");
    public static PseudoClass SCHEDULED_LIGHT = PseudoClass.getPseudoClass("scheduledLight");

    public static PseudoClass EXECUTED_DARK = PseudoClass.getPseudoClass("executedDark");
    public static PseudoClass SCHEDULED_DARK = PseudoClass.getPseudoClass("scheduledDark");


    public static Optional<Object> getParagraphBox(CodeArea codeArea, int i) {
        try {
            Field field = codeArea.getClass().getSuperclass().getSuperclass().getSuperclass().getField("virtualFlow");
            field.setAccessible(true);
            VirtualFlow flow = (VirtualFlow) field.get(codeArea);
            return (Optional<Object>) flow.getCellIfVisible(i);

        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
            return Optional.empty();
        }
    }

    public static void setPseudoClass(CodeArea codeArea, int i, boolean val, PseudoClass pseudoClass) {
        try {
            Class superClass = codeArea.getClass().getSuperclass().getSuperclass().getSuperclass();
            Field field = superClass.getDeclaredField("virtualFlow");
            field.setAccessible(true);
            VirtualFlow flow = (VirtualFlow) field.get(codeArea);
            Optional<Object> wrapper = flow.getCellIfVisible(i);

            if (wrapper.isEmpty()) return;

            Method method = wrapper.get().getClass().getMethod("getNode");
            method.setAccessible(true);
            Object box = method.invoke(wrapper.get());

            method = box.getClass().getMethod("pseudoClassStateChanged", PseudoClass.class, boolean.class);
            method.setAccessible(true);
            method.invoke(box, pseudoClass, val);
            /*
            method = box.getClass().getSuperclass().getMethod("getStylesheets");
            method.setAccessible(true);
            ObservableList<String> l = (ObservableList<String>) method.invoke(box);
            l.clear();
            l.add(Application.getUserAgentStylesheet());
            System.out.println(Application.getUserAgentStylesheet());
            */
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void setExecuted(CodeArea codeArea, int i, boolean val) {
        if (Application.getUserAgentStylesheet() == null) setPseudoClass(codeArea, i, val, EXECUTED_DARK);
        else setPseudoClass(codeArea, i, val, Application.getUserAgentStylesheet().endsWith("dark.css") ? EXECUTED_DARK : EXECUTED_LIGHT);
    }

    public static void setScheduled(CodeArea codeArea, int i, boolean val) {
        if (Application.getUserAgentStylesheet() == null) setPseudoClass(codeArea, i, val, SCHEDULED_DARK);
        else setPseudoClass(codeArea, i, val, Application.getUserAgentStylesheet().endsWith("dark.css") ? SCHEDULED_DARK : SCHEDULED_LIGHT);

    }
}
