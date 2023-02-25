package dev.tim.discordbot.command;

import dev.tim.discordbot.DiscordBot;
import dev.tim.discordbot.model.Warning;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class WarnCommand extends ListenerAdapter {

    private final DiscordBot bot;

    public WarnCommand(DiscordBot bot){
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("warn")){

            if(event.getMember().hasPermission(Permission.KICK_MEMBERS)){

                OptionMapping option1 = event.getOption("user");
                OptionMapping option2 = event.getOption("reason");

                if(option1 == null || option2 == null){
                    event.reply("An option is not given!").setEphemeral(true).queue();
                    return;
                }

                User user = option1.getAsUser();
                String reason = option2.getAsString();

                List<Warning> warnings;
                try {
                    warnings = bot.getDatabase().findWarningsByMember(user.getIdLong());
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("An error occurred when using this command").setEphemeral(true).queue();
                    return;
                }

                if(warnings.size() > 15){
                    event.reply("This user already has 15+ warnings. To give this user another warning, remove one.").setEphemeral(true).queue();
                    return;
                }

                Warning warning = new Warning(UUID.randomUUID().toString(), user.getIdLong(), event.getMember().getIdLong(), reason, new Date());

                try {
                    bot.getDatabase().createWarning(warning);
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("An error occurred when using this command").setEphemeral(true).queue();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 0, 0));
                embed.setTitle("User Warned");
                embed.addField("ID:", warning.getId(), false);
                embed.addField("Warned:", user.getAsMention(), false);
                embed.addField("Warned By:", event.getMember().getAsMention(), false);
                embed.addField("Reason:", reason, false);
                embed.addField("Date:", formatter.format(warning.getDate()), false);
                try {
                    embed.setFooter(user.getName() + " now has " + bot.getDatabase().findWarningsByMember(user.getIdLong()).size() + " warnings");
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("An error occurred when using this command").setEphemeral(true).queue();
                    return;
                }

                event.replyEmbeds(embed.build()).queue();

            } else {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            }

        }
    }
}
