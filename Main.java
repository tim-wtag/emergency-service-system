import java.util.Scanner;

public class Main 
{
    static String [] emergencyKeywords = new String[]{"fire", "theft", "crime", "health", "ambulance", "bleeding"};
    
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        

        while (true) 
        {
            System.out.println("Please enter your emergency description (or type exit to quit): ");
            String input = scanner.nextLine();

            sendAlert(input);
            receiveAlerts(input);
            if (input.equalsIgnoreCase("exit")) 
            {
                System.out.println("Goodbye, have a nice day!");
                break;
            }
        }

        scanner.close();
    }

    public static void sendAlert(String userMessage)
    {
        System.out.println("[ALERT SENT]: Transmission received: " + userMessage + "");
        
    }

    public static void receiveAlerts(String userMessage)
    {
        String lowerInput = userMessage.toLowerCase();
        for(int i = 0; i <= emergencyKeywords.length; i++)
        {
            
                if(lowerInput.contains(emergencyKeywords[i]))
                {
                    String alertMessage = " ";
                    alertMessage = switch (emergencyKeywords[i]) {
                        case "fire" -> "Fire truck routed to your location";
                        case "health", "ambulance", "bleeding" -> "Paramedics deployed immediately";
                        case "theft", "crime" -> "Police units dispatched to the scene!";
                        default -> "Contacting operator.";
                    };

                    System.out.println("[DISPATCH]: " + alertMessage);
                }
                else
                {
                    System.out.println("[SYSTEM]: Alert description unclear. Forwarding to a human operator");
                }
            }
        
    }

}


