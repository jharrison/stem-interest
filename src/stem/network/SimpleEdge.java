package stem.network;

import java.io.Serializable;

public class SimpleEdge implements Serializable
{
	private static final long serialVersionUID = 1L;

	public SimpleEdge(String string) {
		// TODO Auto-generated constructor stub
		type = string;
	}

	// Enumeration<String> type;
	public String type;

}
