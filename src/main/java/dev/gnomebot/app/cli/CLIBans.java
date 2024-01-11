package dev.gnomebot.app.cli;

import dev.gnomebot.app.data.GnomeAuditLogEntry;
import dev.latvian.apps.webutils.ansi.Table;
import discord4j.common.util.Snowflake;

public class CLIBans {
	public static final CLICommand COMMAND = CLICommand.make("bans")
			.description("Export all bans")
			.run(CLIBans::run);

	private static String tableString(String s) {
		return s.isEmpty() ? "-" : s;
	}

	private static void run(CLIEvent event) {
		var table = new Table("User ID", "Tag", "Reason", "Banned By", "Banned At");
		var userCache = event.gc.db.app.discordHandler.createUserCache();

		for (var ban : event.gc.getGuild().getBans().toIterable()) {
			var userId = Snowflake.of(ban.getData().user().id().asLong());

			var found = false;

			for (var entry : event.gc.auditLog.query().eq("type", GnomeAuditLogEntry.Type.BAN.name).eq("user", userId.asLong())) {
				found = true;
				table.addRow(userId.asString(), userCache.getTag(userId), tableString(entry.getContent()), userCache.getTag(Snowflake.of(entry.getSource())), entry.getDate().toInstant().toString());
			}

			if (!found) {
				table.addRow(userId.asString(), userCache.getTag(userId), ban.getReason().orElse("-"), "-", "-");
			}
		}

		event.respond("Ban list");
		event.response.addFile("banlist-" + event.gc.guildId.asString() + ".csv", table.getCSVBytes(false));
	}
}
