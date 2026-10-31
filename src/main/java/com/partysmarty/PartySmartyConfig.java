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

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(PartySmartyConfig.GROUP)
public interface PartySmartyConfig extends Config
{
	String GROUP = "partysmarty";
	String KEY_AUTO_REJOIN = "autoRejoin";
	String KEY_PARTY_PASSPHRASE = "partyPassphrase";

	@ConfigItem(
		keyName = KEY_AUTO_REJOIN,
		name = "Auto rejoin",
		description = "On each login, join the saved party without clicking Join party.",
		position = 0
	)
	default boolean autoRejoin()
	{
		return false;
	}

	@ConfigItem(
		keyName = KEY_PARTY_PASSPHRASE,
		name = "Party passphrase",
		description = "Passphrase from the Party panel. Updated when you join a party, and used when Auto rejoin is on.",
		position = 1
	)
	default String partyPassphrase()
	{
		return "";
	}

	@ConfigItem(
		keyName = KEY_PARTY_PASSPHRASE,
		name = "",
		description = "",
		hidden = true
	)
	void setPartyPassphrase(String passphrase);
}
