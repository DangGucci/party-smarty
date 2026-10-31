/*
 * Copyright (c) 2026, DangGucci
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.partysmarty;

import com.google.inject.Provides;
import com.partysmarty.ui.PartySmartyPanel;
import com.partysmarty.ui.PartyHatIcon;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PartyChanged;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(
	name = "Party Smarty",
	configName = "partysmarty",
	description = "Rejoin a saved RuneLite party on login",
	tags = {"party"}
)
public class PartySmartyPlugin extends Plugin
{
	// Hub Party Panel is priority 7. The next value sits directly under that icon.
	private static final int SIDEBAR_PRIORITY = 8;

	@Inject
	private PartySmartyConfig config;

	@Inject
	private PartyService party;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private PartySmartyPanel panel;

	private NavigationButton navButton;

	@Override
	protected void startUp() throws Exception
	{
		final BufferedImage icon = PartyHatIcon.create();
		navButton = NavigationButton.builder()
			.tooltip("Party Smarty")
			.icon(icon)
			.priority(SIDEBAR_PRIORITY)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navButton);
		SwingUtilities.invokeLater(panel::load);
		rejoinIfNeeded();
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientToolbar.removeNavigation(navButton);
		navButton = null;
	}

	@Provides
	PartySmartyConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartySmartyConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			rejoinIfNeeded();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!PartySmartyConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		if (PartySmartyConfig.KEY_AUTO_REJOIN.equals(event.getKey())
			|| PartySmartyConfig.KEY_PARTY_PASSPHRASE.equals(event.getKey()))
		{
			SwingUtilities.invokeLater(panel::load);
			rejoinIfNeeded();
		}
	}

	@Subscribe
	public void onPartyChanged(PartyChanged event)
	{
		String passphrase = event.getPassphrase();
		if (passphrase == null || passphrase.isEmpty())
		{
			return;
		}

		if (passphrase.equals(config.partyPassphrase()))
		{
			return;
		}

		config.setPartyPassphrase(passphrase);
	}

	private void rejoinIfNeeded()
	{
		if (!config.autoRejoin())
		{
			return;
		}

		String saved = config.partyPassphrase();
		if (saved == null)
		{
			return;
		}

		saved = saved.trim();
		if (saved.isEmpty())
		{
			return;
		}

		// changeParty closes the websocket even when the passphrase is unchanged.
		if (saved.equals(party.getPartyPassphrase()))
		{
			return;
		}

		log.debug("Joining saved party");
		party.changeParty(saved);
	}
}
