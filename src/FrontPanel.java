import java.lang.*;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.function.BiConsumer;

public class FrontPanel
{
    private Registers registers;
    private MCU mcu;

    private int flag;
    int anInt1;
    public static int anInt2;
    public static HashMap<Integer,String> mapList;
    public HashMap<String, Queue<String>> ipHashMap;
    private HashMap<String, JTextField> textHashMap = new HashMap<>();
    public static ArrayList<Integer> arrayList;

    private JPanel marObj, mbrObj, msrObj, irObj, pcObj, ccObj, mfrObj, pnlReg, pnlCache, pnlProg1,
            pnlProg2, pnxReg, pnsReg, pnkReg, r0Reg, r1Reg, r2Reg, r3Reg, x1Reg, x2Reg, x3Reg;
    private JButton singleStepButton, iplButton, resetButton, haltButton, storeButton, runButton, btnStoreMAR, btnStoreMBR, btnStoreMSR,
            btnStoreIR, btnStorePC, btnStoreCC, btnStoreMFR, btnStoreR0, btnStoreR1, btnStoreR2, btnStoreR3,
            btnStoreX1, btnStoreX2, btnStoreX3; // need to do
    private JTextArea consoleInput, consolePrint;
    private JLabel jLabelObj;
    private JTable jTableObj;
    private JScrollPane jScrollPaneObj;
    public JFrame jFrameObj;

    private static final Color BLUE_BG = new Color(173, 216, 230);

    // constructor initializes components and listeners and sets up the CPU.
    public FrontPanel() {

        this.anInt1 = 0;
        this.mapList = new HashMap<>();
        this.arrayList = new ArrayList<>();
        this.ipHashMap = new HashMap<>();

        initGUI(); // Initialize GUI
        addListeners(); // Adding listeners
        initCPU(); // Initialize CPU struct and objects

        // Initializing buttons with dynamic mouse listener object
        this.btnStoreR0.addMouseListener(createRegisterMouseListener(textHashMap.get("R0"), Registers::setR0));
        this.btnStoreR1.addMouseListener(createRegisterMouseListener(textHashMap.get("R1"), Registers::setR1));
        this.btnStoreR2.addMouseListener(createRegisterMouseListener(textHashMap.get("R2"), Registers::setR2));
        this.btnStoreR3.addMouseListener(createRegisterMouseListener(textHashMap.get("R3"), Registers::setR3));
        this.btnStoreX1.addMouseListener(createRegisterMouseListener(textHashMap.get("X1"), Registers::setX1));
        this.btnStoreX2.addMouseListener(createRegisterMouseListener(textHashMap.get("X2"), Registers::setX2));
        this.btnStoreX3.addMouseListener(createRegisterMouseListener(textHashMap.get("X3"), Registers::setX3));
        this.btnStoreMAR.addMouseListener(createRegisterMouseListener(textHashMap.get("MAR"), Registers::setMAR));
        this.btnStoreMBR.addMouseListener(createRegisterMouseListener(textHashMap.get("MBR"), Registers::setMBR));
        this.btnStoreMSR.addMouseListener(createRegisterMouseListener(textHashMap.get("MSR"), Registers::setMSR));
        this.btnStoreMFR.addMouseListener(createRegisterMouseListener(textHashMap.get("MFR"), Registers::setMFR));
        this.btnStorePC.addMouseListener(createRegisterMouseListener(textHashMap.get("PC"), Registers::setPC));
        this.btnStoreIR.addMouseListener(createRegisterMouseListener(textHashMap.get("IR"), Registers::setIR));
        this.btnStoreCC.addMouseListener(createRegisterMouseListener(textHashMap.get("CC"), Registers::setCC));

    }

