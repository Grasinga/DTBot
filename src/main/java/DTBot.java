import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * Discord Tabletop Bot
 */
public class DTBot extends ListenerAdapter{

    /**
     * Starts the bot with the given arguments (from command line or bot.properties).
     *
     * @param args Given arguments from command line.
     */
    public static void main(String[] args) {
        String token = "";

        // Initialize bot via the command line.
        if(args.length >= 1) {
            token = args[0];
        }
        try {
            if(token.equals("")) {
                BufferedReader br = Files.newBufferedReader(Paths.get("./bot.properties"));

                String properties = br.readLine();
                if (properties != null)
                    token = properties;

                br.close();
            }

            new JDABuilder(AccountType.BOT)
                    .setBulkDeleteSplittingEnabled(false)
                    .setToken(token)
                    .addListener(new DTBot())
                    .buildBlocking();
        }
        catch (IllegalArgumentException e) {
            System.out.println("The config was not populated. Please make sure all arguments were given.");
        }
        catch (LoginException e) {
            System.out.println("The provided bot token was incorrect. Please provide a valid token.");
        }
        catch (InterruptedException | RateLimitedException e) {
            System.out.println("A thread interruption occurred. Check Stack Trace below for source.");
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            System.out.println("Could not find Bot Token file!");
        }
        catch (IOException e) {
            System.out.println("Could not read Bot Token file!");
        }
        catch (Exception e) {
            System.out.println("A general exception was caught. Exception: " + e.getCause());
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("DTBot has started successfully!");
    }

    /**
     * Method used to receive command input.
     * @param event Holds the info needed.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Message sent was not a command.
        if(!event.getMessage().getContent().toLowerCase().startsWith("-"))
            return;

        MessageChannel channel = event.getChannel();
        Member member = event.getMember();
        Message message = event.getMessage();
        String command = message.getContent();

        // Each argument is the text after a space. commandArgs[0] == [command]
        String[] commandArgs = command.split(" ");

        switch (commandArgs[0].toLowerCase()) {
            case "-dice":
                if(command.length() > "-dice".length()) {

                    // Contains only numbers and has at least one.
                    if (commandArgs[1].matches("[0-9]+")) {
                        try {
                            int roll = getDiceRoll(Integer.parseInt(commandArgs[1]));
                            channel.sendMessage("d" + commandArgs[1] + ": " + roll).queue();
                        } catch (Exception e) {
                            event.getChannel().sendMessage("That was not a valid number!").queue();
                        }
                    }
                }
                else
                    channel.sendMessage("Usage: -dice [number]").queue();
                break;
            case "-pong": // Test command.
                channel.sendMessage("ping").queue();
        }
    }

    /**
     * Returns a number within the amount of sides the passed in dice has.
     * @param diceSides Number of sides on the dice.
     * @return A random number up to the amount of sides on the passed in dice.
     */
    private int getDiceRoll(int diceSides) {
        return new SecureRandom().nextInt(diceSides);
    }

}
