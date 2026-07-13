import java.util.Scanner;

public class Main 
{
    private static String [] emergencyKeywords = new String[]{"fire", "theft", "crime", "health", "ambulance", "bleeding"};
    
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (true) 
        {
            System.out.println("Please enter your emergency description (or type exit to quit): ");
            input = scanner.nextLine();

            if(input.trim().isEmpty()){
                System.out.println("Input cannot be empty. Please try again.");
                continue;   
            }

            if (input.equalsIgnoreCase("exit")) 
            {
                System.out.println("Goodbye, have a nice day!");
                break;
            }
            sendAlert(input);
            receiveAlerts(input); 
        }
        scanner.close();
    }

    public static void sendAlert(String userMessage)
    {
        System.out.println("[ALERT SENT]: Transmission received: " + userMessage);
    }

    public static void receiveAlerts(String userMessage)
    {
        String lowerInput = userMessage.toLowerCase();
        String match = "";
        for(int i = 0; i < emergencyKeywords.length; i++)
        {  
            if(lowerInput.contains(emergencyKeywords[i]))
            {
                match = emergencyKeywords[i];
                break;
            }
        }
         String alertMessage = switch (match) 
         {
            case "fire" -> "[DISPATCH] Fire truck routed to your location.";
            case "health", "ambulance", "bleeding" -> "[DISPATCH] Paramedics deployed immediately.";
            case "theft", "crime" -> "[DISPATCH] Police units dispatched to the scene!";
            default -> "[SYSTEM]: Alert description unclear. Forwarding to a human operator.";
         };

        System.out.println(alertMessage);          
    }
}