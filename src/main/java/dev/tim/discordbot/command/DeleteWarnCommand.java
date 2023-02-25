package dev.tim.discordbot.command;

import dev.tim.discordbot.DiscordBot;
import dev.tim.discordbot.model.Warning;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class DeleteWarnCommand extends ListenerAdapter {

    private final DiscordBot bot;

    public DeleteWarnCommand(DiscordBot bot){
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("deletewarn")){

            if(event.getMember().hasPermission(Permission.KICK_MEMBERS)){

                OptionMapping option = event.getOption("id");

                if(option == null){
                    event.reply("An option is not given!").setEphemeral(true).queue();
                    return;
                }

                String id = option.getAsString();
                Warning warning;

                try {
                    warning = bot.getDatabase().findWarningById(id);
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("An error occurred when using this command").setEphemeral(true).queue();
                    return;
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 0, 0));

                if(warning == null){
                    embed.setTitle("Warning not found...");
                } else {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                        bot.getDatabase().deleteWarning(warning.getId());
                        embed.setTitle("Warning Deleted");
                        embed.setDescription(event.getMember().getAsMention() + " deleted this warning:");
                        embed.addField("ID:", warning.getId(), false);
                        embed.addField("Warned:", bot.getShardManager().getUserById(warning.getMember_id()).getAsMention(), false);
                        embed.addField("Warned By:", bot.getShardManager().getUserById(warning.getMember_warner_id()).getAsMention(), false);
                        embed.addField("Reason:", warning.getReason(), false);
                        embed.addField("Date:", formatter.format(warning.getDate()), false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        event.reply("An error occurred when using this command").setEphemeral(true).queue();
                        return;
                    }
                }

                event.replyEmbeds(embed.build()).queue();

            } else {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            }

        }
    }
}
