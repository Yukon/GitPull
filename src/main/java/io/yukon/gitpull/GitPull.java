package io.yukon.gitpull;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;

public class GitPull extends JavaPlugin {
    public static GitPull inst;
    private CommandsManager<CommandSender> commands;
    private Repository repo;
    public boolean reloadfailed;
    public String reloaderror;

    public static GitPull get() {
        return inst;
    }

    public GitPull() {
        inst = this;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();

        SshSessionFactory.setInstance(new SshSessionFactory());
        this.setupCommands();
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override public boolean hasPermission(CommandSender sender, String perm) {
                return sender.hasPermission(perm);
            }
        };

        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
        cmdRegister.register(Commands.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    @Override
    public void reloadConfig() {
        reloadfailed = false;
        reloaderror = "";
        super.reloadConfig();

        try {
            this.repo = new FileRepositoryBuilder().setGitDir(new File(Config.pullPath()))
                .readEnvironment()
                .findGitDir()
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            reloadfailed = true;
            reloaderror = e.getCause().getMessage();
        }
    }

    public Repository getRepo() {
        return this.repo;
    }
}
