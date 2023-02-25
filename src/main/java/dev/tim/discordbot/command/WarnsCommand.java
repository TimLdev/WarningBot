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
import java.util.List;

public class WarnsCommand extends ListenerAdapter {

    private final DiscordBot bot;

    public WarnsCommand(DiscordBot bot){
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equals("warns")) {

            if (event.getMember().hasPermission(Permission.KICK_MEMBERS)) {

                OptionMapping option = event.getOption("user");

                if (option == null) {
                    event.reply("An option is not given!").setEphemeral(true).queue();
                    return;
                }

                User user = option.getAsUser();

                List<Warning> warnings;

                try {
                    warnings = bot.getDatabase().findWarningsByMember(user.getIdLong());
                } catch (SQLException e) {
                    e.printStackTrace();
                    event.reply("An error occurred when using this command").setEphemeral(true).queue();
                    return;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(new Color(255, 0, 0));
                embed.setTitle("Warnings of " + user.getName());

                if (warnings.size() == 0) {
                    embed.setDescription("This user has no warnings.");
                } else {
                    int i = 1;
                    for (Warning warning : warnings) {
                        embed.setDescription("This user has **" + warnings.size() + "** warnings.");
                        embed.addField(i + ". " + warning.getReason() + " (" + formatter.format(warning.getDate()) + ")", "By: " + bot.getShardManager().getUserById(warning.getMember_warner_id()).getAsMention() + " ID: " + warning.getId(), false);
                        i++;
                    }
                }

                event.replyEmbeds(embed.build()).queue();

            } else {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            }
        }
    }
}
