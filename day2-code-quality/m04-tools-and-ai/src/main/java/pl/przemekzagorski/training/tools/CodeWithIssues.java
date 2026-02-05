package pl.przemekzagorski.training.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * ⚠️ CELOWO ZŁY KOD - dla demonstracji SonarLint!
 *
 * Uruchom SonarLint i zobacz ile problemów znajdzie.
 * Każdy problem jest oznaczony komentarzem.
 */
public class CodeWithIssues {

    // 🦨 Code Smell: Unused private field
    private String unusedField = "never used";

    // 🔓 Vulnerability: Hardcoded password!
    private static final String DB_PASSWORD = "admin123";

    public void processData(String input) {
        // 🐛 Bug: Potential null pointer dereference
        System.out.println(input.length());

        // 🦨 Code Smell: Empty catch block
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Nic nie robimy - ZŁE!
        }
    }

    public void resourceLeak() {
        // 🐛 Bug: Resource leak - connection not closed!
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", DB_PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT 1");
            // Brak close() - wyciek zasobów!
        } catch (Exception e) {
            e.printStackTrace();  // 🦨 Code Smell: printStackTrace()
        }
    }

    public void sqlInjection(String userInput) {
        // 🔓 Vulnerability: SQL Injection!
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:test", "sa", "");
            Statement stmt = conn.createStatement();
            // Konkatenacja stringa z userem = SQL Injection!
            stmt.execute("SELECT * FROM users WHERE name = '" + userInput + "'");
        } catch (Exception e) {
            // 🦨 Swallowing exception
        }
    }

    public int calculate(int a, int b) {
        // 🐛 Bug: Division by zero possible
        return a / b;
    }

    public void unusedVariable() {
        // 🦨 Code Smell: Unused local variable
        String neverUsed = "this variable is never used";
        System.out.println("Hello");
    }

    public void tooComplex(int x) {
        // 🦨 Code Smell: Cognitive complexity too high
        if (x > 0) {
            if (x > 10) {
                if (x > 20) {
                    if (x > 30) {
                        if (x > 40) {
                            if (x > 50) {
                                System.out.println("Very high");
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean badEquals(Object obj) {
        // 🐛 Bug: equals() without hashCode()
        return this == obj;
    }

    public void infiniteLoop() {
        // 🐛 Bug: Potential infinite loop (commented out for safety!)
        // while (true) { }
    }

    public String nullReturn() {
        // 🦨 Code Smell: Returning null
        return null;
    }

    public void duplicateCode1() {
        System.out.println("Step 1");
        System.out.println("Step 2");
        System.out.println("Step 3");
        System.out.println("Step 4");
        System.out.println("Step 5");
    }

    public void duplicateCode2() {
        // 🦨 Code Smell: Duplicate code
        System.out.println("Step 1");
        System.out.println("Step 2");
        System.out.println("Step 3");
        System.out.println("Step 4");
        System.out.println("Step 5");
    }

    // 🦨 Code Smell: Too many parameters
    public void tooManyParams(String a, String b, String c, String d,
                               String e, String f, String g, String h) {
        System.out.println(a + b + c + d + e + f + g + h);
    }

    public static void main(String[] args) {
        System.out.println("🔍 Uruchom SonarLint i zobacz problemy!");
        System.out.println("W IntelliJ: View -> Tool Windows -> SonarLint");
    }
}

