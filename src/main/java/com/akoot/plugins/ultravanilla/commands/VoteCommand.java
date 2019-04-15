package com.akoot.plugins.ultravanilla.commands;

import com.akoot.plugins.ultravanilla.Ultravanilla;
import com.akoot.plugins.ultravanilla.reference.Users;
import com.akoot.plugins.ultravanilla.stuff.Poll;
import com.akoot.plugins.ultravanilla.util.RawComponent;
import com.akoot.plugins.ultravanilla.util.RawMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoteCommand extends UltraCommand implements CommandExecutor, TabExecutor {

    public static final ChatColor COLOR = ChatColor.GRAY;
    private List<Poll> polls;

    public VoteCommand(Ultravanilla instance) {
        super(instance);
        color = COLOR;
        polls = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        args = refinedArgs(args);
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("list")) {
                    RawMessage message = new RawMessage();
                    for (Poll poll : polls) {
                        String name = poll.getName();
                        String title = poll.getTitle();
                        RawComponent text = new RawComponent();
                        text.setContent(ChatColor.GRAY + "- " + color + name);
                        text.setHoverText(ChatColor.LIGHT_PURPLE + title + "\\n" + ChatColor.AQUA + "Click for more info...");
                        message.addComponent(text);
                    }
                    player.sendRawMessage(message.getJSON());
                } else {
                    Poll poll = getPoll(args[0]);
                    if (poll != null) {
                        String name = poll.getName();
                        if (args[0].equalsIgnoreCase(name)) {
                            poll.show(player);
                        }
                    } else {
                        sendPollNotFound(sender, args[0]);
                    }
                }
            }
            return true;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cancel")) {
                Poll poll = getPoll(args[1]);
                if (poll != null) {
                    if (args[1].equalsIgnoreCase(poll.getName())) {
                        poll.cancel();
                        polls.remove(poll);
                        sender.sendMessage(format("Cancelled poll: %s", quote(object(poll.getName()))));
                    }
                } else {
                    sendPollNotFound(sender, args[1]);
                }
            } else {
                if (sender instanceof Player) {
                    Poll poll = getPoll(args[0]);
                    Player player = (Player) sender;
                    if (poll != null) {
                        String name = poll.getName();
                        if (args[0].equalsIgnoreCase(name)) {
                            if (!Ultravanilla.getConfig(player.getUniqueId()).getList(Users.VOTE_LIST, new ArrayList<>()).contains(name)) {
                                for (String key : poll.getVotes().keySet()) {
                                    if (args[1].equalsIgnoreCase(key)) {
                                        sender.sendMessage(format("You voted %s for poll %s", object(key), quote(noun(poll.getName()))));
                                        poll.vote(key);
                                    } else {
                                        sender.sendMessage(format("%s is not an option for this poll", wrong(args[1])));
                                    }
                                }
                            } else {
                                sender.sendMessage(format("You already voted for this poll"));
                            }
                        }
                    } else {
                        sendPollNotFound(sender, args[0]);
                    }
                }
            }
            return true;
        } else if (args.length > 2) {
            // vote create name "The title" -time 10s -options "one, two, three"
            if (args[0].equalsIgnoreCase("create")) {

                String name = args[1];
                long time = (5 * 60) * 20L;

                String title = args[2];
                if (title == null) {
                    title = name;
                }

                String optionsString = getArgFor(args, "-options");
                String[] options = optionsString != null ? optionsString.split(", ") : new String[]{"Yes", "No"};

                String timeString = getArgFor(args, "-time");
                if (timeString != null) {
                    time = Poll.getTime(timeString);
                }

                Poll poll = new Poll(name, title, time);
                for (String option : options) {
                    poll.getVotes().put(option, 0);
                }

                polls.add(poll);
                poll.init();

                System.out.println(Arrays.asList(args));
                return true;
            } else if (args[0].equalsIgnoreCase("rename")) {
                Poll poll = getPoll(args[1]);
                if (poll != null) {
                    poll.setName(args[2]);
                } else {
                    sendPollNotFound(sender, args[1]);
                }
                return true;
            }
        }
        return false;
    }

    private Poll getPoll(String name) {
        for (Poll poll : polls) {
            if (name.equalsIgnoreCase(poll.getName())) {
                return poll;
            }
        }
        return null;
    }

    private void sendPollNotFound(CommandSender sender, String string) {
        sender.sendMessage(format("%s is not an ongoing poll. %s", wrong(string), ChatColor.GRAY + "Type /vote list to list ongoing polls"));

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
