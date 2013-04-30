package chunking;

import java.util.Arrays;

public final class Chunk {

	private final int size; // the size of the chunk
	private final byte[] data; // the raw data of the chunk
	private String url; //the URL associated with the chunk

	// creates a chunnk with data full with zeroes
	public Chunk(int size) {
		this.size = size;
		this.data = new byte[size];
		for (int i = 0; i < size; i++) {
			this.data[i] = 0;
		}
	}

	public Chunk(int size, byte[] data, String u) {
		this.size = size;
		this.url = u;
		this.data = new byte[data.length];
		for (int i = 0; i < size; i++) {
			this.data[i] = data[i];
		}
	}

	public int getSize() {
		return this.size;
	}

	public byte[] getData() {
		return data;
	}
	
	public String getUrl(){
		return url;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Chunk)) {
			return false;
		}
		Chunk other = (Chunk) obj;
		if (other.getSize() != this.size) {
			return false;
		}

		byte[] odata = other.getData();
		for (int i = 0; i < this.size; i++) {
			if (this.data[i] != odata[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 401;
		int result = 1;

		result += prime * result + size;
		result += prime * result + Arrays.hashCode(data);
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		
		result += "Chunk[";
		result += "[size=" + String.valueOf(this.size);
		result += ", data = [ ";
		for (int i = 0; i < data.length - 1; i++) {
			result += String.valueOf(data[i]);
			result += ",";
		}
		result += String.valueOf(data[data.length - 1]);
		result += "]]";
		return result;
	}
}
