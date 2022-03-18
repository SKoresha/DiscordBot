import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static net.dv8tion.jda.api.requests.GatewayIntent.ALL_INTENTS;
import static net.dv8tion.jda.api.requests.GatewayIntent.getIntents;

public class MessageListenerExample extends ListenerAdapter
{
    /**
     * This is the method where the program starts.
     */
    public static void main(String[] args)
    {
        //We construct a builder for our bot.
        try
        {
            JDA jda = JDABuilder.createDefault("OTUzNTk4MzkwNjMyMDgzNDY2.YjG5tQ.v33iGM7lPZ5DsFc4gkZJ9Oh8mIw") // The token of the account that is logging in.
                    .addEventListeners(new MessageListenerExample())   // An instance of a class that will handle events.
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS,CacheFlag.ACTIVITY)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.awaitReady(); // Blocking guarantees that JDA will be completely loaded.
            System.out.println("Finished Building JDA!");
        }
        catch (LoginException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link ListenerAdapter ListenerAdapter} class. It has convenience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link Message Message} that was
     *          sent in a channel.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {

        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.

        String msg = message.getContentDisplay();              //This returns a human readable version of the Message. Similar to

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            int numb;
            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name;
            if (message.isWebhookMessage())
            {
                name = author.getName();                //If this is a Webhook message, then there is no Member associated
            }                                           // with the User, thus we default to the author for name.
            else
            {
                name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
            }                                           // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
        }
        else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {

            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }

        if (msg.equals("!ping"))
        {
            channel.sendMessage("pong!").queue();
        }
        else if (msg.equals("!roll"))
        {
            Random rand = ThreadLocalRandom.current();
            int roll = rand.nextInt(100) + 1; //This results in 1 - 6 (instead of 0 - 5)
            String name;
            name = author.getName();
            channel.sendMessage("Your roll: " + roll)
                    .flatMap(
                            (v) -> roll < 45, // This is called a lambda expression. If you don't know what they are or how they work, try google!
                            // Send another message if the roll was bad (less than 3)
                            sentMessage -> channel.sendMessage(name + " just sucked \n")
                    )
                    .queue();
        }
        else if(msg.equals("!memb"))
        {
            Guild guild = event.getGuild();
            int playerCount = guild.getMemberCount();

            channel.sendMessage("All members" + playerCount).queue();
        }
        else if(msg.equals("!play")){
            Guild guild = event.getGuild();
            List<Member> members = guild.getMembers();
            ArrayList<String> online = new ArrayList<>();

            for(Member m: members){
                if(m.getActivities().stream().anyMatch(activity -> activity.getType()== Activity.ActivityType.PLAYING)){
                    online.add(m.getEffectiveName());
                }
            }
            String s = String.join("\n", online);
            channel.sendMessage(s).queue();
        }
        else if(msg.equals("!online")) {

            Guild guild = event.getGuild();
            List<Member> members = guild.getMembers();
            ArrayList<String> online = new ArrayList<>();
            for (Member m : members) {
                if (!m.getUser().isBot() && m.getOnlineStatus()==OnlineStatus.ONLINE) {
                   online.add(m.getEffectiveName());
                }
            }
            String s = String.join("\n", online);
            channel.sendMessage(s).queue();
          //  channel.sendMessage(online.toString()).queue();
        }
        else if(msg.equals("@Freeman")){
            channel.sendMessage("Поднять карасевые флаги!").queue();
        }

        else if (msg.equals("!whoami"))
        {
            Member member = event.getMember(); //This Member that sent the message. Contains Guild specific information about the User!
            if (member != null) // This member might be null if the message came from a webhook or a DM
            {
                channel.sendMessage(
                        "Your ID: " + member.getId() +                          // Get ID from User
                                "\n Your EffectiveName: " + member.getEffectiveName() + // Get Display Name from User
                                "\n As Nickname: " + member.getNickname() +                 // Get User Mention
                                "\n Avatar: " + member.getEffectiveAvatarUrl()+             //Get Avatar from User
                                "\n" + member.getOnlineStatus()
                ).queue();
            }
        }
    }


}
