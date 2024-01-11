package dev.gnomebot.app.data.ping;

import dev.gnomebot.app.App;
import discord4j.common.util.Snowflake;

public class RelayPingTask implements Runnable {
	private final PingDestination destination;
	private final Snowflake targetId;
	private final PingData pingData;
	private final Ping ping;

	public RelayPingTask(PingDestination destination, Snowflake targetId, PingData pingData, Ping ping) {
		this.destination = destination;
		this.targetId = targetId;
		this.pingData = pingData;
		this.ping = ping;
	}

	@Override
	public void run() {
		try {
			var start = System.nanoTime();

			if (targetId.asLong() == 0L || pingData.channel().canViewChannel(targetId)) {
				destination.relayPing(targetId, pingData, ping);
			}

			var time = System.nanoTime() - start;

			if (time >= 1_000_000_000L) { // 1000.000 ms
				App.warn("Reply: " + ((time / 1000L) / 1000F) + " ms " + destination);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
