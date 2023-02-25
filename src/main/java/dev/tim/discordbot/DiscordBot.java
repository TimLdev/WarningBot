package dev.tim.discordbot;

import dev.tim.discordbot.command.DeleteWarnCommand;
import dev.tim.discordbot.command.WarnsCommand;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import dev.tim.discordbot.command.WarnCommand;
import dev.tim.discordbot.database.Database;
import dev.tim.discordbot.manager.SlashCommandManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.sql.SQLException;

public class DiscordBot {

    private final Dotenv config;
    private final Database database;

    private ShardManager shardManager;

    public DiscordBot(){
        config = Dotenv.configure().load();

        database = new Database(this);
        try {
            database.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Can't connect to database / Can't make table in database");
            return;
        }

        String token = config.get("BOT_TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setActivity(Activity.playing("Warning Bot"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        shardManager = builder.build();
        shardManager.addEventListener(
                new SlashCommandManager(),
                new WarnCommand(this),
                new WarnsCommand(this),
                new DeleteWarnCommand(this));
    }

    public static void main(String[] args) {
        new DiscordBot();
    }

    public Dotenv getConfig() {
        return config;
    }
    public Database getDatabase() {
        return database;
    }
    public ShardManager getShardManager() {
        return shardManager;
    }
}