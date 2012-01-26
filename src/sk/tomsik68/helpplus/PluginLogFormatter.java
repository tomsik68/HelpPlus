package sk.tomsik68.helpplus;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import org.bukkit.plugin.PluginDescriptionFile;

public class PluginLogFormatter extends Formatter {
	private final PluginDescriptionFile pdf;

	public PluginLogFormatter(PluginDescriptionFile pdf) {
		this.pdf = pdf;
	}

	@Override
	public String format(LogRecord record) {
		if (record.getThrown() == null)
			return "[" + pdf.getName() + " v" + pdf.getVersion() + "]" + record.getMessage();
		StackTraceElement[] stes = record.getThrown().getStackTrace();
		ByteArrayOutputStream baos;
		PrintWriter pw = new PrintWriter(baos = new ByteArrayOutputStream());
		pw.println("[" + pdf.getName() + " v" + pdf.getVersion() + "]" + "Error encountered:");
		pw.println(record.getThrown().getClass().getName());
		pw.println("  from" + record.getSourceClassName() + "." + record.getSourceMethodName());
		for (StackTraceElement ste : stes) {
			// indent
			pw.print("  ");
			pw.print("at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber());
		}
		pw.flush();
		pw.close();
		return new String(baos.toByteArray());
	}

}