    // Initialize GUI component
    private void initGUI() {
        this.jFrameObj = new JFrame();

        // Configuring application frame dimensions, and background
        jFrameObj.setTitle("COMPUTER SIMULATOR");
        jFrameObj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrameObj.getContentPane().setBackground(BLUE_BG);
        this.jFrameObj.setSize(new Dimension(1000, 600));

        this.pnlReg = newJPanel();
        this.pnxReg = newJPanel();
        this.pnsReg = newJPanel();
        this.pnkReg = newJPanel();

        // Adding the register buttons
        this.r0Reg = newRegJPanel("R0", true);
        this.btnStoreR0 = (JButton) r0Reg.getComponent(0);

        this.r1Reg = newRegJPanel("R1", true);
        this.btnStoreR1 = (JButton) r1Reg.getComponent(0);

        this.r2Reg = newRegJPanel("R2", true);
        this.btnStoreR2 = (JButton) r2Reg.getComponent(0);

        this.r3Reg = newRegJPanel("R3", true);
        this.btnStoreR3 = (JButton) r3Reg.getComponent(0);

        this.x1Reg = newRegJPanel("X1", true);
        this.btnStoreX1 = (JButton) x1Reg.getComponent(0);

        this.x2Reg = newRegJPanel("X2", true);
        this.btnStoreX2 = (JButton) x2Reg.getComponent(0);

        this.x3Reg = newRegJPanel("X3", true);
        this.btnStoreX3 = (JButton) x3Reg.getComponent(0);

        this.pcObj = newRegJPanel("PC", true);
        this.btnStorePC = (JButton) pcObj.getComponent(0);

        this.marObj = newRegJPanel("MAR", false);
        this.btnStoreMAR = (JButton) marObj.getComponent(0);

        this.mbrObj = newRegJPanel("MBR", false);
        this.btnStoreMBR = (JButton) mbrObj.getComponent(0);

        this.msrObj = newRegJPanel("MSR", false);
        this.btnStoreMSR = (JButton) msrObj.getComponent(0);

        this.irObj = newRegJPanel("IR", false);
        this.btnStoreIR = (JButton) irObj.getComponent(0);

        this.mfrObj = newRegJPanel("MFR", false);
        this.btnStoreMFR = (JButton) mfrObj.getComponent(0);

        this.ccObj = newRegJPanel("CC", false);
        this.btnStoreCC = (JButton) ccObj.getComponent(0);

        this.iplButton = new JButton("IPL");
        this.iplButton.setBounds(308, 650, 75, 48);
        this.iplButton.setBackground(Color.BLACK);

        this.singleStepButton = new JButton("SS");
        this.singleStepButton.setBounds(208, 650, 75, 48);
        this.singleStepButton.setBackground(Color.BLACK);

        addLayout(); // adding main frame layout
        addButtons(); // adding buttons
        addCache(); // adding cache to initialized objects
        addTabbedPane(); // adding the tabbed pane - console tab
    }

    // Function for dynamically creating the register panels that hold the text-fields, labels, and buttons
    private JPanel newRegJPanel(String name, boolean isEditable) {
        JTextField jTextField = new JTextField();
        jTextField.setName(name);
        jTextField.setBackground(Color.WHITE);
        jTextField.setEditable(isEditable);
        jTextField.setPreferredSize(new Dimension(100, 24));

        JPanel jPanel = newJPanel();

        JButton button = new JButton("Load");
        button.setPreferredSize(new Dimension(67, 24));
        jPanel.add(button);

        jPanel.add(jTextField);
        jPanel.add(new JLabel(name));

        textHashMap.put(name, jTextField);
        return jPanel;
    }

    // initializes the CPU components.
    private void initCPU() {
        this.registers = new Registers(); // Initializing registers
        this.mcu = new MCU(); // Initializing the MCU (Memory Control Unit)
        this.anInt1 = 0;
        this.registers.setPC(arrayList.isEmpty() ? 10 : arrayList.get(0)); //Initializing 10 as default, to be changed according to start address
    }

