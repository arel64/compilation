package AST;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AST_GRAPHVIZ
{
	private PrintWriter fileWriter;
	private static AST_GRAPHVIZ instance = null;
	private List<String> lines = new ArrayList<>();
	private AST_GRAPHVIZ() {}
	public static AST_GRAPHVIZ getInstance()
	{
		if (instance == null)
		{
			instance = new AST_GRAPHVIZ();
			try
			{
				String dirname = "./output/";
				String filename = "AST_IN_GRAPHVIZ_DOT_FORMAT.txt";
				instance.fileWriter = new PrintWriter(new FileWriter(dirname + filename, false));
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	public void logNode(int nodeSerialNumber, String nodeName)
	{
		lines.add("v" + nodeSerialNumber + " [label = \"" + nodeName.replace("\"", "\\\"") + "\"];");
	}
	public void metadataNode(int nodeSerialNumber, String metadata)
	{
		for(int i = 0; i < lines.size(); i++)
		{
			if(lines.get(i).startsWith("v" + nodeSerialNumber + " [label ="))
			{
				String line = lines.get(i);
				int start = line.indexOf("label = \"");
				int end = line.lastIndexOf("\"]");
				if(start != -1 && end != -1)
				{
					String labelContent = line.substring(start + 9, end);
					String newLabelContent = labelContent + "\\n" + metadata.replace("\"", "\\\"");
					String newLine = line.substring(0, start + 9) + newLabelContent + line.substring(end);
					lines.set(i, newLine);
				}
				break;
			}
		}
	}
	public void logEdge(int fatherNodeSerialNumber, int sonNodeSerialNumber)
	{
		lines.add("v" + fatherNodeSerialNumber + " -> v" + sonNodeSerialNumber + ";");
	}
	public void finalizeFile()
	{
		fileWriter.println("digraph");
		fileWriter.println("{");
		fileWriter.println("graph [ordering = \"out\"]");
		for(String line : lines)
		{
			fileWriter.println(line);
		}
		fileWriter.println("}");
		fileWriter.close();
	}
}
