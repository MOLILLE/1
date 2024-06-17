import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodeDetector {
    public static void main(String[] args) {
        String apiKey = "your_api_key_here";
        String codeFilePath = "path/to/your/code/file.java";

        String output = detectCodeDetails(apiKey, codeFilePath);
        System.out.println(output);
    }

    public static String detectCodeDetails(String apiKey, String codeFilePath) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty.");
        }

        if (codeFilePath == null || codeFilePath.isEmpty()) {
            throw new IllegalArgumentException("Code file path cannot be null or empty.");
        }

        OpenAiService service = new OpenAiService(apiKey);

        String prompt = String.format("""
        Given the contents of a code file located at "%s", please provide the following information:

        1. The testing framework used (e.g. JUnit, TestNG, etc.)
        2. The programming language of the code (e.g. Java, Python, etc.)
        3. The runtime environment (e.g. JVM, Python interpreter, etc.)
        4. Whether comments should be added to the test code (yes/no)
        5. Any additional information about the project

        Provide the information in the following format:
        ```
        Unit Test Code Generator Output:
        Testing Framework: <testing_framework>
        Programming Language: <programming_language>
        Runtime Environment: <runtime_environment>
        Need Comments: <yes/no>
        Additional Information: <additional_info>
        ```
        """, codeFilePath);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(prompt)
                .maxTokens(1024)
                .n(1)
                .stop(null)
                .temperature(0.5)
                .build();

        try {
            CompletionResult result = service.createCompletion(completionRequest);
            return result.getchoices().get(0).getText().trim();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while detecting code details: " + e.getMessage(), e);
        }
    }
    class CodeDetectorTest {
        @Test
        void testDetectCodeDetailsWithValidInput() {
            String apiKey = "your_api_key_here";
            String codeFilePath = "path/to/your/code/file.java";

            String output = CodeDetector.detectCodeDetails(apiKey, codeFilePath);
            Assertions.assertNotNull(output);
            Assertions.assertTrue(output.contains("Unit Test Code Generator Output"));
            Assertions.assertTrue(output.contains("Testing Framework:"));
            Assertions.assertTrue(output.contains("Programming Language:"));
            Assertions.assertTrue(output.contains("Runtime Environment:"));
            Assertions.assertTrue(output.contains("Need Comments:"));
            Assertions.assertTrue(output.contains("Additional Information:"));
        }

        @Test
        void testDetectCodeDetailsWithInvalidApiKey() {
            Assertions.assertThrows(RuntimeException.class, () -> {
                CodeDetector.detectCodeDetails(null, "path/to/your/code/file.java");
            });
        }

        @Test
        void testDetectCodeDetailsWithInvalidCodeFilePath() {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                CodeDetector.detectCodeDetails("your_api_key_here", null);
            });
        }
    }
}