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
package com.partysmarty.ui;

import java.awt.image.BufferedImage;

public final class PartyHatIcon
{
	private PartyHatIcon()
	{
	}

	public static BufferedImage create()
	{
		// Symmetric party-hat crown: tall center point, matching side points, straight base.
		// @ is the white face, + is the shaded face. The outline is drawn around both.
		final String[] rows = {
			"................",
			".......@@.......",
			"......@@@@......",
			"..@..@@@@@@..@..",
			".@@@.@@@@@@.@@@.",
			".@@@@@@@@@@@@@@.",
			".@@@@@@@@@@@@@@.",
			".+@@@@@@@@@@@@@.",
			".++@@@@@@@@@@@@.",
			".+++@@@@@@@@@@@.",
			".++++@@@@@@@@@@.",
			".+++++@@@@@@@@@.",
			".++++++@@@@@@@@.",
			".@@@@@@@@@@@@@@.",
			"................",
			"................"
		};

		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		int white = 0xFFF6F6F6;
		int shade = 0xFFB0B0B0;
		int outline = 0xFF161616;
		boolean[][] filled = new boolean[16][16];
		for (int y = 0; y < rows.length; y++)
		{
			String row = rows[y];
			for (int x = 0; x < row.length(); x++)
			{
				char pixel = row.charAt(x);
				if (pixel == '.')
				{
					continue;
				}

				filled[y][x] = true;
				image.setRGB(x, y, pixel == '+' ? shade : white);
			}
		}

		for (int y = 0; y < 16; y++)
		{
			for (int x = 0; x < 16; x++)
			{
				if (!filled[y][x] && touchesFill(filled, x, y))
				{
					image.setRGB(x, y, outline);
				}
			}
		}
		return image;
	}

	private static boolean touchesFill(boolean[][] filled, int x, int y)
	{
		for (int dy = -1; dy <= 1; dy++)
		{
			for (int dx = -1; dx <= 1; dx++)
			{
				if (dx == 0 && dy == 0)
				{
					continue;
				}

				int nx = x + dx;
				int ny = y + dy;
				if (nx >= 0 && ny >= 0 && nx < 16 && ny < 16 && filled[ny][nx])
				{
					return true;
				}
			}
		}
		return false;
	}
}