    // creating Dynamic Mouse Listener event for TextFields
    private MouseAdapter createRegisterMouseListener(JTextField jTextField, BiConsumer<Registers, Integer> registerVals) {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String errMessage = "Please enter octal value only.";
                try {
                    String valueStr = jTextField.getText();
                    int octalValue = Integer.parseInt(valueStr, 8); // Parse value to octal value
                    if (valueStr.equals(Integer.toString(octalValue, 8))) { //checking if it is octal value
                        registerVals.accept(FrontPanel.this.registers, octalValue);
                        String message = jTextField.getName() + " ->" + valueStr;
                        System.out.println(message);
                        FrontPanel.this.printConsole(message);
                    } else {
                        // If octal parsing fails, display error
                        System.out.println(errMessage);
                        FrontPanel.this.printConsole(errMessage);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println(errMessage);
                    FrontPanel.this.printConsole(errMessage);
                }
            }
        };
    }

    // adding mouse listeners to buttons
    private void addListeners() {
        // Mouse listener for Single-Step button - one instruction on click
        this.singleStepButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (FrontPanel.this.anInt1 < FrontPanel.mapList.size()) {
                    final int value = Integer.parseInt(FrontPanel.mapList.get(FrontPanel.arrayList.get(FrontPanel.this.anInt1)), 2);
                    FrontPanel.this.mcu.storeIntoCache(FrontPanel.this.registers.getPC(), value);
                    FrontPanel.this.registers.setMAR(Integer.parseInt(Integer.toOctalString(FrontPanel.this.registers.getPC()), 8));
                    FrontPanel.this.registers.setMBR(Integer.parseInt(Integer.toOctalString(FrontPanel.this.mcu.fetchFromCache(FrontPanel.this.registers.getMAR()))));
                    FrontPanel.this.registers.setIR(FrontPanel.this.registers.getMBR());
                    final String ins = FrontPanel.this.registers.getBinaryStringIr();
                    FrontPanel.this.runInstruction(ins, FrontPanel.this.registers, FrontPanel.this.mcu); // ins needs to be a 16-char binary string
                    FrontPanel.this.printConsole("PC: " + FrontPanel.this.registers.getPC() + ", instruction: " + ins);
                    FrontPanel.this.refreshRegistersPanel();
                    final FrontPanel this$0 = FrontPanel.this;
                    ++this$0.anInt1;
                }
                else {
                    System.out.println("End of Program");
                }
            }
        });

        // Mouse listener for Run button - all instructions executed on click
        this.runButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                while (FrontPanel.this.anInt1 < FrontPanel.mapList.size()) {
                    final int value = Integer.parseInt(FrontPanel.mapList.get(FrontPanel.arrayList.get(FrontPanel.this.anInt1)), 2);
                    FrontPanel.this.mcu.storeIntoCache(FrontPanel.this.registers.getPC(), value);
                    FrontPanel.this.registers.setMAR(Integer.parseInt(Integer.toOctalString(FrontPanel.this.registers.getPC()), 8));
                    FrontPanel.this.registers.setMBR(Integer.parseInt(Integer.toOctalString(FrontPanel.this.mcu.fetchFromCache(FrontPanel.this.registers.getMAR()))));
                    FrontPanel.this.registers.setIR(FrontPanel.this.registers.getMBR());
                    final String ins = FrontPanel.this.registers.getBinaryStringIr();
                    FrontPanel.this.runInstruction(ins, FrontPanel.this.registers, FrontPanel.this.mcu); // ins needs to be a 16-char binary string
                    FrontPanel.this.printConsole("PC: " + FrontPanel.this.registers.getPC() + ", instruction: " + ins);
                    FrontPanel.this.refreshRegistersPanel();
                    final FrontPanel this$0 = FrontPanel.this;
                    ++this$0.anInt1;
                }
            }
        });

        // Mouse listener for Reset button listener
        this.resetButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                clearAll(); // clears all fields and console
            }
        });

        // Mouse listener for Initial Program Load button - Loads the program into memory and enables Single Step or Run execution
        this.iplButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {

                if (!FrontPanel.this.iplButton.isEnabled()) {
                    return; // if the IPL button is disabled, Exit
                }

                // Initialize the CPU and Memory, and disable the IPL button
                FrontPanel.this.initCPU();

                // Load the input text file
                try {
                    FrontPanel.this.mcu.loadProgramFile("input.txt");
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }

                FrontPanel.this.iplButton.setEnabled(false);

                if (FrontPanel.this.flag == 0) {
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnlReg, true);
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnsReg, true);
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnkReg, true);
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnxReg, true);
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnlProg1, true);
                    FrontPanel.this.setEnableForPanel(FrontPanel.this.pnlProg2, true);
                    FrontPanel.this.singleStepButton.setEnabled(true);
                    FrontPanel.this.runButton.setEnabled(true);
                    FrontPanel.modifyEnableFlag(FrontPanel.this, 1);
                }

                FrontPanel.this.registers.setPC(arrayList.get(anInt2));
                FrontPanel.this.refreshRegistersPanel();
                FrontPanel.this.printConsole("Initial Program Load Complete!");
            }
        });
    }

    // Refreshes the cache table by updating its contents based on the current state of the MCU's cache.
    private void refreshCacheTable() {
        int row = 0;
        // Iterate through each cache line in the MCU's cache.
        for (final Test.CacheLine line : this.mcu.getCache().getCacheLines()) {
            // Update the table's rows with the tag and data from each cache line.
            this.jTableObj.setValueAt(line.getTag(), row, 0);
            this.jTableObj.setValueAt(line.getData(), row, 1);
            row++;
        }
    }

    // Updates the console with the latest data from the printer and keyboard buffers.
    private void pushConsoleBuffer() {
        // Append the printer buffer content to the console, if not null.
        if (this.mcu.getPrinterBuffer() != null) {
            this.consolePrint.append(this.mcu.getPrinterBuffer());
            this.mcu.setPrinterBuffer(""); // Clear the buffer after pushing its content.
        }
        // Update the console keyboard with the latest buffer content, if not null.
        if (this.mcu.getKeyboardBuffer() != null) {
            this.consoleInput.setText(this.mcu.getKeyboardBuffer());
        }
    }

    // Refreshes the display of all register panels to reflect the current state of the registers.
    private void refreshRegistersPanel() {
        // Refresh each panel containing registers or other CPU-related information.
        refreshPanel(this.r0Reg);
        refreshPanel(this.r1Reg);
        refreshPanel(this.r2Reg);
        refreshPanel(this.r3Reg);
        refreshPanel(this.x1Reg);
        refreshPanel(this.x2Reg);
        refreshPanel(this.x3Reg);
        refreshPanel(this.marObj);
        refreshPanel(this.mbrObj);
        refreshPanel(this.msrObj);
        refreshPanel(this.irObj);
        refreshPanel(this.pcObj);
        refreshPanel(this.ccObj);
        refreshPanel(this.mfrObj);
    }

    // Refresh all components of panel
    private void refreshPanel(JPanel panel) {
        // Iterate through all components within the panel
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField txt = (JTextField) comp;
                String registerName = txt.getName();
                int regVal = 0;

                switch (registerName) {
                    case "MAR":
                        regVal = this.registers.getMAR(); // Assuming you have this method in CPU class
                        break;
                    case "MBR":
                        regVal = this.registers.getMBR(); // Assuming you have this method in CPU class
                        break;
                    case "IR":
                        regVal = this.registers.getIR(); // Assuming you have this method in CPU class
                        break;
                    default:
                        regVal = this.registers.getRegistersByName(registerName); // Assuming you have this method in Registers class
                        break;
                }

                txt.setText(String.valueOf(regVal));
            }
        }
    }

    // Clear all fields and output
    private void clearAll() {
        // Reset all the following register fields
        for (Component c : Arrays.asList(r0Reg, r1Reg, r2Reg, r3Reg, x1Reg, x2Reg, x3Reg, marObj, mbrObj, msrObj, irObj, pcObj, mfrObj, ccObj)) {
            if (c instanceof JPanel) {
                for (Component comp : ((JPanel) c).getComponents()) {
                    if (comp instanceof JTextField) {
                        ((JTextField) comp).setText("");
                    }
                }
            }
        }


        // Clear instruction mappings and addresses
        mapList.clear();
        arrayList.clear();

        consolePrint.setText(""); // Clear console output
        initCPU(); // Reset the CPU components, which will also reset the PC
        refreshCacheTable(); // Optionally refresh the cache table if needed

        // Update the anInt2 value, disable SS, and enable flag
        FrontPanel.anInt2 = 0;
        flag = 0;
        singleStepButton.setEnabled(false);
        runButton.setEnabled(false);
        iplButton.setEnabled(true); //enabling only IPL button
    }

    // Print passed message to the console
    private void printConsole(final String message) {
        // Append the message to console text area, and go to next line
        this.consolePrint.append(message + "\n");
    }

    // Enable or disable all components within a given panel recursively
    private void setEnableForPanel(final JPanel jPanel, final boolean flag) {

        if (jPanel == null) {
            return; // check to prevent NullPointerException
        }

        // Iterate all components in the panel
        for (Component component : jPanel.getComponents()) {

            if (component instanceof JPanel) {
                // If the component is a JPanel, apply the method recursively
                setEnableForPanel((JPanel) component, flag);
            } else {
                // Else, enable or disable the component based on flag
                component.setEnabled(flag);
            }
        }
    }

    // Execute instructions based on its opcode and updates the CPU
    private void runInstruction(final String instruction, final Registers registers, final MCU mcu) {

        final String opCode = instruction.substring(0, 6); // Extract opcode from the instruction

        // Check if opcode is valid, else handle a machine fault
        if (!Constants.OPCODE.containsKey(opCode)) {
            handleMachineFault(Constants.FaultCode.ILL_OPRC.getValue(), Constants.FaultCode.ILL_OPRC.getMessage());
            return;
        }

        try {
            // Dynamically load the class corresponding to the opcode and execute the instruction
            AbstractInstruction abstractInstruction = getInstructionInstance(Constants.OPCODE.get(opCode));
            abstractInstruction.execute(instruction, registers, mcu);
            System.out.println("PC: " + registers.getPC() + ", instruction: " + instruction);

            // Update cache table and console buffer after execution
            pushConsoleBuffer();
            refreshCacheTable();

            System.out.println(abstractInstruction.getExecuteMessage()); // log message of the instruction

        } catch (ReflectiveOperationException | MachineFaultException e) {
            // Handle exceptions during instruction execution or class loading
            e.printStackTrace();
            if (e instanceof MachineFaultException) {
                handleMachineFault(((MachineFaultException) e).getFaultCode(), e.getMessage());
            }
        }
    }

    @SuppressWarnings("deprecation")
    private AbstractInstruction getInstructionInstance(String className) throws ReflectiveOperationException {
        // Creates an instance of the instruction class
        return (AbstractInstruction) Class.forName(className).newInstance();
    }

    // Handles machine fault by updating CPU state and displaying error message
    private void handleMachineFault(final int faultCode, final String message) {
        // Set up the Machine Fault Register and update the memory with the PC and MSR
        this.registers.setMAR(5);
        this.registers.setMBR(this.registers.getMSR());
        this.mcu.storeIntoCache(this.registers.getMAR(), this.registers.getMBR());

        this.registers.setMAR(4);
        this.registers.setMBR(this.registers.getPC());
        this.mcu.storeIntoCache(this.registers.getMAR(), this.registers.getMBR());

        // Display a dialog with fault message
        this.registers.setMFR(faultCode);
        JOptionPane.showMessageDialog(null, message, "Fault Code: " + faultCode, JOptionPane.ERROR_MESSAGE);

        // Update Program Counter to point the fault handler
        this.registers.setPC(this.mcu.fetchFromCache(1));
    }

    // A static method to modify the enableFlag of the class
    static void modifyEnableFlag(final FrontPanel frontPanel, final int enableFlag) {
        frontPanel.flag = enableFlag;
    }

    // add layout function
    private void addLayout() {
        jFrameObj.getContentPane().setLayout(new BorderLayout());

        // Displaying GPR register panels on the left, and IXR registers on the right
        JPanel leftJPanel = newJPanel();
        JPanel rightJPanel = newJPanel();

        leftJPanel.setLayout(new BoxLayout(leftJPanel, BoxLayout.X_AXIS));
        leftJPanel.add(pnlReg);
        leftJPanel.add(pnsReg);

        rightJPanel.setLayout(new BoxLayout(rightJPanel, BoxLayout.Y_AXIS));
        rightJPanel.add(pnxReg);
        rightJPanel.add(pnkReg);

        // Add a central panel that can contain other components like labels and textboxes
        JPanel centerJPanel = new JPanel(new GridLayout(0, 2));
        centerJPanel.add(createBoxPanel("GPR", r0Reg, r1Reg, r2Reg, r3Reg));
        centerJPanel.add(createBoxPanel("IXR", x1Reg, x2Reg, x3Reg));
        centerJPanel.add(createBoxPanel("OTHER REGISTERS", msrObj, irObj, pcObj));
        centerJPanel.add(createBoxPanel("ADDRESS", marObj, mbrObj, mfrObj, ccObj));
        JScrollPane centerJScrollPane = new JScrollPane(centerJPanel);
        jFrameObj.getContentPane().add(centerJScrollPane, BorderLayout.WEST);
    }

    // create box panel component function
    private JPanel createBoxPanel(String title, JPanel... panels) {
        JPanel boxPanel = new JPanel();

        for (JPanel panel : panels) {
            boxPanel.add(panel);
        }

        boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
        boxPanel.setBorder(BorderFactory.createTitledBorder(title));

        return boxPanel;
    }

    // add tabbedPane component function
    private void addTabbedPane() {
        JPanel consolePanel = new JPanel(new BorderLayout());
        this.consolePrint = new JTextArea();
        this.consolePrint.setEditable(true); // allowing user input
        JScrollPane jScrollPane = new JScrollPane(this.consolePrint);
        consolePanel.add(jScrollPane, BorderLayout.CENTER);

        // Adding a key listener to insert the "> " prompt on new lines
        consolePrint.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int caretPosition = consolePrint.getCaretPosition();
                        int lineOffset = consolePrint.getLineStartOffset(consolePrint.getLineOfOffset(caretPosition));

                        // Insert prompt only when at the start of a new line
                        if (lineOffset == caretPosition && (caretPosition == 0 || consolePrint.getText().charAt(caretPosition - 1) == '\n')) {
                            consolePrint.getDocument().insertString(caretPosition, "> ", null);
                        }
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });

        JTabbedPane jTabbedPane = new JTabbedPane(); // adding tabbed pane to frame
        jFrameObj.add(jTabbedPane, BorderLayout.CENTER);

        // Add tab to the tabbed pane
        jTabbedPane.addTab("Console Input", null, consolePanel, "Console Output/Input");
    }

    // add buttons component function
    private void addButtons() {
        JPanel buttonJPanel = newJPanel();
        buttonJPanel.setLayout(new FlowLayout());

        // Set button config and add to frame
        this.resetButton = createButton("Reset", BLUE_BG);
        buttonJPanel.add(this.resetButton);

        this.haltButton = createButton("Halt", BLUE_BG);
        buttonJPanel.add(this.haltButton);

        this.storeButton = createButton("Store", BLUE_BG);
        buttonJPanel.add(this.storeButton);

        this.singleStepButton = createButton("Single Step", BLUE_BG);
        buttonJPanel.add(this.singleStepButton);
        this.singleStepButton.setEnabled(false);

        this.runButton = createButton("Run", BLUE_BG);
        buttonJPanel.add(this.runButton);
        this.runButton.setEnabled(false);

        this.iplButton = createButton("IPL", BLUE_BG);
        buttonJPanel.add(this.iplButton);

        jFrameObj.getContentPane().add(buttonJPanel, BorderLayout.NORTH);
    }

    // add cache component function
    private void addCache() {
        this.jLabelObj = new JLabel("Cache");
        this.jLabelObj.setForeground(new Color(220,220,220));

        this.jScrollPaneObj = new JScrollPane();
        this.jScrollPaneObj.setViewportView(this.jTableObj);

        this.jTableObj = new JTable(16, 2);
        this.jTableObj.setEnabled(false);
        this.jTableObj.setModel(new DefaultTableModel(new Object[][]{
                new Object[2], new Object[2], new Object[2], new Object[2], new Object[2], new Object[2],
                new Object[2], new Object[2], new Object[2], new Object[2], new Object[2], new Object[2],
                new Object[2], new Object[2], new Object[2], new Object[2]},
                new String[]{"Tag", "Data"}));

        this.pnlCache = new JPanel();
        this.pnlCache.setBounds(800, 400, 250, 150);
        this.pnlCache.setLayout(new BoxLayout(this.pnlCache, BoxLayout.Y_AXIS));
        this.pnlCache.add(this.jLabelObj);
        this.pnlCache.add(this.jScrollPaneObj);
        this.pnlCache.setBackground(new Color(60, 63, 65));
    }

    // add jPanel component function
    private JPanel newJPanel() {
        JPanel jPanel = new JPanel();
        jPanel.setBackground(FrontPanel.BLUE_BG);
        return jPanel;
    }

    // create button component function
    private JButton createButton(String label, Color bgColor) {
        // This function sets the button and bg colour
        JButton jButton = new JButton(label);
        jButton.setBackground(bgColor);
        jButton.setPreferredSize(new Dimension(100, 24));
        return jButton;
    }
}
