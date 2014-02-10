package io.yukon.gitpull;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Commands {
    @Command(
        aliases = {"gitpull"},
        desc = "Reloads GitPull config",
        min = 0,
        max = 0
    )
    @CommandPermissions("gitpull.reload")
    public static void reload(CommandContext args, CommandSender sender) throws CommandException {
        GitPull.get().reloadConfig();
        if (GitPull.get().reloadfailed) {
            throw new CommandException("Could not reload config.\n" + GitPull.get().reloaderror);
        } else {
            sender.sendMessage(ChatColor.GREEN + "Config reloaded");
        }
    }

    @Command(
        aliases = {"update", "pull"},
        desc = "Pulls the latest remote repo",
        min = 0,
        max = 0
    )
    @CommandPermissions("gitpull.pull")
    public static void pull(CommandContext args, CommandSender sender) throws CommandException {
        try {
            new Git(GitPull.get().getRepo()).pull().call();
        } catch (GitAPIException e) {
            e.printStackTrace();
            throw new CommandException("An error has occurred, repo not updated.\n" + e.getCause().getMessage());
        }
        sender.sendMessage(ChatColor.GREEN + "Repo pulled");
    }
}
