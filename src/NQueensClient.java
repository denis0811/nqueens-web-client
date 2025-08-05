import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class NQueensClient {

    private static final String API_BASE_URL = "http://localhost:7000/solutions/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();

        while (true) {
            System.out.print("Enter a solution number (1-92) or 'exit' to quit: ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                int solutionNumber = Integer.parseInt(input);
                if (solutionNumber < 1) {
                    System.out.println("Please enter a positive number.");
                    continue;
                }

                // Build the request URL
                URI uri = URI.create(API_BASE_URL + solutionNumber);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();

                // Send the request and get the response
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("----------------------------------------");
                if (response.statusCode() == 200) {
                    // Success, print the solution
                    System.out.println("Solution " + solutionNumber + ": " + formatSolution(response.body()));
                } else {
                    // API returned an error
                    System.out.println("Error from server: " + response.body());
                }
                System.out.println("----------------------------------------");

            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            } catch (IOException | InterruptedException e) {
                System.out.println("An error occurred while communicating with the server. Is the API running?");
            }
        }
        scanner.close();
    }

    private static String formatSolution(String jsonString) {
        // The API returns a JSON array like "[1, 5, 8, ...]"
        // We'll format it into "(row, column)" tuples

        String[] columns = jsonString.substring(1, jsonString.length() - 1).split(",");
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i = 0; i < columns.length; i++) {
            sb.append("(").append(i + 1).append(", ").append(columns[i].trim()).append(")");
            if (i < columns.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}