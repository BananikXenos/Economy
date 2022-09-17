package xyz.synse.economy.manager.users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import xyz.synse.economy.Economy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final File usersFolder;
    private HashMap<UUID, User> onlineUsers = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UserManager(File usersFolder) {
        this.usersFolder = usersFolder;
    }

    @Nonnull
    public User loadUser(@Nonnull Player player) throws FileNotFoundException {
        return loadUser(player.getUniqueId());
    }

    @Nonnull
    public User loadUser(@Nonnull UUID uuid) throws FileNotFoundException {
        Economy.getInstance().getLogger().info("Loading user with uuid " + uuid);

        if (onlineUsers.containsKey(uuid)) {
            Economy.getInstance().getLogger().info("Already loaded user with uuid " + uuid);
            return onlineUsers.get(uuid);
        }

        File userFile = new File(usersFolder, uuid + ".json");

        User user;

        if (!userFile.exists()) {
            Economy.getInstance().getLogger().info("Setting up new user with uuid " + uuid);
            user = new User(uuid);
            user.getWallet().setBalance(Economy.getInstance().getConfiguration().get("starting-balance"));
        } else {
            user = gson.fromJson(new FileReader(userFile), User.class);
            Economy.getInstance().getLogger().info("User loaded");
        }

        onlineUsers.put(uuid, user);

        return user;
    }

    @Nullable
    public User unloadUser(@Nonnull Player player) throws IOException {
        return unloadUser(onlineUsers.get(player.getUniqueId()));
    }

    @Nullable
    public User unloadUser(@Nonnull User user) throws IOException {
        if (!onlineUsers.containsKey(user.getUniqueID())) {
            Economy.getInstance().getLogger().info("User with uuid " + user.getUniqueID() + " already unloaded");
            return null;
        }

        saveUser(user);

        Economy.getInstance().getLogger().info("User unloaded");
        onlineUsers.remove(user.getUniqueID());

        return user;
    }

    @Nullable
    public User saveUser(@Nonnull User user) throws IOException {
        File userFile = new File(usersFolder, user.getUniqueID() + ".json");

        if (!userFile.exists()) {
            userFile.createNewFile();
        }

        Economy.getInstance().getLogger().info("Saving user " + user.getUniqueID());
        FileWriter fw = new FileWriter(userFile, false);
        fw.write(gson.toJson(user));
        fw.flush();
        fw.close();

        return user;
    }

    @Nonnull
    public User getUser(@Nonnull Player player) {
        return onlineUsers.get(player.getUniqueId());
    }

    @Nullable
    public User getUser(@Nonnull UUID uuid) throws FileNotFoundException {
        if (onlineUsers.containsKey(uuid))
            return onlineUsers.get(uuid);

        return loadUser(uuid);
    }

    public void unloadAllUsers(){
        for(User user : onlineUsers.values()){
            try {
                saveUser(user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        onlineUsers.clear();
    }
}
