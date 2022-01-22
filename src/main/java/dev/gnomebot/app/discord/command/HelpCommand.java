package dev.gnomebot.app.discord.command;

import dev.gnomebot.app.data.DiscordCustomCommand;
import dev.gnomebot.app.discord.legacycommand.DiscordCommandException;
import dev.gnomebot.app.discord.legacycommand.DiscordCommandImpl;
import dev.gnomebot.app.util.Utils;
import discord4j.rest.util.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class HelpCommand extends ApplicationCommands {
	@RootCommand
	public static final CommandBuilder COMMAND = root("help")
			.description("Sends you a list of available commands or info about a specific command")
			.add(string("command"))
			.run(HelpCommand::run);

	private static void run(ApplicationCommandEventWrapper event) throws DiscordCommandException {
		String cmd = event.get("command").asString();

		if (cmd.equals("permissions")) {
			event.acknowledgeEphemeral();
			StringBuilder sb = new StringBuilder("Permissions in " + event.context.channelInfo.getMention() + ":\n");
			List<String> list = new ArrayList<>();

			for (Permission permission : event.context.channelInfo.getSelfPermissions()) {
				list.add(permission.name());
			}

			list.sort(String.CASE_INSENSITIVE_ORDER);

			for (String s : list) {
				sb.append('\n');
				sb.append(s);
			}

			event.respond(sb.toString());
			return;
		}

		event.acknowledgeEphemeral();
		StringBuilder sb = new StringBuilder("Gnome commands: (prefix: `");
		sb.append(event.context.gc.prefix);
		sb.append("`)\n");
		sb.append(DiscordCommandImpl.COMMAND_LIST.stream().filter(c -> c.callback.hasPermission(c, event.context)).map(DiscordCommandImpl::getInfo).sorted().collect(Collectors.joining("\n")));

		long macros = event.context.gc.macros.count();

		if (macros > 0L || event.context.gc.customCommands.count() > 0L) {
			sb.append("\n\nCustom commands: (prefix `");
			sb.append(event.context.gc.customCommandPrefix);
			sb.append("`)\n");
			sb.append(Utils.toStream(event.context.gc.customCommands.query()).map(DiscordCustomCommand::getCommandName).sorted().collect(Collectors.joining("\n")));

			if (macros > 0L) {
				sb.append("\n+ ").append(macros).append(" macros (`").append(event.context.gc.prefix).append("macro list`)");
			}
		}

		sb.append("\n\nTo configure or add this bot to your server, visit [gnomebot.dev](https://gnomebot.dev/)");
		event.respond(sb.toString());
	}
}
