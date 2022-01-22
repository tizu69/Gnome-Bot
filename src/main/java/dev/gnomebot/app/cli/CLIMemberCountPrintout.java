package dev.gnomebot.app.cli;

import dev.gnomebot.app.discord.CachedRole;
import dev.gnomebot.app.discord.command.RootCommand;
import dev.gnomebot.app.util.Table;
import discord4j.core.object.entity.Member;

import java.time.Instant;

public class CLIMemberCountPrintout {
	@RootCommand
	public static final CLICommand COMMAND = CLICommand.make("membercount_printout")
			.description("Membercount Printout")
			.run(CLIMemberCountPrintout::run);

	private static void run(CLIEvent event) throws Exception {
		CachedRole role = event.reader.readRole().orElse(null);

		Table table = new Table("ID", "Tag");

		for (Member member : event.gc.getMembers()) {
			if (role == null || member.getRoleIds().contains(role.id)) {
				table.addRow(member.getId().asString(), member.getTag());
			}
		}

		event.respondFile("Done!", "role-export-" + event.gc.guildId.asString() + "-" + (role == null ? "all" : role.id.asString()) + "-" + Instant.now().toString().replace(':', '-') + ".csv", table.getCSVBytes(false));
	}
}
