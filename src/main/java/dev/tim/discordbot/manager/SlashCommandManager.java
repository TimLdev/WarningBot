package dev.tim.discordbot.manager;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

public class SlashCommandManager extends ListenerAdapter {

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("warn", "Give a user a warning")
                .addOptions(new OptionData(OptionType.USER, "user", "The user you want to warn", true),
                        new OptionData(OptionType.STRING, "reason", "The reason of warning", true)
                                .setMaxLength(100)));

        commandData.add(Commands.slash("warns", "See the warnings of a user")
                .addOption(OptionType.USER, "user", "The user whose warns you want to see", true));
        commandData.add(Commands.slash("deletewarn", "Delete a warning")
                .addOption(OptionType.STRING, "id", "The ID of the warning to delete", true));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
