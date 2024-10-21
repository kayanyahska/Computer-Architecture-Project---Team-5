import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<String, String> INSTRUCTION_SET = new HashMap<>();
    static {
                INSTRUCTION_SET.put("LOAD", "0001");
                INSTRUCTION_SET.put("ADD", "0010");
                INSTRUCTION_SET.put("SUB", "0011");
                INSTRUCTION_SET.put("JUMP", "0100");
    }
    public static void main(String[] args) {
        {
            EventQueue.invokeLater(() -> {
                try {
                    FrontPanel window = new FrontPanel();
                    window.jFrameObj.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}