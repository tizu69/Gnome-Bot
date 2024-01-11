package dev.gnomebot.app.discord.legacycommand;

import dev.gnomebot.app.App;
import dev.gnomebot.app.server.AuthLevel;
import dev.latvian.apps.webutils.ansi.Ansi;
import discord4j.common.util.Snowflake;

public class RemoveRoleMembersCommand {
	@LegacyDiscordCommand(name = "remove_role_members", help = "Removes members from role", arguments = "<role>", permissionLevel = AuthLevel.ADMIN)
	public static final CommandCallback COMMAND = (context, reader) -> {
		var confirm = reader.readString().orElse("").equalsIgnoreCase("confirm");

		final var role = Snowflake.of(reader.readLong().orElse(0L));

		context.handler.app.queueBlockingTask(task -> {
			var removed = 0;

			for (var member : context.gc.getGuild().getMembers().toIterable()) {
				if (task.cancelled) {
					return;
				}

				var m = context.gc.members.findFirst(member);

				if (m != null) {
					var n = Ansi.of(member.getTag());

					if (member.getRoleIds().size() > 1) {
						n.cyan();
					}

					if (member.getRoleIds().contains(role)) {
						removed++;
						App.info(Ansi.of("- ").append(n));

						if (confirm) {
							member.removeRole(role).block();
						}
					}
				}
			}

			App.info("Removed " + removed);

			if (confirm) {
				context.reply("<@&" + role.asString() + ">: removed " + removed);
			} else {
				context.reply("<@&" + role.asString() + ">: removed " + removed + "\n\nType `" + context.gc.legacyPrefix + "remove_role_members <role> confirm` to change roles");
			}
		});
	};
}
