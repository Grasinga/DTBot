import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;
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
        Member member = event.getMember();
        Message message = event.getMessage();
        String messageContent = message.getContent();

        switch (messageContent.toLowerCase()) {
            case "-ping":
                event.getChannel().sendMessage("pong").queue();
                break;
            case "-pong":
                event.getChannel().sendMessage("ping").queue();
        }
    }

}
