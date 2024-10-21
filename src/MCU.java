import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MCU {

	ArrayList<Integer> memory;
	Test cache;
	String printerBuffer;
	String keyboardBuffer;
	String cardBuffer;

	public MCU() {
		this.memory = new ArrayList<Integer>(Constants.MEMORY_WORDS_BOUND);
		for (int i = 0; i < Constants.MEMORY_WORDS_BOUND; i++) {
			this.memory.add(0);
		}
		this.cache = new Test();
	}

	public String getPrinterBuffer() {
		return printerBuffer;
	}

	public void setPrinterBuffer(String printerBuffer) {
		this.printerBuffer = printerBuffer;
	}

	public String getKeyboardBuffer() {
		return keyboardBuffer;
	}

	public Test getCache() {
		return cache;
	}

	public int getCurrentMemorySize() {
		if (this.memory != null) {
			return this.memory.size();
		}
		return 0;
	}

	public int fetchFromMemory(int address) {
		return this.memory.get(address);
	}

	public void storeIntoMemory(int address, int value) {
		if (this.memory != null) {
			this.memory.set(address, value);
		}
	}

	public int fetchFromCache(int address) {
		for (Test.CacheLine line : cache.getCacheLines()) { // check every block
														// whether the tag is
														// already exist
			if (address == line.getTag()) {
				return line.getData(); // tag exist, return the data of the
										// block
			}
		}
		// tag not exist
		int value = fetchFromMemory(address);
		cache.add(address, value);
		return value;
	}

	public void storeIntoCache(int address, int value) {
		storeIntoMemory(address, value);
		for (Test.CacheLine line : cache.getCacheLines()) { // check every block the
														// tag is already exist
			if (address == line.getTag()) {
				line.setData(value); // replace the block
				return;
			}
		}
		// tag not exist
		cache.add(address, value);
	}

	public void loadProgramFile(String inputFile) throws Exception {
		String filePath = inputFile; // Adjust the file path as necessary
		File selectedFile = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(selectedFile));
			String data = null;
			while ((data = br.readLine()) != null) {
				final String[] inp = data.split("\\s+");
				if (inp.length >= 2) { // Ensure there are at least two elements to prevent ArrayIndexOutOfBoundsException
					System.out.println("Address: " + inp[0] + ", Value: " + inp[1]);
					final String st = Integer.toBinaryString(Integer.parseInt(inp[1], 8)); // Convert the octal value to binary string for further processing
					FrontPanel.arrayList.add(Integer.parseInt(inp[0], 8));
					FrontPanel.mapList.put(Integer.parseInt(inp[0], 8), st);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe2) {
				ioe2.printStackTrace();
			}
		}
	}

	public void setKeyboardBuffer(String keyboardBuffer) {
		this.keyboardBuffer = keyboardBuffer;
	}

	public String getCardBuffer() {
		return cardBuffer;
	}

	public void setCardBuffer(String cardBuffer) {
		this.cardBuffer = cardBuffer;
	}

	public void expandMemorySize() {
		if (this.memory != null && this.memory.size() > 0) {
			this.memory.ensureCapacity(Constants.MEMORY_WORDS_BOUND_EXPANDED);
			for (int currentSize = memory.size(); currentSize < Constants.MEMORY_WORDS_BOUND_EXPANDED; currentSize++) {
				this.memory.add(0);
			}
		}
		System.out.println("memory size has been expanded to " + memory.size());
	}

}


