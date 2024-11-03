import java.io.*;
import java.util.*;

public class Assembler {
    private Map<String, Integer> symbolTable = new HashMap<>();
    private int codeLocation = 0;

    public void passOne(String sourceFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue; // Skip empty lines and comments
                }

                String[] parts = line.split("\\s+", 2);
                if (parts[0].endsWith(":")) {
                    String label = parts[0].substring(0, parts[0].length() - 1);
                    symbolTable.put(label, codeLocation);
                    if (parts.length > 1) {
                        parts = parts[1].split("\\s+", 2);
                    } else {
                        continue; // Label alone on the line
                    }
                }

                if (parts[0].equalsIgnoreCase("LOC")) {
                    codeLocation = Integer.parseInt(parts[1].split("\\s+")[0]);
                } else if (parts[0].equalsIgnoreCase("DATA") || parts[0].equalsIgnoreCase("HLT") ||
                        parts[0].equalsIgnoreCase("LDX") || parts[0].equalsIgnoreCase("LDR") ||
                        parts[0].equalsIgnoreCase("LDA") || parts[0].equalsIgnoreCase("JZ")) {
                    codeLocation++; // Allocate memory for data and instructions
                }
            }
        }
    }

    public void passTwo(String sourceFile, String listingFile, String loadFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
             PrintWriter listingWriter = new PrintWriter(new FileWriter(listingFile));
             PrintWriter loadWriter = new PrintWriter(new FileWriter(loadFile))) {

            String line;
            codeLocation = 0; // Reset for the second pass
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    listingWriter.println(line); // Print comments and empty lines
                    continue;
                }

                String[] parts = line.split("\\s+", 2);
                StringBuilder listingLine = new StringBuilder();

                try {
                    // Process the instruction
                    if (parts[0].equalsIgnoreCase("LOC")) {
                        codeLocation = Integer.parseInt(parts[1].split("\\s+")[0]);
                        listingWriter.println(" " + line); // Print LOC directive with a leading space
                    } else if (parts[0].equalsIgnoreCase("DATA")) {
                        int value = 0;
                        String[] dataParts = parts[1].split("\\s+", 2);
                        if (symbolTable.containsKey(dataParts[0])) {
                            value = symbolTable.get(dataParts[0]);
                        } else {
                            value = Integer.parseInt(dataParts[0]);
                        }
                        listingLine.append(String.format("%06o %06o ", codeLocation, value));
                        listingLine.append(line);
                        loadWriter.printf("%06o %06o%n", codeLocation, value);
                        codeLocation++;
                    } else if (parts[0].equalsIgnoreCase("End:")) {
                        listingLine.append(String.format("%06o %06o ", codeLocation, 0));
                        listingLine.append(line);
                        loadWriter.printf("%06o %06o%n", codeLocation, 0);
                    } else if (parts[0].equalsIgnoreCase("HLT") || parts[0].equalsIgnoreCase("LDX") ||
                            parts[0].equalsIgnoreCase("LDR") || parts[0].equalsIgnoreCase("LDA") ||
                            parts[0].equalsIgnoreCase("JZ")) {
                        int instruction = encodeInstruction(parts[0], parts.length > 1 ? parts[1] : "");
                        listingLine.append(String.format("%06o %06o ", codeLocation, instruction));
                        listingLine.append(line);
                        loadWriter.printf("%06o %06o%n", codeLocation, instruction);
                        codeLocation++;
                    } else if (parts[0].endsWith(":")) {
                        listingWriter.println(line); // Print labels as is
                        continue;
                    } else {
                        System.err.println("Error: Unknown instruction " + parts[0]);
                    }

                    // Write to the listing file
                    listingWriter.println(listingLine);
                } catch (NumberFormatException e) {
                    System.err.println("Error processing line: " + line);
                    System.err.println("Error details: " + e.getMessage());
                }
            }
        }
    }

    private int encodeInstruction(String opcode, String operands) {
        int encodedInstruction = 0;

        // Remove comments from operands
        int commentIndex = operands.indexOf(';');
        if (commentIndex != -1) {
            operands = operands.substring(0, commentIndex).trim();
        }

        String[] operandParts = operands.split(",");

        switch (opcode.toUpperCase()) {
            case "LDX":
                encodedInstruction = 0x102000; // Base opcode for LDX
                encodedInstruction |= Integer.parseInt(operandParts[0].trim()) << 9; // Register
                encodedInstruction |= Integer.parseInt(operandParts[1].trim()); // Address
                break;
            case "LDR":
                encodedInstruction = 0x000000; // Base opcode for LDR
                encodedInstruction |= Integer.parseInt(operandParts[0].trim()) << 13; // Register
                encodedInstruction |= Integer.parseInt(operandParts[1].trim()) << 9; // Index
                encodedInstruction |= Integer.parseInt(operandParts[2].trim()); // Address
                if (operandParts.length > 3) {
                    encodedInstruction |= 0x000400; // Set bit for indirect addressing
                }
                break;
            case "LDA":
                encodedInstruction = 0x006000; // Base opcode for LDA
                encodedInstruction |= Integer.parseInt(operandParts[0].trim()) << 13; // Register
                encodedInstruction |= Integer.parseInt(operandParts[1].trim()) << 9; // Index
                encodedInstruction |= Integer.parseInt(operandParts[2].trim()); // Address
                break;
            case "JZ":
                encodedInstruction = 0x020000; // Base opcode for JZ
                encodedInstruction |= Integer.parseInt(operandParts[0].trim()) << 9; // Register
                encodedInstruction |= Integer.parseInt(operandParts[1].trim()) << 5; // Index
                encodedInstruction |= Integer.parseInt(operandParts[2].trim()); // Address
                break;
            case "HLT":
                encodedInstruction = 0x000000; // Opcode for HLT
                break;
        }
        return encodedInstruction;
    }

    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        String sourceFile = "source.txt"; // Input source file
        String listingFile = "listing.txt"; // Output listing file
        String loadFile = "load.txt"; // Output load file

        try {
            assembler.passOne(sourceFile);
            assembler.passTwo(sourceFile, listingFile, loadFile);
            System.out.println("Assembly completed successfully.");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}