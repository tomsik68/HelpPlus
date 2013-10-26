package sk.tomsik68.helpplus.msgs;

public class MessageFormatter {
    public static String format(String template, Object... variables) {
        for (int i = 0; i < variables.length; i++) {
            if (template.contains("${" + i + "}")) {
                template = template.replace("${" + i + "}", variables[i].toString());
            }
        }
        return template;
    }
}
