package io.yukon.gitpull;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    public static Configuration getConfiguration() {
        GitPull gitPull = GitPull.get();
        if(gitPull != null) {
            return gitPull.getConfig();
        } else {
            return new YamlConfiguration();
        }
    }


    public static String pullPath() {
        String path = "";

        path += getConfiguration().getString("repo-path", "/");
        if(!path.endsWith("/")) path += "/";
        path += ".git";

        return path;
    }

    public static String sshPassphrase() {
        return getConfiguration().getString("ssh-passphrase", "password");
    }
}
